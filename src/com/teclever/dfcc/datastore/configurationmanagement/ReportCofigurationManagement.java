package com.teclever.dfcc.datastore.configurationmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.GetObjResponse;
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

public class ReportCofigurationManagement {

	public Response addReportConfig(ReportConfigDto reportConfigDto) {
		Response res = new Response();
		try {
			ReportService reportService = new ReportService();
			GetResponse getResponse = reportService.addReportConfig(reportConfigDto.getFileNameWitFullPath(),
					reportConfigDto.getSessionId(), reportConfigDto.getReportType(),
					reportConfigDto.getLevelOneId(), reportConfigDto.getLevelTwoId(), reportConfigDto.getLevelThreeId(),
					reportConfigDto.getLevelFourId(), reportConfigDto.getLevelFiveId());
			res.setResponseCode(getResponse.getCode());
			res.setResponseMessage(getResponse.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(0);
			res.setResponseMessage("Data Save Unsucessfull ");
		}
		return res;
	}

	public ReportConfigResponse getAllReportConfig(String sessionId, String reportType) {
		ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
		Response res = new Response();
		try {
			ReportService reportService = new ReportService();
			GetResponse getResponse = reportService.getReportCofigDetails(sessionId, reportType);

//			List<ReportConfig> listOfOrderReportConfig = listReportConfig(currentSessionDetails.getSessionId(),getResponse);
			List<?> listOfReportConfigObj = getResponse.getResponseList();

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

//			for (ReportConfig repoConfig : listOfOrderReportConfig) {
			for (Object reportConfigObject : listOfReportConfigObj) {

				ReportConfig repoConfig = (ReportConfig) reportConfigObject;

				ReportConfigDto reportConfigDto = new ReportConfigDto();

				reportConfigDto.setReportConfigId(repoConfig.getReportConfigId());
				reportConfigDto.setFileName(repoConfig.getFileNameWitFullPath());
				reportConfigDto.setReportType(repoConfig.getReportType());
				reportConfigDto.setUploadDate(repoConfig.getUploadDate());

				// Check if levelOneId is not equal to "null"
				if (repoConfig.getLevelOneId() != null) {
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
				}

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
			GetObjResponse getreportConfig = new GetObjResponse();
			getreportConfig = 	reportService.getReportConfigById(reportConfigId);
			ReportConfig reportConfig = new ReportConfig();
			reportConfig = (ReportConfig) getreportConfig.getObject();
			
			String delteFilePath = reportConfig.getFileNameWitFullPath();
			
			System.out.println("Delete File ::"+delteFilePath);

			File file = new File(delteFilePath);
			if (file.exists()) {
				if (file.delete()) {
					System.out.println("File deleted successfully.");
				} else {
					System.out.println("Failed to delete the file.");
				}
			} else {
				System.out.println("File does not exist.");
			}

			// Report Delete the Upload Files

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Delete Data Unsuccesfull");
			e.printStackTrace();
		}
		return res;
	}

}
