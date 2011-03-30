package ru.belitsky.split.prefs;


public interface IPreferences {

	String getInputFolder();

	String getOutputFolder();

	int getPartSize();

	int getPortSizeUnit();

	void setInputFolder(String folder);

	void setOutputFolder(String folder);

	void setPartSize(int size);

	void setPartSizeUnit(int unit);

}
