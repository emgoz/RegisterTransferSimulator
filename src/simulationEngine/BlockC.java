package simulationEngine;

import hdlparser.CheckVisitor;
import hdlparser.CheckVisitor.HDLCheckException;
import hdlparser.ExecutionVisitor;
import hdlparser.HDLParser;
import hdlparser.ParseException;
import hdlparser.SimpleNode;

import java.util.HashMap;

import javax.print.DocFlavor.INPUT_STREAM;

import symbols.LabelStr;
import symbols.Symbol;
import symbols.Symbol.Dir;
import symbols.Variable;

public class BlockC extends Block {
	
	private SimpleNode root;
	protected HashMap<String, Symbol> st;
	private String file;

	public BlockC(String label) {
		st = new HashMap<>();
		st.put("#", new LabelStr(label));
	}
	
	public void addTerminal(String id, int width, boolean isOutput) {
		terminals.put(id, new Terminal(width, isOutput, this));
		Symbol.Dir dir = isOutput?Dir.OUTPUT:Dir.INPUT;
		st.put(id, new  Variable(dir, width));
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
		
		ExecutionVisitor ev = new ExecutionVisitor();
		try {
			root.jjtAccept(ev, st);
	    } catch (HDLCheckException e) {
            if (file != null) throw new LogicException("in file "+file+", "+e.getMessage());
            else{
            	err = true;
            	throw new LogicException(e.getMessage());
            }
	    }
		
		updateOutputs();
	}
	@Override
	public String getLabel() {
		return st.get("#").toString();
	}
	public void initialize() throws Exception {
		calculate();
		updateOutputs();
	}

}
