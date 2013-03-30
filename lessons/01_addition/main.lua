require("compile")

local program = compile("1+2+3")
print(serialize_table(program))

-- Simple recursive evaluation function, just for kicks
function eval(node)
	if (node.tag=="binary_op") then
		if (node.operator.type=="+") then
			return eval(node.operand1) + eval(node.operand2)
		end
	elseif (node.tag=="literal_int") then
		return tonumber(node.value)
	end
end

print("Result of evaluation: " .. eval(program.ast.root))

export_binary_and_header("test", program)

