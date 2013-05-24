package compilertalk.vm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class VMProgram {
	public final short[] bytecode;
	public final float[] constants;
	public final short[] labels;

	public VMProgram(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		int bytecodeOffset = bb.getInt();
		int constantOffset = bb.getInt();
		int labelOffset = bb.getInt();
		if (constantOffset > bytecodeOffset || bytecodeOffset > labelOffset || labelOffset > data.length)
			throw new IllegalStateException("Unexpected order of internal buffers");
		constants = new float[(bytecodeOffset - constantOffset) / 4];
		bb.position(constantOffset);
		bb.asFloatBuffer().get(constants);
		bytecode = new short[(labelOffset - bytecodeOffset) / 2];
		labels = new short[(data.length - labelOffset) / 2];
		bb.position(bytecodeOffset);
		ShortBuffer sb = bb.asShortBuffer();
		sb.get(bytecode);
		sb.get(labels);
	}
}
