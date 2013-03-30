require("parse")
require("opcodes")
require("functions")
require("util/export")
require("util/serialize")
require("util/unique_set")
require("util/util")

function children(node)
	-- This functions returns an iterator which iterates over the child nodes of the nodes (iterators are idiomatic lua)
	-- This is a very nice way to abstract the navigation of the tree, which varies for different node types

	if (node.tag=="binary_op") then
		return ipairs({[1] = node.operand1, [2] = node.operand2})
	elseif (node.tag=="unary_op") then
		return ipairs({[1] = node.operand})
	elseif (node.tag=="statement_list") then
		return ipairs(node)
	elseif (node.tag=="function_call") then
		return ipairs(node.arguments)
	elseif (node.tag=="function_call_statement") then
		return ipairs({[1] = node.function_call})
	elseif (node.tag=="assign_statement") then
		return ipairs({[1] = node.expression})
	else
		return function() return nil end
	end
end

function generate_bytecode(node, program)
	-- Generate bytecode for child nodes first
	for i,child in children(node) do
		generate_bytecode(child, program)
	end

	-- Generate bytecode for this node
	if (node.tag=="literal_int") then
		node.constant_index = program.constants:get_id(tonumber(node.value))

		program.bytecode:insert(kOpPush)
		program.bytecode:insert(node.constant_index-1) -- (index is 1 offset)
	elseif (node.tag=="variable") then
		node.variable_index = program.variables:get_id(node.identifier)

		program.bytecode:insert(kOpPushVar)
		program.bytecode:insert(node.variable_index-1) -- (index is 1 offset)
	elseif (node.tag=="binary_op") then
		program.bytecode:insert(binary_opcodes[node.operator.type])
	elseif (node.tag=="unary_op") then
		program.bytecode:insert(unary_opcodes[node.operator.type])
	elseif (node.tag=="function_call") then
		local func = functions[node.identifier]

		program.bytecode:insert(kOpCallFunc)
		program.bytecode:insert(func.id)
	elseif (node.tag=="function_call_statement") then
		local func = functions[node.function_call.identifier]

		if (func.result=="number") then
			program.bytecode:insert(kOpPop)
		end
	elseif (node.tag=="assign_statement") then
		node.variable_index = program.variables:get_id(node.identifier)

		program.bytecode:insert(kOpPopVar)
		program.bytecode:insert(node.variable_index-1) -- (index is 1 offset)
	end
end

-- This functions transforms right associative binary operators to left associative ones, when neccesary
function transform_associativity(node)
	if (type(node)=="table") then
		if (
				node.tag                     == "binary_op" and
				node.operand2.tag            == "binary_op" and
				node.operator.assoc          == "left"      and
				node.operand2.operator.assoc == "left"      and
				node.operator.precedence     ==  node.operand2.operator.precedence
			)
			then
			-- node and node.operand2 must have their associativity changed.

			-- rotate nodes
			local newroot = node.operand2
			node.operand2 = newroot.operand1
			newroot.operand1 = node

			return transform_associativity(newroot)
		else
			-- standard case
			for i,v in pairs(node) do
				node[i] = transform_associativity(v)
			end

			return node
		end
	else
		return node
	end
end

function check_expression(node)
	if (node.tag=="function_call") then
		local func = functions[node.identifier]

		if (func.result~="number") then
			error("Function '"..node.identifier.."' does not return a number.")
		end
	elseif (node.tag=="literal_int" or node.tag=="unary_op" or node.tag=="binary_op" or node.tag=="variable") then
		-- this is ok, do nothing
	else
		error("Node not an expression.")
	end

	for i,child in children(node) do
		check_expression(child)
	end
end

function check_semantics(node)
	if (node.tag=="function_call") then
		local f = functions[node.identifier]
		assert(f, "Compilation error: function '"..node.identifier.."' is unknown.")
		assert(#f.arguments==#node.arguments, "Compilation error: function '"..node.identifier.."' expects "..#f.arguments.." arguments, got "..#node.arguments..".")

		for i,arg in ipairs(node.arguments) do
			check_expression(arg)
		end
	elseif (node.tag=="assign_statement") then
		check_expression(node.expression)
	else
		for i,child in children(node) do
			check_semantics(child)
		end
	end
end

-- Compile program
function compile(str)
	local program = {}
	program.ast = transform_associativity(parse(str))

	check_semantics(program.ast.root)

	program.constants = create_unique_set()
	program.variables = create_unique_set()
	program.bytecode  = create_table()

	generate_bytecode(program.ast.root, program)
	program.bytecode:insert(kOpReturn)

	program.constants = program.constants:to_table()

	return program
end
