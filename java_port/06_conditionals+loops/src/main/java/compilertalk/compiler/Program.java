package compilertalk.compiler;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import compilertalk.ast.AbstractExpressionNode;
import compilertalk.ast.AbstractNode;
import compilertalk.ast.AssignStatementNode;
import compilertalk.ast.BinaryOperatorNode;
import compilertalk.ast.FunctionCallNode;
import compilertalk.ast.FunctionCallStatementNode;
import compilertalk.ast.IfStatementNode;
import compilertalk.ast.LiteralNode;
import compilertalk.ast.UnaryOperatorNode;
import compilertalk.ast.VariableNode;
import compilertalk.ast.WhileStatementNode;
import compilertalk.parser.ParseException;
import compilertalk.parser.Parser;
import compilertalk.util.UniqueSet;
import compilertalk.vm.Opcode;

public class Program {

	private final List<AbstractNode> ast;

	private final UniqueSet<Integer> constants = new UniqueSet<Integer>();
	private final UniqueSet<String> variables = new UniqueSet<String>();

	private final List<Label> labels = new ArrayList<Label>();
	private final List<OpcodeWithArgs> bytecode = new ArrayList<OpcodeWithArgs>();
	int bytecodeSize = 0;

	private final List<Integer> labelOffsets = new ArrayList<Integer>();

	public Program(String str) throws ParseException {
		ast = new Parser(new StringReader(str)).start();

		checkSemantics(ast);
		for (AbstractNode node : ast) {
			node.generateBytecode(this);
		}
		addBytecode(Opcode.kOpReturn);

		updateLabels();
	}

	private static void checkSemantics(List<AbstractNode> ast) {
		for (AbstractNode node : ast) {
			checkSemantics(node);
		}
	}

	private static void checkSemantics(AbstractNode node) {
		if (node instanceof FunctionCallNode) {
			FunctionCallNode fn = (FunctionCallNode) node;
			FunctionInfo f = fn.getFunctionInfo();

			if (f.getArgumentCount() != fn.getArguments().size()) {
				throw new RuntimeException("Compilation error: function '" + fn.getIdentifier() + "' expects " + f.getArgumentCount() + " arguments, got " + fn.getArguments().size() + ".");
			}
			for (AbstractExpressionNode arg : fn.getArguments()) {
				checkExpression(arg);
			}
		} else if (node instanceof AssignStatementNode) {
			checkExpression(((AssignStatementNode) node).getExpression());
		} else if (node instanceof IfStatementNode) {
			IfStatementNode in = (IfStatementNode) node;
			checkExpression(in.getExpression());
			checkSemantics(in.getIfBlock());
			if (in.getElseBlock() != null)
				checkSemantics(in.getElseBlock());
		} else if (node instanceof WhileStatementNode) {
			WhileStatementNode wn = (WhileStatementNode) node;
			checkExpression(wn.getExpression());
			checkSemantics(wn.getBlock());
		} else {
			for (AbstractNode child : children(node)) {
				checkSemantics(child);
			}
		}
	}

	private static void checkExpression(AbstractExpressionNode node) {
		if (node instanceof FunctionCallNode) {
			FunctionInfo func = ((FunctionCallNode) node).getFunctionInfo();
			if (func.getResult() != FunctionInfo.ResultType.NUMBER)
				throw new RuntimeException("Function '" + ((FunctionCallNode) node).getIdentifier() + "' does not return a number.");
		} else if (node instanceof LiteralNode || node instanceof UnaryOperatorNode || node instanceof BinaryOperatorNode || node instanceof VariableNode) {
			// this is ok, do nothing
		} else {
			throw new RuntimeException("Node not an expression");
		}

		for (AbstractExpressionNode child : childExpressions(node)) {
			checkExpression(child);
		}
	}

	private static List<? extends AbstractNode> children(AbstractNode node) {
		if (node instanceof AbstractExpressionNode) {
			return childExpressions((AbstractExpressionNode) node);
		} else if (node instanceof FunctionCallStatementNode) {
			return Arrays.asList(((FunctionCallStatementNode) node).getFunctionCallNode());
		} else if (node instanceof AssignStatementNode) {
			return Arrays.asList(((AssignStatementNode) node).getExpression());
		} else if (node instanceof IfStatementNode) {
			IfStatementNode in = (IfStatementNode) node;
			List<AbstractNode> result = new ArrayList<AbstractNode>();
			result.add(in.getExpression());
			result.addAll(in.getIfBlock());
			if (in.getElseBlock() != null)
				result.addAll(in.getElseBlock());
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	private static List<AbstractExpressionNode> childExpressions(AbstractExpressionNode node) {
		if (node instanceof BinaryOperatorNode) {
			BinaryOperatorNode bn = (BinaryOperatorNode) node;
			return Arrays.asList(bn.getOperand1(), bn.getOperand2());
		} else if (node instanceof UnaryOperatorNode) {
			return Arrays.asList(((UnaryOperatorNode) node).getOperand());
		} else if (node instanceof FunctionCallNode) {
			return ((FunctionCallNode) node).getArguments();
		} else {
			return Collections.emptyList();
		}
	}

	private void updateLabels() {
		// create sorted label list
		Label[] sortedLabels = labels.toArray(new Label[labels.size()]);
		Arrays.sort(sortedLabels, new Comparator<Label>() {
			@Override
			public int compare(Label o1, Label o2) {
				return o1.address - o2.address;
			}
		});

		// create distinct address set
		UniqueSet<Integer> distinctLabels = new UniqueSet<Integer>();
		for (Label label : sortedLabels)
			distinctLabels.getIDZeroBased(label.address);

		// update label references in bytecode
		for (int i = 0; i < bytecode.size(); i++) {
			if (bytecode.get(i).labelArg != null) {
				bytecode.set(i, bytecode.get(i).resolveLabel(distinctLabels));
			}
		}

		// generate new label set from sorted, distinct set
		labelOffsets.addAll(Arrays.asList(distinctLabels.toArray(new Integer[0])));
	}

	public UniqueSet<Integer> getConstants() {
		return constants;
	}

	public UniqueSet<String> getVariables() {
		return variables;
	}

	public void addBytecode(Opcode opcode) {
		bytecode.add(new OpcodeWithArgs(opcode));
		bytecodeSize++;
	}

	public void addBytecode(Opcode opcode, short arg) {
		bytecode.add(new OpcodeWithArgs(opcode, arg));
		bytecodeSize += 2;
	}

	public void addBytecode(Opcode opcode, Label label) {
		bytecode.add(new OpcodeWithArgs(opcode, label));
		bytecodeSize += 2;
	}

	public void addLabel(Label label) {
		label.address = bytecodeSize;
	}

	public byte[] generateData() throws IOException {
		Integer[] consts = constants.toArray(new Integer[0]);
		byte[] constArray = new byte[consts.length * 4];
		FloatBuffer constBuffer = ByteBuffer.wrap(constArray).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
		for (int i = 0; i < consts.length; i++) {
			constBuffer.put((float) (int) consts[i]);
		}

		byte[] bytecodeArray = new byte[bytecodeSize * 2];
		ShortBuffer bytecodeBuffer = ByteBuffer.wrap(bytecodeArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		for (OpcodeWithArgs opcode : bytecode) {
			opcode.put(bytecodeBuffer);
		}

		byte[] labelArray = new byte[labelOffsets.size() * 2];
		ShortBuffer labelBuffer = ByteBuffer.wrap(labelArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		for (Integer offset : labelOffsets) {
			labelBuffer.put((short) (int) offset);
		}

		int headerSize = 12;
		int constantsPos = headerSize;
		int opcodePos = constantsPos + constArray.length;
		int labelsPos = opcodePos + bytecodeArray.length;
		int totalLength = labelsPos + labelArray.length;

		byte[] result = new byte[totalLength];
		ByteBuffer bb = ByteBuffer.wrap(result).order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(opcodePos);
		bb.putInt(constantsPos);
		bb.putInt(labelsPos);
		bb.put(constArray);
		bb.put(bytecodeArray);
		bb.put(labelArray);
		return result;
	}

	public class Label {
		private int address = -1;

		public Label() {
			labels.add(this);
		}
	}

	private static class OpcodeWithArgs {

		private final Opcode opcode;
		private final boolean argUsed;
		private final short arg;
		private final Label labelArg;

		public OpcodeWithArgs(Opcode opcode) {
			this(opcode, new short[0]);
		}

		public OpcodeWithArgs(Opcode opcode, short arg) {
			this(opcode, new short[] { arg });
		}

		public OpcodeWithArgs(Opcode opcode, Label arg) {
			if (opcode.getType() != Opcode.Type.JUMP)
				throw new IllegalArgumentException(opcode + " is not a jump");
			if (arg == null)
				throw new NullPointerException("label is null");
			this.opcode = opcode;
			this.argUsed = false;
			this.arg = 0;
			this.labelArg = arg;
		}

		private OpcodeWithArgs(Opcode opcode, short[] args) {
			if (opcode.getType().getArgLength() != args.length)
				throw new IllegalArgumentException(opcode + " needs " + opcode.getType().getArgLength() + " args, not " + args.length);
			this.opcode = opcode;
			this.argUsed = args.length == 1;
			this.arg = args.length == 0 ? 0 : args[0];
			this.labelArg = null;
		}

		private OpcodeWithArgs resolveLabel(UniqueSet<Integer> distinctLabels) {
			return new OpcodeWithArgs(opcode, distinctLabels.getIDZeroBased(labelArg.address));
		}

		private void put(ShortBuffer buf) {
			if (labelArg != null)
				throw new RuntimeException("Label must be resolved first!");
			buf.put(opcode.getValue());
			if (argUsed)
				buf.put(arg);
		}
	}
}
