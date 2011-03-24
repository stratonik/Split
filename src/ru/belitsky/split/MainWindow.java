package ru.belitsky.split;

import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -4355409599886686324L;

	private ResourceBundle localization;

	public MainWindow() {
		localization = ResourceBundle.getBundle("ru.belitsky.split.resources.SplitLocalization");

		setTitle(localization.getString("title"));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		add(new JTextField(20));

		pack();
	}

	public void setSplitedFile(String file) {

	}

}
