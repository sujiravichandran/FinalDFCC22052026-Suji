package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.SymbolDto;

public class SymbolFileParser {
//    public static List<SymbolDto> parseSymbols(List<String> filePaths, String runPathMasterId) {
//        List<SymbolDto> symbols = new ArrayList<>();
//        for (String filePath : filePaths) {
//            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//                String line;
//                String fileName = Paths.get(filePath).toString();
//                String symbolName = null;
//                String symbolType = null;
//                String minValue = null;
//                String maxValue = null;	
//                String readValue = null;	
//                String writeValue = null;
//                
//
//                Pattern keyValuePattern = Pattern.compile("(?i)\\b(SYMB|STYPE|TYPE|MIN|MAX|READ|WRTE)\\b\\s*=\\s*(\\S+)");
//                Pattern symbStart = Pattern.compile("^\\s*SYMB=.*");
//                Pattern commentedSymb = Pattern.compile("^\\s*!SYMB=.*");
//                StringBuilder fullContent = new StringBuilder();
//                boolean capturing = false;
//
//                while ((line = br.readLine()) != null) {
//
//                    // Ignore commented SYMB completely
//                    if (commentedSymb.matcher(line).matches()) {
//                        capturing = false;
//                        fullContent.setLength(0);
//                        continue;
//                    }
//
//                   
//
//                        // Save previous symbol before starting new one
//                        if (symbolName != null) {
//                            SymbolDto symbolDto = new SymbolDto(
//                                symbolName, symbolType, minValue, maxValue,
//                                fileName, runPathMasterId, readValue, writeValue,fullContent.toString()
//                            );
//                            symbols.add(symbolDto);
//                            ////System.out.println("Symbol Name::"+ symbolName +"Full SYMB block:\n" + fullContent);
//                        }
//
//                        // Reset for new symbol
//                        fullContent.setLength(0);
//                        capturing = true;
//
//                        symbolName = null;
//                        symbolType = null;
//                        minValue = null;
//                        maxValue = null;
//                        readValue = null;
//                        writeValue = null;
//                    
//
//                    // Capture all lines belonging to the symbol
//                    if (capturing) {
//                        fullContent.append(line).append(System.lineSeparator());
//                    }
//
//                    // Existing key-value parsing still works
//                    Matcher keyValueMatcher = keyValuePattern.matcher(line);
//                    while (keyValueMatcher.find()) {
//                        String key = keyValueMatcher.group(1).toUpperCase();
//                        String value = keyValueMatcher.group(2);
//
//                        switch (key) {
//                            case "SYMB": symbolName = value; break;
//                            case "STYPE":
//                            case "TYPE": symbolType = value; break;
//                            case "MIN": minValue = value; break;
//                            case "MAX": maxValue = value; break;
//                            case "READ": readValue = value; break;
//                            case "WRTE": writeValue = value; break;
//                        }
//                    }
//                }
//                if (symbolName != null) {
//                    SymbolDto symbolDto = new SymbolDto(symbolName, symbolType, minValue, maxValue, fileName, runPathMasterId, readValue, writeValue,fullContent.toString());
//                    symbols.add(symbolDto);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return symbols;
//    }
	
	public static List<SymbolDto> parseSymbols(List<String> filePaths, String runPathMasterId) {

	    List<SymbolDto> symbols = new ArrayList<>();

	    Pattern keyValuePattern = Pattern.compile("(?i)\\b(SYMB|STYPE|TYPE|MIN|MAX|READ|WRTE)\\b\\s*=\\s*(\\S+)");
	    Pattern symbStart = Pattern.compile("(?i)\\bSYMB\\b\\s*=");
	    Pattern commentedSymb = Pattern.compile("(?i)^\\s*!\\s*SYMB\\b");

	    for (String filePath : filePaths) {

	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

	            String line;
	            String fileName = Paths.get(filePath).toString();

	            String symbolName = null;
	            String symbolType = null;
	            String minValue = null;
	            String maxValue = null;
	            String readValue = null;
	            String writeValue = null;

	            StringBuilder fullContent = new StringBuilder();
	            boolean capturing = false;

	            while ((line = br.readLine()) != null) {

	                // 1️⃣ Ignore commented SYMB lines completely
	                if (commentedSymb.matcher(line).find()) {
	                    continue;
	                }

	                boolean isNewSymbol = symbStart.matcher(line).find();

	                // 2️⃣ If new SYMB found → save previous symbol
	                if (isNewSymbol) {

	                    if (capturing && symbolName != null) {
	                        symbols.add(new SymbolDto(
	                                symbolName,
	                                symbolType,
	                                minValue,
	                                maxValue,
	                                fileName,
	                                runPathMasterId,
	                                readValue,
	                                writeValue,
	                                fullContent.toString()
	                        ));
	                    }

	                    // Reset for new symbol
	                    fullContent.setLength(0);
	                    symbolName = null;
	                    symbolType = null;
	                    minValue = null;
	                    maxValue = null;
	                    readValue = null;
	                    writeValue = null;

	                    capturing = true;
	                }

	                // 3️⃣ Capture entire symbol block
	                if (capturing) {
	                    fullContent.append(line).append(System.lineSeparator());
	                }

	                // 4️⃣ Extract key-value pairs
	                Matcher matcher = keyValuePattern.matcher(line);
	                while (matcher.find()) {

	                    String key = matcher.group(1).toUpperCase();
	                    String value = matcher.group(2);

	                    switch (key) {
	                        case "SYMB":
	                            symbolName = value;
	                            break;
	                        case "STYPE":
	                        case "TYPE":
	                            symbolType = value;
	                            break;
	                        case "MIN":
	                            minValue = value;
	                            break;
	                        case "MAX":
	                            maxValue = value;
	                            break;
	                        case "READ":
	                            readValue = value;
	                            break;
	                        case "WRTE":
	                            writeValue = value;
	                            break;
	                    }
	                }
	            }

	            // 5️⃣ Add last symbol after file ends
	            if (capturing && symbolName != null) {
	                symbols.add(new SymbolDto(
	                        symbolName,
	                        symbolType,
	                        minValue,
	                        maxValue,
	                        fileName,
	                        runPathMasterId,
	                        readValue,
	                        writeValue,
	                        fullContent.toString()
	                ));
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    return symbols;
	}
}






