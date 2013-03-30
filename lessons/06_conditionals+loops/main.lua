require("compile")

local source = [[
	a = 3;

	if (a==1)
	{
		print(4);
	}
	else if (a==2)
	{
		print(5);
	}
	else
	{
		print(6);
	}

	while (a>=0)
	{
		print(a);
		a = a - 1;
	}
]]

local program = compile(source)
print(serialize_table(program))
export_binary_and_header("test", program)
