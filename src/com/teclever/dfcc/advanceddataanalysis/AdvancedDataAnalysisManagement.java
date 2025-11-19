package com.teclever.dfcc.advanceddataanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.teclever.datastore.entities.SessionMaster;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.response.UUTMasterDetailsServiceResponse;
import com.teclever.datastore.service.SessionMasterService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.UUTMasterDetailsService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.dashboard.DashboardManagement;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.FilesFetchFailsDTO;
import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.resultstore.resultmanagement.StepParser;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class AdvancedDataAnalysisManagement {
	
	public AdvancedDataAnalysisDTO getAdvancedDataDetails(String uutTypeId)
	{
		AdvancedDataAnalysisDTO res = new AdvancedDataAnalysisDTO();
		try {
			// Get Units Count
			DashboardManagement dashboardManagement = new DashboardManagement();
			ResultUnitSessionDetailsResponse resultUnitSessionDetailsResponse = new ResultUnitSessionDetailsResponse();
			resultUnitSessionDetailsResponse = dashboardManagement.getSessionDetailsForResultsByUnit(uutTypeId);
		
			SessionMasterService s = new SessionMasterService();

			GetResponse sessionTypeMasterRes = s.getAllSessionMaster();
			List<SessionMaster> lst = (List<SessionMaster>) sessionTypeMasterRes.getResponseList();
			Map<String, String> sessionTypeIdName = new HashMap<String, String>();
			List<String>sessionTypeIdList = new ArrayList<String>();
			for (SessionMaster sessionMaster : lst) {
				sessionTypeIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
				sessionTypeIdList.add(sessionMaster.getSessionMasterId());
			}
			
			List<ResultUnitSessionDetailsDTO>sessionList = resultUnitSessionDetailsResponse.getResultUnitSessionDetailsDTOList();
			
			List<AdvancedDataUnitsDetailsDTO>unitListDetails = new ArrayList<AdvancedDataUnitsDetailsDTO>();
			for (String sesType : sessionTypeIdList) {
				String sessionName = sessionTypeIdName.get(sesType);
				AdvancedDataUnitsDetailsDTO advancedDataUnitsDetailsDTO = new AdvancedDataUnitsDetailsDTO();
				int count = sessionList.stream().filter(ses -> ses.getSessionType().equalsIgnoreCase(sessionName))
						.collect(Collectors.toList()).size();
				advancedDataUnitsDetailsDTO.setCount(count);
				advancedDataUnitsDetailsDTO.setSessionTypeId(sesType);
				advancedDataUnitsDetailsDTO.setSessionTypeName(sessionTypeIdName.get(sesType));
				advancedDataUnitsDetailsDTO.setSessionUutType(currentSessionDetails.getUutType());
				unitListDetails.add(advancedDataUnitsDetailsDTO);
			}
			
			//Seting Unit Session Type Counts
			res.setUnitsDetails(unitListDetails);
			
			
			//Setting All Session With Respective UUTtype
			res.setSessionList(sessionList);
			
			
			
		} catch (Exception ex) {
			ex.getLocalizedMessage();
		}
		return res;
	}
	
	
	
	public List<FilesFetchFailsDTO> getAllFailsByFolder(String path) {
		List<FilesFetchFailsDTO> filesFetchFailsDTOList = new ArrayList<FilesFetchFailsDTO>();
		List<StepDto> allSteps = new ArrayList<StepDto>();
		try {
			File filePath = new File(path);
			List<String> listFilesRecursively = listFilesRecursively(filePath);
			// From The Rdf Files Extracting The Steps
			for (String rdfFullPath : listFilesRecursively) {
				List<StepDto> stepDtoList = new ArrayList<StepDto>();
				stepDtoList = StepParser.parseStepContextNEW(rdfFullPath);
				for (StepDto stepDto : stepDtoList) {
					FilesFetchFailsDTO filesFetchFailsDTO = new FilesFetchFailsDTO();
					
					Map<String, String> faultyChannelMap = stepDto.getFaultyChannel();

					if (faultyChannelMap != null && !faultyChannelMap.isEmpty()) {
					    String joined = faultyChannelMap.entrySet()
					        .stream()
					        .map(e -> e.getKey() + "=" + e.getValue())
					        .collect(Collectors.joining(", "));
					    filesFetchFailsDTO.setFaultyChannel(joined);
					}
					filesFetchFailsDTO.setdStarInfo(stepDto.getdStarInfo());
			
					List<String> formattedChannels = new ArrayList<>();
					String dStarInfo = stepDto.getdStarInfo();
					if (dStarInfo != null) {
					    
						Pattern pattern = Pattern.compile("\\((.*)\\)"); 
						Matcher matcher = pattern.matcher(stepDto.getdStarInfo());

					
						if (matcher.find()) {
						    String insideParentheses = matcher.group(1);
						    String[] parts = insideParentheses.split(",", -1); 
//						    System.out.println("Parts = " + Arrays.toString(parts));

						    for (int i = 0; i < parts.length; i++) {
						        String channelValue = parts[i].trim();
//						        System.out.println("D* info Check: " + channelValue);

						        if (channelValue.equalsIgnoreCase("passed")) continue;

						        if (channelValue.contains("offline") || channelValue.contains("diff") || channelValue.startsWith("*")) {
						            if (channelValue.startsWith("*")) {
						                channelValue = channelValue.substring(1).trim();
						            }
						            formattedChannels.add("CH" + (i + 1) + ": " + channelValue);
						        }
						    }

//						    System.out.println("Formatted Channels Final = " + formattedChannels);
						}



		 
					}

					filesFetchFailsDTO.setdStarChannels(String.join(", ", formattedChannels));
					filesFetchFailsDTO.setExpectedValue(stepDto.getExpectedValue());
					filesFetchFailsDTO.setFaultySRU(stepDto.getFaultySRU());
					filesFetchFailsDTO.setFilePath(rdfFullPath);
					filesFetchFailsDTO.setInput(stepDto.getInput());
					filesFetchFailsDTO.setLowerLimit(stepDto.getLowerLimit());
					filesFetchFailsDTO.setMeasuredValue(stepDto.getMeasuredValue());
					filesFetchFailsDTO.setReadingInfo(stepDto.getReadingInfo());
					filesFetchFailsDTO.setResultDataFile(stepDto.getResultDataFile());
					filesFetchFailsDTO.setSignalName(stepDto.getSignalName());
					filesFetchFailsDTO.setStep(stepDto.getStep());
					filesFetchFailsDTO.setTestPlanFile(stepDto.getTestPlanFile());
					filesFetchFailsDTO.setTpgph(stepDto.getTpgph());
					filesFetchFailsDTO.setUnit(stepDto.getUnit());
					filesFetchFailsDTO.setUpperLimit(stepDto.getUpperLimit());
					filesFetchFailsDTOList.add(filesFetchFailsDTO);
				}
				allSteps.addAll(stepDtoList);
			}
			filesFetchFailsDTOList = filesFetchFailsDTOList.stream()
					.filter(e -> e.getFaultyChannel() != null && !e.getFaultyChannel().isEmpty())
					.collect(Collectors.toList());
//			System.out.println("All Steps " + allSteps);
//			System.out.println("All Steps After Filter " + filesFetchFailsDTOList.size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return filesFetchFailsDTOList;
	}
	
	
	public List<String> listFilesRecursively(File folder) {
		List<String> listOfPath = new ArrayList<>();
		try {
			File[] filesAndDirs = folder.listFiles();
			if (filesAndDirs != null) {
				for (File file : filesAndDirs) {
					if (file.isFile()) {
						if (file.getName().toLowerCase().contains(".rdf")) {
//							System.out.println(file.getAbsolutePath());
							listOfPath.add(file.getAbsolutePath());
						}
					} else if (file.isDirectory()) {
						listOfPath.addAll(listFilesRecursively(file));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return listOfPath;
	}
	
	//Get Cummaltive For Current UUT Type..
	public String getCummulativeTimeForCurrentUut(String uutType) {
		String uutCumlativeHours = "";
		GetResponse getResponse = new GetResponse();
		try {

			SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();

			getResponse = sessionStagesTestFilesResultService.getTestResultFileByUUTId(uutType);

			List<SessionStagesTestFilesResult> sessionResList = (List<SessionStagesTestFilesResult>) getResponse
					.getResponseList();

			SessionFileManagement sessionFileManagment = new SessionFileManagement();
			long runnedSec = 0;
			for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionResList) {
				runnedSec = sessionFileManagment.getRunnedSeconds(sessionStagesTestFilesResult.getStartTime(),
						sessionStagesTestFilesResult.getEndTime());
			}
			ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
			uutCumlativeHours = resultExecutionManagement.formatSecondsToHHMMSS(runnedSec);

		} catch (Exception ex) {
			System.out.println(getResponse.geteMsg());

		}
		return uutCumlativeHours;
	}

	//Get Cummaltive For All UUT Type.. 
	public Map<String, String> getCummulativeTimeAllUUTs() {

		Map<String, String> cummaltiveHoursForUUTs = new HashMap<String, String>();
		GetResponse getResponse = new GetResponse();
		try {

			UUTMasterDetailsService uUTMasterDetailsService = new UUTMasterDetailsService();
			UUTMasterDetailsServiceResponse res = uUTMasterDetailsService.getAllUutDetails();
			String[][] allUUts = res.getData();

			for (int i = 0; i < allUUts.length; i++) {
				String uutName = allUUts[i][0];
			}

			for (int i = 0; i < allUUts.length; i++) {
				String uutName = allUUts[i][0];
				SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();

				getResponse = sessionStagesTestFilesResultService.getTestResultFileByUUTId(uutName);

				List<SessionStagesTestFilesResult> sessionResList = (List<SessionStagesTestFilesResult>) getResponse
						.getResponseList();

				SessionFileManagement sessionFileManagment = new SessionFileManagement();
				long runnedSec = 0;
				for (SessionStagesTestFilesResult sessionStagesTestFilesResult : sessionResList) {
					Long runnedSec1 = sessionFileManagment.getRunnedSeconds(sessionStagesTestFilesResult.getStartTime(),
							sessionStagesTestFilesResult.getEndTime());
					runnedSec = runnedSec1 + runnedSec;
				}
				ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
				String value = resultExecutionManagement.formatSecondsToHHMMSS(runnedSec);
				cummaltiveHoursForUUTs.put(uutName, value);
			}

		} catch (Exception ex) {
			System.out.println(getResponse.geteMsg());

		}
		return cummaltiveHoursForUUTs;
	}
	
	
	
	
}
