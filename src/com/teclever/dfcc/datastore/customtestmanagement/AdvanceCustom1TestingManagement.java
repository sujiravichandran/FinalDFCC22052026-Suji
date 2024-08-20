package com.teclever.dfcc.datastore.customtestmanagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.teclever.datastore.entities.TestFile;
import com.teclever.datastore.service.CustomTestService;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.datastore.service.TestFileService;
import com.teclever.datastore.service.TestFilesStagesMappingService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.CustomTestFileResponse;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.MacroListResponse;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.dto.SymbolListResponse;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SymbolFileManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class AdvanceCustom1TestingManagement {

	static String currentDirectory = new File(
			AdvanceCustom1TestingManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath())
			.getParent();

	static String customFileDir = currentDirectory + File.separator + "CustomTesting1Files" + File.separator;

	// Custom Testing For Generating Macro On Drop Down..
	public MacroListResponse getAllMacrosForAdavanceTest(String uutTypeId, String testTypeId) {
		MacroListResponse macroListResponse = new MacroListResponse();
		Response res = new Response();
		try {
			List<MacroDto> macros = MacroFileManagement.getAllMacros(getRunConfigId(testTypeId));
			if (macros != null && macros.size() > 0) {

				macroListResponse.setListOfMacroDto(macros);
				res.setResponseCode(1);
				res.setResponseMessage("Data Fetching Succesfull ");
			} else {
				macroListResponse.setListOfMacroDto(macros);
				res.setResponseCode(0);
				res.setResponseMessage("Macro Data is Empty ");
			}

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			macroListResponse.setListOfMacroDto(null);
			res.setResponseCode(0);
			res.setResponseMessage("Error while Fetching Macro ");

		}
		macroListResponse.setResponse(res);
		return macroListResponse;
	}

	// Custom Testing For Generating the Symbols On the Drop Down
	public SymbolListResponse getAllSymbolsForAdavanceTest(String uutTypeId, String testTypeId) {
		SymbolListResponse symbolListResponse = new SymbolListResponse();
		Response res = new Response();
		try {
			List<SymbolDto> symbols = SymbolFileManagement.getAllSymbols(getRunConfigId(testTypeId));

			if (symbols != null && symbols.size() > 0) {

				symbolListResponse.setListOfSymbolDto(symbols);
				res.setResponseCode(1);
				res.setResponseMessage("Data Fetching Succesfull ");
			} else {
				symbolListResponse.setListOfSymbolDto(symbols);
				res.setResponseCode(0);
				res.setResponseMessage("Symbol Data is Empty ");
			}

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			symbolListResponse.setListOfSymbolDto(null);
			res.setResponseCode(0);
			res.setResponseMessage("Symbol Data is Empty ");
		}
		symbolListResponse.setResponse(res);
		return symbolListResponse;
	}

	// Running Single Symbol Command
	public String customOneRun(String symbolName, String minValue, String maxValue, String ipData) {

		String symbolCommand;
		try {
			symbolCommand = customOneAdd(symbolName, minValue, maxValue, ipData);
		} catch (Exception e) {
			return null;
		}
		return symbolCommand;

	}

	// Running Single Macro Command
	public String customOneRun(String macroName) {

		String macroCommand;
		try {
			macroCommand = macroName;
		} catch (Exception e) {
			return null;
		}
		return macroCommand;

	}

	// Return Formated Single Symbol Command
	public String customOneAdd(String symbolName, String minValue, String maxValue, String ipData) {

		String symbolCommand;
		try {
			symbolCommand = symbolName + " = " + ipData;

			/*
			 * SYMB=TTR1_I DEST=FCC1 IOTYPE=SPIL TYPE=U16 CHAN=1000 ADDR=206CC03E MASK=FFFF
			 * SLPE=1. BIAS=0. MIN=-32768. MAX=32767. READ=1 WRTE=1 UNIT=XXX
			 */

			symbolCommand = "SYMB=" + symbolName + " DEST=" + " IOTYPE= " + " TYPE=" + " CHAN=" + " ADDR=" + " MASK="
					+ ipData;

			symbolCommand = symbolCommand + "SLPE=" + " BIAS" + " MIN=" + minValue + " MAX=" + maxValue + " READ="
					+ " WRTE=" + " UNIT=";

		} catch (Exception e) {
			return null;
		}
		return symbolCommand;

	}

	// Return Formated Single Macro Command
	public String customOneAdd(String macroName) {

		String macroCommand;
		try {
			macroCommand = macroName;
		} catch (Exception e) {
			return null;
		}
		return macroCommand;

	}

	// Creating Text File To Run Test
	public Response customOneRunTestFile(String stageId, String stageName, String fileName,
			List<String> symbolMacroTextFormate, String testTypeId) {
		fileName = fileName + ".tst";
		Response res = new Response();
		String fileNamewithFullPath=customFileDir + fileName;
		try {
			createDirectoryIfNotExists(customFileDir);
			if (directoryExist(customFileDir)) {
				if (!createFileIfNotExists(fileNamewithFullPath)) {
					res.setResponseCode(0);
					res.setResponseMessage("File Already Exist: Use Other Name ");
					return res;
				}

				File file = new File(fileNamewithFullPath);
				writeCommandsToFile(file, symbolMacroTextFormate);

				addCustomTest(fileName, file);
				
				
				updateDB(testTypeId, fileName, stageId, stageName);
			} else {
				System.out.println("CustomTesting1Files Directory Not Present,Please Create");
				res.setResponseCode(0);
				res.setResponseMessage("Test Not Started : CustomTesting1Files Directory Not Present, Create It");
			}

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
		return res;
	}

	private void writeCommandsToFile(File file, List<String> commands) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			for (String command : commands) {
				bw.write(command);
				bw.newLine();
			}
		}
	}

	private void addCustomTest(String fileName, File file) throws SQLException, IOException {
		CustomTestService customTestService = new CustomTestService();
		customTestService.addCustomTest(fileName, StateMachine.currentSessionDetails.getSessionId(),
				convertFileToBlob(file), customFileDir, "custom1");
	}

	private void updateDB(String testTypeId, String fileName, String stageId, String stageName) {
		try {

			List<String> listOfTestFileIds = addTestFileandGetFileId(fileName, testTypeId);
			TestFilesStagesMappingService testFileStageMapService = new TestFilesStagesMappingService();
			testFileStageMapService.addTestFilesStagesMapping(stageId, listOfTestFileIds);

			TestProcessManagement testProcessManangement = new TestProcessManagement();
			testProcessManangement.testProcesControl(StateMachine.currentSessionDetails.getSessionId(), stageId, 1,
					listOfTestFileIds /* listOfFileId */, true /* continueWithError */, stageName/* stageName */,
					testTypeId/* testTypeId */);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createDirectoryIfNotExists(String directoryPath) {
		File theDir = new File(directoryPath);
		if (!theDir.exists()) {
			theDir.mkdirs();
		}
	}

	private boolean createFileIfNotExists(String filePath) throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			return false; // File already exists
		} else {
			return file.createNewFile(); // File created
		}
	}

	private String getRunConfigId(String testTypeId) {
		RunConfigurationService runConfigurationService = new RunConfigurationService();
		return runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);

	}

	private List<String> addTestFileandGetFileId(String fileName, String testTypeId) {
		TestFileService testFileService = new TestFileService();
		TestFile testFile = new TestFile();
		testFile.setTestFileName(fileName);

		testFile.setRunPathMasterId(getRunConfigId(testTypeId));
		TestFile dbSavedTestFile = testFileService.saveTestFile(testFile);

		String testFileId = dbSavedTestFile.getTestFileId();
		List<String> listOfTestFileIds = new ArrayList<>();
		listOfTestFileIds.add(testFileId);
		return listOfTestFileIds;
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

	// NOT USED
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

	// NOT USED
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