package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ClockListComponent extends JPanel implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<String> clockPhases = new ArrayList<>();

	private JPanel listPane = null;

	public ClockListComponent() {
		JButton addButton = new JButton(" + ");
		setPreferredSize(new Dimension(150, 150));
		if (clockPhases.isEmpty()) clockPhases.add("phase_"+clockPhases.size());

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel("Clock Phases"));


		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clockPhases.add("phase_"+clockPhases.size());
				ClockListComponent.this.revalidate();
			}
		});
		add(addButton, c);

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		listPane = new JPanel();
		JScrollPane listScroller = new JScrollPane(listPane);
		add(listScroller, c);

		listPane.setLayout(new GridLayout(20, 1));

		revalidate();
	}	
	
	public void clear() {
		clockPhases.clear();
		clockPhases.add("phase_"+clockPhases.size());
		revalidate();
	}

	public ArrayList<String> getClockPhaseNames() {
		return clockPhases;
	}
	
	
	public void revalidate() {
		if (listPane != null) {
			listPane.removeAll();
			for (int i = 0; i < clockPhases.size(); i++) {
				listPane.add(new ClockPhase(i));
			}
			repaint();
		}
		super.revalidate();
	}

	private class ClockPhase extends JPanel {
		private static final long serialVersionUID = 1L;

		public ClockPhase(int index) {
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			JTextField nameField = new JTextField();
			nameField.setText(clockPhases.get(index));
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
					clockPhases.set(index, nameField.getText());
				}
			});

			JButton deleteButton = new JButton(" - ");
			if (clockPhases.size() == 1) deleteButton.setEnabled(false);
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (clockPhases.size() > 1)	{
						clockPhases.remove(index);
						ClockPhase.this.getParent().getParent().getParent().getParent().revalidate();
					}
				}
			});
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(nameField, gbc);
			gbc.weightx = 0.0;
			this.add(deleteButton, gbc);
		}
	}
	@SuppressWarnings("unchecked")
	public void loadFromStream(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		clockPhases = (ArrayList<String>) ois.readObject();
		revalidate();
	}
	public void writeToStream(ObjectOutputStream oos) throws IOException {
		oos.writeObject(clockPhases);
	}
}
