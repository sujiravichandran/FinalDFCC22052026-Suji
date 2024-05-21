package com.teclever.dfcc.datastore.filemanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.entities.TestFile;
import com.teclever.datastore.service.TestFileService;
import com.teclever.dfcc.datastore.dto.TestFileDto;

public class TestPlanFileManagement {

	// GET TPF FILES LIST
	public static List<TestFileDto> getAllTestFiles(String runConfigId) {
		List<TestFileDto> testFileDtos = new ArrayList<>();
		try {
			TestFileService testFileService = new TestFileService();
			List<String> runPathMasterIds = testFileService.getRunPathMasterIdsByRunConfigId(runConfigId);

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

			markPreviousTestFileRowsAsDeleted(runPathMasterId);

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
}
