package com.teclever.dfcc.datastore.configurationmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.ReportConfig;
import com.teclever.datastore.service.LevelFiveMasterService;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.datastore.service.ReportService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.ReportConfigDto;
import com.teclever.dfcc.datastore.dto.ReportConfigResponse;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class ReportCofigurationManagement {

	public Response addReportConfig(ReportConfigDto reportConfigDto) {
		Response res = new Response();
		try {
			ReportService reportService = new ReportService();
			GetResponse getResponse = reportService.addReportConfig(reportConfigDto.getFileNameWitFullPath(),
					currentSessionDetails.getSessionId(), reportConfigDto.getReportType(),
					reportConfigDto.getLevelOneId(), reportConfigDto.getLevelTwoId(), reportConfigDto.getLevelThreeId(),
					reportConfigDto.getLevelFourId(), reportConfigDto.getLevelFiveId());
			res.setResponseCode(getResponse.getCode());
			res.setResponseMessage(getResponse.geteMsg());
		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(0);
			res.setResponseMessage("Data Save Unsucessfull ");
		}
		return res;
	}

	public ReportConfigResponse getAllReportConfig(String reportType) {
		ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
		Response res = new Response();
		try {
			ReportService reportService = new ReportService();
			GetResponse getResponse = reportService.getReportCofigDetails(currentSessionDetails.getSessionId(),
					reportType);
			List<?> listOfReportConfig = getResponse.getResponseList();
			List<ReportConfigDto> listOfReportConfigDto = new ArrayList<>();

			LevelOneMasterService levelOneService = new LevelOneMasterService();
			Map<String, String> levelOneStage = levelOneService.getAllLevelOneIdAndLevelName();

			LevelTwoMasterService levelTwoService = new LevelTwoMasterService();
			Map<String, String> levelTwoStage = levelTwoService.getAllLevelIdAndLevelName();

			LevelThreeService levelThreeService = new LevelThreeService();
			Map<String, String> levelThreeStage = levelThreeService.getAllLevelIdAndLevelName();

			LevelFourMasterSevice levelFourService = new LevelFourMasterSevice();
			Map<String, String> levelFourStage = levelFourService.getAllLevelIdAndLevelName();

			LevelFiveMasterService levelFiveService = new LevelFiveMasterService();
			Map<String, String> levelFiveStage = levelFiveService.getAllLevelIdAndLevelName();

			for (Object reportConfigObject : listOfReportConfig) {

				ReportConfig repoConfig = (ReportConfig) reportConfigObject;
				ReportConfigDto reportConfigDto = new ReportConfigDto();

				reportConfigDto.setReportConfigId(repoConfig.getReportConfigId());
				reportConfigDto.setFileName(repoConfig.getFileNameWitFullPath());
				reportConfigDto.setReportType(repoConfig.getReportType());

				reportConfigDto.setLevelOneId(repoConfig.getLevelOneId());
				reportConfigDto.setLevelOneName(levelOneStage.get(repoConfig.getLevelOneId()));

				reportConfigDto.setLevelTwoId(repoConfig.getLevelTwoId());
				reportConfigDto.setLevelTwoName(levelTwoStage.get(repoConfig.getLevelTwoId()));

				reportConfigDto.setLevelThreeId(repoConfig.getLevelThreeId());
				reportConfigDto.setLevelThreeName(levelThreeStage.get(repoConfig.getLevelThreeId()));

				reportConfigDto.setLevelFourId(repoConfig.getLevelFourId());
				reportConfigDto.setLevelFourName(levelFourStage.get(repoConfig.getLevelFourId()));

				reportConfigDto.setLevelFiveId(repoConfig.getLevelFiveId());
				reportConfigDto.setLevelFiveName(levelFiveStage.get(repoConfig.getLevelFiveId()));

				listOfReportConfigDto.add(reportConfigDto);

			}
			reportConfigResponse.setListOfReportConfigDto(listOfReportConfigDto);
			if (reportConfigResponse.getListOfReportConfigDto().size() == 0) {
				res.setResponseCode(0);
				res.setResponseMessage("Data is Empty ");
			} else {
				res.setResponseCode(1);
				res.setResponseMessage("Data Fetch Sucessfull ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(0);
			res.setResponseMessage("Data Fetch Unsucessfull ");
		}
		reportConfigResponse.setResponse(res);
		return reportConfigResponse;
	}

	public Response deleteFileName(String reportConfigId) {
		Response res = new Response();
		try {
			ReportService reportService = new ReportService();
			res = reportService.deleteByReportConfigId(reportConfigId);
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Delete Data Unsuccesfull ");
		}
		return res;
	}

}
