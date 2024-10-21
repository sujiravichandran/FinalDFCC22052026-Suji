package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.GetObjResponse;
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

	private static String configFilePath = "C:\\Users\\anujk\\Downloads\\dfccConfig.txt";
	private Thread dfccCheckStatusThread;

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

	public Response getDfccCheckStatus() {
		Response response = new Response();
		File configFile = new File(configFilePath);

		// Check if the file exists
		if (!configFile.exists()) {
			System.err.println("Config file not found at " + configFilePath);
			response.setResponseCode(0);
			response.setResponseMessage("Config file not found at " + configFilePath);

		} else {
			Debug.printDebug("Config file found at " + configFilePath);
			response.setResponseCode(1);
			response.setResponseMessage("Config file found " + configFilePath);
			parseConfigFile(configFilePath);

		}

		// more logic to implement
		dfccCheckStatusThread = new Thread(() -> {

			// dfccPowerOn
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getDfccPowerOnCommand());

			// dfccPowerOff
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getDfccPowerOffCommand());

			// onlineStatus
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getOnlineStatusCommand());

			// mk1ScTemperature
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getMk1ScTemperatureCommand());

			// mk1AecTemperature
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getMk1AecTemperatureCommand());

		});
		dfccCheckStatusThread.start();

		return response;

	}
	// READ FROM SYSTEM CONFIG FILE.
	public static void parseConfigFile(String configFilePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
			String line;
			while ((line = br.readLine()) != null) {

				if (line.startsWith("#dfccPowerOn:")) {
					dfccCheckStatus.setDfccPowerOnCommand(line.split(":", 2)[1].trim());
					Debug.printDebug(dfccCheckStatus.getDfccPowerOnCommand());
				} else if (line.startsWith("#dfccPowerOff:")) {
					dfccCheckStatus.setDfccPowerOffCommand(line.split(":", 2)[1].trim());
					Debug.printDebug(dfccCheckStatus.getDfccPowerOffCommand());
				} else if (line.startsWith("#onlineStatus:")) {
					dfccCheckStatus.setOnlineStatusCommand(line.split(":", 2)[1].trim());
					Debug.printDebug(dfccCheckStatus.getOnlineStatusCommand());
				} else if (line.startsWith("#mk1 SC temperature:")) {
					dfccCheckStatus.setMk1ScTemperatureCommand(line.split(":", 2)[1].trim());
					Debug.printDebug(dfccCheckStatus.getMk1ScTemperatureCommand());
				} else if (line.startsWith("#mk1 AEC temperature:")) {
					dfccCheckStatus.setMk1AecTemperatureCommand(line.split(":", 2)[1].trim());
					Debug.printDebug(dfccCheckStatus.getMk1AecTemperatureCommand());
				} else if (line.startsWith("#mk1a temperature:")) {
					dfccCheckStatus.setMk1aTemperatureCommand(line.split(":", 2)[1].trim());
					Debug.printDebug(dfccCheckStatus.getMk1aTemperatureCommand());
				} else if (line.startsWith("#mk2 temperature:")) {
					dfccCheckStatus.setMk2TemperatureCommand(line.split(":", 2)[1].trim());
					Debug.printDebug(dfccCheckStatus.getMk2TemperatureCommand());
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					Debug.printDebug(dfccCheckStatus.getMk1ScTemperatureCommand());
					break;
				case "mk1 AEC temperature":
					dfccCheckStatus.setMk1AecTemperatureCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getMk1AecTemperatureCommand());
					break;
				case "mk1a temperature":
					dfccCheckStatus.setMk1aTemperatureCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getMk1aTemperatureCommand());
					break;
				case "mk2 temperature":
					dfccCheckStatus.setMk2TemperatureCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getMk2TemperatureCommand());
					break;

				case "OFPversion":
					dfccCheckStatus.setOfpVersionStatusCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getOfpVersionStatusCommand());
					break;
				case "WDMversion":
					dfccCheckStatus.setWdmStatusCommand(dfccStatusCommand.getCommand());
					Debug.printDebug(dfccCheckStatus.getWdmStatusCommand());
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

	// UPDATE DFCC STATUS COMMAND
	public static void updateDfccCommand(int id, String command) {
		try {

			DfccStatusCommandService commandService = new DfccStatusCommandService();
			DfccStatusCommand dfccStatusCommand = new DfccStatusCommand();
			dfccStatusCommand.setCommand(command);
//			dfccStatusCommand.setName(name);
			dfccStatusCommand.setId(id);

			GetObjResponse getObjResponse = commandService.adddfccStatusCommand(dfccStatusCommand);
			DfccStatusCommand responseObj = (DfccStatusCommand) getObjResponse.getObject();

			String name = responseObj.getName();

			switch (name) {

			case "dfccPowerOn":
				dfccCheckStatus.setDfccPowerOnCommand(responseObj.getCommand());
				Debug.printDebug(dfccCheckStatus.getDfccPowerOnCommand());
				break;

			case "dfccPowerOff":
				dfccCheckStatus.setDfccPowerOffCommand(responseObj.getCommand());
				Debug.printDebug(dfccCheckStatus.getDfccPowerOffCommand());
				break;
			case "onlineStatus":
				dfccCheckStatus.setOnlineStatusCommand(responseObj.getCommand());
				Debug.printDebug(dfccCheckStatus.getOnlineStatusCommand());
				break;
			case "mk1 SC temperature":
				dfccCheckStatus.setMk1ScTemperatureCommand(responseObj.getCommand());
				Debug.printDebug(dfccCheckStatus.getMk1ScTemperatureCommand());
				break;
			case "mk1 AEC temperature":
				dfccCheckStatus.setMk1AecTemperatureCommand(responseObj.getCommand());
				Debug.printDebug(dfccCheckStatus.getMk1AecTemperatureCommand());
				break;
			case "mk1a temperature":
				dfccCheckStatus.setMk1aTemperatureCommand(responseObj.getCommand());
				Debug.printDebug(dfccCheckStatus.getMk1aTemperatureCommand());
				break;
			case "mk2 temperature":
				dfccCheckStatus.setMk2TemperatureCommand(responseObj.getCommand());
				Debug.printDebug(dfccCheckStatus.getMk2TemperatureCommand());
				break;
			case "OFPversion":
				dfccCheckStatus.setOfpVersionStatusCommand(dfccStatusCommand.getCommand());
				Debug.printDebug(dfccCheckStatus.getOfpVersionStatusCommand());
				break;
			case "WDMversion":
				dfccCheckStatus.setWdmStatusCommand(dfccStatusCommand.getCommand());
				Debug.printDebug(dfccCheckStatus.getWdmStatusCommand());
				break;
			default: Debug.printDebug("Invalid Name ");
				break;

			}

//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
