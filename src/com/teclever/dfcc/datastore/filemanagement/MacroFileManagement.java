package com.teclever.dfcc.datastore.filemanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.Macro;
import com.teclever.datastore.service.MacroService;
import com.teclever.dfcc.datastore.dto.MacroDto;

public class MacroFileManagement {
	
	
	 //API : GET ALL THE MACRO LIST BASED ON RUN PATH MASTER ID
    public static List<MacroDto> getAllMacrosByRunPathMasterId(String runPathMasterId) {
        List<MacroDto> macroDtos = new ArrayList<>();
        try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Macro> macros = session.createQuery("FROM Macro WHERE runPathMasterId = :runPathMasterId AND deleteStatus = false", Macro.class)
                                        .setParameter("runPathMasterId", runPathMasterId)
                                        .getResultList();

            for (Macro macro : macros) {
                MacroDto macroDto = new MacroDto();
                macroDto.setMacroId(macro.getMacroId()); 
                macroDto.setMacroName(macro.getMacroName());
                macroDto.setFileName(macro.getFileName()); 
                macroDto.setRunPathMasterId(macro.getRunPathMasterId());
                macroDtos.add(macroDto);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macroDtos;
    }
	
	
	
	// GET MACRONAME WITH THEIR FILE NAME AND SAVE TO DB
    public static List<MacroDto> saveMacroNames(List<String> fileNamePaths, String runPathMasterId) {
        List<MacroDto> macroNames = new ArrayList<>();
        MacroService macroService = new MacroService();

        // Mark previous macro rows as deleted before adding new ones
        markPreviousMacroRowsAsDeleted(runPathMasterId);

        for (String fileName : fileNamePaths) {
            MacroFileParser macroParser = new MacroFileParser();
            try {
                List<String> parsedMacroNames = macroParser.parse(fileName);
                for (String macroName : parsedMacroNames) {
                    String filename = Paths.get(fileName).getFileName().toString();

                    Response response = macroService.saveMacroToDatabase(macroName, filename, runPathMasterId);
                    if (response.getResponseCode() == 1) {
                        MacroDto macroDto = new MacroDto(macroName, filename, runPathMasterId);
                        macroNames.add(macroDto);
                    } else {
                        System.err.println("Failed to save macro: " + macroName + " Error: " + response.getResponseMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return macroNames;
    }

    // Method to mark previous macro rows as deleted
    private static void markPreviousMacroRowsAsDeleted(String runPathMasterId) {
        try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Query the database to find macros associated with the given runPathMasterId
            List<Macro> macros = session.createQuery("FROM Macro WHERE runPathMasterId = :runPathMasterId", Macro.class)
                                        .setParameter("runPathMasterId", runPathMasterId)
                                        .getResultList();

            // Mark all macros associated with the runPathMasterId as deleted
            for (Macro macro : macros) {
                macro.setDeleteStatus(true); 
                session.merge(macro);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<String> fetchMacroFilePathsDoubleSlash(List<String> macroFilePaths) throws IOException {
        List<String> filePaths = new ArrayList<>();
        for (String filePath : macroFilePaths) {
            try {
                Path dir = Paths.get(filePath);
                Files.walk(dir)
                     .filter(Files::isRegularFile)
                     .map(Path::toString)
                     .map(path -> path.replace("\\", "\\\\"))
                     .forEach(filePaths::add);
            } catch (IOException e) {
                System.err.println("Error processing file path: " + filePath);
                e.printStackTrace();
            }
        }
        return filePaths;
    }

    }
