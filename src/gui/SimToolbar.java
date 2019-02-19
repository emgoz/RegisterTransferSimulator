package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import simulationEngine.Block;
import simulationEngine.Model;
import simulationEngine.Registered;
import simulationEngine.Wire;

public class SimToolbar extends JToolBar {
	private static final long serialVersionUID = 1L;

	private Model model;
	private Canvas canvas;
	
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));

	public SimToolbar(Model model, Canvas canvas) {
		this.model = model;
		this.canvas = canvas;
		setFloatable(false);
		
		class OpenFilter extends FileFilter {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String extension = getExtension(f.getName());
				if (extension != null) {
					if (extension.equals("rtl")) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}
			

			@Override
			public String getDescription() {
				return "Register Transfer Simulator files (.rtl)";
			}
		}
		dialog.setFileFilter(new OpenFilter());
		
		
		
		add(New);
		add(Open);
		add(Save);
		
		add(new Separator(new Dimension(50,20)));
		//Compile.putValue(Action.SHORT_DESCRIPTION, "Compile Model");
		add(Compile);
		//SingleStep.putValue(Action.SHORT_DESCRIPTION, "Single Step");
		add(SingleStep);
		add(Cycle);
		
		add(new Separator(new Dimension(50,20)));
		add(new JLabel("Grid size = "));
		JSpinner gridSp = new JSpinner(new SpinnerNumberModel(Grid.getSize(), 8, 32, 2));
		gridSp.setMaximumSize(new Dimension(50,32));
		gridSp.setFocusable(false);
		gridSp.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Grid.setSize((int)gridSp.getValue());
				canvas.repaint();
			}
		});
		add(gridSp);
		// setRollover(true);
		/*
		for (Component c : getComponents()) {
			((JComponent) c).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		*/
	}
	Action New = new AbstractAction("New") {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			Object[] options = { "Save", "Don't Save", "Cancel" };
			  int action = JOptionPane.showOptionDialog(null, "Doing this will clear the workspace. Do you want to save your project first?", "New File",
				        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				        null, options, options[0]);	
			  if (action == 0) {
				  Save.actionPerformed(null);
				  canvas.clear();
			  } else if (action == 1) {
				  canvas.clear();
			  }
		}
	};
	

	Action Compile = new AbstractAction("Compile / Reset") {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.cleanUp();
			System.err.flush();
			ArrayList<BlockGE> blocks = canvas.getBlocks();
			ArrayList<WireGE> wires = canvas.getWires();
			ClockListComponent clc = canvas.getClockListComponent();
			model.clear();
			model.registerObserver(canvas);
			for (String clockPhaseName : clc.getClockPhaseNames()) {
				try {
					model.addClockPhase(clockPhaseName);
				} catch (Exception e1) {
					System.err.println(e1);
					canvas.repaint();
					return;
				}
			}
			for (BlockGE blockge : blocks) {
				try {
					Block b = blockge.createModelBlock();
					model.addBlock(b);
					if (b instanceof Registered) {
						for (String clockPhaseName : blockge.getUsedClockPhaseNames()) {
							model.registerBlock((Registered)b, clockPhaseName);
						}
					}
				} catch (Exception e1) {
					System.err.println(e1);
					canvas.repaint();
					return;
				}
			}
			for (WireGE wirege : wires) {
				try {
					Wire w = wirege.createModelWire();
					model.addWire(w);
				} catch (Exception e1) {
					System.err.println(e1);
					canvas.repaint();
					return;
				}
			}
			try {
				model.initSimulation();
			} catch (Exception e1) {
				System.err.println(e1);
				canvas.repaint();
				return;
			}
		}
	};
	Action SingleStep = new AbstractAction("Single Step") {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				model.singleStep();
			} catch (Exception e1) {
				System.err.println(e1);
				return;
			}
		}
	};
	Action Cycle = new AbstractAction("Cycle") {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				model.cycle();
			} catch (Exception e1) {
				System.err.println(e1);
				return;
			}
		}
	};
	Action Open = new AbstractAction("Open") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = dialog.getSelectedFile();
				try {
					FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);
	
					canvas.loadFromStream(ois);
	
					fis.close();
					ois.close();
				
				} catch (Exception e1) {
					System.err.println(e1);
				}
			}
		}
	};
	public Action Save = new AbstractAction("Save") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = dialog.getSelectedFile();
				if (!getExtension(file.getName()).equals("rtl")) {
					 file = new File(file.toString() + ".rtl"); 
				}
				
				try {
					FileOutputStream fos = new FileOutputStream(file);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					canvas.writeToStream(oos);
					fos.close();
					oos.close();
				} catch (Exception e1) {
					System.err.println(e1);
				}
			}
		}
	};
	public String getExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
