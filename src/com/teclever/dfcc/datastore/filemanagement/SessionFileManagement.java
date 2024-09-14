package com.teclever.dfcc.datastore.filemanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import com.teclever.dfcc.datastore.dto.ReportConfigDto;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

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

			System.out.println(currentDirectory);
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
				System.out.println("dfcc serial number folder already exists: " + dfccSerialNoDirectory);
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
				System.out.println("Session folder already exists: " + sessionDirectory);
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
			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();
			GetResponse getResponseStageTestFileResult = sessionStagesTestFilesResultService
					.getTestResultFileBySessionIdAndStageId(sessionId, stageId);
			List<SessionStagesTestFilesResult> sessionStagesTestFilesResultServiceList = new ArrayList<SessionStagesTestFilesResult>();
			Map<String, SessionStagesTestFilesResult> fileIdObj = new HashMap<String, SessionStagesTestFilesResult>();
			List<String> fileIdsRunnedInStages = new ArrayList<String>();

			if (getResponseStageTestFileResult.getCode() != 0) {
				sessionStagesTestFilesResultServiceList = (List<SessionStagesTestFilesResult>) getResponseStageTestFileResult
						.getResponseList();
				for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionStagesTestFilesResultServiceList) {
					fileIdsRunnedInStages.add(sessionStagesTestFilesResult.getSelectedtestFileId());

				}

			}

			SessionSelectedStagesService sessionSelectedStagesService = new SessionSelectedStagesService();
			GetObjResponse getObject = sessionSelectedStagesService.getSessionStagesMapp(sessionId, stageId);
			SessionStagesMapping session = new SessionStagesMapping();
			session = (SessionStagesMapping) getObject.getObject();
			String sessionStagesMappingId = session.getSessionStagesMappingId();
			String stagePath = session.getPath();

			SessionStagesSelectedTestFilesService sessionStagesSelectedTestFilesService = new SessionStagesSelectedTestFilesService();
			GetResponse resStagesMap = sessionStagesSelectedTestFilesService
					.getSelectedTestFilesBySessionstageMapsId(sessionStagesMappingId);
			List<SessionStagesSelectedTestFiles> sessionStagesSelectedTestFilesList = new ArrayList<SessionStagesSelectedTestFiles>();
			sessionStagesSelectedTestFilesList = (List<SessionStagesSelectedTestFiles>) resStagesMap.getResponseList();
			Map<String, String> sessionStagesSelectedTestFilesIdAndTestFileId = new HashMap<String, String>();
			for (SessionStagesSelectedTestFiles sssTestFiles : sessionStagesSelectedTestFilesList) {
				sessionStagesSelectedTestFilesIdAndTestFileId.put(sssTestFiles.getSessionStagesSelectedTestFilesId(),
						sssTestFiles.getTestFilesId());
			}

			TestFilesStagesMappingService testFilesStagesMappingService = new TestFilesStagesMappingService();
			GetResponse getResponseFileMapping = testFilesStagesMappingService
					.getTestFilesStagesMappingByLastLevelReference(stageId);
			List<TestFilesStagesMapping> testFilesStagesMappingList = new ArrayList();
			testFilesStagesMappingList = (List<TestFilesStagesMapping>) getResponseFileMapping.getResponseList();
			List<String> filesMappingIds = new ArrayList<String>();
			if (getResponseFileMapping.getCode() != 0) {
				testFilesStagesMappingList = (List<TestFilesStagesMapping>) getResponseFileMapping.getResponseList();
				for (TestFilesStagesMapping testFilesStagesMapping : testFilesStagesMappingList) {
					filesMappingIds.add(testFilesStagesMapping.getTestFileId());
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

							for (SessionStagesTestFilesResult s : lstByKeys) {
								{
									/*
									 * String dStarCount = s.getdStarCount(); int dStarCountInt =
									 * Integer.parseInt(dStarCount); if (dStarCountInt > 0) { runnedAllSuccess =
									 * false; }
									 */
									String testStatus = s.getTestStatus();
									if (testStatus != null && !testStatus.equals("")
											&& testStatus.equalsIgnoreCase("failure")) {
										runnedAllSuccess = false;
									}
								}

							}
						} /*
							 * else { notRunnedAll = true; }
							 */

					} else {
						notRunnedAll = true;
					}
				}
			}

			if (!notRunnedAll && runnedAllSuccess) {
				// stagePath = "C:\\Users\\TECLEVER\\Downloads\\Copied\\";
				System.out.println("STAGE PATH..." + stagePath);
				Path outputPath = Path.of(stagePath);
				List<Path> listOfPath = new ArrayList<Path>();
				for (SessionStagesTestFilesResult service : lstByKeys) {
					String pathString = service.getRdfPath() + service.getRdfFileName();
					Path path = Path.of(pathString);
					listOfPath.add(path);
					System.out.println("PATH...." + pathString);
				}

				copyFilesToOutputFolder(listOfPath, outputPath);
			}

			if (!notRunnedAll && !runnedAllSuccess) {
				popupShowed = true;
			}

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return popupShowed;
	}

	// Method to copy a list of files to the output folder
	public void copyFilesToOutputFolder(List<Path> sourceFiles, Path outputFolder) {
		try {
			System.out.println("Enter To the Method copyFilesToOutputFolder");
			if (outputFolder != null && Files.exists(outputFolder)) {
				for (Path sourceFile : sourceFiles) {
					Path destinationFile = outputFolder.resolve(sourceFile.getFileName());
					Files.copy(sourceFile, destinationFile);
					System.out.println("Copied file " + sourceFile.getFileName() + " to " + destinationFile);
				}
			} else {
				System.out.println("Output folder does not exist for the current session.");
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
				System.out.println("Copied file " + sourceFile.getFileName() + " to " + destinationFile);
			} else {
				System.out.println("Output folder does not exist for current session.");
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

	// To Vignesh Implement
	public CopyingListDTO getShowPopupContent(String sessionId, String stageId) {
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
			response.setCode(1);
			response.setCodeMsg("Fetched Succesfully..");
			response.setFromPath(fromPath);
			response.setToPath(stagePath);
		} catch (Exception ex) {
			response.setCode(1);
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
				paths.add(Paths.get(copyFileDTO.getRdfFileNamewithPath()));
			}

			copyFilesToOutputFolder(paths, Paths.get(stagePath));

		} catch (Exception ex) {

		}
		return response;
	}

	// datapack
	public void copyToDataPack(List<ReportConfigDto> list) {

		String dataPackPath = StateMachine.getHomelocation() + File.separator + currentSessionDetails.getUutType()
				+ File.separator + currentSessionDetails.getDfccSerialNumber() + File.separator
				+ currentSessionDetails.getSessionName() + File.separator + "datapack";
		System.out.println("DATAPACK FOLDER CHECK ----- :: " + dataPackPath);
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
				System.out.println("File copied and renamed to: " + newFileLocation.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Failed to copy file: " + e.getMessage());

			}
		}
	}

}