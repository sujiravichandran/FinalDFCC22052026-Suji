package com.teclever.dfcc.advanceddataanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.entities.PowerAutoDataAnalysis;
import com.teclever.datastore.entities.PowerManDataAnalysis;
import com.teclever.datastore.entities.RDFFileDetails;
import com.teclever.datastore.entities.SessionMaster;
import com.teclever.datastore.entities.SessionStagesTestFilesResult;
import com.teclever.datastore.response.UUTMasterDetailsServiceResponse;
import com.teclever.datastore.service.PowerAutoDataAnalysisService;
import com.teclever.datastore.service.PowerManDataAnalysisService;
import com.teclever.datastore.service.RDFFileDetailsService;
import com.teclever.datastore.service.SessionMasterService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionStagesTestFilesResultService;
import com.teclever.datastore.service.UUTMasterDetailsService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.dashboard.DashboardManagement;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.FilesFetchFailsDTO;
import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.resultstore.resultmanagement.StepParser;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdvancedDataAnalysisManagement {
	
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	public AdvancedDataAnalysisDTO getAdvancedDataDetails(String uutTypeId) {
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
			List<String> sessionTypeIdList = new ArrayList<String>();
			for (SessionMaster sessionMaster : lst) {
				sessionTypeIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
				sessionTypeIdList.add(sessionMaster.getSessionMasterId());
			}

			List<ResultUnitSessionDetailsDTO> sessionList = resultUnitSessionDetailsResponse
					.getResultUnitSessionDetailsDTOList();

			List<AdvancedDataUnitsDetailsDTO> unitListDetails = new ArrayList<AdvancedDataUnitsDetailsDTO>();
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

			// Seting Unit Session Type Counts
			res.setUnitsDetails(unitListDetails);

			// Setting All Session With Respective UUTtype
			res.setSessionList(sessionList);

		} catch (Exception ex) {
			ex.getLocalizedMessage();
		}
		return res;
	}
	
	
	public AdvancedDataAnalysisDTO getAdvancedDataDetailsUUT(String uutTypeId) {
		AdvancedDataAnalysisDTO res = new AdvancedDataAnalysisDTO();
		try {
			// Get Units Count
			DashboardManagement dashboardManagement = new DashboardManagement();
			ResultUnitSessionDetailsResponse resultUnitSessionDetailsResponse = new ResultUnitSessionDetailsResponse();
			resultUnitSessionDetailsResponse = dashboardManagement.getSessionDetailsForResultsByUnitUUT(uutTypeId);

			SessionMasterService s = new SessionMasterService();

			GetResponse sessionTypeMasterRes = s.getAllSessionMaster();
			List<SessionMaster> lst = (List<SessionMaster>) sessionTypeMasterRes.getResponseList();
			Map<String, String> sessionTypeIdName = new HashMap<String, String>();
			List<String> sessionTypeIdList = new ArrayList<String>();
			for (SessionMaster sessionMaster : lst) {
				sessionTypeIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
				sessionTypeIdList.add(sessionMaster.getSessionMasterId());
			}

			List<ResultUnitSessionDetailsDTO> sessionList = resultUnitSessionDetailsResponse
					.getResultUnitSessionDetailsDTOList();

			List<AdvancedDataUnitsDetailsDTO> unitListDetails = new ArrayList<AdvancedDataUnitsDetailsDTO>();
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

			// Seting Unit Session Type Counts
			res.setUnitsDetails(unitListDetails);

			// Setting All Session With Respective UUTtype
			res.setSessionList(sessionList);

		} catch (Exception ex) {
			ex.getLocalizedMessage();
		}
		return res;
	}
	
	
	
	
	
	
	
	public AdvancedDataAnalysisDTO getAdvancedDataDetailsAdavancedPage(String uutTypeId) {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
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
			List<String> sessionTypeIdList = new ArrayList<String>();
			
			for (SessionMaster sessionMaster : lst) {
				sessionTypeIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
				sessionTypeIdList.add(sessionMaster.getSessionMasterId());
			}

			List<ResultUnitSessionDetailsDTO> sessionList = resultUnitSessionDetailsResponse
					.getResultUnitSessionDetailsDTOList();

			List<AdvancedDataUnitsDetailsDTO> unitListDetails = new ArrayList<AdvancedDataUnitsDetailsDTO>();
			for (String sesType : sessionTypeIdList) {
				String sessionName = sessionTypeIdName.get(sesType);
				AdvancedDataUnitsDetailsDTO advancedDataUnitsDetailsDTO = new AdvancedDataUnitsDetailsDTO();
				int count = sessionList.stream().filter(ses -> ses.getSessionType().equalsIgnoreCase(sessionName))
						.collect(Collectors.toList()).size();
				advancedDataUnitsDetailsDTO.setCount(count);
				advancedDataUnitsDetailsDTO.setSessionTypeId(sesType);
				advancedDataUnitsDetailsDTO.setSessionTypeName(sessionTypeIdName.get(sesType));
				String selectedUutType = fetchUutType(uutTypeId);
				advancedDataUnitsDetailsDTO.setSessionUutType(selectedUutType);
				unitListDetails.add(advancedDataUnitsDetailsDTO);
			}

			// Seting Unit Session Type Counts
			res.setUnitsDetails(unitListDetails);

			// Setting All Session With Respective UUTtype
			res.setSessionList(sessionList);

		} catch (Exception ex) {
			ex.getLocalizedMessage();
		}
		return res;
	}
	
	
	public AdvancedDataAnalysisDTO getAdvancedDataDetailsAdavancedPageUUT(String uutTypeId) {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());
		AdvancedDataAnalysisDTO res = new AdvancedDataAnalysisDTO();
		try {
			// Get Units Count
			DashboardManagement dashboardManagement = new DashboardManagement();
			ResultUnitSessionDetailsResponse resultUnitSessionDetailsResponse = new ResultUnitSessionDetailsResponse();
			resultUnitSessionDetailsResponse = dashboardManagement.getSessionDetailsForResultsByUnitUUT(uutTypeId);

			SessionMasterService s = new SessionMasterService();

			GetResponse sessionTypeMasterRes = s.getAllSessionMaster();
			List<SessionMaster> lst = (List<SessionMaster>) sessionTypeMasterRes.getResponseList();
			Map<String, String> sessionTypeIdName = new HashMap<String, String>();
			List<String> sessionTypeIdList = new ArrayList<String>();
			
			for (SessionMaster sessionMaster : lst) {
				sessionTypeIdName.put(sessionMaster.getSessionMasterId(), sessionMaster.getSessionTypeName());
				sessionTypeIdList.add(sessionMaster.getSessionMasterId());
			}

			List<ResultUnitSessionDetailsDTO> sessionList = resultUnitSessionDetailsResponse
					.getResultUnitSessionDetailsDTOList();

			List<AdvancedDataUnitsDetailsDTO> unitListDetails = new ArrayList<AdvancedDataUnitsDetailsDTO>();
			for (String sesType : sessionTypeIdList) {
				String sessionName = sessionTypeIdName.get(sesType);
				AdvancedDataUnitsDetailsDTO advancedDataUnitsDetailsDTO = new AdvancedDataUnitsDetailsDTO();
				int count = sessionList.stream().filter(ses -> ses.getSessionType().equalsIgnoreCase(sessionName))
						.collect(Collectors.toList()).size();
				advancedDataUnitsDetailsDTO.setCount(count);
				advancedDataUnitsDetailsDTO.setSessionTypeId(sesType);
				advancedDataUnitsDetailsDTO.setSessionTypeName(sessionTypeIdName.get(sesType));
				String selectedUutType = fetchUutType(uutTypeId);
				advancedDataUnitsDetailsDTO.setSessionUutType(selectedUutType);
				unitListDetails.add(advancedDataUnitsDetailsDTO);
			}

			// Seting Unit Session Type Counts
			res.setUnitsDetails(unitListDetails);

			// Setting All Session With Respective UUTtype
			res.setSessionList(sessionList);

		} catch (Exception ex) {
			ex.getLocalizedMessage();
		}
		return res;
	}

	private String fetchUutType(String uutId) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutId().equals(uutId)) {
				return uut.getUutType();
			}
		}
		return null;
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
						String joined = faultyChannelMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
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
//						    ////System.out.println("Parts = " + Arrays.toString(parts));

							for (int i = 0; i < parts.length; i++) {
								String channelValue = parts[i].trim();
//						        ////System.out.println("D* info Check: " + channelValue);

								if (channelValue.equalsIgnoreCase("passed"))
									continue;

								if (channelValue.contains("offline") || channelValue.contains("diff")
										|| channelValue.startsWith("*")) {
									if (channelValue.startsWith("*")) {
										channelValue = channelValue.substring(1).trim();
									}
									formattedChannels.add("CH" + (i + 1) + ": " + channelValue);
								}
							}

//						    ////System.out.println("Formatted Channels Final = " + formattedChannels);
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

//			////System.out.println("All Steps " + allSteps);
//			////System.out.println("All Steps After Filter " + filesFetchFailsDTOList.size());
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
//							////System.out.println(file.getAbsolutePath());
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

	// Get Cummaltive For Current UUT Type..
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
			////System.out.println(getResponse.geteMsg());

		}
		return uutCumlativeHours;
	}

	// Get Cummaltive For All UUT Type..
	public Map<String, String> getCummulativeTimeAllUUTs() {

		Map<String, String> cummaltiveHoursForUUTs = new HashMap<String, String>();
		GetResponse getResponse = new GetResponse();
		try {

			UUTMasterDetailsService uUTMasterDetailsService = new UUTMasterDetailsService();
			UUTMasterDetailsServiceResponse res = uUTMasterDetailsService.getAllUutDetails();
			String[][] allUUts = res.getData();

			for (int i = 0; i < allUUts.length; i++) {
				String uutName = allUUts[i][0];
			}	SessionService sessionService = new SessionService();
			
			
			SessionResponse s = sessionService.getAllSession();
			List<SessionDto> sessionList = new ArrayList<SessionDto>();
			sessionList = s.getListOfSession();
			sessionList = sessionList.stream().filter(session -> session.getUutId().equals(DFCCConstant.selectedUut))
					.collect(Collectors.toList());

			sessionList = sessionList.stream().filter(session -> session.getDfccSNo().equals(DFCCConstant.selectedSNo))
					.collect(Collectors.toList());
			List<String> sessionIds = new ArrayList<String>();
		
			for (SessionDto sessionDto : sessionList) {
				sessionIds.add(sessionDto.getSessionId());
			}
			

			for (int i = 0; i < allUUts.length; i++) {
				String uutName = allUUts[i][0];
				SessionStagesTestFilesResultService sessionStagesTestFilesResultService = new SessionStagesTestFilesResultService();

				getResponse = sessionStagesTestFilesResultService.getTestResultFileByUUTId(uutName);

				List<SessionStagesTestFilesResult> sessionResList = (List<SessionStagesTestFilesResult>) getResponse
						.getResponseList();
				
				
				sessionResList = sessionResList.stream()
				        .filter(ses -> sessionIds.contains(ses.getSessionId()))
				        .toList();  
		

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
			////System.out.println(getResponse.geteMsg());

		}
		return cummaltiveHoursForUUTs;
	}

	public List<StepDeviationDTO> getDeviationForStep(String uutId, String stepNo, double reductionRange) {
		//System.out.println("Check in Methoid Mani " +uutId + stepNo +reductionRange );
		List<StepDeviationDTO> deviationList = new ArrayList<StepDeviationDTO>();
		try {
			List<RDFFileDetails> rDFFileDetailsList = new ArrayList<RDFFileDetails>();
			GetResponse getResponse = new GetResponse();
			RDFFileDetailsService rDFFileDetailsService = new RDFFileDetailsService();
			getResponse = rDFFileDetailsService.getRdfFileDetailsForUUTId(uutId, stepNo);
			rDFFileDetailsList = (List<RDFFileDetails>) getResponse.getResponseList();
			for (RDFFileDetails rDFFileDetails : rDFFileDetailsList) {

				boolean invalidFormat = false;
				StepDeviationDTO stepDeviationDTO = new StepDeviationDTO();

				// Regex to validate and extract numbers
				String pattern = "^\\(\\s*(-?\\d+(?:\\.\\d+)?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?)\\s*\\)$";

				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(rDFFileDetails.getExpectedValue());

				double min = 0.0;
				double max = 0.0;
				if (m.matches()) {
					min = Double.parseDouble(m.group(1));
					stepDeviationDTO.setMinimum(m.group(1));
					max = Double.parseDouble(m.group(2));
					stepDeviationDTO.setMaximum(m.group(2));

					////System.out.println("min = " + min);
					////System.out.println("max = " + max);
					// From Faulty Channel Map Fetch The Channels

				} else {
					invalidFormat = true;
					////System.out.println("Invalid format");
					Notifications.showErrorAlert("Enter Valid Step");
				}

				// Matches: {channelX=value, channelY=value, ...}|
				//String fullPattern = "\\{\\s*channel1=([^,]+),\\s*channel2=([^,]+),\\s*channel3=([^,]+),\\s*channel4=([^}]+)\\}\\|";
				String fullPattern =
					    "\\{\\s*channel1\\s*=\\s*([^,]+)," +
					    "\\s*channel2\\s*=\\s*([^,]+)," +
					    "\\s*channel3\\s*=\\s*([^,]+)," +
					    "\\s*channel4\\s*=\\s*([^}]+)\\s*\\}";

				
				
				Pattern full = Pattern.compile(fullPattern);
				Matcher fullMatch = full.matcher(rDFFileDetails.getFaultyChannel());

				if (!fullMatch.matches()) {
					////System.out.println("Faulty Channel   ::::"+rDFFileDetails.getFaultyChannel());
					////System.out.println("❌ String format does NOT match expected pattern!");
					
//					Notifications.showErrorAlert("Channel Offine For Selected Step"+rDFFileDetails.getFaultyChannel());
					invalidFormat = true;
				} else {
					double channel1 = 0.0;
					double channel2 = 0.0;
					double channel3 = 0.0;
					double channel4 = 0.0;
					// Extract all raw values
					String raw1 = fullMatch.group(1).trim();
					String raw2 = fullMatch.group(2).trim();
					String raw3 = fullMatch.group(3).trim();
					String raw4 = fullMatch.group(4).trim();

					// Fix malformed numbers and parse
					channel1 = Double.parseDouble(fix(raw1));
					channel2 = Double.parseDouble(fix(raw2));
					channel3 = Double.parseDouble(fix(raw3));
					channel4 = Double.parseDouble(fix(raw4));

					// Output results
					////System.out.println("channel1 = " + channel1);
					////System.out.println("channel2 = " + channel2);
					////System.out.println("channel3 = " + channel3);
					////System.out.println("channel4 = " + channel4);

					if (!invalidFormat) {

						// Nominal
						double nominal = (min + max) / 2.0;

						// Range
						double range = max - min;

						// Reduced Range
						double reducedRange = range * (1 - (reductionRange / 100.0));

						// New Min & New Max
						double newMin = nominal + (reducedRange / 2.0);
						double newMax = nominal - (reducedRange / 2.0);

						////System.out.println("Nominal = " + nominal);
						////System.out.println("Range = " + range);
						////System.out.println("Reduced Range = " + reducedRange);
						////System.out.println("New Min = " + newMin);
						////System.out.println("New Max = " + newMax);

						stepDeviationDTO.setNominalRange(String.valueOf(nominal));
						stepDeviationDTO.setNewMax(String.valueOf(newMax));
						stepDeviationDTO.setNewMin(String.valueOf(newMin));
						stepDeviationDTO.setUut(rDFFileDetails.getUutId());
						stepDeviationDTO.setRdfFileName(rDFFileDetails.getRdfFileName());
						

					}

				}

				if (!invalidFormat) {
					deviationList.add(stepDeviationDTO);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return deviationList;
	}

	// Fix malformed values: "0." → "0.0", ".5" → "0.5", "83." → "83.0"
	private static String fix(String num) {
		num = num.trim();
		if (num.startsWith("."))
			num = "0" + num;
		if (num.endsWith("."))
			num = num + "0";
		return num;
	}

	public List<String> getStepsForSession(String sessionId) {
		List<String> stepsInSession = new ArrayList<String>();
		try {
			RDFFileDetailsService rDFFileDetailsService = new RDFFileDetailsService();
			GetResponse getRes = new GetResponse();
			getRes = rDFFileDetailsService.getRdfFileDetailsForSessionStageId(sessionId);
			////System.out.println("Check Response Mgmyt" +getRes.getCode() );
			  List<RDFFileDetails> rDFFileDetailsList = (List<RDFFileDetails>) getRes.getResponseList(); 
			for (RDFFileDetails rDFFileDetails : rDFFileDetailsList) {
				if (!stepsInSession.contains(rDFFileDetails.getStep()))
					stepsInSession.add(rDFFileDetails.getStep());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		////System.out.println("Managemnt chck step No: " + stepsInSession.size());
		return stepsInSession;
	}

	public Response addRDFFilesDetails(List<StepDto> stepDtoList) {

		Response response = new Response();

		try {
			List<RDFFileDetails> rDFFileDetailsList = new ArrayList<RDFFileDetails>();

			for (StepDto stepDto : stepDtoList) {

				RDFFileDetails rDFFileDetails = new RDFFileDetails();

				Map<String, String> faultyChannelMap = stepDto.getFaultyChannel();

				if (faultyChannelMap != null && !faultyChannelMap.isEmpty()) {
					String joined = faultyChannelMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
							.collect(Collectors.joining(", "));
					rDFFileDetails.setFaultyChannel(joined);
				}
				rDFFileDetails.setdStarInfo(stepDto.getdStarInfo());

				List<String> formattedChannels = new ArrayList<>();
				String dStarInfo = stepDto.getdStarInfo();
				if (dStarInfo != null) {

					Pattern pattern = Pattern.compile("\\((.*)\\)");
					Matcher matcher = pattern.matcher(stepDto.getdStarInfo());

					if (matcher.find()) {
						String insideParentheses = matcher.group(1);
						String[] parts = insideParentheses.split(",", -1);
//					    ////System.out.println("Parts = " + Arrays.toString(parts));

						for (int i = 0; i < parts.length; i++) {
							String channelValue = parts[i].trim();
//					        ////System.out.println("D* info Check: " + channelValue);

							if (channelValue.equalsIgnoreCase("passed"))
								continue;

							if (channelValue.contains("offline") || channelValue.contains("diff")
									|| channelValue.startsWith("*")) {
								if (channelValue.startsWith("*")) {
									channelValue = channelValue.substring(1).trim();
								}
								formattedChannels.add("CH" + (i + 1) + ": " + channelValue);
							}
						}

//					    ////System.out.println("Formatted Channels Final = " + formattedChannels);
					}

				}

				rDFFileDetails.setdStarInfo(String.join(", ", formattedChannels));
				rDFFileDetails.setExpectedValue(stepDto.getExpectedValue());
				rDFFileDetails.setFaultySRU(stepDto.getFaultySRU());
				rDFFileDetails.setInput(stepDto.getInput());
				rDFFileDetails.setUnit(stepDto.getUnit());
				rDFFileDetails.setSignalName(stepDto.getSignalName());
				rDFFileDetails.setTpgph(stepDto.getTpgph());
				rDFFileDetails.setStep(stepDto.getStep());
				rDFFileDetails.setSessionId(StateMachine.currentSessionDetails.getSessionId());
				rDFFileDetails.setStageId(DFCCConstant.stageId);
				rDFFileDetails.setSessionMapId(DFCCConstant.sessionStageMapId);
				rDFFileDetails.setUutId(StateMachine.currentSessionDetails.getUutId());
				rDFFileDetails.setRdfFileName(DFCCConstant.fileNameRdf);
				rDFFileDetails.setNewMinimum(DFCCConstant.tpfFileName);
				rDFFileDetails.setUutId(StateMachine.currentSessionDetails.getUutId());
				rDFFileDetails.setSerialNo(StateMachine.currentSessionDetails.getDfccSerialNumber());
				if (stepDto.getFaultyChannel().size() > 0) {
					// stepDto.getFaultyChannel().size()>0
					Map<String, String> chValues = stepDto.getFaultyChannel();
					rDFFileDetails.setFaultyChannel(chValues.toString());
					rDFFileDetails.setChannelType("FC");

				} else {
					if (stepDto.getChannelValues() != null) {
						Map<String, String> chValues = stepDto.getChannelValues();
						rDFFileDetails.setFaultyChannel(chValues.toString());
						rDFFileDetails.setChannelType("CC");
					}
				}

				rDFFileDetailsList.add(rDFFileDetails);
			}
			
			////System.out.println("Rdf Details Service"+rDFFileDetailsList.size());
			RDFFileDetailsService rDFFileDetailsService = new RDFFileDetailsService();
			response = rDFFileDetailsService.addPowerAutoDataAnalysisForSession(rDFFileDetailsList);


		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}



	// Get Power Man Configuration..
	public PowerAutoDataAnalysis getPowerAutoConfig(String sessionId, String stageId) {
		PowerAutoDataAnalysis powerAutoDataAnalysis = new PowerAutoDataAnalysis();
		try {
			PowerAutoDataAnalysisService powerAutoDataAnalysisService = new PowerAutoDataAnalysisService();
			GetResponse getResponse = new GetResponse();
			getResponse = powerAutoDataAnalysisService.getPowerAutoDataAnalysisForSessionStageId(sessionId, stageId);
			List<PowerAutoDataAnalysis> lst = (List<PowerAutoDataAnalysis>) getResponse.getResponseList();
			if (lst != null) {
				powerAutoDataAnalysis = lst.get(lst.size() - 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return powerAutoDataAnalysis;
	}

	// Add Power Auto Configuration..
	public Response addPowerAutoConfig(PowerAutoDataAnalysis powerAutoDataAnalysis, String sessionId, String stageId) {
		Response response = new Response();
		try {

			PowerAutoDataAnalysisService powerAutoDataAnalysisService = new PowerAutoDataAnalysisService();
			Response resDelt = powerAutoDataAnalysisService.deletePowerAutoForSession(
					powerAutoDataAnalysis.getSessionId(), powerAutoDataAnalysis.getStageId());
			powerAutoDataAnalysisService.addPowerAutoDataAnalysisForSession(powerAutoDataAnalysis, sessionId, stageId);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setResponseMessage("Not Added Power Auto ::" + ex.getLocalizedMessage());
		}

		return response;
	}

	// Get Power Man Configuration..
	public PowerManDataAnalysis getPowerManConfig(String sessionId, String stageId) {
		PowerManDataAnalysis powerManDataAnalysis = new PowerManDataAnalysis();
		try {
			PowerManDataAnalysisService powerManDataAnalysisService = new PowerManDataAnalysisService();
			GetResponse getResponse = new GetResponse();
			getResponse = powerManDataAnalysisService.getPowerManDataAnalysisForSessionStageId(sessionId, stageId);
			List<PowerManDataAnalysis> lst = (List<PowerManDataAnalysis>) getResponse.getResponseList();
			if (lst != null) {
				powerManDataAnalysis = lst.get(lst.size() - 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return powerManDataAnalysis;
	}

	// Add Power Man Configuration..
	public Response addPowerManConfig(PowerManDataAnalysis powerManDataAnalysis) {
		Response response = new Response();
		try {
			
			PowerManDataAnalysisService powerManDataAnalysisService = new PowerManDataAnalysisService();
			Response resDelt = powerManDataAnalysisService.deletePowerManForSession(powerManDataAnalysis.getSessionId(),
					powerManDataAnalysis.getStageId());
			powerManDataAnalysisService.addPowerManDataAnalysisForSession(powerManDataAnalysis);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setResponseMessage("Not Added Power Man ::" + ex.getLocalizedMessage());
		}

		return response;
	}

}
