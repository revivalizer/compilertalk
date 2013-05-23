package compilertalk.compiler;

import org.junit.Test;

import compilertalk.ast.BinaryOperatorNode;
import compilertalk.ast.UnaryOperatorNode;
import compilertalk.vm.BinaryOperator;
import compilertalk.vm.Opcode;
import compilertalk.vm.UnaryOperator;

// test some edge cases to increase test coverage :)
public class EdgeCasesTest {

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidBinaryOp() {
		BinaryOperator.forOpcode(Opcode.kOpPush);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidBinaryOpNode() {
		BinaryOperatorNode.Operator.fromString("<=>");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidUnaryOps() {
		UnaryOperator.forOpcode(Opcode.kOpPush);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidUnaryOpNode() {
		UnaryOperatorNode.Operator.fromChar('q');
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidOpcode() {
		Opcode.forValue((short)0xff);
	}

}
