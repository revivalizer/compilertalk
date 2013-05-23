package compilertalk.ast;

import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;

public class AssignStatementNode extends AbstractNode {

	private String variable;
	private AbstractExpressionNode expression;

	public AssignStatementNode(String variable, AbstractExpressionNode expression) {
		this.variable = variable;
		this.expression = expression;
	}

	public String getVariable() {
		return variable;
	}
	
	public AbstractExpressionNode getExpression() {
		return expression;
	}

	@Override
	public void generateBytecode(Program program) {
		expression.generateBytecode(program);
		short variable_index = program.getVariables().getIDZeroBased(variable);
		program.addBytecode(Opcode.kOpPopVar, variable_index);
	}
}
