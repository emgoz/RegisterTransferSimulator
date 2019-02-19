package simulationEngine;

import java.util.ArrayList;

public class Clock {
	private ArrayList<Registered> registeredBlocks = new ArrayList<>();
	
	public void addRegisteredBlock(Registered r) {
		registeredBlocks.add(r);
	}
	public ArrayList<Registered> GetBlocksToUpdate() {
		return registeredBlocks;
	}
	
}
