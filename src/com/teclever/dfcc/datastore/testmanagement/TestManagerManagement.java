package com.teclever.dfcc.datastore.testmanagement;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.service.RunConfigurationService;

public class TestManagerManagement {
		
	public void startTest(String sessionId, String stageId, String testTypeId,String listOfTestFiles,int repeatCount, boolean stopOnError)
	{
		//get uutId from STATE MACHINE
		String uutId = "UUT1";
		
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		//getCurrentRunConfigId Details FROM STATE MACHINE
		String smRunConfigId = "RUN001";
		//get currentRunConfigId Details from DB in Object
		AitessConfigurationDetails currentAitess = runConfigurationService.getAitessDetailsByRunConfigId(smRunConfigId);
		
				
		//get RunConfigId based on uutId and testTypeId
		String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);
		//get runConfigId Details from DB in object
		AitessConfigurationDetails aitess = runConfigurationService.getAitessDetailsByRunConfigId(runConfigId);
		
		System.out.println();
		
			
		if(currentAitess.getDriverName().equals(aitess.getDriverName()))
		{
			System.out.println("<< Drivers Matched >> "+ "currentAitess: "+currentAitess.getDriverName()+ " ::: " +"aitess: " + aitess.getDriverName());
		}else 
		{
			System.out.println("<< Drivers NOT Matched >> "+ "currentAitess: "+currentAitess.getDriverName()+ " ::: " +"aitess: " + aitess.getDriverName());
		}
	
		
		if(currentAitess.getAitessName().equals(aitess.getAitessName()))
		{
			System.out.println("<< Aitess Matched >> "+ "currentAitess: "+currentAitess.getAitessName()+ " ::: " +"aitess: " + aitess.getAitessName());
			
		}else
		{
			System.out.println("<< Aitess NOT Matched >> "+ "currentAitess: "+currentAitess.getAitessName()+ " ::: " +"aitess: " + aitess.getAitessName());

		}
		
		
		if(currentAitess.getConfigFile().equals(aitess.getConfigFile()))
		{
			System.out.println("<< ConfigFile Matched >> "+ "currentAitess: "+currentAitess.getConfigFile()+ " ::: " +"aitess: " + aitess.getConfigFile());

		}else 
		{
			System.out.println("<< ConfigFile NOT Matched >> "+ "currentAitess: "+currentAitess.getConfigFile()+ " ::: " +"aitess: " + aitess.getConfigFile());

		}
		
		
		
	}

}
