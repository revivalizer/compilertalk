package compilertalk.vm;

import java.util.Formatter;

public enum Function {

	kFuncPrint((short) 0) {
		protected void execute(Stack stack) {
			new Formatter(stack.out).format(stack.locale, "%f\n", stack.values[stack.sp - 1]);
			stack.sp--;
		}
	},

	kFuncSin((short) 1) {
		protected void execute(Stack stack) {
			stack.values[stack.sp - 1] = (float) Math.sin(stack.values[stack.sp - 1]);
		}
	},

	kFuncCos((short) 2) {
		protected void execute(Stack stack) {
			stack.values[stack.sp - 1] = (float) Math.cos(stack.values[stack.sp - 1]);
		}
	};

	public static Function[] functionTable = new Function[] { kFuncPrint, kFuncSin, kFuncCos };

	public static Function fromIndex(int index) {
		return functionTable[index];
	}

	private short index;

	private Function(short index) {
		this.index = index;
	}

	protected abstract void execute(Stack stack);

	public short getIndex() {
		return index;
	}
}
