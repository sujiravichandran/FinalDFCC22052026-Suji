package com.teclever.dfcc.datastore.filemanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionStagesMapping;
import com.teclever.datastore.entities.SessionStagesSelectedTestFiles;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.entities.TestFilesStagesMapping;
import com.teclever.datastore.service.SessionSelectedStagesService;
import com.teclever.datastore.service.SessionStagesSelectedTestFilesService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.TestFilesStagesMappingService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.dto.CopyingListDTO;
import com.teclever.dfcc.datastore.dto.LogOutFileCopyResponse;
import com.teclever.dfcc.datastore.dto.ReportConfigDto;
import com.teclever.dfcc.datastore.dto.SessionStagesFileCopyingDTO;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Debug;

public class SessionFileManagement {

	private Path mark1Directory;
	private Path mark1aDirectory;
	private Path mark2Directory;
	private Path dfccSerialNoDirectory;
	private Path sessionDirectory;
	private Path currentOutputFolder; // Track current output folder for session

	public SessionFileManagement() {
		currentOutputFolder = null;
	}

	// Create session folders
	public void createSessionFolders(String uutType, String dfccSerialNumber, String sessionName,
			List<List<String>> levelSets) {
		try {
			String currentDirectory = new File(
					SessionFileManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath())
					.getParent();

			// StateMachine.setHomelocation(Paths.get("C:\\testingSession"));
			StateMachine.setHomelocation(Paths.get(currentDirectory));

			Debug.printDebug(currentDirectory);
			mark1Directory = StateMachine.getHomelocation().resolve("MK-1");
			mark1aDirectory = StateMachine.getHomelocation().resolve("MK-1A");
			mark2Directory = StateMachine.getHomelocation().resolve("MK-2");

			if (Files.notExists(mark1Directory)) {
				Files.createDirectory(mark1Directory);
			}
			if (Files.notExists(mark1aDirectory)) {
				Files.createDirectory(mark1aDirectory);
			}
			if (Files.notExists(mark2Directory)) {
				Files.createDirectory(mark2Directory);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			dfccSerialNoDirectory = null;
			switch (uutType) {
			case "MK-1":
				dfccSerialNoDirectory = mark1Directory.resolve(dfccSerialNumber);
				break;
			case "MK-1A":
				dfccSerialNoDirectory = mark1aDirectory.resolve(dfccSerialNumber);
				break;
			case "MK-2":
				dfccSerialNoDirectory = mark2Directory.resolve(dfccSerialNumber);
				break;
			default:
				throw new IllegalArgumentException("Invalid uutType: " + uutType);
			}

			if (dfccSerialNoDirectory != null && !Files.exists(dfccSerialNoDirectory)) {
				Files.createDirectories(dfccSerialNoDirectory);
			} else {
				Debug.printDebug("dfcc serial number folder already exists: " + dfccSerialNoDirectory);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			sessionDirectory = null;
			switch (uutType) {
			case "MK-1":
			case "MK-1A":
			case "MK-2":
				// sessionDirectory = dfccSerialNoDirectory.resolve(sessionName + "_" +
				// dfccSerialNumber + "_" + new
				// SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()));
				sessionDirectory = dfccSerialNoDirectory.resolve(sessionName);

				break;
			default:
				throw new IllegalArgumentException("Invalid uutType: " + uutType);
			}

			if (sessionDirectory != null && !Files.exists(sessionDirectory)) {
				Files.createDirectories(sessionDirectory);
				Files.createDirectories(sessionDirectory.resolve("upload"));
				Files.createDirectories(sessionDirectory.resolve("datapack"));
				Files.createDirectories(sessionDirectory.resolve("report"));
				Files.createDirectories(sessionDirectory.resolve("Advance Testing"));

				// Creating four more folders inside "Advance Testing"
				Path advanceTestingDirectory = sessionDirectory.resolve("Advance Testing");
				Files.createDirectories(advanceTestingDirectory.resolve("HWATP HSI Testing"));
				Files.createDirectories(advanceTestingDirectory.resolve("Interface Testing"));
				Files.createDirectories(advanceTestingDirectory.resolve("Custom Testing 01"));
				Files.createDirectories(advanceTestingDirectory.resolve("Custom Testing 02"));

				// Create multiple sets of levels
				for (List<String> levels : levelSets) {
					createLevel(sessionDirectory, levels, 0);
				}
			} else {
				Debug.printDebug("Session folder already exists: " + sessionDirectory);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createLevel(Path basePath, List<String> levels, int index) throws IOException {
		Path currentPath = basePath.resolve(levels.get(index).trim());
		Files.createDirectories(currentPath);

		// Track output folder path for the current session
		if (index == levels.size() - 1) {
			currentOutputFolder = currentPath;
			Files.createDirectories(currentOutputFolder); // Create output folder for this stage
		}

		// Recursively create sub-levels and stages
		if (index < levels.size() - 1) {
			createLevel(currentPath, levels, index + 1);
		}
	}

	// checking the is All Are TestFiles Runned
	public boolean getTestFilesRunnedSuccess(String sessionId, String stageId) {
		boolean popupShowed = false;
		try {

			// Debug.printDebug("Enter Into getTestFilesRunnedSuccess");
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse getResponseStageTestFileResult = sessionStagesTestFilesResultService
					.getTestResultFileBySessionIdAndStageId(sessionId, stageId);

			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultServiceList = new ArrayList<SessionStagesTestFilesResult>();
			Map<String, SessionStagesTestFilesResult> fileIdObj = new HashMap<String, SessionStagesTestFilesResult>();
			List<String> fileIdsRunnedInStages = new ArrayList<String>();

			if (getResponseStageTestFileResult.getCode() != 0) {
				sessionStagesTestFilesResultServiceList = (List<SessionStagesTestFilesResult>) getResponseStageTestFileResult
						.getResponseList();
				if (sessionStagesTestFilesResultServiceList != null) {

					Debug.printDebug("sessionStagesTestFilesResultServiceList Size"
							+ sessionStagesTestFilesResultServiceList.size());
					for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultServiceList) {
						fileIdsRunnedInStages.add(sessionStagesTestFilesResult.getSelectedtestFileId());

					}
				}

			}

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObject = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping session = new SessionStagesMapping();
			session = (SessionStagesMapping) getObject.getObject();
			String sessionStagesMappingId = session.getSessionStagesMappingId();
			String stagePath = session.getPath();
			Debug.printDebug("sessionStagesMappingId" + sessionStagesMappingId);
			Debug.printDebug("stagePath" + stagePath);

			SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
			GetResponse resStagesMap = sessionStagesSelectedTestFilesService
					.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
			List<SessionStagesSelectedTestFiles> sessionStagesSelectedTestFilesList = new ArrayList<SessionStagesSelectedTestFiles>();
			sessionStagesSelectedTestFilesList = (List<SessionStagesSelectedTestFiles>) resStagesMap.getResponseList();
			Map<String, String> sessionStagesSelectedTestFilesIdAndTestFileId = new HashMap<String, String>();
			for (SessionStagesSelectedTestFiles sssTestFiles : sessionStagesSelectedTestFilesList) {
				sessionStagesSelectedTestFilesIdAndTestFileId.put(sssTestFiles.getSessionStagesSelectedTestFilesId(),
						sssTestFiles.getTestFilesId());
				Debug.printDebug("sessionStagesSelectedTestFilesList" + sessionStagesSelectedTestFilesList.size());
			}

			TestFilesStagesMappingService testFilesStagesMappingService = new TestFilesStagesMappingService();
			GetResponse getResponseFileMapping = testFilesStagesMappingService
					.getTestFilesStagesMappingByLastLevelReference(stageId);
			List<TestFilesStagesMapping> testFilesStagesMappingList = new ArrayList();
			testFilesStagesMappingList = (List<TestFilesStagesMapping>) getResponseFileMapping.getResponseList();
			List<String> filesMappingIds = new ArrayList<String>();
			if (getResponseFileMapping.getCode() != 0) {
				testFilesStagesMappingList = (List<TestFilesStagesMapping>) getResponseFileMapping.getResponseList();
				if (testFilesStagesMappingList != null) {
					Debug.printDebug("testFilesStagesMappingList Size" + testFilesStagesMappingList.size());
					for (TestFilesStagesMapping testFilesStagesMapping : testFilesStagesMappingList) {
						filesMappingIds.add(testFilesStagesMapping.getTestFileId());
					}
				}

			}
			List<SessionStagesTestFilesResult> lstByKeys = new ArrayList<SessionStagesTestFilesResult>();

			boolean notRunnedAll = false;
			boolean runnedAllSuccess = true;
			if (filesMappingIds.size() > 0 && fileIdsRunnedInStages.size() > 0) {
				for (String fileId : filesMappingIds) {

					if (sessionStagesSelectedTestFilesIdAndTestFileId.values().contains(fileId)) {

						List<String> keys = getKeysByValue(sessionStagesSelectedTestFilesIdAndTestFileId, fileId);

						// List<SessionStagesTestFilesResult> lstByKeys = new
						// ArrayList<SessionStagesTestFilesResult>();

						for (String key : keys) {
							Debug.printDebug("Entered KEYS  ==");
							List<SessionStagesTestFilesResult> lst = sessionStagesTestFilesResultServiceList.stream()
									.filter(stage -> stage.getSelectedtestFileId().equals(key))
									.collect(Collectors.toList());
							if (lst != null) {
								lstByKeys.addAll(lst);
							}
						}

						if (lstByKeys != null) {

							/*
							 * List<SessionStagesTestFilesResult> lstFilter = lst.stream() .filter(stage ->
							 * stage.getTestStatus().equalsIgnoreCase("failure"))
							 * .collect(Collectors.toList()); if(lstFilter!=null) { runnedAllSuccess =
							 * false; }
							 */
							Debug.printDebug("lstByKeys -" + lstByKeys.size());
							for (SessionStagesTestFilesResult s : lstByKeys) {
								{
									/*
									 * String dStarCount = s.getdStarCount(); int dStarCountInt =
									 * Integer.parseInt(dStarCount); if (dStarCountInt > 0) { runnedAllSuccess =
									 * false; }
									 */
									String testStatus = s.getTestStatus();
									if (testStatus != null && !testStatus.equals("")
											&& !testStatus.equalsIgnoreCase("SUCCESS")) {
										runnedAllSuccess = false;
									}
								}

							}
						}
						/*
						 * else { notRunnedAll = true; }
						 */

					} else {
						notRunnedAll = true;
					}
				}
			}

			if (!notRunnedAll && runnedAllSuccess) {
				// stagePath = "C:\\Users\\TECLEVER\\Downloads\\Copied\\";
				Debug.printDebug("STAGE PATH..." + stagePath);
				Path outputPath = Path.of(stagePath);
				List<Path> listOfPath = new ArrayList<Path>();
				Debug.printDebug("lstByKeys...:" + lstByKeys.size());
				for (SessionStagesTestFilesResult service : lstByKeys) {
					String pathString = service.getRdfPath() + service.getRdfFileName();
					Path path = Path.of(pathString);
					listOfPath.add(path);
					Debug.printDebug("PATH...." + pathString);
				}

				copyFilesToOutputFolder(listOfPath, outputPath);
			}

			Debug.printDebug("notRunnedAll----FLAG" + notRunnedAll);
			Debug.printDebug("runnedAllSuccess----FLAG" + runnedAllSuccess);

			if (!notRunnedAll && !runnedAllSuccess) {
				popupShowed = true;
				Debug.printDebug("Popup Showed Flag True Status Activated");

			}

		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return popupShowed;
	}

	// OLD Method to copy a list of files to the output folder
		/*public void copyFilesToOutputFolder(List<Path> sourceFiles, Path outputFolder) {
			try {
				Debug.printDebug("Enter To the Method copyFilesToOutputFolder");
				if (outputFolder != null && Files.exists(outputFolder)) {
					for (Path sourceFile : sourceFiles) {
						Path destinationFile = outputFolder.resolve(sourceFile.getFileName());
						Files.copy(sourceFile, destinationFile);
						Debug.printDebug("Copied file " + sourceFile.getFileName() + " to " + destinationFile);
					}
				} else {
					Debug.printDebug("Output folder does not exist for the current session.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	
	//New Method to copy a list of files to the output folder
	public void copyFilesToOutputFolder(List<Path> sourceFiles, Path outputFolder) {
		try {
			Debug.printDebug("Enter To the Method copyFilesToOutputFolder");

			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String dateFolder = currentDateTime.format(formatter);

			Path newFolderPath = outputFolder.resolve(dateFolder);

			if (!Files.exists(newFolderPath)) {
				Files.createDirectories(newFolderPath);
				Debug.printDebug("Folder created at: " + newFolderPath.toString());

			} else {
				Debug.printDebug("Folder Already Exits On : " + newFolderPath.toString());
			}

			if (newFolderPath != null && Files.exists(newFolderPath)) {
				for (Path sourceFile : sourceFiles) {
					Path destinationFile = newFolderPath.resolve(sourceFile.getFileName());
					Files.copy(sourceFile, destinationFile);
					Debug.printDebug("Copied file " + sourceFile.getFileName() + " to " + destinationFile);
				}
			} else {
				Debug.printDebug("Output folder does not exist for the current session.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void copyFilesToOutputFolder(Path sourceFile) {
		try {
			if (currentOutputFolder != null && Files.exists(currentOutputFolder)) {
				Path destinationFile = currentOutputFolder.resolve(sourceFile.getFileName());
				Files.copy(sourceFile, destinationFile);
				Debug.printDebug("Copied file " + sourceFile.getFileName() + " to " + destinationFile);
			} else {
				Debug.printDebug("Output folder does not exist for current session.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <K, V> K getKeyByValue(Map<K, V> map, V value) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null; // Return null if value is not found
	}

	public <K, V> List<K> getKeysByValue(Map<K, V> map, V value) {
		List<K> keys = new ArrayList<>();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				keys.add(entry.getKey());
			}
		}
		return keys; // Return list of keys with matching value
	}

	// Not Use
	public CopyingListDTO getShowPopupContentAllOfFilesFromStage(String sessionId, String stageId) {
		CopyingListDTO response = new CopyingListDTO();
		try {
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse getResponseStageTestFileResult = sessionStagesTestFilesResultService
					.getTestResultFileBySessionIdAndStageId(sessionId, stageId);
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultServiceList = new ArrayList<SessionStagesTestFilesResult>();
			sessionStagesTestFilesResultServiceList = (List<SessionStagesTestFilesResult>) getResponseStageTestFileResult
					.getResponseList();
			Map<String, SessionStagesTestFilesResult> fileIdObj = new HashMap<String, SessionStagesTestFilesResult>();
			String fromPath = "";
			List<CopyFileDTO> lst = new ArrayList<CopyFileDTO>();
			if (sessionStagesTestFilesResultServiceList.size() > 0) {
				for (SessionStagesTestFilesResult sesStageTFR : sessionStagesTestFilesResultServiceList) {
					CopyFileDTO copyFileDTO = new CopyFileDTO();
					copyFileDTO.setCopyingFileId(sesStageTFR.getSessionStagesTestFilesResultId());
					copyFileDTO.setRdfFiledName(sesStageTFR.getRdfFileName());
					copyFileDTO.setRdfFileNamewithPath(sesStageTFR.getRdfPath() + sesStageTFR.getRdfFileName());
					copyFileDTO.setRdfFilePath(sesStageTFR.getRdfPath());
					copyFileDTO.setCopyingFileId(sesStageTFR.getSessionStagesTestFilesResultId());
					copyFileDTO.setStatus(sesStageTFR.getTestStatus());
					lst.add(copyFileDTO);
				}
			}
			fromPath = sessionStagesTestFilesResultServiceList.get(0).getRdfPath();
			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObject = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping session = new SessionStagesMapping();
			session = (SessionStagesMapping) getObject.getObject();
			String sessionStagesMappingId = session.getSessionStagesMappingId();
			String stagePath = session.getPath();
			response.setLst(lst);
			response.setCode(1);
			response.setCodeMsg("Fetched Succesfully..");
			response.setFromPath(fromPath);
			response.setToPath(stagePath);
		} catch (Exception ex) {
			response.setCode(0);
			response.setCodeMsg("Un Fetched Succesfully.." + ex.getLocalizedMessage());

		}
		return response;
	}
	
	//Last SessionMap Id Test File Results...Only
	public CopyingListDTO getShowPopupContent(String sessionId, String stageId) {
		CopyingListDTO response = new CopyingListDTO();
		try {

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObject = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping sessionStageMapObj = new SessionStagesMapping();
			sessionStageMapObj = (SessionStagesMapping) getObject.getObject();
			String sessionStagesMappingId = sessionStageMapObj.getSessionStagesMappingId();
			response.setToPath(sessionStageMapObj.getPath());
			
			SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
			GetResponse getResponsetestFiles = new GetResponse();
			getResponsetestFiles = sessionStagesSelectedTestFilesService
					.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
			List<SessionStagesSelectedTestFiles> sessionStagesSelectedTestFilesList = new ArrayList<SessionStagesSelectedTestFiles>();
			sessionStagesSelectedTestFilesList = (List<SessionStagesSelectedTestFiles>) getResponsetestFiles
					.getResponseList();
			List<String>selectedTestFileIds = new ArrayList<String>();
			for(SessionStagesSelectedTestFiles sessionSelectedTFFiles:sessionStagesSelectedTestFilesList)
			{
				selectedTestFileIds.add(sessionSelectedTFFiles.getSessionStagesSelectedTestFilesId());
			}
			List<CopyFileDTO> copyingList = new ArrayList<CopyFileDTO>();
			String fromPath = "";
			if(selectedTestFileIds.size()>0)
			{
				
				SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
				GetResponse getResponseStageTestFileResult = sessionStagesTestFilesResultService
						.getTestFileResultListByselectTestFileIds(selectedTestFileIds);
				List<SessionStagesTestFilesResult> sessionStagesTestFilesResultServiceList = new ArrayList<SessionStagesTestFilesResult>();
				sessionStagesTestFilesResultServiceList = (List<SessionStagesTestFilesResult>) getResponseStageTestFileResult
						.getResponseList();
				
				for (SessionStagesTestFilesResult sesStageTFR : sessionStagesTestFilesResultServiceList) {
					CopyFileDTO copyFileDTO = new CopyFileDTO();
					copyFileDTO.setCopyingFileId(sesStageTFR.getSessionStagesTestFilesResultId());
					copyFileDTO.setRdfFiledName(sesStageTFR.getRdfFileName());
					copyFileDTO.setRdfFileNamewithPath(sesStageTFR.getRdfPath() + sesStageTFR.getRdfFileName());
					copyFileDTO.setRdfFilePath(sesStageTFR.getRdfPath());
					copyFileDTO.setCopyingFileId(sesStageTFR.getSessionStagesTestFilesResultId());
					copyFileDTO.setStatus(sesStageTFR.getTestStatus());
					fromPath = sesStageTFR.getRdfPath();
					copyingList.add(copyFileDTO);
				}
					
			}
			response.setLst(copyingList);
			response.setFromPath(fromPath);
			response.setCode(1);
			response.setCodeMsg("Fetched");
	//		Debug.printDebug("Selected Test Files List" + sessionStagesSelectedTestFilesList.size());
		} catch (Exception ex) {
			response.setCode(0);
			response.setCodeMsg("Un Fetched Succesfully.." + ex.getLocalizedMessage());

		}
		return response;
	}	

	// To Vignesh Implement
	// Selected File Copying Method..
	public Response copyingSelectedFile(List<CopyFileDTO> lst, String sessionId, String stageId) {
		Response response = new Response();
		try {

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObject = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping session = new SessionStagesMapping();
			session = (SessionStagesMapping) getObject.getObject();
			String sessionStagesMappingId = session.getSessionStagesMappingId();
			String stagePath = session.getPath();
			
			
			List<Path> paths = new ArrayList<Path>();
			for (CopyFileDTO copyFileDTO : lst) {
				paths.add(Path.of(copyFileDTO.getRdfFileNamewithPath()));
			}

			copyFilesToOutputFolder(paths, Path.of(stagePath));
			response.setResponseCode(1);
			response.setResponseMessage("Added");
		
		} catch (Exception ex) {
			response.setResponseCode(0);
			response.setResponseMessage(ex.getLocalizedMessage());
		}
		return response;
	}

	// datapack
	public void copyToDataPack(List<ReportConfigDto> list) {

		String dataPackPath = StateMachine.getHomelocation() + File.separator + currentSessionDetails.getUutType()
				+ File.separator + currentSessionDetails.getDfccSerialNumber() + File.separator
				+ currentSessionDetails.getSessionName() + File.separator + "datapack";
		Debug.printDebug("DATAPACK FOLDER CHECK ----- :: " + dataPackPath);
		File targetDir = new File(dataPackPath);

		if (!targetDir.exists()) {
			if (!targetDir.mkdirs()) {
				System.err.println("Failed to create directory: " + targetDir.getAbsolutePath());
				return;
			}
		}

		for (ReportConfigDto item : list) {
			StringBuilder hierarchy = new StringBuilder();

			if (item.getLevelOneName() != null) {
				hierarchy.append(item.getLevelOneName() + "_");
			}
			if (item.getLevelTwoName() != null) {
				hierarchy.append(item.getLevelTwoName() + "_");
			}
			if (item.getLevelThreeName() != null) {
				hierarchy.append(item.getLevelThreeName() + "_");
			}
			if (item.getLevelFourName() != null) {
				hierarchy.append(item.getLevelFourName() + "_");
			}
			if (item.getLevelFiveName() != null) {
				hierarchy.append(item.getLevelFiveName() + "_");
			}

			String filePath = item.getFileName();
			Path path = Paths.get(filePath);
			String fileName = path.getFileName().toString();

			hierarchy.append(fileName);

			File newFileLocation = new File(targetDir, hierarchy.toString());

			try {
				Files.copy(Paths.get(filePath), Paths.get(newFileLocation.getAbsolutePath()),
						StandardCopyOption.REPLACE_EXISTING);
				Debug.printDebug("File copied and renamed to: " + newFileLocation.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Failed to copy file: " + e.getMessage());

			}
		}
	}

	// upload
	public void copyToUpload(List<ReportConfigDto> list) {
//					String uploadPath="C:\\Suji\\New folder (3)\\2dy\\New folder";
		String uploadPath = StateMachine.getHomelocation() + File.separator + currentSessionDetails.getUutType()
				+ File.separator + currentSessionDetails.getDfccSerialNumber() + File.separator
				+ currentSessionDetails.getSessionName() + File.separator + "upload";
		Debug.printDebug("UPLOAD FOLDER CHECK ----- :: " + uploadPath);
		File targetDir = new File(uploadPath);
		if (!targetDir.exists()) {
			if (!targetDir.mkdirs()) {
				System.err.println("Failed to create directory: " + targetDir.getAbsolutePath());
				return;
			}
		}
		for (ReportConfigDto item : list) {
			String filePath = item.getFileName();
			Path path = Paths.get(filePath);
			String fileName = path.getFileName().toString();
			File newFileLocation = new File(targetDir, fileName);
			try {
				Files.copy(Paths.get(filePath), Paths.get(newFileLocation.getAbsolutePath()),
						StandardCopyOption.REPLACE_EXISTING);
				Debug.printDebug("File copied and renamed to: " + newFileLocation.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Failed to copy file: " + e.getMessage());
			}
		}
	}

	//Log Out Calling To Session Checking....
	public LogOutFileCopyResponse copyingFileWhileLogOut(String sessionId) {
		LogOutFileCopyResponse res = new LogOutFileCopyResponse();
		try {
			
			TestState currentState = StateMachine.getTestState();
			if (currentState == TestState.PENDING ) {
				Debug.printDebug("Any Test Not Runned....System Log Out");
				res.setCode(1);
				res.seteMsg("Any Test Not Runned");
			return res;
			
			}

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetResponse getResponse = sessionSelectedStagesService.getAllSessionStagesMappingBySessionId(sessionId);
			List<SessionStagesMapping> sessionMappingList = new ArrayList<SessionStagesMapping>();
			Map<String,String> sessionMapIdStagePath = new HashMap<String,String>();
			Map<String,String> selectedTFIdSessionMapId = new HashMap<String,String>();
			Map<String,String> testFileResultIdSelectedTFId = new HashMap<String,String>();
			Map<String,String> sessionMapIdStageId = new HashMap<String,String>();
			sessionMappingList = (List<SessionStagesMapping>) getResponse.getResponseList();
			List<String> completedStageIds = new ArrayList<String>();
			
			List<SessionStagesFileCopyingDTO> sessionStagesDTOList = new ArrayList<SessionStagesFileCopyingDTO>();
			for (SessionStagesMapping sessionStagesMapping : sessionMappingList) {

				if (sessionStagesMapping.getRunCount() > 0 ) {
					SessionStagesFileCopyingDTO sessionStagesFileCopyingDTO = new SessionStagesFileCopyingDTO();
					String stageId = "";

					// Level Stage Five Id
					if (sessionStagesMapping.getLevelTwoStageId() != null
							&& sessionStagesMapping.getLevelThreeStageId() != null
							&& sessionStagesMapping.getLevelFourStageId() != null
							&& sessionStagesMapping.getLevelFiveStageId() != null) {
						stageId = sessionStagesMapping.getLevelFiveStageId();

					}

					// Level Stage Four Id
					if (sessionStagesMapping.getLevelTwoStageId() != null
							&& sessionStagesMapping.getLevelThreeStageId() != null
							&& sessionStagesMapping.getLevelFourStageId() != null
							&& sessionStagesMapping.getLevelFiveStageId() == null) {
						stageId = sessionStagesMapping.getLevelFourStageId();

					}

					// Level Stage Three Id
					if (sessionStagesMapping.getLevelTwoStageId() != null
							&& sessionStagesMapping.getLevelThreeStageId() != null
							&& sessionStagesMapping.getLevelFourStageId() == null
							&& sessionStagesMapping.getLevelFiveStageId() == null) {
						stageId = sessionStagesMapping.getLevelThreeStageId();

					}

					// Level Stage Two Id
					if (sessionStagesMapping.getLevelTwoStageId() != null
							&& sessionStagesMapping.getLevelThreeStageId() == null
							&& sessionStagesMapping.getLevelFourStageId() == null
							&& sessionStagesMapping.getLevelFiveStageId() == null) {
						stageId = sessionStagesMapping.getLevelTwoStageId();

					}
					// Level Stage Two Id
					if (sessionStagesMapping.getLevelTwoStageId() == null
							&& sessionStagesMapping.getLevelThreeStageId() == null
							&& sessionStagesMapping.getLevelFourStageId() == null
							&& sessionStagesMapping.getLevelFiveStageId() == null) {
						stageId = sessionStagesMapping.getLevelOneStageId();

					}
					
					sessionStagesFileCopyingDTO.setStageId(stageId);
					sessionStagesFileCopyingDTO.setSessionMapId(sessionStagesMapping.getSessionStagesMappingId());
					sessionStagesFileCopyingDTO.setLevel1StageId(sessionStagesMapping.getLevelOneStageId());
					sessionStagesFileCopyingDTO.setLevel2StageId(sessionStagesMapping.getLevelTwoStageId());
					sessionStagesFileCopyingDTO.setLevel3StageId(sessionStagesMapping.getLevelThreeStageId());
					sessionStagesFileCopyingDTO.setLevel4StageId(sessionStagesMapping.getLevelFourStageId());
					sessionStagesFileCopyingDTO.setLevel5StageId(sessionStagesMapping.getLevelFiveStageId());
					sessionStagesFileCopyingDTO.setRunCount(sessionStagesMapping.getRunCount());
					sessionStagesFileCopyingDTO.setStatus(sessionStagesMapping.getStatus());
					if(sessionStagesMapping.getStatus().equalsIgnoreCase("Completed with Success") || sessionStagesMapping.getStatus().equalsIgnoreCase("Completed with Failure"))
					{
						completedStageIds.add(stageId);
						Debug.printDebug("Completed Stage Id"+stageId);
					}
							
					Debug.printDebug("Stage Id"+  stageId);
					sessionMapIdStageId.put(sessionStagesMapping.getSessionStagesMappingId(),stageId);
					sessionMapIdStagePath.put(sessionStagesMapping.getSessionStagesMappingId(), sessionStagesMapping.getPath());
					sessionStagesDTOList.add(sessionStagesFileCopyingDTO);
				}

			}	
			
			
					
			//For Filter the Completed Stages..
			List<SessionStagesFileCopyingDTO> filteredSessionStagesFileCopyingDTOList = sessionStagesDTOList.stream()
		            .filter(dto -> !completedStageIds.contains(dto.getStageId()))
		            .collect(Collectors.toList());
			
			
			Debug.printDebug("List Size"+sessionStagesDTOList.size());
		
			Debug.printDebug("Fileter List Size"+filteredSessionStagesFileCopyingDTOList.size());
			List<String> sessionMappingIds = new ArrayList<String>();

			for (SessionStagesFileCopyingDTO filteredObj : filteredSessionStagesFileCopyingDTOList) {
				sessionMappingIds.add(filteredObj.getSessionMapId());
				Debug.printDebug(filteredObj.getSessionMapId());
			}

			if (filteredSessionStagesFileCopyingDTOList == null) {
				res.setCode(1);
				res.setMsg("Already All Are Copied");
				return res;
			}
//
			SessionStagesSelectedTestFilesService  sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
			
			GetResponse  getResponsetestFiles =new GetResponse();
			getResponsetestFiles = sessionStagesSelectedTestFilesService.getAllSelectedTestFileListBySessionStageMapIds(sessionMappingIds);
			List<SessionStagesSelectedTestFiles> sessionStagesSelectedTestFilesList= new ArrayList<SessionStagesSelectedTestFiles>();
			sessionStagesSelectedTestFilesList = (List<SessionStagesSelectedTestFiles>) getResponsetestFiles.getResponseList();
			Debug.printDebug("Selected Test Files List"+sessionStagesSelectedTestFilesList.size());
			
			Map<String,String> sessionSelectedTestFileIdSessionMappingId = new HashMap<String,String>();
			List<String> selectedTestFilesIds = new ArrayList<String>();
			if(sessionStagesSelectedTestFilesList!=null)
			{
				for(SessionStagesSelectedTestFiles sessionTF:sessionStagesSelectedTestFilesList)
				{
					sessionSelectedTestFileIdSessionMappingId.put(sessionTF.getSessionStagesSelectedTestFilesId(), sessionTF.getSessionstageMapsId());
					selectedTestFilesIds.add(sessionTF.getSessionStagesSelectedTestFilesId());
					selectedTFIdSessionMapId.put(sessionTF.getSessionStagesSelectedTestFilesId(), sessionTF.getSessionstageMapsId());
					Debug.printDebug("Selected Test File Ids"+sessionTF.getSessionStagesSelectedTestFilesId()   +"MappingId :"+sessionTF.getSessionstageMapsId());
				}
				
			}
			
			//
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse testFileResultsGetResponse =  new GetResponse();
			List<String>stageIds = new ArrayList<String>();
		    Debug.printDebug();
			testFileResultsGetResponse =sessionStagesTestFilesResultService.getTestFileResultListByselectTestFileIds(selectedTestFilesIds);
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultList = new ArrayList< SessionStagesTestFilesResult>();
			sessionStagesTestFilesResultList = (List<SessionStagesTestFilesResult>) testFileResultsGetResponse.getResponseList();
			Debug.printDebug("Test FileResults Size"+sessionStagesTestFilesResultList.size());
			//Checking The Condition Wheather All TPF File Passed Or Failed..
			String stageId = "";
			if(sessionStagesTestFilesResultList.size()>0)
			{
				Debug.printDebug("List Test Result Size "+sessionStagesTestFilesResultList);
				List<SessionStagesTestFilesResult> filteredFailedSessionStagesTestFilesResult = sessionStagesTestFilesResultList
						.stream().filter(dto -> dto.getTestStatus().equalsIgnoreCase("FAILURE"))
						.collect(Collectors.toList());
				
				Map<String, String> stageIdName = new HashMap<String, String>();
				SessionManagement sessionManagament = new SessionManagement();
				stageIdName = sessionManagament.getAllStageIdName();
			
			

				if (filteredFailedSessionStagesTestFilesResult.size()>0) {
					Debug.printDebug("Filtered Failed Session Stages Test Files Result"+filteredFailedSessionStagesTestFilesResult.size());
					
					// Get Flag Enabling
					Debug.printDebug("User Action Need");
					
				/*	List<CopyFileDTO> copyFileDTOList = new ArrayList<CopyFileDTO>();
					for (SessionStagesTestFilesResult ses : sessionStagesTestFilesResultList) {
						CopyFileDTO copyFileDTO = new CopyFileDTO();
						copyFileDTO.setRdfFiledName(ses.getRdfFileName());
						copyFileDTO.setRdfFileNamewithPath(ses.getRdfPath() + ses.getRdfFileName());
						copyFileDTO.setRdfFilePath(ses.getRdfPath());
						copyFileDTO.setStageName(stageIdName.get(ses.getStageId()));
						String sessionMapId = selectedTFIdSessionMapId.get(ses.getSelectedtestFileId());
						copyFileDTO.setStagePath(sessionMapIdStagePath.get(sessionMapId));
						copyFileDTOList.add(copyFileDTO);
						Debug.printDebug("StagePath"+sessionMapIdStagePath.get(sessionMapId));

					}*/
					stageId = sessionStagesTestFilesResultList.get(0).getStageId();
					
					res.setCode(100);
					SessionTestStateObject.setPopupStageId(stageId);
					res.setMsg("User Action Needs");
					res.setFlag(true);
				//	res.setCopyFileDTOList(copyFileDTOList);
					

				} else {
					Debug.printDebug("Inside Direct Copying ");
					
					List<CopyFileDTO> copyFileDTOList = new ArrayList<CopyFileDTO>();

					for (SessionStagesTestFilesResult ses : sessionStagesTestFilesResultList) {
						/*
						 * CopyFileDTO copyFileDTO = new CopyFileDTO();
						 * copyFileDTO.setRdfFiledName(ses.getRdfFileName());
						 * copyFileDTO.setRdfFileNamewithPath(ses.getRdfPath() + ses.getRdfFileName());
						 * copyFileDTO.setRdfFilePath(ses.getRdfPath());
						 * copyFileDTO.setStageName(stageIdName.get(ses.getStageId())); String
						 * sessionMapId = selectedTFIdSessionMapId.get(ses.getSelectedtestFileId());
						 * copyFileDTO.setStagePath(sessionMapIdStagePath.get(sessionMapId));
						 * copyFileDTOList.add(copyFileDTO);
						 */
						Path sourceFile = Paths.get(ses.getRdfPath() + ses.getRdfFileName());
						String sessionMapId = selectedTFIdSessionMapId.get(ses.getSelectedtestFileId());
						Path destination = Paths.get(sessionMapIdStagePath.get(sessionMapId));
						copyFilesToOutputFolder(sourceFile, destination);
						
					}
					res.setCode(1);
					res.seteMsg("Copied Internally All Files Are Passed");
					
				}

			}			
			
		} catch (Exception ex) {
			res.setCode(0);
			res.seteMsg("Error"+ex.getMessage());
			res.setMsg("Some Issues");
		}
		return res;
	}
	
	


	public void copyFilesToOutputFolder(Path sourceFile, Path outputFolder) {
		try {
			Debug.printDebug("Enter To the Method copyFilesToOutputFolder");

			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String dateFolder = currentDateTime.format(formatter);

			Path newFolderPath = outputFolder.resolve(dateFolder);

			if (!Files.exists(newFolderPath)) {
				Files.createDirectories(newFolderPath);
				Debug.printDebug("Folder created at: " + newFolderPath.toString());

			} else {
				Debug.printDebug("Folder Already Exits On : " + newFolderPath.toString());
			}

			if (newFolderPath != null && Files.exists(newFolderPath)) {

				Path destinationFile = newFolderPath.resolve(sourceFile.getFileName());
				Files.copy(sourceFile, destinationFile);
				Debug.printDebug("Copied file " + sourceFile.getFileName() + " to " + destinationFile);

			} else {
				Debug.printDebug("Output folder does not exist for the current session.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void copyFilesToOutputFolder(List<CopyFileDTO>selectedFileCoyingList) {
		
		
		
	}
	

	public void deleteAllFilesInDirectory(String directoryPath) throws IOException {
		Path dir = Paths.get(directoryPath);

		if (!Files.exists(dir)) {
			System.out.println("Directory does not exist: " + directoryPath);
			return;
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				if (Files.isRegularFile(path)) {
					Files.delete(path);
					System.out.println("Deleted: " + path.getFileName());
	
				}
			}
		} catch (IOException e) {
			System.out.println("Error deleting files: " + e.getMessage());
			throw e;
		}

	}
	
}



