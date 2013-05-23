package compilertalk.compiler;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws Exception {

		String source = "a = 3;\r\n" +
				"\r\n" +
				"		if (a==1)\r\n" +
				"		{\r\n" +
				"			print(4);\r\n" +
				"		}\r\n" +
				"		else if (a==2)\r\n" +
				"		{\r\n" +
				"			print(5);\r\n" +
				"		}\r\n" +
				"		else\r\n" +
				"		{\r\n" +
				"			print(6);\r\n" +
				"		}\r\n" +
				"\r\n" +
				"		while (a>=0)\r\n" +
				"		{\r\n" +
				"			print(a);\r\n" +
				"			a = a - 1;\r\n" +
				"		}";
		Program program = new Program(source);
		exportBinaryAndHeader("test", program);
	}

	private static void exportBinaryAndHeader(String path, Program program) throws IOException {
		byte[] data = program.generateData();
		FileOutputStream f = new FileOutputStream(path + ".bin");
		f.write(data);
		f.close();
		FileWriter fw = new FileWriter(path + ".h");
		fw.write("unsigned char vm_data_raw[] = { ");
		for (int i = 0; i < data.length; i++) {
			fw.write(String.format("0x%02X, ", data[i] & 0xFF));
		}
		fw.write("};");
		fw.close();
	}

}
