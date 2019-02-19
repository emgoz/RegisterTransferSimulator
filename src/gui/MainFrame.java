package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import simulationEngine.Model;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private Canvas canvas;
	private SimToolbar toolbar;
	private Model model;
	private ClockListComponent clc;

	public MainFrame() {
		this.setSize(880, 640);

		this.setTitle("Register Transfer Simulator");
		setLayout(new BorderLayout());
		this.setFocusable(true);

		clc = new ClockListComponent();
		canvas = new Canvas(clc);
		canvas.setPreferredSize(new Dimension(3200, 2000));

		JScrollPane scrollcanvas = new JScrollPane(canvas);
		scrollcanvas.getVerticalScrollBar().setUnitIncrement(8);
		scrollcanvas.getHorizontalScrollBar().setUnitIncrement(8);

		model = new Model();

		toolbar = new SimToolbar(model, canvas);
		Console console = new Console();

		add(scrollcanvas, BorderLayout.CENTER);
		add(toolbar, BorderLayout.NORTH);
		add(console, BorderLayout.SOUTH);
		add(clc, BorderLayout.EAST);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	Object[] options = {"Save", "Don't Save", "Cancel"};
				int action = JOptionPane.showOptionDialog(null, "Do you want to save your project before you leave?", "Save first?",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (action == 0) {
					toolbar.Save.actionPerformed(null);
					dispose();
					System.exit(0);
				} else if (action == 1) {
					dispose();
					System.exit(0);
				}
            }
        });
		setVisible(true);
	}
}
