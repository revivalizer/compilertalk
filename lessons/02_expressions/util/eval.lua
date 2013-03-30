-- Simple recursive evaluation function, just for kicks
function eval(node)
	if (node.tag=="binary_op") then
		if (node.operator.type=="+") then
			return eval(node.operand1) + eval(node.operand2)
		elseif (node.operator.type=="-") then
			return eval(node.operand1) - eval(node.operand2)
		elseif (node.operator.type=="*") then
			return eval(node.operand1) * eval(node.operand2)
		elseif (node.operator.type=="/") then
			return eval(node.operand1) / eval(node.operand2)
		elseif (node.operator.type=="%") then
			return eval(node.operand1) % eval(node.operand2)
		elseif (node.operator.type=="==") then
			if eval(node.operand1)==eval(node.operand2) then
				return 1
			else
				return 0
			end
		elseif (node.operator.type=="!=") then
			if eval(node.operand1) ~= eval(node.operand2) then
				return 1
			else
				return 0
			end
		elseif (node.operator.type=="<") then
			if eval(node.operand1) < eval(node.operand2) then
				return 1
			else
				return 0
			end
		elseif (node.operator.type=="<=") then
			if eval(node.operand1) <= eval(node.operand2) then
				return 1
			else
				return 0
			end
		elseif (node.operator.type==">") then
			if eval(node.operand1) > eval(node.operand2) then
				return 1
			else
				return 0
			end
		elseif (node.operator.type==">=") then
			if eval(node.operand1) >= eval(node.operand2) then
				return 1
			else
				return 0
			end
		elseif (node.operator.type=="&&") then
			if eval(node.operand1)~=0 and eval(node.operand2)~=0 then
				return 1
			else
				return 0
			end
		elseif (node.operator.type=="||") then
			if eval(node.operand1)~=0 or eval(node.operand2)~=0 then
				return 1
			else
				return 0
			end
		end
	elseif (node.tag=="unary_op") then
		if (node.operator.type=="+") then
			return eval(node.operand)
		elseif (node.operator.type=="-") then
			return -eval(node.operand)
		elseif (node.operator.type=="!") then
			if eval(node.operand) ~= 0 then
				return 0
			else
				return 1
			end
		end
	elseif (node.tag=="literal_int") then
		return tonumber(node.value)
	end
end
