require("compile")

local source = [[
	print(cos(0));
	print(cos(31415/10000));
	print(cos(31415/10000/2));
	print(sin(0));
	print(sin(31415/10000));
	print(sin(31415/10000/2));
]]

local program = compile(source)
print(serialize_table(program))
export_binary_and_header("test", program)

print(serialize_table(functions))
