package simulationEngine;

import java.util.HashMap;

public abstract class Block {
	protected HashMap<String,Terminal> terminals = new HashMap<>();
	protected boolean err = false;
	
	public Terminal getTerminal(String id) throws LogicException {
		Terminal t = terminals.get(id);
		if (t == null){ 
			err = true;
			throw new LogicException("Terminal "+id+" not found.");
		}
		return t;
	}
	/**
	 * Calculates the logic function of the block
	 */
	public abstract void calculate() throws Exception;
	public abstract void initialize() throws Exception;
	public abstract String getLabel();
	public boolean hasError() {
		return err;
	}
	public void resetError() {
		err = false;
	}
}
