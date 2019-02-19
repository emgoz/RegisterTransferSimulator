package gui.commands;

import gui.BlockGE;
import gui.Grid;
import gui.Selection;
import gui.Vertex;
import gui.WireGE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DrawSelectionCommand implements Command {

	private ArrayList<BlockGE> blocks;
	private ArrayList<WireGE> wires;
	private Selection selection;
	private Point selectCorner1, selectCorner2;
	private Rectangle2D selectionRect;
	
	
	public DrawSelectionCommand(ArrayList<BlockGE> blocks, ArrayList<WireGE> wires, Selection selection, MouseEvent e) {
		this.blocks = blocks;
		this.wires = wires;
		this.selection = selection;
		this.selectCorner1 = e.getPoint();
		this.selectCorner2 = e.getPoint();
		this.selectionRect = new Rectangle2D.Double();
		if (e.isControlDown()) {
			// add to selection
		} else {
			selection.clear();
		}
	}
	
	
	public void checkRectangleContainment() {
		for (WireGE w : wires) {
			for (Vertex v : w.getAllVertices()) {
				if (selectionRect.contains(Grid.toPixelPoint(v.getLocation()))) {
					selection.add(v.getVertex());
				}
			}
		}
		for (BlockGE b : blocks) {
			if (selectionRect.contains(b.getBoundsRect())) {
				selection.add(b);
			}
		}
	}
	
	@Override
	public void onDrag(MouseEvent e) {
		selectCorner2 = e.getPoint();
		if (isPointTwoInQuadOne(selectCorner1, selectCorner2)) {
			selectionRect.setRect(selectCorner2.x, selectCorner2.y, selectCorner1.x - selectCorner2.x, selectCorner1.y - selectCorner2.y);
		} else {
			selectionRect.setRect(selectCorner1.x, selectCorner1.y, selectCorner2.x - selectCorner1.x, selectCorner2.y - selectCorner1.y);
		}
		checkRectangleContainment();
	}
	public boolean isPointTwoInQuadOne(Point p1, Point p2) {
		return p1.x >= p2.x && p1.y >= p2.y;
	}

	@Override
	public void onRelease(MouseEvent e) {
		
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		final Color CURSOR_COLOR = new Color(100, 100, 100, 100);
		if (selectionRect != null) {
			g2.setColor(CURSOR_COLOR);
			g2.fill(selectionRect);
		}
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
