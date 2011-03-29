package ru.belitsky.split;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -4355409599886686324L;

	private static final String[] DIMENSIONS = { "b", "kb", "mb", "gb", "tb" };

	private ResourceBundle localization;

	private JFileChooser inputFileChooser;

	private JTextField inputFileName;

	private JLabel inputFileInfo;

	private JFileChooser outputFolderChooser;

	private JTextField outputFolderName;

	private JLabel outputFolderInfo;

	private JProgressBar progressBar;

	private JButton runButton;

	private File inputFile;

	private File outputFolder;

	private JSpinner partSize;

	private JComboBox partSizeUnit;

	private JSpinner fromPart;

	private JSpinner partNumber;

	private SplitWorker worker;

	private boolean isRunning = false;

	public MainWindow() {
		localization = ResourceBundle.getBundle("ru.belitsky.split.resources.SplitLocalization");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle(localization.getString("title"));
		getContentPane().setLayout(new BorderLayout());

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
		center.add(createInputFilePanel());
		center.add(createOutputFolderPanel());
		center.add(createPartSizePanel());
		center.add(createPartCountPanel());
		center.add(Box.createVerticalGlue());
		getContentPane().add(center, BorderLayout.CENTER);

		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.PAGE_AXIS));
		south.add(createProgressBar());
		south.add(createButtons());
		getContentPane().add(south, BorderLayout.SOUTH);

		refreshInfo();
		pack();
	}

	private long calcPartSize() {
		int size = (Integer) partSize.getValue();
		int unit = partSizeUnit.getSelectedIndex();
		return (long) (size * Math.pow(1024, unit));
	}

	private JPanel createButtons() {
		runButton = new JButton(localization.getString("button.run"));
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isRunning) {
					split();
				} else {
					worker.cancel(false);
				}
			}
		});

		JButton exitButton = new JButton(localization.getString("button.exit"));
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isRunning) {
					worker.cancel(false);
				}
				MainWindow.this.dispose();
			}
		});

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));

		buttonsPanel.add(runButton);
		buttonsPanel.add(exitButton);

		return buttonsPanel;
	}

	private JPanel createInputFilePanel() {
		inputFileChooser = new JFileChooser();
		inputFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		inputFileName = new JTextField(20);
		inputFileName.setEditable(false);
		fixHeight(inputFileName);

		JButton inputFileBrowse = new JButton(localization.getString("inputFile.browse"));
		inputFileBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = inputFileChooser.showOpenDialog(MainWindow.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setSplitedFile(inputFileChooser.getSelectedFile());
				}
			}
		});

		inputFileInfo = new JLabel();

		JPanel inputFileBrowsePanel = new JPanel();
		inputFileBrowsePanel.setLayout(new BoxLayout(inputFileBrowsePanel, BoxLayout.LINE_AXIS));
		inputFileBrowsePanel.add(inputFileName);
		inputFileBrowsePanel.add(inputFileBrowse);

		JPanel inputFilePanel = new JPanel();
		inputFilePanel.setBorder(BorderFactory.createTitledBorder(localization.getString("inputFile.title")));
		inputFilePanel.setLayout(new BoxLayout(inputFilePanel, BoxLayout.PAGE_AXIS));
		inputFilePanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		inputFileBrowsePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputFilePanel.add(inputFileBrowsePanel);
		inputFileInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputFilePanel.add(inputFileInfo);

		return inputFilePanel;
	}

	private JPanel createOutputFolderPanel() {
		outputFolderChooser = new JFileChooser();
		outputFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		outputFolderName = new JTextField(20);
		outputFolderName.setEditable(false);
		fixHeight(outputFolderName);

		JButton outputFolderBrowse = new JButton(localization.getString("outputFolder.browse"));
		outputFolderBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = outputFolderChooser.showOpenDialog(MainWindow.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setOutputFolder(outputFolderChooser.getSelectedFile());
				}
			}
		});

		outputFolderInfo = new JLabel();

		JPanel outputFolderBrowsePanel = new JPanel();
		outputFolderBrowsePanel.setLayout(new BoxLayout(outputFolderBrowsePanel, BoxLayout.LINE_AXIS));
		outputFolderBrowsePanel.add(outputFolderName);
		outputFolderBrowsePanel.add(outputFolderBrowse);

		JPanel outputFolderPanel = new JPanel();
		outputFolderPanel.setBorder(BorderFactory.createTitledBorder(localization.getString("outputFolder.title")));
		outputFolderPanel.setLayout(new BoxLayout(outputFolderPanel, BoxLayout.PAGE_AXIS));
		outputFolderPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		outputFolderBrowsePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		outputFolderPanel.add(outputFolderBrowsePanel);
		outputFolderInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		outputFolderPanel.add(outputFolderInfo);

		return outputFolderPanel;
	}

	private JPanel createPartCountPanel() {
		SpinnerNumberModel fromPartModel = new SpinnerNumberModel();
		fromPartModel.setMinimum(new Integer(1));
		fromPartModel.setValue(new Integer(1));
		fromPart = new JSpinner(fromPartModel);
		fixHeight(fromPart);

		SpinnerNumberModel partNumberModel = new SpinnerNumberModel();
		partNumberModel.setMinimum(new Integer(1));
		partNumberModel.setValue(new Integer(1));
		partNumber = new JSpinner(partNumberModel);
		fixHeight(partNumber);

		JButton recalc = new JButton(localization.getString("button.calc"));
		recalc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((inputFile != null) && inputFile.exists()) {
					long fSize = inputFile.length();
					long pSize = calcPartSize();
					long number = fSize / pSize;
					if (fSize % pSize != 0) {
						number++;
					}
					number -= (Integer) fromPart.getValue() - 1;
					partNumber.setValue(Math.max((int) number, 1));
				}
			}
		});

		JPanel partCountPanel = new JPanel();
		partCountPanel.setBorder(BorderFactory.createTitledBorder(localization.getString("partCount.title")));
		partCountPanel.setLayout(new BoxLayout(partCountPanel, BoxLayout.LINE_AXIS));
		partCountPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		JPanel fromPartPanel = new JPanel();
		fromPartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		fromPartPanel.setLayout(new BoxLayout(fromPartPanel, BoxLayout.LINE_AXIS));
		fromPartPanel.add(new JLabel(localization.getString("partCount.from")));
		fromPartPanel.add(fromPart);

		JPanel partNumberPanel = new JPanel();
		partNumberPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		partNumberPanel.setLayout(new BoxLayout(partNumberPanel, BoxLayout.LINE_AXIS));
		partNumberPanel.add(new JLabel(localization.getString("partCount.number")));
		partNumberPanel.add(partNumber);
		partNumberPanel.add(recalc);

		partCountPanel.add(fromPartPanel);
		partCountPanel.add(partNumberPanel);

		return partCountPanel;
	}

	private JPanel createPartSizePanel() {
		SpinnerNumberModel model = new SpinnerNumberModel();
		model.setMinimum(new Integer(1));
		model.setValue(new Integer(4));
		partSize = new JSpinner(model);
		fixHeight(partSize);

		String[] units = new String[DIMENSIONS.length];
		for (int i = 0; i < DIMENSIONS.length; i++) {
			units[i] = localization.getString("size.long." + DIMENSIONS[i]);
		}
		partSizeUnit = new JComboBox(units);
		partSizeUnit.setSelectedIndex(3);
		fixHeight(partSizeUnit, partSize.getMaximumSize().height);

		JPanel partSizePanel = new JPanel();
		partSizePanel.setBorder(BorderFactory.createTitledBorder(localization.getString("partSize.title")));
		partSizePanel.setLayout(new BoxLayout(partSizePanel, BoxLayout.LINE_AXIS));
		partSizePanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		partSizePanel.add(new JLabel(localization.getString("partSize.size")));
		partSizePanel.add(partSize);
		partSizePanel.add(partSizeUnit);

		return partSizePanel;
	}

	private JPanel createProgressBar() {
		progressBar = new JProgressBar(0, 100);

		JPanel progressBarPanel = new JPanel();
		progressBarPanel.setLayout(new BoxLayout(progressBarPanel, BoxLayout.PAGE_AXIS));

		progressBarPanel.add(progressBar);

		return progressBarPanel;
	}

	private void fixHeight(JComponent comp) {
		fixHeight(comp, comp.getPreferredSize().height);
	}

	private void fixHeight(JComponent comp, int height) {
		comp.setMaximumSize(new Dimension(comp.getMaximumSize().width, height));
	}

	private void refreshInfo() {
		if ((inputFile != null) && inputFile.exists()) {
			inputFileInfo.setText(localization.getString("inputFile.info") + sizeToString(inputFile.length()));
		} else {
			inputFileInfo.setText(localization.getString("inputFile.info") + localization.getString("inputFile.info.unknown"));
		}
		if ((outputFolder != null) && outputFolder.exists()) {
			outputFolderInfo.setText(localization.getString("outputFolder.info") + sizeToString(outputFolder.getFreeSpace()));
		} else {
			outputFolderInfo.setText(localization.getString("outputFolder.info") + localization.getString("outputFolder.info.unknown"));
		}
	}

	public void setOutputFolder(File folder) {
		if (folder.exists()) {
			outputFolder = folder;
			outputFolderChooser.setSelectedFile(outputFolder);
			outputFolderName.setText(outputFolder.getName());
			refreshInfo();
		}
	}

	public void setOutputFolder(String folder) {
		setOutputFolder(new File(folder));
	}

	public void setSplitedFile(File file) {
		if (file.exists()) {
			inputFile = file;
			inputFileChooser.setSelectedFile(inputFile);
			inputFileName.setText(inputFile.getName());
			refreshInfo();
		}
	}

	public void setSplitedFile(String file) {
		setSplitedFile(new File(file));
	}

	private void showError(String errorKey) {
		showErrorDialog(localization.getString(errorKey));
	}

	private void showError(String errorKey, String message) {
		showErrorDialog(localization.getString(errorKey) + message);
	}

	private void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(this, message, localization.getString("error.title"), JOptionPane.ERROR_MESSAGE);
	}

	private String sizeToString(long size) {
		double convertSize = size;
		int dim = 0;
		while (size / 1024 > 0) {
			size /= 1024;
			convertSize /= 1024.0;
			dim++;
		}

		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(2);
		return format.format(convertSize) + " " + localization.getString("size.short." + DIMENSIONS[dim]);
	}

	private void split() {
		if (inputFile == null) {
			showError("error.inputFile.null");
			return;
		} else if (!inputFile.exists()) {
			showError("error.inputFile.notExists");
			return;
		}

		if (outputFolder == null) {
			showError("error.outputFolder.null");
			return;
		} else if (!outputFolder.exists()) {
			showError("error.outputFolder.notExists");
			return;
		}

		Task task = new Task(inputFile, outputFolder);
		task.setPartSize(calcPartSize());
		task.setPartFrom((Integer) fromPart.getValue());
		task.setPartNumber((Integer) partNumber.getValue());

		if (outputFolder.getFreeSpace() <= task.getPartSize()) {
			showError("error.partSize.tooBig");
			return;
		}
		if (inputFile.length() == task.getFirstByteOffset()) {
			showError("error.partCount.nothing");
			return;
		}

		progressBar.setValue(0);
		worker = new SplitWorker(task) {
			@Override
			protected void done() {
				try {
					fromPart.setValue(get());
				} catch (CancellationException e) {
					progressBar.setValue(0);
					fromPart.setValue((int) getCurrentPart());
				} catch (Exception e) {
					showError("error.task.exception", e.getLocalizedMessage());
					progressBar.setValue(0);
					fromPart.setValue((int) getCurrentPart());
				}
				refreshInfo();
				isRunning = false;
				runButton.setText(localization.getString("button.run"));
			}
		};
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					progressBar.setValue((Integer) evt.getNewValue());
				}
			}
		});

		worker.execute();

		isRunning = true;
		runButton.setText(localization.getString("button.cancel"));
	}

}
