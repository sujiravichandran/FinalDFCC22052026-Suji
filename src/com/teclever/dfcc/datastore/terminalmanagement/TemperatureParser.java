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
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp;

import javafx.collections.ObservableMap;

public class TemperatureParser {
	
	
	//MK1
	public ChannelTemperature getChannelTemperature(String line){
		 Pattern channelTempPattern = Pattern.compile("<\\s*\\d+>\\s*\\(([^,]+),([^,]+),([^,]+),([^\\)]+)\\)\\s*DEGC");
	     Matcher channelTempMatcher = channelTempPattern.matcher(line);
		
	     if(channelTempMatcher.find()) {
	    	 String channel1Temp = channelTempMatcher.group(1);
	    	 String channel2Temp = channelTempMatcher.group(2);
	    	 String channel3Temp = channelTempMatcher.group(3);
	    	 String channel4Temp = channelTempMatcher.group(4);
	    	 
	    	 return new ChannelTemperature(channel1Temp,channel2Temp,channel3Temp,channel4Temp);
	     } else {
	    	 return null;
	     }        
	}
	

	//MK1a MK2
	public static Map<String, ChannelTemperature> parseFile(String filePath) throws IOException {
	    Map<String, ChannelTemperature> boardTemperatureMap = new HashMap<>();
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

	                ChannelTemperature channelTemperature = new ChannelTemperature(
	                    matcher.group(1).trim(),
	                    matcher.group(2).trim(),
	                    matcher.group(3).trim(),
	                    matcher.group(4).trim()
	                );

//	                boardTemperatureMap
//	                    .computeIfAbsent(currentBoardName, k -> new ArrayList<>())
//	                    .add(channelTemperature);
	                boardTemperatureMap.put(currentBoardName, channelTemperature);

	                // Move to the next board name
	                currentBoardName = boardNames.isEmpty() ? null : boardNames.remove(0);
	            }
	        }

	        if (boardTemperatureMap.isEmpty()) {
	            throw new IOException("No temperature data found in the file.");
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	        throw e;
	    } finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    boardChannelTemp.setBoardTemperatureMap((ObservableMap<String, ChannelTemperature>) boardTemperatureMap);
	    System.out.println("---->>>>  "+ boardChannelTemp.getBoardTemperatureMap());
	    return boardTemperatureMap;
	}

}
