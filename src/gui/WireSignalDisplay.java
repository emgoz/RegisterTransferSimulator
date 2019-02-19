package gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import simulationEngine.Signal;

public class WireSignalDisplay implements Serializable {
	private static final long serialVersionUID = 1L;
	private int rx, ry;  //relative grid coordinates to Abstract Vertex
	private int w = 10,h = 10;
	private AbstractVertex vertex;
	private int base = 16;
	
	public WireSignalDisplay(AbstractVertex v) {
		rx = 0;
		ry = 0;
		vertex = v;
	}
	public void moveToPixel(int x, int y) {
		rx = Grid.toGridPoint(x-w/2)-vertex.getX();
		ry = Grid.toGridPoint(y-h/2)-vertex.getY();	
	}
	public boolean contains(int x, int y) {
		
		int x1 = Grid.toPixelPoint(vertex.getX()+rx);
		int y1 = Grid.toPixelPoint(vertex.getY()+ry);
	
		
		return (new Rectangle2D.Double(x1+4, y1, w, h)).contains(x,y);
	}
	
	public void draw(Graphics g) {
		String l = "?";
		Signal s = vertex.getSignal();
		if (s != null) {
			l = s.getString(base);
		}
		int x = Grid.toPixelPoint(vertex.getX()+rx);
		int y = Grid.toPixelPoint(vertex.getY()+ry);
		Font font = new Font("Courier", Font.BOLD, Grid.getSize());
		FontMetrics metrics = g.getFontMetrics(font);
		w = metrics.stringWidth(l);
		h = metrics.getHeight();
	    g.setFont(font);
	    g.drawString(l, x+4, y+h);
	}
	
}
