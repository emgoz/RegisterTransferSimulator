package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

public class Vertex extends AbstractVertex implements Selectable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int clickRad = 4;
	private int x,y;
	private transient Selection selection = null;
	
	public Vertex(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Vertex getVertex() {
		return this;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void draw(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D)g;
		int px = Grid.toPixelPoint(x);
		int py = Grid.toPixelPoint(y);
		g2d.fillRect(px-clickRad, py-clickRad, 2*clickRad+1, 2*clickRad+1);
	}
	public void drawSmall(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D)g;
		int px = Grid.toPixelPoint(x);
		int py = Grid.toPixelPoint(y);
		int circleRad = Grid.getSize()/8+1;
		g2d.fillOval(px-circleRad, py-circleRad, 2*circleRad+1, 2*circleRad+1);
	}
	public void translate(int dx, int dy) {
		x += dx;
		y += dy;
	}
	public boolean contains(int x, int y) {
		return x >= Grid.toPixelPoint(this.x)-clickRad && x <= Grid.toPixelPoint(this.x)+clickRad && y >= Grid.toPixelPoint(this.y)-clickRad && y <= Grid.toPixelPoint(this.y)+clickRad;
	}
	
	@Override
	public String toString() {
		return "("+x+", "+y+")";
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

	public Point getLocation() {
		return new Point(x,y);
	}

	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isTrueVertex() {

		return true;
	}
}
