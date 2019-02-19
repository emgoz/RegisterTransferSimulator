package gui.commands;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public interface Command {
	
	public void onDrag(MouseEvent e);
	public void onRelease(MouseEvent e);
	public void draw(Graphics g);
	
	public void execute();
	public void undo();
}
