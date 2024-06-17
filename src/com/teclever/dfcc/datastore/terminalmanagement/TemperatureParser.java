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

		List<List<ChannelTemperature>> allTemperatureLists = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			Pattern pattern = Pattern.compile("<\\s*\\d+>\\s*\\(([^,]+),([^,]+),([^,]+),([^\\)]+)\\)\\s*DEGC");

			List<ChannelTemperature> currentTemperatureList = null;

			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					currentTemperatureList = new ArrayList<>();
					currentTemperatureList.add(new ChannelTemperature("Channel1", matcher.group(1).trim()));
					currentTemperatureList.add(new ChannelTemperature("Channel2", matcher.group(2).trim()));
					currentTemperatureList.add(new ChannelTemperature("Channel3", matcher.group(3).trim()));
					currentTemperatureList.add(new ChannelTemperature("Channel4", matcher.group(4).trim()));
					allTemperatureLists.add(currentTemperatureList);
				}
			}

			if (allTemperatureLists.isEmpty()) {
				temperatureResponse.setResponseMsg("No temperature data found in the file.");
				temperatureResponse.setResponseCode(0);
			} else {
				temperatureResponse.setResponseMsg("SUCCESS");
				temperatureResponse.setResponseCode(1);
				temperatureResponse.setTemperatures(allTemperatureLists);
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
                    currentTemperatureList.add(new ChannelTemperature("Channel1", matcher.group(1).trim()));
                    currentTemperatureList.add(new ChannelTemperature("Channel2", matcher.group(2).trim()));
                    currentTemperatureList.add(new ChannelTemperature("Channel3", matcher.group(3).trim()));
                    currentTemperatureList.add(new ChannelTemperature("Channel4", matcher.group(4).trim()));

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
