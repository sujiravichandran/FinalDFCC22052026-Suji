package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.hibernate.internal.build.AllowSysOut;

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


	
	// GET MACRONAME WITH THEIR FILE NAME AND SAVE TO DB
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
    
    public static List<MacroDto> saveMacroNames(
            List<String> fileNamePaths,
            String runPathMasterId) {

        // Step 1: Resolve ALL macro files (direct + nested)
        List<String> allMacroFilePaths =
                fetchMacroFilePathsDoubleSlash(fileNamePaths);

        MacroService macroService = new MacroService();

        // Step 2: Save file references first (placeholder like Symbols)
        for (String filePath : allMacroFilePaths) {
            macroService.saveMacroToDatabase(
                    "--",          // macroName placeholder
                    filePath,      // fileName
                    runPathMasterId
            );
        }

        // Step 3: Parse MACRONAME entries (NO filtering)
        List<MacroDto> macros = new ArrayList<>();
        MacroFileParser parser = new MacroFileParser();

        for (String filePath : allMacroFilePaths) {
            try {
                List<String> macroNames = parser.parse(filePath);

                for (String macroName : macroNames) {
                    MacroDto dto = new MacroDto();
                    dto.setMacroName(macroName);
                    dto.setFileName(filePath);
                    dto.setRunPathMasterId(runPathMasterId);

                    macros.add(dto); // ✅ ALWAYS add
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Step 4: Mark previous macros as deleted
        markPreviousMacroRowsAsDeleted(runPathMasterId);

        // Step 5: Save ALL parsed macros
        for (MacroDto macroDto : macros) {

            Response response = macroService.saveMacroToDatabase(
                    macroDto.getMacroName(),
                    macroDto.getFileName(),
                    runPathMasterId
            );

            if (response.getResponseCode() != 1) {
                System.err.println(
                        "Failed to save macro: " +
                        macroDto.getMacroName() +
                        " Error: " + response.getResponseMessage()
                );
            }
        }

        return macros;
    }






    
//    public static List<MacroDto> saveMacroNamesForCustomFiles(List<String> fileNamePaths, String runPathMasterId) {
//        List<MacroDto> macroNames = new ArrayList<>();
//        MacroService macroService = new MacroService();
//
//        // Mark previous macro rows as deleted before adding new ones
//        //markPreviousMacroRowsAsDeleted(runPathMasterId);
//
//        for (String fileName : fileNamePaths) {
//            MacroFileParser macroParser = new MacroFileParser();
//            try {
//            	macroService.saveMacroToDatabase("--", fileName, runPathMasterId);
//                List<String> parsedMacroNames = macroParser.parse(fileName);
//                for (String macroName : parsedMacroNames) {
//                	
//                    String filename = Paths.get(fileName).toString();
//
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
//    
    
    public static List<MacroDto> saveMacroNamesForCustomFiles(
            List<String> fileNamePaths,
            String runPathMasterId) {

        List<MacroDto> macroNames = new ArrayList<>();

        // SAME AS SYMBOL: resolve actual macro files
        List<String> filePaths = fetchMacroFilePathsDoubleSlash(fileNamePaths);

        ////System.out.println("Check both: " + filePaths + runPathMasterId);

        MacroService macroService = new MacroService();

        // Mark previous macro rows as deleted before adding new ones
        // markPreviousMacroRowsAsDeleted(runPathMasterId);

        for (String fileName : filePaths) {

            MacroFileParser macroParser = new MacroFileParser();

            try {
                // OPTIONAL placeholder row (same style as symbol)
                macroService.saveMacroToDatabase("--", fileName, runPathMasterId);

                // 🔹 Parse macros USING MacroFileParser instance
                List<String> parsedMacroNames = macroParser.parse(fileName);
                
                ////System.out.println("");

                for (String macroName : parsedMacroNames) {

                    Response response = macroService.saveMacroToDatabase(
                            macroName,
                            fileName,
                            runPathMasterId
                    );

                    if (response.getResponseCode() == 1) {
                        MacroDto macroDto =
                                new MacroDto(macroName, fileName, runPathMasterId);
                        macroNames.add(macroDto);

                        ////System.out.println("macroName " + macroName);
                        ////System.out.println("fileName " + fileName);
                    } else {
                        System.err.println(
                            "Failed to save macro: " + macroName +
                            " Error: " + response.getResponseMessage()
                        );
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return macroNames;
    }

    
    public static List<String> macroFilePathReader(String filePath) {
        List<String> nestedMacroFiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.startsWith("@")) {
                    nestedMacroFiles.add(line.substring(1).trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nestedMacroFiles;
    }

    
    public static List<String> fetchReferencedMacroFiles(String filePath) {
        List<String> allPaths = new ArrayList<>();

        List<String> nestedPaths = macroFilePathReader(filePath);
        for (String nested : nestedPaths) {
            allPaths.add(nested);
            List<String> deeper = fetchReferencedMacroFiles(nested);
            allPaths.addAll(deeper);
        }

        return allPaths;
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
    
    
//    public static List<String> fetchMacroFilePathsDoubleSlash(List<String> macroFilePaths) throws IOException {
//        List<String> filePaths = new ArrayList<>();
//        for (String filePath : macroFilePaths) {
//            try {
//                Path dir = Paths.get(filePath);
//                Files.walk(dir)
//                     .filter(Files::isRegularFile)
//                     .filter(path -> path.toString().endsWith(".mac")) // Filter only .mac files
//                     .map(Path::toString)
//                     .map(path -> path.replace("\\", "\\\\")) // Escape backslashes
//                     .forEach(filePaths::add);
//            } catch (IOException e) {
//                System.err.println("Error processing file path: " + filePath);
//                e.printStackTrace();
//            }
//        }
//        return filePaths;
//    }
    
    public static List<String> fetchMacroFilePathsDoubleSlash(List<String> inputPaths) {

        List<String> finalFiles = new ArrayList<>();

        for (String pathStr : inputPaths) {
            resolveMacroFileAllowDuplicates(new File(pathStr), finalFiles);
        }

        return finalFiles;
    }

    private static void resolveMacroFileAllowDuplicates(
            File file,
            List<String> finalFiles) {

        try {
            if (!file.exists()) {
                System.err.println("File not found: " + file);
                return;
            }

            // CASE 1: Directory
//            if (file.isDirectory()) {
//                Files.walk(file.toPath())
//                        .filter(Files::isRegularFile)
//                        .filter(p -> p.toString().toLowerCase().endsWith(".mac"))
//                        .forEach(p -> resolveMacroFileAllowDuplicates(p.toFile(), finalFiles));
//                return;
//            }
            if (file.isDirectory()) {
                Files.walk(file.toPath())
                        .filter(Files::isRegularFile)
                        .filter(p -> {
                            String lower = p.toString().toLowerCase();
                            return lower.endsWith(".mac") || lower.endsWith(".mdf");
                        })
                        .forEach(p -> resolveMacroFileAllowDuplicates(p.toFile(), finalFiles));
                return;
            }


            // Only .mac files
//            if (!file.getName().toLowerCase().endsWith(".mac")) return;

            ////System.out.println("Processing: " + file);

            // CASE 2: Real macro file
            if (containsMacroDefinitions(file)) {
                finalFiles.add(file.getAbsolutePath());
                ////System.out.println("MACRO FILE FOUND: " + file.getAbsolutePath());
                return;
            }

            // CASE 3: Combined file → resolve references
            File parentDir = file.getParentFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty() || line.startsWith("!")) continue;

                    if (line.startsWith("@")) {
                        line = line.substring(1).trim();
                    }

                    File referenced = new File(line);
                    if (!referenced.isAbsolute()) {
                        referenced = new File(parentDir, line);
                    }

                    resolveMacroFileAllowDuplicates(referenced, finalFiles);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    
    private static void processMacFile(File macFile, List<String> finalFiles) {
        try {
            if (containsMacroDefinitions(macFile)) {
                finalFiles.add(macFile.getAbsolutePath().replace("\\", "\\\\"));
            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(macFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            if (line.startsWith("@")) {
                                line = line.substring(1).trim();
                            }
                            finalFiles.add(line.replace("\\", "\\\\"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }	

    
    private static boolean containsMacroDefinitions(File file) {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {

                ////System.out.println("Read line: " + line);

                line = line.trim().toUpperCase();

                if (line.contains("MACRONAME")) {
                    ////System.out.println("MACRONAME FOUND in: " + file.getName());
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
