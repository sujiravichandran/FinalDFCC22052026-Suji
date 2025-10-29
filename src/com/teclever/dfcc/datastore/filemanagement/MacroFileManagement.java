package com.teclever.dfcc.datastore.filemanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.Macro;
import com.teclever.datastore.service.MacroService;
import com.teclever.dfcc.datastore.dto.MacroDto;

public class MacroFileManagement {
		
	//GET ALL MACROS 
    public static List<MacroDto> getAllMacros(String runConfigId) {
        List<MacroDto> macroDtos = new ArrayList<>();
        try {
            MacroService macroService = new MacroService();
            List<String> runPathMasterIds = macroService.getRunPathMasterIdsByRunConfigId(runConfigId);

            // Fetch macros based on runPathMasterIds and deleteStatus
            for (String runPathMasterId : runPathMasterIds) {
                List<Macro> macros = macroService.getAllMacrosByRunPathMasterId(runPathMasterId);
                for (Macro macro : macros) {
                    MacroDto macroDto = new MacroDto();
                    macroDto.setMacroId(macro.getMacroId());
                    macroDto.setMacroName(macro.getMacroName());
                    macroDto.setFileName(macro.getFileName());
                    macroDto.setRunPathMasterId(macro.getRunPathMasterId());
                    macroDtos.add(macroDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macroDtos;
    }


	
	// GET MACRONAME WITH THEIR FILE NAME AND SAVE TO DB : old
//    public static List<MacroDto> saveMacroNames(List<String> fileNamePaths, String runPathMasterId) {
//        List<MacroDto> macroNames = new ArrayList<>();
//        MacroService macroService = new MacroService();
//
//        // Mark previous macro rows as deleted before adding new ones
//        markPreviousMacroRowsAsDeleted(runPathMasterId);
//
//        for (String fileName : fileNamePaths) {
//            MacroFileParser macroParser = new MacroFileParser();
//            try {
//                List<String> parsedMacroNames = macroParser.parse(fileName);
//                if(!(parsedMacroNames.size()>0)) {
//                	
//                }
//                for (String macroName : parsedMacroNames) {
//                    String filename = Paths.get(fileName).toString();
//                    macroService.saveMacroToDatabase("macroName", Paths.get(fileName).toString(), runPathMasterId);
//                    Response response = macroService.saveMacroToDatabase(macroName, filename, runPathMasterId);
//                    if (response.getResponseCode() == 1) {
//                        MacroDto macroDto = new MacroDto(macroName, filename, runPathMasterId);
//                        macroNames.add(macroDto);
//                    } else {
//                        System.err.println("Failed to save macro: " + macroName + " Error: " + response.getResponseMessage());
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return macroNames;
//    }
    
	// GET MACRONAME WITH THEIR FILE NAME AND SAVE TO DB : new
    
    public static List<MacroDto> saveMacroNames(List<String> fileNamePaths, String runPathMasterId) {
        List<MacroDto> macroNames = new ArrayList<>();
        MacroService macroService = new MacroService();
        Set<String> processedFiles = new HashSet<>(); 

        // Mark previous macro rows as deleted before adding new ones
        markPreviousMacroRowsAsDeleted(runPathMasterId);

        for (String fileName : fileNamePaths) {
            processMacroFileRecursively(fileName, runPathMasterId, macroNames, macroService, processedFiles);
        }

        return macroNames;
    }

   
    private static void processMacroFileRecursively(
            String filePath,
            String runPathMasterId,
            List<MacroDto> macroNames,
            MacroService macroService,
            Set<String> processedFiles) {

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();

            if (!processedFiles.add(path.toString())) {
                return;
            }

            MacroFileParser macroParser = new MacroFileParser();
            List<String> parsedLines = macroParser.parse(filePath);

            if (parsedLines == null || parsedLines.isEmpty()) {
                System.err.println("No macros or paths found in file: " + filePath);
                return;
            }

            for (String line : parsedLines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                Path nestedPath = Paths.get(trimmed);
                if (Files.exists(nestedPath) && Files.isRegularFile(nestedPath)) {
                    processMacroFileRecursively(nestedPath.toString(), runPathMasterId, macroNames, macroService, processedFiles);
                }
                else {
                    Response response = macroService.saveMacroToDatabase(trimmed, path.toString(), runPathMasterId);
                    if (response.getResponseCode() == 1) {
                        MacroDto macroDto = new MacroDto(trimmed, path.toString(), runPathMasterId);
                        macroNames.add(macroDto);
                    } else {
                        System.err.println("Failed to save macro: " + trimmed +
                                           " | File: " + path +
                                           " | Error: " + response.getResponseMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading or parsing file: " + filePath + " — " + e.getMessage());
        }
    }

    
    public static List<MacroDto> saveMacroNamesForCustomFiles(List<String> fileNamePaths, String runPathMasterId) {
        List<MacroDto> macroNames = new ArrayList<>();
        MacroService macroService = new MacroService();

        // Mark previous macro rows as deleted before adding new ones
        //markPreviousMacroRowsAsDeleted(runPathMasterId);

        for (String fileName : fileNamePaths) {
            MacroFileParser macroParser = new MacroFileParser();
            try {
            	macroService.saveMacroToDatabase("--", fileName, runPathMasterId);
                List<String> parsedMacroNames = macroParser.parse(fileName);
                for (String macroName : parsedMacroNames) {
                	
                    String filename = Paths.get(fileName).toString();

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

    public static void markPreviousMacroRowsAsDeleted(String runPathMasterId) {
    	MacroService macroService = new MacroService();
        List<Macro> macros = macroService.getMacrosByRunPathMasterId(runPathMasterId);

        try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

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
                     .filter(path -> path.toString().endsWith(".mac")) // Filter only .mac files
                     .map(Path::toString)
                     .map(path -> path.replace("\\", "\\\\")) // Escape backslashes
                     .forEach(filePaths::add);
            } catch (IOException e) {
                System.err.println("Error processing file path: " + filePath);
                e.printStackTrace();
            }
        }
        return filePaths;
    }
    
    public static List<MacroDto> getAllMacrobyRunPathMassterId(String runPathMasterId){
    	List<MacroDto> macroDtos = new ArrayList<>();
        try {
            MacroService macroService = new MacroService();
    		 List<Macro> macros = macroService.getAllMacrosByRunPathMasterId(runPathMasterId);
             for (Macro macro : macros) {
                 MacroDto macroDto = new MacroDto();
                 macroDto.setMacroId(macro.getMacroId());
                 macroDto.setMacroName(macro.getMacroName());
                 macroDto.setFileName(macro.getFileName());
                 macroDto.setRunPathMasterId(macro.getRunPathMasterId());
                 macroDtos.add(macroDto);
             }
             return macroDtos;
		} catch (Exception e) {
			throw e;
		}
    }

    }
