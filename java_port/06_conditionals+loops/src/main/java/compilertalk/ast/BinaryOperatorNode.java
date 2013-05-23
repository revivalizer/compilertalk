package compilertalk.ast;

import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;
import compilertalk.vm.Opcode.Type;
import compilertalk.vm.VM;

public class BinaryOperatorNode extends AbstractExpressionNode {

	private AbstractExpressionNode operand1;
	private Operator operator;
	private AbstractExpressionNode operand2;

	public BinaryOperatorNode(AbstractExpressionNode operand1, BinaryOperatorNode.Operator operator, AbstractExpressionNode operand2) {
		this.operand1 = operand1;
		this.operator = operator;
		this.operand2 = operand2;
	}

	public AbstractExpressionNode getOperand1() {
		return operand1;
	}

	public AbstractExpressionNode getOperand2() {
		return operand2;
	}

	public BinaryOperatorNode(AbstractExpressionNode operand1, String operator, AbstractExpressionNode operand2) {
		this(operand1, Operator.fromString(operator), operand2);
	}

	@Override
	public void generateBytecode(Program program) {
		operand1.generateBytecode(program);
		operand2.generateBytecode(program);
		program.addBytecode(operator.opcode);
	}
	
	@Override
	public float evaluate() {
		return VM.evaluateBinary(operand1.evaluate(), operator.opcode, operand2.evaluate());
	}

	public static enum Operator {
		// logical operators
		LOGICAL_OR("||", Opcode.kOpLogicalOr), LOGICAL_AND("&&", Opcode.kOpLogicalAnd),

		// additive operators
		PLUS("+", Opcode.kOpAdd), MINUS("-", Opcode.kOpSubtract),

		// multitive operators
		MULTIPLY("*", Opcode.kOpMultiply), DIVIDE("/", Opcode.kOpDivide), MODULO("%", Opcode.kOpModulo),

		// equality operators
		EQUALS("==", Opcode.kOpEqual), NOT_EQUALS("!=", Opcode.kOpNotEqual),

		// relational operators
		LESS_OR_EQUAL("<=", Opcode.kOpLessThanOrEqual), GREATER_OR_EQUAL(">=", Opcode.kOpGreaterThanOrEqual),
		LESS("<", Opcode.kOpLessThan), GREATER(">", Opcode.kOpGreaterThan);

		private String str;
		private Opcode opcode;

		private Operator(String str, Opcode opcode) {
			if (opcode.getType() != Type.BINARY)
				throw new IllegalArgumentException(opcode + " is not a binary operator!");
			this.str = str;
			this.opcode = opcode;
		}

		public static Operator fromString(String str) {
			for (Operator op : values()) {
				if (op.str.equals(str))
					return op;
			}
			throw new IllegalArgumentException("Invalid operator: " + str);
		}
	}
}
