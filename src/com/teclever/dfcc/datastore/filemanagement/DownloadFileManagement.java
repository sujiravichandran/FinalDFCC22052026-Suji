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
import com.teclever.datastore.entities.DownloadFile;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.dfcc.datastore.dto.DownloadFileDto;

public class DownloadFileManagement {

	// GET DOWNLOAD FILES LIST
	public static List<DownloadFileDto> getAllDownloadFiles(String runConfigId) {
		List<DownloadFileDto> downloadFileDtos = new ArrayList<>();
		try {
			DownloadFileService downloadFileService = new DownloadFileService();
			List<String> runPathMasterIds = downloadFileService.getRunPathMasterIdsByRunConfigId(runConfigId);

			// Fetch download files based on runPathMasterIds and deleteStatus
			for (String runPathMasterId : runPathMasterIds) {
				List<DownloadFile> downloadFiles = downloadFileService
						.getAllDownloadFilesByRunPathMasterId(runPathMasterId);
				for (DownloadFile downloadFile : downloadFiles) {
					DownloadFileDto downloadFileDto = new DownloadFileDto();
					downloadFileDto.setDownloadFileId(downloadFile.getDownloadFileId());
					downloadFileDto.setDownloadFileName(downloadFile.getDownloadFileName());
					downloadFileDto.setRunPathMasterId(downloadFile.getRunPathMasterId());
					downloadFileDtos.add(downloadFileDto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return downloadFileDtos;
	}

	public static List<String> saveDownloadFilesToDatabase(List<String> downloadFilePaths, String runPathMasterId) {
		DownloadFileService downloadFileService = new DownloadFileService();
		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			markPreviousDownloadFileRowsAsDeleted(runPathMasterId);

			for (String filePath : downloadFilePaths) {
				Path dir = Paths.get(filePath);
				Files.walk(dir).filter(Files::isRegularFile).forEach(path -> {
					String fileName = path.toString();
					DownloadFile downloadFile = new DownloadFile();
					downloadFile.setDownloadFileName(fileName);
					downloadFile.setRunPathMasterId(runPathMasterId);
					downloadFileService.saveDownloadFileToDatabase(downloadFile);
				});
			}

			session.flush();
			session.clear();
			transaction.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return downloadFilePaths;
	}

	// Method to mark previous download file rows as deleted
	private static void markPreviousDownloadFileRowsAsDeleted(String runPathMasterId) {
		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			List<DownloadFile> downloadFiles = session
					.createQuery("FROM DownloadFile WHERE runPathMasterId = :runPathMasterId", DownloadFile.class)
					.setParameter("runPathMasterId", runPathMasterId).getResultList();

			for (DownloadFile downloadFile : downloadFiles) {
				downloadFile.setDeleteStatus(true);
				session.merge(downloadFile);
			}

			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<DownloadFileDto> getAllDownloadFilesByRunPathId(String runPathMasterId) {
		List<DownloadFileDto> downloadFileDtos = new ArrayList<>();
		try {
			DownloadFileService downloadFileService = new DownloadFileService();
			List<DownloadFile> downloadFiles = downloadFileService
					.getAllDownloadFilesByRunPathMasterId(runPathMasterId);
			for (DownloadFile downloadFile : downloadFiles) {
				DownloadFileDto downloadFileDto = new DownloadFileDto();
				downloadFileDto.setDownloadFileId(downloadFile.getDownloadFileId());
				downloadFileDto.setDownloadFileName(downloadFile.getDownloadFileName());
				downloadFileDto.setRunPathMasterId(downloadFile.getRunPathMasterId());
				downloadFileDtos.add(downloadFileDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return downloadFileDtos;
	}
}