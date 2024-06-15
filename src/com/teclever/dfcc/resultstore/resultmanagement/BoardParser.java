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
import com.teclever.dfcc.resultstore.dto.BoardDto;

public class BoardParser {

	public static List<BoardDto> parseBoardContext(String filePath) {

	    List<BoardDto> boardList = new ArrayList<>();
	    List<BoardDto> failedBoardList = new ArrayList<>();
	    String macname = null;
	    List<String> readingInfo = null;
	    String dStarInfo = null;
	    String testPlanFile = null;
	    String resultDataFile = null;
	    String unit = null;
	    Map<String, String> faultyChannel = null;
	    String expectedValue = null;
	    String boardName = null;
	    boolean isAfterBoard = false; // Flag to indicate whether the line is after "S> brd"

	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	        String line;

	        while ((line = reader.readLine()) != null) {
	            if (line.startsWith("S>") && line.contains("macname")) {
	                // Store the previous board if exists
	                if (boardName != null) {
	                    BoardDto boardDto = createBoardDto(macname, readingInfo, dStarInfo, testPlanFile,
	                            resultDataFile, unit, faultyChannel, expectedValue, boardName);
	                    boardList.add(boardDto); // Add board to boardList

	                    if (dStarInfo != null) {
	                        failedBoardList.add(boardDto); // Add failed board to failedBoardList
	                    }
	                }

	                // Reset board-related variables when encountering a new macname
	                macname = extractMacname(line);
	                readingInfo = new ArrayList<>();
	                dStarInfo = null;
	                unit = null;
	                faultyChannel = null;
	                expectedValue = null;
	                boardName = null;
	                isAfterBoard = false;

	            } else if (line.startsWith("S>") && line.contains("brd")) {
	                // Extract boardName from the line
	                int startIndex = line.indexOf("brd") + 3;
	                int endIndex = startIndex;

	                // Find where boardName ends (stop at first non-alphanumeric character)
	                while (endIndex < line.length() && Character.isLetterOrDigit(line.charAt(endIndex))) {
	                    endIndex++;
	                }

	                String candidateBoardName = "brd" + line.substring(startIndex, endIndex);

	                // Check if this boardName has already been added
	                boolean boardExists = false;
	                for (BoardDto boardDto : boardList) {
	                    if (boardDto.getBoardName().equals(candidateBoardName)) {
	                        boardExists = true;
	                        break;
	                    }
	                }

	                if (!boardExists) {
	                    boardName = candidateBoardName;
	                }

	                readingInfo = new ArrayList<>();
	                dStarInfo = null;
	                unit = null;
	                faultyChannel = null;
	                expectedValue = null;
	                isAfterBoard = true; // Set flag to true after encountering "S> brd"
	            } else if (line.startsWith("Z>") && line.contains("Test plan file")) {
	                testPlanFile = extractTestPlanFileName(line);
	            } else if (line.startsWith("Z>") && line.contains("Result data file")) {
	                resultDataFile = extractResultDataFileName(line);
	            } else if (line.startsWith("D*>")) {
	                // Capture dStarInfo line
	                dStarInfo = line.substring(3).trim();
	                unit = extractUnit(dStarInfo);
	                faultyChannel = extractFaultyChannels(dStarInfo);

	            } else if (line.startsWith("B>")) {
	                // Stop processing lines when encountering B> after a board
	                isAfterBoard = false;

	                // Store the board if boardName is not null
	                if (boardName != null) {
	                    // Check if boardName already exists in boardList
	                    boolean boardExists = false;
	                    for (BoardDto boardDto : boardList) {
	                        if (boardDto.getBoardName().equals(boardName)) {
	                            boardExists = true;
	                            break;
	                        }
	                    }

	                    if (!boardExists) {
	                        BoardDto boardDto = createBoardDto(macname, readingInfo, dStarInfo, testPlanFile,
	                                resultDataFile, unit, faultyChannel, expectedValue, boardName);
	                        boardList.add(boardDto); // Add board to boardList

	                        if (dStarInfo != null) {
	                            failedBoardList.add(boardDto); // Add failed board to failedBoardList
	                        }
	                    }
	                }
	            } else if ((line.startsWith("S>") ||  line.startsWith("R>"))&&boardName!=null && isAfterBoard) {
	                // Capture reading information (S> lines,  R> lines) after a board until B> is found
	                if (line.startsWith("R>") && line.contains("(")) {
	                    if (readingInfo == null) {
	                        readingInfo = new ArrayList<>();
	                    }
	                    readingInfo.add(line.substring(3).trim());
	                } else if (line.startsWith("S>")) {
	                    if (readingInfo == null) {
	                        readingInfo = new ArrayList<>();
	                    }
	                    readingInfo.add(line.substring(3).trim());
	                }
	            }
	        }

	        // Store the last board if exists
	        if (boardName != null) {
	            // Check if boardName already exists in boardList
	            boolean boardExists = false;
	            for (BoardDto boardDto : boardList) {
	                if (boardDto.getBoardName().equals(boardName)) {
	                    boardExists = true;
	                    break;
	                }
	            }

	            if (!boardExists) {
	                BoardDto boardDto = createBoardDto(macname, readingInfo, dStarInfo, testPlanFile, resultDataFile, unit,
	                        faultyChannel, expectedValue, boardName);
	                boardList.add(boardDto); // Add board to boardList

	                if (dStarInfo != null) {
	                    failedBoardList.add(boardDto); // Add failed board to failedBoardList
	                }
	            }
	        }

	        // Print steps and failed steps with counts
	        int totalBoards = boardList.size();
	        int failedBoards = failedBoardList.size();
	        System.out.println("Total Boards: " + totalBoards);
	        System.out.println("Total Failed Boards: " + failedBoards);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return boardList;
	}



	private static BoardDto createBoardDto(String macname, List<String> readingInfo, String dStarInfo,
			String testPlanFile, String resultDataFile, String unit, Map<String, String> faultyChannel,
			String expectedValue, String boardName) {
		BoardDto boardDto = new BoardDto();
		boardDto.setTestPlanFile(testPlanFile);
		boardDto.setResultDataFile(resultDataFile);
		boardDto.setMacname(macname);
		boardDto.setReadingInfo(new ArrayList<>(readingInfo));
		boardDto.setdStarInfo(dStarInfo);
		boardDto.setUnit(unit);
		boardDto.setFaultyChannel(faultyChannel);
		boardDto.setExpectedValue(expectedValue);
		boardDto.setBoardName(boardName);

		System.out.println("Test Plan File: " + boardDto.getTestPlanFile());
		System.out.println("Result Data File: " + boardDto.getResultDataFile());
		System.out.println("macname: " + boardDto.getMacname());
		System.out.println("boardName: " + boardDto.getBoardName());
		System.out.println("DStarInfo: " + boardDto.getdStarInfo());
		// System.out.println("ReadingInfo: " + boardDto.getReadingInfo());
		System.out.println("unit: " + boardDto.getUnit());
		System.out.println("faultyChannel: " + boardDto.getFaultyChannel());
		// System.out.println("expectedValue: " + boardDto.getExpectedValue());
		System.out.println("---------------------------------------");

		return boardDto;
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

	private static String extractMacname(String line) {
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

	private static String extractBoardName(String line) {

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

		if (separatorIndex != -1 && questionIndex != -1) {
			return line.substring(separatorIndex + 1, questionIndex).trim().replaceAll("[=<>]", ""); // Remove symbols
		}

		return null;
	}

}
