package simulationEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class ClockManager {
	private ArrayList<String> clockPhaseNames = new ArrayList<>();
	private HashMap<String, Clock> clockPhases = new HashMap<>();
	private int nextClockIndex = 0;
	
	public void clear() {
		clockPhaseNames.clear();
		clockPhases.clear();
	}
	public Clock getClockByName(String name) {
		return clockPhases.get(name);
	}
	
	public void addClockPhase(String name) throws Exception {
		if (clockPhaseNames.contains(name)) {
			throw new LogicException("Duplicate clock phase '"+name+"'");
		}
		clockPhaseNames.add(name);
		clockPhases.put(name, new Clock());
	}
	public Clock getNextClock() {
		Clock nextC = clockPhases.get(clockPhaseNames.get(nextClockIndex));
		nextClockIndex++;
		if (nextClockIndex >= clockPhaseNames.size()) nextClockIndex = 0;
		return nextC; 
	}
	
	public void init() {
		nextClockIndex = 0;
	}
	public int getNextClockIndex() {
		return nextClockIndex;
	}
}
