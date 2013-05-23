package compilertalk.ast;

import java.util.List;

import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;

public class WhileStatementNode extends AbstractNode {

	private AbstractExpressionNode expression;
	private List<AbstractNode> block;

	public WhileStatementNode(AbstractExpressionNode expression, List<AbstractNode> block) {
		this.expression = expression;
		this.block = block;
	}

	public AbstractExpressionNode getExpression() {
		return expression;
	}

	public List<AbstractNode> getBlock() {
		return block;
	}

	@Override
	public void generateBytecode(Program program) {
		Program.Label test = program.new Label();
		Program.Label finish = program.new Label();
		program.addLabel(test);
		expression.generateBytecode(program);
		program.addBytecode(Opcode.kOpJumpEqual, finish);
		for (AbstractNode node : block) {
			node.generateBytecode(program);
		}
		program.addBytecode(Opcode.kOpJump, test);
		program.addLabel(finish);
	}
}
