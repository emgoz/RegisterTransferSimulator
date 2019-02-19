package simulationEngine;


public class Terminal {
	private Signal sig;
	private Block belongsTo;
	private boolean isOutput;
	
	public Terminal(int width, boolean isOutput) {
		this.belongsTo = null;
		this.sig = new Signal(width);
		this.isOutput = isOutput;
	}
	public Terminal(int width, boolean isOutput, Block belongsTo) {
		this.belongsTo = belongsTo;
		this.sig = new Signal(width);
		this.isOutput = isOutput;
	}

	public Signal getSignal() {
		return sig;
	}
	public void setSignal(Signal sig) {
		this.sig = sig;
	}
	public Block getBelongsTo(){
		return belongsTo;
	}
	public void setBelongsTo(Block b) {
		belongsTo = b;
	}
	public boolean updateValue(Signal value) {
		if (!sig.equals(value)) {
			try {
				sig.setSame(value);
			} catch (Exception e) {
				e.printStackTrace();
				assert false;  //TODO
			}
			return true;
		}
		return false;
	}
	public boolean isOutput() {
		return isOutput;
	}
}
