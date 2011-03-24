package ru.belitsky.split;

public class Split {

	public static void main(String[] args) {
		MainWindow window = new MainWindow();
		if (args.length > 0) {
			window.setSplitedFile(args[0]);
		}
		window.setVisible(true);
	}

}
