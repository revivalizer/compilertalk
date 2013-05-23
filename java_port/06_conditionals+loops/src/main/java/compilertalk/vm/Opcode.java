package compilertalk.vm;

public enum Opcode {

	kOpReturn(0x00, Type.SPECIAL_NO_ARG),
	kOpPush(0x10, Type.SPECIAL_WITH_ARG),
	kOpPop(0x11, Type.SPECIAL_NO_ARG),
	kOpPushVar(0x18, Type.SPECIAL_WITH_ARG),
	kOpPopVar(0x19, Type.SPECIAL_WITH_ARG),

	kOpAdd(0x20, Type.BINARY),
	kOpSubtract(0x21, Type.BINARY),
	kOpMultiply(0x22, Type.BINARY),
	kOpDivide(0x23, Type.BINARY),
	kOpModulo(0x24, Type.BINARY),
	kOpEqual(0x25, Type.BINARY),
	kOpNotEqual(0x26, Type.BINARY),
	kOpLessThan(0x27, Type.BINARY),
	kOpLessThanOrEqual(0x28, Type.BINARY),
	kOpGreaterThan(0x29, Type.BINARY),
	kOpGreaterThanOrEqual(0x2A, Type.BINARY),
	kOpLogicalAnd(0x2B, Type.BINARY),
	kOpLogicalOr(0x2C, Type.BINARY),

	kOpNot(0x40, Type.UNARY),
	kOpPlus(0x41, Type.UNARY),
	kOpMinus(0x42, Type.UNARY),

	kOpCallFunc(0x60, Type.SPECIAL_WITH_ARG),

	kOpJump(0x80, Type.JUMP),
	kOpJumpEqual(0x81, Type.JUMP);

	private static Opcode[] lookupTable = new Opcode[0x100];

	static {
		for (Opcode opcode : values()) {
			lookupTable[opcode.getValue()] = opcode;
		}
	}

	public static Opcode forValue(short value) {
		Opcode result = lookupTable[value];
		if (result == null)
			throw new IllegalArgumentException("No opcode for value " + value);
		return result;
	}

	private short value;
	private Type type;

	private Opcode(int value, Type type) {
		this.value = (short) value;
		this.type = type;
	}

	public short getValue() {
		return value;
	}

	public Type getType() {
		return type;
	}

	public static enum Type {
		UNARY(0), BINARY(0), JUMP(1), SPECIAL_NO_ARG(0), SPECIAL_WITH_ARG(1);

		private int argLength;

		private Type(int argLength) {
			this.argLength = argLength;
		}

		public int getArgLength() {
			return argLength;
		}
	}
}
