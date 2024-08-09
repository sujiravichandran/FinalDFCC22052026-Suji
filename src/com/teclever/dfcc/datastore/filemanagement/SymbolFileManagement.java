package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.FileReader;
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

			// Fetch macros based on runPathMasterIds and deleteStatus
			for (String runPathMasterId : runPathMasterIds) {
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

	    try {
	        // Step 1: Fetch the initial list of file paths
	        List<String> initialFilePaths = fetchSymbolFilePathsDoubleSlash(fileNamePaths);

	        // Step 2: Recursively fetch all symbol files
	        for (String filePath : initialFilePaths) {
	            allFilePaths.add(filePath);
	            List<String> additionalPaths = fetchReferencedSymbolFiles(filePath);
	            allFilePaths.addAll(additionalPaths);
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    SymbolService symbolService = new SymbolService();

	    for (String filePath : allFilePaths) {
	        symbolService.saveSymbolToDatabase("--", "--", "--", "--", filePath, runPathMasterId);
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
	        Response response = symbolService.saveSymbolToDatabase(symbolName, symbolType, minValue, maxValue, fileName,
	                runPathMasterId);
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
	        // Assuming the references are valid file paths, otherwise modify accordingly
	        referencedFilePaths.add(reference);

	        // Recursively fetch any further referenced symbol files
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
	                if (line.startsWith("@")) { // Assuming '@' marks a reference to another file
	                    line = line.substring(1);
	                    listOfSymbolNames.add(line); // Add the reference for further processing
	                } else {
	                    // Process the line as a normal symbol
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
		try {
			filePaths = fetchSymbolFilePathsDoubleSlash(fileNamePaths);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<SymbolDto> symbols = SymbolFileParser.parseSymbols(filePaths, runPathMasterId);
		SymbolService symbolService = new SymbolService();

		// Mark previous macro rows as deleted before adding new ones
		//markPreviousSymbolRowsAsDeleted(runPathMasterId);

		for (SymbolDto symbolDto : symbols) {
			String symbolName = symbolDto.getSymbolName();
			String symbolType = symbolDto.getSymbolType();
			String minValue = symbolDto.getMin();
			String maxValue = symbolDto.getMax();
			String fileName = symbolDto.getFileName();
			Response response = symbolService.saveSymbolToDatabase(symbolName, symbolType, minValue, maxValue, fileName,
					runPathMasterId);
			if (response.getResponseCode() == 1) {
//				System.out.println("Symbol saved successfully: " + symbolName);
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

	public static List<String> fetchSymbolFilePathsDoubleSlash(List<String> symbolFilePaths) throws IOException {
		List<String> filePaths = new ArrayList<>();
		for (String filePath : symbolFilePaths) {
			try {
				Path dir = Paths.get(filePath);
				Files.walk(dir).filter(Files::isRegularFile).map(Path::toString).map(path -> path.replace("\\", "\\\\"))
						.forEach(filePaths::add);
			} catch (IOException e) {
				System.err.println("Error processing file path: " + filePath);
				e.printStackTrace();
			}
		}
		return filePaths;
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
				symbolDtos.add(symbolDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return symbolDtos;
	}
}
