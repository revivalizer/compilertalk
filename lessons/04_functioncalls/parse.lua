require("re") -- this is the "re" module of lpeg

-- full grammar with captures
p = re.compile([[
	start         <- ws? ({:root: statement_list:} {:match_length: {}:}) -> {}

	statement_list <- ({:tag: '' -> 'statement_list':} statement*) -> {}
	statement      <- function_call_statement

	function_call_statement <- ({:tag: '' -> 'function_call_statement':} {:function_call: function_call:} semicolon) -> {}

	function_call <- ({:tag: '' -> 'function_call':} {:identifier: identifier:} open_parens {:arguments: function_call_arguments?:} close_parens ) -> {}
	function_call_arguments <- (expression (',' ws? expression)*) -> {}

	expression  <- logical_or
	logical_or  <- ({:tag: '' -> 'binary_op':} {:operand1: logical_and:} {:operator: logical_or_operator:}  {:operand2: logical_or:})  -> {}	/ logical_and
	logical_and <- ({:tag: '' -> 'binary_op':} {:operand1: equality:}    {:operator: logical_and_operator:} {:operand2: logical_and:}) -> {}	/ equality
	equality    <- ({:tag: '' -> 'binary_op':} {:operand1: relational:}  {:operator: equality_operators:}   {:operand2: equality:})    -> {}	/ relational
	relational  <- ({:tag: '' -> 'binary_op':} {:operand1: additive:}    {:operator: relational_operators:} {:operand2: relational:})  -> {}	/ additive
	additive    <- ({:tag: '' -> 'binary_op':} {:operand1: multitive:}   {:operator: additive_operators:}   {:operand2: additive:})    -> {}	/ multitive
	multitive   <- ({:tag: '' -> 'binary_op':} {:operand1: unary:}       {:operator: multitive_operators:}  {:operand2: multitive:})   -> {}	/ unary
	unary       <- ({:tag: '' -> 'unary_op':}                            {:operator: unary_operators:}      {:operand: unary:})        -> {}	/ primary
	primary     <- integer / function_call / open_parens expression close_parens

	additive_operators   <- addition_operator / subtraction_operator
	multitive_operators  <- multiplication_operator / division_operator / modulo_operator
	equality_operators   <- equality_operator / inequality_operator
	relational_operators <- less_than_or_equal_operator / greater_than_or_equal_operator / less_than_operator / greater_than_operator
	unary_operators      <- not_operator / plus_operator / minus_operator

	not_operator                   <- ({:precedence: '' -> '3':}  {:assoc: '' -> 'right':} {:type: '!':}  ws?) -> {}
	plus_operator                  <- ({:precedence: '' -> '3':}  {:assoc: '' -> 'right':} {:type: '+':}  ws?) -> {}
	minus_operator                 <- ({:precedence: '' -> '3':}  {:assoc: '' -> 'right':} {:type: '-':}  ws?) -> {}
	multiplication_operator        <- ({:precedence: '' -> '5':}  {:assoc: '' -> 'left':}  {:type: '*':}  ws?) -> {}
	division_operator              <- ({:precedence: '' -> '5':}  {:assoc: '' -> 'left':}  {:type: '/':}  ws?) -> {}
	modulo_operator                <- ({:precedence: '' -> '5':}  {:assoc: '' -> 'left':}  {:type: '%':}  ws?) -> {}
	addition_operator              <- ({:precedence: '' -> '6':}  {:assoc: '' -> 'left':}  {:type: '+':}  ws?) -> {}
	subtraction_operator           <- ({:precedence: '' -> '6':}  {:assoc: '' -> 'left':}  {:type: '-':}  ws?) -> {}
	less_than_or_equal_operator    <- ({:precedence: '' -> '8':}  {:assoc: '' -> 'left':}  {:type: '<=':} ws?) -> {}
	greater_than_or_equal_operator <- ({:precedence: '' -> '8':}  {:assoc: '' -> 'left':}  {:type: '>=':} ws?) -> {}
	less_than_operator             <- ({:precedence: '' -> '8':}  {:assoc: '' -> 'left':}  {:type: '<':}  ws?) -> {}
	greater_than_operator          <- ({:precedence: '' -> '8':}  {:assoc: '' -> 'left':}  {:type: '>':}  ws?) -> {}
	equality_operator              <- ({:precedence: '' -> '9':}  {:assoc: '' -> 'left':}  {:type: '==':} ws?) -> {}
	inequality_operator            <- ({:precedence: '' -> '9':}  {:assoc: '' -> 'left':}  {:type: '!=':} ws?) -> {}
	logical_and_operator           <- ({:precedence: '' -> '13':} {:assoc: '' -> 'left':}  {:type: '&&':} ws?) -> {}
	logical_or_operator            <- ({:precedence: '' -> '14':} {:assoc: '' -> 'left':}  {:type: '||':} ws?) -> {}

	integer       <- ({:tag: '' -> 'literal_int':} {:value: '-'? [0-9]+:} ws?) -> {}
	identifier    <- [a-zA-Z] [a-zA-Z0-9_]* ws?

	open_parens   <- '(' ws?
	close_parens  <- ')' ws?

	open_brace    <- '{' ws?
	close_brace   <- '}' ws?

	semicolon     <- ';' ws?

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

