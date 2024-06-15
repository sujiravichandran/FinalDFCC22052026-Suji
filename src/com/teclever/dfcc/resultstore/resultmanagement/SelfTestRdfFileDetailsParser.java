package com.teclever.dfcc.resultstore.resultmanagement;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.teclever.dfcc.resultstore.configuration.ResultStoreConnection;
import com.teclever.dfcc.resultstore.dto.BoardDto;
import com.teclever.dfcc.resultstore.dto.SelfTestRdfFileDetailsDto;

public class SelfTestRdfFileDetailsParser {
	
	
	//SAVE API
    public static void saveSelfTestToMongoDB(String testRunId, String filePath) {
        List<SelfTestRdfFileDetailsDto> selfTestRdfFileDetailsList = parseProjectDetails(filePath);
        String collectionName = testRunId + "_" + getCollectionNameFromFilePath(filePath);
        MongoCollection<Document> collection = ResultStoreConnection.getDatabase().getCollection(collectionName);
        List<BoardDto> boardDtoList = BoardParser.parseBoardContext(filePath);

        MongoCollection<Document> collection1 = ResultStoreConnection.getDatabase().getCollection(testRunId);

        for (SelfTestRdfFileDetailsDto selfTestRdfFileDetails : selfTestRdfFileDetailsList) {
            Document existingDoc = collection.find(eq("resultDataFile", selfTestRdfFileDetails.getResultDataFile())).first();
            if (existingDoc == null) {
                Document doc = new Document("project", selfTestRdfFileDetails.getProject())
                        .append("systemDatabaseFile", selfTestRdfFileDetails.getSystemDatabaseFile())
                        .append("userDatabaseFile", selfTestRdfFileDetails.getUserDatabaseFile())
                        .append("systemMacroFile", selfTestRdfFileDetails.getSystemMacroFile())
                        .append("userMacroFile", selfTestRdfFileDetails.getUserMacroFile())
                        .append("testPlanFile", selfTestRdfFileDetails.getTestPlanFile())
                        .append("resultDataFile", selfTestRdfFileDetails.getResultDataFile())
                        .append("dateOfExecution", selfTestRdfFileDetails.getDateOfExecution())
                        .append("timeOfExecution", selfTestRdfFileDetails.getTimeOfExecution())
                        .append("board", selfTestRdfFileDetails.getBoard())
                        .append("failedBoard", selfTestRdfFileDetails.getFailedBoard());

                collection.insertOne(doc);

                Document selfTestRdfFileInfoDoc = new Document("testPlanFile", selfTestRdfFileDetails.getTestPlanFile())
                        .append("testRunId", testRunId)
                        .append("time", selfTestRdfFileDetails.getDateOfExecution() + " " + selfTestRdfFileDetails.getTimeOfExecution())
                        .append("rdfFile", getCollectionName(selfTestRdfFileDetails.getResultDataFile()))
                        .append("RefObjectId", doc.getObjectId("_id"))
                        .append("RefCollectionName", testRunId + "_" + getCollectionName(selfTestRdfFileDetails.getResultDataFile()));

                collection1.insertOne(selfTestRdfFileInfoDoc);
            }
        }

        Map<String, ObjectId> boardObjectIdMap = new LinkedHashMap<>();
        for (BoardDto boardDto : boardDtoList) {
            Document doc = new Document("macname", boardDto.getMacname());

            if (boardDto.getBoardName() != null) {
                doc.append("board", boardDto.getBoardName());
            }
//            if (stepDto.getInput() != null) {
//                doc.append("input", stepDto.getInput());
            //}
            if (boardDto.getdStarInfo() != null) {
                doc.append("dStarInfo", boardDto.getdStarInfo());
            }
            if (boardDto.getReadingInfo() != null && !boardDto.getReadingInfo().isEmpty()) {
                doc.append("readingInfo", boardDto.getReadingInfo());
            }
            if (boardDto.getUnit() != null) {
                doc.append("unit", boardDto.getUnit());
            }
            if (boardDto.getFaultyChannel() != null) {
                doc.append("faultyChannel", boardDto.getFaultyChannel());
                if (boardDto.getBoardName() != null) {
                    doc.append("board", boardDto.getBoardName());
                }
                if (boardDto.getExpectedValue() != null) {
                    doc.append("expectedValue", boardDto.getExpectedValue());
                }
            }
            collection.insertOne(doc);

            ObjectId objectId = doc.getObjectId("_id");
            if (objectId != null && boardDto.getBoardName() != null) {
            	boardObjectIdMap.put(boardDto.getBoardName(), objectId);
            }
        }

        for (SelfTestRdfFileDetailsDto selfTestRdfFileDetails : selfTestRdfFileDetailsList) {
            Document existingDoc = collection.find(Filters.eq("resultDataFile", selfTestRdfFileDetails.getResultDataFile())).first();
            if (existingDoc != null) {
                ObjectId objectId = existingDoc.getObjectId("_id");

                Map<String, ObjectId> boardObjectIdMap1 = new HashMap<>();
                Map<String, ObjectId> failedBoardObjectIdMap = new HashMap<>();
                for (BoardDto boardDto : boardDtoList) {
                    if (boardDto.getResultDataFile().equals(selfTestRdfFileDetails.getResultDataFile())) {
                        if (boardDto.getBoardName() != null) {
                            ObjectId boardObjectId = boardObjectIdMap.get(boardDto.getBoardName());
                            if (boardObjectId != null) {
                                boardObjectIdMap.put(boardDto.getBoardName(), boardObjectId);
                            }
                        }
                        if (boardDto.getdStarInfo() != null) {
                            ObjectId failedBoardObjectId = boardObjectIdMap.get(boardDto.getBoardName());
                            if (failedBoardObjectId != null) {
                                failedBoardObjectIdMap.put(boardDto.getBoardName(), failedBoardObjectId);
                            }
                        }
                    }
                }

                collection.updateOne(
                        Filters.eq("_id", objectId),
                        new Document("$set", new Document("board", boardObjectIdMap).append("failedBoard", failedBoardObjectIdMap))
                );
            }
        }
    }

    private static List<SelfTestRdfFileDetailsDto> parseProjectDetails(String filePath) {
        List<SelfTestRdfFileDetailsDto> projectDetailsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String project = null;
            String systemDatabaseFile = null;
            String userDatabaseFile = null;
            String systemMacroFile = null;
            String userMacroFile = null;
            String testPlanFile = null;
            String resultDataFile = null;
            String dateOfExecution = null;
            String timeOfExecution = null;
            List<String> board = null;
            List<String> failedBoard = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Z>")) {
                    if (line.length() <= 3) {
                        continue;
                    }

                    int colonIndex = line.indexOf(':');
                    if (colonIndex != -1) {
                        String attributeName = line.substring(3, colonIndex).trim();
                        String attributeValue = line.substring(colonIndex + 1).trim();

                        switch (attributeName) {
                            case "Project":
                                project = attributeValue;
                                break;
                            case "System database file":
                                systemDatabaseFile = attributeValue;
                                break;
                            case "User database file":
                                userDatabaseFile = attributeValue;
                                break;
                            case "System macro file":
                                systemMacroFile = attributeValue;
                                break;
                            case "User macro file":
                                userMacroFile = attributeValue;
                                break;
                            case "Test plan file":
                                testPlanFile = attributeValue;
                                break;
                            case "Result data file":
                                resultDataFile = attributeValue;
                                break;
                            case "Date of execution":
                                dateOfExecution = attributeValue;
                                break;
                            case "Time of execution":
                                timeOfExecution = attributeValue;
                                break;
                        }
                    }
                } else if (line.startsWith("D*>")) {
                    continue;
                } else {
                    if (resultDataFile != null) {
                        resultDataFile += line.trim();
                    }
                }

                if (dateOfExecution != null && timeOfExecution != null) {
                    SelfTestRdfFileDetailsDto projectDetails = new SelfTestRdfFileDetailsDto(
                            project, systemDatabaseFile, userDatabaseFile, systemMacroFile,
                            userMacroFile, testPlanFile, resultDataFile, dateOfExecution, timeOfExecution, board, failedBoard);
                    projectDetailsList.add(projectDetails);

                    project = null;
                    systemDatabaseFile = null;
                    userDatabaseFile = null;
                    systemMacroFile = null;
                    userMacroFile = null;
                    testPlanFile = null;
                    resultDataFile = null;
                    dateOfExecution = null;
                    timeOfExecution = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return projectDetailsList;
    }

    private static String getCollectionName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.replace(" ", "_").replace(".", "_").replace("\\", "_");
    }

    private static String getCollectionNameFromFilePath(String filePath) {
        String[] parts = filePath.split("\\\\");
        String fileName = parts[parts.length - 1];
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

}
