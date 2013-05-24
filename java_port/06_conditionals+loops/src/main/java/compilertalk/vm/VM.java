package compilertalk.vm;

public class VM {

	public static void run(VMProgram program, short ip, Stack stack, float[] variables) {
		while (true)
		{
			Opcode op = Opcode.forValue(program.bytecode[ip]);
			switch (op.getType()) {
			case UNARY:
				UnaryOperator uo = UnaryOperator.forOpcode(op);
				stack.values[stack.sp - 1] = uo.evaluate(stack.values[stack.sp - 1]);
				ip++;
				break;
			case BINARY:
				BinaryOperator bo = BinaryOperator.forOpcode(op);
				stack.values[stack.sp - 2] = bo.evaluate(stack.values[stack.sp - 2], stack.values[stack.sp - 1]);
				stack.sp--;
				ip++;
				break;
			case JUMP:
				boolean jumpTaken = true;
				if (op == Opcode.kOpJumpEqual) {
					jumpTaken = stack.values[stack.sp - 1] == 0.f;
					stack.sp--;
				}
				if (jumpTaken) {
					ip = program.labels[program.bytecode[ip + 1]];
				} else {
					ip += 2;
				}
				break;
			case SPECIAL_WITH_ARG:
			case SPECIAL_NO_ARG:
				switch (op) {
				case kOpReturn:
					return;
				case kOpPush:
					stack.values[stack.sp + 0] = program.constants[program.bytecode[ip + 1]];
					stack.sp++;
					ip += 2;
					break;
				case kOpPop:
					stack.sp--;
					ip += 1;
					break;
				case kOpPushVar:
					stack.values[stack.sp + 0] = variables[program.bytecode[ip + 1]];
					stack.sp++;
					ip += 2;
					break;
				case kOpPopVar:
					variables[program.bytecode[ip + 1]] = stack.values[stack.sp - 1];
					stack.sp--;
					ip += 2;
					break;
				case kOpCallFunc:
					Function f = Function.fromIndex(program.bytecode[ip + 1]);
					f.execute(stack);
					ip += 2;
					break;
				default:
					throw new IllegalStateException("Unsupported opcode " + op);
				}
				break;
			default:
				throw new IllegalStateException("Unsupported opcode " + op);
			}
		}
	}

	public static void run(VMProgram program) {
		run(program, (short) 0, new Stack(1000), new float[1000]);
	}

	public static float evaluateBinary(float operand1, Opcode opcode, Float operand2) {
		return BinaryOperator.forOpcode(opcode).evaluate(operand1, operand2);
	}

	public static float evaluateUnary(Opcode opcode, float operand) {
		return UnaryOperator.forOpcode(opcode).evaluate(operand);
	}

	public static float callFunction(short functionIndex, float[] operands) {
		Stack stack = new Stack(operands.length + 1);
		for (int i = 0; i < operands.length; i++) {
			stack.values[i] = operands[i];
		}
		stack.sp = operands.length;
		Function.fromIndex(functionIndex).execute(stack);
		return stack.sp == 0 ? Float.NaN : stack.values[0];
	}

	// example program from the Lua compiler
	private static final byte[] vm_data_raw = { 0x28, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x00, (byte) 0x96, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x40, 0x00, 0x00, (byte) 0x80, 0x3F, 0x00, 0x00, (byte) 0x80, 0x40, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, (byte) 0xA0, 0x40, 0x00, 0x00, (byte) 0xC0, 0x40, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x19, 0x00, 0x00, 0x00, 0x18, 0x00, 0x00, 0x00, 0x10, 0x00, 0x01, 0x00, 0x25, 0x00, (byte) 0x81, 0x00, 0x00, 0x00, 0x10, 0x00, 0x02, 0x00, 0x60, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x02, 0x00, 0x18, 0x00, 0x00, 0x00, 0x10, 0x00, 0x03, 0x00, 0x25, 0x00, (byte) 0x81, 0x00, 0x01, 0x00, 0x10, 0x00, 0x04, 0x00, 0x60, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x02, 0x00, 0x10, 0x00, 0x05, 0x00, 0x60, 0x00, 0x00, 0x00, 0x18, 0x00, 0x00, 0x00, 0x10, 0x00, 0x06, 0x00, 0x2A, 0x00, (byte) 0x81, 0x00, 0x03, 0x00, 0x18, 0x00, 0x00, 0x00, 0x60, 0x00, 0x00, 0x00, 0x18, 0x00, 0x00, 0x00, 0x10, 0x00, 0x01, 0x00, 0x21, 0x00, 0x19, 0x00, 0x00, 0x00, (byte) 0x80,
			0x00, 0x02, 0x00, 0x00, 0x00, 0x11, 0x00, 0x1E, 0x00, 0x22, 0x00, 0x36, 0x00, };

	public static void main(String[] args) {
		VMProgram program = new VMProgram(vm_data_raw);
		run(program);
	}
}
