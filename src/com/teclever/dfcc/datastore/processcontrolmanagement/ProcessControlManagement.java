package com.teclever.dfcc.datastore.processcontrolmanagement;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.datastore.entities.CardDetails;
import com.teclever.dfcc.datastore.dto.DriverCard;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.terminalmanagement.DriverManagement;
import com.teclever.utils.ProcessControl;
import javafx.scene.web.WebEngine;
public class ProcessControlManagement {
	
    public enum LoadMode {
        STARTUP,
        SWITCH
    }
    
    
    private BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(10000);
    private BlockingQueue<String> aitess1BQueue = new ArrayBlockingQueue<>(100);
    private static String temporaryFilePath ="/home/teclever/Documents/output.txt" ;

    private Thread loadDriverLaunchingThread;
	private Thread outputProcessingThread;
	
    private String currentLoadedDriver = null;
    
    private ProcessControl loadDriverProcessController;
    
	CompletableFuture<Void> launcherFuture = new CompletableFuture<>();

    public ProcessControlManagement()
    {
    	loadDriverProcessController = new ProcessControl(loadDriverBQueue);
    }
	
    public  DriverCardDetailsResponse loadDriver(String command, String unloadCommand, int aitessId, LoadMode mode) {
        ProcessControlManagement pcm = new ProcessControlManagement();

        try {
            switch (mode) {
                case STARTUP:
                	
                	loadDriverLaunchingThread = new Thread(()-> {			
                		loadDriverProcessController.LaunchingProcess(command, launcherFuture);
            			launcherFuture.thenRun(() -> {
            				loadDriverProcessController.ReadingProcess();
            				outputProcessingThread = new Thread(() -> {
            	                try (BufferedWriter writer = new BufferedWriter(new FileWriter(temporaryFilePath, true))) {
            	                    while (true) {
            	                        String cleanText;
            	                        String output = loadDriverBQueue.take();
            	                        cleanText = cleanOutput(output);
            	                        cleanText = cleanText.replaceAll("\\(B", "");
            	                        cleanText = cleanText.replaceAll("]104", "");
            	                        final String finalLine = cleanText;
            	                        
            	                        System.out.println("LOAD DRIVER: " +finalLine);
            	                        writer.write(finalLine);
            	                        writer.newLine(); 
            	                        writer.flush();
            	                        
            	                        // Check if the output contains the specified string
            	                        if (cleanText.contains("Starting AETS RT Scheduler")) {
            	                            System.out.println("Found the stopping point. Stopping writing to the file.");
            	                            break; 
            	                        }
            	                    }
            	                } catch (InterruptedException e1) {
            	                } catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
            	            });
            	            outputProcessingThread.start();
            			});			
            		});
            		loadDriverLaunchingThread.start();		
            		//this writing for checking purpose
            		launcherFuture.thenRun(()-> loadDriverProcessController.WritingProcess("cat loadDriverOutput.txt"+"\n"));               	
                    pcm.setCurrentLoadedDriver(command);
                    DriverCardDetailsResponse response = pcm.validateDriverCards(aitessId);
                    return response;
                    
                    
                case SWITCH:
                    if (!pcm.isCurrentDriver(command)) {
                        // Cancel
                		launcherFuture.thenRun(()-> loadDriverProcessController.WritingProcess("\u0003" + "\n"));               	

                        // Unload
                        if (unloadCommand != null && !unloadCommand.isEmpty()) {
                        	loadDriverProcessController.WritingProcess(unloadCommand);
                        }    
                        
                        // Load
                		launcherFuture.thenRun(()-> loadDriverProcessController.WritingProcess(command));               	
                        pcm.setCurrentLoadedDriver(command);
                    }
                    break;
            }
         
        } catch (Exception e) {
            System.err.println("An error occurred while loading the driver: " + e.getMessage());
            e.printStackTrace();
        }
       
        return null; 
    }
    
    
    //Load AITESS
    public static void loadAitess(String aitessCommand)
    {
        ProcessControlManagement pcm = new ProcessControlManagement();
    	Aitess1ProcessControl aitess1ProcessControl = Aitess1ProcessControl.getInstance();
    	//pcm.startAitess1ManagementThread();
        aitess1ProcessControl.launchAitess(aitessCommand);   	
    }

    
    private void stopLoadDriverLaunchingThread() {
        if (loadDriverLaunchingThread != null) {
        	loadDriverLaunchingThread.interrupt();
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
    public DriverCardDetailsResponse validateDriverCards(int aitessId) {
        DriverManagement driverManagement = new DriverManagement();
        DriverCardDetailsResponse response = driverManagement.validateDriverCard(temporaryFilePath, aitessId);
        
        if (response != null && response.getDriverCardDetails() != null) {
            for (DriverCard cardDetails : response.getDriverCardDetails()) {
                System.out.println("Card Details:");
                System.out.println("Card Name: " + cardDetails.getCardName());
                System.out.println("Number of Cards: " + cardDetails.getTotalNumberOfCards());
                System.out.println("Message:  " + cardDetails.getMsg());
                System.out.println("--------");
            }
        } else {
            System.out.println("No card details found.");
        }
        return driverManagement.validateDriverCard(temporaryFilePath, aitessId);
    }
    
    //AITESS 1
//    public void startAitess1ManagementThread() {
//    	aitess1ManagementThread = new Thread(() -> {
//            try {
//                while (!Thread.currentThread().isInterrupted()) {
//                    String aitessOutput = Aitess1ProcessControl.getInstance().getAitessTerminalOutputFromQueue();
//                    aitess1BQueue.put(aitessOutput);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//    	aitess1ManagementThread.start();
//    }
//    public void stopAitess1ManagementThread() {
//        if (aitess1ManagementThread != null) {
//        	aitess1ManagementThread.interrupt();
//        }
//    }
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