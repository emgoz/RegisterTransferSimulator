package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

import simulationEngine.Block;
import simulationEngine.BlockM;

public class BlockM_GE extends BlockGE {
	private static final long serialVersionUID = 1L;

	private ArrayList<WritePort> writePorts = new ArrayList<>();
	private ArrayList<ReadPort> readPorts = new ArrayList<>();

	private HashSet<String> usedClocks = new HashSet<>();

	private String code = "";
	private transient BlockM block;
	private int addrWidth, dataWidth;

	public BlockM_GE(int x, int y, ArrayList<WireGE> wires) {
		super(x, y, wires);
		addrWidth = 1;
		dataWidth = 1;
	}

	public void draw(Graphics g) {
		super.draw(g);
		if (!writePorts.isEmpty()) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.black);

			g2d.setStroke(new BasicStroke(Grid.getSize() / 8));
			int gd = Grid.getSize();
			int x = Grid.toPixelPoint(getX1());
			int y = Grid.toPixelPoint(getY1());
			g2d.drawLine(x + gd / 2, y, x + gd, y + gd * 2 / 3);
			g2d.drawLine(x + 3 * gd / 2, y, x + gd, y + gd * 2 / 3);
		}
	}

	private class WritePort implements Serializable {
		private static final long serialVersionUID = 1L;
		TerminalGE addr, data, ena;
	}

	private class ReadPort implements Serializable {
		private static final long serialVersionUID = 1L;
		TerminalGE addr, data;
	}

	@Override
	public void openDialog(Canvas c) {
		new Dialog(c);
	}

	private class Dialog extends JFrame {
		private static final long serialVersionUID = 1L;

		public Dialog(Canvas c) {
			ClockListComponent clc = c.getClockListComponent();
			if (usedClocks == null)
				usedClocks = new HashSet<>();
			Iterator<String> it = usedClocks.iterator();
			while (it.hasNext()) {
				String s = it.next();
				if (!clc.getClockPhaseNames().contains(s)) {
					it.remove();
				}
			}

			addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					if (usedClocks.isEmpty() && !writePorts.isEmpty()) {
						usedClocks.add(clc.getClockPhaseNames().get(0)); // add
																			// first
																			// clock
																			// if
																			// none
																			// selected
						System.out.println("Memery Block clock phase forced to " + clc.getClockPhaseNames().get(0));
					}

					c.repaint();
				}
			});

			setTitle("Edit Memory Block");
			setSize(500, 600);
			setLocationRelativeTo(c.getParent());

			JPanel page = new ScrollablePanel();

			page.setLayout(new BorderLayout());
			;
			JPanel top = new JPanel();
			top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
			namePanel.add(new JLabel("Label:"));
			JTextField nameF = new JTextField(label);
			namePanel.add(nameF);
			nameF.setFont(new Font("Courier", Font.PLAIN, 16));
			nameF.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					label = nameF.getText();
				}
			});
			top.add(namePanel);
			JPanel clockPanel = new JPanel();
			clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.X_AXIS));
			clockPanel.add(new JLabel("Write on clock phases:"));
			clockPanel.add(new ClockSelectPanel(clc));
			top.add(clockPanel);

			JPanel widthPanel = new JPanel();
			widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
			widthPanel.add(new JLabel("Address Width:"));
			JSpinner addrSpinner = new JSpinner(new SpinnerNumberModel(addrWidth, 1, 8192, 1));
			addrSpinner.setFont(new Font("Courier", Font.PLAIN, 16));
			addrSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					addrWidth = (int) addrSpinner.getValue();
					for (ReadPort p : readPorts) {
						p.addr.setWidth(addrWidth);
					}
					for (WritePort p : writePorts) {
						p.addr.setWidth(addrWidth);
					}
				}
			});
			widthPanel.add(addrSpinner);
			widthPanel.add(new JLabel("Data Width:"));

			JSpinner dataSpinner = new JSpinner(new SpinnerNumberModel(dataWidth, 1, 8192, 1));
			dataSpinner.setFont(new Font("Courier", Font.PLAIN, 16));
			dataSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					dataWidth = (int) dataSpinner.getValue();
					for (ReadPort p : readPorts) {
						p.data.setWidth(dataWidth);
					}
					for (WritePort p : writePorts) {
						p.data.setWidth(dataWidth);
					}
				}
			});
			widthPanel.add(dataSpinner);
			top.add(widthPanel);
			WritePortList wpl = new WritePortList();
			ReadPortList rpl = new ReadPortList();

			top.add(wpl);
			top.add(rpl);
			JLabel header = new JLabel("Initial Memory Contents");
			top.add(header);
			page.add(top, BorderLayout.NORTH);
			JTextArea codeArea = new JTextArea();
			JTextArea lines = new JTextArea("1");
			JScrollPane sp = new JScrollPane(codeArea);

			codeArea.setFont(new Font("Courier", Font.PLAIN, 14));
			lines.setFont(new Font("Courier", Font.PLAIN, 14));

			lines.setBackground(Color.LIGHT_GRAY);
			lines.setEditable(false);

			sp.getViewport().add(codeArea);
			sp.setRowHeaderView(lines);

			codeArea.setTabSize(4);
			codeArea.getDocument().addDocumentListener(new DocumentListener() {
				public String getText() {
					int caretPosition = codeArea.getDocument().getLength();
					Element root = codeArea.getDocument().getDefaultRootElement();
					String text = "1 " + System.getProperty("line.separator");
					for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
						text += i +" "+ System.getProperty("line.separator");
					}
					return text;
				}

				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					code = codeArea.getText();
					lines.setText(getText());
				}
			});
			codeArea.setText(code);
			page.add(sp, BorderLayout.CENTER);
			setVisible(true);

			// JScrollPane pageSc = new JScrollPane(page);
			// pageSc.getVerticalScrollBar().setUnitIncrement(30);

			add(page);
		}
	}

	private class ClockSelectPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public ClockSelectPanel(ClockListComponent clc) {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			if (usedClocks.isEmpty() && !writePorts.isEmpty())
				usedClocks.add(clc.getClockPhaseNames().get(0)); // add first
																	// clock if
																	// none
																	// selected
			for (String name : clc.getClockPhaseNames()) {
				JCheckBox b = new JCheckBox(name);
				add(b);
				b.setSelected(usedClocks.contains(name));
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (b.isSelected()) {
							usedClocks.add(name);
						} else {
							usedClocks.remove(name);
						}
					}
				});
			}
		}
	}

	private class WritePortList extends JPanel {
		private static final long serialVersionUID = 1L;
		private JPanel listPane = null;

		public WritePortList() {

			setPreferredSize(new Dimension(400, 150));

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			JLabel header = new JLabel("Write Ports");
			header.setHorizontalAlignment(JLabel.CENTER);
			c.weightx = 1.0;
			c.weighty = 0.0;
			c.fill = GridBagConstraints.BOTH;
			add(header, c);
			JButton addButton = new JButton(" + ");

			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					WritePort wp = new WritePort();
					wp.addr = new TerminalGE(addrWidth, false, "waddr" + writePorts.size(), BlockM_GE.this);
					wp.data = new TerminalGE(dataWidth, false, "wdata" + writePorts.size(), BlockM_GE.this);
					wp.ena = new TerminalGE(1, false, "wena" + writePorts.size(), BlockM_GE.this);
					writePorts.add(wp);
					BlockM_GE.this.addTerminal(wp.addr);
					BlockM_GE.this.addTerminal(wp.data);
					BlockM_GE.this.addTerminal(wp.ena);
					WritePortList.this.revalidate();
				}
			});

			c.gridwidth = GridBagConstraints.REMAINDER;
			add(addButton, c);

			c.weightx = 0.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;

			listPane = new JPanel();
			JScrollPane listScroller = new JScrollPane(listPane);
			add(listScroller, c);

			listPane.setLayout(new GridLayout(100, 1));

			revalidate();

		}

		public void revalidate() {
			if (listPane != null) {
				listPane.removeAll();
				for (WritePort wp : writePorts) {
					listPane.add(new WritePortListEntry(wp));
				}
				repaint();
			}
			super.revalidate();
		}
	}

	private class ReadPortList extends JPanel {
		private static final long serialVersionUID = 1L;
		private JPanel listPane = null;

		public ReadPortList() {

			setPreferredSize(new Dimension(400, 150));

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			JLabel header = new JLabel("Read Ports");
			header.setHorizontalAlignment(JLabel.CENTER);
			c.weightx = 1.0;
			c.weighty = 0.0;
			c.fill = GridBagConstraints.BOTH;
			add(header, c);
			JButton addButton = new JButton(" + ");

			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ReadPort rp = new ReadPort();
					rp.addr = new TerminalGE(addrWidth, false, "raddr" + readPorts.size(), BlockM_GE.this);
					rp.data = new TerminalGE(dataWidth, true, "rdata" + readPorts.size(), BlockM_GE.this);
					readPorts.add(rp);
					BlockM_GE.this.addTerminal(rp.addr);
					BlockM_GE.this.addTerminal(rp.data);
					ReadPortList.this.revalidate();
				}
			});

			c.gridwidth = GridBagConstraints.REMAINDER;
			add(addButton, c);

			c.weightx = 0.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;

			listPane = new JPanel();
			JScrollPane listScroller = new JScrollPane(listPane);
			add(listScroller, c);

			listPane.setLayout(new GridLayout(100, 1));

			revalidate();

		}

		public void revalidate() {
			if (listPane != null) {
				listPane.removeAll();
				for (ReadPort rp : readPorts) {
					listPane.add(new ReadPortListEntry(rp));
				}
				repaint();
			}
			super.revalidate();
		}
	}

	private class WritePortListEntry extends JPanel {
		private static final long serialVersionUID = 1L;

		public WritePortListEntry(WritePort p) {
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			JTextField addrNameField = new JTextField(p.addr.getName());
			JTextField dataNameField = new JTextField(p.data.getName());
			JTextField enaNameField = new JTextField(p.ena.getName());

			addrNameField.setFont(new Font("Courier", Font.PLAIN, 16));
			dataNameField.setFont(new Font("Courier", Font.PLAIN, 16));
			enaNameField.setFont(new Font("Courier", Font.PLAIN, 16));

			addrNameField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					p.addr.setName(addrNameField.getText());
				}
			});
			dataNameField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					p.data.setName(dataNameField.getText());
				}
			});
			enaNameField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					p.ena.setName(enaNameField.getText());
				}
			});
			JButton deleteButton = new JButton(" - ");
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BlockM_GE.this.deleteTerminal(p.addr);
					BlockM_GE.this.deleteTerminal(p.data);
					BlockM_GE.this.deleteTerminal(p.ena);
					writePorts.remove(p);
					WritePortListEntry.this.getParent().getParent().getParent().getParent().revalidate();
				}
			});

			gbc.weightx = 0.34;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(addrNameField, gbc);
			this.add(dataNameField, gbc);
			this.add(enaNameField, gbc);

			gbc.weightx = 0.0;
			this.add(deleteButton, gbc);
		}
	}

	private class ReadPortListEntry extends JPanel {
		private static final long serialVersionUID = 1L;

		public ReadPortListEntry(ReadPort p) {
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			JTextField addrNameField = new JTextField(p.addr.getName());
			JTextField dataNameField = new JTextField(p.data.getName());

			addrNameField.setFont(new Font("Courier", Font.PLAIN, 16));
			dataNameField.setFont(new Font("Courier", Font.PLAIN, 16));

			addrNameField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					p.addr.setName(addrNameField.getText());
				}
			});
			dataNameField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void update() {
					p.data.setName(dataNameField.getText());
				}
			});
			JButton deleteButton = new JButton(" - ");
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BlockM_GE.this.deleteTerminal(p.addr);
					BlockM_GE.this.deleteTerminal(p.data);
					readPorts.remove(p);
					ReadPortListEntry.this.getParent().getParent().getParent().getParent().revalidate();
				}
			});

			gbc.weightx = 0.5;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(addrNameField, gbc);
			this.add(dataNameField, gbc);
			gbc.weightx = 0.0;
			this.add(deleteButton, gbc);
		}
	}

	@Override
	public Block createModelBlock() throws Exception {
		block = new BlockM(label, addrWidth, dataWidth);
		for (WritePort w : writePorts) {
			block.addWritePort(w.addr.getName(), w.data.getName(), w.ena.getName());
		}
		for (ReadPort r : readPorts) {
			block.addReadPort(r.addr.getName(), r.data.getName());
		}
		block.parseFromString(code);
		return block;
	}

	public Block getBlock() {
		return block;
	}

	public HashSet<String> getUsedClockPhaseNames() {
		return usedClocks;
	}
}
