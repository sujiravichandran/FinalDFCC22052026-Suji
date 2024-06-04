package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.teclever.utils.ProcessControl;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;

public class LoadDriverProcessControl {

	private ProcessControl loadDriverProcessControl;
	private BlockingQueue<String> loadDriverQueue = new ArrayBlockingQueue<>(100);
	private Thread outputProcessingThread;

	public LoadDriverProcessControl() {


		loadDriverProcessControl = new ProcessControl(loadDriverQueue);
		System.out.println("LOAD DRIVER PC anuj:::" + loadDriverProcessControl);
	}


	void launchLoadDriver(String command, WebEngine webEngine) {
		CompletableFuture<Void> launcherFuture = new CompletableFuture<>();
		System.out.println("loadDriverProcessControl PC anuj:::" + loadDriverProcessControl);
		loadDriverProcessControl.LaunchingProcess(command, launcherFuture);
		launcherFuture.thenRun(() -> {
			loadDriverProcessControl.ReadingProcess();
			outputProcessingThread = new Thread(() -> {
                try {
                    while (true) {
                        String cleanText;
                        String output = loadDriverQueue.take();
                        cleanText = cleanOutput(output);
                        cleanText = cleanText.replaceAll("\\(B", "");
                        cleanText = cleanText.replaceAll("]104", "");
                        final String finalLine = cleanText;
                        System.out.println(finalLine);

                    }
                });
                outputProcessingThread.start();
                isTerminalLaunched = true; // Set the flag to true after launching terminal
                isDriverLoaded = true; // Set the flag to true after launching driver
            });

            outputProcessingThread.start();
		});
	}

	void terminateLoadDriver(String command) {
		loadDriverProcessControl.WritingProcess("\u0003" + "\n"); // capturing C signal
		loadDriverProcessControl.WritingProcess(command + "\n");
		loadDriverProcessControl.stopProcesses();
		if (outputProcessingThread != null) {
			outputProcessingThread.interrupt();
		}
	}

	void write(String command) {
		System.out.println("write PC anuj:::" + loadDriverProcessControl);

		loadDriverProcessControl.WritingProcess(command + "\n");

	}

	private String cleanOutput(String output) {
		String regex1 = "\u001B\\[[;\\d]*[A-Za-z]|\\[\\??\\d*[A-Za-z]|\\u0007|\\u0008|"
				+ "\\u0009|\\u000B|\\u000C|\\u000D|\\u000E|\\u000F | \\p{Cntrl}|\\u001B\\(B | \\p{Cntrl}";
		Pattern pattern1 = Pattern.compile(regex1);
		Matcher matcher1 = pattern1.matcher(output);
		return matcher1.replaceAll("");
	}

	void read(BlockingQueue<String> loadDriverQueue, boolean isAitess1, WebEngine webEngine) {
		System.out.println("read PC anuj:::" + loadDriverProcessControl);
		//JUN-03
		//	loadDriverProcessControl.ReadingProcess(loadDriverQueue, false, webEngine);
		loadDriverProcessControl.ReadingProcess();
		
		outputProcessingThread = new Thread(() -> {
			try {
				while (true) {
					String cleanText;
					String output = loadDriverQueue.take();
					cleanText = cleanOutput(output);
					cleanText = cleanText.replaceAll("\\(B", "");
					cleanText = cleanText.replaceAll("]104", "");
					final String finalLine = cleanText;
					System.out.println(finalLine);
				}
			} catch (InterruptedException e1) {
				// Thread interrupted, stopping gracefully
			}
		});
		outputProcessingThread.start();
	}

}