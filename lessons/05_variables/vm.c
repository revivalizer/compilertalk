#include "test.h"
#include "stdio.h"
#include "math.h"

// Define opcodes
enum
{
	kOpReturn   = 0x00,
	kOpPush     = 0x10,
	kOpPop      = 0x11,
	kOpPushVar  = 0x18,
	kOpPopVar   = 0x19,

	kOpAdd                = 0x20,
	kOpSubtract           = 0x21,
	kOpMultiply           = 0x22,
	kOpDivide             = 0x23,
	kOpModulo             = 0x24,
	kOpEqual              = 0x25,
	kOpNotEqual           = 0x26,
	kOpLessThan           = 0x27,
	kOpLessThanOrEqual    = 0x28,
	kOpGreaterThan        = 0x29,
	kOpGreaterThanOrEqual = 0x2A,
	kOpLogicalAnd         = 0x2B,
	kOpLogicalOr          = 0x2C,

	kOpNot    = 0x40,
	kOpPlus   = 0x41,
	kOpMinus  = 0x42,

	kOpCallFunc  = 0x60,
};

enum
{
	kFuncPrint = 0,
	kFuncSin   = 1,
	kFuncCos   = 2,
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

void vm_run(vm_program* program, opcode_t ip, float* stack, float* variables)
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
			case kOpPop:
				stack--;
				ip+=1;
				break;
			case kOpPushVar:
				stack[0] = variables[program->bytecode[ip+1]];
				stack++;
				ip+=2;
				break;
			case kOpPopVar:
				variables[program->bytecode[ip+1]] = stack[-1];
				stack--;
				ip+=2;
				break;
			case kOpAdd:
				stack[-2] = stack[-2] + stack[-1];
				stack--;
				ip+=1;
				break;
			case kOpSubtract:
				stack[-2] = stack[-2] - stack[-1];
				stack--;
				ip+=1;
				break;
			case kOpMultiply:
				stack[-2] = stack[-2] * stack[-1];
				stack--;
				ip+=1;
				break;
			case kOpDivide:
				stack[-2] = stack[-2] / stack[-1];
				stack--;
				ip+=1;
				break;
			case kOpModulo:
				stack[-2] = fmodf(stack[-2], stack[-1]);
				stack--;
				ip+=1;
				break;
			case kOpEqual:
				stack[-2] = (stack[-2] == stack[-1]) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpNotEqual:
				stack[-2] = (stack[-2] != stack[-1]) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpLessThan:
				stack[-2] = (stack[-2] < stack[-1]) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpLessThanOrEqual:
				stack[-2] = (stack[-2] <= stack[-1]) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpGreaterThan:
				stack[-2] = (stack[-2] > stack[-1]) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpGreaterThanOrEqual:
				stack[-2] = (stack[-2] >= stack[-1]) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpLogicalAnd:
				stack[-2] = (stack[-2]!=0.f && stack[-1]!=0.f) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpLogicalOr:
				stack[-2] = (stack[-2]!=0.f || stack[-1]!=0.f) ? 1.f : 0.f;
				stack--;
				ip+=1;
				break;
			case kOpNot:
				stack[-1] = (stack[-1]==0.f) ? 1.f : 0.f;
				ip+=1;
				break;
			case kOpPlus:
				stack[-1] = +stack[-1];
				ip+=1;
				break;
			case kOpMinus:
				stack[-1] = +stack[-1];
				ip+=1;
				break;
			case kOpCallFunc:
			{
				switch (program->bytecode[ip+1])
				{
					case kFuncPrint:
						printf("%f\n", stack[-1]);
						stack--;
						break;
					case kFuncSin:
						stack[-1] = sinf(stack[-1]);
						break;
					case kFuncCos:
						stack[-1] = cosf(stack[-1]);
						break;
				}

				ip+=2;
				break;
			}
		}
	}
}

float vm_stack[1000];
float vm_variables[1000];

int main(void)
{
	vm_program* program = update_vm_program((vm_program*)vm_data_raw);
	vm_run(program, 0, vm_stack, vm_variables);

	return 0;
}
