package com.teclever.dfcc.datastore.configurationmanagement;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.entities.AitessConfiguration;
import com.teclever.datastore.entities.RunConfiguration;
import com.teclever.datastore.entities.TestTypeMasterDetails;
import com.teclever.datastore.response.AitessConfigurationResponse;
import com.teclever.datastore.response.AitessDriverResponse;
import com.teclever.datastore.response.RunConfigurationResponse;
import com.teclever.datastore.response.TestTypeMasterDetailsResponse;
import com.teclever.datastore.response.UUTMasterDetailsServiceResponse;
import com.teclever.datastore.service.AitessConfigurationService;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.datastore.service.TestTypeMasterDetailsService;
import com.teclever.datastore.service.UUTMasterDetailsService;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.AitessDriverDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;

public class ConfigurationManagement {
	
	
	// API 1 : GET ALL UUT
	public UUTMasterDetailsDto[] getAllUUT() {
		UUTMasterDetailsService service = new UUTMasterDetailsService();
		UUTMasterDetailsServiceResponse serviceResponse = service.getAllUutDetails();
		UUTMasterDetailsDto[] dtoArray = new UUTMasterDetailsDto[0];

		if (serviceResponse.getResponseCode() == 1) {
			String[][] data = serviceResponse.getData();
			dtoArray = new UUTMasterDetailsDto[data.length];

			for (int i = 0; i < data.length; i++) {
				String uutId = data[i][0];
				String uutType = data[i][1];
				UUTMasterDetailsDto dto = new UUTMasterDetailsDto(uutType);
				dto.setUutId(uutId);
				dtoArray[i] = dto;
			}
		}
		return dtoArray;
	}
	
	
	//API 2 : GET TEST TYPE NAME BASED ON UUT
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
	
	
	//API 3 : ADD AITESS CONFIG BASED ON UUT
	public AitessConfigurationResponse addAitessConfig(AitessConfigurationDto aitessConfigurationDto, String uutId) {
	    AitessConfigurationService service = new AitessConfigurationService();
	    
	    AitessConfiguration aitessConfiguration = new AitessConfiguration();
	    aitessConfiguration.setAitessId(aitessConfigurationDto.getAitessId());
	    aitessConfiguration.setUutId(aitessConfigurationDto.getUutId());
	    aitessConfiguration.setAitessName(aitessConfigurationDto.getAitessName());
	    aitessConfiguration.setAitessCommand(aitessConfigurationDto.getAitessCommand());
	    aitessConfiguration.setAitessVersion(aitessConfigurationDto.getAitessVersion());
	    aitessConfiguration.setDriverName(aitessConfigurationDto.getDriverName());
	    aitessConfiguration.setDriverCommand(aitessConfigurationDto.getDriverCommand());
	    aitessConfiguration.setDriverVersion(aitessConfigurationDto.getDriverVersion());
	    aitessConfiguration.setDeleteStatus(aitessConfigurationDto.isDeleteStatus());
	    
	    AitessConfigurationResponse serviceResponse = service.addAitessConfiguration(aitessConfiguration, uutId);

//	    AitessConfigurationDto[] dtoArray = new AitessConfigurationDto[0];
//
//	    if (serviceResponse.getResponseCode() == 1) {
//	        List<AitessConfiguration> configurationList = serviceResponse.getConfigurations();
//	        List<AitessConfigurationDto> dtoList = new ArrayList<>();
//
//	        for (AitessConfiguration configuration : configurationList) {
//	            AitessConfigurationDto dto = new AitessConfigurationDto();
//	            dto.setAitessId(configuration.getAitessId());
//	            dto.setUutId(configuration.getUutId());
//	            dto.setAitessName(configuration.getAitessName());
//	            dto.setAitessCommand(configuration.getAitessCommand());
//	            dto.setAitessVersion(configuration.getAitessVersion());
//	            dto.setDriverName(configuration.getDriverName());
//	            dto.setDriverCommand(configuration.getDriverCommand());
//	            dto.setDriverVersion(configuration.getDriverVersion());
//	            dto.setDeleteStatus(configuration.isDeleteStatus());
//	            dtoList.add(dto);
//	        }
//
//	        dtoArray = dtoList.toArray(new AitessConfigurationDto[0]);
//	    }

	    return serviceResponse;
	}

	
	
	//API 4 : GET AITESS CONFIG LIST BASED ON UUT
	public List<AitessConfigurationDto> getAitessConfig(String uutId) {
        AitessConfigurationService service = new AitessConfigurationService();
        AitessConfigurationResponse serviceResponse = service.getAllAitessConfigurationByUutId(uutId);

        List<AitessConfigurationDto> dtoList = new ArrayList<>();

        if (serviceResponse.getResponseCode() == 1) {
            List<AitessConfiguration> configurationList = serviceResponse.getConfigurations();

            for (AitessConfiguration configuration : configurationList) {
                AitessConfigurationDto dto = new AitessConfigurationDto();
                dto.setAitessId(configuration.getAitessId());
                dto.setUutId(configuration.getUutId());
                dto.setAitessName(configuration.getAitessName());
                dto.setAitessCommand(configuration.getAitessCommand());
                dto.setAitessVersion(configuration.getAitessVersion());
                dto.setDriverName(configuration.getDriverName());
                dto.setDriverCommand(configuration.getDriverCommand());
                dto.setDriverVersion(configuration.getDriverVersion());
                dtoList.add(dto);
            }
        } else {
            System.err.println("Failed to fetch Aitess configurations: " + serviceResponse.getResponseMessage());
        }

        return dtoList;
    }
	
	
	//API 5 : DELETE AITESS CONFIG
	public AitessConfigurationDto[] deleteAitessConfig(int aitessId) {
	    AitessConfigurationService service = new AitessConfigurationService();
	    AitessConfigurationResponse serviceResponse = service.removeAitessConfiguration(aitessId);

	    AitessConfigurationDto[] dtoArray = new AitessConfigurationDto[0];

	    if (serviceResponse.getResponseCode() == 1) {
	        List<AitessConfiguration> configurationList = serviceResponse.getConfigurations();

	        if (configurationList != null) {
	            List<AitessConfigurationDto> dtoList = new ArrayList<>();

	            for (AitessConfiguration configuration : configurationList) {
	                AitessConfigurationDto dto = new AitessConfigurationDto();
	                dto.setAitessId(configuration.getAitessId());
	                dto.setUutId(configuration.getUutId());
	                dto.setAitessName(configuration.getAitessName());
	                dto.setAitessCommand(configuration.getAitessCommand());
	                dto.setAitessVersion(configuration.getAitessVersion());
	                dto.setDriverName(configuration.getDriverName());
	                dto.setDriverCommand(configuration.getDriverCommand());
	                dto.setDriverVersion(configuration.getDriverVersion());
	                dto.setDeleteStatus(configuration.isDeleteStatus());
	                dtoList.add(dto);
	            }

	            dtoArray = dtoList.toArray(new AitessConfigurationDto[0]);
	        }
	    } else {
	        System.err.println("Failed to remove Aitess configuration: " + serviceResponse.getResponseMessage());
	    }

	    return dtoArray;
	}
	

	//API 6 : GET AITESS NAME AND DRIVER NAME BASED ON UUT ID IN RUN CONFIGURATION
	public List<AitessDriverDto> getAitessAndDriverName(String uutId) {
        AitessConfigurationService service = new AitessConfigurationService();
        AitessDriverResponse serviceResponse = service.extractAitessAndDriverNamesByUutId(uutId);

        List<AitessDriverDto> resultList = new ArrayList<>();

        if (serviceResponse.getResponseCode() == 1) {
            List<Object[]> aitessAndDriverNames = serviceResponse.getAitessAndDriverNames();

            for (Object[] names : aitessAndDriverNames) {
                String aitessName = (String) names[0];
                String driverName = (String) names[1];
                AitessDriverDto dto = new AitessDriverDto(aitessName, driverName);
                resultList.add(dto);
            }
        } else {
            System.err.println("Failed to extract Aitess and Driver Names: " + serviceResponse.getResponseMessage());
        }

        return resultList;
    }
	
	
	//API 7 : ADD RUN CONFIG 
	public RunConfigurationResponse addRunConfig(RunConfigurationDto runConfigurationDto, String uutId) {
	    RunConfigurationService service = new RunConfigurationService();
	    RunConfiguration runConfiguration = new RunConfiguration();
	    runConfiguration.setUutId(uutId);
	    runConfiguration.setTestTypeId(runConfigurationDto.getTestTypeId());
	    runConfiguration.setConfigFile(runConfigurationDto.getConfigFile());
	    runConfiguration.setAitess(runConfigurationDto.getAitess());
	    runConfiguration.setDriver(runConfigurationDto.getDriver());

	    RunConfigurationResponse serviceResponse = service.addRunConfiguration(runConfiguration, uutId);

//	    RunConfigurationDto[] dtoArray = new RunConfigurationDto[0];
//
//	    if (serviceResponse.getResponseCode() == 1) {
//	        List<RunConfiguration> runConfigurations = serviceResponse.getRunConfigurations();
//
//	        if (runConfigurations != null) {
//	            List<RunConfigurationDto> dtoList = new ArrayList<>();
//
//	            for (RunConfiguration runConfig : runConfigurations) {
//	                RunConfigurationDto dto = new RunConfigurationDto();
//	                dto.setRunConfigId(runConfig.getRunConfigId());
//	                dto.setUutId(runConfig.getUutId());
//	                dto.setTestTypeId(runConfig.getTestTypeId());
//	                dto.setConfigFile(runConfig.getConfigFile());
//	                dto.setAitess(runConfig.getAitess());
//	                dto.setDriver(runConfig.getDriver());
//	                dtoList.add(dto);
//	            }
//
//	            dtoArray = dtoList.toArray(new RunConfigurationDto[0]);
//	        }
//	    } else {
//	        System.err.println("Failed to add Run Configuration: " + serviceResponse.getResponseMessage());
//	    }

	    return serviceResponse;
	}

	
	
	//API 8 : GET RUN CONFIG BASED ON UUT
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
	                dtoList.add(dto);
	            }
	        } else {
	            System.err.println("Failed to fetch run configurations: " + serviceResponse.getResponseMessage());
	        }

	        return dtoList;
	    }

	 
	 //API 9 : DELETE RUN CONFIG
	 public RunConfigurationDto[] deleteRunConfig(String runConfigId) {
		    RunConfigurationService service = new RunConfigurationService();
		    RunConfigurationResponse serviceResponse = service.removeRunConfiguration(runConfigId);
		    RunConfigurationDto[] dtoArray = new RunConfigurationDto[0];

		    if (serviceResponse.getResponseCode() == 1) {
		        List<RunConfiguration> runConfigurations = serviceResponse.getRunConfigurations();

		        if (runConfigurations != null) {
		            List<RunConfigurationDto> dtoList = new ArrayList<>();

		            for (RunConfiguration runConfig : runConfigurations) {
		                RunConfigurationDto dto = new RunConfigurationDto();
		                dto.setRunConfigId(runConfig.getRunConfigId());
		                dto.setUutId(runConfig.getUutId());
		                dto.setTestTypeId(runConfig.getTestTypeId());
		                dto.setConfigFile(runConfig.getConfigFile());
		                dto.setAitess(runConfig.getAitess());
		                dto.setDriver(runConfig.getDriver());
		                dtoList.add(dto);
		            }

		            dtoArray = dtoList.toArray(new RunConfigurationDto[0]);
		        }
		    } else {
		        System.err.println("Failed to remove Run Configuration: " + serviceResponse.getResponseMessage());
		    }

		    return dtoArray;
		}

}
