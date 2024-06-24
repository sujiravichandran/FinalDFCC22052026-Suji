package com.teclever.dfcc.resultstore.resultmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp.rdfFileParser;


public class StepParser {

    public static List<StepDto> parseStepContext(String filePath) {
    	
        List<StepDto> stepList = new ArrayList<>();
        List<StepDto> failedStepList = new ArrayList<>(); 
        String tpgph = null;
        String step = null;
        String input = null;
        List<String> readingInfo = null;
        String dStarInfo = null;
        String testPlanFile = null;
        String resultDataFile = null;
        String unit = null;
        Map<String,String> faultyChannel = null;
        String expectedValue = null;
        String signalName = null;
        boolean isAfterStep = false; // Flag to indicate whether the line is after "S> STEP"
        String specificLine = null; // To store the specific line for signalName and expectedValue

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("S>") && line.contains("TPGPH")) {
                    // Store the previous step if exists
                    if (step != null) {
                        StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile,unit,faultyChannel,expectedValue,signalName);
                        stepList.add(stepDto); // Add step to stepList

                        if (dStarInfo != null) {
                            failedStepList.add(stepDto); // Add failed step to failedStepList
                        }
                    }

                    // Reset step-related variables when encountering a new TPGPH
                    tpgph = extractTPGPH(line);
                    step = null;
                    input = null;
                    readingInfo = new ArrayList<>();
                    dStarInfo = null;
                    unit = null;
                    faultyChannel = null;
                    expectedValue = null;
                    signalName = null;
                    isAfterStep = false;

                } else if (line.startsWith("S>") && line.contains("STEP")) {
                    if (step != null) {
                        StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile,unit,faultyChannel,expectedValue,signalName);
                        stepList.add(stepDto); // Add step to stepList

                        if (dStarInfo != null) {
                            failedStepList.add(stepDto); // Add failed step to failedStepList
                        }
                    }
                    step = extractStepNumber(line);
                    input = "";
                    readingInfo = new ArrayList<>();
                    dStarInfo = null;
                    unit=null;
                    faultyChannel = null;
                    expectedValue = null;
                    signalName = null;
                    isAfterStep = true; // Set flag to true after encountering "S> STEP"
                } else if (line.startsWith("Z>") && line.contains("Test plan file")) {
                    testPlanFile = extractTestPlanFileName(line);
                } else if (line.startsWith("Z>") && line.contains("Result data file")) {
                    resultDataFile = extractResultDataFileName(line);
                }  else if (line.startsWith("S>") && isAfterStep) {
                    // Ignore S> opwait lines and continue searching for signalName and expectedValue
                    if (line.contains("opwait")) {
                        continue; // Skip this line and proceed to the next
                    }
                    // Extract signalName and expectedValue from this line
                    signalName = extractSignalName(line);
                    expectedValue = extractExpectedValue(line);
                    specificLine = line.substring(3).trim(); // Store the specific line
                    isAfterStep = false; // Reset flag after extracting values
                }
                else if (line.startsWith("D*>")) {
                    dStarInfo = line.substring(3).trim();
                    unit = extractUnit(dStarInfo);
                    faultyChannel = extractFaultyChannels(dStarInfo);
                    rdfFileParser.setDStarFound(true); //d star found update to state machine
                    rdfFileParser.incrementDStarCount();
                } else if ((line.startsWith("D>") || line.startsWith("R>")) && step != null && !line.startsWith("R> Waited")) {
                    if (line.startsWith("R>") && line.contains("(")) {
                        readingInfo.add(line.substring(3).trim());
                    } else if (line.startsWith("D>")) {
                        readingInfo.add(line.substring(3).trim());
                    }
                }
                
                // Append other S> lines to input except the specific line
                if (line.startsWith("S>") && !isAfterStep && !line.contains("STEP")) {
                    input += line.substring(3).trim() + "\n";
                }
            }

            // Store the last step if exists
            if (step != null) {
                StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile,unit,faultyChannel,expectedValue,signalName);
                stepList.add(stepDto); // Add step to stepList

                if (dStarInfo != null) {
                    failedStepList.add(stepDto); // Add failed step to failedStepList
                }
            }

            // Print steps and failed steps with counts
//            int totalSteps = stepList.size();
//            int failedSteps = failedStepList.size();
//            System.out.println("Total Steps: " + totalSteps);
//            System.out.println("Total Failed Steps: " + failedSteps);
            
      
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("DStar Count:----->>> " + rdfFileParser.getDStarCount());

        return stepList;
    }

    private static StepDto createStepDto(String tpgph, String step, String input, List<String> readingInfo,
                                         String dStarInfo, String testPlanFile, String resultDataFile,
                                         String unit,Map<String,String> faultyChannel,String expectedValue,
                                         String signalName) {
        StepDto stepDto = new StepDto();
        stepDto.setTestPlanFile(testPlanFile);
        stepDto.setResultDataFile(resultDataFile);
        stepDto.setTpgph(tpgph);
        stepDto.setStep(step);
        stepDto.setInput(input.trim());
        stepDto.setReadingInfo(new ArrayList<>(readingInfo));
        stepDto.setdStarInfo(dStarInfo);
        stepDto.setUnit(unit);
        stepDto.setFaultyChannel(faultyChannel);
        stepDto.setExpectedValue(expectedValue);
        stepDto.setSignalName(signalName);
        
//        System.out.println("Test Plan File: " + stepDto.getTestPlanFile());
//        System.out.println("Result Data File: " + stepDto.getResultDataFile());
//        System.out.println("TPGPH: " + stepDto.getTpgph());
//        System.out.println("Step: " + stepDto.getStep());
//        System.out.println("Input:");
//        String[] inputs = stepDto.getInput().split("\n");
//        for (int i = 0; i < inputs.length; i++) {
//            System.out.println("  " + inputs[i]);
//        }
//        System.out.println("DStarInfo: " + stepDto.getdStarInfo());
//        System.out.println("ReadingInfo: " + stepDto.getReadingInfo());
//        System.out.println("unit: " + stepDto.getUnit());
//        System.out.println("faultyChannel: " + stepDto.getFaultyChannel());
//        System.out.println("expectedValue: " + stepDto.getExpectedValue());
//        System.out.println("signalName: " + stepDto.getSignalName());        
//        System.out.println("---------------------------------------");

        return stepDto;
    }

    private static String extractTestPlanFileName(String line) {
        String[] parts = line.split(":");
        if (parts.length >= 2) {
            return parts[1].trim();
        }
        return null;
    }

    private static String extractResultDataFileName(String line) {
        String[] parts = line.split(":");
        if (parts.length >= 2) {
            return parts[1].trim();
        }
        return null;
    }

    private static String extractStepNumber(String line) {
        String[] parts = line.split("=");
        if (parts.length >= 2) {
            return parts[1].trim();
        }
        return null;
    }

    private static String extractTPGPH(String line) {
        String[] parts = line.split("=");
        if (parts.length >= 2) {
            return parts[1].trim();
        }
        return null;
    }
    
    private static String extractUnit(String line) {
        int lastParenIndex = line.lastIndexOf(')');
        if (lastParenIndex != -1 && lastParenIndex < line.length() - 1) {
            return line.substring(lastParenIndex + 1).trim();
        }
        return null;
    }
    
    private static Map<String, String> extractFaultyChannels(String dStarInfo) {
        Map<String, String> failedChannels = new LinkedHashMap<>();
        if (dStarInfo != null) {
            Pattern pattern = Pattern.compile("\\((.*?)\\)");
            Matcher matcher = pattern.matcher(dStarInfo);
            if (matcher.find()) {
                String channels = matcher.group(1);
                String[] channelValues = channels.split(",\\s*");
                for (int i = 0; i < channelValues.length; i++) {
                    String trimmedValue = channelValues[i].trim();
                    if (trimmedValue.startsWith("*") || trimmedValue.contains("down")) {
                        String valueWithoutAsterisk = trimmedValue.replace("*", "").trim();
                        failedChannels.put("Channel" + (i + 1), valueWithoutAsterisk);
                    }
                }
            }
        }
        return failedChannels;
    }
    
    private static String extractSignalName(String line) {

    	line = line.substring(3);

        int symbolIndex = line.indexOf('<');
        if (symbolIndex == -1)
            symbolIndex = line.indexOf('>');
        if (symbolIndex == -1)
            symbolIndex = line.indexOf("<=");
        if (symbolIndex == -1)
            symbolIndex = line.indexOf(">=");
        if (symbolIndex == -1)
            symbolIndex = line.indexOf("=");

        if (symbolIndex != -1) {
            return line.substring(0, symbolIndex).trim();
        }
        return null;
    }


    
    private static String extractExpectedValue(String line) {
        line = line.substring(3).trim();

        int separatorIndex = -1;

        if (line.contains("<=")) {
            separatorIndex = line.indexOf("<=");
        } else if (line.contains(">=")) {
            separatorIndex = line.indexOf(">=");
        } else if (line.contains("<")) {
            int lessThanIndex = line.indexOf("<");
            if (lessThanIndex != 0 && lessThanIndex != line.length() - 1 &&
                line.charAt(lessThanIndex - 1) != '=') {
                separatorIndex = lessThanIndex;
            }
        } else if (line.contains(">")) {
            int greaterThanIndex = line.indexOf(">");
            if (greaterThanIndex != 0 && greaterThanIndex != line.length() - 1 &&
                line.charAt(greaterThanIndex - 1) != '=') {
                separatorIndex = greaterThanIndex;
            }
        } else if (line.contains("=")) {
            int equalsIndex = line.indexOf("=");
            if (equalsIndex != 0 && equalsIndex != line.length() - 1 &&
                line.charAt(equalsIndex - 1) != '<' && line.charAt(equalsIndex - 1) != '>') {
                separatorIndex = equalsIndex;
            }
        }

        int questionIndex = line.indexOf('?');

        if (separatorIndex != -1 && questionIndex != -1) {
            return line.substring(separatorIndex + 1, questionIndex).trim().replaceAll("[=<>]", ""); // Remove symbols
        }

        return null;
    }






}


