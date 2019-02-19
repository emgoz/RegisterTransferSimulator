package gui;

import gui.commands.Command;
import gui.commands.DrawWireCommand;
import gui.commands.MoveVertexCommand;
import gui.commands.MoveWireSignalDisplayCommand;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import simulationEngine.Signal;
import simulationEngine.Wire;

public class WireGE implements Serializable {
	private static final long serialVersionUID = 1L;
	// private ArrayList<WireSection> sections = new ArrayList<>();

	private HashMap<AbstractVertex, LinkedList<AbstractVertex>> adjacencyMatrix = new HashMap<>();
	private HashSet<AbstractVertex> vertices = new HashSet<>(); 
																// redundant to
																// keyset of
																// adjacencyMatrix
	private transient Wire wire;
	
	public WireGE(AbstractVertex u, AbstractVertex v) {
		vertices.add(v);
		adjacencyMatrix.put(v, new LinkedList<>());
		vertices.add(u);
		adjacencyMatrix.put(u, new LinkedList<>());
		adjacencyMatrix.get(u).add(v);
		adjacencyMatrix.get(v).add(u);
		u.attach(this);
		v.attach(this);
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (this.wire != null && this.wire.hasError()) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.black);
		}
		for (AbstractVertex v : vertices) {
			if (v.getVertex() != null) {
				if (v.isSelected())
					v.getVertex().draw(g);
				else
					v.getVertex().drawSmall(g);
			}
			for (AbstractVertex u : adjacencyMatrix.get(v)) {
				g2d.setStroke(new BasicStroke(Grid.getSize()/8+1));
				g2d.drawLine(Grid.toPixelPoint(u.getX()), Grid.toPixelPoint(u.getY()), Grid.toPixelPoint(v.getX()), Grid.toPixelPoint(v.getY()));
				/*
				if (this.wire != null && this.wire.getValue() != null && u.distanceTo(v) > 4) {
					String l = this.wire.getValue().getHexString();
					int x = Grid.toPixelPoint((u.getX()+v.getX()))/2+4;
					int y = Grid.toPixelPoint((u.getY()+v.getY()))/2-4;
					Font font = new Font("Courier", Font.BOLD, Grid.getSize());
					//FontMetrics metrics = g.getFontMetrics(font);
				    //x = x - metrics.stringWidth(l) / 2;
				    //y = y - metrics.getHeight();
				    g.setFont(font);
				    g.drawString(l, x, y);
				}*/
			}
		}
	}
	public Signal getSignal() {
		if (this.wire != null && this.wire.getValue() != null) {
			return this.wire.getValue();
		}
		return null;
	}

	public void addVertex(AbstractVertex v, AbstractVertex connectedTo) {
		//System.out.println(vertices);

		assert vertices.contains(connectedTo);
		if (!vertices.contains(v)) {
			vertices.add(v);
			adjacencyMatrix.put(v, new LinkedList<>());
			v.attach(this);
		}
		adjacencyMatrix.get(v).add(connectedTo);
		adjacencyMatrix.get(connectedTo).add(v);
	}
		
	public boolean deleteVertex(AbstractVertex v) {
		return deleteVertexA(v, true);
	}

	private boolean deleteVertexA(AbstractVertex v, boolean removeLast) {
		if (vertices.contains(v)) {
			ArrayList<AbstractVertex> rejoinList = new ArrayList<>(2);
			for (AbstractVertex index : adjacencyMatrix.get(v)) {
				adjacencyMatrix.get(index).remove(v);
				if (rejoinList.size() < 2) {
					rejoinList.add(index);
				}
			}
			adjacencyMatrix.remove(v);
			vertices.remove(v);
			if (rejoinList.size() == 2) {
				AbstractVertex x = rejoinList.get(0);
				AbstractVertex y = rejoinList.get(1);
				adjacencyMatrix.get(x).add(y);
				adjacencyMatrix.get(y).add(x);
			}
			v.detach(this);
			if (isAlmostEmpty() && removeLast)
				deleteLast();
		}
		return isEmpty();
	}
	public void connect(AbstractVertex v, AbstractVertex u) {
		assert vertices.contains(u) && vertices.contains(v);
		if (v.hasSameLocation(u)) {
			//u will be removed an all its neighbors connected to v
			HashSet<AbstractVertex> neighbors = new HashSet<>(adjacencyMatrix.get(u));
			for (AbstractVertex index : adjacencyMatrix.get(u)) {
				adjacencyMatrix.get(index).remove(u);
			}
			for (AbstractVertex index : neighbors) {
				adjacencyMatrix.get(index).add(v);
				adjacencyMatrix.get(v).add(index);
			}
			adjacencyMatrix.remove(u);
			vertices.remove(u);
		} else {
			adjacencyMatrix.get(v).add(u);
			adjacencyMatrix.get(u).add(v);
		}
	}
	public void disconnect(AbstractVertex u, AbstractVertex v) {
		assert vertices.contains(u) && vertices.contains(v);
		adjacencyMatrix.get(u).remove(v);
		adjacencyMatrix.get(v).remove(u);
	}
	public boolean removeSection(int x, int y) {
		System.out.println("WireGE.removeSection() Todo!");
		return false;
	}
	public void addIntermediateVertex(int x, int y) {
		AbstractVertex v1 = null, v2 = null;
		for (AbstractVertex u : vertices) {
			for (AbstractVertex v : adjacencyMatrix.get(u)) {
				if (lineContains(u, v, x, y)) {
					v1 = v;
					v2 = u;
					break;
				}
			}
		}
		if (v1 != null) {
			Vertex newV = new Vertex(Grid.toGridPoint(x), Grid.toGridPoint(y));
			disconnect(v1, v2);
			addVertex(newV, v1);
			addVertex(newV, v2);
		}
	}

	public void replaceVertex(AbstractVertex oldV, AbstractVertex newV) {
		assert vertices.contains(oldV);
		LinkedList<AbstractVertex> connected = adjacencyMatrix.get(oldV);
		deleteVertexA(oldV, false);
		for (AbstractVertex u : connected) {
			addVertex(newV, u);
		}
	}
	private void addAllAndReplace(WireGE formerWire) {
		for (AbstractVertex v : formerWire.vertices) {
			if (!vertices.contains(v)) {
				vertices.add(v);
				adjacencyMatrix.put(v, new LinkedList<>());
				v.attach(this);
			}
			adjacencyMatrix.get(v).addAll(formerWire.adjacencyMatrix.get(v));
			v.detach(formerWire);
		}
	}
	
	public WireGE(WireGE w1, WireGE w2, AbstractVertex v1, AbstractVertex v2) {
		addAllAndReplace(w1);
		addAllAndReplace(w2);
		connect(v1, v2);
	}
	

	public boolean contains(int x, int y) {
		boolean c = false;
		for (AbstractVertex u : vertices) {
			for (AbstractVertex v : adjacencyMatrix.get(u)) {
				if (lineContains(u, v, x, y)) {
					c = true;
					break;
				}
			}
		}
		for (AbstractVertex v : vertices) {
			if (v.contains(x, y) && v.isSelected()) {
				c = true;
				break;
			}
		}
		return c;
	}
	public AbstractVertex getVertexAt(int x, int y) {
		AbstractVertex vt = null;
		for (AbstractVertex v : vertices) {
			if (v.getX() == x && v.getY() == y) {
				vt = v;
				break;
			}
		}
		return vt;
	}
	

	public boolean onSelectedSection(int x, int y) {
		for (AbstractVertex u : vertices) {
			for (AbstractVertex v : adjacencyMatrix.get(u)) {
				if (u.isSelected() && v.isSelected() && lineContains(u, v, x, y)) {
					return true;
				}
			}
		}
		return false;
	}

	public void selectAllVertices(Selection selection) {
		for (AbstractVertex v : vertices) {
			if (v.isTrueVertex())
				v.getVertex().select(selection);
		}
	}

	public ArrayList<Vertex> getAllVertices() {
		ArrayList<Vertex> r = new ArrayList<>();
		for (AbstractVertex v : vertices) {
			if (v.isTrueVertex())
				r.add(v.getVertex());
		}
		return r;
	}

	public Command getCommand(MouseEvent e, ArrayList<BlockGE> blocks, ArrayList<WireGE> wires) {
		int x = e.getX();
		int y = e.getY();
		for (AbstractVertex v : vertices) {
			if (v.getDisplay() != null && v.getDisplay().contains(x, y)) {
				return new MoveWireSignalDisplayCommand(v.getDisplay());
			}
			
			if (v.isTrueVertex() && v.contains(x, y)) {
				if (v.isSelected()) {
					return new MoveVertexCommand(this, v, blocks);
				} else {
					return new DrawWireCommand(this, v, wires, blocks, e);
				}
			}
		}
		return null;
	}

	public boolean deleteSelectedVertices() {
		HashSet<AbstractVertex> toDelete = new HashSet<>();
		for (AbstractVertex v : vertices) {
			if (v.isTrueVertex() && v.isSelected()) {
				toDelete.add(v);
			}
		}
		for (AbstractVertex v : toDelete) {
			deleteVertex(v);
		}
		return isEmpty();
	}
	public boolean disconnectIfAlmostEmpty() {
		if (isAlmostEmpty()) {
			deleteLast();
			return true;
		} else {
			return isEmpty();
		}
		
	}

	public boolean isEmpty() {
		return vertices.isEmpty();
	}

	public boolean isAlmostEmpty() {
		return vertices.size() == 1;
	}

	public void deleteLast() {
		assert vertices.size() == 1;
		AbstractVertex v = vertices.iterator().next();
		this.deleteVertex(v);
	}

	private boolean lineContains(AbstractVertex p1, AbstractVertex p2, int x, int y) {
		double threshold = 5d;
		int x1 = Grid.toPixelPoint(p1.getX()), y1 = Grid.toPixelPoint(p1.getY());
		int x2 = Grid.toPixelPoint(p2.getX()), y2 = Grid.toPixelPoint(p2.getY());

		// create coordinate form ax+by=c with a^2+b^2 = 1
		double a, b, c;
		if (x1 == x2) {
			a = 1;
			b = 0;
			c = x1;
		} else if (y1 == y2) {
			a = 0;
			b = 1;
			c = y1;
		} else {
			double m = (double) (y2 - y1) / (double) (x2 - x1); // = -a/b
			b = 1d / Math.sqrt(m * m + 1);
			a = -m * b;
			c = a * x1 + b * y1;
		}
		double d = Math.abs(a * x + b * y - c);
		return (d <= threshold && (new Rectangle2D.Double(Math.min(x1, x2) - threshold, Math.min(y1, y2) - threshold, Math.abs(x2 - x1) + threshold * 2,
				Math.abs(y2 - y1) + threshold * 2)).contains(x, y));
	}
	
	public Wire createModelWire() throws Exception {
		wire = new Wire();
		//int ts = 0;
		for (AbstractVertex v : vertices) {
			if (!v.isTrueVertex()) {
				TerminalGE t = (TerminalGE)v;
				String id = t.getName();
				BlockGE parent = t.getParentBlock();
				wire.addTerminal(parent.getBlock().getTerminal(id));
			//	ts++;
			}
		}
		//System.out.println("Created Wire with "+ ts+" terminals. ("+vertices.size()+" total vertices)");
		return wire;
	}
	
	public AbstractVertex getClosestVertex(AbstractVertex to) {
		AbstractVertex closest = null;
		int bestDist = Integer.MAX_VALUE;
		for (AbstractVertex v : vertices) {
			int d = v.distanceTo(to);
			if (d < bestDist) {
				closest = v;
				bestDist = d;
			}
		}
		return closest;
	}

}
