package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import simulationEngine.Block;
import simulationEngine.BlockD;

public class BlockD_GE extends BlockGE {
	private static final long serialVersionUID = 1L;

	private transient BlockD block;
	private int base = 10;
	
	public BlockD_GE(int x, int y, ArrayList<WireGE> wires) {
		super(x, y, wires);
		super.addTerminal(new TerminalGE(1, false, "x", this));
	}
	
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
			
			
			setTitle("Edit Display Block");
			setSize(500, 120);
			setLocationRelativeTo(c.getParent());
			
			JPanel page = new JPanel();
			
			page.setLayout(new BorderLayout());;
			JPanel top = new JPanel();
			top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
			JPanel basePanel = new JPanel();
			basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.X_AXIS));
			basePanel.add(new JLabel("Base for signal value display:"));
			String[] bases = { "2", "10", "16" };

			JComboBox<String> baseList = new JComboBox<>(bases);
			int selIndex = 0;
			switch (base) {
			case 2: selIndex = 0; break;
			case 10: selIndex = 1; break;
			case 16: selIndex = 2; break;
			}
			baseList.setSelectedIndex(selIndex);
			baseList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					base = Integer.parseInt((String)baseList.getSelectedItem());
				}
			});
			basePanel.add(baseList);
			top.add(basePanel);
			
			TerminalPanel input = new TerminalPanel(terminals.get(0));
			top.add(input);
			page.add(top, BorderLayout.NORTH);
		
			setVisible(true);
			
			JScrollPane pageSc = new JScrollPane(page);
			pageSc.getVerticalScrollBar().setUnitIncrement(30);
			
			add(pageSc);
		}
	}

	private class TerminalPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		public TerminalPanel(TerminalGE t) {
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
			
			gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(nameField, gbc);
			gbc.weightx = 0.0;
			this.add(sizeSpinner,gbc);
		}
	}
	@Override
	public Block createModelBlock() throws Exception {
		TerminalGE t = terminals.get(0);
		block = new BlockD(t.getName(), t.getWidth(), base);
		return block;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public HashSet<String> getUsedClockPhaseNames() {
		return new HashSet<>();
	}

}
