package com.teclever.dfcc.datastore.terminalmanagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OFPversionStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.WDMStatus;
import com.teclever.dfcc.utils.Debug;

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

					Debug.printDebug("State Machine CH1 Online Status:: " + channel1);
					Debug.printDebug("State Machine CH2 Online Status:: " + channel2);
					Debug.printDebug("State Machine CH3 Online Status:: " + channel3);
					Debug.printDebug("State Machine CH4 Online Status:: " + channel4);
					OnlineStatus.setChannel1Status(channel1);
					OnlineStatus.setChannel2Status(channel2);
					OnlineStatus.setChannel3Status(channel3);
					OnlineStatus.setChannel4Status(channel4);
					return new ChannelStatus(channel1, channel2, channel3, channel4);

				} else {
					Debug.printDebug("Invalid format: Expected 4 values.");
				}
			} else {
				Debug.printDebug("Invalid format: Missing values part.");
			}
		} else {
			Debug.printDebug("LINE NOT FOUND");
		}
		return null;

	}
	 
	 
	public ChannelStatus getOFPversionStatus(String outputLine) {
		if (outputLine.contains("0x9fffb")) {
			String[] parts = outputLine.split(">");

			if (parts.length > 1) {
				String valuesPart = parts[1].trim();
				valuesPart = valuesPart.substring(1, valuesPart.length() - 1);
				String[] values = valuesPart.split(",");

				if (values.length == 4) {
					String written1 = values[0].trim();
					String written2 = values[1].trim();
					String written3 = values[2].trim();
					String written4 = values[3].trim();

					Debug.printDebug("StateMachine OFP CH1 version :: " + written1);
					Debug.printDebug("StateMachine OFP CH2 version :: " + written2);
					Debug.printDebug("StateMachine OFP CH3 version :: " + written3);
					Debug.printDebug("StateMachine OFP CH4 version :: " + written4);
					OFPversionStatus.setChannel1Status(written1);
					OFPversionStatus.setChannel2Status(written2);
					OFPversionStatus.setChannel3Status(written3);
					OFPversionStatus.setChannel4Status(written4);

					return new ChannelStatus(written1, written2, written3, written4);

				} else {
					Debug.printDebug("Invalid format: Expected 4 values.");
				}
			} else {
				Debug.printDebug("Invalid format: Missing values part.");
			}
		} else {
			Debug.printDebug("LINE NOT FOUND");
		}
		return null;

	}
	 
	 
	 
	 
	public ChannelStatus getWDMStatus(String outputLine) {
		Pattern channelPattern = Pattern.compile("<.*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)");
        Matcher channelMatcher = channelPattern.matcher(outputLine);
        
        if (channelMatcher.find()) {
            String channel1 = channelMatcher.group(1);
            String channel2 = channelMatcher.group(2);
            String channel3 = channelMatcher.group(3);
            String channel4 = channelMatcher.group(4);
            
            
            Debug.printDebug("StateMachine WDM status CH1 :: " + channel1);
            Debug.printDebug("StateMachine WDM status CH2 :: " + channel2);
            Debug.printDebug("StateMachine WDM status CH3 :: " + channel3);
            Debug.printDebug("StateMachine WDM status CH4 :: " + channel4);
            WDMStatus.setChannel1Status(channel1);
            WDMStatus.setChannel2Status(channel2);
            WDMStatus.setChannel3Status(channel3);
            WDMStatus.setChannel4Status(channel4);

                     
            return new ChannelStatus(channel1, channel2, channel3, channel4);
        } else {
        	return null;
        }
       
    }
		
}
