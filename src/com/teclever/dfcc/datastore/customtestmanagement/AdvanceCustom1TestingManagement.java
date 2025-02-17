package com.teclever.dfcc.datastore.customtestmanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.teclever.dfcc.utils.Debug;

public class AdvanceCustom1TestingManagement {

	static String currentDirectory = new File(
			AdvanceCustom1TestingManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath())
			.getParent();

	// static String customFileDir = currentDirectory + File.separator +
	// "CustomTesting1Files" + File.separator;

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
			Debug.printDebug(ex.getLocalizedMessage());
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
			Debug.printDebug(ex.getLocalizedMessage());
			symbolListResponse.setListOfSymbolDto(null);
			res.setResponseCode(0);
			res.setResponseMessage("Symbol Data is Empty ");
		}
		symbolListResponse.setResponse(res);
		return symbolListResponse;
	}

	// Running Single Symbol And Macro Command
		public String customOneRun(String runCommand, String testTypeId) {
			try {
				TestProcessManagement testProcess = new TestProcessManagement();
				testProcess.runCommand(runCommand, testTypeId , "CUSTOM ONE");
			} catch (Exception e) {
				return "NOT OK";
			}
			return "OK";
		}
		// Not Using
		public String customTwoRun(String runCommand, String testTypeId) {
			try {
				TestProcessManagement testProcess = new TestProcessManagement();
				testProcess.runCommand(runCommand, testTypeId ,"CUSTOM TWO");
			} catch (Exception e) {
				return "NOT OK";
			}
			return "OK";
		}

	// Creating Text File To Run Test
	public Response customOneRunTestFile(String stageId, String fileName, List<String> symbolMacroTextFormate,
			String testTypeId) {
		fileName = fileName + ".tst";
		Response res = new Response();
		String customFileDir;
		try {
			String uutType = currentSessionDetails.getUutType();
			switch (uutType) {
			case "MK-1":
				customFileDir = currentDirectory + File.separator + "CustomTesting1Files"+ File.separator + "MK-1" 
						+ File.separator;

				break;
			case "MK-1A":
				customFileDir = currentDirectory + File.separator + "CustomTesting1Files"+ File.separator + "MK-1A" 
						+ File.separator;
				break;
			case "MK-2":
				customFileDir = currentDirectory + File.separator + "CustomTesting1Files" + File.separator + "MK-2"
						+ File.separator;
				break;
			default:
				throw new IllegalArgumentException("Invalid uutType: " + uutType);
			}

			String fileNamewithFullPath = customFileDir + fileName;
			;

			// Debug.printDebug("fileNamewithFullPath " + fileNamewithFullPath);
			createDirectoryIfNotExists(customFileDir);
			if (directoryExist(customFileDir)) {
				if (!createFileIfNotExists(fileNamewithFullPath)) {
					res.setResponseCode(0);
					res.setResponseMessage("File Already Exist: Use Other Name ");
					return res;
				}

				File file = new File(fileNamewithFullPath);
				writeCommandsToFile(file, symbolMacroTextFormate);

				addCustomTest(fileName, customFileDir, file, "C1");

			res =	addTestFiletoStageAndStartTest(testTypeId, fileNamewithFullPath, stageId, "CUSTOM ONE");

			} else {
				Debug.printDebug("CustomTesting1Files Directory Not Present,Please Create");
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
//		res.setResponseCode(1);
//		res.setResponseMessage("Test Started");
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

	private void addCustomTest(String fileName, String filePath, File file, String customType)
			throws SQLException, IOException {
		CustomTestService customTestService = new CustomTestService();
		customTestService.addCustomTest(fileName, StateMachine.currentSessionDetails.getSessionId(),
				convertFileToBlob(file), filePath, customType);
	}

	private Response addTestFiletoStageAndStartTest(String testTypeId, String fileName, String stageId, String stageName) {
		Response res = new Response();
		try {

			List<String> listOfTestFileIds = addTestFileandGetFileId(fileName, testTypeId);
			TestFilesStagesMappingService testFileStageMapService = new TestFilesStagesMappingService();
			testFileStageMapService.addTestFilesStagesMapping(stageId, listOfTestFileIds);

			TestProcessManagement testProcessManangement = new TestProcessManagement();
			res =testProcessManangement.testProcesControl(StateMachine.currentSessionDetails.getSessionId(), stageId, 1,
					listOfTestFileIds /* listOfFileId */, true /* continueWithError */, stageName/* stageName */,
					testTypeId/* testTypeId */, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return res;
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
								Debug.printDebug(f.getName() + " deleted.");
							} else {
								Debug.printDebug("Failed to delete " + f.getName());
							}
						}
					}
				}
				returnFlag = true;
			} else {
				Debug.printDebug("Directory does not exist.");
				returnFlag = false;
			}
		} catch (Exception e) {
			throw e;
		}
		return returnFlag;
	}

	public Response customTwoRunTestFile(String stageId, String testfileName, String testTypeId) {
		Response res = new Response();
		try {
			File file = new File(testfileName);
			if (!file.exists()) {
				res.setResponseCode(0);
				res.setResponseMessage("File Not Exist In the Path " + testfileName);
				return res;

			}
			Path path = Paths.get(testfileName);

			// Get the file name
			String fileName = path.getFileName().toString();

			// Get the parent directory (path without the file name)
			String filePath = path.getParent().toString();

			// Print results
			Debug.printDebug("File Name: " + fileName);
			Debug.printDebug("Directory Path: " + filePath);

			addCustomTest(fileName, filePath, file, "C2");

			res = addTestFiletoStageAndStartTest(testTypeId, testfileName, stageId, "CUSTOM TWO");

		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(0);
			res.setResponseMessage("Test not Started : " + e.getLocalizedMessage());
		}
		return res;
	}

	public Response customTwoRunDownloadFile(String stageId, String downloadFilePath, String checkSumValueFile,
			String testTypeId) {
		Response res = new Response();
		try {
			String uutType = currentSessionDetails.getUutType();
			String basetestFilePath = "";
			String modifiedFilePath = "";
			switch (uutType) {
			case "MK-1":
				basetestFilePath = currentDirectory + File.separator + "download_files" + File.separator + "MK-1"
						+ File.separator + "basefile" + File.separator + "download_V7355.tpf";
				modifiedFilePath = currentDirectory + File.separator + "download_files" + File.separator + "MK-1"
						+ File.separator + "modifiedfile" + File.separator;
				break;
			case "MK-1A":
				basetestFilePath = currentDirectory + File.separator + "download_files" + File.separator + "MK-1A"
						+ File.separator + "basefile" + File.separator + "external_flash.tst";
				modifiedFilePath = currentDirectory + File.separator + "download_files" + File.separator + "MK-1A"
						+ File.separator + "modifiedfile" + File.separator;
				break;
			case "MK-2":
				basetestFilePath = currentDirectory + File.separator + "download_files" + File.separator + "MK-2"
						+ File.separator + "basefile" + File.separator + "external_flash.tst";
				modifiedFilePath = currentDirectory + File.separator + "download_files" + File.separator + "MK-2"
						+ File.separator + "modifiedfile" + File.separator;
				break;
			default:
				throw new IllegalArgumentException("Invalid uutType: " + uutType);
			}
			File testFile = new File(basetestFilePath);
			File downloadFile = new File(downloadFilePath);

			// Check base File directory and file Exists.
			if (!testFile.exists()) {
//				Debug.printDebug("Please Check File paths - " + basetestFilePath);
				res.setResponseCode(0);
				res.setResponseMessage("Please Check Base File path - " + basetestFilePath);
				return res;
			}
			if (!downloadFile.exists() /* || !checkSumFile.exists() */) {
				res.setResponseCode(0);
				res.setResponseMessage("Please Check Given File paths - " + downloadFilePath);
				return res;

			}
			Path path = Paths.get(downloadFilePath);
			Map<String, String> listOfCheckSum = null;
			if (checkSumValueFile != null) {
				File checkSumFile = new File(checkSumValueFile);
				if (!checkSumFile.exists()) {
					res.setResponseCode(0);
					res.setResponseMessage("Please Check Given File paths - " + checkSumValueFile);
					return res;

				}
				listOfCheckSum = extractCheckSumValues(checkSumFile);
			}

			File modifiedFile = filenameAndCheckSumModification(testFile, listOfCheckSum,
					path.getParent().toString() + File.separator, path.getFileName().toString(), modifiedFilePath);

			addCustomTest(modifiedFile.getName(), modifiedFile.getParent().toString() + File.separator, downloadFile,
					"C2");

			addTestFiletoStageAndStartTest(testTypeId, modifiedFile.getAbsolutePath(), stageId, "CUSTOM TWO");
			res.setResponseCode(1);
			res.setResponseMessage("Test Started ");
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(0);
			res.setResponseMessage("Test not Started : " + e.getLocalizedMessage());
		}
		return res;
	}

	private File filenameAndCheckSumModification(File baseTestFile, Map<String, String> listOfCheckSum,
			String downloadFilePath, String downloadFileName, String fileLocationToCopy) {

		File modifiedFile = new File(fileLocationToCopy + File.separator + baseTestFile.getName());
		
		// Define the regex pattern to match the path and file name
		Pattern pathFinePattern = Pattern.compile("DOWNLOAD =\\s*(.+/)([^/]+)$");
		Pattern downloadPathPattern = Pattern.compile("download=\\s*(.+/)([^/]+)$");
		// Define the regex pattern to match the Blocks(BLOCK1, BLOCK2, etc.)
		Pattern blockFindPattern = Pattern.compile("OPMSG ... CHECK SUM VERIFICATION FOR BLOCK-(\\d{1,2}) IN PROGRESS");

		// Define the regex pattern to match the Value lines
		Pattern valueFinPattern = Pattern.compile("tip # VR (\\S+) (\\S+) (\\S+)");

		try (BufferedReader br = new BufferedReader(new FileReader(baseTestFile));
				BufferedWriter bw = new BufferedWriter(new FileWriter(modifiedFile))) {
			String line;
			String currentBlock = null;
			while ((line = br.readLine()) != null) {
				Matcher blockMatcher = blockFindPattern.matcher(line);

				Matcher matcher = pathFinePattern.matcher(line);

				if (matcher.find()) {
					String filePath = matcher.group(1); // File Path
					String fileName = matcher.group(2);// File Name
					line = line.replace(filePath, downloadFilePath).replace(fileName, downloadFileName);
//					line = line.replace(fileName, downloadFileName);
				}
				Matcher downloadMatcher = downloadPathPattern.matcher(line);
				if (downloadMatcher.find()) {
					String filePath = downloadMatcher.group(1); // File Path
					String fileName = downloadMatcher.group(2);// File Name
					line = line.replace(filePath, downloadFilePath).replace(fileName, downloadFileName);
				}

				if (blockMatcher.find()) {

					// Get the current block number from the OPMSG line
					currentBlock = "BLOCK" + blockMatcher.group(1); // BLOCK1, BLOCK2, etc.

				} else if (currentBlock != null) {
					Matcher checkSumValueMatcher = valueFinPattern.matcher(line);
					if (checkSumValueMatcher.find()) {

						String oldChecksum = checkSumValueMatcher.group(3); // The original checksum value

						// Replace the old checksum with the new checksum from the map
						if (listOfCheckSum != null && listOfCheckSum.size() > 0) {
							String newChecksum = listOfCheckSum.get(currentBlock);
							if (newChecksum != null) {
								line = line.replace(oldChecksum, newChecksum); // Replace in the line
								currentBlock = null;
							}
						}
					}
				}
				// Write the modified line to the temporary file
				bw.write(line);
				bw.newLine(); // Add a new line to the temporary file
			}
			bw.close();
			br.close();
			// Replace the original file with the temporary file
//		if (downloadFile.delete()) {
//			tempFile.renameTo(downloadFile);
//			Debug.printDebug("File Deleted Succesfull  "+tempFile.getAbsolutePath()+"  "+downloadFile.getAbsolutePath());
//		} else {
//			Debug.printDebug("Could not delete the original file.");
//		}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return modifiedFile;
	}

	public Map<String, String> extractCheckSumValues(File checkSumFile) {
		Map<String, String> checksumMap = new LinkedHashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(checkSumFile))) {
			String line;
			Pattern pattern = Pattern.compile("CHECKSUM FOR (BLOCK\\d+) .* (\\w{8})$");

			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);

				if (matcher.find()) {
					String key = matcher.group(1); // BLOCK1, BLOCK2, etc.
					String value = matcher.group(2);// The checksum value
					checksumMap.put(key, value);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checksumMap;
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
				Debug.printDebug("Line data --  " + l);
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

			Debug.printDebug("File copied successfully to " + destinationPath.toString());
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

			Debug.printDebug("File copied successfully to " + destinationPath.toString());
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