--[[
	Assume C struct (32-bit pointers)

	typedef unsigned short opcode_t;
	typedef double constant_t;

	struct vm_program
	{
		opcode_t*   bytecode;
		constant_t* constants;
	};
]]

require("pack")

function generate_data(program)
	-- Generate arrays of data in strings, using the pack library
	local constants_str = string.pack("f"..#program.constants, unpack(program.constants))
	local opcodes_str   = string.pack("H"..#program.bytecode, unpack(program.bytecode))
	local labels_str   = string.pack("H"..#program.labels, unpack(program.labels))

	local header_size = 12 -- assuming 32-bit pointers

	-- Compute offsets to data
	local constants_pos   = header_size
	local opcodes_pos     = constants_pos + constants_str:len()
	local labels_pos     = opcodes_pos + opcodes_str:len()

	-- Generate header data
	local header_str = string.pack("III", opcodes_pos, constants_pos, labels_pos)

	-- Return concantenated data
	return header_str..constants_str..opcodes_str..labels_str
end

function export_binary_and_header(path, program)
	-- Generate raw data
	local data = generate_data(program)

	-- Write binary data
	local f = assert(io.open(path..".bin", "wb"))
	f:write(data)
	f:close()

	-- Create hex encoded string
	local datastr = data:gsub('.', function (c)
        return string.format('0x%02X, ', string.byte(c))
    end)

	-- Write hex data to header
	local f = assert(io.open(path..".h", "w"))
	f:write("unsigned char vm_data_raw[] = { "..datastr.."};")
	f:close()
end
