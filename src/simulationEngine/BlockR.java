package simulationEngine;

import hdlparser.CheckVisitor;
import hdlparser.CheckVisitor.HDLCheckException;
import hdlparser.ExecutionVisitor;
import hdlparser.HDLParser;
import hdlparser.ParseException;
import hdlparser.SimpleNode;

import java.util.HashMap;

import memparser.MemParser;
import symbols.LabelStr;
import symbols.Symbol;
import symbols.Symbol.Dir;
import symbols.Variable;

public class BlockR extends Block implements Registered {

	private SimpleNode root;
	protected HashMap<String, Symbol> st;
	protected HashMap<Symbol, Symbol> registerConnections;
	protected HashMap<String, Signal> initialValues;
	private String file;
	private String nextPrefix;

	public BlockR(String nextPrefix, String label) {
		st = new HashMap<>();
		st.put("#", new LabelStr(label));
		registerConnections = new HashMap<>();
		initialValues = new HashMap<>();
		this.nextPrefix = nextPrefix;
	}

	public void setInitialValue(String id, String value) throws Exception {
		try {
			value = MemParser.parseNumberToBinary(value);

			int valueWidth = st.get(id).getSignal().getWidth();
			if (value.length() > valueWidth) {
				value = value.substring(value.length() - valueWidth);
			}
			value = value.replaceFirst("^0+(?!$)", "");

			initialValues.put(id, new Signal(value, valueWidth));
		} catch (Exception e) {
			err = true;
			throw e;
		}
	}

	@Override
	public void initialize() throws Exception {

		for (String id : initialValues.keySet()) {
			try {
				st.get(id).getSignal().setSame(initialValues.get(id));
			} catch (Exception e) {
				err = true;
				throw e;
			}
		}
		calculate();
		updateOutputs();

	}

	@Override
	public void updateRegister() throws Exception {
		// System.out.println("Update init: " + st);
		for (Symbol output : registerConnections.keySet()) {
			try {
				output.getSignal().setSame(registerConnections.get(output).getSignal());
			} catch (Exception e) {
				err = true;
				throw e;
			}
		}
		calculate();
		// System.out.println("Update complete: " + st);
	}

	public void addTerminal(String id, int width, boolean isOutput) {
		terminals.put(id, new Terminal(width, isOutput, this));
		if (isOutput) {
			Variable output = new Variable(Dir.INPUT, width);

			st.put(id, output); // register value is input to logic
			Variable next = new Variable(Dir.OUTPUT, width);
			st.put(nextPrefix + id, next); // next value is output to logic
			registerConnections.put(output, next);

		} else {
			st.put(id, new Variable(Dir.INPUT, width));
		}
	}

	public void updateOutputs() {
		for (String id : terminals.keySet()) {
			Symbol sym = st.get(id);
			Terminal t = terminals.get(id);
			if (t.isOutput()) {
				t.setSignal(sym.getSignal());
			}
		}
	}

	public void updateInputs() throws Exception {
		for (String id : terminals.keySet()) {
			Symbol sym = st.get(id);
			Terminal t = terminals.get(id);
			if (!t.isOutput()) {
				try {
					sym.getSignal().setSame(t.getSignal());
				} catch (Exception e) {
					err = true;
					throw e;
				}
			}
		}
	}

	public void initFromFile(String file) throws Exception {
		this.file = file;
		try {
			root = HDLParser.parseFile(file);
		} catch (ParseException e) {
			err = true;
			throw e;
		}
		CheckVisitor cv = new CheckVisitor(st);
	    try {
	        root.jjtAccept(cv, null);
	    } catch (HDLCheckException e) {
	    	err = true;
            throw new LogicException("in file "+file+", "+e.getMessage());
	    }
	    //createTerminals();
	}
	public void initFromString(String hdlStr) throws Exception {
		this.file = null;
		try {
			root = HDLParser.parseString(hdlStr+"\n");
		} catch (ParseException e) {
			err = true;
			throw e;
		}
		CheckVisitor cv = new CheckVisitor(st);
	    try {
			root.jjtAccept(cv, null);
	    } catch (HDLCheckException e) {
	    	err = true;
            throw new LogicException(e.getMessage());
	    }
	}

	@Override
	public void calculate() throws Exception {
		updateInputs();
		// System.out.println("Calculate(): ST = "+st);
		ExecutionVisitor ev = new ExecutionVisitor();
		try {
			root.jjtAccept(ev, st);
		} catch (HDLCheckException e) {
			err = true;
			if (file != null)
				throw new LogicException("in file " + file + ", " + e.getMessage());
			else
				throw new LogicException(e.getMessage());
		}
		updateOutputs();
	}

	public String toString() {
		String r = "";
		for (String id : terminals.keySet()) {
			Symbol sym = st.get(id);
			Terminal t = terminals.get(id);
			if (t.isOutput()) {
				r += String.format("%s = %s, ", id, sym);
			}
		}
		return r;
	}

	@Override
	public String getLabel() {
		return st.get("#").toString();
	}

}
