package com.teclever.dfcc.datastore.filemanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.teclever.dfcc.stateMachine.StateMachine;

public class SessionFileManagement {

    private Path mark1Directory;
    private Path mark1aDirectory;
    private Path mark2Directory;
    private Path dfccSerialNoDirectory;
    private Path sessionDirectory;
    private Path currentOutputFolder; // Track current output folder for session

    public SessionFileManagement() {
        currentOutputFolder = null;
    }

    // Create session folders
    public void createSessionFolders(String uutType, String dfccSerialNumber, String sessionName, List<List<String>> levelSets) {
        try {
        	String currentDirectory = new File(
        			SessionFileManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

           // StateMachine.setHomelocation(Paths.get("C:\\testingSession"));
            StateMachine.setHomelocation(Paths.get(currentDirectory));
            
            System.out.println(currentDirectory);
            mark1Directory = StateMachine.getHomelocation().resolve("MK1");
            mark1aDirectory = StateMachine.getHomelocation().resolve("MK1-A");
            mark2Directory = StateMachine.getHomelocation().resolve("MK2");

            if (Files.notExists(mark1Directory)) {
                Files.createDirectory(mark1Directory);
            }
            if (Files.notExists(mark1aDirectory)) {
                Files.createDirectory(mark1aDirectory);
            }
            if (Files.notExists(mark2Directory)) {
                Files.createDirectory(mark2Directory);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            dfccSerialNoDirectory = null;
            switch (uutType) {
                case "MK1":
                    dfccSerialNoDirectory = mark1Directory.resolve(dfccSerialNumber);
                    break;
                case "MK1-A":
                    dfccSerialNoDirectory = mark1aDirectory.resolve(dfccSerialNumber);
                    break;
                case "MK2":
                    dfccSerialNoDirectory = mark2Directory.resolve(dfccSerialNumber);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid uutType: " + uutType);
            }

            if (dfccSerialNoDirectory != null && !Files.exists(dfccSerialNoDirectory)) {
                Files.createDirectories(dfccSerialNoDirectory);
            } else {
                System.out.println("dfcc serial number folder already exists: " + dfccSerialNoDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            sessionDirectory = null;
            switch (uutType) {
                case "MK1":
                case "MK1-A":
                case "MK2":
                    //sessionDirectory = dfccSerialNoDirectory.resolve(sessionName + "_" + dfccSerialNumber + "_" + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()));
                    sessionDirectory = dfccSerialNoDirectory.resolve(sessionName);
                    
                    break;
                default:
                    throw new IllegalArgumentException("Invalid uutType: " + uutType);
            }

            if (sessionDirectory != null && !Files.exists(sessionDirectory)) {
                Files.createDirectories(sessionDirectory);
                Files.createDirectories(sessionDirectory.resolve("upload"));
                Files.createDirectories(sessionDirectory.resolve("datapack"));
                Files.createDirectories(sessionDirectory.resolve("report"));
                Files.createDirectories(sessionDirectory.resolve("Advance Testing"));
                
                // Creating four more folders inside "Advance Testing"
                Path advanceTestingDirectory = sessionDirectory.resolve("Advance Testing");
                Files.createDirectories(advanceTestingDirectory.resolve("HWATP HSI Testing"));
                Files.createDirectories(advanceTestingDirectory.resolve("Interface Testing"));
                Files.createDirectories(advanceTestingDirectory.resolve("Custom Testing 01"));
                Files.createDirectories(advanceTestingDirectory.resolve("Custom Testing 02"));
                
                // Create multiple sets of levels
                for (List<String> levels : levelSets) {
                    createLevel(sessionDirectory, levels, 0);
                }
            } else {
                System.out.println("Session folder already exists: " + sessionDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createLevel(Path basePath, List<String> levels, int index) throws IOException {
        Path currentPath = basePath.resolve(levels.get(index));
        Files.createDirectories(currentPath);

        // Track output folder path for the current session
        if (index == levels.size() - 1) {
            currentOutputFolder = currentPath;
            Files.createDirectories(currentOutputFolder); // Create output folder for this stage
        }

        // Recursively create sub-levels and stages
        if (index < levels.size() - 1) {
            createLevel(currentPath, levels, index + 1);
        }
    }

    public void copyFilesToOutputFolder(Path sourceFile) {
        try {
            if (currentOutputFolder != null && Files.exists(currentOutputFolder)) {
                Path destinationFile = currentOutputFolder.resolve(sourceFile.getFileName());
                Files.copy(sourceFile, destinationFile);
                System.out.println("Copied file " + sourceFile.getFileName() + " to " + destinationFile);
            } else {
                System.out.println("Output folder does not exist for current session.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}