require("compile")

--------------------------------------------------------
-- before running this, compile the vm like this:     --
--                                                    --
--  cc -DTEST_MODE -Wall vm.c -otestvm                --
--------------------------------------------------------

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
		local datastr = generate_data(program):gsub('.', function (c)
			return string.format('%02X', string.byte(c))
		end)
		local proc = io.popen("testvm " .. datastr)
		local result = proc:read("*a")
		proc:close()
		result = tonumber(string.sub(result, string.find(result, "-?[0-9.]+")))
		if result~=t[2] then
			print("Unexpected result: ", t[1], " got ", result, " expected ", t[2])
			eval_success = false
		end

	else
		print("Couldn't compile ", t[1])
	end
end

if (eval_success==true) then
	print("Integration tests ok.")
end
