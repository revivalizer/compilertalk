package compilertalk.ast;

import java.util.List;

import compilertalk.compiler.FunctionInfo;
import compilertalk.compiler.Program;
import compilertalk.vm.Function;
import compilertalk.vm.Opcode;
import compilertalk.vm.VM;

public class FunctionCallNode extends AbstractExpressionNode {
	private String identifier;
	private List<AbstractExpressionNode> arguments;

	public FunctionCallNode(String identifier, List<AbstractExpressionNode> arguments) {
		this.identifier = identifier;
		this.arguments = arguments;
	}

	public FunctionInfo getFunctionInfo() {
		return FunctionInfo.valueOf(identifier);
	}

	public String getIdentifier() {
		return identifier;
	}

	public List<AbstractExpressionNode> getArguments() {
		return arguments;
	}

	@Override
	public void generateBytecode(Program program) {
		for (AbstractExpressionNode arg : arguments) {
			arg.generateBytecode(program);
		}
		Function func = getFunctionInfo().getFunction();
		program.addBytecode(Opcode.kOpCallFunc, func.getIndex());
	}
	
	@Override
	public float evaluate() {
		float[] args = new float[arguments.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = arguments.get(i).evaluate();
		}
		return VM.callFunction(getFunctionInfo().getFunction().getIndex(), args);
	}
}