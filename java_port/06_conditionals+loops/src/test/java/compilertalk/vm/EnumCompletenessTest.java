package compilertalk.vm;

import org.junit.Assert;

import org.junit.Test;

public class EnumCompletenessTest {

	@Test
	public void testFunctions() {
		for(Function func : Function.values()) {
			Assert.assertSame(func, Function.fromIndex(func.getIndex()));
		}
	}
	
	@Test
	public void testBinaryOperators() {
		for(Opcode opcode : Opcode.values()) {
			if (opcode.getType() == Opcode.Type.BINARY) {
				Assert.assertSame(opcode, BinaryOperator.forOpcode(opcode).getOpcode());
			}
		}
	}
	
	@Test
	public void testUnaryOperators() {
		for(Opcode opcode : Opcode.values()) {
			if (opcode.getType() == Opcode.Type.UNARY) {
				Assert.assertSame(opcode, UnaryOperator.forOpcode(opcode).getOpcode());
			}
		}
	}
}
