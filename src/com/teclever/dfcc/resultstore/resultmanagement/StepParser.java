package com.teclever.dfcc.resultstore.resultmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.FaultSRUResponse;
import com.teclever.dfcc.datastore.dto.FaultySRUDto;
import com.teclever.dfcc.datastore.filemanagement.FaultySRUManagement;
import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.rdfFileParser;

public class StepParser {

	static String adbuf_ram_startv_signalName = "";
	static boolean adbuf_ram_startv_signalName_flag = false;

	//Commented on 05-02-2026 For input Memory Out Of Bound Exception
//	public static List<StepDto> parseStepContextNEW(String filePath) throws InterruptedException {
//		List<StepDto> stepList = new ArrayList<>();
//		List<StepDto> failedStepList = new ArrayList<>();
//		String tpgph = null;
//		String step = null;
//		String input = "";
//		List<String> readingInfo = new ArrayList<>();
//		String dStarInfo = null;
//		String testPlanFile = null;
//		String resultDataFile = null;
//		String unit = null;
//		Map<String, String> faultyChannel = new HashMap<>();
//
//		// Added For Deviation
//		Map<String, String> channelValues = new HashMap<>();
//		String expectedValue = null;
//		String signalName = null;
//		boolean isAfterStep = false;
//		String faultySRU = null;
//		boolean stepAdded = false;
//
//		File file = new File(filePath);
//
////	    ////System.out.println("Length Of File Before Wait  :"+file.length());
////		if (file.exists()) {
////			if (file.length() < 1) {
////				Thread.sleep(5000);
////				////System.out.println("Length Of First Thread Sleep 1st :"+file.length());
////			}
////			
////			if (file.length() < 1) {
////				Thread.sleep(3000);
////				////System.out.println("Length Of Second  Thread Sleep 2nd :"+file.length());
////			}
////		}
//
////	    ////System.out.println("Length Of File After Wait  :"+file.length());
//
//		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//			String line;
//
//			while ((line = reader.readLine()) != null) {
//				// ////System.out.println("--" + line);
//				if (line.startsWith("S>") && line.contains("TPGPH")) {
//					if (!stepAdded && (step != null || dStarInfo != null)) {
//						StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//								resultDataFile, unit, faultyChannel, channelValues, expectedValue, signalName,
//								faultySRU);
//						stepList.add(stepDto);
//						if (dStarInfo != null)
//							failedStepList.add(stepDto);
//						stepAdded = true;
//					}
//
//					tpgph = extractTPGPH(line);
//					step = null;
//					input = "";
//					readingInfo = new ArrayList<>();
//					dStarInfo = null;
//					unit = null;
//					faultyChannel = new HashMap<>();
//					channelValues = new HashMap<>();
//					expectedValue = null;
//					signalName = null;
//					isAfterStep = false;
//					faultySRU = null;
//					stepAdded = false;
//
//				} else if (line.startsWith("S>") && line.contains("STEP")) {
//					if (!stepAdded && (step != null || dStarInfo != null)) {
//						StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//								resultDataFile, unit, faultyChannel, channelValues, expectedValue, signalName,
//								faultySRU);
//						stepList.add(stepDto);
//						if (dStarInfo != null)
//							failedStepList.add(stepDto);
//						stepAdded = true;
//					}
//
//					step = extractStepNumber(line);
//					input = "";
//					readingInfo = new ArrayList<>();
//					dStarInfo = null;
//					unit = null;
//					faultyChannel = new HashMap<>();
//					channelValues = new HashMap<>();
//					expectedValue = null;
//					signalName = null;
//					isAfterStep = true;
//					faultySRU = null;
//					stepAdded = false;
//
//					FaultySRUManagement f = new FaultySRUManagement();
//					FaultSRUResponse res = f.getFaultySRUsByUutIdNEW(currentSessionDetails.getUutId(), filePath);
//
//					for (FaultySRUDto mysqlRecord : res.getFaultySRUs()) {
//						String[] stepParts = mysqlRecord.getStep().split("=");
//						String trimmedStep = stepParts.length > 1 ? stepParts[1].trim() : mysqlRecord.getStep().trim();
//						if (trimmedStep.equals(step)) {
//							faultySRU = mysqlRecord.getFaultySRU();
//							break;
//						}
//					}
//
//				} else if (line.startsWith("Z>") && line.contains("Test plan file")) {
//					testPlanFile = extractTestPlanFileName(line);
//
//				} else if (line.startsWith("Z>") && line.contains("Result data file")) {
//					resultDataFile = extractResultDataFileName(line);
//
//				} else if (line.startsWith("S>") && !isAfterStep) {
//					if (!line.contains("STEP") && !line.contains("opwait")) {
//						// Always reset for new signal
//						signalName = null;
//
//						// For The Deviation We Reset
//						// expectedValue = null;
//						signalName = extractSignalName(line);
//						// expectedValue = extractExpectedValue(line);
//
//						String tempExpected = extractExpectedValue(line);
//						if (tempExpected != null && !tempExpected.isBlank()) {
//							expectedValue = tempExpected; // ✔ keep the last valid one
//						}
//
//					}
//					input += line.substring(3).trim() + "\n";
//
//				} else if (line.startsWith("S>") && isAfterStep) {
//					if (line.contains("opwait"))
//						continue;
//					signalName = null;
//					expectedValue = null;
//					signalName = extractSignalName(line);
//					// expectedValue = extractExpectedValue(line);
//
//					String tempExpected = extractExpectedValue(line);
//					if (tempExpected != null && !tempExpected.isBlank()) {
//						expectedValue = tempExpected; // ✔ keep the last valid one
//					}
//
//					isAfterStep = false;
//
//				} else if (line.startsWith("D*>")) {
//
//					boolean waitedTimeFlag = false;
//
//					if (line.contains("diff(s)")) {
//						dStarInfo = dStarSpecialExtractor(line);
//					} else if (line.contains("Wait for condition timed out.")) {
//						waitedTimeFlag = true;
//
//					} else {
//						dStarInfo = line.substring(3).trim();
//					}
//
//					if (waitedTimeFlag) {
//
//						unit = "";
//						faultyChannel.put("CH1", "Wait for condition timed out.");
//						faultyChannel.put("CH2", "Wait for condition timed out.");
//						faultyChannel.put("CH3", "Wait for condition timed out.");
//						faultyChannel.put("CH4", "Wait for condition timed out.");
//						signalName = "Wait for condition timed out.";
//						faultySRU = "CH1,CH2,CH3,CH4";
//						rdfFileParser.setDStarFound(true);
//						rdfFileParser.incrementDStarCount();
//
//						StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//								resultDataFile, unit, faultyChannel, channelValues, expectedValue, signalName,
//								faultySRU);
//						stepList.add(stepDto);
//						failedStepList.add(stepDto);
//						stepAdded = true;
//
//					} else {
//						unit = extractUnit(dStarInfo);
//						faultyChannel = extractFaultyChannels(dStarInfo);
//						rdfFileParser.setDStarFound(true);
//						rdfFileParser.incrementDStarCount();
//						StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//								resultDataFile, unit, faultyChannel, channelValues, expectedValue, signalName,
//								faultySRU);
//						stepList.add(stepDto);
//						failedStepList.add(stepDto);
//						stepAdded = true;
//					}
//					// Reset only D* fields
//					dStarInfo = null;
//					unit = null;
//					faultyChannel = new HashMap<>();
//					expectedValue = null;
//					signalName = null;
//					faultySRU = null;
//
//				}
//
//				else if ((line.startsWith("D>") || line.startsWith("R>")) && step != null
//						&& !line.startsWith("R> Waited")) {
//					if (line.startsWith("R>") && line.contains("(")) {
//						channelValues = extractChannelsValues(line);
//						readingInfo.add(line.substring(3).trim());
//					} else if (line.startsWith("D>")) {
//						readingInfo.add(line.substring(3).trim());
//					}
//
//				} else if (line.contains("Parse Error")) {
//					rdfFileParser.setParseFileError(true);
//				}
//
//				if (line.startsWith("S>") && !isAfterStep && !line.contains("STEP")) {
//					input += line.substring(3).trim() + "\n";
//				}
//			}
//
//			// Previous Final step if not already added
//			if (!stepAdded && (step != null || dStarInfo != null)) {
//				StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//						resultDataFile, unit, faultyChannel, channelValues, expectedValue, signalName, faultySRU);
//				stepList.add(stepDto);
//				if (dStarInfo != null)
//					failedStepList.add(stepDto);
//			}
//
//			return stepList;
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return stepList;
//	}
	
	// ADDED ON 05-02-2026 For Input Memory Out Of Bound Exception
	public static List<StepDto> parseStepContextNEW(String filePath) throws InterruptedException {
	    List<StepDto> stepList = new ArrayList<>();
	    List<StepDto> failedStepList = new ArrayList<>();
	    String tpgph = null;
	    String step = null;

	    // FIX: use StringBuilder instead of String
	    StringBuilder inputBuilder = new StringBuilder(4096);

	    List<String> readingInfo = new ArrayList<>();
	    String dStarInfo = null;
	    String testPlanFile = null;
	    String resultDataFile = null;
	    String unit = null;
	    Map<String, String> faultyChannel = new HashMap<>();

	    // Added For Deviation
	    Map<String, String> channelValues = new HashMap<>();
	    String expectedValue = null;
	    String signalName = null;
	    boolean isAfterStep = false;
	    String faultySRU = null;
	    boolean stepAdded = false;

	    File file = new File(filePath);

	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	        String line;

	        while ((line = reader.readLine()) != null) {

	            if (line.startsWith("S>") && line.contains("TPGPH")) {

	                if (!stepAdded && (step != null || dStarInfo != null)) {
	                    StepDto stepDto = createStepDto(
	                            tpgph, step, inputBuilder.toString(), readingInfo, dStarInfo,
	                            testPlanFile, resultDataFile, unit, faultyChannel,
	                            channelValues, expectedValue, signalName, faultySRU
	                    );
	                    stepList.add(stepDto);
	                    if (dStarInfo != null)
	                        failedStepList.add(stepDto);
	                    stepAdded = true;
	                }

	                tpgph = extractTPGPH(line);
	                step = null;
	                inputBuilder.setLength(0); // RESET
	                readingInfo = new ArrayList<>();
	                dStarInfo = null;
	                unit = null;
	                faultyChannel = new HashMap<>();
	                channelValues = new HashMap<>();
	                expectedValue = null;
	                signalName = null;
	                isAfterStep = false;
	                faultySRU = null;
	                stepAdded = false;

				} else if (line.startsWith("S>") && line.contains("STEP")) {

					if (!stepAdded && (step != null || dStarInfo != null)) {
						StepDto stepDto = createStepDto(tpgph, step, inputBuilder.toString(), readingInfo, dStarInfo,
								testPlanFile, resultDataFile, unit, faultyChannel, channelValues, expectedValue,
								signalName, faultySRU);
						stepList.add(stepDto);
						if (dStarInfo != null)
							failedStepList.add(stepDto);
						stepAdded = true;
					}

					step = extractStepNumber(line);
					inputBuilder.setLength(0); // RESET
					readingInfo = new ArrayList<>();
					dStarInfo = null;
					unit = null;
					faultyChannel = new HashMap<>();
					channelValues = new HashMap<>();
					expectedValue = null;
					signalName = null;
					isAfterStep = true;
					faultySRU = null;
					stepAdded = false;

					FaultySRUManagement f = new FaultySRUManagement();
//	                FaultSRUResponse res = f.getFaultySRUsByUutIdNEW(
//	                        currentSessionDetails.getUutId(), filePath
//	                );

					FaultSRUResponse res = f.getFaultySRUsByUutIdAndStep(currentSessionDetails.getUutId(), step);
					if (res.getResponseCode() == 1) {
						faultySRU = "";
					}

//					System.out.println("FAULTY ::");
					for (FaultySRUDto mysqlRecord : res.getFaultySRUs()) {
						
						String[] stepParts = mysqlRecord.getStep().split("=");
						String trimmedStep = stepParts.length > 1 ? stepParts[1].trim() : mysqlRecord.getStep().trim();
						if (trimmedStep.equals(step)) {
							faultySRU = faultySRU + mysqlRecord.getFaultySRU() + ";";
							//System.out.println("Faulty SRU"+faultySRU);
						}
					}

				} else if (line.startsWith("Z>") && line.contains("Test plan file")) {
	                testPlanFile = extractTestPlanFileName(line);

	            } else if (line.startsWith("Z>") && line.contains("Result data file")) {
	                resultDataFile = extractResultDataFileName(line);

	            } else if (line.startsWith("S>") && !isAfterStep) {

	                if (!line.contains("STEP") && !line.contains("opwait")) {
	                    signalName = extractSignalName(line);

	                    String tempExpected = extractExpectedValue(line);
	                    if (tempExpected != null && !tempExpected.isBlank()) {
	                        expectedValue = tempExpected;
	                    }
	                }

	                // FIX: safe append
	                inputBuilder.append(line, 3, line.length()).append('\n');

	            } else if (line.startsWith("S>") && isAfterStep) {

	                if (line.contains("opwait"))
	                    continue;

	                signalName = extractSignalName(line);
	                expectedValue = null;

	                String tempExpected = extractExpectedValue(line);
	                if (tempExpected != null && !tempExpected.isBlank()) {
	                    expectedValue = tempExpected;
	                }

	                isAfterStep = false;

	            } else if (line.startsWith("D*>")) {

	                boolean waitedTimeFlag = false;

	                if (line.contains("diff(s)")) {
	                    dStarInfo = dStarSpecialExtractor(line);
	                } else if (line.contains("Wait for condition timed out.")) {
	                    waitedTimeFlag = true;
	                } else {
	                    dStarInfo = line.substring(3).trim();
	                }

	                if (waitedTimeFlag) {

	                    unit = "";
	                    faultyChannel.put("CH1", "Wait for condition timed out.");
	                    faultyChannel.put("CH2", "Wait for condition timed out.");
	                    faultyChannel.put("CH3", "Wait for condition timed out.");
	                    faultyChannel.put("CH4", "Wait for condition timed out.");
	                    signalName = "Wait for condition timed out.";
	                    faultySRU = "CH1,CH2,CH3,CH4";
	                    rdfFileParser.setDStarFound(true);
	                    rdfFileParser.incrementDStarCount();

	                } else {
	                    unit = extractUnit(dStarInfo);
	                    faultyChannel = extractFaultyChannels(dStarInfo);
	                    rdfFileParser.setDStarFound(true);
	                    rdfFileParser.incrementDStarCount();
	                    //For Faulty Correction Mani Added - 25-03-2026
//	                    System.out.println("DSTAR"+dStarInfo);
//	                    System.out.println("FAULTY"+faultySRU);
	                    
	                    Map<String,String> faultyChannelsList = new LinkedHashMap<String,String>();
	                    Matcher matcher = Pattern.compile("-?\\d+\\.\\d+").matcher(dStarInfo);
	                    int inc = 1;
	                    
//						while (matcher.find()) {
//							if (matcher.group().contains("*")) {
//								faultyChannelsList.put("CH" + inc, matcher.group());
//							}
//							inc++;
//						}
						
						String[]channelStars = dStarInfo.split(",");
						for (int i = 0; i < channelStars.length; i++) {
							
							if (channelStars[i].contains("*") || channelStars[i].contains("offline")) {
								faultyChannelsList.put("CH" +inc, channelStars[i]);
							}
							inc++;
						}
						
						
						
	                    if(faultySRU!=null)
						{
							String[] faultySRUArr = faultySRU.split(";");
							Map<String, String> faultyChannelValues = new HashMap<>();
							for (int i = 0; i < faultySRUArr.length; i++) {
								if (faultySRUArr[i].trim().contains("Ch1")) {
									faultyChannelValues.put("CH1", faultySRUArr[i].trim());
								}
								if (faultySRUArr[i].trim().contains("Ch2")) {
									faultyChannelValues.put("CH2", faultySRUArr[i].trim());
								}
								if (faultySRUArr[i].trim().contains("Ch3")) {
									faultyChannelValues.put("CH3", faultySRUArr[i].trim());
								}
								if (faultySRUArr[i].trim().contains("Ch4")) {
									faultyChannelValues.put("CH4", faultySRUArr[i].trim());
								}
							}

							String finalFaultySRU = "";

							for (String faultyChannels : faultyChannelsList.keySet()) {
								if (faultyChannelValues.keySet().contains(faultyChannels))
									finalFaultySRU = finalFaultySRU + faultyChannelValues.get(faultyChannels) + ";";
							}

							faultySRU = finalFaultySRU;
//							System.out.println("Final faulty SRU" + faultySRU);

						}else
						{
							if (faultyChannelsList.keySet().size() > 0) {
								String finalOfflineFaulty = "";
								for (String f : faultyChannelsList.keySet()) {
									if (faultyChannelsList.get(f).contains("offline")) {
										finalOfflineFaulty = finalOfflineFaulty + f + ";";
									}
								}
								faultySRU = "POWERSUPPLY DIGITAL" + finalOfflineFaulty;
							}
						}
	                }
	                
	                
	                
	                
	                
	
	                StepDto stepDto = createStepDto(
	                        tpgph, step, inputBuilder.toString(), readingInfo, dStarInfo,
	                        testPlanFile, resultDataFile, unit, faultyChannel,
	                        channelValues, expectedValue, signalName, faultySRU
	                );

	                stepList.add(stepDto);
	                failedStepList.add(stepDto);
	                stepAdded = true;

	                dStarInfo = null;
	                unit = null;
	                faultyChannel = new HashMap<>();
	                expectedValue = null;
	                signalName = null;
	                faultySRU = null;

	            } else if ((line.startsWith("D>") || line.startsWith("R>")) && step != null
	                    && !line.startsWith("R> Waited")) {

	                if (line.startsWith("R>") && line.contains("(")) {
	                    channelValues = extractChannelsValues(line);
	                    readingInfo.add(line.substring(3).trim());
	                } else if (line.startsWith("D>")) {
	                    readingInfo.add(line.substring(3).trim());
	                }

	            } else if (line.contains("Parse Error")) {
	                rdfFileParser.setParseFileError(true);
	            }
	        }

	        if (!stepAdded && (step != null || dStarInfo != null)) {
	            StepDto stepDto = createStepDto(
	                    tpgph, step, inputBuilder.toString(), readingInfo, dStarInfo,
	                    testPlanFile, resultDataFile, unit, faultyChannel,
	                    channelValues, expectedValue, signalName, faultySRU
	            );
	            stepList.add(stepDto);
	            if (dStarInfo != null)
	                failedStepList.add(stepDto);
	        }

	        return stepList;

	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return stepList;
	}

	private static String dStarSpecialExtractor(String dStarInfo) {
		String res = "";

		// Match everything from the LAST opening '(' to its matching closing ')'
		Pattern pattern = Pattern.compile("\\((.*)\\)");
		Matcher matcher = pattern.matcher(dStarInfo);

		if (matcher.find()) {
			res = "(" + matcher.group(1) + ")";
//			////System.out.println("Extracted: " + res);
		} else {
			////System.out.println("No match found.");
		}

		return res;
	}

	// kindof
//	public static List<StepDto> parseStepContextNEW(String filePath) {
//	    List<StepDto> stepList = new ArrayList<>();
//	    List<StepDto> failedStepList = new ArrayList<>();
//	    String tpgph = null;
//	    String step = null;
//	    String input = "";
//	    List<String> readingInfo = new ArrayList<>();
//	    String dStarInfo = null;
//	    String testPlanFile = null;
//	    String resultDataFile = null;
//	    String unit = null;
//	    Map<String, String> faultyChannel = new HashMap<>();
//	    String expectedValue = null;
//	    String signalName = null;
//	    boolean isAfterStep = false;
//	    String faultySRU = null;
//	    boolean stepAdded = false; // New flag to track if step has already been added
//
//	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//	        String line;
//
//	        while ((line = reader.readLine()) != null) {
//	            if (line.startsWith("S>") && line.contains("TPGPH")) {
//	                if (!stepAdded && (step != null || dStarInfo != null)) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//	                            resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	                    stepList.add(stepDto);
//	                    if (dStarInfo != null) failedStepList.add(stepDto);
//	                    stepAdded = true;
//	                }
//
//	                tpgph = extractTPGPH(line);
//	                step = null;
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = false;
//	                faultySRU = null;
//	                stepAdded = false;
//
//	            } else if (line.startsWith("S>") && line.contains("STEP")) {
//	                if (!stepAdded && (step != null || dStarInfo != null)) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//	                            resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	                    stepList.add(stepDto);
//	                    if (dStarInfo != null) failedStepList.add(stepDto);
//	                    stepAdded = true;
//	                }
//
//	                step = extractStepNumber(line);
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = true;
//	                faultySRU = null;
//	                stepAdded = false;
//
//	                FaultySRUManagement f = new FaultySRUManagement();
//	                FaultSRUResponse res = f.getFaultySRUsByUutIdNEW(currentSessionDetails.getUutId(), filePath);
//
//	                for (FaultySRUDto mysqlRecord : res.getFaultySRUs()) {
//	                    String[] stepParts = mysqlRecord.getStep().split("=");
//	                    String trimmedStep = stepParts.length > 1 ? stepParts[1].trim() : mysqlRecord.getStep().trim();
//	                    if (trimmedStep.equals(step)) {
//	                        faultySRU = mysqlRecord.getFaultySRU();
//	                        break;
//	                    }
//	                }
//
//	            } else if (line.startsWith("Z>") && line.contains("Test plan file")) {
//	                testPlanFile = extractTestPlanFileName(line);
//
//	            } else if (line.startsWith("Z>") && line.contains("Result data file")) {
//	                resultDataFile = extractResultDataFileName(line);
//
//	            } else if (line.startsWith("S>") && !isAfterStep) {
//	                if (signalName == null || expectedValue == null) {
//	                    if (!line.contains("STEP") && !line.contains("opwait")) {
//	                        signalName = extractSignalName(line);
//	                        expectedValue = extractExpectedValue(line);
//	                    }
//	                }
//	                input += line.substring(3).trim() + "\n";
//
//	            } else if (line.startsWith("S>") && isAfterStep) {
//	                if (line.contains("opwait")) continue;
//	                signalName = extractSignalName(line);
//	                expectedValue = extractExpectedValue(line);
//	                isAfterStep = false;
//
//	            } else if (line.startsWith("D*>")) {
//	                dStarInfo = line.substring(3).trim();
//	                unit = extractUnit(dStarInfo);
//	                faultyChannel = extractFaultyChannels(dStarInfo);
//	                rdfFileParser.setDStarFound(true);
//	                rdfFileParser.incrementDStarCount();
//
//	                // Create and add StepDto for each D* line
//	                StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//	                        resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	                stepList.add(stepDto);
//	                failedStepList.add(stepDto);
//	                stepAdded = true;
//
//	                // Reset only dStar-specific fields, keep step unchanged
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                faultySRU = null;
//
//	            } else if ((line.startsWith("D>") || line.startsWith("R>")) && step != null && !line.startsWith("R> Waited")) {
//	                if (line.startsWith("R>") && line.contains("(")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                } else if (line.startsWith("D>")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                }
//
//	            } else if (line.contains("Parse Error")) {
//	                rdfFileParser.setParseFileError(true);
//	            }
//
//	            if (line.startsWith("S>") && !isAfterStep && !line.contains("STEP")) {
//	                input += line.substring(3).trim() + "\n";
//	            }
//	        }
//
//	        // Final step if not already added
//	        if (!stepAdded && (step != null || dStarInfo != null)) {
//	            StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile,
//	                    resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	            stepList.add(stepDto);
//	            if (dStarInfo != null) failedStepList.add(stepDto);
//	        }
//
//	        ////System.out.println("DStar Count:----->>> " + rdfFileParser.getDStarCount());
//	        return stepList;
//
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//
//	    return stepList;
//	}

	// final 0
//	public static List<StepDto> parseStepContextNEW(String filePath) {
//	    List<StepDto> stepList = new ArrayList<>();
//	    List<StepDto> failedStepList = new ArrayList<>();
//	    String tpgph = null;
//	    String step = null;
//	    String input = "";
//	    List<String> readingInfo = new ArrayList<>();
//	    String dStarInfo = null;
//	    String testPlanFile = null;
//	    String resultDataFile = null;
//	    String unit = null;
//	    Map<String, String> faultyChannel = new HashMap<>();
//	    String expectedValue = null;
//	    String signalName = null;
//	    boolean isAfterStep = false;
//	    String faultySRU = null;
//
//	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//	        String line;
//
//	        while ((line = reader.readLine()) != null) {
//	            if (line.startsWith("S>") && line.contains("TPGPH")) {
//	                // Store previous step
//	                if (step != null || dStarInfo != null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	                    stepList.add(stepDto);
//	                    if (dStarInfo != null) failedStepList.add(stepDto);
//	                }
//
//	                // Reset on new TPGPH
//	                tpgph = extractTPGPH(line);
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = false;
//	                faultySRU = null;
//	                // NOTE: Don't reset step here
//	            } 
//	            else if (line.startsWith("S>") && line.contains("STEP")) {
//	                if (step != null || dStarInfo != null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	                    stepList.add(stepDto);
//	                    if (dStarInfo != null) failedStepList.add(stepDto);
//	                }
//
//	                step = extractStepNumber(line);
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = true;
//	                faultySRU = null;
//
//	                FaultySRUManagement f = new FaultySRUManagement();
//	                FaultSRUResponse res = f.getFaultySRUsByUutIdNEW(currentSessionDetails.getUutId(), filePath);
//	                for (FaultySRUDto mysqlRecord : res.getFaultySRUs()) {
//	                    String[] stepParts = mysqlRecord.getStep().split("=");
//	                    String trimmedStep = stepParts.length > 1 ? stepParts[1].trim() : mysqlRecord.getStep().trim();
//	                    if (trimmedStep.equals(step)) {
//	                        faultySRU = mysqlRecord.getFaultySRU();
//	                        break;
//	                    }
//	                }
//	            } 
//	            else if (line.startsWith("Z>") && line.contains("Test plan file")) {
//	                testPlanFile = extractTestPlanFileName(line);
//	            } 
//	            else if (line.startsWith("Z>") && line.contains("Result data file")) {
//	                resultDataFile = extractResultDataFileName(line);
//	            } 
//	            else if (line.startsWith("S>") && !isAfterStep) {
//	                if ((signalName == null || expectedValue == null) && !line.contains("STEP") && !line.contains("opwait")) {
//	                    signalName = extractSignalName(line);
//	                    expectedValue = extractExpectedValue(line);
//	                }
//	                input += line.substring(3).trim() + "\n";
//	            } 
//	            else if (line.startsWith("S>") && isAfterStep) {
//	                if (line.contains("opwait")) continue;
//
//	                signalName = extractSignalName(line);
//	                expectedValue = extractExpectedValue(line);
//	                isAfterStep = false;
//	            } 
//	            else if (line.startsWith("D*>")) {
//	                dStarInfo = line.substring(3).trim();
//	                unit = extractUnit(dStarInfo);
//	                faultyChannel = extractFaultyChannels(dStarInfo);
//	                rdfFileParser.setDStarFound(true);
//	                rdfFileParser.incrementDStarCount();
//
//	                // Always use the current step (even across multiple D*> lines)
//	                StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	                stepList.add(stepDto);
//	                failedStepList.add(stepDto);
//
//	                // Reset for next D*> or reading
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                faultySRU = null;
//	            } 
//	            else if ((line.startsWith("D>") || line.startsWith("R>")) && step != null && !line.startsWith("R> Waited")) {
//	                if (line.startsWith("R>") && line.contains("(")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                } else if (line.startsWith("D>")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                }
//	            } 
//	            else if (line.contains("Parse Error")) {
//	                rdfFileParser.setParseFileError(true);
//	            }
//
//	            if (line.startsWith("S>") && !isAfterStep && !line.contains("STEP")) {
//	                input += line.substring(3).trim() + "\n";
//	            }
//	        }
//
//	        // Store final step
//	        if (step != null || dStarInfo != null) {
//	            StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName, faultySRU);
//	            stepList.add(stepDto);
//	            if (dStarInfo != null) failedStepList.add(stepDto);
//	        }
//
//	        ////System.out.println("DStar Count:----->>> " + rdfFileParser.getDStarCount());
//	        return stepList;
//
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//
//	    return stepList;
//	}

	// Last Git
//	public static List<StepDto> parseStepContextNEW(String filePath) {
//	    List<StepDto> stepList = new ArrayList<>();
//	    List<StepDto> failedStepList = new ArrayList<>();
//	    String tpgph = null;
//	    String step = null;
//	    String input = "";
//	    List<String> readingInfo = new ArrayList<>();
//	    String dStarInfo = null;
//	    String testPlanFile = null;
//	    String resultDataFile = null;
//	    String unit = null;
//	    Map<String, String> faultyChannel = new HashMap<>();
//	    String expectedValue = null;
//	    String signalName = null;
//	    boolean isAfterStep = false; // Flag to indicate whether the line is after "S> STEP"
//	    String faultySRU = null;
//
//	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//	        String line;
//
//	        while ((line = reader.readLine()) != null) {
//	            if (line.startsWith("S>") && line.contains("TPGPH")) {
//	                // Store the previous step if exists
//	                if (step != null || dStarInfo != null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName,faultySRU);
//	                    stepList.add(stepDto); // Add step to stepList
//
//	                    if (dStarInfo != null) {
//	                        failedStepList.add(stepDto); // Add failed step to failedStepList
//	                    }
//	                }
//
//	                // Reset step-related variables when encountering a new TPGPH
//	                tpgph = extractTPGPH(line);
//	                step = null;
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = false;
//	                faultySRU =null;
//
//	            } else if (line.startsWith("S>") && line.contains("STEP")) {
//	                if (step != null || dStarInfo != null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName,faultySRU);
//	                    stepList.add(stepDto); // Add step to stepList
//
//	                    if (dStarInfo != null) {
//	                        failedStepList.add(stepDto); // Add failed step to failedStepList
//	                    }
//	                }
//	                step = extractStepNumber(line);
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = true; // Set flag to true after encountering "S> STEP"
//	                faultySRU=null;
//
//	        		FaultySRUManagement f = new FaultySRUManagement();
//	        		FaultSRUResponse res =f.getFaultySRUsByUutIdNEW(currentSessionDetails.getUutId(),filePath);
//	        		 // Here, check for faultySRU
//	                for (FaultySRUDto mysqlRecord : res.getFaultySRUs()) {
//	                    String[] stepParts = mysqlRecord.getStep().split("=");
//	                    String trimmedStep = stepParts.length > 1 ? stepParts[1].trim() : mysqlRecord.getStep().trim();
//
//	                    if (trimmedStep.equals(step)) {
//	                        faultySRU = mysqlRecord.getFaultySRU();
//	                        break;
//	                    }
//	                }
//
//	            } else if (line.startsWith("Z>") && line.contains("Test plan file")) {
//	                testPlanFile = extractTestPlanFileName(line);
//	            } else if (line.startsWith("Z>") && line.contains("Result data file")) {
//	                resultDataFile = extractResultDataFileName(line);
//	            }else if (line.startsWith("S>") && !isAfterStep) {
//	                // Only extract signalName and expectedValue if they have not been set yet
//	                if (signalName == null || expectedValue == null) {
//	                    // Extract signalName and expectedValue even when there's no STEP or TPGPH
//	                    if (!line.contains("STEP") && !line.contains("opwait")) {
//	                        signalName = extractSignalName(line);
//	                        expectedValue = extractExpectedValue(line);
//	                    }
//	                }
//
//	                // Append to input otherwise
//	                input += line.substring(3).trim() + "\n";
//	            }
//
//	            else if (line.startsWith("S>") && isAfterStep) {
//	                // Ignore S> opwait lines and continue searching for signalName and expectedValue
//	                if (line.contains("opwait")) {
//	                    continue; // Skip this line and proceed to the next
//	                }
//	                // Extract signalName and expectedValue from this line
//	                signalName = extractSignalName(line);
//	                expectedValue = extractExpectedValue(line);
//	                isAfterStep = false; // Reset flag after extracting values
//	            } else if (line.startsWith("D*>")) {
//	                dStarInfo = line.substring(3).trim();
//	                unit = extractUnit(dStarInfo);
//	                faultyChannel = extractFaultyChannels(dStarInfo);
//	                rdfFileParser.setDStarFound(true); // d star found update to state machine
//	                rdfFileParser.incrementDStarCount();
//
//	                // Create a stepDto for D*> line if no STEP or TPGPH is present
//	                if (step == null && tpgph == null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName,faultySRU);
//	                    stepList.add(stepDto);
//
//	                    if (dStarInfo != null) {
//	                        failedStepList.add(stepDto); // Add failed step to failedStepList
//	                    }
//
//	                    // Reset variables for next potential D*> line
//	                    dStarInfo = null;
//	                    unit = null;
//	                    faultyChannel = new HashMap<>();
//	                    expectedValue = null;
//	                    signalName = null;
//	                    faultySRU=null;
//	                }
//	            } else if ((line.startsWith("D>") || line.startsWith("R>")) && step != null && !line.startsWith("R> Waited")) {
//	                if (line.startsWith("R>") && line.contains("(")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                } else if (line.startsWith("D>")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                }
//	            } else if (line.contains("Parse Error")) {
//	                rdfFileParser.setParseFileError(true);
//	            }
//
//	            // Append other S> lines to input except the specific line
//	            if (line.startsWith("S>") && !isAfterStep && !line.contains("STEP")) {
//	                input += line.substring(3).trim() + "\n";
//	            }
//	        }
//
//	        // Store the last step if exists
//	        if (step != null || dStarInfo != null) {
//	            StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName,faultySRU);
//	            stepList.add(stepDto); // Add step to stepList
//
//	            if (dStarInfo != null) {
//	                failedStepList.add(stepDto); // Add failed step to failedStepList
//	            }
//	        }
//
//	        ////System.out.println("DStar Count:----->>> " + rdfFileParser.getDStarCount());
//
//	        return stepList;
//
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//
//	    return stepList;
//	}

//	public static List<StepDto> parseStepContext(String filePath) {
//	    List<StepDto> stepList = new ArrayList<>();
//	    List<StepDto> failedStepList = new ArrayList<>();
//	    String tpgph = null;
//	    String step = null;
//	    String input = "";
//	    List<String> readingInfo = new ArrayList<>();
//	    String dStarInfo = null;
//	    String testPlanFile = null;
//	    String resultDataFile = null;
//	    String unit = null;
//	    Map<String, String> faultyChannel = new HashMap<>();
//	    String expectedValue = null;
//	    String signalName = null;
//	    boolean isAfterStep = false; // Flag to indicate whether the line is after "S> STEP"
//
//	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//	        String line;
//
//	        while ((line = reader.readLine()) != null) {
//	            if (line.startsWith("S>") && line.contains("TPGPH")) {
//	                // Store the previous step if exists
//	                if (step != null || dStarInfo != null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName);
//	                    stepList.add(stepDto); // Add step to stepList
//
//	                    if (dStarInfo != null) {
//	                        failedStepList.add(stepDto); // Add failed step to failedStepList
//	                    }
//	                }
//
//	                // Reset step-related variables when encountering a new TPGPH
//	                tpgph = extractTPGPH(line);
//	                step = null;
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = false;
//
//	            } else if (line.startsWith("S>") && line.contains("STEP")) {
//	                if (step != null || dStarInfo != null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName);
//	                    stepList.add(stepDto); // Add step to stepList
//
//	                    if (dStarInfo != null) {
//	                        failedStepList.add(stepDto); // Add failed step to failedStepList
//	                    }
//	                }
//	                step = extractStepNumber(line);
//	                input = "";
//	                readingInfo = new ArrayList<>();
//	                dStarInfo = null;
//	                unit = null;
//	                faultyChannel = new HashMap<>();
//	                expectedValue = null;
//	                signalName = null;
//	                isAfterStep = true; // Set flag to true after encountering "S> STEP"
//
//	            } else if (line.startsWith("Z>") && line.contains("Test plan file")) {
//	                testPlanFile = extractTestPlanFileName(line);
//	            } else if (line.startsWith("Z>") && line.contains("Result data file")) {
//	                resultDataFile = extractResultDataFileName(line);
//	            } else if (line.startsWith("S>") && isAfterStep) {
//	                // Ignore S> opwait lines and continue searching for signalName and expectedValue
//	                if (line.contains("opwait")) {
//	                    continue; // Skip this line and proceed to the next
//	                }
//	                // Extract signalName and expectedValue from this line
//	                signalName = extractSignalName(line);
//	                expectedValue = extractExpectedValue(line);
//	                isAfterStep = false; // Reset flag after extracting values
//	            } else if (line.startsWith("D*>")) {
//	                dStarInfo = line.substring(3).trim();
//	                unit = extractUnit(dStarInfo);
//	                faultyChannel = extractFaultyChannels(dStarInfo);
//	                rdfFileParser.setDStarFound(true); // d star found update to state machine
//	                rdfFileParser.incrementDStarCount();
//
//	                // Create a stepDto for D*> line if no STEP or TPGPH is present
//	                if (step == null && tpgph == null) {
//	                    StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName);
//	                    stepList.add(stepDto);
//
//	                    if (dStarInfo != null) {
//	                        failedStepList.add(stepDto); // Add failed step to failedStepList
//	                    }
//
//	                    // Reset variables for next potential D*> line
//	                    dStarInfo = null;
//	                    unit = null;
//	                    faultyChannel = new HashMap<>();
//	                    expectedValue = null;
//	                    signalName = null;
//	                }
//	            } else if ((line.startsWith("D>") || line.startsWith("R>")) && step != null && !line.startsWith("R> Waited")) {
//	                if (line.startsWith("R>") && line.contains("(")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                } else if (line.startsWith("D>")) {
//	                    readingInfo.add(line.substring(3).trim());
//	                }
//	            } else if (line.contains("Parse Error")) {
//	                rdfFileParser.setParseFileError(true);
//	            }
//
//	            // Append other S> lines to input except the specific line
//	            if (line.startsWith("S>") && !isAfterStep && !line.contains("STEP")) {
//	                input += line.substring(3).trim() + "\n";
//	            }
//	        }
//
//	        // Store the last step if exists
//	        if (step != null || dStarInfo != null) {
//	            StepDto stepDto = createStepDto(tpgph, step, input, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit, faultyChannel, expectedValue, signalName);
//	            stepList.add(stepDto); // Add step to stepList
//
//	            if (dStarInfo != null) {
//	                failedStepList.add(stepDto); // Add failed step to failedStepList
//	            }
//	        }
//
//	        Debug.printDebug("DStar Count:----->>> " + rdfFileParser.getDStarCount());
//
//	        return stepList;
//
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//
//	    return stepList;
//	}

	private static StepDto createStepDto(String tpgph, String step, String input, List<String> readingInfo,
			String dStarInfo, String testPlanFile, String resultDataFile, String unit,
			Map<String, String> faultyChannel, Map<String, String> channelValues, String expectedValue,
			String signalName, String faultySRU) {
		StepDto stepDto = new StepDto();
		stepDto.setTpgph(tpgph);
		stepDto.setStep(step);
		stepDto.setInput(input);
		stepDto.setReadingInfo(readingInfo != null ? readingInfo : new ArrayList<>()); // Initialize if null
		stepDto.setdStarInfo(dStarInfo);
		stepDto.setTestPlanFile(testPlanFile);
		stepDto.setResultDataFile(resultDataFile);
		stepDto.setUnit(unit);
		stepDto.setFaultyChannel(faultyChannel != null ? faultyChannel : new HashMap<>()); // Initialize if null
		stepDto.setChannelValues(channelValues);
		stepDto.setExpectedValue(expectedValue);
		stepDto.setSignalName(signalName);
		stepDto.setFaultySRU(faultySRU);
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

	private static List<String> extractChannelValues(String input) {
		List<String> channelValues = new ArrayList<>();

		if (input.contains("diff(s)")) {

			// Remove parentheses
			input = input.replaceAll("[()]", "");

			// Split by comma and trim each part
			String[] parts = input.split(",");

			List<String> result = new ArrayList<>();
			for (String part : parts) {
				channelValues.add(part.trim());
			}

			return channelValues;
		}

		Pattern pattern = Pattern.compile("\\((.*?)\\)");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			String channels = matcher.group(1);
			String[] channelArray = channels.split(",\\s*");

			// For Other Pass Fail Case
//			////System.out.println("Channels String" + channels);
//			////System.out.println("Channels Array" + Arrays.toString(channelArray));

			for (String value : channelArray) {
				String cleanedValue = value.trim().replace("*", "").trim(); // Remove asterisk and trim
				channelValues.add(cleanedValue);
			}
		}
		return channelValues;
	}

	// For Deviation
	public static Map<String, String> extractChannelsValues(String input) {
		Map<String, String> extractedChannels = new LinkedHashMap<>();

		if (input != null) {

			// Regex to extract content inside parentheses
			Pattern pattern = Pattern.compile("\\(([^)]*)\\)");
			Matcher matcher = pattern.matcher(input);

			if (matcher.find()) {
				String inside = matcher.group(1); // e.g. "-2.2, -2.197, -2.17, -2.183"

				// Split by comma
				String[] values = inside.split(",");

				// Store up to 4 channel values
				for (int i = 0; i < values.length && i < 4; i++) {
					extractedChannels.put("channel" + (i + 1), values[i].trim());
				}

				// Pad with empty values if fewer than 4 found
				while (extractedChannels.size() < 4) {
					int index = extractedChannels.size() + 1;
					extractedChannels.put("channel" + index, "");
				}
			}
		}

		return extractedChannels;
	}

	public static Map<String, String> extractFaultyChannels(String dStarInfo) {
		Map<String, String> extractedChannels = new LinkedHashMap<>();

		if (dStarInfo != null) {
			List<String> channelValues = extractChannelValues(dStarInfo);

			// If exactly 4 values are found, store them directly
			if (channelValues.size() == 4) {
				for (int i = 0; i < 4; i++) {
					extractedChannels.put("Channel" + (i + 1), channelValues.get(i));
				}
			} else {
				// If there are not exactly 4 values, look for the "diff(s)" pattern
				Pattern diffPattern = Pattern.compile(
						"\\(\\s*(\\d+ diff\\(s\\))\\s*,\\s*(\\d+ diff\\(s\\))\\s*,\\s*(\\d+ diff\\(s\\))\\s*,\\s*(\\d+ diff\\(s\\))\\s*\\)");
				Matcher diffMatcher = diffPattern.matcher(dStarInfo);
				if (diffMatcher.find()) {
					for (int i = 0; i < 4; i++) {
						String diffValue = diffMatcher.group(i + 1);
						extractedChannels.put("Channel" + (i + 1), diffValue);
					}
				}
			}
		}
		return extractedChannels;
	}

	private static String extractSignalName(String line) {
		
		if (line.contains("S> ADBUF_RAM_STARTV")) {
			// String extracted = line.replaceAll(".*(!.*)$", "$1");

			String extracted = line.replaceAll(".*(!.*)$", "$1").replaceFirst("^!", "");
			////System.out.println(extracted);
			adbuf_ram_startv_signalName = extracted;
		}

		line = line.substring(3).trim();

		int symbolIndex = line.indexOf('<');
		if (symbolIndex == -1)
			symbolIndex = line.indexOf('>');
		if (symbolIndex == -1)
			symbolIndex = line.indexOf("<=");
		if (symbolIndex == -1)
			symbolIndex = line.indexOf(">=");
		if (symbolIndex == -1)
			symbolIndex = line.indexOf("=");

		String signalName = null;
		if (symbolIndex != -1) {
			signalName = line.substring(0, symbolIndex).trim();
		}

		int exclamationIndex = line.indexOf('!');
		if (exclamationIndex != -1) {
			String additionalName = line.substring(exclamationIndex + 1).trim();
			if (!additionalName.isEmpty()) {
				signalName = additionalName;
			}
		}
		if (signalName != null) {
			if (signalName.contains("adbuf_ram_startv")) {
				signalName = adbuf_ram_startv_signalName;
				adbuf_ram_startv_signalName_flag = true;
//				//System.out.println("ADBUF "+signalName);
			}
		}

		return signalName;
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
			if (lessThanIndex != 0 && lessThanIndex != line.length() - 1 && line.charAt(lessThanIndex - 1) != '=') {
				separatorIndex = lessThanIndex;
			}
		} else if (line.contains(">")) {
			int greaterThanIndex = line.indexOf(">");
			if (greaterThanIndex != 0 && greaterThanIndex != line.length() - 1
					&& line.charAt(greaterThanIndex - 1) != '=') {
				separatorIndex = greaterThanIndex;
			}
		} else if (line.contains("=")) {
			int equalsIndex = line.indexOf("=");
			if (equalsIndex != 0 && equalsIndex != line.length() - 1 && line.charAt(equalsIndex - 1) != '<'
					&& line.charAt(equalsIndex - 1) != '>') {
				separatorIndex = equalsIndex;
			}
		}

		int questionIndex = line.indexOf('?');

		if (separatorIndex != -1 && questionIndex != -1 && separatorIndex < questionIndex) {
			return line.substring(separatorIndex + 1, questionIndex).trim().replaceAll("[=<>]", "");
		}

		return null;
	}

	// File Wait Process
	public static void waitForFileRelease(File file, int checkIntervalMillis) {
		boolean fileInUse = true;

		while (fileInUse) {
			try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
					FileChannel channel = raf.getChannel();
					FileLock lock = channel.tryLock()) {

				if (lock != null) {
					// Lock acquired - file is free
					fileInUse = false;
					lock.release(); // Always release the lock
				}

			} catch (Exception e) {
				// Lock not available - file is still in use
			}

			if (fileInUse) {
				try {
					Thread.sleep(checkIntervalMillis); // Wait before retrying
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
//                    ////System.out.println("Interrupted while waiting for file release.");
					break;
				}
			}
		}

//        ////System.out.println("File is now free to use.");
	}

}
