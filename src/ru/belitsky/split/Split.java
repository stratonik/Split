package ru.belitsky.split;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Split {

	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}

		MainWindow window = new MainWindow();
		if (args.length > 0) {
			window.setSplitedFile(args[0]);
		}
		window.setVisible(true);
	}

}
