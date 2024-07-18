//package com.teclever.dfcc.datastore.customtestmanagement;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.sql.Blob;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.sql.rowset.serial.SerialBlob;
//
//import com.teclever.datastore.dto.GetObjResponse;
//import com.teclever.datastore.dto.Response;
//import com.teclever.datastore.entities.CustomTest;
//import com.teclever.datastore.service.CustomTestService;
//import com.teclever.datastore.utils.GetResponse;
//import com.teclever.dfcc.datastore.dto.CustomTestFileResponse;
//import com.teclever.dfcc.stateMachine.StateMachine;
//
//public class CustomTestManagement {
//
//	static String currentDirectory = new File(
//			CustomTestManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
//
//	static String customFileDir = currentDirectory + File.separator + "tmp" + File.separator;
//
//	public String customTestSymbolADD(String symbolName, String ipData) {
//
//		String symbolCommand;
//		try {
//			symbolCommand = symbolName + " = " + ipData;
//		} catch (Exception e) {
//			return null;
//		}
//		return symbolCommand;
//
//	}
//
//	public Response customTestSymbolRUN(String fileName, List<String> symbolTestFormate) {
//
//		Response res = new Response();
//		if (directoryExist(customFileDir)) {
//
//			File file = new File(customFileDir + fileName);
//			try {
//				if (file.exists()) {
//					System.out.println("File exist");
//				} else {
//					if (file.createNewFile()) {
//						System.out.println("New File is Created ");
//					} else {
//						System.out.println("File is not created");
//					}
//				}
//
//				try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
//					for (String symbolCmd : symbolTestFormate) {
//						bw.write(symbolCmd);
//						bw.newLine();
//					}
//
//					bw.close();
//				}
//				CustomTestService customTestService = new CustomTestService();
//				customTestService.addCustomTest(fileName, StateMachine.currentSessionDetails.getSessionId(),
//						convertFileToBlob(file));
//
////				TestProcessManagement testProcessManangement = new TestProcessManagement();
////				testProcessManangement.testProcesControl(StateMachine.currentSessionDetails.getSessionId(), "stageId",
////						0, null /* listOfFileId */, true /* continueWithError */, null/* stageName */,
////						null/* testTypeId */);
//
//			} catch (IOException e) {
//				e.printStackTrace();
//
//			} catch (SQLException e) {
//				e.printStackTrace();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw e;
//			}
//			res.setResponseCode(1);
//			res.setResponseMessage("Test Started");
//		} else {
//			System.out.println("tmp Directory Not Present ");
//			res.setResponseCode(0);
//			res.setResponseMessage("Test Not Started : tmp Directory Not Present ");
//		}
//		return res;
//	}
//
//	private Blob convertFileToBlob(File file) throws IOException, SQLException {
//		byte[] fileContent = Files.readAllBytes(file.toPath());
//		return new SerialBlob(fileContent);
//	}
//
//	private boolean directoryExist(String fileDir) {
//		File directory = new File(fileDir);
//		boolean returnFlag;
//		try {
//			// Check if the directory exists
//			if (directory.exists() && directory.isDirectory()) {
//				// Get all files in the directory
//				File[] files = directory.listFiles();
//				if (files != null) {
//					for (File f : files) {
////	                if (f.isFile() && !f.getName().equals(fileName)) {
////	                    // Delete each file except the .tst file
//						if (f.delete()) {
//							System.out.println(f.getName() + " deleted.");
//						} else {
//							System.out.println("Failed to delete " + f.getName());
//						}
////	                }
//					}
//				}
//				returnFlag = true;
//			} else {
//				System.out.println("Directory does not exist.");
//				returnFlag = false;
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//		return returnFlag;
//	}
//
//	public List<String> readFromDotTstFile(String customId) {
//		List<String> listOfFileData = new ArrayList<>();
//
//		CustomTestService customTestService = new CustomTestService();
//		GetObjResponse objResponse = customTestService.getFileByCustomId(customId);
//		CustomTest customTest = new CustomTest();
//		customTest = (CustomTest) objResponse.getObject();
//		try {
//			// Assume you have a method to retrieve the blob from the database
//			Blob blob = customTest.getContent();
//
//			// Convert Blob to byte array
//			byte[] bytes = blob.getBytes(1, (int) blob.length());
//
//			// Convert byte array to String (assuming UTF-8 encoding)
//			String fileContent = new String(bytes, StandardCharsets.UTF_8);
//
//			// Split the file content into lines (assuming each line is a symbol command)
//			String[] lines = fileContent.split("\\r?\\n");
//
//			// Add each line to the list
//			listOfFileData.addAll(Arrays.asList(lines));
//
//			for (String l : listOfFileData) {
//				System.out.println("Line data --  " + l);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			// Handle exceptions as needed
//		}
//		return listOfFileData;
//
//	}
//
//	public CustomTestFileResponse getListOfFileNamesWithId() {
//		CustomTestFileResponse customTestFileResponse = new CustomTestFileResponse();
//		Response res = new Response();
//		try {
//
//			CustomTestService customTestService = new CustomTestService();
//			GetResponse objResponse = customTestService.getAllFileNames();
//			CustomTest customTest;
//			if (objResponse.getCode() == 1) {
//
//				Map<String, String> fileIdAndName = new HashMap<>();
//				for (Object obj : objResponse.getResponseList()) {
//					customTest = (CustomTest) obj;
//					fileIdAndName.put(customTest.getCustomId(), customTest.getFilename());
//				}
//				customTestFileResponse.setFileIdAndName(fileIdAndName);
//			}
//			res.setResponseCode(objResponse.getCode());
//			res.setResponseMessage(objResponse.getMsg());
//			customTestFileResponse.setResponse(res);
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			res.setResponseCode(0);
//			res.setResponseMessage("Exception " + e.getLocalizedMessage());
//			customTestFileResponse.setResponse(res);
//		}
//		return customTestFileResponse;
//	}
//
//}
