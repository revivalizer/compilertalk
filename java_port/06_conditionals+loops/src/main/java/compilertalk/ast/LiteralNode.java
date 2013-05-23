package compilertalk.ast;

import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;

public class LiteralNode extends AbstractExpressionNode {

	private int value;

	public LiteralNode(int value) {
		this.value = value;
	}

	@Override
	public void generateBytecode(Program program) {
		short constantIndex = program.getConstants().getIDZeroBased(value);
		program.addBytecode(Opcode.kOpPush, constantIndex);
	}
	
	@Override
	public float evaluate() {
		return value;
	}
}
