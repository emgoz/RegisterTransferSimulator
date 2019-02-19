package gui.commands;

import gui.BlockGE;
import gui.Grid;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class ResizeBlockCommand implements Command {
	
	private BlockGE block;
	private Corner corner;
	private int otherX, otherY;
	
	public enum Corner {
		TL, TR, BL, BR
	}
	
	public ResizeBlockCommand(BlockGE block, Corner corner) {
		this.block = block;
		this.corner = corner;
		switch (corner) {
		case TL:
			otherX = block.getX2();
			otherY = block.getY2();
			break;
		case TR:
			otherX = block.getX1();
			otherY = block.getY2();
			break;
		case BL:
			otherX = block.getX2();
			otherY = block.getY1();
			break;
		case BR:
			otherX = block.getX1();
			otherY = block.getY1();
			break;
		}
	}
	@Override
	public void onDrag(MouseEvent e) {
		int x = Grid.toGridPoint(e.getX());
		int y = Grid.toGridPoint(e.getY());
		switch (corner) {
		case TL:
			block.setCornersClip(x,y,otherX, otherY);
			break;
		case TR:
			block.setCornersClip(otherX,y,x, otherY);
			break;
		case BL:
			block.setCornersClip(x,otherY,otherX, y);
			break;
		case BR:
			block.setCornersClip(otherX, otherY,x,y);
			break;
		}
		
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
