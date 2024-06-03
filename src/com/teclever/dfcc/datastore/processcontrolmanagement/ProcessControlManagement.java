package com.teclever.dfcc.datastore.processcontrolmanagement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.terminalmanagement.DriverManagement;
import com.teclever.utils.ProcessControl;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
public class ProcessControlManagement {
	
    public enum LoadMode {
        STARTUP,
        SWITCH
    }
    private BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(1000000);
    private BlockingQueue<String> aitess1BQueue = new ArrayBlockingQueue<>(100);
    private Thread loadDriverManagementThread;
    private Thread aitess1ManagementThread;
    private String currentLoadedDriver = null;
    private ProcessControl loadDriverProcessController;
    
    
    
    public  DriverCardDetailsResponse loadDriver(String command, String unloadCommand, int aitessId, LoadMode mode,WebEngine webEngine) {
        ProcessControlManagement pcm = new ProcessControlManagement();
        loadDriverProcessController = new ProcessControl(loadDriverBQueue);

        //pcm.startLoadDriverManagementThread();
        try {
            switch (mode) {
                case STARTUP:
                        System.out.println(command);
            			CompletableFuture<Void> launcherFuture1 = new CompletableFuture<>();
            		  	System.out.println("OK1 Before Launching Process");
                        loadDriverProcessController.LaunchingProcess(command,launcherFuture1);
                        
                        
                        launcherFuture1.thenRun(() -> {
            				//JUN-03
            				//handler1.ReadingProcess(null,true,webEngine);
                        	loadDriverProcessController.ReadingProcess();
            				new Thread(() -> {
            					try {
            						while (true) {
            	                        String cleanText;
            	                        String output = loadDriverBQueue.take();
            	                        cleanText = cleanOutput(output);
            	                        cleanText = cleanText.replaceAll("\\(B", "");
            	                        cleanText = cleanText.replaceAll("]104", "");
            	                        final String finalLine = cleanText;
            	                        System.out.println(finalLine);
            	                    }
            					} catch (InterruptedException e1) {
            						e1.printStackTrace();
            					}
            				}).start();
            			});
                        
                        
                        
                    	System.out.println("OK1 After Launching Process");
                    	
                    	//JUN-03
                    	//loadDriverProcessController.ReadingProcess(loadDriverBQueue,false,webEngine);
                        System.out.println("OK2 Before Reading Process");
//                    	loadDriverProcessController.ReadingProcess();
                        System.out.println("OK2 After Reading Process");
                        pcm.setCurrentLoadedDriver(command);
                    
//                    return pcm.validateDriverCardsFromQueue(aitessId);
                case SWITCH:
                    if (!pcm.isCurrentDriver(command)) {
                        // Cancel
                      	System.out.println("OK1 Before Writing Process");
                    	loadDriverProcessController.WritingProcess("cat loadDriverOutput.txt");
                      	System.out.println("OK1 After Writing Process");
//                    	loadDriverProcessController.WritingProcess("\u0003" + "\n");
//                        pcm.waitForExpectedOutput("STRING FOR DETECTING TERMINATE DRIVER PROCESS FINISHED ???");
                        //waiting time required or not? then only proceed for next command
                        // Unload
                        if (unloadCommand != null && !unloadCommand.isEmpty()) {
                        	loadDriverProcessController.WritingProcess(command);
                            pcm.waitForExpectedOutput("STRING FOR DETECTING UNLOAD DRIVER PROCESS FINISHED ???");
                        }
                        // Load
                        loadDriverProcessController.WritingProcess(command);
                        pcm.setCurrentLoadedDriver(command);
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("An error occurred while loading the driver: " + e.getMessage());
            e.printStackTrace();
        }
        //finally {
        //pcm.stopManagementThread();
        //}
        return null; // Return null if an error occurs
    }
    //Load AITESS
    public static void loadAitess(String aitessCommand)
    {
        ProcessControlManagement pcm = new ProcessControlManagement();
    	Aitess1ProcessControl aitess1ProcessControl = Aitess1ProcessControl.getInstance();
    	pcm.startAitess1ManagementThread();
        aitess1ProcessControl.launchAitess(aitessCommand);   	
    }
    //LOAD DRIVER
//    private void startLoadDriverManagementThread() {
//    	loadDriverManagementThread = new Thread(() -> {
//            try {
//                while (!Thread.currentThread().isInterrupted()) {
//                    String output = LoadDriverProcessControl.getInstance().getOutputFromQueue();
//                    loadDriverBQueue.put(output);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//    	loadDriverManagementThread.start();
//    }
    private void stopLoadDriverManagementThread() {
        if (loadDriverManagementThread != null) {
        	loadDriverManagementThread.interrupt();
        }
    }
    private void waitForExpectedOutput(String expectedOutput) throws InterruptedException {
        while (true) {
            String output = loadDriverBQueue.take();
            //System.out.println("Management Thread Output: " + output);
            if (output.contains(expectedOutput)) {
                break;
            }
        }
    }
    private boolean isCurrentDriver(String driverCommand) {
        return driverCommand.equals(currentLoadedDriver);
    }
    private void setCurrentLoadedDriver(String driverCommand) {
        this.currentLoadedDriver = driverCommand;
    }
    public DriverCardDetailsResponse validateDriverCardsFromQueue(int aitessId) {
        DriverManagement driverManagement = new DriverManagement();
        return driverManagement.validateDriverCardFromQueue(loadDriverBQueue, aitessId);
    }
    //AITESS 1
    public void startAitess1ManagementThread() {
    	aitess1ManagementThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String aitessOutput = Aitess1ProcessControl.getInstance().getAitessTerminalOutputFromQueue();
                    aitess1BQueue.put(aitessOutput);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    	aitess1ManagementThread.start();
    }
    public void stopAitess1ManagementThread() {
        if (aitess1ManagementThread != null) {
        	aitess1ManagementThread.interrupt();
        }
    }
//    private boolean isCurrentAitess1(String aitessCommand) {
//        return aitessCommand.equals(currentAitess1);
//    }
//
//    private void setCurrentAitess1(String aitessCommand) {
//        this.currentAitess1 = aitessCommand;
//    }
    public void waitForAitessExpectedOutput(String expectedOutput) throws InterruptedException {
        while (true) {
            String aitessTerminalOutput = aitess1BQueue.take();
            //System.out.println("Management Thread Output: " + output);
            if (aitessTerminalOutput.contains(expectedOutput)) {
                break;
            }
        }
    }
    
    private String cleanOutput(String output) {
		String regex1 = "\u001B\\[[;\\d]*[A-Za-z]|\\[\\??\\d*[A-Za-z]|\\u0007|\\u0008|"
				+ "\\u0009|\\u000B|\\u000C|\\u000D|\\u000E|\\u000F | \\p{Cntrl}|\\u001B\\(B | \\p{Cntrl}";
		Pattern pattern1 = Pattern.compile(regex1);
		Matcher matcher1 = pattern1.matcher(output);
		return matcher1.replaceAll("");
	}
    
}