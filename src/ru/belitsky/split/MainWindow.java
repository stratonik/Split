package ru.belitsky.split;

import java.util.ResourceBundle;

import javax.swing.JFrame;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -4355409599886686324L;

	private ResourceBundle localization;

	public MainWindow() {
		localization = ResourceBundle.getBundle("ru.belitsky.split.resources.SplitLocalization");

		setTitle(localization.getString("title"));
	}

	public void setSplitedFile(String file) {

	}

}
