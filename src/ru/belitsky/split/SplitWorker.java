package ru.belitsky.split;

import java.util.List;

import javax.swing.SwingWorker;

public class SplitWorker extends SwingWorker<Integer, Long> {

	private final Task task;

	private long counter;

	public SplitWorker(Task task) {
		this.task = task;
		this.counter = 0;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		long size = task.getLastByteOffset() - task.getFirstByteOffset();
		while (size > 4000 && !isCancelled()) {
			publish(4000L);
			size -= 4000;
			Thread.sleep(100);
		}
		if (!isCancelled()) {
			publish(size);
		}
		return 1;
	}

	@Override
	protected void process(List<Long> chunks) {
		for (long size : chunks) {
			counter += size;
		}
		double procent = ((double) counter) / ((double) (task.getLastByteOffset() - task.getFirstByteOffset()));
		setProgress((int) (procent * 100.0));
	}

}
