package compilertalk.compiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import compilertalk.vm.Stack;
import compilertalk.vm.VM;
import compilertalk.vm.VMProgram;

public class IntegrationTest {
	
	private static final String[][] integrationTests = {
		{"print(1+2);", "3.000000\n"},
		{" print( 1+2 );", "3.000000\n"},
		{"print(1+ 2);", "3.000000\n"},
		{"print(1  +  2);", "3.000000\n"},
		{"print(1 + 2); ", "3.000000\n"},
		{"print(1+2+3+4+5);", "15.000000\n"},
		{"print(1+-2);", "-1.000000\n"},
		{"print(-1);", "-1.000000\n"},
		{"print(12345);", "12345.000000\n"},
		{"print(0+0);", "0.000000\n"},
		{"print(000001+00219);", "220.000000\n"},
		{"print(1-2);", "-1.000000\n"},
		{"print(!1*200/3+4-5*-(1*2)<1<=2>3>=4&&1||2);", "1.000000\n"},
		{"print(+2);print(1);", "2.000000\n1.000000\n"},
		{"print(cos(+2));", "-0.416147\n"},
		{"print(sin(+2));", "0.909297\n"},
		{"cos(1);", ""},
		{"sin(1);", ""},
		{"a=cos(1);", ""},
		{"a=sin(1);b=a;", ""},
		{"a=sin(1);b=a;print(b);", "0.841471\n"},
		{"if(a) print(1);", ""},
		{"if (a) print(1); else if (b) print(2); else print(3);", "3.000000\n"},
		{"while (a) { a=a-1; }", ""},
		{"a=2; while (a < 100000) a = a * 3 + 1; print(a);", "147622.000000\n"},
		{"if (0) if (0) print(10); else print (42); else print (99);", "99.000000\n"},
		{"if (0) if (1) print(10); else print (42); else print (99);", "99.000000\n"},
		{"if (1) if (0) print(10); else print (42); else print (99);", "42.000000\n"},
		{"if (1) if (1) print(10); else print (42); else print (99);", "10.000000\n"},
		{"cos = cos(cos); print(cos(cos));", "0.540302\n"},
		{"if (1) {} else {}", ""},
		{"print((3 < 9) * (4 <= 4) * (8 > 7) * (8 >= 8));", "1.000000\n"},
		{"print (1 && 2 || 0);", "1.000000\n"},
		{"print (100 % 7);", "2.000000\n"},
	};
	
	@Test
	public void testCompileAndRun() throws Exception {
		for (String[] test : integrationTests) {
			if (test.length != 2)
				throw new RuntimeException("Each test needs its output");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Program program = new Program(test[0]);
			VMProgram vmProgram = new VMProgram(program.generateData());
			Stack stack = new Stack(1000);
			stack.out = new PrintStream(baos);
			stack.locale = Locale.ENGLISH;
			VM.run(vmProgram, (short) 0, stack, new float[1000]);
			String output = new String(baos.toByteArray(), "ISO-8859-1").replace("\r\n", "\n").replace('\r', '\n');
			Assert.assertEquals(test[1], output);		
		}
	}
	
	// TODO increase coverage?
	// TODO port integration tests to Lua?
}
