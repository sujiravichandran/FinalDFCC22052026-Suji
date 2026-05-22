package com.teclever.dfcc.datastore.filemanagement;

import java.util.ArrayList;
import java.util.List;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.DfccStatusCheck;
import com.teclever.datastore.entities.DfccStatusCommand;
import com.teclever.datastore.response.DfccStatusCheckResponse;
import com.teclever.datastore.service.DfccStatusCheckService;
import com.teclever.datastore.service.DfccStatusCommandService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.DfccStatusCheckDto;
import com.teclever.dfcc.datastore.dto.DfccStatusCommandDto;
import com.teclever.dfcc.datastore.dto.DfccStatusCommandResponse;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;
import com.teclever.dfcc.utils.Debug;

public class Aitess2ConfigManagement {

	AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();

	// API:: ADDING DFCC CHECK STATUS
	public DfccStatusCheckResponse addDfccStatusCheck(DfccStatusCheckDto dfccStatusCheckDto, int loginSessionId) {
		DfccStatusCheckService dfccStatusCheckService = new DfccStatusCheckService();

		DfccStatusCheck dfccStatusCheck = new DfccStatusCheck();
		dfccStatusCheck.setId(dfccStatusCheckDto.getId());
		dfccStatusCheck.setLoginSessionId(dfccStatusCheckDto.getLoginSessionId());
		dfccStatusCheck.setUutId(dfccStatusCheckDto.getUutId());
		dfccStatusCheck.setBoardName(dfccStatusCheckDto.getBoardName());
		dfccStatusCheck.setData(dfccStatusCheckDto.getData());
		dfccStatusCheck.setChannel1(dfccStatusCheckDto.getChannel1());
		dfccStatusCheck.setChannel2(dfccStatusCheckDto.getChannel2());
		dfccStatusCheck.setChannel3(dfccStatusCheckDto.getChannel3());
		dfccStatusCheck.setChannel4(dfccStatusCheckDto.getChannel4());

		DfccStatusCheckResponse serviceResponse = dfccStatusCheckService.addDfccStatusCheck(dfccStatusCheck,
				loginSessionId);
		return serviceResponse;
	}

	
	
	//FETCH ALL DFCC STATUS COMMAND
	public DfccStatusCommandResponse getAllDfccStatusCommand() {
		DfccStatusCommandResponse dfccStatusCommandResponse = new DfccStatusCommandResponse();
		try {

			DfccStatusCommandService commandService = new DfccStatusCommandService();
			GetResponse getResponse = commandService.getAllDfccStatusCommand();
			List<?> responseList = getResponse.getResponseList();
			String name;
			List<DfccStatusCommandDto> listOfDfccStatusCommandDto = new ArrayList<>();
			for (Object obj : responseList) {

				DfccStatusCommand dfccStatusCommand = (DfccStatusCommand) obj;
				name = dfccStatusCommand.getName();
				switch (name) {

				case "dfccPowerOn":
					dfccCheckStatus.setDfccPowerOnCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getDfccPowerOnCommand());
					break;

				case "dfccPowerOff":
					dfccCheckStatus.setDfccPowerOffCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getDfccPowerOffCommand());
					break;
				case "onlineStatus":
					dfccCheckStatus.setOnlineStatusCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getOnlineStatusCommand());
					break;
				case "mk1 SC temperature":
					dfccCheckStatus.setMk1ScTemperatureCommand(dfccStatusCommand.getCommand());
					break;
				case "mk1 AEC temperature":
					dfccCheckStatus.setMk1AecTemperatureCommand(dfccStatusCommand.getCommand());
					break;
				case "sc temperature":
					dfccCheckStatus.setScTemperatureCommand(dfccStatusCommand.getCommand());
					break;
				case "aec temperature":
					dfccCheckStatus.setAecTemperatureCommand(dfccStatusCommand.getCommand());
					break;

				case "OFPversion":
					dfccCheckStatus.setOfpVersionStatusCommandMk1(dfccStatusCommand.getCommand());
//					System.out.println("Suji Aitess 2 Config Check OFP Command::" + dfccStatusCommand.getCommand());
//					System.out.println("Suji Aitess 2 Config Check OFP Command 6666::" + dfccCheckStatus.getOfpVersionStatusCommandMk1());
					Debug.printDebug(dfccCheckStatus.getOfpVersionStatusCommandMk1());
					break;
				case "OFPversions":
					dfccCheckStatus.setOfpVersionStatusCommandMk1A(dfccStatusCommand.getCommand());
					////System.out.println("Check OFP Command::MK1a" + dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getOfpVersionStatusCommandMk1A());
					break;
					
				case "WDMversion":
					dfccCheckStatus.setWdmStatusCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getWdmStatusCommand());
					break;
				case "dfccPowerOnStatus":
					dfccCheckStatus.setDfccPowerOnStatus(dfccStatusCommand.getCommand());
					break;
					
				default: Debug.printDebug("Invalid Name ");
					break;
				}

				DfccStatusCommandDto dfccStatusCommandDto = new DfccStatusCommandDto();
				dfccStatusCommandDto.setId(dfccStatusCommand.getId());
				dfccStatusCommandDto.setCommand(dfccStatusCommand.getCommand());
				dfccStatusCommandDto.setName(dfccStatusCommand.getName());
				listOfDfccStatusCommandDto.add(dfccStatusCommandDto);
			}
			Response res = new Response();
			res.setResponseCode(1);
			res.setResponseMessage("Success");
			dfccStatusCommandResponse.setResponse(res);
			dfccStatusCommandResponse.setListOfDfccStatusCommandDto(listOfDfccStatusCommandDto);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dfccStatusCommandResponse;
	}

	
}
