package compilertalk.compiler;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import compilertalk.parser.ParseException;
import compilertalk.parser.Parser;

public class CompileTest {

	private static final String[] compileOKTests = {
			"print(1+2);",
			" print( 1+2 );",
			"print(1+ 2);",
			"print(1  +  2);",
			"print(1 + 2); ",
			"print(1+2+3+4+5);",
			"print(1+-2);",
			"print(-1);",
			"print(12345);",
			"print(0+0);",
			"print(000001+00219);",
			"print(1-2);",
			"print(!1*200/3+4-5*-(1*2)<1<=2>3>=4&&1||2);",
			"print(+2);print(1);",
			"print(cos(+2));",
			"print(sin(+2));",
			"cos(1);",
			"sin(1);",
			"a=cos(1);",
			"a=sin(1);b=a;",
			"if(a) print(1);",
			"if (a) print(1); else if (b) print(2); else print(3);",
			"while (a) { a=a-1; }",
	};

	private static final String[] compileFailTests = {
			"a",
			"1+2+",
			"print(1+);",
			" print( 1+2 )",
			"prin(1+ 2);",
			"print(1  +  2,);",
			"print(print(1));",
			"b=print(a);",
			"a=b=1;",
			"while (print(1))",
			// TODO port those to LUA?
			"print();",
			"print(1,2);",
			"sin();",
			"sin(1,2);",
			"print(print(9));",
	};
	
	@Test
	public void testCompileOK() throws ParseException {
		for (String str : compileOKTests) {
			new Parser(new StringReader(str)).start();
		}
	}

	@Test
	public void testCompileFail() {
		for (String str : compileFailTests) {
			try {
				new Parser(new StringReader(str)).start();
				Assert.fail();
			} catch (Throwable ex) {
			}
		}
	}
}
