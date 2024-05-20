package com.teclever.dfcc.datastore.filemanagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
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
import com.teclever.datastore.service.RunPathMasterService;
import com.teclever.dfcc.datastore.dto.VDDDto;

public class CustomFileAddManagement {

	// Add Custom Files
	public Response addCustomFiles(String runId, List<String> filePaths,String fileType) {
		Response res = new Response();
		try {
			switch (fileType) {
			case "S":
				;
			case "T":
				;
			case "M":
				;
			default:
				System.out.println("Default");
				;
			}
		} catch (Exception ex) {

		}
		return res;
	}

	public Map<String,String> copyingListOfFiles(Path currentDir, Path pathMasterDir, List<String> fileNames) {
		
		Map<String,String>FileNamesmsg = new HashMap<String,String>();
		/*
		 * List<String> filesToCopy = List.of( "Checking1.txt", "Checking2.txt",
		 * "Checking3.txt" );
		 * 
		 * currentDir = Paths.get("C:\\Users\\manik\\Downloads\\"); pathMasterDir =
		 * Paths.get("C:\\Users\\manik\\Documents\\");
		 */

		// Validate if source directory exists
		if (Files.notExists(currentDir)) {
			System.out.println("Source directory does not exist: " + currentDir);
			return null;
		}

		try {
			
			
			for (String fileName : fileNames) {
				Path currentFile = currentDir.resolve(fileName);
				Path targetFile = pathMasterDir.resolve(fileName);

				// Validate if the source file exists
				if (Files.notExists(currentFile)) {
					System.out.println("File does not exist: " + currentFile);
					continue;
				}

				// Skip if the target file already exists
				if (Files.exists(targetFile)) {
				//	FileNamesmsg.put(fileName, "Already Exist");
					
					continue;
				}

				// Ensure the target directory exists
				if (Files.notExists(targetFile.getParent())) {
					Files.createDirectories(targetFile.getParent());
				}

				// Copy the file
				Files.copy(currentFile, targetFile);
				FileNamesmsg.put(fileName, "File Copied");
	
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
			System.out.println("Base Path: " + basePath);
			System.out.println("File Name: " + fileName);
		}

	}

	public Response addCustomSymbolFiles(String runId, List<String> filePaths,String fileType) {
		Response res = new Response();
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			RunPathMaster runMaster = new RunPathMaster();
			runMaster=	runPathMasterService.fetchMasterPathForFiles(runId,fileType);
			Path masterPath = Paths.get(runMaster.getLocation());

		
			
			//To Fetch Files Name And FileNamePaths...
			List<String> fileNames = new ArrayList<>();
			Map<String,String>filesNamesPath = new HashMap<String,String>();
			String currentFileString = "";
			for (String filePath : filePaths) {
				Path path = Paths.get(filePath);
				String files = path.getFileName().toString();
				filesNamesPath.put(files,filePath);
				fileNames.add(files);
				currentFileString = path.getParent().toString();
				
			}
			Path currentPath = Paths.get(currentFileString);
			
			//To Get the Which Files are Not Available in the Path Master Location
			Map<String, String> FilesMsg = copyingListOfFiles(currentPath, masterPath, fileNames);
			Map<String, String> filePathCheckSumValues = new HashMap<String, String>();
			
			
			//Available files Creating CheckSum For Files...
			for (String fileName : FilesMsg.keySet()) {
			    String pathFile =	filesNamesPath.get(fileName);
				Path path = Paths.get(pathFile);
				String checksum = calculateChecksum(path);
				System.out.println("File Path: " + pathFile + "File Name: "+ path.getFileName().toString()  + " Checksum: " + checksum);
				filePathCheckSumValues.put(pathFile, checksum);
			}
			
			//Put Into VDDDTO List For Adding CheckSum TO DB For Availabe Files
			List<VDDDto>vDDDtoList = new ArrayList<VDDDto>();
			if (filePathCheckSumValues.size() > 0) {
				for (String filePath : filePathCheckSumValues.keySet()) {
					VDDDto v1 = new VDDDto();
					v1.setFilePath(filePath);
					v1.setFileCheckSum(filePathCheckSumValues.get(filePath));
					Path path = Paths.get(filePath);
					String fileName = path.getFileName().toString();
					v1.setFileName(fileName);
					v1.setBaseFileName("Custom Adding");

				}
			}
			VDDManagement vDDManagement = new VDDManagement();
			Response res1 = vDDManagement.addVDDList(vDDDtoList);
			if(res1.getResponseCode()!=1)
			{
				res.setResponseCode(res1.getResponseCode());
				res.setResponseMessage(res1.getResponseMessage());
				return res;
			}
			//Symbol File Parser
			
			
		} catch (Exception ex) {

		}
		return res;

	}

	public Response addCustomTestFiles(List<String>filePaths) {
		Response res = new Response();
		
		return res;
	}

	public Response addCustomMacroFiles(List<String>filePaths) {
		Response res = new Response();
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
	
	
}
