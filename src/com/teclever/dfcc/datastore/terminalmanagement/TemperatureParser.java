package com.teclever.dfcc.datastore.terminalmanagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;

public class TemperatureParser {
	
	
	//MK1
	public ChannelTemperature getChannelTemperature(String line){
		 Pattern channelTempPattern = Pattern.compile("<.*>\\s*\\(([^,]+),([^,]+),([^,]+),([^\\)]+)\\)\\s*DEGC");
		 //Pattern channelTempPattern = Pattern.compile("<\\s*.*\\s*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)\\s*DEGC");
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
}
