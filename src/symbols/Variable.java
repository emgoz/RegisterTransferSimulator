package symbols;

import simulationEngine.Signal;

public class Variable extends Symbol {
	
	private Signal signal;	
	private Dir dir;

	public Variable(Dir dir) {
		this.signal = new Signal(1);
		this.dir = dir;
	}
	public Variable(Dir dir, int width) {
		this.signal = new Signal(width);
		this.dir = dir;
	}
	public Variable(Dir dir, String value) {
		this.dir = dir;
		this.signal = new Signal(value);
	}
	
	public Signal getSignal() {
		return signal;
	}
	public void setDir(Dir dir) {
		this.dir = dir;
	}

	
	public String toString() {
		return this.signal.toString();
	}
	@Override
	public boolean writeable() {
		return this.dir != Dir.INPUT; 
	}
	@Override
	public Dir getDir() {
		return dir;
	}

}
