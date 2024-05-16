package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MacroFileParser {

	 public List<String> parse(String fileName) throws IOException {
	        List<String> macroNames = new ArrayList<>();
	        BufferedReader br = new BufferedReader(new FileReader(fileName));
	        String line;

	        while ((line = br.readLine()) != null) {
	            if (line.trim().startsWith("macroname")) {
	                int equalsIndex = line.indexOf("=");
	                if (equalsIndex != -1) {
	                    String macroName = line.substring(equalsIndex + 1).trim();
	                    macroNames.add(macroName);
	                }
	            }
	        }
	        br.close();

	        return macroNames;
	    }
}
