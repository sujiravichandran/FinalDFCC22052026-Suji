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
    public static List<SymbolDto> parseSymbols(List<String> filePaths, String runPathMasterId) {
        List<SymbolDto> symbols = new ArrayList<>();
        for (String filePath : filePaths) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                String fileName = Paths.get(filePath).toString();
                String symbolName = null;
                String symbolType = null;
                String minValue = null;
                String maxValue = null;
                

                Pattern keyValuePattern = Pattern.compile("(?i)\\b(SYMB|STYPE|TYPE|MIN|MAX)\\b\\s*=\\s*(\\S+)");
                
                while ((line = br.readLine()) != null) {
                    Matcher keyValueMatcher = keyValuePattern.matcher(line);
                    while (keyValueMatcher.find()) {
                        String key = keyValueMatcher.group(1).toUpperCase();
                        String value = keyValueMatcher.group(2);
                        switch (key) {
                            case "SYMB":
                                if (symbolName != null) {
                                    SymbolDto symbolDto = new SymbolDto(symbolName, symbolType, minValue, maxValue, fileName, runPathMasterId);
                                    symbols.add(symbolDto);
                                }
                                symbolName = value;
                                symbolType = null;
                                minValue = null;
                                maxValue = null;
                                break;
                            case "STYPE":
                                symbolType = value;
                                break;
                            case "TYPE":
                                symbolType = value;
                                break;
                            case "MIN":
                                minValue = value;
                                break;
                            case "MAX":
                                maxValue = value;
                                break;
                        }
                    }
                }
                if (symbolName != null) {
                    SymbolDto symbolDto = new SymbolDto(symbolName, symbolType, minValue, maxValue, fileName, runPathMasterId);
                    symbols.add(symbolDto);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return symbols;
    }
}






