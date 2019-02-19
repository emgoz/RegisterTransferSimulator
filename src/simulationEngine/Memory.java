package simulationEngine;

import java.util.HashMap;
import java.util.HashSet;

import memparser.MemParser;
import memparser.MemoryVisitor;
import memparser.SimpleNode;

public class Memory {

	private HashMap<String,String> data = new HashMap<>();
	private HashSet<String> readOnly = new HashSet<>();
	private int addressWidth, valueWidth;
	
	public static void main(String[] args) throws Exception {
		Memory mem = new Memory(4,8,"./exampleFiles/test2.txt");
		mem.dump();
		Signal a = new Signal("0011");
		System.out.println(mem.loadValue(a));
		Signal x = new Signal("00101101");
		mem.storeValue(a, x);
		mem.dump();
		System.out.println(mem.loadValue(a));
		
	}
	public Memory(int addressWidth, int valueWidth) {
		this.addressWidth = addressWidth;
		this.valueWidth = valueWidth;
		data.put(null, "0");
	}
	public Memory(int addressWidth, int valueWidth, String file) throws Exception  {
		this.addressWidth = addressWidth;
		this.valueWidth = valueWidth;
		data.put(null, "0");
		initFromFile(file);
	}
	public void initFromFile(String file) throws Exception {
		SimpleNode root = MemParser.parseFile(file);
		MemoryVisitor mv = new MemoryVisitor();
	    root.jjtAccept(mv, this);
	}
	public void initFromString(String str) throws Exception {
		SimpleNode root = MemParser.parseString(str);
		MemoryVisitor mv = new MemoryVisitor();
	    root.jjtAccept(mv, this);
	}
	public void addValue(String addr, String value) {
		if (addr.length() > addressWidth) {
			addr = addr.substring(addr.length()-addressWidth);
		}
		addr = addr.replaceFirst("^0+(?!$)", "");
		if (value.length() > valueWidth) {
			value = value.substring(value.length()-valueWidth);
		}
		value = value.replaceFirst("^0+(?!$)", "");
		this.data.put(addr, value);
	}
	public void setReadOnly(String addr) {
		if (addr.length() > addressWidth) {
			addr = addr.substring(addr.length()-addressWidth);
		}
		addr = addr.replaceFirst("^0+(?!$)", "");
		this.readOnly.add(addr);
	}
	
	public void setDefault(String value) {
		if (value.length() > valueWidth) {
			value = value.substring(value.length()-valueWidth);
		}
		value = value.replaceFirst("^0+(?!$)", "");
		this.data.put(null, value);
	}
	
	public void storeValue(Signal addr, Signal value) {
		assert addr.getWidth() == addressWidth;
		assert value.getWidth() == valueWidth;
		String aStr = addr.getTruncatedString();
		String vStr = value.getTruncatedString();
		if (vStr.equals(data.get(null))) {  //don't store default values
			if (data.containsKey(aStr))	data.remove(aStr);
		} else if (!readOnly.contains(aStr)){
			data.put(aStr, vStr);
		}
	}
	public Signal loadValue(Signal addr) {
		assert addr.getWidth() == addressWidth;
		String aStr = addr.getTruncatedString();
		String vStr;
		if (data.containsKey(aStr)) {
			vStr = data.get(aStr);
		} else {
			vStr = data.get(null);  //default value
		}
		return new Signal(vStr, valueWidth);
	}
	public void dump() {
		for (String s : data.keySet()) {
			String value = data.get(s);
			if (s == null) {
				s = "default";
			}
			System.out.printf("%10s : %10s\n",s,value);
		}
	}
}
