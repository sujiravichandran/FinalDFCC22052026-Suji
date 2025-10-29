package com.teclever.dfcc.datastore.testmanagement;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.service.CardDetailsService;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.DriverCard;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.processcontrolmanagement.LoadDriverProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.currentTestDetails;
import com.teclever.dfcc.utils.Debug;


public class TestManagerManagement {
	
	public DriverCardDetailsResponse preLoadDriver() throws Exception {

		String uutId = StateMachine.currentSessionDetails.getUutId();

		RunConfigurationManagement mng = new RunConfigurationManagement();

		String testTypeId = mng.getTestTypeIdForSelfTestByUUT(uutId);
		currentTestDetails.setTestType(testTypeId);
		Debug.printDebug(
				"At time of Load Driver TEST TYPE ID :: " + currentTestDetails.getTestType() + "--" + testTypeId);

		RunConfigurationService runConfigurationService = new RunConfigurationService();

		String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		currentSessionDetails.setRunConfigId(runConfigId);
		Debug.printDebug(
				"At time of Load Driver RUN CONFIG :: " + currentSessionDetails.getRunConfigId() + "--" + runConfigId);

		AitessConfigurationDetails aitess = runConfigurationService.getAitessDetailsByRunConfigId(runConfigId);
		LoadDriverProcessControlManagement pc = LoadDriverProcessControlManagement.getInstance();

		DriverCardDetailsResponse responseList = pc.loadDriver("ps -aux --cols=200" + "\n", null, 0,
				LoadDriverProcessControlManagement.LoadMode.AUX);
		//Anuj Added Points 
		if (!responseList.getProcessIds().isEmpty()) {
			for (String s : responseList.getProcessIds()) {
				Debug.printDebug("KILL COMMAND GOING TO EXECUTED:--------->>>>>" + "sudo kill -9 " + s);
				pc.loadDriver("sudo kill -9 " + s + "\n", aitess.getUnloadDriverCommand(), 0,
						LoadDriverProcessControlManagement.LoadMode.KILL);
			}
			Thread.sleep(3000);

		}

		DriverCardDetailsResponse response = pc.loadDriver("sudo " + aitess.getLoadDriverCommand() + "\n", null,
				aitess.getAitessId(), LoadDriverProcessControlManagement.LoadMode.STARTUP);

		// 09-07-2025
		RunConfigurationService r = new RunConfigurationService();
		String driver = r.getDriverByUutIdAndTestTypeId(uutId, testTypeId); // HWATP
//		System.out.println("-------    " + driver);

		int xx = r.getAitessIdByDriver(driver); // aitessId
//		System.out.println("--" + xx);

		CardDetailsService c = new CardDetailsService();
		String aimMilDbFound = c.getCardNameByIdentificationText1("1553B MODULE", xx);
		if (aimMilDbFound != null) {
//			// call down -- response1
//			//aim card response
			DriverCardDetailsResponse response1 = pc.loadDriver("lsmod" + "\n", aimMilDbFound, aitess.getAitessId(),
					LoadDriverProcessControlManagement.LoadMode.CARD);
		

			if (response1.getDriverCardDetails().size() > 0) {

				response.getDriverCardDetails().add(response1.getDriverCardDetails().get(0));
			} else {

				DriverCard driverCard = new DriverCard();
				driverCard.setCardName("1553B MODULE");
				driverCard.setMsg("NOT OK");
				response.getDriverCardDetails().add(driverCard);
			}

		}
		// aim card response
		return response;

	}
	
}
