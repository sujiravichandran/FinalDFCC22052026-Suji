package com.teclever.dfcc.datastore.terminalmanagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OFPversionStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.WDMStatus;
import com.teclever.dfcc.utils.Debug;

public class ChannelStatusParser {

	// WDMstatus
	public ChannelStatus getWDMStatus(String outputLine) {
		Pattern channelPattern = Pattern.compile("<.*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)");
		Matcher channelMatcher = channelPattern.matcher(outputLine);

		if (channelMatcher.find()) {
			String channel1 = channelMatcher.group(1).trim();
			String channel2 = channelMatcher.group(2).trim();
			String channel3 = channelMatcher.group(3).trim();
			String channel4 = channelMatcher.group(4).trim();

			WDMStatus.setChannel1Status(channel1);
			WDMStatus.setChannel2Status(channel2);
			WDMStatus.setChannel3Status(channel3);
			WDMStatus.setChannel4Status(channel4);

			// Check if channels are not "offline" and set OnlineStatus
			if (!channel1.trim().equalsIgnoreCase("offline")) {
				OnlineStatus.setChannel1Status("online");
			} else {
				OnlineStatus.setChannel1Status("offline");
			}

			if (!channel2.trim().equalsIgnoreCase("offline")) {
				OnlineStatus.setChannel2Status("online");
			} else {
				OnlineStatus.setChannel2Status("offline");
			}

			if (!channel3.trim().equalsIgnoreCase("offline")) {
				OnlineStatus.setChannel3Status("online");
			} else {
				OnlineStatus.setChannel3Status("offline");
			}

			if (!channel4.trim().equalsIgnoreCase("offline")) {
				OnlineStatus.setChannel4Status("online");
			} else {
				OnlineStatus.setChannel4Status("offline");
			}

			return new ChannelStatus(channel1, channel2, channel3, channel4);
		} else {
			return null;
		}

	}
	
	//OFP for all
	public ChannelStatus getOFPstatus(String outputLine) {
		Pattern channelPattern = Pattern.compile("<.*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)");
		Matcher channelMatcher = channelPattern.matcher(outputLine);

		if (channelMatcher.find()) {
			String channel1 = channelMatcher.group(1).trim();
			String channel2 = channelMatcher.group(2).trim();
			String channel3 = channelMatcher.group(3).trim();
			String channel4 = channelMatcher.group(4).trim();
			
			OFPversionStatus.setChannel1Status(channel1);
			OFPversionStatus.setChannel2Status(channel2);
			OFPversionStatus.setChannel3Status(channel3);
			OFPversionStatus.setChannel4Status(channel4);
			
			return new ChannelStatus(channel1, channel2, channel3, channel4);
		} else {
			return null;
		}

	}
	
	

	// OFPversion
	public ChannelStatus getOFPversionStatus(String outputLine) {
		if (outputLine.contains("0x9fff8")) {
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
		}else if (outputLine.contains("0x203ffff8")) {
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
		
		}
		else {
			Debug.printDebug("LINE NOT FOUND");
		}
		return null;

	}

}
