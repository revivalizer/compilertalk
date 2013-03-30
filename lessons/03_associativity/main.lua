require("compile")
require("util/eval")

local program = compile("3-2-1")
print(serialize_table(program))
export_binary_and_header("test", program)

print("Result of evaluation: " .. eval(program.ast.root))
