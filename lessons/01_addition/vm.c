#ifdef TEST_MODE
#include "testmode.h"
#else
#include "test.h"
#endif
#include "stdint.h"
#include "stdio.h"

// Define opcodes
enum
{
	kOpReturn = 0x00,
	kOpPush   = 0x10,
	kOpAdd    = 0x20,
};

// Define types
typedef unsigned short opcode_t;
typedef float           constant_t;

typedef struct
{
	opcode_t*   bytecode;
	constant_t* constants;
} vm_program;

// Add base offset to program pointers (stored as offsets from 0)
vm_program* update_vm_program(vm_program* program)
{
	program->bytecode = (opcode_t*)((uintptr_t)program + (uintptr_t)program->bytecode);
	program->constants = (constant_t*)((uintptr_t)program + (uintptr_t)program->constants);

	return program;
}

void vm_run(vm_program* program, opcode_t ip, constant_t* stack)
{
	// stack always points to the top position where the next element should be pushed

	while (1)
	{
		opcode_t op = program->bytecode[ip];

		switch (op)
		{
			case kOpReturn:
				return;
			case kOpPush:
				stack[0] = program->constants[program->bytecode[ip+1]];
				stack++;
				ip+=2;
				break;
			case kOpAdd:
				stack[-2] = stack[-2] + stack[-1];
				stack--;
				ip+=1;
				break;
		}
	}
}

float vm_stack[1000];

int main(int argc, char* argv[])
{
#ifdef TEST_MODE
	vm_program* program = update_vm_program((vm_program*)load(argv[1]));
#else
	vm_program* program = update_vm_program((vm_program*)vm_data_raw);
#endif
	vm_run(program, 0, vm_stack);

	printf("VM result: %f\n", vm_stack[0]);

	return 0;
}
