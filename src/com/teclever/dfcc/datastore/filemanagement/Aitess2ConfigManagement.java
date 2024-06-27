package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.DfccStatusCheck;
import com.teclever.datastore.response.DfccStatusCheckResponse;
import com.teclever.datastore.service.DfccStatusCheckService;
import com.teclever.dfcc.datastore.dto.DfccStatusCheckDto;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;

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
			System.out.println("Config file found at " + configFilePath);
			response.setResponseCode(1);
			response.setResponseMessage("Config file found " + configFilePath);
			parseConfigFile(configFilePath);

		}

		//more logic to implement
		dfccCheckStatusThread = new Thread(() -> {
						
			//dfccPowerOn
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getDfccPowerOnCommand());
			
			//dfccPowerOff
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getDfccPowerOffCommand());

			//onlineStatus
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getOnlineStatusCommand());

			//mk1ScTemperature
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getMk1ScTemperatureCommand());

			//mk1AecTemperature
			aitessProcessControlManagement.WriteAitess2Command(dfccCheckStatus.getMk1AecTemperatureCommand());

			
			
		});
		dfccCheckStatusThread.start();
		
		return response;

	}

	public  static void parseConfigFile(String configFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("#dfccPowerOn:")) {
                    dfccCheckStatus.setDfccPowerOnCommand(line.split(":", 2)[1].trim());
                    System.out.println(dfccCheckStatus.getDfccPowerOnCommand());
                } else if (line.startsWith("#dfccPowerOff:")) {
                    dfccCheckStatus.setDfccPowerOffCommand(line.split(":", 2)[1].trim());
                    System.out.println(dfccCheckStatus.getDfccPowerOffCommand());
                } else if (line.startsWith("#onlineStatus:")) {
                    dfccCheckStatus.setOnlineStatusCommand(line.split(":", 2)[1].trim());
                    System.out.println(dfccCheckStatus.getOnlineStatusCommand());
                } else if (line.startsWith("#mk1 SC temperature:")) {
                    dfccCheckStatus.setMk1ScTemperatureCommand(line.split(":", 2)[1].trim());
                    System.out.println(dfccCheckStatus.getMk1ScTemperatureCommand());
                } else if (line.startsWith("#mk1 AEC temperature:")) {
                    dfccCheckStatus.setMk1AecTemperatureCommand(line.split(":", 2)[1].trim());
                    System.out.println(dfccCheckStatus.getMk1AecTemperatureCommand());
                }else if (line.startsWith("#mk1a temperature:")) {
                    dfccCheckStatus.setMk1aTemperatureCommand(line.split(":", 2)[1].trim());
                    System.out.println(dfccCheckStatus.getMk1aTemperatureCommand());
                }else if (line.startsWith("#mk2 temperature:")) {
                    dfccCheckStatus.setMk2TemperatureCommand(line.split(":", 2)[1].trim());
                    System.out.println(dfccCheckStatus.getMk2TemperatureCommand());
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
