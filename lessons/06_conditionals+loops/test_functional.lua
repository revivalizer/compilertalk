require("compile")

-- Compile tests
local ok_tests ={
		"print(1+2);",
		" print( 1+2 );",
		"print(1+ 2);",
		"print(1  +  2);",
		"print(1 + 2); ",
		"print(1+2+3+4+5);",
		"print(1+-2);",
		"print(-1);",
		"print(12345);",
		"print(0+0);",
		"print(000001+00219);",
		"print(1-2);",
		"print(!1*200/3+4-5*-(1*2)<1<=2>3>=4&&1||2);",
		"print(+2);print(1);",
		"print(cos(+2));",
		"print(sin(+2));",
		"cos(1);",
		"sin(1);",
		"a=cos(1);",
		"a=sin(1);b=a;",
		"if(a) print(1);",
		"if (a) print(1); else if (b) print(2); else print(2);",
		"while (a) { a=a-1; }",
}

local fail_tests = {
		"a",
		"1+2+",
		"print(1+);",
		" print( 1+2 )",
		"prin(1+ 2);",
		"print(1  +  2,);",
		"print(print(1));",
		"b=print(a);",
		"a=b=1;",
		"while (print(1))",
}

local test_ok = true

-- .. check positive tests
for i,str in pairs(ok_tests) do
	if (not pcall(function() compile(str) end)) then
		print("'"..str.."' expected to compile")
		test_ok = false
	end
end

-- .. check negative tests
for i,str in pairs(fail_tests) do
	if pcall(function() compile(str) end) then
		print("'"..str.."' expected to fail compilation")
		test_ok = false
	end
end

if (test_ok) then
	print("Compile tests ok.")
end

-- Eval tests - not doing those here because our program is now composed of a set of statements, not just a single expression.
