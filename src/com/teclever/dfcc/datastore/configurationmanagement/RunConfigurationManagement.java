package com.teclever.dfcc.datastore.configurationmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.RunConfiguration;
import com.teclever.datastore.entities.TestTypeMasterDetails;
import com.teclever.datastore.response.RunConfigurationResponse;
import com.teclever.datastore.response.TestTypeMasterDetailsResponse;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.datastore.service.MacroService;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.datastore.service.RunPathMasterService;
import com.teclever.datastore.service.SymbolService;
import com.teclever.datastore.service.TestFileService;
import com.teclever.datastore.service.TestTypeMasterDetailsService;
import com.teclever.datastore.utils.PathMasterDeleteResponse;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.DownloadFileManagement;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SymbolFileManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;

public class RunConfigurationManagement {

	//API :  GET TEST TYPE NAME BASED ON UUT
	public TestTypeMasterDetailsDto[] getTestTypeByUUTId(String uutId) {
		TestTypeMasterDetailsService service = new TestTypeMasterDetailsService();
		TestTypeMasterDetailsResponse serviceResponse = service.getTestNamesByUUTId(uutId);

		TestTypeMasterDetailsDto[] dtoArray = new TestTypeMasterDetailsDto[0];

		if (serviceResponse.getResponseCode() == 1) {
			List<TestTypeMasterDetailsDto> dtoList = new ArrayList<>();
			List<TestTypeMasterDetails> testTypeList = serviceResponse.getTestTypeMasterDetailsList();

			for (TestTypeMasterDetails testType : testTypeList) {
				TestTypeMasterDetailsDto dto = new TestTypeMasterDetailsDto();
				dto.setTestTypeId(testType.getTestTypeId());
				dto.setUutId(testType.getUutId());
				dto.setTestName(testType.getTestName());
				dtoList.add(dto);
			}

			dtoArray = dtoList.toArray(new TestTypeMasterDetailsDto[0]);
		}

		return dtoArray;
	}

	//API : ADD RUN CONFIG
	public RunConfigurationResponse addRunConfig(RunConfigurationDto runConfigurationDto, String uutId) {
		RunConfigurationService service = new RunConfigurationService();
		RunConfiguration runConfiguration = new RunConfiguration();
		runConfiguration.setUutId(uutId);
		runConfiguration.setTestTypeId(runConfigurationDto.getTestTypeId());
		runConfiguration.setConfigFile(runConfigurationDto.getConfigFile());
		runConfiguration.setAitess(runConfigurationDto.getAitess());
		runConfiguration.setDriver(runConfigurationDto.getDriver());
		runConfiguration.setAitees2ConfigFile(runConfigurationDto.getAitess2ConfigFile());
		System.out.println("Aites  2"+runConfiguration.getAitees2ConfigFile());
		RunConfigurationResponse serviceResponse = new RunConfigurationResponse();
		try {
			serviceResponse = service.addRunConfiguration(runConfiguration, uutId);
			if (serviceResponse.getResponseCode() == 1) {
				String runConfigId = runConfiguration.getRunConfigId();
				System.out.println(runConfigId);
				System.out.println("Run Config Id With TestType Id"+runConfigurationDto.getTestTypeId());

				// updating run path master table
				updatePathsInDatabase(runConfiguration);

				// test file
				String runPathMasterId2 = fetchRunPathMasterIdForTestFile(runConfigId);
				List<String> testFileLocation = fetchTestFilePathsFromRunPathMaster(runPathMasterId2);
				List<String> testFilesPaths = TestPlanFileManagement.saveTestFilesToDatabase(testFileLocation, runPathMasterId2);
				System.out.println(testFileLocation);
				
				// download file
				String runPathMasterId3 = fetchRunPathMasterIdForDownloadFile(runConfigId);
				List<String> downloadFileLocation = fetchDownloadFilePathsFromRunPathMaster(runPathMasterId3);
				List<String> downloadFilesPaths = DownloadFileManagement.saveDownloadFilesToDatabase(downloadFileLocation, runPathMasterId3);
				System.out.println(downloadFileLocation);
				
				// macro
				String runPathMasterId = fetchRunPathMasterIdForMacro(runConfigId);
				List<String> macroLocation = fetchMacroFilePathsFromRunPathMaster(runPathMasterId);
				List<String> macrofilePaths = MacroFileManagement.fetchMacroFilePathsDoubleSlash(macroLocation);
				List<MacroDto> macroDtos = MacroFileManagement.saveMacroNames(macrofilePaths, runPathMasterId);

				// symbol
				String runPathMasterId1 = fetchRunPathMasterIdForSymbol(runConfigId);
				System.out.println(runPathMasterId1);
				List<String> symbolLocation = fetchSymbolFilePathsFromRunPathMaster(runPathMasterId1);
				List<String> symbolfilePaths = SymbolFileManagement.fetchSymbolFilePathsDoubleSlash(symbolLocation);
				List<SymbolDto> symbolDtos = SymbolFileManagement.saveSymbols(symbolfilePaths, runPathMasterId1);


			} else {
				System.err.println("Failed to add Run Configuration: " + serviceResponse.getResponseMessage());
			}
		} catch (Exception e) {
			System.err.println("Failed to add Run Configuration: " + e.getMessage());
			serviceResponse.setResponseCode(0);
			serviceResponse.setResponseMessage("Failed to add Run Configuration: " + e.getMessage());
		}
		return serviceResponse;
	}

	// API : GET RUN CONFIG BASED ON UUT
	public List<RunConfigurationDto> getRunConfig(String uutId) {
		RunConfigurationService service = new RunConfigurationService();
		RunConfigurationResponse serviceResponse = service.getRunConfigurationByUutId(uutId);

		List<RunConfigurationDto> dtoList = new ArrayList<>();

		if (serviceResponse.getResponseCode() == 1) {
			List<RunConfiguration> runConfigurations = serviceResponse.getRunConfigurations();

			for (RunConfiguration runConfig : runConfigurations) {
				RunConfigurationDto dto = new RunConfigurationDto();
				dto.setRunConfigId(runConfig.getRunConfigId());
				dto.setUutId(runConfig.getUutId());
				dto.setTestTypeId(runConfig.getTestTypeId());
				dto.setConfigFile(runConfig.getConfigFile());
				dto.setAitess(runConfig.getAitess());
				dto.setDriver(runConfig.getDriver());
				dto.setAitess2ConfigFile(runConfig.getAitees2ConfigFile());
				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch run configurations: " + serviceResponse.getResponseMessage());
		}

		return dtoList;
	}
	
	
		// API : GET ALL RUN CONFIG
		public List<RunConfigurationDto> getAllRunConfig() {
			RunConfigurationService service = new RunConfigurationService();
			RunConfigurationResponse serviceResponse = service.getAllRunConfigurations();

			List<RunConfigurationDto> dtoList = new ArrayList<>();

			if (serviceResponse.getResponseCode() == 1) {
				List<RunConfiguration> runConfigurations = serviceResponse.getRunConfigurations();

				for (RunConfiguration runConfig : runConfigurations) {
					RunConfigurationDto dto = new RunConfigurationDto();
					dto.setRunConfigId(runConfig.getRunConfigId());
					dto.setUutId(runConfig.getUutId());
					dto.setTestTypeId(runConfig.getTestTypeId());
					dto.setConfigFile(runConfig.getConfigFile());
					dto.setAitess(runConfig.getAitess());
					dto.setDriver(runConfig.getDriver());
					dto.setAitess2ConfigFile(runConfig.getAitees2ConfigFile());
					dtoList.add(dto);
				}
			} else {
				System.err.println("Failed to fetch run configurations: " + serviceResponse.getResponseMessage());
			}

			return dtoList;
		}
		
	//API To Delete New
	public Response deleteRunConfigById(String runConfigId)	
	{
		System.out.println("Run Config Id"+runConfigId);
		Response res = new Response();
		try {
	    RunConfigurationService service = new RunConfigurationService();
	    RunConfigurationResponse serviceResponse = service.removeRunConfiguration(runConfigId);
	    RunPathMasterService pathMasterService = new RunPathMasterService();
	    PathMasterDeleteResponse pathsResponse = pathMasterService.deletePathMasterByRunConfigId(runConfigId);
	    Map<String,String>map = pathsResponse.getResponseMap();
	    String testPathMasterId = map.get("tpf");
	    String macroPathMasterId = map.get("macros");
	    String symbolPathMasterId = map.get("symbols");
	    String downloadPathMasterId = map.get("download");
	    System.out.println("RunPathIds Below");
	    System.out.println("testPathMasterId"+testPathMasterId);
	    System.out.println("macroPathMasterId"+macroPathMasterId);
	    System.out.println("symbolPathMasterId"+symbolPathMasterId);
	    System.out.println("downloadPathMasterId"+downloadPathMasterId);
	    //Test File Delete From TestFiles List
	    TestFileService testFileService = new TestFileService();
        Response testFileResponse = testFileService.deleteTestFiles(testPathMasterId);
        if (testFileResponse.getResponseCode() == 0) {
            System.err.println("Failed to update delete status for test files: " + testFileResponse.getResponseMessage());
            res.setResponseMessage("Error on TestFile Deleting" +testFileResponse.getResponseMessage());
            return res;
        }
        
	    //Macro's Deleted From Macros
	    MacroService macroService = new MacroService();
        Response macroResponse = macroService.deleteMacro(macroPathMasterId);
        if (macroResponse.getResponseCode() == 0) {
            System.err.println("Failed to update delete status for macros: " + macroResponse.getResponseMessage());
            res.setResponseMessage("Error on macros Deleting" +macroResponse.getResponseMessage());
            return res;
        }
        
        //Symbols Deleted From Symbols Entity
        SymbolService symbolService = new SymbolService();
        Response symbolResponse = symbolService.deleteSymbols(symbolPathMasterId);
        if (symbolResponse.getResponseCode() == 0) {
            System.err.println("Failed to  delete symbols: " + symbolResponse.getResponseMessage());
            res.setResponseMessage("Error on symbol Deleting" +symbolResponse.getResponseMessage());
            return res;
        }
        
        //DOWNLOAD FILES DELETING
        DownloadFileService downloadFileService = new DownloadFileService();
        Response downloadFileResponse = downloadFileService.deleteDownloadFiles(downloadPathMasterId);
        if (downloadFileResponse.getResponseCode() == 0) {
            System.err.println("Failed delete download files: " + downloadFileResponse.getResponseMessage());
            res.setResponseMessage("Error on Download File Deleting" +downloadFileResponse.getResponseMessage());
            return res;
        }
       
        res.setResponseCode(1);
        res.setResponseMessage("Deleted Successful");
		}
		catch(Exception ex)
		{
			res.setResponseCode(0);
			res.setResponseMessage("Error On deleting   :" +ex.getLocalizedMessage());
		
		}
	    return res; 
		
	}
	
		
		

	//API : DELETE RUN CONFIG
	public RunConfigurationDto[] deleteRunConfig(String runConfigId) {
	    RunConfigurationService service = new RunConfigurationService();
	    RunConfigurationResponse serviceResponse = service.removeRunConfiguration(runConfigId);
	    RunConfigurationDto[] dtoArray = new RunConfigurationDto[0];

	    // Delete entries in RunPathMaster table and retrieve associated runPathMasterIds
	    RunPathMasterService pathMasterService = new RunPathMasterService();
	    Response pathsResponse = pathMasterService.deletePathMaster(runConfigId);
	    
	    	List<String> runPathMasterIdForMacros = pathMasterService.getRunPathMasterIdsForMacros(runConfigId);
	  	    if (serviceResponse.getResponseCode() == 1) {
	        MacroService macroService = new MacroService();
	        Response macroResponse = macroService.updateMacroDeleteStatus(runPathMasterIdForMacros, true);
	        if (macroResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for macros: " + macroResponse.getResponseMessage());
	        }

	        
		    List<String> runPathMasterIdForSymbols = pathMasterService.getRunPathMasterIdsForSymbols(runConfigId);
	        SymbolService symbolService = new SymbolService();
	        Response symbolResponse = symbolService.updateSymbolDeleteStatus(runPathMasterIdForSymbols, true);
	        if (symbolResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for symbols: " + symbolResponse.getResponseMessage());
	        }
	        
	        
		    List<String> runPathMasterIdForTestFiles = pathMasterService.getRunPathMasterIdsForTestFiles(runConfigId);
	        TestFileService testFileService = new TestFileService();
	        Response testFileResponse = testFileService.updateTestFilesDeleteStatus(runPathMasterIdForTestFiles, true);
	        if (testFileResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for test files: " + testFileResponse.getResponseMessage());
	        }
	        
	        List<String> runPathMasterIdForDownloadFiles = pathMasterService.getRunPathMasterIdsForDownloadFiles(runConfigId);
	        DownloadFileService downloadFileService = new DownloadFileService();
	        Response downloadFileResponse = downloadFileService.updateDownloadFilesDeleteStatus(runPathMasterIdForDownloadFiles, true);
	        if (downloadFileResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for download files: " + downloadFileResponse.getResponseMessage());
	        }
	        
	        
	        

	        // Retrieve and return updated RunConfigurationDto array
	        List<RunConfiguration> runConfigurations = serviceResponse.getRunConfigurations();
	        if (runConfigurations != null) {
	            List<RunConfigurationDto> dtoList = new ArrayList<>();
	            for (RunConfiguration runConfigItem : runConfigurations) {
	                RunConfigurationDto dto = new RunConfigurationDto();
	                dto.setRunConfigId(runConfigItem.getRunConfigId());
	                dto.setUutId(runConfigItem.getUutId());
	                dto.setTestTypeId(runConfigItem.getTestTypeId());
	                dto.setConfigFile(runConfigItem.getConfigFile());
	                dto.setAitess(runConfigItem.getAitess());
	                dto.setDriver(runConfigItem.getDriver());
	                dtoList.add(dto);
	            }
	            dtoArray = dtoList.toArray(new RunConfigurationDto[0]);
	        }
	    } else {
	        System.err.println("Failed to remove Run Configuration: " + serviceResponse.getResponseMessage());
	    }

	    return dtoArray;
	}


	// UPDATING RUN PATH MASTER
	private void updatePathsInDatabase(RunConfiguration runConfiguration) {
		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();
			RunPathMasterService pathMasterService = new RunPathMasterService();
			Response pathsResponse = pathMasterService.saveRunPathsToDatabase(runConfiguration.getConfigFile(),
					runConfiguration.getTestTypeId(), runConfiguration.getRunConfigId());
			if (pathsResponse.getResponseCode() == 0) {
				transaction.rollback();
				System.err.println("Failed to save paths to the database: " + pathsResponse.getResponseMessage());
			} else {
				transaction.commit();
			}
		} catch (Exception e) {
			System.err.println("Failed to update paths in the database: " + e.getMessage());
		}
	}
	
	public String fetchRunPathMasterIdForMacro(String runConfigId) {
        try {
            RunPathMasterService runPathMasterService = new RunPathMasterService();
        	return runPathMasterService.fetchRunPathMasterIdForMacro(runConfigId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	 public List<String> fetchMacroFilePathsFromRunPathMaster(String runPathMasterId) {
	        try {
	            RunPathMasterService runPathMasterService = new RunPathMasterService();
	            return runPathMasterService.fetchMacroFilePathsFromRunPathMaster(runPathMasterId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ArrayList<>();
	        }
	    }

	 public String fetchRunPathMasterIdForSymbol(String runConfigId) {
	        try {
	            RunPathMasterService runPathMasterService = new RunPathMasterService();
	            return runPathMasterService.fetchRunPathMasterIdForSymbol(runConfigId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public List<String> fetchSymbolFilePathsFromRunPathMaster(String runPathMasterId) {
	        try {
	            RunPathMasterService runPathMasterService = new RunPathMasterService();
	            return runPathMasterService.fetchSymbolFilePathsFromRunPathMaster(runPathMasterId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ArrayList<>();
	        }
	    }



	    public String fetchRunPathMasterIdForTestFile(String runConfigId) {
	        try {
	            RunPathMasterService runPathMasterService = new RunPathMasterService();
	            return runPathMasterService.fetchRunPathMasterIdForTestFile(runConfigId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public List<String> fetchTestFilePathsFromRunPathMaster(String runPathMasterId) {
	        try {
	            RunPathMasterService runPathMasterService = new RunPathMasterService();
	            return runPathMasterService.fetchTestFilePathsFromRunPathMaster(runPathMasterId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ArrayList<>();
	        }
	    }
	
	    
	    
	    public String fetchRunPathMasterIdForDownloadFile(String runConfigId) {
	        try {
	            RunPathMasterService runPathMasterService = new RunPathMasterService();
	            return runPathMasterService.fetchRunPathMasterIdForDownloadFile(runConfigId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public List<String> fetchDownloadFilePathsFromRunPathMaster(String runPathMasterId) {
	        try {
	            RunPathMasterService runPathMasterService = new RunPathMasterService();
	            return runPathMasterService.fetchDownloadFilePathsFromRunPathMaster(runPathMasterId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ArrayList<>();
	        }
	    }
	
	
}