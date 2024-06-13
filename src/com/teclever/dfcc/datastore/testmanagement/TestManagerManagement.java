package com.teclever.dfcc.datastore.testmanagement;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.processcontrolmanagement.LoadDriverProcessControlManagement;


public class TestManagerManagement {
	
	private static String homeLocation = "home/bel/desktop/";          //user.home
	private static String configHomeLocation = "home/bel/desktop/config.dat";  //user.home+/config.dat
	private static String startupUserFileLocation = "home/bel/downloads/startup.user";
	private static String cacheFilePath = "home/bel/desktop/.cache";   //home location + .cache
    AitessAction action;

	public enum AitessAction {
	    TERMINATE,
	    LOAD_AITESS,
	    EXECUTE_TPF_FILES
	}


	
	public DriverCardDetailsResponse preLoadDriver() {

		// Get uutId from STATE MACHINE
		String uutId = "UUT1";

		// testTypeId for SELF TEST
		String testTypeId = "TT1";

		RunConfigurationService runConfigurationService = new RunConfigurationService();

		// Get runConfigId based on uutId and testTypeId
		String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		// Get runConfigId Details from DB in object
		AitessConfigurationDetails aitess = runConfigurationService.getAitessDetailsByRunConfigId(runConfigId);

		// Load driver in STARTUP mode and returning Driver Card Details
		LoadDriverProcessControlManagement pc = new LoadDriverProcessControlManagement();
		DriverCardDetailsResponse response = pc.loadDriver("sudo " + aitess.getLoadDriverCommand() + "\n", null,
				aitess.getAitessId(), LoadDriverProcessControlManagement.LoadMode.STARTUP);

		return response;

	}
	
	
	
//	public void startTest(String sessionId, String stageId, String testTypeId,String testfile,int repeatCount, boolean stopOnError)
//	{
//		//get uutId from STATE MACHINE
//		String uutId = "UUT1";
//		
//		RunConfigurationService runConfigurationService = new RunConfigurationService();
//
//		//getCurrentRunConfigId Details FROM STATE MACHINE
//		String smRunConfigId = "RUN001";
//		//get currentRunConfigId Details from DB in Object
//		AitessConfigurationDetails currentAitess = runConfigurationService.getAitessDetailsByRunConfigId(smRunConfigId);
//		
//				
//		//get RunConfigId based on uutId and testTypeId
//		String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);
//		//get runConfigId Details from DB in object
//		AitessConfigurationDetails aitess = runConfigurationService.getAitessDetailsByRunConfigId(runConfigId);
//		
//		System.out.println();
//    	Aitess1ProcessControl aitess1ProcessControl = Aitess1ProcessControl.getInstance();
//
//			
//		if (!currentAitess.getDriverName().equals(aitess.getDriverName())) {
//
//           // ProcessControlManagement.loadDriver(aitess.getLoadDriverCommand(), currentAitess.getUnloadDriverCommand(),0, ProcessControlManagement.LoadMode.SWITCH);
//            System.out.println("<< Drivers NOT Matched >> " + "currentAitess: " + currentAitess.getDriverName() + " ::: " + "aitess: " + aitess.getDriverName());
//        } else {
//            System.out.println("<< Drivers Matched >> " + "currentAitess: " + currentAitess.getDriverName() + " ::: " + "aitess: " + aitess.getDriverName());
//        
//        }
//	
//		if(!currentAitess.getConfigFile().equals(aitess.getConfigFile()))
//		{
//	        copyConfigFile(aitess.getConfigFile(),configHomeLocation); //copy config file to home location
//	        copyConfigFile(startupUserFileLocation,homeLocation );     //copy startup.user file to home location
//	        deleteCacheFile(cacheFilePath);								//delete cache file
//	        
//			System.out.println("<< ConfigFile NOT Matched >> "+ "currentAitess: "+currentAitess.getConfigFile()+ " ::: " +"aitess: " + aitess.getConfigFile());
//			
//		}else 
//		{
//			System.out.println("<< ConfigFile Matched >> "+ "currentAitess: "+currentAitess.getConfigFile()+ " ::: " +"aitess: " + aitess.getConfigFile());
//		}
//		
//
//		if (!currentAitess.getAitessName().equals(aitess.getAitessName())) {
//	        action = AitessAction.TERMINATE;
//	    } else if (!currentAitess.getDriverName().equals(aitess.getDriverName())) {
//	        action = AitessAction.LOAD_AITESS;      
//	    } else {
//	        action = AitessAction.EXECUTE_TPF_FILES;
//	    }
//	
//		
//	
//	 switch (action) {
//     case TERMINATE:
//	    
//         aitess1ProcessControl.terminateAitess("exit");
//         break;
//         
//     case LOAD_AITESS:
//
//         //ProcessControlManagement.loadDriver(aitess.getLoadDriverCommand(), currentAitess.getUnloadDriverCommand(), 0, ProcessControlManagement.LoadMode.SWITCH);
//         copyConfigFile(aitess.getConfigFile(), configHomeLocation);
//         copyConfigFile(startupUserFileLocation, homeLocation);
//         deleteCacheFile(cacheFilePath);
//         aitess1ProcessControl.write(aitess.getAitessCommand());
//         break;
//         
//     case EXECUTE_TPF_FILES:
//
//         aitess1ProcessControl.write("@" + testfile);
//         
//         break;
//	 }
//	 
//	}
//
//		private void copyConfigFile(String sourcePath, String destinationPath) {
//		    try {
//		        Files.copy(Paths.get(sourcePath), Paths.get(destinationPath));
//		        System.out.println("Config file copied from " + sourcePath + " to " + destinationPath);
//		    } catch (IOException e) {
//		        System.err.println("An error occurred while copying the config file: " + e.getMessage());
//		        e.printStackTrace();
//		    }
//		}
//
//		 private void deleteCacheFile(String cacheFilePath) {
//		        try {
//		            Path path = Paths.get(cacheFilePath);
//		            if (Files.exists(path)) {
//		                Files.delete(path);
//		                System.out.println("Cache file deleted from " + cacheFilePath);
//		            } else {
//		                System.out.println("Cache file does not exist at " + cacheFilePath);
//		            }
//		        } catch (IOException e) {
//		            System.err.println("An error occurred while deleting the cache file: " + e.getMessage());
//		            e.printStackTrace();
//		        }
//		 }
		
		
}
