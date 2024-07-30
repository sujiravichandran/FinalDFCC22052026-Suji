package com.teclever.dfcc.datastore.customtestmanagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.CustomTest;
import com.teclever.datastore.service.CustomTestService;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.CustomTestFileResponse;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SymbolFileManagement;
import com.teclever.dfcc.stateMachine.StateMachine;

public class AdvanceCustom1TestingManagement {

	static String currentDirectory = new File(
			AdvanceCustom1TestingManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath())
			.getParent();

	static String customFileDir = currentDirectory + File.separator + "CustomTesting1Files" + File.separator;

	// Custom Testing For Generating Macro On Drop Down..
	public List<MacroDto> getAllMacrosForAdavanceTest(String uutTypeId, String testTypeId) {
		List<MacroDto> macros = new ArrayList<MacroDto>();
		try {
			RunConfigurationService runConfigurationService = new RunConfigurationService();
			String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutTypeId, testTypeId);
			macros = MacroFileManagement.getAllMacros(runConfigId);

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}

		return macros;
	}

	public List<String> getAllMacroNames(String uutTypeId, String testTypeId) {
		List<String> macroNames = new ArrayList<String>();
		try {
			RunConfigurationService runConfigurationService = new RunConfigurationService();
			String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutTypeId, testTypeId);
			List<MacroDto> macros = MacroFileManagement.getAllMacros(runConfigId);

			for (MacroDto m : macros) {
				macroNames.add(m.getMacroName());
			}

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return macroNames;

	}

	// Custom Testing For Generating the Symbols On the Drop Down
	public List<SymbolDto> getAllSymbolsForAdavanceTest(String uutTypeId, String testTypeId) {
		List<SymbolDto> symbols = new ArrayList<SymbolDto>();
		try {
			RunConfigurationService runConfigurationService = new RunConfigurationService();
			String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutTypeId, testTypeId);
			symbols = SymbolFileManagement.getAllSymbols(runConfigId);

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return symbols;
	}

	// Running Single Symbol Command
	public String customTest1SymbolRun(String symbolName, String ipData) {

		String symbolCommand;
		try {
			symbolCommand = symbolName + " = " + ipData;
		} catch (Exception e) {
			return null;
		}
		return symbolCommand;

	}

	// Running Single Macro Command
	public String customTest1MacroRun(String macroName) {

		String macroCommand;
		try {
			macroCommand = macroName;
		} catch (Exception e) {
			return null;
		}
		return macroCommand;

	}

	public Response customTesting1FileMaking(String fileName, List<String> symbolTestFormate) {

		Response res = new Response();

		File theDir = new File(customFileDir);
		if (!theDir.exists()) {
			theDir.mkdirs();
		}

		if (directoryExist(customFileDir)) {

			File file = new File(customFileDir + fileName + ".txt");

			try {
				if (file.exists()) {
					System.out.println("File exist");
				} else {
					if (file.createNewFile()) {
						System.out.println("New File is Created ");
					} else {
						System.out.println("File is not created");
					}
				}

				try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
					for (String symbolCmd : symbolTestFormate) {
						bw.write(symbolCmd);
						bw.newLine();
					}

					bw.close();
				}

				StateMachine.currentSessionDetails.setSessionId("SASN00002");
				CustomTestService customTestService = new CustomTestService();
				customTestService.addCustomTest(fileName, StateMachine.currentSessionDetails.getSessionId(),
						convertFileToBlob(file), customFileDir + fileName, "custom1");

//				TestProcessManagement testProcessManangement = new TestProcessManagement();
//				testProcessManangement.testProcesControl(StateMachine.currentSessionDetails.getSessionId(), "stageId",
//						0, null /* listOfFileId */, true /* continueWithError */, null/* stageName */,
//						null/* testTypeId */);
				
				

			} catch (IOException e) {
				e.printStackTrace();

			} catch (SQLException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			res.setResponseCode(1);
			res.setResponseMessage("Test Started");
		} else {
			System.out.println("CustomTesting1Files Directory Not Present,Please Create");
			res.setResponseCode(0);
			res.setResponseMessage("Test Not Started : CustomTesting1Files Directory Not Present, Create It");
		}
		return res;
	}

	private SerialBlob convertFileToBlob(File file) throws IOException, SQLException {
		byte[] fileContent = Files.readAllBytes(file.toPath());
		return new SerialBlob(fileContent);
	}

	private boolean directoryExist(String fileDir) {
		File directory = new File(fileDir);
		boolean returnFlag;
		try {
			// Check if the directory exists
			if (directory.exists() && directory.isDirectory()) {
				// Get all files in the directory
				File[] files = directory.listFiles();
				if (files != null) {
					for (File f : files) {
						if (f.isFile() && !f.getName().equals(fileDir)) {
							// Delete each file except the .tst file
							if (f.delete()) {
								System.out.println(f.getName() + " deleted.");
							} else {
								System.out.println("Failed to delete " + f.getName());
							}
						}
					}
				}
				returnFlag = true;
			} else {
				System.out.println("Directory does not exist.");
				returnFlag = false;
			}
		} catch (Exception e) {
			throw e;
		}
		return returnFlag;
	}

	public List<String> readFromDotTstFile(String customId) {
		List<String> listOfFileData = new ArrayList<>();

		CustomTestService customTestService = new CustomTestService();
		GetObjResponse objResponse = customTestService.getFileByCustomId(customId);
		CustomTest customTest = new CustomTest();
		customTest = (CustomTest) objResponse.getObject();
		try {
			// Assume you have a method to retrieve the blob from the database
			Blob blob = customTest.getContent();

			// Convert Blob to byte array
			byte[] bytes = blob.getBytes(1, (int) blob.length());

			// Convert byte array to String (assuming UTF-8 encoding)
			String fileContent = new String(bytes, StandardCharsets.UTF_8);

			// Split the file content into lines (assuming each line is a symbol command)
			String[] lines = fileContent.split("\\r?\\n");

			// Add each line to the list
			listOfFileData.addAll(Arrays.asList(lines));

			for (String l : listOfFileData) {
				System.out.println("Line data --  " + l);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exceptions as needed
		}
		return listOfFileData;

	}

	public CustomTestFileResponse getListOfFileNamesWithId() {
		CustomTestFileResponse customTestFileResponse = new CustomTestFileResponse();
		Response res = new Response();
		try {

			CustomTestService customTestService = new CustomTestService();
			GetResponse objResponse = customTestService.getAllFileNames();
			CustomTest customTest;
			if (objResponse.getCode() == 1) {

				Map<String, String> fileIdAndName = new HashMap<>();

				List<CustomTest> custom1TestList = new ArrayList<CustomTest>();
				custom1TestList = (List<CustomTest>) objResponse.getResponseList();
				custom1TestList = custom1TestList.stream().filter(S -> S.getCustomTestType().equals("custom1"))
						.toList();

				for (CustomTest obj : custom1TestList) {
					fileIdAndName.put(obj.getCustomId(), obj.getFilename());
				}
				customTestFileResponse.setFileIdAndName(fileIdAndName);
			}
			res.setResponseCode(objResponse.getCode());
			res.setResponseMessage(objResponse.getMsg());
			customTestFileResponse.setResponse(res);
		} catch (Exception e) {

			e.printStackTrace();
			res.setResponseCode(0);
			res.setResponseMessage("Exception " + e.getLocalizedMessage());
			customTestFileResponse.setResponse(res);
		}
		return customTestFileResponse;
	}

	// Copying The File
	public static void copyFileRdfFile(String sourceFilePath, String destinationDirectory) {
		try {
			String newFileName = "Ram.sym";
			// Create Path objects for the source and destination
			Path sourcePath = Paths.get(sourceFilePath);
			Path destinationPath = Paths.get(destinationDirectory, newFileName);

			// Copy the file to the new location with the new name
			Files.copy(sourcePath, destinationPath);

			System.out.println("File copied successfully to " + destinationPath.toString());
		} catch (IOException e) {
			System.err.println("An error occurred while copying the file: " + e.getMessage());
		}
	}
	
	
	
	 public void copyFileWithRename(String sourceFilePath, String destinationDirectory) {
	        try {
	            // Create Path objects for the source and initial destination
	        	String newFileName = "Ram.syb";
	            Path sourcePath = Paths.get(sourceFilePath);
	            Path destinationPath = Paths.get(destinationDirectory, newFileName);

	            // Check if a file with the same name already exists
	            destinationPath = resolveFileNameConflict(destinationPath);

	            // Copy the file to the new location with the new name
	            Files.copy(sourcePath, destinationPath);

	            System.out.println("File copied successfully to " + destinationPath.toString());
	        } catch (IOException e) {
	            System.err.println("An error occurred while copying the file: " + e.getMessage());
	        }
	    }

	    public Path resolveFileNameConflict(Path destinationPath) {
	        int count = 1;
	        String baseName = destinationPath.getFileName().toString();
	        String newName = baseName;
	        Path parentDir = destinationPath.getParent();
	        String fileNameWithoutExt = getFileNameWithoutExtension(baseName);
	        String fileExtension = getFileExtension(baseName);

	        while (Files.exists(destinationPath)) {
	            newName = fileNameWithoutExt + "(" + count + ")" + fileExtension;
	            destinationPath = parentDir.resolve(newName);
	            count++;
	        }

	        return destinationPath;
	    }

	    private String getFileNameWithoutExtension(String fileName) {
	        int dotIndex = fileName.lastIndexOf('.');
	        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
	    }

	    private String getFileExtension(String fileName) {
	        int dotIndex = fileName.lastIndexOf('.');
	        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
	    }

	

}