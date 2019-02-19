package gui.commands;

import gui.AbstractVertex;
import gui.BlockGE;
import gui.Grid;
import gui.TerminalGE;
import gui.WireGE;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MoveVertexCommand implements Command {
	
	private WireGE wire;
	private AbstractVertex v;
	private ArrayList<BlockGE> blocks;

	public MoveVertexCommand(WireGE wire, AbstractVertex v, ArrayList<BlockGE> blocks) {
		this.wire = wire;
		this.v = v;
		this.blocks = blocks;
	}

	@Override
	public void onDrag(MouseEvent e) {
		assert v.isTrueVertex();
		int x = Grid.toGridPoint(e.getX());
		int y = Grid.toGridPoint(e.getY());
		v.getVertex().moveTo(x,y);
	}

	@Override
	public void onRelease(MouseEvent e) {
		for (BlockGE b : blocks) {
			TerminalGE t = b.getTerminalForXY(e.getX(),e.getY());
			if (t != null) {
				v.getVertex().deselect();
				wire.replaceVertex(v, t);
			}
		}
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
