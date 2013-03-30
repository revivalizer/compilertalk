require("compile")

local source = [[
	b=100;
	a=b/3;
	print(a);
	print(b);
]]

local program = compile(source)
print(serialize_table(program))
export_binary_and_header("test", program)
