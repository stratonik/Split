package ru.belitsky.split.prefs;

public class PreferencesFactory {

	public static final IPreferences getPreferences() {
		return new SystemPreferences();
	}

}
