package com.teclever.dfcc.datastore.testmanagement;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.processcontrolmanagement.LoadDriverProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine;


public class TestManagerManagement {
	
	public DriverCardDetailsResponse preLoadDriver() {

		String uutId =  StateMachine.currentSessionDetails.getUutId();
		
		String testTypeId = "TT1";

		RunConfigurationService runConfigurationService = new RunConfigurationService();

		String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		AitessConfigurationDetails aitess = runConfigurationService.getAitessDetailsByRunConfigId(runConfigId);

		LoadDriverProcessControlManagement pc = LoadDriverProcessControlManagement.getInstance();
		DriverCardDetailsResponse response = pc.loadDriver("sudo " + aitess.getLoadDriverCommand() + "\n", null,
				aitess.getAitessId(), LoadDriverProcessControlManagement.LoadMode.STARTUP);

		return response;

	}
	
}
