package compilertalk.ast;

import java.util.List;

import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;

public class IfStatementNode extends AbstractNode {

	private AbstractExpressionNode expression;
	private List<AbstractNode> ifBlock;
	private List<AbstractNode> elseBlock;

	public IfStatementNode(AbstractExpressionNode expression, List<AbstractNode> ifBlock, List<AbstractNode> elseBlock) {
		this.expression = expression;
		this.ifBlock = ifBlock;
		this.elseBlock = elseBlock;
	}

	public AbstractExpressionNode getExpression() {
		return expression;
	}

	public List<AbstractNode> getIfBlock() {
		return ifBlock;
	}

	public List<AbstractNode> getElseBlock() {
		return elseBlock;
	}

	@Override
	public void generateBytecode(Program program) {
		Program.Label postIf = program.new Label();
		expression.generateBytecode(program);
		program.addBytecode(Opcode.kOpJumpEqual, postIf);
		for (AbstractNode node : ifBlock) {
			node.generateBytecode(program);
		}
		if (elseBlock == null) {
			program.addLabel(postIf);
		} else {
			Program.Label postElse = program.new Label();
			program.addBytecode(Opcode.kOpJump, postElse);
			program.addLabel(postIf);
			for (AbstractNode node : elseBlock) {
				node.generateBytecode(program);
			}
			program.addLabel(postElse);
		}
	}
}
