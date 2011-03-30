package ru.belitsky.split.prefs;

import java.util.prefs.Preferences;

import ru.belitsky.split.Split;

public class SystemPreferences implements IPreferences {

	private static final String INPUT_FOLDER = "InputFolder";
	private static final String OUTPUT_FOLDER = "OutputFolder";
	private static final String PART_SIZE = "PartSize";
	private static final String PART_SIZE_UNIT = "PartSizeUnit";

	private Preferences prefs = Preferences.userNodeForPackage(Split.class);

	@Override
	public String getInputFolder() {
		return prefs.get(INPUT_FOLDER, null);
	}

	@Override
	public String getOutputFolder() {
		return prefs.get(OUTPUT_FOLDER, null);
	}

	@Override
	public int getPartSize() {
		return prefs.getInt(PART_SIZE, -1);
	}

	@Override
	public int getPortSizeUnit() {
		return prefs.getInt(PART_SIZE_UNIT, -1);
	}

	@Override
	public void setInputFolder(String folder) {
		if (folder != null) {
			prefs.put(INPUT_FOLDER, folder);
		}
	}

	@Override
	public void setOutputFolder(String folder) {
		if (folder != null) {
			prefs.put(OUTPUT_FOLDER, folder);
		}
	}

	@Override
	public void setPartSize(int size) {
		prefs.putInt(PART_SIZE, size);

	}

	@Override
	public void setPartSizeUnit(int unit) {
		prefs.putInt(PART_SIZE_UNIT, unit);
	}

}
