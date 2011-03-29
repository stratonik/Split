package ru.belitsky.split;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.SwingWorker;

public class SplitWorker extends SwingWorker<Integer, Integer> {

	private final Task task;

	private long total;

	private long currentPart;

	public SplitWorker(Task task) {
		this.task = task;
		this.total = 0;
		this.currentPart = task.getPartFrom();
	}

	@Override
	protected Integer doInBackground() throws Exception {
		if (task.getTotalSize() == 0) {
			return (int) currentPart;
		}

		long counter = 0;

		byte[] buffer = new byte[1024 * 1024];

		BufferedInputStream input = new BufferedInputStream(new FileInputStream(task.getInputFile()));
		File outputFile = getPartFile(currentPart);
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
		try {
			input.skip(task.getFirstByteOffset());

			int length;
			input: while ((length = input.read(buffer)) != -1 && !isCancelled()) {
				int offset = 0;
				while (counter + length >= task.getPartSize()) {
					int tail = (int) (task.getPartSize() - counter);
					output.write(buffer, offset, tail);
					publish(tail);
					output.close();

					if (currentPart >= task.getPartFrom() + task.getPartNumber() - 1) {
						break input;
					}

					outputFile = getPartFile(++currentPart);
					output = new BufferedOutputStream(new FileOutputStream(outputFile));
					counter = 0;
					offset += tail;
					length -= tail;
				}
				output.write(buffer, offset, length);
				counter += length;
				publish(length);
			}
			if (isCancelled()) {
				output.close();
				outputFile.delete();
			}
		} catch (IOException ex) {
			try {
				output.close();
				outputFile.delete();
			} catch (IOException e) {
			}
			throw ex;
		} finally {
			try {
				input.close();
			} catch (IOException ex) {
			}
			try {
				output.close();
			} catch (IOException ex) {
			}
		}
		return (int) currentPart + 1;
	}

	public long getCurrentPart() {
		return currentPart;
	}

	private File getPartFile(long partNum) {
		return new File(task.getOutputDir(), task.getInputFile().getName() + "." + new DecimalFormat("000").format(partNum));
	}

	@Override
	protected void process(List<Integer> chunks) {
		for (long size : chunks) {
			total += size;
		}
		double procent = ((double) total) / ((double) (task.getTotalSize()));
		if (!isCancelled()) {
			setProgress((int) (procent * 100.0));
		}
	}

}
