package ru.belitsky.split;

import java.io.File;

public class Task {

	private File inputFile;

	private File outputDir;

	private long partSize = 1;

	private long partFrom = 1;

	private long partNumber = 1;

	public Task(File inputFile, File outputDir) {
		this.inputFile = inputFile;
		this.outputDir = outputDir;
	}

	public long getFirstByteOffset() {
		return Math.min(inputFile.length(), getPartSize() * (getPartFrom() - 1));
	}

	public File getInputFile() {
		return inputFile;
	}

	public long getLastByteOffset() {
		return Math.min(inputFile.length(), getPartSize() * (getPartFrom() - 1 + getPartNumber()));
	}

	public File getOutputDir() {
		return outputDir;
	}

	public long getPartFrom() {
		return partFrom;
	}

	public long getPartNumber() {
		return partNumber;
	}

	public long getPartSize() {
		return partSize;
	}

	public void setPartFrom(long partFrom) {
		if (partFrom > 0) {
			this.partFrom = partFrom;
		}
	}

	public void setPartNumber(long partNumber) {
		if (partNumber > 0) {
			this.partNumber = partNumber;
		}
	}

	public void setPartSize(int partSize, int unit) {
		setPartSize((long) (partSize * Math.pow(1024, unit)));
	}

	public void setPartSize(long partSize) {
		if (partSize > 0) {
			this.partSize = partSize;
		}
	}

}
