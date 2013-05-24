package compilertalk.ast;

import compilertalk.compiler.FunctionInfo;
import compilertalk.compiler.Program;
import compilertalk.vm.Opcode;

public class FunctionCallStatementNode extends AbstractNode {

	private FunctionCallNode call;

	public FunctionCallStatementNode(FunctionCallNode call) {
		this.call = call;
	}

	public FunctionCallNode getFunctionCallNode() {
		return call;
	}

	@Override
	public void generateBytecode(Program program) {
		call.generateBytecode(program);
		FunctionInfo func = call.getFunctionInfo();
		if (func.getResult() == FunctionInfo.ResultType.NUMBER)
			program.addBytecode(Opcode.kOpPop);
	}
}