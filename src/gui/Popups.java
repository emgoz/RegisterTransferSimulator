package gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Popups {
	
	
	public static boolean onWire(ArrayList<WireGE> wires, WireGE w, MouseEvent e, Container c) {
		int x = e.getX();
		int y = e.getY();
		
		//Wire w clicked somewhere in between two vertices and another wire's vertex coincides with mouse coords
		if (w.contains(x, y)) {
			for (WireGE w2 : wires) {
				int x1 = Grid.toGridPoint(x);
				int y1 = Grid.toGridPoint(y);
				AbstractVertex v = w2.getVertexAt(x1,y1);
				if (w2 != w && v != null) {
					MergeWireMenu menu = new MergeWireMenu(w2, w, v, wires, c);
					menu.show(e.getComponent(), e.getX(), e.getY());
					return true;
				}
			} 
		}
		//Vertex of wire w clicked.
		for (AbstractVertex v : w.getAllVertices()) {
			if (v.getDisplay() != null && v.getDisplay().contains(x, y)) {
				DeleteDisplayMenu menu = new DeleteDisplayMenu(v, c);
				menu.show(e.getComponent(), e.getX(), e.getY());
				return true;
			}
			if (v.contains(x, y)) {
				for (WireGE w2 : wires) {
					if (w2 != w && w2.contains(x, y)) {
						MergeWireMenu menu = new MergeWireMenu(w, w2, v, wires, c);
						menu.show(e.getComponent(), e.getX(), e.getY());
						return true;
					}
				} 
				OnVertexMenu menu = new OnVertexMenu(w, v, c);
				menu.show(e.getComponent(), e.getX(), e.getY());
				return true;
			}	
		}
		//Wire section clicked and all above conditions false
		if (w.contains(x, y)) {
			OnWireMenu menu = new OnWireMenu(e, wires, w, c);
			menu.show(e.getComponent(), e.getX(), e.getY());
			return true;
		}
	
		return false;
	}
	public static boolean onBlock(BlockGE b, MouseEvent e, Container c) {
		int x = e.getX();
		int y = e.getY();
		for (TerminalGE t : b.getTerminals()) {
			if (t.getDisplay() != null && t.getDisplay().contains(x, y)) {
				DeleteDisplayMenu menu = new DeleteDisplayMenu(t, c);
				menu.show(e.getComponent(), e.getX(), e.getY());
				return true;
			}
			if (t.contains(x, y)) {
				AddDisplayMenu menu = new AddDisplayMenu(t, c);
				menu.show(e.getComponent(), e.getX(), e.getY());
				return true;
			}
		}
		return false;
	}
	private static class AddDisplayMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public AddDisplayMenu(AbstractVertex v, Container c) {
			JMenuItem add = new JMenuItem("Add Wire Display");
			add(add);
			add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					v.setDisplay(new WireSignalDisplay(v));
					c.repaint();
				}
			});
		}
	}
	private static class DeleteDisplayMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public DeleteDisplayMenu(AbstractVertex v, Container c) {
			JMenuItem del = new JMenuItem("Delete Wire Display");
			add(del);
			del.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					v.setDisplay(null);
					c.repaint();
				}
			});
		}
	}
	
	private static class MergeWireMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public MergeWireMenu(WireGE w1, WireGE w2, AbstractVertex v1, ArrayList<WireGE> wires, Container c) {
			JMenuItem merge = new JMenuItem("Connect Wires");
			add(merge);
			merge.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AbstractVertex v2 = w2.getVertexAt(v1.getX(), v1.getY());
					if (v2 == null) {
						w2.addIntermediateVertex(Grid.toPixelPoint(v1.getX()), Grid.toPixelPoint(v1.getY()));
						v2 = w2.getVertexAt(v1.getX(), v1.getY());
					}
					WireGE sum = new WireGE(w1, w2, v1, v2);
					wires.remove(w1);
					wires.remove(w2);
					wires.add(sum);
					c.repaint();
				}
			});
		}
	}
	
	
	private static class OnVertexMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public OnVertexMenu(WireGE w, AbstractVertex v, Container c) {
			JMenuItem del = new JMenuItem("Delete Vertex");
			add(del);
			del.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					w.deleteVertex(v);
					c.repaint();
				}
			});
			JMenuItem add = new JMenuItem("Add Wire Display");
			add(add);
			add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					v.setDisplay(new WireSignalDisplay(v));
					c.repaint();
				}
			});
		}
	}
	private static class OnWireMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public OnWireMenu(MouseEvent f, ArrayList<WireGE> wires, WireGE w, Container c) {
			JMenuItem add = new JMenuItem("Add Vertex");
			add(add);
			JMenuItem delSec = new JMenuItem("Delete Section");
			add(delSec);
			JMenuItem del = new JMenuItem("Delete Wire");
			add(del);
			JMenuItem addD = new JMenuItem("Add Wire Display");
			add(addD);
			add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					w.addIntermediateVertex(f.getX(),f.getY());
					c.repaint();
				}
			});
			del.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					wires.remove(w);
					c.repaint();
				}
			});
			delSec.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean delete = w.removeSection(f.getX(), f.getY());
					if (delete) wires.remove(w);
					c.repaint();
				}
			});
			addD.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AbstractVertex v = w.getClosestVertex(new Vertex(Grid.toGridPoint(f.getX()), Grid.toGridPoint(f.getY())));
					v.setDisplay(new WireSignalDisplay(v));
					v.getDisplay().moveToPixel(f.getX(), f.getY());
					c.repaint();
				}
			});
		}
	}
	public static class newBlockMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		
		JMenuItem newBlockC, newBlockR, newBlockM, newBlockD;

		public newBlockMenu(MouseEvent f, ArrayList<BlockGE> blocks, ArrayList<WireGE> wires, Container c) {
			newBlockC = new JMenuItem("New Combinatorical Block");
			add(newBlockC);
			newBlockC.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					blocks.add(new BlockC_GE(f.getX(), f.getY(), wires));
					c.repaint();
				}
			});
			newBlockR = new JMenuItem("New Registered Block");
			add(newBlockR);
			newBlockR.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					blocks.add(new BlockR_GE(f.getX(), f.getY(), wires));
					c.repaint();
				}
			});
			newBlockM = new JMenuItem("New Memory Block");
			add(newBlockM);
			newBlockM.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					blocks.add(new BlockM_GE(f.getX(), f.getY(), wires));
					c.repaint();
				}
			});
			newBlockD = new JMenuItem("New Display Block");
			add(newBlockD);
			newBlockD.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					blocks.add(new BlockD_GE(f.getX(), f.getY(), wires));
					c.repaint();
				}
			});
		}
	}
	public static void onCanvas(MouseEvent e, ArrayList<BlockGE> blocks, ArrayList<WireGE> wires, Container c) {
		Popups.newBlockMenu menu = new newBlockMenu(e, blocks, wires, c);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}
