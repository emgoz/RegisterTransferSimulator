package simulationEngine;

import java.util.ArrayList;

import memparser.MemParser;
import memparser.MemoryVisitor;
import memparser.SimpleNode;

public class BlockM extends Block implements Registered {

	private Memory mem;
	private int addrWidth, dataWidth;
	private SimpleNode root;
	private String label, file;
	private ArrayList<WritePort> writePorts = new ArrayList<>();
	private ArrayList<ReadPort> readPorts = new ArrayList<>();

	private class WritePort {
		String addrName, dataName, enaName;

		public WritePort(String addrName, String dataName, String enaName) {
			this.addrName = addrName;
			this.dataName = dataName;
			this.enaName = enaName;
		}
	}

	private class ReadPort {
		String addrName, dataName;

		public ReadPort(String addrName, String dataName) {
			this.addrName = addrName;
			this.dataName = dataName;
		}
	}

	public BlockM(String label, int addrWidth, int dataWidth) {
		this.label = label;
		this.addrWidth = addrWidth;
		this.dataWidth = dataWidth;
		this.mem = new Memory(addrWidth, dataWidth);
	}

	public void updateRegister(){
		for (WritePort w : writePorts) {
			if (terminals.get(w.enaName).getSignal().nonzero()) {
				Signal addr = terminals.get(w.addrName).getSignal();
				Signal value = terminals.get(w.dataName).getSignal();
				mem.storeValue(addr, value);
			}
		}
		updateOutputs();
	}

	public void addWritePort(String addrName, String dataName, String enaName) {
		WritePort w = new WritePort(addrName, dataName, enaName);
		writePorts.add(w);
		terminals.put(addrName, new Terminal(addrWidth, false, this));
		terminals.put(dataName, new Terminal(dataWidth, false, this));
		terminals.put(enaName, new Terminal(1, false, this));
	}

	public void addReadPort(String addrName, String dataName) {
		ReadPort r = new ReadPort(addrName, dataName);
		readPorts.add(r);
		terminals.put(addrName, new Terminal(addrWidth, false, this));
		terminals.put(dataName, new Terminal(dataWidth, true, this));
	}

	public void updateOutputs() {
		for (ReadPort r : readPorts) {
			Signal addr = terminals.get(r.addrName).getSignal();
			Signal value = mem.loadValue(addr);
			terminals.get(r.dataName).setSignal(value);
		}
	}

	@Override
	public void calculate() {
		updateOutputs();
	}

	public void initialize() throws Exception {
		MemoryVisitor mv = new MemoryVisitor();
		try {
			root.jjtAccept(mv, mem);
			calculate();
		} catch (Exception e) {
			err = true;
			throw e;
		}
	}

	public void parseFromFile(String file) throws Exception {
		this.file = file;
		try {
			root = MemParser.parseFile(file);
		} catch (Exception e) {
			err = true;
			throw e;
		}
	}

	public void parseFromString(String hdlStr) throws Exception {
		this.file = null;
		try {
			root = MemParser.parseString(hdlStr+"\n");
		} catch (Exception e) {
			err = true;
			throw e;
		}
	}

	@Override
	public String getLabel() {
		return label;
	}

	public String toString() {
		return "I'm a lonely memory block";
	}

}
