package gui.commands;

import gui.Grid;
import gui.Selection;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class MoveSelectionCommand implements Command {
	
	private Selection selection;
	private Point moveStart;
	
	public MoveSelectionCommand(Selection selection, MouseEvent e) {
		this.moveStart = e.getPoint();
		this.selection = selection;
		selection.initMovement();
	}
	
	@Override
	public void onDrag(MouseEvent e) {
		int deltaX = Grid.toGridPoint(e.getX()-moveStart.x);
		int deltaY = Grid.toGridPoint(e.getY()-moveStart.y);
		selection.moveAll(deltaX, deltaY);
	}

	@Override
	public void onRelease(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}

}
