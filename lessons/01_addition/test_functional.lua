require("compile")

-- tests
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
		"000001+00219"
}

local fail_tests = {
		"1-2",
		"+2",
		"a",
		"1+2+"
}

for i,str in pairs(ok_tests) do
	assert(type(parse(str))=="table", "'"..str.."' expected to compile")
end

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

print("Tests ok.")
