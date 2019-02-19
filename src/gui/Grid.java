package gui;

import java.awt.Point;

public class Grid {
	private static int grid = 16;
	
	public static void main(String args[]) {
	//	System.out.println(toGrid(8));
	//	System.out.println(toGrid(-8));
	//	System.out.println(toGrid(-9));
	}
/*
	public static int toGrid(int x) {
		if (x>=0) return (x+grid/2)/grid*grid;
		else return (x-grid/2+1)/grid*grid;
	}
	*/
	
	public static int toGridPoint(int x) {
		if (x>=0) return (x+grid/2)/grid;
		else return (x-grid/2+1)/grid;
	}
	public static int toPixelPoint(int x) {
		return x*grid;
	}
	public static Point toGridPoint(Point p) {
		return new Point(toGridPoint(p.x),toGridPoint(p.y));
	}
	public static Point toPixelPoint(Point p) {
		return new Point(toPixelPoint(p.x),toPixelPoint(p.y));
	}
	
	public static int getSize(){
		return grid;
	}
	public static void setSize(int g) {
		grid = g;
	}

}
