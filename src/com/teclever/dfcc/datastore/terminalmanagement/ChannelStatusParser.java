package com.teclever.dfcc.datastore.terminalmanagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;

public class ChannelStatusParser {
	
	public ChannelStatus getChannelStatus(String outputLine) {
		if (outputLine.contains("0xdfcc0000")) {
			String[] parts = outputLine.split(">");

			if (parts.length > 1) {
				String valuesPart = parts[1].trim();
				valuesPart = valuesPart.substring(1, valuesPart.length() - 1);
				String[] values = valuesPart.split(",");

				if (values.length == 4) {
					String channel1 = values[0].trim();
					String channel2 = values[1].trim();
					String channel3 = values[2].trim();
					String channel4 = values[3].trim();

					System.out.println("State Machine Channel 1 Status:: " + channel1);
					System.out.println("State Machine Channel 2 Status:: " + channel2);
					System.out.println("State Machine Channel 3 Status:: " + channel3);
					System.out.println("State Machine Channel 4 Status:: " + channel4);
					return new ChannelStatus(channel1, channel2, channel3, channel4);

				} else {
					System.out.println("Invalid format: Expected 4 values.");
				}
			} else {
				System.out.println("Invalid format: Missing values part.");
			}
		} else {
			System.out.println("LINE NOT FOUND");
		}
		return null;

	}
	 
	 
	 public ChannelStatus getOFPversionStatus(String outputLine) {
	        Pattern channelPattern = Pattern.compile("<    0x9fffb> \\(\\s*(\\w+),\\s*(\\w+),\\s*(\\w+),\\s*(\\w+)\\)");
	        Matcher channelMatcher = channelPattern.matcher(outputLine);
	        
	        if (channelMatcher.find()) {
	            String channel1 = channelMatcher.group(1);
	            String channel2 = channelMatcher.group(2);
	            String channel3 = channelMatcher.group(3);
	            String channel4 = channelMatcher.group(4);
	            
	            return new ChannelStatus(channel1, channel2, channel3, channel4);
	        } else {
	        	return null;
	        }
	       
	    }
	 
	 
	 
	 
	 public ChannelStatus getWDMStatus(String outputLine) {
	        Pattern channelPattern = Pattern.compile("<   0x3d6028> \\(\\s*(\\w+),\\s*(\\w+),\\s*(\\w+),\\s*(\\w+)\\)");
	        Matcher channelMatcher = channelPattern.matcher(outputLine);
	        
	        if (channelMatcher.find()) {
	            String channel1 = channelMatcher.group(1);
	            String channel2 = channelMatcher.group(2);
	            String channel3 = channelMatcher.group(3);
	            String channel4 = channelMatcher.group(4);
	            
	            return new ChannelStatus(channel1, channel2, channel3, channel4);
	        } else {
	        	return null;
	        }
	       
	    }
		
}
