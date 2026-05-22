package com.teclever.dfcc.datastore.configurationmanagement;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.OfpConfiguration;
import com.teclever.datastore.response.OfpConfigurationResponse;
import com.teclever.datastore.service.DownloadFileService;
import com.teclever.datastore.service.MacroService;
import com.teclever.datastore.service.OfpConfigurationService;
import com.teclever.datastore.service.RunPathMasterService;
import com.teclever.datastore.service.SymbolService;
import com.teclever.datastore.service.TestFileService;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.filemanagement.DownloadFileManagement;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SymbolFileManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.utils.Debug;

public class OfpConfigurationManagement {
	
	
	// API : ADD OFP CONFIG
		public OfpConfigurationResponse addOfpConfig(OfpConfigurationDto ofpConfigurationDto, String uutId) {
			
			System.out.println("UUT TYPE ID"+uutId);
			OfpConfigurationService service = new OfpConfigurationService();
			OfpConfiguration ofpConfiguration = new OfpConfiguration();
			ofpConfiguration.setUutId(uutId);
			ofpConfiguration.setConfigFile(ofpConfigurationDto.getConfigFile());
			ofpConfiguration.setOfpName(ofpConfigurationDto.getOfpName());
			ofpConfiguration.setOfpVersion(ofpConfigurationDto.getOfpVersion());

			OfpConfigurationResponse serviceResponse = new OfpConfigurationResponse();
			try {

				RunConfigurationManagement runConfigurationManagement = new RunConfigurationManagement();
				boolean pbitConifguredStatus = runConfigurationManagement.getStatusPbitConfig(uutId);

				if (!pbitConifguredStatus) {
					// Notifications.showErrorAlert("Pls Configured PBIT Test Type...");
					serviceResponse.setResponseCode(2);
					serviceResponse.setResponseMessage("Pls Configured PBIT Test Type...");
					return serviceResponse;
				} else {
					String rdfPath = runConfigurationManagement.getOutputPathPbitConfig(uutId);
					Map<String, String> paths = new HashMap<String, String>();

					RunPathMasterService runPathMS = new RunPathMasterService();
					paths = runPathMS.parseDATFile(ofpConfigurationDto.getConfigFile());

//					for (Entry<String, String> s : paths.entrySet()) {
//						//System.out.println("KEY PAIRS ::" + s);
//					}

					String currentOFPrdfPath = paths.get("rdfpath");
					//System.out.println("Rdf Path" + currentOFPrdfPath);
					if (!currentOFPrdfPath.trim().equals(rdfPath)) {
						serviceResponse.setResponseCode(2);
						serviceResponse.setResponseMessage("Path Varies From PBIT Test Type...");
						return serviceResponse;
					}

				}

				serviceResponse = service.addOfpConfiguration(ofpConfiguration, uutId);
				if (serviceResponse.getResponseCode() == 1) {
					String ofpConfigId = ofpConfiguration.getOfpConfigId();
					Debug.printDebug(ofpConfigId);

					// updating run path master table
					updatePathsInDatabase(ofpConfiguration);

					// macro
					String runPathMasterId = fetchRunPathMasterIdForMacro(ofpConfigId);
					List<String> macroLocation = fetchMacroFilePathsFromRunPathMaster(runPathMasterId);
					List<String> macrofilePaths = MacroFileManagement.fetchMacroFilePathsDoubleSlash(macroLocation);
					List<MacroDto> macroDtos = MacroFileManagement.saveMacroNames(macrofilePaths, runPathMasterId);

					// symbol
					String runPathMasterId1 = fetchRunPathMasterIdForSymbol(ofpConfigId);
					Debug.printDebug(runPathMasterId1);
					List<String> symbolLocation = fetchSymbolFilePathsFromRunPathMaster(runPathMasterId1);
					List<String> symbolfilePaths = SymbolFileManagement.collectSymbolFilesRecursively(symbolLocation);
					List<SymbolDto> symbolDtos = SymbolFileManagement.saveSymbols(symbolfilePaths, runPathMasterId1);

					// test file
					String runPathMasterId2 = fetchRunPathMasterIdForTestFile(ofpConfigId);
					List<String> testFileLocation = fetchTestFilePathsFromRunPathMaster(runPathMasterId2);
					List<String> testFilesPaths = TestPlanFileManagement.saveTestFilesToDatabase(testFileLocation,
							runPathMasterId2);
					Debug.printDebug(testFileLocation);

					// download file
					String runPathMasterId3 = fetchRunPathMasterIdForDownloadFile(ofpConfigId);
					List<String> downloadFileLocation = fetchDownloadFilePathsFromRunPathMaster(runPathMasterId3);
					List<String> downloadFilesPaths = DownloadFileManagement
							.saveDownloadFilesToDatabase(downloadFileLocation, runPathMasterId3);
					Debug.printDebug(downloadFileLocation);

				} else {
					System.err.println("Failed to add Ofp Configuration: " + serviceResponse.getResponseMessage());
				}
			} catch (Exception e) {
				System.err.println("Failed to add Ofp Configuration: " + e.getMessage());
				serviceResponse.setResponseCode(0);
				serviceResponse.setResponseMessage("Failed to add Ofp Configuration: " + e.getMessage());
			}
			return serviceResponse;
		}

	// API : ADD OFP CONFIG
	public OfpConfigurationResponse addOfpConfigOld(OfpConfigurationDto ofpConfigurationDto, String uutId) {
		OfpConfigurationService service = new OfpConfigurationService();
		OfpConfiguration ofpConfiguration = new OfpConfiguration();
		ofpConfiguration.setUutId(uutId);
		ofpConfiguration.setConfigFile(ofpConfigurationDto.getConfigFile());
		ofpConfiguration.setOfpName(ofpConfigurationDto.getOfpName());
		ofpConfiguration.setOfpVersion(ofpConfigurationDto.getOfpVersion());

		OfpConfigurationResponse serviceResponse = new OfpConfigurationResponse();
		try {
			serviceResponse = service.addOfpConfiguration(ofpConfiguration, uutId);
			if (serviceResponse.getResponseCode() == 1) {
				String ofpConfigId = ofpConfiguration.getOfpConfigId();
				Debug.printDebug(ofpConfigId);

				// updating run path master table
				updatePathsInDatabase(ofpConfiguration);

				// macro
				String runPathMasterId = fetchRunPathMasterIdForMacro(ofpConfigId);
				List<String> macroLocation = fetchMacroFilePathsFromRunPathMaster(runPathMasterId);
				List<String> macrofilePaths = MacroFileManagement.fetchMacroFilePathsDoubleSlash(macroLocation);
				List<MacroDto> macroDtos = MacroFileManagement.saveMacroNames(macrofilePaths, runPathMasterId);

				// symbol
				String runPathMasterId1 = fetchRunPathMasterIdForSymbol(ofpConfigId);
				Debug.printDebug(runPathMasterId1);
				List<String> symbolLocation = fetchSymbolFilePathsFromRunPathMaster(runPathMasterId1);
				List<String> symbolfilePaths = SymbolFileManagement.collectSymbolFilesRecursively(symbolLocation);
				List<SymbolDto> symbolDtos = SymbolFileManagement.saveSymbols(symbolfilePaths, runPathMasterId1);

				// test file
				String runPathMasterId2 = fetchRunPathMasterIdForTestFile(ofpConfigId);
				List<String> testFileLocation = fetchTestFilePathsFromRunPathMaster(runPathMasterId2);
				List<String> testFilesPaths = TestPlanFileManagement.saveTestFilesToDatabase(testFileLocation,runPathMasterId2);
				Debug.printDebug(testFileLocation);
				
				
				// download file
				String runPathMasterId3 = fetchRunPathMasterIdForDownloadFile(ofpConfigId);
				List<String> downloadFileLocation = fetchDownloadFilePathsFromRunPathMaster(runPathMasterId3);
				List<String> downloadFilesPaths = DownloadFileManagement.saveDownloadFilesToDatabase(downloadFileLocation,runPathMasterId3);
				Debug.printDebug(downloadFileLocation);
				

			} else {
				System.err.println("Failed to add Ofp Configuration: " + serviceResponse.getResponseMessage());
			}
		} catch (Exception e) {
			System.err.println("Failed to add Ofp Configuration: " + e.getMessage());
			serviceResponse.setResponseCode(0);
			serviceResponse.setResponseMessage("Failed to add Ofp Configuration: " + e.getMessage());
		}
		return serviceResponse;
	}

	// API : DELETE OFP CONFIG
	public OfpConfigurationResponse deleteOfpConfig(String ofpConfigId) {
	    OfpConfigurationService service = new OfpConfigurationService();
	    OfpConfigurationResponse serviceResponse = service.deleteOfpConfiguration(ofpConfigId);
	    // Delete entries in RunPathMaster table and retrieve associated runPathMasterIds
	    RunPathMasterService pathMasterService = new RunPathMasterService();
	    Response pathsResponse = pathMasterService.deletePathMaster(ofpConfigId);
	    if (serviceResponse.getResponseCode() == 1) {
	        List<String> runPathMasterIdForMacros = pathMasterService.getRunPathMasterIdsForMacros(ofpConfigId);
	        MacroService macroService = new MacroService();
	        Response macroResponse = macroService.updateMacroDeleteStatus(runPathMasterIdForMacros, true);
	        if (macroResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for macros: " + macroResponse.getResponseMessage());
	        }
	        List<String> runPathMasterIdForSymbols = pathMasterService.getRunPathMasterIdsForSymbols(ofpConfigId);
	        SymbolService symbolService = new SymbolService();
	        Response symbolResponse = symbolService.updateSymbolDeleteStatus(runPathMasterIdForSymbols, true);
	        if (symbolResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for symbols: " + symbolResponse.getResponseMessage());
	        }
	        List<String> runPathMasterIdForTestFiles = pathMasterService.getRunPathMasterIdsForTestFiles(ofpConfigId);
	        TestFileService testFileService = new TestFileService();
	        Response testFileResponse = testFileService.updateTestFilesDeleteStatus(runPathMasterIdForTestFiles, true);
	        if (testFileResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for test files: " + testFileResponse.getResponseMessage());
	        }
	        List<String> runPathMasterIdForDownloadFiles = pathMasterService.getRunPathMasterIdsForDownloadFiles(ofpConfigId);
	        DownloadFileService downloadFileService = new DownloadFileService();
	        Response downloadFileResponse = downloadFileService.updateDownloadFilesDeleteStatus(runPathMasterIdForDownloadFiles, true);
	        if (downloadFileResponse.getResponseCode() == 0) {
	            System.err.println("Failed to update delete status for download files: " + downloadFileResponse.getResponseMessage());
	        }
	    } else {
	        System.err.println("Failed to remove Ofp Configuration: " + serviceResponse.getResponseMessage());
	    }
	    return serviceResponse;
	}

	// API : GET OFP CONFIG BASED ON UUT
	public List<OfpConfigurationDto> getOfpConfig(String uutId) {
		OfpConfigurationService service = new OfpConfigurationService();
		OfpConfigurationResponse serviceResponse = service.getOfpConfigurationByUutId(uutId);

		List<OfpConfigurationDto> dtoList = new ArrayList<>();

		if (serviceResponse.getResponseCode() == 1) {
			List<OfpConfiguration> ofpConfigurations = serviceResponse.getOfpConfiguration();

			for (OfpConfiguration ofpConfig : ofpConfigurations) {
				OfpConfigurationDto dto = new OfpConfigurationDto();
				dto.setOfpConfigId(ofpConfig.getOfpConfigId());
				dto.setUutId(ofpConfig.getUutId());
				dto.setConfigFile(ofpConfig.getConfigFile());
				dto.setOfpName(ofpConfig.getOfpName());
				dto.setOfpVersion(ofpConfig.getOfpVersion());
				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch ofp configurations: " + serviceResponse.getResponseMessage());
		}

		return dtoList;
	}

	// API : GET ALL OFP CONFIG
	public List<OfpConfigurationDto> getAllOfpConfig() {
		OfpConfigurationService service = new OfpConfigurationService();
		OfpConfigurationResponse serviceResponse = service.getAllOfpConfigurations();

		List<OfpConfigurationDto> dtoList = new ArrayList<>();

		if (serviceResponse.getResponseCode() == 1) {
			List<OfpConfiguration> ofpConfigurations = serviceResponse.getOfpConfiguration();

			for (OfpConfiguration ofpConfig : ofpConfigurations) {
				OfpConfigurationDto dto = new OfpConfigurationDto();
				dto.setOfpConfigId(ofpConfig.getOfpConfigId());
				dto.setUutId(ofpConfig.getUutId());
				dto.setConfigFile(ofpConfig.getConfigFile());
				dto.setOfpName(ofpConfig.getOfpName());
				dto.setOfpVersion(ofpConfig.getOfpVersion());
				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch ofp configurations: " + serviceResponse.getResponseMessage());
		}

		return dtoList;
	}

	// UPDATING RUN PATH MASTER
	private void updatePathsInDatabase(OfpConfiguration ofpConfiguration) {
		try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();
			RunPathMasterService pathMasterService = new RunPathMasterService();
			Response pathsResponse = pathMasterService.saveOfpPathsToDatabase(ofpConfiguration.getConfigFile(),
					ofpConfiguration.getOfpConfigId());
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

	public String fetchRunPathMasterIdForMacro(String ofpConfigId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			return runPathMasterService.fetchRunPathMasterIdForMacroOfp(ofpConfigId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> fetchMacroFilePathsFromRunPathMaster(String runPathMasterId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			return runPathMasterService.fetchMacroFilePathsFromRunPathMasterOfp(runPathMasterId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public String fetchRunPathMasterIdForSymbol(String ofpConfigId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			return runPathMasterService.fetchRunPathMasterIdForSymbolOfp(ofpConfigId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> fetchSymbolFilePathsFromRunPathMaster(String runPathMasterId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			return runPathMasterService.fetchSymbolFilePathsFromRunPathMasterOfp(runPathMasterId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public String fetchRunPathMasterIdForTestFile(String ofpConfigId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();

			return runPathMasterService.fetchRunPathMasterIdForTestFileOfp(ofpConfigId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> fetchTestFilePathsFromRunPathMaster(String runPathMasterId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();

			return runPathMasterService.fetchTestFilePathsFromRunPathMasterOfp(runPathMasterId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	
	public String fetchRunPathMasterIdForDownloadFile(String ofpConfigId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();

			return runPathMasterService.fetchRunPathMasterIdForDownloadFileOfp(ofpConfigId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> fetchDownloadFilePathsFromRunPathMaster(String runPathMasterId) {
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();

			return runPathMasterService.fetchDownloadFilePathsFromRunPathMasterOfp(runPathMasterId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public static String getConfigFilePathById(String ofpConfigId) {
	    try {
	        SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
	        try (Session session = sessionFactory.openSession()) {
	            Query<OfpConfiguration> query = session.createQuery(
	                "FROM OfpConfiguration WHERE id = :ofpConfigId AND deleteStatus = false", 
	                OfpConfiguration.class
	            );
	            query.setParameter("ofpConfigId", ofpConfigId);
	            OfpConfiguration ofpConfiguration = query.uniqueResult();

	            return (ofpConfiguration != null) ? ofpConfiguration.getConfigFile() : null;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
