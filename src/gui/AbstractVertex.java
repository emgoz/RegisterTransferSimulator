package gui;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.HashSet;

import simulationEngine.Signal;

public abstract class AbstractVertex implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected HashSet<WireGE> wires = new HashSet<>();
	protected WireSignalDisplay disp = null;
	
	public void setDisplay(WireSignalDisplay d) {
		disp = d;
	}
	public WireSignalDisplay getDisplay() {
		return disp;
	}
	
	public boolean hasConnectedWires() {
		return !wires.isEmpty();
	}
	public HashSet<WireGE> getConnectedWires () {
		return wires;
	}
	public void attach(WireGE w) {
		wires.add(w);
	}
	public void detach(WireGE w) {
		wires.remove(w);
	}
	public Signal getSignal() {
		if (hasConnectedWires()) {
			return wires.iterator().next().getSignal();
		} else {
			System.out.println("No wires registered...");
			return null;
		}
	}
	
	public int distanceTo(AbstractVertex other) {
		int dx = this.getX()-other.getX();
		int dy = this.getY()-other.getY();
		return (int)Math.sqrt(dx*dx+dy*dy);
	}
	public boolean hasSameLocation(AbstractVertex o) {
		return o.getX() == this.getX() && o.getY() == this.getY();
	}
	
	public abstract boolean isTrueVertex();
	public abstract Vertex getVertex();
	public Vertex getEquivalentVertex() {
		return new Vertex(getX(), getY());
	}

	public abstract int getX();
	public abstract int getY();
	public abstract boolean contains(int x, int y);
	public abstract boolean isSelected();
	
	public void draw(Graphics g) {
		if (disp != null) {
			disp.draw(g);
		}
	}
}
