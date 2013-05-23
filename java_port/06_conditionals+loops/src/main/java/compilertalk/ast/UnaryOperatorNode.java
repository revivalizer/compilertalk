package compilertalk.ast;

import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;
import compilertalk.vm.Opcode.Type;
import compilertalk.vm.VM;

public class UnaryOperatorNode extends AbstractExpressionNode {

	private Operator operator;
	private AbstractExpressionNode operand;

	public UnaryOperatorNode(UnaryOperatorNode.Operator operator, AbstractExpressionNode operand) {
		this.operator = operator;
		this.operand = operand;
	}

	public AbstractExpressionNode getOperand() {
		return operand;
	}

	@Override
	public void generateBytecode(Program program) {
		operand.generateBytecode(program);
		program.addBytecode(operator.opcode);
	}
	
	@Override
	public float evaluate() {
		return VM.evaluateUnary(operator.opcode, operand.evaluate());
	}

	public static enum Operator {
		NOT('!', Opcode.kOpNot), PLUS('+', Opcode.kOpPlus), MINUS('-', Opcode.kOpMinus);

		private char ch;
		private Opcode opcode;

		private Operator(char ch, Opcode opcode) {
			if (opcode.getType() != Type.UNARY)
				throw new IllegalArgumentException(opcode + " is not a unary operator!");
			this.ch = ch;
			this.opcode = opcode;
		}

		public char getCharacter() {
			return ch;
		}

		public static Operator fromChar(char ch) {
			for (Operator op : values()) {
				if (op.ch == ch)
					return op;
			}
			throw new IllegalArgumentException("Invalid operator: " + ch);
		}
	}
}
