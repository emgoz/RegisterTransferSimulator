package symbols;

import simulationEngine.Signal;

public class LabelStr extends Symbol {

	private String text;
	

	public LabelStr(String text) {
		this.text = text;
	}
	public String toString() {
		return this.text;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public Signal getSignal() {
		assert false;
		return null;
	}

	@Override
	public boolean writeable() {
		return true;
	}
	@Override
	public Dir getDir() {
		return Dir.NOT_A_SIGNAL;
	}

}
