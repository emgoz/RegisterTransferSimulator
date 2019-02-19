package gui.commands;

import gui.Grid;
import gui.TerminalGE;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class MoveTerminalCommand implements Command {
	
	private TerminalGE terminal;
	
	public MoveTerminalCommand(TerminalGE terminal) {
		this.terminal = terminal;
	}
	
	@Override
	public void onDrag(MouseEvent e) {
		int x = Grid.toGridPoint(e.getX());
		int y = Grid.toGridPoint(e.getY());
		terminal.moveTo(x,y);
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
