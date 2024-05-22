package com.teclever.dfcc.datastore.configurationmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.LevelOneResponseDto;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.StageLevelResponse;
import com.teclever.datastore.dto.StageMasterLevelResponse;
import com.teclever.datastore.dto.SubLevelResponseDto;
import com.teclever.datastore.entities.SessionMaster;
import com.teclever.datastore.entities.TestFile;
import com.teclever.datastore.entities.TestFilesStagesMapping;
import com.teclever.datastore.service.LevelFiveMasterService;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.datastore.service.SessionMasterService;
import com.teclever.datastore.service.TestFileService;
import com.teclever.datastore.service.TestFilesStagesMappingService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.LevelOneAddResponse;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.LevelsAddResponse;
import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelDto;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.StageMasterLevelsResponse;
import com.teclever.dfcc.datastore.dto.StagesFilesDTO;
import com.teclever.dfcc.datastore.dto.StagesFilesResponseDTO;

public class StageConfiguration {

	public StageMasterLevelOneResponse getLevelOneStageMaster(String uutId, String userType) {
		StageMasterLevelOneResponse stageMasterLevelOne = new StageMasterLevelOneResponse();
		try {
			LevelOneMasterService levelOne = new LevelOneMasterService();
			StageLevelResponse serviceResponse = levelOne.getLevelTOneMasterByUUTId(uutId, userType);

			if (serviceResponse.getResponse().getResponseCode() == 0) {
				stageMasterLevelOne.setResponse(serviceResponse.getResponse());
				stageMasterLevelOne.setLevelOneResponse(null);
				return stageMasterLevelOne;
			}
			List<?> listOfLevelOneStage = serviceResponse.getStageLevelList();
			List<LevelOneDto> listOfLevelOnDto = new ArrayList<>();
			for (Object levelOneStageMaster : listOfLevelOneStage) {
				LevelOneResponseDto levelOneEntity = (LevelOneResponseDto) levelOneStageMaster;

				LevelOneDto levelOneDto = new LevelOneDto();
				levelOneDto.setLevelOneId(levelOneEntity.getLevelOneId());
				levelOneDto.setStageName(levelOneEntity.getStageName());
				levelOneDto.setSessionIds(levelOneEntity.getSessionIds());
				levelOneDto.setUutId(levelOneEntity.getUutId());
				levelOneDto.setNextLevel(levelOneEntity.getNextLevel());

				listOfLevelOnDto.add(levelOneDto);

			}
			stageMasterLevelOne.setLevelOneResponse(listOfLevelOnDto);
			stageMasterLevelOne.setResponse(serviceResponse.getResponse());
			return stageMasterLevelOne;
		} catch (Exception e) {
			throw e;
		}
	}

	public StageMasterLevelsResponse getStageLevelMaster(String parentId) {
		StageMasterLevelsResponse stageMasterLevel = new StageMasterLevelsResponse();
		try {
			StageLevelResponse serviceResponse = new StageLevelResponse();
			String levelType = parentId.substring(0, 2);
			System.out.println("Level Type  " + levelType);
			switch (levelType) {
			case "L1":
				LevelTwoMasterService levelTwo = new LevelTwoMasterService();
				serviceResponse = levelTwo.getLevelTwoMasterByLevleOneId(parentId);

				break;
			case "L2":
				LevelThreeService levelThree = new LevelThreeService();
				serviceResponse = levelThree.getLevelThreeMasterByLevleTwoId(parentId);
				break;
			case "L3":
				LevelFourMasterSevice levelFour = new LevelFourMasterSevice();
				serviceResponse = levelFour.getLevelFourMasterByLevleThreeId(parentId);
				break;
			case "L4":
				LevelFiveMasterService levelFive = new LevelFiveMasterService();
				serviceResponse = levelFive.getLevelFiveMasterByLevleFourId(parentId);

				break;
			default:
				Response res = new Response();
				res.setResponseCode(0);
				res.setResponseMessage("Invalid Input ");
				stageMasterLevel.setResponse(res);
				stageMasterLevel.setLevelsResponse(null);
				return stageMasterLevel;
			}

			if (serviceResponse.getResponse().getResponseCode() == 0) {
				stageMasterLevel.setResponse(serviceResponse.getResponse());
				stageMasterLevel.setLevelsResponse(null);
				return stageMasterLevel;
			}
			List<?> listOfLevelsStage = serviceResponse.getStageLevelList();
			List<StageMasterLevelDto> listOfstageMasterLevelDto = new ArrayList<>();

			for (Object stageMasterService : listOfLevelsStage) {
				SubLevelResponseDto subLevelResponseDto = (SubLevelResponseDto) stageMasterService;

				StageMasterLevelDto stageMasterLevelDto = new StageMasterLevelDto();
				stageMasterLevelDto.setLevelId(subLevelResponseDto.getLevelId());
				stageMasterLevelDto.setStageName(subLevelResponseDto.getStageName());
				stageMasterLevelDto.setParentId(subLevelResponseDto.getParentId());
				stageMasterLevelDto.setNextLevel(subLevelResponseDto.getNextLevel());
				stageMasterLevelDto.setTestType(subLevelResponseDto.getTestType());

				listOfstageMasterLevelDto.add(stageMasterLevelDto);
			}
			stageMasterLevel.setLevelsResponse(listOfstageMasterLevelDto);
			stageMasterLevel.setResponse(serviceResponse.getResponse());
			return stageMasterLevel;

		} catch (Exception e) {
			Response res = new Response();
			res.setResponseCode(0);
			res.setResponseMessage("Add Stage Unsuccessfull : " + e.getLocalizedMessage());
			stageMasterLevel.setResponse(res);
			stageMasterLevel.setLevelsResponse(null);
			return stageMasterLevel;
		}
	}

	public LevelOneAddResponse addLevelOneStageMaster(String stageName, String sessions, String uutIds,
			String userType) {
		LevelOneAddResponse levelOneResponseDto = new LevelOneAddResponse();

		try {
			LevelOneMasterService levelOne = new LevelOneMasterService();
			StageMasterLevelResponse serviceResponse = levelOne.addLevelOneStage(stageName, sessions, uutIds, userType);
			levelOneResponseDto.setResponse(serviceResponse.getResponse());

			if (serviceResponse.getResponse().getResponseCode() == 0) {
				levelOneResponseDto.setLevelOneResponse(null);
				return levelOneResponseDto;
			}
			LevelOneResponseDto levelOneEntity = (LevelOneResponseDto) serviceResponse.getStageLevelMaster();
			LevelOneDto levelOneDto = new LevelOneDto();
			levelOneDto.setLevelOneId(levelOneEntity.getLevelOneId());
			levelOneDto.setStageName(levelOneEntity.getStageName());
			levelOneDto.setNextLevel(levelOneEntity.getNextLevel());
			levelOneDto.setSessionIds(levelOneEntity.getSessionIds());
			levelOneDto.setUutId(levelOneEntity.getUutId());

			levelOneResponseDto.setLevelOneResponse(levelOneDto);

			return levelOneResponseDto;

		} catch (Exception e) {
			Response res = new Response();
			res.setResponseCode(0);
			res.setResponseMessage("Add Stage Unsuccessfull : " + e.getLocalizedMessage());
			levelOneResponseDto.setResponse(res);
			levelOneResponseDto.setLevelOneResponse(null);
			return levelOneResponseDto;
		}
	}

	public LevelsAddResponse addStageMasterLevel(String parentId, String stageName, String testTypeId) {
		LevelsAddResponse levelsResponseDto = new LevelsAddResponse();
		try {
			StageMasterLevelResponse subLevelserviceResponse = new StageMasterLevelResponse();
			String levelType = parentId.substring(0, 2);
			StageMasterLevelDto levelsDto = new StageMasterLevelDto();
			switch (levelType) {
			case "L1":
				LevelTwoMasterService levelTwo = new LevelTwoMasterService();
				subLevelserviceResponse = levelTwo.addLevelTwoStage(parentId, stageName, testTypeId);
				break;
			case "L2":
				LevelThreeService levelThree = new LevelThreeService();
				subLevelserviceResponse = levelThree.addLevelThreeStage(parentId, stageName, testTypeId);
				break;
			case "L3":
				LevelFourMasterSevice levelFour = new LevelFourMasterSevice();
				subLevelserviceResponse = levelFour.addLevelFourStage(parentId, stageName, testTypeId);

				break;
			case "L4":
				LevelFiveMasterService levelFive = new LevelFiveMasterService();
				subLevelserviceResponse = levelFive.addLevelFiveStage(parentId, stageName, testTypeId);

				break;
			default:
				Response res = new Response();
				res.setResponseCode(0);
				res.setResponseMessage("Invalid Input ");
				levelsResponseDto.setResponse(res);
				levelsResponseDto.setLevelsResponse(null);
				return levelsResponseDto;
			}

			levelsResponseDto.setResponse(subLevelserviceResponse.getResponse());

			if (subLevelserviceResponse.getResponse().getResponseCode() == 0) {
				levelsResponseDto.setLevelsResponse(null);
				return levelsResponseDto;
			}
			SubLevelResponseDto levelThreeEntity = (SubLevelResponseDto) subLevelserviceResponse.getStageLevelMaster();

			levelsDto.setLevelId(levelThreeEntity.getLevelId());
			levelsDto.setStageName(levelThreeEntity.getStageName());
			levelsDto.setNextLevel(levelThreeEntity.getNextLevel());
			levelsDto.setTestType(levelThreeEntity.getTestType());
			levelsDto.setParentId(levelThreeEntity.getNextLevel());

			levelsResponseDto.setLevelsResponse(levelsDto);

			return levelsResponseDto;
		} catch (Exception e) {
			Response res = new Response();
			res.setResponseCode(0);
			res.setResponseMessage("Add Stage Unsuccessfull : " + e.getLocalizedMessage());
			levelsResponseDto.setResponse(res);
			levelsResponseDto.setLevelsResponse(null);
			return levelsResponseDto;
		}
	}

	public Response deleteStageLevelById(String levelId) {
		Response response = new Response();
		try {
			String levelType = levelId.substring(0, 2);
			// Delete
			TestFilesStagesMappingService test = new TestFilesStagesMappingService();
			GetResponse res1 = test.getTestFilesStagesMappingByLastLevelReference(levelId);
			List<?> resList = res1.getResponseList();
			if (resList.size() > 0) {
				test.deleteTestFilesMapping(levelId);
			}
			switch (levelType) {
			case "L1":
				LevelOneMasterService levelOne = new LevelOneMasterService();
				response = levelOne.deleteStageOneRelatedSubStages(levelId);
				break;
			case "L2":
				LevelTwoMasterService levelTwo = new LevelTwoMasterService();
				response = levelTwo.deleteStageTwoRelatedSubStages(levelId);
				break;
			case "L3":
				LevelThreeService levelThree = new LevelThreeService();
				response = levelThree.deleteStageThreeRelatedSubStages(levelId);
				break;
			case "L4":
				LevelFourMasterSevice levelFour = new LevelFourMasterSevice();
				response = levelFour.deleteStageFourRelatedSubstages(levelId);

				break;
			case "L5":
				LevelFiveMasterService levelFive = new LevelFiveMasterService();
				response = levelFive.deleteLevelFiveStateMaster(levelId);

				break;
			default:
				Response res = new Response();
				res.setResponseCode(0);
				res.setResponseMessage("Invalid Input ");
				return res;
			}
		} catch (Exception e) {

		}
		return response;
	}
	
	public LevelsAddResponse updateStageMasterLevel(String levelId,String parentId, String stageName, String testTypeId) {
		LevelsAddResponse levelsResponseDto = new LevelsAddResponse();
		try {
			StageMasterLevelResponse subLevelserviceResponse = new StageMasterLevelResponse();
			String levelType = levelId.substring(0, 2);
			StageMasterLevelDto levelsDto = new StageMasterLevelDto();
			switch (levelType) {
			case "L2":
				LevelTwoMasterService levelTwo = new LevelTwoMasterService();
				subLevelserviceResponse = levelTwo.updateLevelTwoStage(levelId,parentId, stageName, testTypeId);
				break;
			case "L3":
				LevelThreeService levelThree = new LevelThreeService();
				subLevelserviceResponse = levelThree.updateLevelThreeStage(levelId,parentId, stageName, testTypeId);
				break;
			case "L4":
				LevelFourMasterSevice levelFour = new LevelFourMasterSevice();
				subLevelserviceResponse = levelFour.updateLevelFourStage(levelId,parentId, stageName, testTypeId);

				break;
			case "L5":
				LevelFiveMasterService levelFive = new LevelFiveMasterService();
				subLevelserviceResponse = levelFive.updateLevelFiveStage(levelId,parentId, stageName, testTypeId);

				break;
			default:
				Response res = new Response();
				res.setResponseCode(0);
				res.setResponseMessage("Invalid Input ");
				levelsResponseDto.setResponse(res);
				levelsResponseDto.setLevelsResponse(null);
				return levelsResponseDto;
			}

			levelsResponseDto.setResponse(subLevelserviceResponse.getResponse());

			if (subLevelserviceResponse.getResponse().getResponseCode() == 0) {
				levelsResponseDto.setLevelsResponse(null);
				return levelsResponseDto;
			}
			SubLevelResponseDto levelsEntity = (SubLevelResponseDto) subLevelserviceResponse.getStageLevelMaster();

			levelsDto.setLevelId(levelsEntity.getLevelId());
			levelsDto.setStageName(levelsEntity.getStageName());
			levelsDto.setNextLevel(levelsEntity.getNextLevel());
			levelsDto.setTestType(levelsEntity.getTestType());
			levelsDto.setParentId(levelsEntity.getNextLevel());

			levelsResponseDto.setLevelsResponse(levelsDto);

			return levelsResponseDto;
		} catch (Exception e) {
			Response res = new Response();
			res.setResponseCode(0);
			res.setResponseMessage("Add Stage Unsuccessfull : " + e.getLocalizedMessage());
			levelsResponseDto.setResponse(res);
			levelsResponseDto.setLevelsResponse(null);
			return levelsResponseDto;
		}
	}
	
	public LevelOneAddResponse updateLevelOneStageMaster(String levelOneId,String stageName, String sessions, String uutIds,
			String userType) {
		LevelOneAddResponse levelOneResponseDto = new LevelOneAddResponse();

		try {
			LevelOneMasterService levelOne = new LevelOneMasterService();
			StageMasterLevelResponse serviceResponse = levelOne.updateLevelOneStageMaster(levelOneId,stageName, sessions, uutIds, userType);
			levelOneResponseDto.setResponse(serviceResponse.getResponse());

			if (serviceResponse.getResponse().getResponseCode() == 0) {
				levelOneResponseDto.setLevelOneResponse(null);
				return levelOneResponseDto;
			}
			LevelOneResponseDto levelOneEntity = (LevelOneResponseDto) serviceResponse.getStageLevelMaster();
			LevelOneDto levelOneDto = new LevelOneDto();
			levelOneDto.setLevelOneId(levelOneEntity.getLevelOneId());
			levelOneDto.setStageName(levelOneEntity.getStageName());
			levelOneDto.setNextLevel(levelOneEntity.getNextLevel());
			levelOneDto.setSessionIds(levelOneEntity.getSessionIds());
			levelOneDto.setUutId(levelOneEntity.getUutId());

			levelOneResponseDto.setLevelOneResponse(levelOneDto);

			return levelOneResponseDto;

		} catch (Exception e) {
			Response res = new Response();
			res.setResponseCode(0);
			res.setResponseMessage("Add Stage Unsuccessfull : " + e.getLocalizedMessage());
			levelOneResponseDto.setResponse(res);
			levelOneResponseDto.setLevelOneResponse(null);
			return levelOneResponseDto;
		}
	}
	 public Response addTestFilesToStage(List<String> fileIds, String stageLevel) {
			Response response = new Response();
			try {
				TestFilesStagesMappingService testFilesStagesMappingService = new TestFilesStagesMappingService();
				response = testFilesStagesMappingService.addTestFilesStagesMapping(stageLevel, fileIds);
			} catch (Exception ex) {
				response.setResponseCode(0);
				response.setResponseMessage("Error" + ex.getLocalizedMessage());
			}
			return response;
		}
	    
		public StagesFilesResponseDTO getTestFilesByStageLevel(String stageLevel) {
			StagesFilesResponseDTO response = new StagesFilesResponseDTO();
			try {
				TestFilesStagesMappingService testFilesStagesMappingService = new TestFilesStagesMappingService();
				GetResponse res = testFilesStagesMappingService.getTestFilesStagesMappingByLastLevelReference(stageLevel);
				List<TestFilesStagesMapping> lst = new ArrayList<TestFilesStagesMapping>();
				lst = (List<TestFilesStagesMapping>) res.getResponseList();
				TestFileService testFileService = new TestFileService();
			//	GetResponse res1 = testFileService.getAllTestFiles();
				List<TestFile> lstTestFiles = new ArrayList<TestFile>();
				lstTestFiles= testFileService.getAllTestFiles();
				Map<String, String> testFileIdName = new HashMap<String, String>();
				Map<String, String> testFileIdPathId = new HashMap<String, String>();
				for (TestFile testFiles : lstTestFiles) {
					testFileIdName.put(testFiles.getTestFileId(), testFiles.getTestFileName());
				
				}
				List<StagesFilesDTO> listStagesFiles = new ArrayList<StagesFilesDTO>();
				for (TestFilesStagesMapping testFilesStagesMapping : lst) {
					StagesFilesDTO stagesFilesDTO = new StagesFilesDTO();
					stagesFilesDTO.setTestFileId(testFilesStagesMapping.getTestFileId());
					stagesFilesDTO.setLevelStage(testFilesStagesMapping.getStageLevel());
					stagesFilesDTO.setPathMasterId(testFileIdPathId.get(testFilesStagesMapping.getTestFileId()));
					listStagesFiles.add(stagesFilesDTO);
				}
				response.setResponseList(listStagesFiles);
				response.setCode(11);
				response.setMsg("Fetched");
			} catch (Exception ex) {
				response.setCode(0);
				response.setMsg("Error Not Fetched");
				response.setEmsg(ex.getLocalizedMessage());
			}
			return response;
		}
		public List<SessionMasterDTO> getSessionMasterList()
		{
			List<SessionMasterDTO>lst = new ArrayList();
			try
			{
				SessionMasterService sessionMasterService = new SessionMasterService();
				GetResponse res = sessionMasterService.getAllSessionMaster();
				List<SessionMaster> sessionList = new ArrayList();
				sessionList = (List<SessionMaster>) res.getResponseList();
				System.out.println("%%%%%%%%%"+sessionList.size());
				for(SessionMaster sessionMaster:sessionList)
				{
					SessionMasterDTO sessionMasterDTO = new SessionMasterDTO();
					sessionMasterDTO.setSessionMasterId(sessionMaster.getSessionMasterId());
					sessionMasterDTO.setSessionTypeName(sessionMaster.getSessionTypeName());
					lst.add(sessionMasterDTO);
					
				}
				
			}catch(Exception ex)
			{
				System.out.println("Error" +ex.getLocalizedMessage());
			}
			return lst;
		}
		
	//Delete Stages Mapping.
	public Response deleteStagesMapping(int testFileMappingId)
	{
		Response res = new Response();
		try
		{
			TestFilesStagesMappingService testMappingService = new TestFilesStagesMappingService();
			res = testMappingService.deleteTestFileFromStages(testFileMappingId);
		}
		catch(Exception ex)
		{
			res.setResponseCode(0);
			res.setResponseMessage("Not Deleted");
		}
		return res;
	}
	


}
