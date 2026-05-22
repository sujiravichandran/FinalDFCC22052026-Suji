package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.Symbol;
import com.teclever.datastore.service.SymbolService;
import com.teclever.dfcc.datastore.dto.SymbolDto;

public class SymbolFileManagement {

	// GET SYMBOL LIST BY TEST TYPE
	public static List<SymbolDto> getAllSymbols(String runConfigId) {
		List<SymbolDto> symbolDtos = new ArrayList<>();
		try {
			SymbolService symbolService = new SymbolService();
			List<String> runPathMasterIds = symbolService.getRunPathMasterIdsByRunConfigId(runConfigId);

			////System.out.println("ADVANCE TESTING CUSTOM 1 RUN CONFIG ID " + runConfigId);

			////System.out.println("RUN PATH MASTER ADVANCE TESTING CUSTOM1 :::" + runPathMasterIds);

			// Fetch macros based on runPathMasterIds and deleteStatus
			for (String runPathMasterId : runPathMasterIds) {
				List<Symbol> symbols = symbolService.getAllSymbolsByRunPathMasterId(runPathMasterId);
				////System.out.println("RUN PATH MASTER  ::"+runPathMasterId);
				for (Symbol symbol : symbols) {
					SymbolDto symbolDto = new SymbolDto();
					symbolDto.setSymbolId(symbol.getSymbolId());
					symbolDto.setSymbolName(symbol.getSymbolName());
					symbolDto.setSymbolType(symbol.getSymbolType());
					symbolDto.setMin(symbol.getMin());
					symbolDto.setMax(symbol.getMax());
					symbolDto.setFileName(symbol.getFileName());
					symbolDto.setRunPathMasterId(symbol.getRunPathMasterId());
					symbolDto.setWriteValue(symbol.getWriteValue());
					symbolDto.setReadValue(symbol.getReadValue());
					symbolDto.setFullSymbolLine(symbol.getFullSymbolLine());
					////System.out.println("Managw:: suji " +symbol.getFullSymbolLine() );
					symbolDtos.add(symbolDto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return symbolDtos;
	}

	public static List<SymbolDto> saveSymbols(List<String> fileNamePaths, String runPathMasterId) {
		List<String> allFilePaths = new ArrayList<>();

		// Step 1: Fetch the initial list of file paths
		List<String> initialFilePaths = collectSymbolFilesRecursively(fileNamePaths);

		// Step 2: Recursively fetch all symbol files
		for (String filePath : initialFilePaths) {
			allFilePaths.add(filePath);
			List<String> additionalPaths = fetchReferencedSymbolFiles(filePath);
			allFilePaths.addAll(additionalPaths);
		}

		SymbolService symbolService = new SymbolService();

		for (String filePath : allFilePaths) {
//	    	Suji changed for Read and Write value storing on 03022026
			symbolService.saveSymbolToDatabase("--", "--", "--", "--", filePath, runPathMasterId, "--", "--", "--");
//	        END::
		}

		List<SymbolDto> symbols = SymbolFileParser.parseSymbols(allFilePaths, runPathMasterId);

		// Mark previous macro rows as deleted before adding new ones
		markPreviousSymbolRowsAsDeleted(runPathMasterId);

		for (SymbolDto symbolDto : symbols) {
			String symbolName = symbolDto.getSymbolName();
			String symbolType = symbolDto.getSymbolType();
			String minValue = symbolDto.getMin();
			String maxValue = symbolDto.getMax();
			String fileName = symbolDto.getFileName();
			String readValue = symbolDto.getReadValue();
			String writeValue = symbolDto.getWriteValue();
			String symbolFullLine = symbolDto.getFullSymbolLine();
			Response response = symbolService.saveSymbolToDatabase(symbolName, symbolType, minValue, maxValue, fileName,
					runPathMasterId, readValue, writeValue, symbolFullLine);
			if (response.getResponseCode() == 1) {
				// Symbol saved successfully
			} else {
				System.err.println("Failed to save symbol: " + symbolName + " Error: " + response.getResponseMessage());
			}
		}

		return symbols;
	}

	public static List<String> fetchReferencedSymbolFiles(String filePath) {
		List<String> referencedFilePaths = new ArrayList<>();
		List<String> symbolReferences = symbolFileReader(filePath);
		for (String reference : symbolReferences) {
			reference = reference.trim();
			referencedFilePaths.add(reference);
			List<String> furtherReferences = fetchReferencedSymbolFiles(reference);
			referencedFilePaths.addAll(furtherReferences);
		}

		return referencedFilePaths;
	}

	public static List<String> symbolFileReader(String filePath) {
		List<String> listOfSymbolNames = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				if ((!line.isEmpty())) {
					if (line.startsWith("@")) {
						line = line.substring(1).trim();
						listOfSymbolNames.add(line);
					} else {

					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return listOfSymbolNames;
	}

	public static List<SymbolDto> saveSymbolsForCustomFiles(List<String> fileNamePaths, String runPathMasterId) {
		List<String> filePaths = null;
		filePaths = collectSymbolFilesRecursively(fileNamePaths);
		////System.out.println("Check both: " + filePaths + runPathMasterId);
		List<SymbolDto> symbols = SymbolFileParser.parseSymbols(filePaths, runPathMasterId);
		////System.out.println("Check Symbols list :" + symbols.size());
		SymbolService symbolService = new SymbolService();

		// Mark previous macro rows as deleted before adding new ones
		// markPreviousSymbolRowsAsDeleted(runPathMasterId);

		for (SymbolDto symbolDto : symbols) {
			String symbolName = symbolDto.getSymbolName();
			String symbolType = symbolDto.getSymbolType();
			String minValue = symbolDto.getMin();
			String maxValue = symbolDto.getMax();
			String fileName = symbolDto.getFileName();
			String readValue = symbolDto.getReadValue();
			String writeValue = symbolDto.getWriteValue();
			String symbolFullLine = symbolDto.getFullSymbolLine();

			////System.out.println("fullContentValue" + symbolFullLine);

			Response response = symbolService.saveSymbolToDatabase(symbolName, symbolType, minValue, maxValue, fileName,
					runPathMasterId, readValue, writeValue, symbolFullLine);
			////System.out.println("Response check:" + response);
			if (response.getResponseCode() == 1) {
				////System.out.println("Response check:" + response);
				////System.out.println("symbolName " + symbolName);
				////System.out.println("symbolType " + symbolType);
				////System.out.println("minValue " + minValue);
				////System.out.println("maxValue " + maxValue);
				////System.out.println("readValue" + readValue);
				////System.out.println("writeValue" + writeValue);
				////System.out.println("fullContentValue" + symbolFullLine);

				////System.out.println("fileName " + fileName);
//				Debug.printDebug("Symbol saved successfully: " + symbolName);
			} else {
				System.err.println("Failed to save symbol: " + symbolName + " Error: " + response.getResponseMessage());
			}
		}

		return symbols;
	}

	// Method to mark previous symbol rows as deleted
	public static void markPreviousSymbolRowsAsDeleted(String runPathMasterId) {
		SymbolService symbolService = new SymbolService();
		List<Symbol> symbols = symbolService.getSymbolsByRunPathMasterId(runPathMasterId);

		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			for (Symbol symbol : symbols) {
				symbol.setDeleteStatus(true);
				session.merge(symbol);
			}

			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	OLD BEFOR SUJI CHANGE FOR SYMBOLS : 11/12/2025
//	public static List<String> fetchSymbolFilePathsDoubleSlash(List<String> symbolFilePaths) throws IOException {
//		List<String> filePaths = new ArrayList<>();
//		for (String filePath : symbolFilePaths) {
//			try {
//				Path dir = Paths.get(filePath);
//				Files.walk(dir).filter(Files::isRegularFile).map(Path::toString).map(path -> path.replace("\\", "\\\\"))
//						.forEach(filePaths::add);
//			} catch (IOException e) {
//				System.err.println("Error processing file path: " + filePath);
//				e.printStackTrace();
//			}
//		}
//		return filePaths;
//	}

//	SUJI CHANGED : 11/12/2025
//	public static List<String> fetchSymbolFilePathsDoubleSlash(List<String> symbolFilePaths) {
//	    List<String> filePaths = new ArrayList<>();
//
//	    for (String inputPath : symbolFilePaths) {
//	        try {
//	            Path path = Paths.get(inputPath);
//
//	            if (Files.isRegularFile(path)) {
//	                // CASE 1: Input is a FILE
//	                filePaths.add(path.toString().replace("\\", "\\\\"));
//	            } 
//	            else if (Files.isDirectory(path)) {
//	                // CASE 2: Input is a FOLDER (scan ALL nested files)
//	                Files.walk(path)
//	                        .filter(Files::isRegularFile)
//	                        .map(Path::toString)
//	                        .map(p -> p.replace("\\", "\\\\"))
//	                        .forEach(filePaths::add);
//	            }
//	        } catch (IOException e) {
//	            System.err.println("Error processing path: " + inputPath);
//	            e.printStackTrace();
//	        }
//	    }
//	    ////System.out.println("Check FIle path "+filePaths);
//	    return filePaths;
//	}

//	public static List<String> fetchSymbolFilePathsDoubleSlash(List<String> inputPaths) {
//	    List<String> finalFiles = new ArrayList<>();
//
//	    for (String pathStr : inputPaths) {
//	        File file = new File(pathStr);
//
//	        // CASE 1: If path is a directory → load all .sym files inside
//	        if (file.isDirectory()) {
//	            try {
//	                Files.walk(file.toPath())
//	                        .filter(Files::isRegularFile)
//	                        .filter(p -> p.toString().toLowerCase().endsWith(".sym"))
//	                        .forEach(p -> processSymFile(p.toFile(), finalFiles));
//	                
//	               
//	            } catch (Exception e) {
//	                e.printStackTrace();
//	            }
//	        }
//	        // CASE 2: If it's a .sym file
//	        else if (file.isFile() && pathStr.toLowerCase().endsWith(".sym")) {
//	        	
//	        	
//	        	
//	        	
//	        	////System.out.println("Entred else if");
//	            if (containsSymbolDefinitions(file)) {
//	                finalFiles.add(file.getAbsolutePath().replace("\\", "\\\\"));
//	            } else {
//	                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//	                    String line;
//	                    while ((line = br.readLine()) != null) {
//	                        line = line.trim();
//	                        ////System.out.println("Suji added for ! line should not read::::");
////	                        Suji added for ! line should not read:::: Open 02022026
//	                        if (line.isEmpty() || line.startsWith("!")) continue;
////	                        Close::
//	                        if (!line.isEmpty()) {
//	                            finalFiles.add(line.replace("\\", "\\\\"));
//	                        }
//	                    }
//	                } catch (Exception e) {
//	                    e.printStackTrace();
//	                }
//	            }
//	        }
//	    }
//
//	    return finalFiles;
//	}
	private static void collectSymbolFiles(File file, List<String> finalFiles, Set<String> visited) {

		try {
			if (!file.exists())
				return;

			String canonicalPath = file.getCanonicalPath();
			if (visited.contains(canonicalPath))
				return; // prevent infinite loops
			visited.add(canonicalPath);

// CASE 1: Directory → recurse all .sym files
			if (file.isDirectory()) {
				Files.walk(file.toPath()).filter(Files::isRegularFile)
				 .filter(p -> {
			         String name = p.toString().toLowerCase();
			         return name.endsWith(".sym") || name.endsWith(".dbf");
			     })
						.forEach(p -> collectSymbolFiles(p.toFile(), finalFiles, visited));
				return;
			}

// CASE 2: .sym file
			if (file.isFile() && 
					   (file.getName().toLowerCase().endsWith(".sym") ||
					    file.getName().toLowerCase().endsWith(".dbf"))) {

// Real symbol definition file
				if (hasSymbolDefinitions(file)) {
					finalFiles.add(file.getAbsolutePath().replace("\\", "\\\\"));
					return;
				}

// File contains paths → recurse into them
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = br.readLine()) != null) {
						line = line.trim();

						if (line.isEmpty() || line.startsWith("!"))
							continue;

						collectSymbolFiles(new File(line), finalFiles, visited);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean hasSymbolDefinitions(File file) {
	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            line = line.trim();

	            if (line.startsWith("SYMBOL")
	                || line.contains("=")
	                || line.toLowerCase().contains("min")) {
	                return true;
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public static List<String> collectSymbolFilesRecursively(List<String> inputPaths) {
	    List<String> finalFiles = new ArrayList<>();
	    Set<String> visited = new HashSet<>();

	    for (String pathStr : inputPaths) {
	        collectSymbolFiles(new File(pathStr), finalFiles, visited);
	    }

	    return finalFiles;
	}


	private static void processSymFile(File symFile, List<String> finalFiles) {
		try {
			if (containsSymbolDefinitions(symFile)) {
				finalFiles.add(symFile.getAbsolutePath().replace("\\", "\\\\"));
			} else {
				try (BufferedReader br = new BufferedReader(new FileReader(symFile))) {
					String line;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						if (!line.isEmpty()) {
							finalFiles.add(line.replace("\\", "\\\\"));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean containsSymbolDefinitions(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();

				// Example: lines containing actual symbols
				if (line.startsWith("SYMBOL") || line.contains("=") || line.toLowerCase().contains("min")) {
					return true; // looks like a real symbol file
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // treat as file containing paths
	}

	public static List<SymbolDto> getAllSymbolsByRunPathMasterId(String runPathMasterId) {
		List<SymbolDto> symbolDtos = new ArrayList<>();
		try {
			SymbolService symbolService = new SymbolService();
			List<Symbol> symbols = symbolService.getAllSymbolsByRunPathMasterId(runPathMasterId);
			for (Symbol symbol : symbols) {
				SymbolDto symbolDto = new SymbolDto();
				symbolDto.setSymbolId(symbol.getSymbolId());
				symbolDto.setSymbolName(symbol.getSymbolName());
				symbolDto.setSymbolType(symbol.getSymbolType());
				symbolDto.setMin(symbol.getMin());
				symbolDto.setMax(symbol.getMax());
				symbolDto.setFileName(symbol.getFileName());
				symbolDto.setRunPathMasterId(symbol.getRunPathMasterId());
				symbolDto.setFullSymbolLine(symbol.getFullSymbolLine());
				symbolDtos.add(symbolDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return symbolDtos;
	}
}
