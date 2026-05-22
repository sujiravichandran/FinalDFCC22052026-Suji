package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MacroFileParser {

	public List<String> parse(String fileName) throws IOException {

	    List<String> macroNames = new ArrayList<>();

	    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

	        String line;

	        while ((line = br.readLine()) != null) {

	            line = line.trim();

	            if (line.toUpperCase().startsWith("MACRONAME")) {

	                // Remove everything after '!' if present
	                int exclIndex = line.indexOf('!');
	                if (exclIndex != -1) {
	                    line = line.substring(0, exclIndex).trim();
	                }

	                macroNames.add(line);
	            }
	        }
	    }

	    return macroNames;
	}

}

