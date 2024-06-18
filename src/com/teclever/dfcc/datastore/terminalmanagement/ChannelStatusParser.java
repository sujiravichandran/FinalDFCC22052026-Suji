package com.teclever.dfcc.datastore.terminalmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.datastore.dto.ChannelStatusResponse;

public class ChannelStatusParser {
	
	public static ChannelStatusResponse parseChannelStatus(String filePath) {
		ChannelStatusResponse channelStatusResponse = new ChannelStatusResponse();
		List<ChannelStatus> channelStatusList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            //List<ChannelStatus> currentChannelStatus = null;
            while ((line = reader.readLine()) != null) {
                String regex = "< 0xdfcc0000> \\(\\s*(\\w+),\\s*(\\w+),\\s*(\\w+),\\s*(\\w+)\\)";

                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                	channelStatusList.add(new ChannelStatus("Channel1", matcher.group(1)));
                	channelStatusList.add(new ChannelStatus("Channel2", matcher.group(2)));
                	channelStatusList.add(new ChannelStatus("Channel3", matcher.group(3)));
                	channelStatusList.add(new ChannelStatus("Channel4", matcher.group(4)));
                }
            }
            
            if(channelStatusList.isEmpty()) {
            	channelStatusResponse.setResponseMsg("No channel status found");
            	channelStatusResponse.setResponseCode(0);
            }else
            {
            	channelStatusResponse.setResponseMsg("SUCCESS");
            	channelStatusResponse.setResponseCode(1);
            	channelStatusResponse.setChannelStatus(channelStatusList);
            }
            
            
        } catch (IOException e) {
        	channelStatusResponse.setResponseMsg("FAILED");
        	channelStatusResponse.setResponseCode(0);
        }
    
        return channelStatusResponse;
    }

}
