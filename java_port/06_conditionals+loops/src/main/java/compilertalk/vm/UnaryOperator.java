package compilertalk.vm;

public enum UnaryOperator {

	NOT(Opcode.kOpNot) {
		protected float evaluate(float operand) {
			return operand == 0.f ? 1.f : 0.f;
		}
	},
	PLUS(Opcode.kOpPlus) {
		protected float evaluate(float operand) {
			return +operand;
		}
	},
	MINUS(Opcode.kOpMinus) {
		protected float evaluate(float operand) {
			return -operand;
		}
	};

	private static UnaryOperator[] lookupTable = new UnaryOperator[0x100];
	private Opcode opcode;

	static {
		for (UnaryOperator op : values()) {
			lookupTable[op.getOpcode().getValue()] = op;
		}
	}

	public static UnaryOperator forOpcode(Opcode opcode) {
		UnaryOperator result = lookupTable[opcode.getValue()];
		if (result != null)
			return result;
		if (opcode.getType() != Opcode.Type.UNARY)
			throw new IllegalArgumentException("Opcode is not unary operator: " + opcode.getType());
		else
			throw new IllegalArgumentException("No unary operator for opcode " + opcode);
	}

	private UnaryOperator(Opcode opcode) {
		this.opcode = opcode;
	}

	protected abstract float evaluate(float operand);

	public Opcode getOpcode() {
		return opcode;
	}
}