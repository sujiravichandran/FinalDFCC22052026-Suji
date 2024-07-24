package com.teclever.dfcc.datastore.terminalmanagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;

public class ChannelStatusParser {
	
	 public ChannelStatus getChannelStatus(String outputLine) {
	        Pattern channelPattern = Pattern.compile("<    0xdfcc0000> \\(\\s*(\\w+),\\s*(\\w+),\\s*(\\w+),\\s*(\\w+)\\)");
	        Matcher channelMatcher = channelPattern.matcher(outputLine);
	        
	        if (channelMatcher.find()) {
	            String channel1 = channelMatcher.group(1);
	            String channel2 = channelMatcher.group(2);
	            String channel3 = channelMatcher.group(3);
	            String channel4 = channelMatcher.group(4);
	            
				OnlineStatus.setChannel1Status(channel1);
				OnlineStatus.setChannel2Status(channel2);
				OnlineStatus.setChannel3Status(channel3);
				OnlineStatus.setChannel4Status(channel4);
	            
	            return new ChannelStatus(channel1, channel2, channel3, channel4);
	        } else {
	        	return null;
	        }
	       
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
