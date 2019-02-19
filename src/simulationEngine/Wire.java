package simulationEngine;

import java.util.HashSet;

public class Wire {
	private int width;
	private HashSet<Terminal> connections = new HashSet<>();
	private Terminal source;
	
	private boolean err = false;
	
	public boolean hasError() {
		return err;
	}
	
	public void addTerminal(Terminal t) {
		assert t != null;
		connections.add(t);
	}
	public boolean removeTerminal(Terminal t) {
		return connections.remove(t);
	}
	public int getWidth() {
		return width;
	}
	public Signal getValue() {
		if (source == null) return null;
		return source.getSignal();
	}
	/*public String getError() {
		return error;
	}*/
	public void checkTypesAndFindSource() throws Exception {
		source = null;
		width = 0;
		err = false;
		for (Terminal t : connections) {
			if (t.isOutput()) {
				if (source == null) {
					source = t;
				} else {
					err = true;
					throw new LogicException("Wire has multiple signal sources.");
				}
			} 
			if (width == 0) {
				width = t.getSignal().getWidth();
			} else {
				if (width != t.getSignal().getWidth()) {
					err = true;
					throw new LogicException("Signal width missmatch on wire.");
				}
			}
		}
		if (source == null) {
			err = true;
			throw new LogicException("No signal source for wire.");
		}
		
	}
	/**
	 * @return Set of all blocks that need an update due to changed signals
	 */
	public HashSet<Block> propagateSignal() {
		HashSet<Block> changed = new HashSet<>();
		for (Terminal t : connections) {
			if (!t.isOutput()) {
				boolean update = t.updateValue(source.getSignal());
				if (update) {
					changed.add(t.getBelongsTo());
				}
			}
		}
		return changed;
	}
}
