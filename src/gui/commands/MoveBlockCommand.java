package gui.commands;

import gui.BlockGE;
import gui.Grid;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class MoveBlockCommand implements Command {

	private BlockGE block;
	private int relativeX, relativeY;
	
	public MoveBlockCommand(BlockGE block, MouseEvent e) {
		this.block = block;
		relativeX = e.getX() - Grid.toPixelPoint(block.getX1());
		relativeY = e.getY() - Grid.toPixelPoint(block.getY1());
	}
	
	@Override
	public void onDrag(MouseEvent e) {
		int x = Grid.toGridPoint(e.getX()-relativeX);
		int y = Grid.toGridPoint(e.getY()-relativeY);
		block.moveTo(x,y);
	}

	@Override
	public void onRelease(MouseEvent e) {
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

	@Override
	public void draw(Graphics g) {
		//nothing to do here
	}

}
