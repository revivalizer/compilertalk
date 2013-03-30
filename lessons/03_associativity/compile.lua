require("parse")
require("opcodes")
require("util/export")
require("util/serialize")
require("util/unique_set")
require("util/util")

function children(node)
	-- This functions returns an iterator which iterates over the child nodes of the nodes (iterators are idiomatic lua)
	-- This is a very nice way to abstract the navigation of the tree, which varies for different node types

	if (node.tag=="binary_op") then
		return ipairs({[1] = node.operand1, [2] = node.operand2})
	else
		return function() return nil end
	end
end

function generate_constants(node, program, constants)
	if (node.tag=="literal_int") then
		node.constant_index = constants:get_id(tonumber(node.value))
	end

	for i,child in children(node) do
		generate_constants(child, program, constants)
	end

	return constants
end

function generate_bytecode(node, program, bytecode)
	-- Generate bytecode for child nodes first
	for i,child in children(node) do
		generate_bytecode(child, program, bytecode)
	end

	-- Generate bytecode for this node
	if (node.tag=="literal_int") then
		bytecode:insert(kOpPush)
		bytecode:insert(node.constant_index-1) -- (index is 1 offset)
	elseif (node.tag=="binary_op") then
		bytecode:insert(binary_opcodes[node.operator.type])
	elseif (node.tag=="unary_op") then
		bytecode:insert(unary_opcodes[node.operator.type])
	end

	return bytecode
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

-- Compile program
function compile(str)
	local program = {}
	program.ast = transform_associativity(parse(str))

	program.constants = generate_constants(program.ast.root, program, create_unique_set()):to_table()
	program.bytecode  = generate_bytecode(program.ast.root, program, create_table())
	program.bytecode:insert(kOpReturn)

	return program
end
