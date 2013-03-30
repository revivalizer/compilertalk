require("compile")
require("util/eval")

-- Compile tests
local ok_tests ={
		"1+2",
		" 1+2",
		"1+ 2",
		"1  +  2",
		"1 + 2 ",
		"1+2+3+4+5",
		"1+-2",
		"-1",
		"12345",
		"0+0",
		"000001+00219",
		"1-2",
		"!1*200/3+4-5*-(1*2)<1<=2>3>=4&&1||2",
		"+2",
}

local fail_tests = {
		"a",
		"1+2+"
}

-- .. check positive tests
for i,str in pairs(ok_tests) do
	assert(type(parse(str))=="table", "'"..str.."' expected to compile")
end

-- .. check negative tests
local old_error = error
local old_print = print -- hide errors
error = function() end
print = function() end
for i,str in pairs(fail_tests) do
	local res = parse(str)
	assert(type(res)=="nil", "'"..str.."' expected to fail compilation")
end
error = old_error
print = old_print

print("Compile tests ok.")

-- Eval tests
local eval_success = true

local tests = {
	{"100", 100},
	{"1+2", 3},
	{"3*9", 27},
	{"9/3", 3},
	{"9/2", 4.5},
	{"!1", 0},
	{"!0", 1},
	{"-1", -1},
	{"+1", 1},
	{"!!!1", 0},
	{"--1", 1},
	{"---1", -1},
	{"2*3+4*5", 26},
	{"2*(3+4)*5", 70},
	{"1<1", 0},
	{"1>1", 0},
	{"1<=1", 1},
	{"1>=1", 1},
	{"1==1", 1},
	{"1!=1", 0},
	{"2<3", 1},
	{"-2<1", 1},
	{"3>2", 1},
	{"-(2<1)", -0},
	{"!(1<1)&&1", 1},
--	{"3-2-1", 0},   -- wrong associativity, won't work until next lesson, same for line below
--	{"27 / 3 / 3", 3},
}

for i,t in pairs(tests) do
	local program = compile(t[1])

	if (program~=nil) then
		local result = eval(program.ast.root)
		if result~=t[2] then
			print("Unexpected result: ", t[1], " got ", result, " expected ", t[2])
			eval_success = false
		end

	else
		print("Couldn't compile ", t[1])
	end
end

if (eval_success==true) then
	print("Eval tests ok.")
end
