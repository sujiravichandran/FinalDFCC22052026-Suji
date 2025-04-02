package com.teclever.dfcc.datastore.filemanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.TestFile;
import com.teclever.datastore.entities.TestFilesStagesMapping;
import com.teclever.datastore.service.TestFileService;
import com.teclever.datastore.service.TestFilesStagesMappingService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.StagesFilesDTO;
import com.teclever.dfcc.datastore.dto.StagesFilesResponseDTO;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.utils.Debug;

public class TestPlanFileManagement {

	// GET TPF FILES LIST
	public static List<TestFileDto> getAllTestFiles(String runConfigId) {
		List<TestFileDto> testFileDtos = new ArrayList<>();
		try {
			TestFileService testFileService = new TestFileService();
			List<String> runPathMasterIds = testFileService.getRunPathMasterIdsByRunConfigId(runConfigId);
			Debug.printDebug("List of Run PathMastet Id:"+runPathMasterIds.toString());

			// Fetch test files based on runPathMasterIds and deleteStatus
			for (String runPathMasterId : runPathMasterIds) {
				List<TestFile> testFiles = testFileService.getAllTestFilesByRunPathMasterId(runPathMasterId);
				for (TestFile testFile : testFiles) {
					TestFileDto testFileDto = new TestFileDto();
					testFileDto.setTestFileId(testFile.getTestFileId());
					testFileDto.setTestFileName(testFile.getTestFileName());
					testFileDto.setRunPathMasterId(testFile.getRunPathMasterId());
					testFileDtos.add(testFileDto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testFileDtos;
	}

	public static List<String> saveTestFilesToDatabase(List<String> testPlanFilePaths, String runPathMasterId) {
		TestFileService testFileService = new TestFileService();
		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			// markPreviousTestFileRowsAsDeleted(runPathMasterId);

			 for (String filePath : testPlanFilePaths) {
		            Path dir = Paths.get(filePath);
		            // Stream<Path> to walk through the directory and filter files by extension
		            try (Stream<Path> paths = Files.walk(dir)) {
		                paths.filter(Files::isRegularFile)
		                     .filter(path -> {
		                         String fileName = path.toString().toLowerCase();
		                         return fileName.endsWith(".tst") || fileName.endsWith(".tpf") || fileName.endsWith(".com");
		                     })
		                     .forEach(path -> {
		                         String fileName = path.toString();
		                         String testFileId = TestFileService.generateUniqueTestFileId();
		                         TestFile testFile = new TestFile();
		                         testFile.setTestFileId(testFileId);
		                         testFile.setTestFileName(fileName);
		                         testFile.setRunPathMasterId(runPathMasterId);
		                         testFileService.saveTestFileToDatabase(testFile);
		                     });
		            }
		        }

		        session.flush();
		        session.clear();
		        transaction.commit();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    return testPlanFilePaths;
		}

	public static List<String> saveTestFilesToDatabaseForCustomFiles(List<String> testPlanFilePaths,
			String runPathMasterId) {
		TestFileService testFileService = new TestFileService();
		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			// markPreviousTestFileRowsAsDeleted(runPathMasterId);

			for (String filePath : testPlanFilePaths) {
				Path dir = Paths.get(filePath);
				Files.walk(dir).filter(Files::isRegularFile).forEach(path -> {
					String fileName = path.toString();
					String testFileId = TestFileService.generateUniqueTestFileId();
					TestFile testFile = new TestFile();
					testFile.setTestFileId(testFileId);
					testFile.setTestFileName(fileName);
					testFile.setRunPathMasterId(runPathMasterId);
					testFileService.saveTestFileToDatabase(testFile);
				});
			}

			session.flush();
			session.clear();
			transaction.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testPlanFilePaths;
	}

	// Method to mark previous test file rows as deleted
	public static void markPreviousTestFileRowsAsDeleted(String runPathMasterId) {
		TestFileService testFileService = new TestFileService();
		List<TestFile> testFiles = testFileService.getTestFilesByRunPathMasterId(runPathMasterId);

		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			for (TestFile testFile : testFiles) {
				testFile.setDeleteStatus(true);
				session.merge(testFile);
			}

			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<TestFileDto> getAllTestFilesByRunPathMasterId(String runPathMasterId) {
		List<TestFileDto> testFileDtos = new ArrayList<>();
		try {
			TestFileService testFileService = new TestFileService();
			List<TestFile> testFiles = testFileService.getAllTestFilesByRunPathMasterId(runPathMasterId);
			for (TestFile testFile : testFiles) {
				TestFileDto testFileDto = new TestFileDto();
				testFileDto.setTestFileId(testFile.getTestFileId());
				testFileDto.setTestFileName(testFile.getTestFileName());
				testFileDto.setRunPathMasterId(testFile.getRunPathMasterId());
				testFileDtos.add(testFileDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testFileDtos;
	}

//	Before Chaning
//	public TestFileResponse getSelectedTestFilesFromStage(String stageId) {
//		TestFileResponse testFileResponse = new TestFileResponse();
//		Response res = new Response();
//		try {
//			TestFileService testFileService = new TestFileService();
//			List<TestFile> listOfTestFile = testFileService.getAllTestFiles();
//			Map<String, String> testFileIdAndName = new HashMap<>();
//			for (TestFile testFile : listOfTestFile) {
//				testFileIdAndName.put(testFile.getTestFileId(), testFile.getTestFileName());
//			}
//			TestFilesStagesMappingService testFilesStageMappingService = new TestFilesStagesMappingService();
//			GetResponse getResponse = testFilesStageMappingService
//					.getTestFilesStagesMappingByLastLevelReference(stageId);
//			if (getResponse.getCode() == 0) {
//				res.setResponseCode(0);
//				res.setResponseMessage(getResponse.geteMsg());
//				testFileResponse.setResponse(res);
//				return testFileResponse;
//			}
//			Map<String, String> testFileNames = new HashMap<>();
//			for (Object obj : getResponse.getResponseList()) {
//				TestFilesStagesMapping testFileStagMapping = (TestFilesStagesMapping) obj;
//				if (testFileIdAndName.get(testFileStagMapping.getTestFileId()) != null) {
//					testFileNames.put(testFileStagMapping.getTestFileId(),
//							testFileIdAndName.get(testFileStagMapping.getTestFileId()));
//				}
//			}
//						
//			testFileResponse.setTestFilesIdName(testFileNames);
//			res.setResponseCode(1);
//			res.setResponseMessage("Fetch Successful ");
//			testFileResponse.setResponse(res);
//		} catch (Exception e) {
//			res.setResponseCode(0);
//			res.setResponseMessage("Fetch Data Unsuccessful ");
//			testFileResponse.setResponse(res);
//			e.printStackTrace();
//		}
//		return testFileResponse;
//	}
	
//	After Changing
	public TestFileResponse getSelectedTestFilesFromStage(String stageId) {
		TestFileResponse testFileResponse = new TestFileResponse();
		Response res = new Response();
		try {
			TestFileService testFileService = new TestFileService();
			List<TestFile> listOfTestFile = testFileService.getAllTestFiles();
			Map<String, String> testFileIdAndName = new HashMap<>();
			for (TestFile testFile : listOfTestFile) {
				testFileIdAndName.put(testFile.getTestFileId(), testFile.getTestFileName());
			}
			TestFilesStagesMappingService testFilesStageMappingService = new TestFilesStagesMappingService();
			GetResponse getResponse = testFilesStageMappingService
					.getTestFilesStagesMappingByLastLevelReference(stageId);
			if (getResponse.getCode() == 0) {
				res.setResponseCode(0);
				res.setResponseMessage(getResponse.geteMsg());
				testFileResponse.setResponse(res);
				return testFileResponse;
			}
			Map<String, String> testFileNames = new LinkedHashMap<>();
			for (Object obj : getResponse.getResponseList()) {
				TestFilesStagesMapping testFileStagMapping = (TestFilesStagesMapping) obj;
				if (testFileIdAndName.get(testFileStagMapping.getTestFileId()) != null) {
					testFileNames.put(testFileStagMapping.getTestFileId(),
							testFileIdAndName.get(testFileStagMapping.getTestFileId()));
				}
			}
						
			testFileResponse.setTestFilesIdName(testFileNames);
			res.setResponseCode(1);
			res.setResponseMessage("Fetch Successful ");
			testFileResponse.setResponse(res);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetch Data Unsuccessful ");
			testFileResponse.setResponse(res);
			e.printStackTrace();
		}
		return testFileResponse;
	}
}
