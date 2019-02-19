package simulationEngine;

import java.util.ArrayList;

public class Model {
	ArrayList<Block> blocks = new ArrayList<>();
	ArrayList<Wire> wires = new ArrayList<>();
	ArrayList<Observer> observers = new ArrayList<>();
	ClockManager clocks = new ClockManager();

	public void clear() {
		blocks.clear();
		wires.clear();
		observers.clear();
		clocks.clear();
	}
	public void registerObserver(Observer o) {
		observers.add(o);
	}
	public void notifyObservers() {
		for (Observer o : observers) o.refresh();
	}

	public void addBlock(Block b) {
		blocks.add(b);
	}
	public void addClockPhase(String name) throws Exception {
		clocks.addClockPhase(name);
	}
	public void registerBlock(Registered b, String clockPhaseName) throws Exception {
		Clock c = clocks.getClockByName(clockPhaseName);
		if (c == null) {
			throw new LogicException("Clock phase '"+clockPhaseName+"' undefined.");
		} else {
			c.addRegisteredBlock(b);
		}
	}

	public void addWire(Wire w) {
		wires.add(w);
	}

	public void initSimulation() throws Exception {
		for (Block b : blocks) {
			b.initialize();
		}
		for (Wire w : wires) {
			w.checkTypesAndFindSource();
		}
		propagateSignals();
		notifyObservers();
	}
	public void propagateSignals() throws Exception {
		int MaxLoop = 10;
		// Do propagation & combinatorial
		for (int i = 0; i < MaxLoop; i++) {
			ArrayList<Block> blocksToUpdate = new ArrayList<>();
			for (Wire w : wires) {
				blocksToUpdate.addAll(w.propagateSignal());
			}
			if (blocksToUpdate.isEmpty()) {
				break;
			} else {
				for (Block b : blocksToUpdate)
					b.calculate();
			}
		}
	}
	public void singleStep() throws Exception {
		// Clock
		ArrayList<Registered> rblocksToUpdate = clocks.getNextClock().GetBlocksToUpdate();
		for (Registered b : rblocksToUpdate) {
			b.updateRegister();
			//System.out.println(b);
		}
		propagateSignals();
		notifyObservers();
	}
	public void cycle() throws Exception {
		do {
			singleStep();
		} while (clocks.getNextClockIndex() > 0);
	}

	public void simulate(int clkCount) throws Exception {
		initSimulation();
		for (int t = 0; t < clkCount; t++) {
			singleStep();
		}
		System.out.println("Done.");
	}

}
