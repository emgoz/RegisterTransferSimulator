package gui;

import java.awt.Point;
import java.util.ArrayList;

public class Selection {
	private ArrayList<Selectable> components = new ArrayList<>();
	private Point[] origLocation;	//in Grid coords
	
	public void add(Selectable sc) {
		if (!components.contains(sc)) {
			components.add(sc);
			sc.select(this);
		}
	}
	public void remove(Selectable sc) {
		components.remove(sc);
		sc.deselect();
	}
	public void clear() {
		for (Selectable s : components) {
			s.deselect();
		}
		components.clear();
	}
	
	public void initMovement() {
		origLocation = new Point[components.size()];
		for (int i = 0; i < components.size(); i++) {
			origLocation[i] = components.get(i).getLocation();
		}
	}
	
	public void moveAll(int deltaX, int deltaY) {
		for (int i = 0; i < components.size(); i++) {
			components.get(i).moveTo(origLocation[i].x + deltaX, origLocation[i].y + deltaY);
		}
	}
	
	public int getSelectionCount() {
		return components.size();
	}
	public boolean contains(Selectable s) {
		return components.contains(s);
	}
	public String toString() {
		return components.toString();
	}
}
