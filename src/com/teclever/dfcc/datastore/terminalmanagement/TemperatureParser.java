package com.teclever.dfcc.datastore.terminalmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.TemperatureResponse;

public class TemperatureParser {
	
	//MK1
	public static TemperatureResponse parseFile1(String filePath) {
		TemperatureResponse temperatureResponse = new TemperatureResponse();
		List<ChannelTemperature> temperatureList = new ArrayList<>();


		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			Pattern pattern = Pattern.compile("<\\s*\\d+>\\s*\\(([^,]+),([^,]+),([^,]+),([^\\)]+)\\)\\s*DEGC");


			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					temperatureList.add(new ChannelTemperature("Channel1", matcher.group(1).trim()));
					temperatureList.add(new ChannelTemperature("Channel2", matcher.group(2).trim()));
					temperatureList.add(new ChannelTemperature("Channel3", matcher.group(3).trim()));
					temperatureList.add(new ChannelTemperature("Channel4", matcher.group(4).trim()));
				}
			}

			if (temperatureList.isEmpty()) {
				temperatureResponse.setResponseMsg("No temperature data found in the file.");
				temperatureResponse.setResponseCode(0);
			} else {
				temperatureResponse.setResponseMsg("SUCCESS");
				temperatureResponse.setResponseCode(1);
				temperatureResponse.setTemperatures(temperatureList);
			}
		} catch (IOException e) {
			temperatureResponse.setResponseMsg("FAILED");
			temperatureResponse.setResponseCode(0);
		}

		return temperatureResponse;
	}
	
	
	
	//Mk1a Mk2
	public static TemperatureResponse parseFile(String filePath) {
        TemperatureResponse temperatureResponse = new TemperatureResponse();
        Map<String, List<ChannelTemperature>> boardTemperatureMap = new HashMap<>();
        List<ChannelTemperature> allTemperatures = new ArrayList<>();
        List<String> boardNames = new ArrayList<>();
        Pattern boardNamePattern = Pattern.compile("!\\s*([\\w-]+)\\s*$");
        Pattern temperaturePattern = Pattern.compile("R>\\s*\\(\\s*([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^\\)]+)\\)\\s*DEGC");

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = boardNamePattern.matcher(line);
                if (matcher.find()) {
                    String boardName = matcher.group(1).trim();
                    boardNames.add(boardName);
                }
            }

            reader.close();
            reader = new BufferedReader(new FileReader(filePath));

            boolean foundBoardNames = !boardNames.isEmpty();
            String currentBoardName = foundBoardNames ? boardNames.remove(0) : null;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = temperaturePattern.matcher(line);
                if (matcher.find()) {
                    if (currentBoardName == null) {
                        throw new IOException("No board names found before temperature data.");
                    }

                    List<ChannelTemperature> currentTemperatureList = new ArrayList<>();
                    ChannelTemperature channel1 = new ChannelTemperature("Channel1", matcher.group(1).trim());
                    ChannelTemperature channel2 = new ChannelTemperature("Channel2", matcher.group(2).trim());
                    ChannelTemperature channel3 = new ChannelTemperature("Channel3", matcher.group(3).trim());
                    ChannelTemperature channel4 = new ChannelTemperature("Channel4", matcher.group(4).trim());
                    
                    currentTemperatureList.add(channel1);
                    currentTemperatureList.add(channel2);
                    currentTemperatureList.add(channel3);
                    currentTemperatureList.add(channel4);

                    allTemperatures.add(channel1);
                    allTemperatures.add(channel2);
                    allTemperatures.add(channel3);
                    allTemperatures.add(channel4);

                    boardTemperatureMap.put(currentBoardName, currentTemperatureList);

                    // Move to the next board name
                    currentBoardName = boardNames.isEmpty() ? null : boardNames.remove(0);
                }
            }

            if (boardTemperatureMap.isEmpty()) {
                temperatureResponse.setResponseMsg("No temperature data found in the file.");
                temperatureResponse.setResponseCode(0);
            } else {
                temperatureResponse.setResponseMsg("SUCCESS");
                temperatureResponse.setResponseCode(1);
                temperatureResponse.setTemperatures(allTemperatures);
                temperatureResponse.setBoardTemperatureMap(boardTemperatureMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
            temperatureResponse.setResponseMsg("File reading failed: " + e.getMessage());
            temperatureResponse.setResponseCode(0);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return temperatureResponse;
    }
	
}
