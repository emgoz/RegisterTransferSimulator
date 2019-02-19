package gui.commands;

import gui.Grid;
import gui.WireSignalDisplay;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class MoveWireSignalDisplayCommand implements Command {
	
	private WireSignalDisplay d;
	
	public MoveWireSignalDisplayCommand(WireSignalDisplay d) {
		this.d = d;
	}
	@Override
	public void onDrag(MouseEvent e) {

		d.moveToPixel(e.getX(),e.getY());
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
