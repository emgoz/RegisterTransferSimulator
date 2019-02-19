package symbols;

import simulationEngine.Signal;

public abstract class Symbol {
	public enum Dir {INPUT, OUTPUT, INTERNAL, NOT_A_SIGNAL};
	
	
	public abstract Signal getSignal();
	public abstract Dir getDir();
	public abstract boolean writeable();
}
