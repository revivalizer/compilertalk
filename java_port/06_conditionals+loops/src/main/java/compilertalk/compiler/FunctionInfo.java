package compilertalk.compiler;

import compilertalk.vm.Function;

public enum FunctionInfo {

	print(Function.kFuncPrint, ResultType.VOID, "Prints number.",
			new ArgInfo(ArgType.NUMBER, "Number to print.")),

	sin(Function.kFuncSin, ResultType.NUMBER, "Returns sine of a number.",
			new ArgInfo(ArgType.NUMBER, "Angle in radians.")),

	cos(Function.kFuncCos, ResultType.NUMBER, "Returns cosine of a number.",
			new ArgInfo(ArgType.NUMBER, "Angle in radians."));

	private final Function func;
	private final ArgInfo[] arguments;
	private final ResultType result;
	private final String description;

	private FunctionInfo(Function func, ResultType result, String description, ArgInfo... arguments) {
		this.func = func;
		this.result = result;
		this.description = description;
		this.arguments = arguments;
	}

	public Function getFunction() {
		return func;
	}

	public int getArgumentCount() {
		return arguments.length;
	}

	public ResultType getResult() {
		return result;
	}

	private static class ArgInfo {
		private final ArgType type;
		private final String description;

		public ArgInfo(ArgType type, String description) {
			this.type = type;
			this.description = description;
		}
	}

	public static enum ResultType {
		VOID, NUMBER
	}

	private static enum ArgType {
		NUMBER
	}
}
