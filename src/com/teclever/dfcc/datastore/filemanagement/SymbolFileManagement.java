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
import com.teclever.datastore.entities.Symbol;
import com.teclever.datastore.service.SymbolService;
import com.teclever.dfcc.datastore.dto.SymbolDto;

public class SymbolFileManagement {
	
	//GET ALL SYMBOL LIST BASED ON RUN PATH MASTER ID
	public static List<SymbolDto> getAllSymbolsByRunPathMasterId(String runPathMasterId) {
        List<SymbolDto> symbolDtos = new ArrayList<>();
        try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Symbol> symbols = session.createQuery("FROM Symbol WHERE runPathMasterId = :runPathMasterId AND deleteStatus = false", Symbol.class)
                                        .setParameter("runPathMasterId", runPathMasterId)
                                        .getResultList();

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

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return symbolDtos;
    }
	
	    public static List<SymbolDto> saveSymbols(List<String> fileNamePaths, String runPathMasterId) {
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
	        markPreviousSymbolRowsAsDeleted(runPathMasterId);
	        
	        for (SymbolDto symbolDto : symbols) {
	            String symbolName = symbolDto.getSymbolName();
	            String symbolType = symbolDto.getSymbolType();
	            String minValue = symbolDto.getMin();
	            String maxValue = symbolDto.getMax();
	            String fileName = symbolDto.getFileName();
	            Response response = symbolService.saveSymbolToDatabase(symbolName, symbolType, minValue, maxValue, fileName, runPathMasterId);
	            if (response.getResponseCode() == 1) {
	                System.out.println("Symbol saved successfully: " + symbolName);
	            } else {
	                System.err.println("Failed to save symbol: " + symbolName + " Error: " + response.getResponseMessage());
	            }
	        }
	        return symbols;
	    }
	
	    // Method to mark previous symbol rows as deleted
	    private static void markPreviousSymbolRowsAsDeleted(String runPathMasterId) {
	        try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
	            Transaction transaction = session.beginTransaction();

	            List<Symbol> symbols = session.createQuery("FROM Symbol WHERE runPathMasterId = :runPathMasterId", Symbol.class)
	                                        .setParameter("runPathMasterId", runPathMasterId)
	                                        .getResultList();

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

