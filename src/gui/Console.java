package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	private static JTextArea  textArea = new JTextArea();
	
	public Console() {
		super(textArea);
		textArea.setFont(new Font("Courier", Font.PLAIN, 12));
		textArea.setPreferredSize(new Dimension(500,450));
		textArea.setEditable(false);
		setPreferredSize(new Dimension(500,100));
		PrintStream consoleStream = new PrintStream(new TextAreaOutputStream(textArea,20));
		System.setOut(consoleStream);
		System.setErr(consoleStream);
	}
	

}
