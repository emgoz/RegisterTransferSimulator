package symbols;

import simulationEngine.Signal;

public class Constant extends Symbol {
	private int value;
	
	public Constant() {
	}
	public Constant(int value) {
		this.value = value;
	}
	@Override
	public Signal getSignal() {
		return null;
	}
	@Override
	public boolean writeable() {
		return false;
	}
	@Override
	public Dir getDir() {
		return Dir.NOT_A_SIGNAL;
	}
}
