package gui;

import gui.commands.Command;
import gui.commands.DrawSelectionCommand;
import gui.commands.MoveSelectionCommand;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

public class Canvas extends JLayeredPane implements simulationEngine.Observer {
	private static final long serialVersionUID = 1L;
	
	private Selection selection = new Selection();
	private ArrayList<BlockGE> blocks = new ArrayList<>();
	private ArrayList<WireGE> wires = new ArrayList<>();
	private transient ClockListComponent clc;
	
	private Command currentCommand;
	private static final String delActionKey =  "delete";

	public Canvas(ClockListComponent clc) {
		super();
		this.clc = clc;
		setBackground(Color.WHITE);
		setOpaque(true);
		setLayout(null);
		setFocusable(true);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"),delActionKey);
		getActionMap().put(delActionKey, new DeleteAction());

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				requestFocus();
				if (e.isPopupTrigger()) {
					doPop(e);
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					// Determine Graphics Element clicked on and create Command
					int x = e.getX();
					int y = e.getY();

					for (WireGE w : wires) {
						if (selection.getSelectionCount() > 1 && w.onSelectedSection(x, y)) {
							currentCommand = new MoveSelectionCommand(selection, e);
							break;
						} else {
							currentCommand = w.getCommand(e, blocks, wires);
							if (currentCommand != null)
								break;
						}
					}
					if (currentCommand == null) {
						ListIterator<BlockGE> bli = blocks.listIterator(blocks.size());
						// Iterate in reverse over blocks
						while (bli.hasPrevious()) {
							BlockGE b = bli.previous();

							if (selection.getSelectionCount() > 1 && b.isSelected() && b.contains(x, y)) {
								currentCommand = new MoveSelectionCommand(selection, e);
								break;
							} else {
								currentCommand = b.getCommand(blocks, e);
								if (currentCommand != null)
									break;
							}
						}
					}
					if (currentCommand == null) {
						currentCommand = new DrawSelectionCommand(blocks, wires, selection, e);
					}
					repaint();
				}
			}

			public void mouseClicked(MouseEvent e) {
				requestFocus();
				int x = e.getX();
				int y = e.getY();
				//Double click on Block => Open Dialog
				if (e.getClickCount() == 2) {
					for (BlockGE b : blocks) {
						if (b.contains(x, y)) {
							b.openDialog(Canvas.this);
							break;
						}
					}
				} else {
					boolean somethingClicked = false;
					//Click on Block => Select or Move
					for (BlockGE b : blocks) {
						if (b.contains(x, y)) {
							if (selection.contains(b) && e.isControlDown()) {
								selection.remove(b);
							} else {
								if (!e.isControlDown())
									selection.clear();
								selection.add(b);
							}
							somethingClicked = true;
							break;
						}
					}
					//Click on Wire => Select wire
					ArrayList<Vertex> verticesToSelect = new ArrayList<>();
					for (WireGE w : wires) {
						if (w.contains(x, y)) verticesToSelect.addAll(w.getAllVertices());
					}
					if (!verticesToSelect.isEmpty()) {
						if (!e.isControlDown())
							selection.clear();
						for (Vertex v : verticesToSelect) {
							if (selection.contains(v) && e.isControlDown()) {
								selection.remove(v);
							} else {
								selection.add(v);
							}
						}
						somethingClicked = true;
					}
					//Clicked on nothing => empty selection
					if (!somethingClicked)
						selection.clear();
				}
				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				requestFocus();
				if (e.isPopupTrigger()) {
					doPop(e);
				} else if (currentCommand != null) {
					currentCommand.onRelease(e);
					currentCommand.execute();
					// insert here command stack push
					currentCommand = null;
				}
				repaint();
			}

		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (currentCommand != null) {
					currentCommand.onDrag(e);
				}
				repaint();
			}
		});
	}
	public void clear() {
		wires.clear();
		blocks.clear();
		clc.clear();
		repaint();
	}
	
	public void doPop(MouseEvent e) {
		boolean triggered = false;
		for (WireGE w : wires) {
			triggered = Popups.onWire(wires, w, e, Canvas.this);
			if (triggered)
				break;
		}
		if (!triggered) {
			ListIterator<BlockGE> bli = blocks.listIterator(blocks.size());
			// Iterate in reverse over blocks
			while (bli.hasPrevious()) {
				BlockGE b = bli.previous();
				triggered = Popups.onBlock(b, e, Canvas.this);
				if (triggered)
					break;
			}
		}
		if (!triggered) {
			Popups.onCanvas(e, blocks, wires, Canvas.this);
		}
	}
	
	public ClockListComponent getClockListComponent() {
		return clc;
	}
	
	
	public ArrayList<BlockGE> getBlocks() {
		return blocks;
	}
	public ArrayList<WireGE> getWires() {
		return wires;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Grids:
		g.setColor(Color.LIGHT_GRAY);
		for (int x = 0; x < getWidth(); x += Grid.getSize()) {
			g.drawLine(x, 0, x, getHeight());
		}
		for (int y = 0; y < getHeight(); y += Grid.getSize()) {
			g.drawLine(0, y, getWidth(), y);
		}
		// Blocks:
		for (BlockGE b : blocks) {
			b.draw(g);
		}
		// Wires:
		for (WireGE w : wires) {
			if (w != null)
			w.draw(g);
		}

		if (currentCommand != null)
			currentCommand.draw(g);
		//System.out.println(currentCommand);
	}
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
        public void actionPerformed(ActionEvent e) {
        	for (WireGE w : wires) {
				w.deleteSelectedVertices();
			}
			Iterator<BlockGE> iter = blocks.iterator();
			while (iter.hasNext()) {
			    BlockGE b = iter.next();
			    if (b.isSelected()) {
			    	b.deleteConnections();
			    	iter.remove();
			    	
			    }
			}
			repaint();
			cleanUp();
        }
    }

	public void cleanUp() {
		Iterator<WireGE> iter = wires.iterator();
		while (iter.hasNext()) {
		    WireGE w = iter.next();
		    if (w.disconnectIfAlmostEmpty())
		        iter.remove();
		}
	}
	@Override
	public void refresh() {
		repaint();
	}
	
	@SuppressWarnings("unchecked")
	public void loadFromStream(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		blocks = (ArrayList<BlockGE>) ois.readObject();
		wires = (ArrayList<WireGE>) ois.readObject();
		clc.loadFromStream(ois);
		repaint();
	}
	public void writeToStream(ObjectOutputStream oos) throws IOException {
		oos.writeObject(blocks);
		oos.writeObject(wires);
		clc.writeToStream(oos);
	}
}
