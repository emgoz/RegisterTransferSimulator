package gui;

import gui.commands.Command;
import gui.commands.CreateWireCommand;
import gui.commands.MoveBlockCommand;
import gui.commands.MoveTerminalCommand;
import gui.commands.MoveWireSignalDisplayCommand;
import gui.commands.ResizeBlockCommand;
import gui.commands.ResizeBlockCommand.Corner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.SwingUtilities;

import simulationEngine.Block;

public abstract class BlockGE implements Selectable, Serializable {
	private static final long serialVersionUID = 1L;

	protected ArrayList<TerminalGE> terminals = new ArrayList<>();
	private ArrayList<Vertex> cornerVertices = new ArrayList<>(4);
	private Point topLeft, bottomRight;
	private transient Selection selection = null;
	private ArrayList<WireGE> wires;
	protected String label = "";
	
	public enum Shape {
		RECT("Rectangle"), ALU_N("ALU facing north"), ALU_E("ALU facing east"), ALU_S("ALU facing south"), ALU_W("ALU facing west"), 
		MUX_N("MUX facing north"), MUX_E("MUX facing east"), MUX_S("MUX facing south"), MUX_W("MUX facing west");
		public String s;
		Shape(String s) {
			this.s = s;
		}
		public String toString() {
			return s;
		}
	}
	protected Shape shape = Shape.RECT;
	
	
	public BlockGE(int px, int py, ArrayList<WireGE> wires) {
		int x = Grid.toGridPoint(px);
		int y = Grid.toGridPoint(py);
		this.wires = wires;
		topLeft = new Point(x,y);
		bottomRight = new Point(x + 6,y + 6);
		updateCorners();	
	}
	
	public abstract void openDialog(Canvas c);
	public abstract Block createModelBlock() throws Exception;
	public abstract Block getBlock();
	public abstract HashSet<String> getUsedClockPhaseNames();
	
	public void setCornersClip(int xr1, int yr1, int xr2, int yr2) {
		
		//initial max movement
		int x1 = topLeft.x;
		int y1 = topLeft.y;
		int x2 = bottomRight.x;
		int y2 = bottomRight.y;
		
		int dx1 = x2-x1 - 2;
		int dx2 = x2-x1 - 2;
		int dy1 = y2-y1 - 2;
		int dy2 = y2-y1 - 2;
		
		for (TerminalGE t : terminals) {
			if (t.isFacing(0) || t.isFacing(2)) {  //horizontal bounds
				if (t.getX() > x2 - dx2 - 1) dx2 = x2 - t.getX() - 1;
				if (t.getX() < x1 + dx1 + 1) dx1 = t.getX() - x1 - 1;
			} else {  //vertical bounds
				if (t.getY() > y2 - dy2 - 1) dy2 = y2 - t.getY() - 1;
				if (t.getY() < y1 + dy1 + 1) dy1 = t.getY() - y1 - 1;
			}
		}
		if (xr2 < x2 - dx2) xr2 = x2 - dx2;
		if (xr1 > x1 + dx1) xr1 = x1 + dx1;
		if (yr2 < y2 - dy2) yr2 = y2 - dy2;
		if (yr1 > y1 + dy1) yr1 = y1 + dy1;
		setCorners(xr1, yr1, xr2, yr2);
		for (TerminalGE t : terminals) {
			t.snapBack();
		}
	}


	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (getBlock() != null && getBlock().hasError()) {
			g2d.setColor(Color.RED);
		} else if (isSelected()) {
			g2d.setColor(new Color(220, 220, 250));
		} else {
			g2d.setColor(new Color(220, 220, 220));
		}

		int x1 = Grid.toPixelPoint(getX1());
		int x2 = Grid.toPixelPoint(getX2());
		int y1 = Grid.toPixelPoint(getY1());
		int y2 = Grid.toPixelPoint(getY2());
		int x[] = null;
		int y[] = null;
		int y_mxi = (y2-y1)/12;
		int x_mxi = (x2-x1)/12;
		int y_ali = (y2-y1)/8;
		int x_ali = (x2-x1)/8;
		int y_m = (y1+y2)/2;
		int x_m = (x1+x2)/2;
		
		if (shape == null) shape = Shape.RECT;
		switch (shape) {
		case ALU_E:
			x = new int[]{x1,x2,x2,x1, x1, x1+2*x_ali, x1};
			y = new int[]{y1-y_ali, y1+y_ali,y2-y_ali,y2+y_ali, y_m+y_ali, y_m, y_m-y_ali};
			break;
		case ALU_N:
			x = new int[]{x1+x_ali, x2-x_ali, x2+x_ali, x_m+x_ali, x_m, x_m-x_ali, x1-x_ali};
			y = new int[]{y1,y1,y2,y2,y2-2*y_ali,y2,y2};
			break;
		case ALU_S:
			x = new int[]{x1-x_ali, x_m-x_ali, x_m, x_m+x_ali, x2+x_ali, x2-x_ali, x1+x_ali};
			y = new int[]{y1,y1,y1+2*y_ali,y1,y1,y2,y2};
			break;
		case ALU_W:
			x = new int[]{x1,x2, x2, x2-2*x_ali, x2 ,x2,x1};
			y = new int[]{y1+y_ali, y1-y_ali, y_m-y_ali, y_m, y_m+y_ali, y2+y_ali, y2-y_ali};
			break;
		case MUX_E:
			x = new int[]{x1,x2,x2,x1};
			y = new int[]{y1-y_mxi,y1+y_mxi,y2-y_mxi,y2+y_mxi};
			break;
		case MUX_N:
			x = new int[]{x1+x_mxi,x2-x_mxi,x2+x_mxi,x1-x_mxi};
			y = new int[]{y1,y1,y2,y2};
			break;
		case MUX_S:
			x = new int[]{x1-x_mxi,x2+x_mxi,x2-x_mxi,x1+x_mxi};
			y = new int[]{y1,y1,y2,y2};
			break;
		case MUX_W:
			x = new int[]{x1,x2,x2,x1};
			y = new int[]{y1+y_mxi,y1-y_mxi,y2+y_mxi,y2-y_mxi};
			break;
		case RECT:
			x = new int[]{x1,x2,x2,x1};
			y = new int[]{y1,y1,y2,y2};
			break;
		default:
			break;
		}
		Polygon p = new Polygon(x,y,x.length);
		/*
		g2d.fillRect(Grid.toPixelPoint(getX1()), Grid.toPixelPoint(getY1()), Grid.toPixelPoint(getWidth()), Grid.toPixelPoint(getHeight()));
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(Grid.getSize()/8+1));
		g2d.drawRect(Grid.toPixelPoint(getX1()), Grid.toPixelPoint(getY1()), Grid.toPixelPoint(getWidth()), Grid.toPixelPoint(getHeight()));
		*/
		g2d.fillPolygon(p);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(Grid.getSize()/8+1));
		g2d.drawPolygon(p);
		
		for (TerminalGE t : terminals) {
			t.draw(g);
		}
			
		if (isSelected()) {
			for (Vertex v : cornerVertices) {
				v.draw(g);
			}
		}
		String l = label;
		Block b = getBlock();
		if (b != null) {
			l = b.getLabel();
		}
		if (l != null) {
			Font font = new Font("Courier", Font.BOLD, Grid.getSize());
			FontMetrics metrics = g.getFontMetrics(font);
		    int tx =  Grid.toPixelPoint(getX1()) + ( Grid.toPixelPoint(getWidth()) - metrics.stringWidth(l)) / 2;
		    int ty =  Grid.toPixelPoint(getY1()) + (( Grid.toPixelPoint(getHeight())/2+Grid.getSize() - metrics.getHeight()) / 2) + metrics.getAscent();
		    g.setFont(font);
		    g.drawString(l, tx, ty);
		}
	}
	public boolean contains(int x, int y) {
		return (x >=  Grid.toPixelPoint(topLeft.x) && x <=  Grid.toPixelPoint(bottomRight.x) && y >=  Grid.toPixelPoint(topLeft.y) && y <=  Grid.toPixelPoint(bottomRight.y));
	}
	
	public void select(Selection s) {
		selection = s;
	}

	public void deselect() {
		selection = null;
	}

	public boolean isSelected() {
		return selection != null;
	}
	public void addTerminal(TerminalGE t) {
		int y = getY1()+1;
		int x = t.isOutput() ? getX2() : getX1();
		for (TerminalGE other : terminals) {
			if (other.getAttX() == x && other.getAttY() >= y) {
				y = other.getAttY()+2;
			}
		}
		if (y >= getY2()-1) {
			setCorners(getX1(), getY1(), getX2(), y+1);
		}
		t.moveTo(x, y);
		terminals.add(t);
	}
	
	public void deleteTerminal(TerminalGE t) {
		for (WireGE w : t.getConnectedWires()) {
			boolean wantsToBeDeleted = w.deleteVertex(t);
			if (wantsToBeDeleted) {
				wires.remove(w);
			}
		}
		terminals.remove(t);
	}
	/**
	 * All params in grid coordinates
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void setCorners(int x1, int y1, int x2, int y2) {
		topLeft		= new Point(x1, y1);
		bottomRight = new Point(x2, y2);
		updateCorners();
	}
	
	public Rectangle getBoundsRect() {
		return new Rectangle( Grid.toPixelPoint(getX1()),  Grid.toPixelPoint(getY1()),  Grid.toPixelPoint(getWidth()),  Grid.toPixelPoint(getHeight()));
	}

	public void moveTo(int x, int y) {
		
		int deltaX = x-getX1(), deltaY = y-getY1();
		topLeft.x += deltaX;
		topLeft.y += deltaY;
		bottomRight.x += deltaX;
		bottomRight.y += deltaY;
		
		for (TerminalGE t : terminals) {
			t.moveTo(t.getAttX()+deltaX, t.getAttY()+deltaY);
		}
		updateCorners();
	}
	public Point getLocation() {
		return topLeft.getLocation();
	}
	
	public int getX1() {
		return topLeft.x;
	}
	public int getY1() {
		return topLeft.y;
	}
	public int getX2() {
		return bottomRight.x;
	}
	public int getY2() {
		return bottomRight.y;
	}
	public int getWidth() {
		return bottomRight.x-topLeft.x;
	}
	public int getHeight() {
		return bottomRight.y-topLeft.y;
	}
	private void updateCorners() {
		cornerVertices.clear();
		cornerVertices.add(new Vertex(getX1(), getY1()));
		cornerVertices.add(new Vertex(getX2(), getY1()));
		cornerVertices.add(new Vertex(getX1(), getY2()));
		cornerVertices.add(new Vertex(getX2(), getY2()));	
	}
	public TerminalGE getTerminalForXY(int x, int y) {
		for (TerminalGE t : terminals) {
			if (t.contains(x, y)) return t;
		}
		return null;
	}
	public ArrayList<TerminalGE> getTerminals() {
		return terminals;
	}
	
	public Command getCommand(ArrayList<BlockGE> blocks, MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
				
		for (TerminalGE t : terminals) {
			WireSignalDisplay d = t.getDisplay();
			if (d != null && d.contains(x, y)) {
				return new MoveWireSignalDisplayCommand(d);
			}
		}
		if (isSelected()) {
			if (cornerVertices.get(0).contains(x, y)) return new ResizeBlockCommand(this, Corner.TL);
			if (cornerVertices.get(1).contains(x, y)) return new ResizeBlockCommand(this, Corner.TR);
			if (cornerVertices.get(2).contains(x, y)) return new ResizeBlockCommand(this, Corner.BL);
			if (cornerVertices.get(3).contains(x, y)) return new ResizeBlockCommand(this, Corner.BR);
			for (TerminalGE t : terminals) {
				if (t.contains(x, y)) return new MoveTerminalCommand(t);
			}
		} else {
			for (TerminalGE t : terminals) {
				if (t.contains(x, y)) {
					return new CreateWireCommand(t, wires, blocks, e);
				}
			}
		}
		if (this.contains(x, y)) return new MoveBlockCommand(this, e);
		
		return null;
	}
	public void deleteConnections() {
		//System.out.println("Delete Connections");
		for (TerminalGE t : terminals) {
			ArrayList<WireGE> wiresToDelete = new ArrayList<>(t.getConnectedWires());
			for (WireGE w : wiresToDelete) {
				//System.out.println("Delete Request: "+t);
				boolean wantsToBeDeleted = w.deleteVertex(t);
				if (wantsToBeDeleted) {
					wires.remove(w);
					//System.out.println("removed wire");
				} else {
					//System.out.println("no remove wire");
				}
			}
		}
	}

}
