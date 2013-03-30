require("re") -- this is the "re" module of lpeg

-- simplified grammar, no captures
--[[
	root          <- ws? plus_expr
	plus_expr     <- integer plus_operator plus_expr
	                 / integer
	plus_operator <- '+' ws?
 	integer       <- '-'? [0-9]+ ws?
	ws            <- %s+
]]

-- full grammar with captures
p = re.compile([[
	start         <- ws? ({:root: plus_expr:} {:match_length: {}:}) -> {}
	plus_expr     <- ({:tag: ''->'binary_op':} {:operand1: integer:} {:operator: plus_operator:} {:operand2: plus_expr:})  -> {}
	                 / integer
	plus_operator <- ({:type: '+':} ws?) -> {}
 	integer       <- ({:tag: '' -> 'literal_int':} {:value: '-'? [0-9]+:} ws?) -> {}
	ws            <- %s+
]])

function parse(str)
	local ast = p:match(str)

	-- Check if match could be found
	if (ast) then
		-- Check if we mathed entire input string
		if (ast.match_length==(str:len()+1)) then
			return ast
		else
			error("Parse error! Didn't match all input.")
		end
	else
		print("Parse error!")
	end
end

