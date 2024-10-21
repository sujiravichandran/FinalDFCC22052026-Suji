package com.teclever.dfcc.datastore.customtestmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.StageLevelResponse;
import com.teclever.datastore.dto.SubLevelResponseDto;
import com.teclever.datastore.entities.LevelFourStageMaster;
import com.teclever.datastore.entities.LevelOneStageMaster;
import com.teclever.datastore.entities.LevelThreeStageMaster;
import com.teclever.datastore.entities.LevelTwoStageMaster;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.dfcc.utils.Debug;

public class AdvanceInterfaceTestingManagement {

	public boolean checkIsStageFromLRU(String parentId) {
		boolean notFromLRU = true;
		try {
			LevelThreeService levelThreeService = new LevelThreeService();
			LevelThreeStageMaster levelThreeStageMaster = levelThreeService.getLevelThreeMasterByLevelId(parentId);
			if (levelThreeStageMaster != null) {
				LevelTwoMasterService levelTwoMasterService = new LevelTwoMasterService();
				LevelTwoStageMaster levelTwoStageMaster = levelTwoMasterService
						.getLevelTwoMasterByLevelId(levelThreeStageMaster.getLevelTwoRefernce());
				if (levelTwoStageMaster != null && levelTwoStageMaster.getStageName().equalsIgnoreCase("SRU Test")) {
					LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
					LevelOneStageMaster levelOneStageMaster = levelOneMasterService
							.getLevelOneMasterByLevelId(levelTwoStageMaster.getLevelOneRefernce());
					if (!levelOneStageMaster.getStageName().equals("LRU Test")) {
						return false;
					}
				} else {
					return false;
				}
			}

		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
			return false;
		}
		return notFromLRU;
	}
	
	
	public Map<String, String> getAdvanceStageNameStageId(String uutId) {
		Map<String, String> advanceStageNameStageId = new HashMap<String, String>();
		try {
			List<LevelFourStageMaster> advanceFinalStages = new ArrayList<LevelFourStageMaster>();
			advanceFinalStages = getadvanceLevel4Stages(uutId);
			if (advanceFinalStages.size() > 0) {
				for (LevelFourStageMaster levelFourStageMaster : advanceFinalStages) {
					advanceStageNameStageId.put(levelFourStageMaster.getStageName(),
							levelFourStageMaster.getLevelFourStageId());
				}
			}
		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return advanceStageNameStageId;
	}

	public List<LevelFourStageMaster> getadvanceLevel4Stages(String uutId) {
		List<LevelFourStageMaster> level4StageMasterList = new ArrayList<LevelFourStageMaster>();
		try {
			LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
			List<SubLevelResponseDto> level2StageMasterList = new ArrayList<SubLevelResponseDto>();
			LevelTwoMasterService levelTwoMasterService = new LevelTwoMasterService();
			List<LevelThreeStageMaster> level3StageMasterList = new ArrayList<LevelThreeStageMaster>();
			LevelThreeService levelThreeService = new LevelThreeService();
			LevelFourMasterSevice level4Service = new LevelFourMasterSevice();
			LevelOneStageMaster advanceLevelOneMaster = levelOneMasterService.getAdvanceLevelOneObjectByUutId(uutId);
			if (advanceLevelOneMaster != null) {
				StageLevelResponse stageLevelReponse = levelTwoMasterService
						.getLevelTwoMasterByLevleOneId(advanceLevelOneMaster.getLevelOneStageId());
				level2StageMasterList = (List<SubLevelResponseDto>) stageLevelReponse.getStageLevelList();
			}
			if (level2StageMasterList.size() > 0) {
				Map<String, List<LevelThreeStageMaster>> levelTwoIdListof3Obj = new HashMap<String, List<LevelThreeStageMaster>>();
				levelTwoIdListof3Obj = levelThreeService.getListOfEntityWithParentId();

				Debug.printDebug(String.valueOf(level2StageMasterList.size()));

				for (SubLevelResponseDto l2Master : level2StageMasterList) {
					Debug.printDebug("L2 Master Id" + l2Master.getLevelId());

					List<LevelThreeStageMaster> level3Masters = new ArrayList<LevelThreeStageMaster>();
					level3Masters=		levelTwoIdListof3Obj.get(l2Master.getLevelId());
				//	Debug.printDebug(level3Masters.size());
					if (level3Masters !=null) {
						level3StageMasterList.addAll(level3Masters);
					}
				}

			}

			if (level3StageMasterList.size() > 0) {

				Map<String, List<LevelFourStageMaster>> level3IdListof4Obj = new HashMap<String, List<LevelFourStageMaster>>();
				level3IdListof4Obj = level4Service.getListOfEntityWithParentId();
				for (LevelThreeStageMaster l3Master : level3StageMasterList) {
					List<LevelFourStageMaster> level4Masters = level3IdListof4Obj.get(l3Master.getLevelThreeStageId());
					if (level4Masters!=null) {
						level4StageMasterList.addAll(level4Masters);
					}
				}

			}
		} catch (Exception ex) {
			Debug.printDebug(ex.getLocalizedMessage());
		}
		return level4StageMasterList;
	}
	
	

}