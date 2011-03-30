package ru.belitsky.split;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import ru.belitsky.split.prefs.PreferencesFactory;

public class Split {

	public static void main(String[] args) {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Advanced Split");
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
		window.applyPreferences(PreferencesFactory.getPreferences());
		if (args.length > 0) {
			window.setSplitedFile(args[0]);
		}
		window.setVisible(true);
	}

}
