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
	elseif (node.tag=="if_statement") then
		return ipairs({[1] = node.expression, [2] = node.block})
	else
		return function() return nil end
	end
end

function create_label(program)
	local label = {}
	program.labels:insert(label)
	label.id = #program.labels
	label.address=-1
	return label
end

function create_label_ref(label)
	local ref = {}
	ref.tag="label_ref"
	ref.id=label.id
	return ref
end

function generate_bytecode(node, program)
	-- Generate bytecode for this node, calling children recursively when appropriate
	if (node.tag=="literal_int") then
		node.constant_index = program.constants:get_id(tonumber(node.value))

		program.bytecode:insert(kOpPush)
		program.bytecode:insert(node.constant_index-1) -- (index is 1 offset)
	elseif (node.tag=="variable") then
		node.variable_index = program.variables:get_id(node.identifier.value)

		program.bytecode:insert(kOpPushVar)
		program.bytecode:insert(node.variable_index-1) -- (index is 1 offset)
	elseif (node.tag=="binary_op") then
		generate_bytecode(node.operand1, program)
		generate_bytecode(node.operand2, program)
		program.bytecode:insert(binary_opcodes[node.operator.type])
	elseif (node.tag=="unary_op") then
		generate_bytecode(node.operand, program)
		program.bytecode:insert(unary_opcodes[node.operator.type])
	elseif (node.tag=="function_call") then
		for i,arg in ipairs(node.arguments) do
			generate_bytecode(arg, program)
		end

		program.bytecode:insert(kOpCallFunc)

		local func = functions[node.identifier.value]
		program.bytecode:insert(func.id)
	elseif (node.tag=="statement_list") then
		for i,statement in ipairs(node) do
			generate_bytecode(statement, program)
		end
	elseif (node.tag=="function_call_statement") then
		generate_bytecode(node.function_call, program)

		local func = functions[node.function_call.identifier.value]

		if (func.result=="number") then
			program.bytecode:insert(kOpPop)
		end
	elseif (node.tag=="assign_statement") then
		generate_bytecode(node.expression, program)

		node.variable_index = program.variables:get_id(node.identifier.value)

		program.bytecode:insert(kOpPopVar)
		program.bytecode:insert(node.variable_index-1) -- (index is 1 offset)
	elseif (node.tag=="if_statement") then
		node.label_post = create_label(program)

		generate_bytecode(node.expression, program)

		program.bytecode:insert(kOpJumpEqual)
		program.bytecode:insert(create_label_ref(node.label_post))

		generate_bytecode(node.block, program)

		node.label_post.address = #program.bytecode
	elseif (node.tag=="if_else_statement") then
		node.label_else = create_label(program)
		node.label_post = create_label(program)

		generate_bytecode(node.expression, program)

		program.bytecode:insert(kOpJumpEqual)
		program.bytecode:insert(create_label_ref(node.label_else))

		generate_bytecode(node.if_block, program)

		program.bytecode:insert(kOpJump)
		program.bytecode:insert(create_label_ref(node.label_post))

		node.label_else.address = #program.bytecode

		generate_bytecode(node.else_block, program)

		node.label_post.address = #program.bytecode
	elseif (node.tag=="while_statement") then
		node.label_test = create_label(program)
		node.label_finish = create_label(program)

		node.label_test.address = #program.bytecode
		generate_bytecode(node.expression, program)

		program.bytecode:insert(kOpJumpEqual)
		program.bytecode:insert(create_label_ref(node.label_finish))

		generate_bytecode(node.block, program)
		program.bytecode:insert(kOpJump)
		program.bytecode:insert(create_label_ref(node.label_test))

		node.label_finish.address = #program.bytecode
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
		local func = functions[node.identifier.value]

		if (func.result~="number") then
			error("Function '"..node.identifier.value.."' does not return a number.")
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
		local f = functions[node.identifier.value]
		assert(f, "Compilation error: function '"..node.identifier.value.."' is unknown.")
		assert(#f.arguments==#node.arguments, "Compilation error: function '"..node.identifier.value.."' expects "..#f.arguments.." arguments, got "..#node.arguments..".")

		for i,arg in ipairs(node.arguments) do
			check_expression(arg)
		end
	elseif (node.tag=="assign_statement") then
		check_expression(node.expression)
	elseif (node.tag=="if_statement") then
		check_expression(node.expression)
		check_semantics(node.block)
	elseif (node.tag=="if_else_statement") then
		check_expression(node.expression)
		check_semantics(node.if_block)
		check_semantics(node.else_block)
	elseif (node.tag=="while_statement") then
		check_expression(node.expression)
		check_semantics(node.block)
	else
		for i,child in children(node) do
			check_semantics(child)
		end
	end
end

function update_labels(program)
	-- create sorted label list
	local sorted_labels = {}
	for i,v in ipairs(program.labels) do
		sorted_labels[i] = v
	end
	table.sort(sorted_labels, function(a,b) return a.address < b.address end)

	-- create distinct address set
	local distinct_labels = create_unique_set()
	for i,label in ipairs(sorted_labels) do
		distinct_labels:get_id(label.address)
	end

	-- update label references in bytecode
	for i,opcode in ipairs(program.bytecode) do
		if (type(opcode)=="table") then
			if (opcode.tag=="label_ref") then
				program.bytecode[i] = distinct_labels:get_id(program.labels[opcode.id].address)-1 -- (index is 1 offset)
			end
		end
	end

	-- generate new label set from sorted, distinct set
	program.labels = distinct_labels:to_table()
end

-- Compile program
function compile(str)
	local program = {}
	program.ast = transform_associativity(parse(str))
--	print(serialize_table(program.ast))

	check_semantics(program.ast.root)

	program.constants = create_unique_set()
	program.variables = create_unique_set()
	program.labels    = create_table()
	program.bytecode  = create_table()

	generate_bytecode(program.ast.root, program)
	program.bytecode:insert(kOpReturn)

	update_labels(program)

	program.constants = program.constants:to_table()

	return program
end
