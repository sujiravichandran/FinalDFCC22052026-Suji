package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.terminalmanagement.DriverManagement;

public class ProcessControlManagement {

    public enum LoadMode {
        STARTUP,
        SWITCH
    }

    private BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(100);
    private BlockingQueue<String> aitess1BQueue = new ArrayBlockingQueue<>(100);
    private Thread loadDriverManagementThread;
    private Thread aitess1ManagementThread;
    private String currentLoadedDriver = null;

    public static DriverCardDetailsResponse loadDriver(String command, String unloadCommand, int aitessId, LoadMode mode) {
        ProcessControlManagement pcm = new ProcessControlManagement();
        LoadDriverProcessControl loadDriverProcessControl = LoadDriverProcessControl.getInstance();
        pcm.startLoadDriverManagementThread();

        try {
            switch (mode) {
                case STARTUP:
                    if (!loadDriverProcessControl.isDriverLoaded() || !pcm.isCurrentDriver(command)) {
                        loadDriverProcessControl.launchLoadDriver(command);
                        loadDriverProcessControl.write("cat loadDriverOutput.txt");
                        pcm.setCurrentLoadedDriver(command);
                    }
                    return pcm.validateDriverCardsFromQueue(aitessId);

                case SWITCH:
                    if (!pcm.isCurrentDriver(command)) {
                        // Cancel
                        loadDriverProcessControl.terminateLoadDriver("\u0003" + "\n");
                        pcm.waitForExpectedOutput("STRING FOR DETECTING TERMINATE DRIVER PROCESS FINISHED ???");
                        
                        //waiting time required or not? then only proceed for next command
                                               
                        // Unload
                        if (unloadCommand != null && !unloadCommand.isEmpty()) {
                            loadDriverProcessControl.write(unloadCommand);
                            pcm.waitForExpectedOutput("STRING FOR DETECTING UNLOAD DRIVER PROCESS FINISHED ???");
                        }

                        // Load
                        loadDriverProcessControl.write(command);
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
    private void startLoadDriverManagementThread() {
    	loadDriverManagementThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String output = LoadDriverProcessControl.getInstance().getOutputFromQueue();
                    loadDriverBQueue.put(output);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    	loadDriverManagementThread.start();
    }

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
    
    
    
}




