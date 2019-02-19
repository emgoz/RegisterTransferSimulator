package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import simulationEngine.BlockC;

public class BlockC_GE extends BlockGE implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String code = "";
	
	
	
	private transient BlockC block;
	
	public BlockC_GE(int x, int y, ArrayList<WireGE> wires) {
		super(x, y, wires);
		super.addTerminal(new TerminalGE(1, false, "x", this));
		super.addTerminal(new TerminalGE(1, true, "y", this));
	}

	@Override
	public void openDialog(Canvas c) {
		new Dialog(c);
	}
	
	private class Dialog extends JFrame {
		private static final long serialVersionUID = 1L;
		public Dialog(Canvas c) {
			
			addWindowListener(new java.awt.event.WindowAdapter() {
			    @Override
			    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			        c.repaint();
			    }
			});
			if (shape == null) {
				shape = Shape.RECT;
			}
			
			setTitle("Edit Combinatorical Block");
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
			
			JPanel shapePanel = new JPanel();
			shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.X_AXIS));
			shapePanel.add(new JLabel("Shape"));
			Shape[] shapes = { Shape.RECT, Shape.ALU_N, Shape.ALU_E, Shape.ALU_S, Shape.ALU_W, Shape.MUX_N, Shape.MUX_E, Shape.MUX_S, Shape.MUX_W };

			JComboBox<Shape> shapeList = new JComboBox<>(shapes);
			int selIndex = 0;
			switch (shape) {
			case ALU_E:
				selIndex = 2;
				break;
			case ALU_N:
				selIndex = 1;
				break;
			case ALU_S:
				selIndex = 3;
				break;
			case ALU_W:
				selIndex = 4;
				break;
			case MUX_E:
				selIndex = 6;
				break;
			case MUX_N:
				selIndex = 5;
				break;
			case MUX_S:
				selIndex = 7;
				break;
			case MUX_W:
				selIndex = 8;
				break;
			case RECT:
				selIndex = 0;
				break;
			default:
				break;
				
			}
			shapeList.setSelectedIndex(selIndex);
			shapeList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shape = (Shape)shapeList.getSelectedItem();
				}
			});
			shapePanel.add(shapeList);
			top.add(shapePanel);
			
			
			
			
			TerminalList inputs = new TerminalList("Inputs", false);
			top.add(inputs);
			TerminalList outputs = new TerminalList("Outputs", true);
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
					BlockC_GE.this.addTerminal(new TerminalGE(1, isOutput, "", BlockC_GE.this));
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
						listPane.add(new TerminalListEntry(t));
					}
				}
				repaint();
			}
			super.revalidate();
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
					BlockC_GE.this.deleteTerminal(t);
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
		block = new BlockC(label);
		for (TerminalGE t : terminals) {
			block.addTerminal(t.getName(), t.getWidth(), t.isOutput());
		}
		block.initFromString(code);
		return block;
	}
	public Block getBlock() {
		return block;
	}
	public HashSet<String> getUsedClockPhaseNames() {
		return new HashSet<String>();
	}
}
