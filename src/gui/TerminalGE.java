package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class TerminalGE extends AbstractVertex implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int facing = 0;
	private BlockGE parentBlock;
	private int attX, attY;   // coordinates where attached to Block
	private String name;
	private String initialValue = "0";

	private boolean isOutput;
	private int width;

	public TerminalGE(int width, boolean isOutput, String name, BlockGE parentBlock) {		
		this.width = width;
		this.isOutput = isOutput;
		this.name = name;
		this.parentBlock = parentBlock;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String s) {
		this.name = s;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public boolean isOutput() {
		return isOutput;
	}
	public BlockGE getParentBlock() {
		return parentBlock;
	}
	public boolean isFacing(int d) {
		return facing == d;
	}
	public String getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	
	public Vertex getVertex() {
		return null;
	}
	public boolean isSelected() {
		return parentBlock.isSelected();
	}

	public boolean contains(int x, int y) {
		int gd = Grid.getSize();
		int attXpx = Grid.toPixelPoint(attX);
		int attYpx = Grid.toPixelPoint(attY);
		switch(facing) {
		case 0:  return x >= attXpx-gd/2 && x <= attXpx+gd/2 && y >= attYpx-gd && y <= attYpx;
		case 1:  return x >= attXpx && x <= attXpx+gd && y >= attYpx-gd/2 && y <= attYpx+gd/2;
		case 2:  return x >= attXpx-gd/2 && x <= attXpx+gd/2 && y >= attYpx && y <= attYpx+gd;
		case 3:  return x >= attXpx-gd && x <= attXpx && y >= attYpx-gd/2 && y <= attYpx+gd/2;
		default: return false;
		}
	}
	
	public void moveTo(int xr, int yr) {
		attX = xr;
		attY = yr;
		if (attX <= parentBlock.getX1()) { facing = 3; attX = parentBlock.getX1(); }
		if (attY <= parentBlock.getY1()) { facing = 0; attY = parentBlock.getY1(); }
		if (attX >= parentBlock.getX2()) { facing = 1; attX = parentBlock.getX2(); }
		if (attY >= parentBlock.getY2()) { facing = 2; attY = parentBlock.getY2(); }
		snapBack();
	}
	public boolean isTrueVertex() {
		return false;
	}
	/**
	 * Moves the terminal back to the side of the parent block
	 */
	public void snapBack() {
		switch (facing) {
		case 0:
			attY = parentBlock.getY1();
			attX = Math.min(Math.max(parentBlock.getX1()+1, attX), parentBlock.getX2()-1);
			break;
		case 1:
			attX = parentBlock.getX2();
			attY = Math.min(Math.max(parentBlock.getY1()+1, attY), parentBlock.getY2()-1);
			break;
		case 2:
			attY = parentBlock.getY2();
			attX = Math.min(Math.max(parentBlock.getX1()+1, attX), parentBlock.getX2()-1);
			break;
		case 3:
			attX = parentBlock.getX1();
			attY = Math.min(Math.max(parentBlock.getY1()+1, attY), parentBlock.getY2()-1);
			break;
		}
	}
	/**
	 * 
	 * @return the x position of the point where to attach a wire in grid coordinates
	 */
	public int getX() {
		if (facing == 1) return attX + 1;
		if (facing == 3) return attX - 1;
		return attX;	
	}
	/**
	 * 
	 * @return the y position of the point where to attach a wire in grid coordinates
	 */
	public int getY() {
		if (facing == 0) return attY - 1;
		if (facing == 2) return attY + 1;
		return attY;	
	}
	
	public int getAttX() {
		return attX;
	}
	
	public int getAttY() {
		return attY;
	}
	
	
	public void draw(Graphics g) {
		super.draw(g);
		
		int ax = Grid.toPixelPoint(getAttX());
		int ay = Grid.toPixelPoint(getAttY());
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLACK);
		int gd = Grid.getSize();
		
		AffineTransform orig = g2.getTransform();
		
		Font font = new Font("Courier", Font.PLAIN, gd);
		int len = g2.getFontMetrics(font).stringWidth(this.name);
		
		switch (facing) {
		case 0:
			g2.translate(ax+gd/3, ay+len+gd/3);
			g2.rotate(-Math.PI/2);
			break;
		case 1:
			g2.translate(ax-len-gd/3, ay+gd/3);
			break;
		case 2:
			g2.translate(ax+gd/3, ay-gd/3);
			g2.rotate(-Math.PI/2);
			break;
		case 3:
			g2.translate(ax+gd/3, ay+gd/3);
			break;
		}
		
		
		g2.setFont(font);
		
		g2.drawString(this.name, 0, 0);
		g2.setTransform(orig);
		
		
		//if input, the peak of the triangle is the attachment point
		int x1 = ax;
		int y1 = ay;
		
		int x[] = new int[3], y[] = new int[3];
		
		int f = facing;
		
		//if output, shift the whole thing correspondingly
		if (isOutput){	
			f = (f+2)%4;
			switch (f) {
			case 0:
				y1 += gd;
				break;
			case 1:
				x1 -= gd;
				break;
			case 2:
				y1 -= gd;
				break;
			case 3:
				x1 += gd;
				break;
			}
			
		}
		x[1]=x1;
		y[1]=y1; 
		if (f % 2 == 0){  //North or south
			x[0]=x1-gd/2; x[2]=x1+gd/2;
			if (f == 0) {
				y[0]=y1-gd; y[2]=y1-gd;
			} else {
				y[0]=y1+gd; y[2]=y1+gd;
			}
		} else {
			y[0]=y1-gd/2; y[2]=y1+gd/2;
			if (f == 3) {
				x[0]=x1-gd; x[2]=x1-gd;
			} else {
				x[0]=x1+gd; x[2]=x1+gd;
			}
		}
		Polygon p = new Polygon(x, y, 3);
		if (hasConnectedWires()) {
			g2.setColor(Color.BLACK);
		} else {
			g2.setColor(new Color(190,50,50));
		}
		g2.fillPolygon(p);
		g2.setColor(Color.BLACK);
	};
	
	public String toString() {
		return attX + ", " + attY + "; facing = " + facing + "; output = " + isOutput();	
	}
}
