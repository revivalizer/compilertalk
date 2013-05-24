package compilertalk.ast;

import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;

public class VariableNode extends AbstractExpressionNode {
	private String identifier;

	public VariableNode(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public void generateBytecode(Program program) {
		short variableIndex = program.getVariables().getIDZeroBased(identifier);
		program.addBytecode(Opcode.kOpPushVar, variableIndex);
	}
	
	@Override
	public float evaluate() {
		throw new IllegalStateException("No variable values present!");
	}
}