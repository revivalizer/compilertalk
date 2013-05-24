package compilertalk.vm;

public enum BinaryOperator {

	ADD(Opcode.kOpAdd) {
		protected float evaluate(float operand1, float operand2) {
			return operand1 + operand2;
		}
	},
	SUBTRACT(Opcode.kOpSubtract) {
		protected float evaluate(float operand1, float operand2) {
			return operand1 - operand2;
		}
	},
	MULTIPLY(Opcode.kOpMultiply) {
		protected float evaluate(float operand1, float operand2) {
			return operand1 * operand2;
		}
	},
	DIVIDE(Opcode.kOpDivide) {
		protected float evaluate(float operand1, float operand2) {
			return operand1 / operand2;
		}
	},
	MODULO(Opcode.kOpModulo) {
		protected float evaluate(float operand1, float operand2) {
			return operand1 % operand2;
		}
	},
	EQUAL(Opcode.kOpEqual) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 == operand2) ? 1.f : 0.f;
		}
	},
	NOT_EQUAL(Opcode.kOpNotEqual) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 != operand2) ? 1.f : 0.f;
		}
	},
	LESS_THAN(Opcode.kOpLessThan) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 < operand2) ? 1.f : 0.f;
		}
	},
	LESS_THAN_OR_EQUAL(Opcode.kOpLessThanOrEqual) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 <= operand2) ? 1.f : 0.f;
		}
	},
	GREATER_THAN(Opcode.kOpGreaterThan) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 > operand2) ? 1.f : 0.f;
		}
	},
	GREATER_THAN_OR_EQUAL(Opcode.kOpGreaterThanOrEqual) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 >= operand2) ? 1.f : 0.f;
		}
	},
	LOGICAL_AND(Opcode.kOpLogicalAnd) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 != 0.f && operand2 != 0.f) ? 1.f : 0.f;
		}
	},
	LOGICAL_OR(Opcode.kOpLogicalOr) {
		protected float evaluate(float operand1, float operand2) {
			return (operand1 != 0.f || operand2 != 0.f) ? 1.f : 0.f;
		}
	};

	private static BinaryOperator[] lookupTable = new BinaryOperator[0x100];
	private Opcode opcode;

	static {
		for (BinaryOperator op : values()) {
			lookupTable[op.getOpcode().getValue()] = op;
		}
	}

	public static BinaryOperator forOpcode(Opcode opcode) {
		BinaryOperator result = lookupTable[opcode.getValue()];
		if (result != null)
			return result;
		if (opcode.getType() != Opcode.Type.BINARY)
			throw new IllegalArgumentException("Opcode is not binary operator: " + opcode.getType());
		else
			throw new IllegalArgumentException("No binary operator for opcode " + opcode);
	}

	private BinaryOperator(Opcode opcode) {
		this.opcode = opcode;
	}

	protected abstract float evaluate(float operand1, float operand2);

	public Opcode getOpcode() {
		return opcode;
	}
}
