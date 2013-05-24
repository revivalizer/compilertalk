package compilertalk.compiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import compilertalk.ast.AbstractExpressionNode;
import compilertalk.ast.AbstractNode;
import compilertalk.ast.AssignStatementNode;
import compilertalk.parser.ParseException;
import compilertalk.parser.Parser;
import compilertalk.vm.Stack;
import compilertalk.vm.VM;
import compilertalk.vm.VMProgram;

public class ExpressionTest {

	private static final String[] compileOKTests = {
			"1+2",
			" 1+2",
			"1+ 2",
			"1  +  2",
			"1 + 2 ",
			"1+2+3+4+5",
			"1+-2",
			"-1",
			"12345",
			"0+0",
			"000001+00219",
			"1-2",
			"!1*200/3+4-5*-(1*2)<1<=2>3>=4&&1||2",
			"+2",
			"cos(+2)",
			"sin(+2)",
	};

	private static final String[] compileFailTests = {
			")",
			"#",
			"1+2+",
			"cos(",
			"sin(+2",
	};

	@Test
	public void testCompileOK() throws ParseException {
		for (String str : compileOKTests) {
			parseExpression(str);
		}
	}

	@Test
	public void testCompileFail() {
		for (String str : compileFailTests) {
			try {
				parseExpression(str);
				Assert.fail();
			} catch (Throwable ex) {
			}
		}
	}

	private AbstractExpressionNode parseExpression(String expr) throws ParseException {
		List<AbstractNode> ast = new Parser(new StringReader("q=" + expr + ";")).start();
		Assert.assertEquals(1, ast.size());
		AssignStatementNode ass = (AssignStatementNode) ast.get(0);
		Assert.assertEquals("q", ass.getVariable());
		return ass.getExpression();
	}

	private static class EvalTest {
		private final String expression;
		private final double result;

		public EvalTest(String expression, double result) {
			this.expression = expression;
			this.result = result;
		}
	}

	private static final EvalTest[] evalTests = {
			new EvalTest("100", 100),
			new EvalTest("1+2", 3),
			new EvalTest("3*9", 27),
			new EvalTest("9/3", 3),
			new EvalTest("9/2", 4.5),
			new EvalTest("!1", 0),
			new EvalTest("!0", 1),
			new EvalTest("-1", -1),
			new EvalTest("+1", 1),
			new EvalTest("!!!1", 0),
			new EvalTest("--1", 1),
			new EvalTest("---1", -1),
			new EvalTest("-2+3", 1),
			new EvalTest("2*3+4*5", 26),
			new EvalTest("2*(3+4)*5", 70),
			new EvalTest("1<1", 0),
			new EvalTest("1>1", 0),
			new EvalTest("1<=1", 1),
			new EvalTest("1>=1", 1),
			new EvalTest("1==1", 1),
			new EvalTest("1!=1", 0),
			new EvalTest("2<3", 1),
			new EvalTest("-2<1", 1),
			new EvalTest("3>2", 1),
			new EvalTest("-(2<1)", -0),
			new EvalTest("!(1<1)&&1", 1),
			new EvalTest("3-2-1", 0),
			new EvalTest("27 / 3 / 3", 3),
			new EvalTest("cos(+2)", Math.cos(2)),
			new EvalTest("sin(+2)", Math.sin(2)),
	};

	@Test
	public void testEvalBuiltin() throws Exception {
		for (EvalTest et : evalTests) {
			AbstractExpressionNode expression = parseExpression(et.expression);
			Assert.assertEquals(et.result, expression.evaluate(), 0.00001);
		}
	}

	@Test
	public void testEvalVM() throws Exception {
		for (EvalTest et : evalTests) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Program program = new Program("print(" + et.expression + ");");
			VMProgram prog = new VMProgram(program.generateData());
			Stack stack = new Stack(1000);
			stack.out = new PrintStream(baos);
			VM.run(prog, (short) 0, stack, new float[1000]);
			String output = new String(baos.toByteArray(), "ISO-8859-1");
			Assert.assertEquals(et.result, DecimalFormat.getInstance().parse(output).doubleValue(), 0.00001);
		}
	}
}
