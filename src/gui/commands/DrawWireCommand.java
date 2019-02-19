package gui.commands;

import gui.AbstractVertex;
import gui.BlockGE;
import gui.Grid;
import gui.TerminalGE;
import gui.Vertex;
import gui.WireGE;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DrawWireCommand implements Command {

	private WireGE wire;
	private AbstractVertex start;
	private Vertex end;
	private ArrayList<BlockGE> blocks;
	private ArrayList<WireGE> wires;

	public DrawWireCommand(WireGE wire, AbstractVertex start, ArrayList<WireGE> wires, ArrayList<BlockGE> blocks, MouseEvent e) {
		this.wires = wires;
		this.blocks = blocks;
		this.wire = wire;
		this.start = start;
		int x = Grid.toGridPoint(e.getX());
		int y = Grid.toGridPoint(e.getY());
		this.end = new Vertex(x, y);
		wire.addVertex(end, start);
	}

	public void onDrag(MouseEvent e) {
		assert end.getVertex() != null;
		int x = Grid.toGridPoint(e.getX());
		int y = Grid.toGridPoint(e.getY());
		end.getVertex().moveTo(x,y);
	}

	@Override
	public void onRelease(MouseEvent e) {
		if (end.getEquivalentVertex().hasSameLocation(start.getEquivalentVertex())) {
			boolean wantsToBeDeleted = wire.deleteVertex(end);
			if (wantsToBeDeleted) wires.remove(wire);
		} else {
			for (BlockGE b : blocks) {
				TerminalGE t = b.getTerminalForXY(e.getX(), e.getY());
				if (t != null) {
					end.deselect();
					wire.replaceVertex(end, t);
				}
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
