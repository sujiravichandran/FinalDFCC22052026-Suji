package com.teclever.dfcc.datastore.testmanagement;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.processcontrolmanagement.LoadDriverProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.currentTestDetails;
import com.teclever.dfcc.utils.Debug;


public class TestManagerManagement {
	
	public DriverCardDetailsResponse preLoadDriver() {

		String uutId =  StateMachine.currentSessionDetails.getUutId();
		
		RunConfigurationManagement mng = new RunConfigurationManagement();
		
		String testTypeId = mng.getTestTypeIdForSelfTestByUUT(uutId);
		currentTestDetails.setTestType(testTypeId);
		Debug.printDebug("At time of Load Driver TEST TYPE ID :: "+ currentTestDetails.getTestType() + "--" + testTypeId);

				
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);
		
		currentSessionDetails.setRunConfigId(runConfigId);
		Debug.printDebug("At time of Load Driver RUN CONFIG :: "+ currentSessionDetails.getRunConfigId() + "--" + runConfigId);


		AitessConfigurationDetails aitess = runConfigurationService.getAitessDetailsByRunConfigId(runConfigId);

		LoadDriverProcessControlManagement pc = LoadDriverProcessControlManagement.getInstance();
		DriverCardDetailsResponse response = pc.loadDriver("sudo " + aitess.getLoadDriverCommand() + "\n", null,
				aitess.getAitessId(), LoadDriverProcessControlManagement.LoadMode.STARTUP);
		
		//aim card response
		DriverCardDetailsResponse response1 = pc.loadDriver("lsmod" +"\n", null,
				0, LoadDriverProcessControlManagement.LoadMode.CARD);

		response.getDriverCardDetails().add(response1.getDriverCardDetails().get(0));
		return response;

	}
	
}
