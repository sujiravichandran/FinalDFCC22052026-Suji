package com.teclever.dfcc.datastore.filemanagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.RunPathMaster;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.datastore.service.MacroService;
import com.teclever.datastore.service.RunPathMasterService;
import com.teclever.datastore.service.SymbolService;
import com.teclever.datastore.service.TestFileService;
import com.teclever.dfcc.datastore.dto.AddCustomFileResponse;
import com.teclever.dfcc.datastore.dto.AddFilesDetailsDTO;
import com.teclever.dfcc.datastore.dto.VDDDto;
import com.teclever.dfcc.utils.Debug;

public class CustomFileAddManagement {

	public Map<String, String> copyingListOfFiles(Path currentDir, Path pathMasterDir, List<String> fileNames) {

		Map<String, String> FileNamesmsg = new HashMap<>();
		/*
		 * List<String> filesToCopy = List.of( "Checking1.txt", "Checking2.txt",
		 * "Checking3.txt" );
		 * 
		 * currentDir = Paths.get("C:\\Users\\manik\\Downloads\\"); pathMasterDir =
		 * Paths.get("C:\\Users\\manik\\Documents\\");
		 */

		// Validate if source directory exists
		System.out.println("In CustomFileAdd:currentDir:" + currentDir);
		if (Files.notExists(currentDir)) {
			Debug.printDebug("Source directory does not exist: " + currentDir);
			System.out.println("NO FILES IN CURRENT DIR");
			return FileNamesmsg;
		}

		try {
			System.out.println("In CustomFileAdd:fileNames:" + fileNames);
			for (String fileName : fileNames) {
				Path currentFile = currentDir.resolve(fileName);
				Path targetFile = pathMasterDir.resolve(fileName);

				// Validate if the source file exists
				if (Files.notExists(currentFile)) {
					System.out.println("Files Not Exist in Current Dir");
					Debug.printDebug("File does not exist: " + currentFile);
					continue;
				}
				
				System.out.println("Checking if file exists: " + targetFile);
				// Skip if the target file already exists
				if (Files.exists(targetFile)) {
//					FileNamesmsg.put(fileName, "Already Exist");
					Debug.printDebug("Already Exist" + "File Name :" + fileName);
					continue;
				}

				// Ensure the target directory exists
				if (Files.notExists(targetFile.getParent())) {
					Files.createDirectories(targetFile.getParent());
				}

				// Copy the file
					
				System.out.println("Before copy: Does target exist? " + Files.exists(targetFile));
				Files.copy(currentFile, targetFile);
				System.out.println("After copy: Does target exist? " + Files.exists(targetFile));
				Debug.printDebug(targetFile.toString() + "File Copied");
				FileNamesmsg.put(fileName, "File Copied");
				System.out.println("In CustomFileAdd:FileNamesmsg:" + FileNamesmsg);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FileNamesmsg;
	}

	public void separateFilesPathAndName(List<String> filePaths) {

		List<String> fileNames = new ArrayList<String>();
		for (String filePath : filePaths) {

			Path path = Paths.get(filePath);
			String fileName = path.getFileName().toString();
			fileNames.add(fileName);
			String basePath = path.getParent().toString();
			Debug.printDebug("Base Path: " + basePath);
			Debug.printDebug("File Name: " + fileName);
		}

	}

	public AddCustomFileResponse addCustomFiles(String runId, List<String> filePaths, String fileType) {
		AddCustomFileResponse res = new AddCustomFileResponse();
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			RunPathMaster runMaster = new RunPathMaster();
			runMaster = runPathMasterService.fetchMasterPathForFiles(runId, fileType);
			Path masterPath = Paths.get(runMaster.getLocation());
			System.out.println("Custom File String:masterPath" + masterPath);

			// To Fetch Files Name And FileNamePaths...
			List<String> fileNames = new ArrayList<>();
			Map<String, String> filesNamesPath = new HashMap<String, String>();
			String currentFileString = "";
			for (String filePath : filePaths) {
				Path path = Paths.get(filePath);
				String files = path.getFileName().toString();
				filesNamesPath.put(files, filePath);
				fileNames.add(files);
				currentFileString = path.getParent().toString();

			}
			Path currentPath = Paths.get(currentFileString);
			
			System.out.println("Custom File String:currentPath" + currentPath);

			// To Get the Which Files are Not Available in the Path Master Location
			Map<String, String> FilesMsg = copyingListOfFiles(currentPath, masterPath, fileNames);
			Map<String, String> filePathCheckSumValues = new HashMap<String, String>();
			
			System.out.println("Custom File String:FilesMsg" + FilesMsg.size());
			System.out.println("Custom File String:FilesMsg" + FilesMsg);

			if (FilesMsg.size() < 1) {
				res.setResponseCode(0);
				res.setResponseMsg("All Files are Already Exist's");
			}
			List<String> availbleFilePaths = new ArrayList<String>();
			List<String> pathMasterFilePaths = new ArrayList<String>();
			// Available files Creating CheckSum For Files...
			if (FilesMsg.size() > 0) {
				for (String fileName : FilesMsg.keySet()) {
					String pathFile = filesNamesPath.get(fileName);
					Path path = Paths.get(pathFile);
					String pathMasterPaths = runMaster.getLocation()+path.getFileName().toString();
					pathMasterFilePaths.add(pathMasterPaths);
					// masterpath
					availbleFilePaths.add(pathFile);
					String checksum = calculateChecksum(path);
					Debug.printDebug("File Path: " + pathFile + "File Name: " + path.getFileName().toString()
							+ " Checksum: " + checksum);
					filePathCheckSumValues.put(pathFile, checksum);
				}
			}

			// Put Into VDDDTO List For Adding CheckSum TO DB For Availabe Files
			List<VDDDto> vDDDtoList = new ArrayList<VDDDto>();
			if (filePathCheckSumValues.size() > 0) {
				for (String filePath : filePathCheckSumValues.keySet()) {
					VDDDto v1 = new VDDDto();
					v1.setFilePath(filePath);
					v1.setFileCheckSum(filePathCheckSumValues.get(filePath));
					Path path = Paths.get(filePath);
					String fileName = path.getFileName().toString();
					Debug.printDebug("fileName---------"+fileName);
					v1.setFileName(fileName);
					v1.setBaseFileName("Custom Added File's");
					Debug.printDebug("File Name  :" + fileName + "CheckSum :" + filePathCheckSumValues.get(filePath));
					vDDDtoList.add(v1);
				}
			}
			VDDManagement vDDManagement = new VDDManagement();
			Response res1 = vDDManagement.addVDDList(vDDDtoList);
			/*
			 * if (res1.getResponseCode() != 1) {
			 * res.setResponseCode(res1.getResponseCode());
			 * res.setResponseMsg(res1.getResponseMessage()); return res; }
			 */

			// Symbol File Parser
			if (availbleFilePaths.size() > 0) {
				// List<SymbolDto>symbolList = SymbolFileParser.parseSymbols(availbleFilePaths,
				// runId);
				// SymbolFileManagement symbolFileManagement = new SymbolFileManagement();

				/*
				 * switch (fileType) { case "symbol":
				 * SymbolFileManagement.saveSymbols(availbleFilePaths,
				 * runMaster.getRunPathMasterId()); case "test":
				 * TestPlanFileManagement.saveTestFilesToDatabase(availbleFilePaths,
				 * runMaster.getRunPathMasterId()); case "macro":
				 * MacroFileManagement.saveMacroNames(availbleFilePaths,
				 * runMaster.getRunPathMasterId()); default: Debug.printDebug("Default"); }
				 */

				if (fileType.equalsIgnoreCase("symbols")) {
					SymbolFileManagement.saveSymbolsForCustomFiles(pathMasterFilePaths, runMaster.getRunPathMasterId());

				} else if (fileType.equalsIgnoreCase("tpf")) {
					TestPlanFileManagement.saveTestFilesToDatabaseForCustomFiles(pathMasterFilePaths, runMaster.getRunPathMasterId());

				} else if (fileType.equalsIgnoreCase("macros")) {
					MacroFileManagement.saveMacroNamesForCustomFiles(pathMasterFilePaths, runMaster.getRunPathMasterId());
				} else if (fileType.equalsIgnoreCase("download")) {
					DownloadFileManagement.saveDownloadFilesToDatabaseForCustomAdding(pathMasterFilePaths, runMaster.getRunPathMasterId());
				}
				
				else {
					Debug.printDebug("File Type is Invalid..");
				}
				List<AddFilesDetailsDTO> addedFileList = new ArrayList<AddFilesDetailsDTO>();
				List<AddFilesDetailsDTO> existFileList = new ArrayList<AddFilesDetailsDTO>();
				for (String filePath : filePaths) {
					if (!availbleFilePaths.contains(filePath)) {
						AddFilesDetailsDTO notExistFileDetails = new AddFilesDetailsDTO();
						Path path = Paths.get(filePath);
						notExistFileDetails.setFileName(path.getFileName().toString());
						notExistFileDetails.setFilePath(path.getParent().toString());
						notExistFileDetails.setMsg("File Already Exist..");
						existFileList.add(notExistFileDetails);

					} else {
						AddFilesDetailsDTO addFileDetails = new AddFilesDetailsDTO();
						Path path = Paths.get(filePath);
						addFileDetails.setFileName(path.getFileName().toString());
						addFileDetails.setFilePath(path.getParent().toString());
						addFileDetails.setMsg("File Added Successfully");
						addedFileList.add(addFileDetails);
					}

				}
				
				
				res.setResponseMsg("All Files are Added");
				res.setResponseCode(1);
				res.setAddedFilesDetailsList(addedFileList);
				if (existFileList.size() > 0) {
					res.setResponseMsg("All Files are Not Added Some Files Are Exist'(s)");
					res.setResponseCode(0);
					res.setExistingFileDetailsList(existFileList);

				}
			}

		} catch (Exception ex) {

			res.setResponseMsg("Error Files are Not Added");
			res.setResponseCode(0);

		}
		return res;

	}

	public String calculateChecksum(Path path) throws NoSuchAlgorithmException, IOException {

		MessageDigest digest = MessageDigest.getInstance("MD5");

		try (FileInputStream fis = new FileInputStream(path.toFile())) {
			byte[] byteArray = new byte[1024];
			int bytesCount;
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
		}

		byte[] bytes = digest.digest();

		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}

		return sb.toString();
	}

	public Response deleteFile(String filePath,String fileType)
	{
		Response res = new Response();
		try
		{
			deleteFileByPath(filePath);
			if(fileType.equalsIgnoreCase("tpf"))
			{
				TestFileService testFileService = new TestFileService();
				testFileService.deleteTestByFileNameMarkAsDelete(filePath);
			}else if(fileType.equalsIgnoreCase("symbols"))
			{
				SymbolService symbolService = new SymbolService();
				symbolService.deleteSymbolByFileNameMarkedAsDelete(filePath);
				
			}
			else if(fileType.equalsIgnoreCase("macros")){
				MacroService macroService = new MacroService();
				macroService.deleteMacrosByFileNameMarkAsDelete(filePath);
			}else if(fileType.equalsIgnoreCase("download")){
				DownloadFileService downloadFileService = new DownloadFileService();
				downloadFileService.deleteDownloadFileByFileNameMarkedAsDelete(filePath);
			}
			else
			{
				Debug.printDebug("Error");
			}
			res.setResponseCode(1);
			res.setResponseMessage("Deleted");
			
		}catch(Exception ex)
		{
			res.setResponseCode(0);
			res.setResponseMessage("Not Deleted  Error:"+ex.getLocalizedMessage());
		}
		return res;
	}
	
	public void deleteFileByPath(String fileName)
	{
	 Path filePath = Paths.get(fileName);
     
     try {
         // Delete the file
         Files.delete(filePath);
         Debug.printDebug("File deleted successfully.");
     } catch (Exception e) {
         System.err.println("File Not deleted");
     }
	}
}
