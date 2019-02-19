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
import simulationEngine.BlockR;

public class BlockR_GE extends BlockGE implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code = "";
	private final String nextPrefix = "next_";
	private transient BlockR block;

	private HashSet<String> usedClocks = new HashSet<>();
	
	public BlockR_GE(int x, int y, ArrayList<WireGE> wires) {
		super(x, y, wires);
		super.addTerminal(new TerminalGE(1, false, "x", this));
		super.addTerminal(new TerminalGE(1, true, "y", this));
	}

	@Override
	public void openDialog(Canvas c) {
		new Dialog(c);
	}
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.black);
		
		g2d.setStroke(new BasicStroke(Grid.getSize()/8));
		int gd = Grid.getSize();
		int x = Grid.toPixelPoint(getX1());
		int y = Grid.toPixelPoint(getY1());
		g2d.drawLine(x+gd/2, y, x+gd, y+gd*2/3);
		g2d.drawLine(x+3*gd/2, y, x+gd, y+gd*2/3);
	}
	
	private class Dialog extends JFrame {
		private static final long serialVersionUID = 1L;
		public Dialog(Canvas c) {
			ClockListComponent clc = c.getClockListComponent();
			if (usedClocks == null) usedClocks = new HashSet<>();
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
			    	if (usedClocks.isEmpty()) {
			    		usedClocks.add(clc.getClockPhaseNames().get(0)); //add first clock if none selected
			    		System.out.println("Register Block clock phase forced to "+ clc.getClockPhaseNames().get(0));
			    	}
			        c.repaint();
			    }
			});
			
			
			setTitle("Edit Registered Block");
			setSize(500, 600);
			setLocationRelativeTo(c.getParent());
			
			JPanel page = new ScrollablePanel();
			
			page.setLayout(new BorderLayout());;
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
			clockPanel.add(new JLabel("Updated on clock phases:"));
			clockPanel.add(new ClockSelectPanel(clc));
			top.add(clockPanel);
			
			TerminalList inputs = new TerminalList("Inputs", false);
			top.add(inputs);
			TerminalList outputs = new TerminalList("Outputs / Register Inputs / Initial values", true);
			top.add(outputs);
			JLabel header = new JLabel("Code");
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
				public String getText(){
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
			
			//JScrollPane pageSc = new JScrollPane(page);
			//pageSc.getVerticalScrollBar().setUnitIncrement(30);
			
			add(page);
		}
	}
	
	
	private class ClockSelectPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		public ClockSelectPanel(ClockListComponent clc) {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			if (usedClocks.isEmpty()) usedClocks.add(clc.getClockPhaseNames().get(0)); //add first clock if none selected
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
	
	
	private class TerminalList extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private boolean isOutput;
		private JPanel listPane = null;
		public TerminalList(String text, boolean isOutput) {
			
			this.isOutput = isOutput;
			
			setPreferredSize(new Dimension(400,150));
			
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			JLabel header = new JLabel(text);
			header.setHorizontalAlignment(JLabel.CENTER);
			c.weightx = 1.0;
			c.weighty = 0.0;
			c.fill = GridBagConstraints.BOTH;
			add(header, c);
			JButton addButton = new JButton(" + ");
			
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BlockR_GE.this.addTerminal(new TerminalGE(1, isOutput, "", BlockR_GE.this));
					TerminalList.this.revalidate();
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
			
			listPane.setLayout(new GridLayout(100,1));
			
			revalidate();

		}
		public void revalidate() {
			if (listPane != null) {
				listPane.removeAll();
				for (TerminalGE t : terminals) {
					if (t.isOutput() == isOutput) {
						if (isOutput) listPane.add(new OutputListEntry(t));
						else listPane.add(new TerminalListEntry(t));
					}
				}
				repaint();
			}
			super.revalidate();
		}
	}
	
	private class OutputListEntry extends JPanel {
		private static final long serialVersionUID = 1L;
		
		public OutputListEntry(TerminalGE t) {
			setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            
            JTextField nameField = new JTextField(t.getName());
            JTextField nextField = new JTextField(nextPrefix + t.getName());
            JTextField initField = new JTextField(t.getInitialValue());
			nameField.setFont(new Font("Courier", Font.PLAIN, 16));
			nextField.setFont(new Font("Courier", Font.PLAIN, 16));
			initField.setFont(new Font("Courier", Font.PLAIN, 16));
			nextField.setEditable(false);
			nameField.getDocument().addDocumentListener(new DocumentListener() {
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
					  t.setName(nameField.getText());
					  nextField.setText(nextPrefix + nameField.getText());
				  }
			});
			initField.getDocument().addDocumentListener(new DocumentListener() {
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
					  t.setInitialValue(initField.getText());
				  }
			});
			
			JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(t.getWidth(),1,8192, 1));
			sizeSpinner.setFont(new Font("Courier", Font.PLAIN, 16));
			
			sizeSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					t.setWidth((int)sizeSpinner.getValue());
				}
			});
			
			JButton deleteButton = new JButton(" - ");
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BlockR_GE.this.deleteTerminal(t);
					OutputListEntry.this.getParent().getParent().getParent().getParent().revalidate();
				}
			});
			
			gbc.weightx = 0.34;
            gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(nameField, gbc);
			this.add(nextField, gbc);
			this.add(initField, gbc);
			
			gbc.weightx = 0.0;
			this.add(sizeSpinner,gbc);
			this.add(deleteButton, gbc);
		}
	}
	
	private class TerminalListEntry extends JPanel {
		private static final long serialVersionUID = 1L;
		
		public TerminalListEntry(TerminalGE t) {
			setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            
            JTextField nameField = new JTextField();
			nameField.setText(t.getName());
			nameField.setFont(new Font("Courier", Font.PLAIN, 16));
			nameField.getDocument().addDocumentListener(new DocumentListener() {
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
					  t.setName(nameField.getText());
				  }
			});
			
			JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(t.getWidth(),1,8192, 1));
			sizeSpinner.setFont(new Font("Courier", Font.PLAIN, 16));
			
			sizeSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					t.setWidth((int)sizeSpinner.getValue());
				}
			});
			
			JButton deleteButton = new JButton(" - ");
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BlockR_GE.this.deleteTerminal(t);
					TerminalListEntry.this.getParent().getParent().getParent().getParent().revalidate();
				}
			});
			
			gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(nameField, gbc);
			gbc.weightx = 0.0;
			this.add(sizeSpinner,gbc);
			this.add(deleteButton, gbc);
		}
	}

	@Override
	public Block createModelBlock() throws Exception {
		block = new BlockR(nextPrefix, label);
		for (TerminalGE t : terminals) {
			block.addTerminal(t.getName(), t.getWidth(), t.isOutput());
			block.setInitialValue(t.getName(), t.getInitialValue());
		}
		block.initFromString(code);
		return block;
	}
	public Block getBlock() {
		return block;
	}
	public HashSet<String> getUsedClockPhaseNames() {
		return usedClocks;
	}
}
