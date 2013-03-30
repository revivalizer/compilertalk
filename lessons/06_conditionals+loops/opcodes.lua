kOpReturn    = 0x00
kOpPush      = 0x10
kOpPop       = 0x11
kOpPushVar   = 0x18
kOpPopVar    = 0x19

kOpCallFunc  = 0x60

kOpJump      = 0x80
kOpJumpEqual = 0x81

-- opcode lookup tables
binary_opcodes = {}
binary_opcodes["+"]  = 0x20
binary_opcodes["-"]  = 0x21
binary_opcodes["*"]  = 0x22
binary_opcodes["/"]  = 0x23
binary_opcodes["%"]  = 0x24
binary_opcodes["=="] = 0x25
binary_opcodes["!="] = 0x26
binary_opcodes["<"]  = 0x27
binary_opcodes["<="] = 0x28
binary_opcodes[">"]  = 0x29
binary_opcodes[">="] = 0x2A
binary_opcodes["&&"] = 0x2B
binary_opcodes["||"] = 0x2C

unary_opcodes = {}
unary_opcodes["!"] = 0x40
unary_opcodes["+"] = 0x41
unary_opcodes["-"] = 0x42


