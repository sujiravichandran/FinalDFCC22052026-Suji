package com.teclever.dfcc.datastore.customtestmanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.service.CustomTestService;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.Debug;

public class AdvanceCustom2TestingManagement {

	public GetObjResponse customTesting2TestFileTaking(String filePath) {

		GetObjResponse res = new GetObjResponse();

		try {
			File file = new File(filePath);
			/*
			 * FileInputStream fis = new FileInputStream(file); byte[]fileBytes = new
			 * byte[(int) file.length()]; fis.read(fileBytes); fis.close();
			 */
			convertFileToBlob(file);
			
			String fileName = "";
			Path path = Paths.get(filePath);
			fileName = path.getFileName().toString();

			//StateMachine.currentSessionDetails.setSessionId("SASN00002");
			CustomTestService customTestService = new CustomTestService();
			res = customTestService.addCustomTest(fileName, StateMachine.currentSessionDetails.getSessionId(),
					convertFileToBlob(file), filePath, "custom2");

//			TestProcessManagement testProcessManangement = new TestProcessManagement();
//			testProcessManangement.testProcesControl(StateMachine.currentSessionDetails.getSessionId(), "stageId",
//					0, null /* listOfFileId */, true /* continueWithError */, null/* stageName */,
//					null/* testTypeId */);

		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return res;

	}

	public GetObjResponse customTesting2DownloadFileTaking(String filePath) {

		GetObjResponse res = new GetObjResponse();

		try {
			File file = new File(filePath);
			/*
			 * FileInputStream fis = new FileInputStream(file); byte[]fileBytes = new
			 * byte[(int) file.length()]; fis.read(fileBytes); fis.close();
			 */
			convertFileToBlob(file);
			String fileName = "";
			
			
			Path path = Paths.get(filePath);
	        fileName = path.getFileName().toString();
	       

			//StateMachine.currentSessionDetails.setSessionId("SASN00002");
			CustomTestService customTestService = new CustomTestService();
			res = customTestService.addCustomTest(fileName, StateMachine.currentSessionDetails.getSessionId(),
					convertFileToBlob(file), filePath, "download");

//			TestProcessManagement testProcessManangement = new TestProcessManagement();
//			testProcessManangement.testProcesControl(StateMachine.currentSessionDetails.getSessionId(), "stageId",
//					0, null /* listOfFileId */, true /* continueWithError */, null/* stageName */,
//					null/* testTypeId */);

		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return res;

	}

	
	private SerialBlob convertFileToBlob(File file) throws IOException, SQLException {
		byte[] fileContent = Files.readAllBytes(file.toPath());
		return new SerialBlob(fileContent);
	}
	
	

}