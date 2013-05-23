package compilertalk.ast;

import compilertalk.compiler.Program;

public abstract class AbstractNode {

	public abstract void generateBytecode(Program program);
}
