package gui;

import java.awt.Point;

public interface Selectable {
	
	public boolean isSelected();
	public void select(Selection s);
	public void deselect();
	public Point getLocation();
	public void moveTo(int x, int y);

}
