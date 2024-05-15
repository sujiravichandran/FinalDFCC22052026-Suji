package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestFileManagement {

	public static String[] getMacroValues(List<String> fileNamePath) {
		List<String> macroValues = new ArrayList<>();

		try {
			for (String fileName : fileNamePath) {
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				String line;
				boolean inMacro = false;

				while ((line = br.readLine()) != null) {
					if (line.trim().startsWith("macroname")) {
						inMacro = true;
						String[] parts = line.split("\\s*=\\s*");
						if (parts.length > 1) {
							macroValues.add(parts[1].trim());
						}
					} else if (line.trim().startsWith("endm")) {
						// End of the current macro
						inMacro = false;
					} else if (inMacro) {
						// Inside a macro, add non-empty lines as values
						if (!line.trim().isEmpty()) {
							macroValues.add(line.trim());
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return macroValues.toArray(new String[0]);
	}

	/*
	 * public List<String> getMacroValuesList(List<String> fileNamePath) {
	 * List<String> macroValues = new ArrayList<>();
	 * 
	 * try { for (String fileName : fileNamePath) { BufferedReader br = new
	 * BufferedReader(new FileReader(fileName)); String line; boolean inMacro =
	 * false;
	 * 
	 * while ((line = br.readLine()) != null) { if
	 * (line.trim().startsWith("macroname")) { inMacro = true; String[] parts =
	 * line.split("\\s*=\\s*"); if (parts.length > 1) {
	 * macroValues.add(parts[1].trim()); } } else if
	 * (line.trim().startsWith("endm")) { // End of the current macro inMacro =
	 * false; } else if (inMacro) { // Inside a macro, add non-empty lines as values
	 * if (!line.trim().isEmpty()) { macroValues.add(line.trim()); } } } } } catch
	 * (IOException e) { e.printStackTrace(); } return macroValues; }
	 */

	public List<String> getMacroValuesList(List<String> fileNamePath) {
		List<String> macroValues = new ArrayList<>();
		boolean inMacro = false;
		StringBuilder currentMacro = new StringBuilder();

		try {
			for (String fileName : fileNamePath) {
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				String line;

				while ((line = br.readLine()) != null) {
					if (line.trim().startsWith("macroname=")) {
						// Start of a new macro
						if (inMacro) {
							// If we were already inside a macro, add it to the list
							macroValues.add(currentMacro.toString());
							currentMacro.setLength(0); // Clear the StringBuilder for the new macro
						}
						inMacro = true;
					}

					if (inMacro) {
						// Inside a macro, add the line to the currentMacro StringBuilder
						currentMacro.append(line).append("\n");
					}

					if (line.trim().startsWith("endm")) {
						// End of the current macro
						inMacro = false;
						macroValues.add(currentMacro.toString());
						currentMacro.setLength(0); // Clear the StringBuilder for the next macro
					}
				}

				br.close(); // Close the BufferedReader after processing each file
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return macroValues;
	}

	public List<String> extractMacronames(List<String> macronameSections) {
		List<String> macronames = new ArrayList<>();

		for (String section : macronameSections) {
			// Find the line that starts with "macroname="
			String[] lines = section.split("\\n");
			for (String line : lines) {
				if (line.trim().startsWith("macroname=")) {
					// Extract the macroname value
					String macroname = line.trim().substring("macroname=".length());
					macronames.add(macroname);
					break; // No need to continue checking lines in this section
				}
			}
		}

		return macronames;
	}

	public  List<String> fetchFilePathsDoubleSlash(String directoryPath) throws IOException {
		Path dir = Paths.get(directoryPath);
		List<String> filePaths = Files.walk(dir).filter(Files::isRegularFile).map(Path::toString)
				.map(path -> path.replace("\\", "\\\\")).collect(Collectors.toList());
		return filePaths;
	}

}