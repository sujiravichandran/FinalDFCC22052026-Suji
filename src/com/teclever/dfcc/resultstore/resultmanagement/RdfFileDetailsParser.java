package com.teclever.dfcc.resultstore.resultmanagement;

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
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.advanceddataanalysis.DataAnalysis1553_BManagement;
import com.teclever.dfcc.resultstore.configuration.ResultStoreConnection;
import com.teclever.dfcc.resultstore.dto.RdfFileDetailsDto;
import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.Debug;


public class RdfFileDetailsParser {
	static ObjectId o;

	//SAVE API
	public static ObjectId saveProjectDetailsToMongoDB(String sessionId, String filePath) throws InterruptedException {
		
		
		
	    List<RdfFileDetailsDto> rdfFileDetailsList = parseProjectDetails(filePath);
	  
	    
//	    ////System.out.println("Rdf File Details List :"+rdfFileDetailsList.size());
	    
	    //Mani Detail Data Change 26-AUG
	    String collectionName = sessionId + "_" + getCollectionNameFromFilePath(filePath)+"_"+DFCCConstant.testFileResultId;
	    MongoCollection<Document> collection = ResultStoreConnection.getDatabase().getCollection(collectionName);
	    
	    
	    List<StepDto> stepDtoList = StepParser.parseStepContextNEW(filePath);
	    String[] rdfFileNameArray = filePath.split(File.separator);
	    DFCCConstant.fileNameRdf = rdfFileNameArray[rdfFileNameArray.length-1];
	 
	    
	    //For Manual Testing Added 
		DataAnalysis1553_BManagement dataAnalysis1553_BManagement = new DataAnalysis1553_BManagement();
		if (StateMachine.currentSessionDetails.getUutId().equals("UUT3")
				|| StateMachine.currentSessionDetails.getUutId().equals("UUT2")) {
			if (filePath.contains("1553_tran_final.rdf")) {
				dataAnalysis1553_BManagement.addTheRequiredStepsFor1553(stepDtoList, DFCCConstant.stageId);
			}
		} else {
			if (filePath.contains("1553_tran_mod.rdf")) {
				dataAnalysis1553_BManagement.addTheRequiredStepsFor1553(stepDtoList, DFCCConstant.stageId);
			}
		}
	    
	    
	    //For Deviation Added
	    AdvancedDataAnalysisManagement advancedDataAnalysisManagement = new AdvancedDataAnalysisManagement();
	    advancedDataAnalysisManagement.addRDFFilesDetails(stepDtoList);
	    
	    
//	    ////System.out.println("StepDtoList Size   -->::"+stepDtoList.size());
	    
	    

	    MongoCollection<Document> collection1 = ResultStoreConnection.getDatabase().getCollection(sessionId);
	    ObjectId rdfFileInfoObjectId = null; // Variable to store the ObjectId


	    for (RdfFileDetailsDto rdfFileDetails : rdfFileDetailsList) {
	        Document doc = new Document("project", rdfFileDetails.getProject())
	                .append("systemDatabaseFile", rdfFileDetails.getSystemDatabaseFile())
	                .append("userDatabaseFile", rdfFileDetails.getUserDatabaseFile())
	                .append("systemMacroFile", rdfFileDetails.getSystemMacroFile())
	                .append("userMacroFile", rdfFileDetails.getUserMacroFile())
	                .append("testPlanFile", rdfFileDetails.getTestPlanFile())
	                .append("resultDataFile", rdfFileDetails.getResultDataFile())
	                .append("dateOfExecution", rdfFileDetails.getDateOfExecution())
	                .append("timeOfExecution", rdfFileDetails.getTimeOfExecution())
	                .append("step", rdfFileDetails.getStep())
	                .append("failedStep", rdfFileDetails.getFailedStep());

	        collection.insertOne(doc);

	        Document rdfFileInfoDoc = new Document("testPlanFile", rdfFileDetails.getTestPlanFile())
	                .append("sessionId", sessionId)
	                .append("time", rdfFileDetails.getDateOfExecution() + " " + rdfFileDetails.getTimeOfExecution())
	                .append("rdfFile", getCollectionName(rdfFileDetails.getResultDataFile()))
	                .append("RefObjectId", doc.getObjectId("_id"))
	                .append("RefCollectionName", sessionId + "_" + getCollectionName(rdfFileDetails.getResultDataFile()));

	        collection1.insertOne(rdfFileInfoDoc);
            rdfFileInfoObjectId = rdfFileInfoDoc.getObjectId("_id"); // Capture the ObjectId
	        o=doc.getObjectId("_id");
	    }

	    Map<String, ObjectId> stepObjectIdMap = new LinkedHashMap<>();
	    for (StepDto stepDto : stepDtoList) {
	        Document doc = new Document("tpgph", stepDto.getTpgph());

	        if (stepDto.getStep() != null) {
	            doc.append("step", stepDto.getStep());
	        }
	        if (stepDto.getInput() != null) {
	            doc.append("input", stepDto.getInput());
	        }
	        if (stepDto.getdStarInfo() != null) {
	            doc.append("dStarInfo", stepDto.getdStarInfo());
	        }
	        if (stepDto.getReadingInfo() != null && !stepDto.getReadingInfo().isEmpty()) {
	            doc.append("readingInfo", stepDto.getReadingInfo());
	        }
	        if (stepDto.getUnit() != null) {
	            doc.append("unit", stepDto.getUnit());
	        }
	        if (stepDto.getFaultyChannel() != null) {
	            doc.append("faultyChannel", stepDto.getFaultyChannel());
	            if (stepDto.getSignalName() != null) {
	                doc.append("signalName", stepDto.getSignalName());
	            }
	            if (stepDto.getExpectedValue() != null) {
	                doc.append("expectedValue", stepDto.getExpectedValue());
	            }
	        }
	        if (stepDto.getFaultySRU() == null) {
	           doc.append("faultySRU", "No Data Found");
	        }else {
	        	 doc.append("faultySRU", stepDto.getFaultySRU());
	        }
	        collection.insertOne(doc);

	        ObjectId objectId = doc.getObjectId("_id");
	        if (objectId != null && stepDto.getStep() != null) {
	            stepObjectIdMap.put(stepDto.getStep(), objectId);
	        }
	    }

	    for (RdfFileDetailsDto rdfFileDetails : rdfFileDetailsList) {
	        //Document existingDoc = collection.find(Filters.eq("resultDataFile", rdfFileDetails.getResultDataFile())).first();
	        //if (existingDoc != null) {
	            ObjectId objectId = o;

	            Map<String, ObjectId> stepObjectIdMap1 = new HashMap<>();
	            Map<String, ObjectId> failedStepObjectIdMap = new HashMap<>();
	            for (StepDto stepDto : stepDtoList) {
	                if (stepDto.getResultDataFile().equals(rdfFileDetails.getResultDataFile())) {
	                    if (stepDto.getStep() != null) {
	                        ObjectId stepObjectId = stepObjectIdMap.get(stepDto.getStep());
	                        if (stepObjectId != null) {
	                            stepObjectIdMap.put(stepDto.getStep(), stepObjectId);
	                        }
	                    }
	                    if (stepDto.getdStarInfo() != null) {
	                        ObjectId failedStepObjectId = stepObjectIdMap.get(stepDto.getStep());
	                        if (failedStepObjectId != null) {
	                            failedStepObjectIdMap.put(stepDto.getStep(), failedStepObjectId);
	                        }
	                    }
	                }
	            }
	            
//	            ////System.out.println("Failed Step Object Id Map  size :"+failedStepObjectIdMap.size());
//	            ////System.out.println("step Object Id Map   Size   :"+stepObjectIdMap.size());

	            collection.updateOne(
	                    Filters.eq("_id", objectId),
	                    new Document("$set", new Document("step", stepObjectIdMap).append("failedStep", failedStepObjectIdMap))
	            );
	        }
	    Debug.printDebug("Returned Object Id------>>>>>    " + rdfFileInfoObjectId );
	    
	    
	    
	    
	    return rdfFileInfoObjectId; // Return the ObjectId

	    }
	//}


    
//  //SAVE API
//  	public static ObjectId saveProjectDetailsToMongoDB(String sessionId, String filePath) {
//  	    List<RdfFileDetailsDto> rdfFileDetailsList = parseProjectDetails(filePath);
//  	    String collectionName = sessionId + "_" + getCollectionNameFromFilePath(filePath);
//  	    MongoCollection<Document> collection = ResultStoreConnection.getDatabase().getCollection(collectionName);
//  	    List<StepDto> stepDtoList = StepParser.parseStepContext(filePath);
//
//  	    MongoCollection<Document> collection1 = ResultStoreConnection.getDatabase().getCollection(sessionId);
//  	    ObjectId rdfFileInfoObjectId = null; // Variable to store the ObjectId
//
//  	    for (RdfFileDetailsDto rdfFileDetails : rdfFileDetailsList) {
//  	        Document existingDoc = collection.find(eq("resultDataFile", rdfFileDetails.getResultDataFile())).first();
//  	        if (existingDoc == null) {
//  	            Document doc = new Document("project", rdfFileDetails.getProject())
//  	                    .append("systemDatabaseFile", rdfFileDetails.getSystemDatabaseFile())
//  	                    .append("userDatabaseFile", rdfFileDetails.getUserDatabaseFile())
//  	                    .append("systemMacroFile", rdfFileDetails.getSystemMacroFile())
//  	                    .append("userMacroFile", rdfFileDetails.getUserMacroFile())
//  	                    .append("testPlanFile", rdfFileDetails.getTestPlanFile())
//  	                    .append("resultDataFile", rdfFileDetails.getResultDataFile())
//  	                    .append("dateOfExecution", rdfFileDetails.getDateOfExecution())
//  	                    .append("timeOfExecution", rdfFileDetails.getTimeOfExecution())
//  	                    .append("step", rdfFileDetails.getStep())
//  	                    .append("failedStep", rdfFileDetails.getFailedStep());
//
//  	            collection.insertOne(doc);
//
//  	            Document rdfFileInfoDoc = new Document("testPlanFile", rdfFileDetails.getTestPlanFile())
//  	                    .append("sessionId", sessionId)
//  	                    .append("time", rdfFileDetails.getDateOfExecution() + " " + rdfFileDetails.getTimeOfExecution())
//  	                    .append("rdfFile", getCollectionName(rdfFileDetails.getResultDataFile()))
//  	                    .append("RefObjectId", doc.getObjectId("_id"))
//  	                    .append("RefCollectionName", sessionId + "_" + getCollectionName(rdfFileDetails.getResultDataFile()));
//
//  	            collection1.insertOne(rdfFileInfoDoc);
//  	            rdfFileInfoObjectId = rdfFileInfoDoc.getObjectId("_id"); // Capture the ObjectId
//  	        }
//  	    }
//
//  	    Map<String, ObjectId> stepObjectIdMap = new LinkedHashMap<>();
//  	    for (StepDto stepDto : stepDtoList) {
//  	        Document doc = new Document("tpgph", stepDto.getTpgph());
//
//  	        if (stepDto.getStep() != null) {
//  	            doc.append("step", stepDto.getStep());
//  	        }
//  	        if (stepDto.getInput() != null) {
//  	            doc.append("input", stepDto.getInput());
//  	        }
//  	        if (stepDto.getdStarInfo() != null) {
//  	            doc.append("dStarInfo", stepDto.getdStarInfo());
//  	        }
//  	        if (stepDto.getReadingInfo() != null && !stepDto.getReadingInfo().isEmpty()) {
//  	            doc.append("readingInfo", stepDto.getReadingInfo());
//  	        }
//  	        if (stepDto.getUnit() != null) {
//  	            doc.append("unit", stepDto.getUnit());
//  	        }
//  	        if (stepDto.getFaultyChannel() != null) {
//  	            doc.append("faultyChannel", stepDto.getFaultyChannel());
//  	            if (stepDto.getSignalName() != null) {
//  	                doc.append("signalName", stepDto.getSignalName());
//  	            }
//  	            if (stepDto.getExpectedValue() != null) {
//  	                doc.append("expectedValue", stepDto.getExpectedValue());
//  	            }
//  	        }
//  	        collection.insertOne(doc);
//
//  	        ObjectId objectId = doc.getObjectId("_id");
//  	        if (objectId != null && stepDto.getStep() != null) {
//  	            stepObjectIdMap.put(stepDto.getStep(), objectId);
//  	        }
//  	    }
//
//  	    for (RdfFileDetailsDto rdfFileDetails : rdfFileDetailsList) {
//  	        Document existingDoc = collection.find(Filters.eq("resultDataFile", rdfFileDetails.getResultDataFile())).first();
//  	        if (existingDoc != null) {
//  	            ObjectId objectId = existingDoc.getObjectId("_id");
//
//  	            Map<String, ObjectId> stepObjectIdMap1 = new HashMap<>();
//  	            Map<String, ObjectId> failedStepObjectIdMap = new HashMap<>();
//  	            for (StepDto stepDto : stepDtoList) {
//  	                if (stepDto.getResultDataFile().equals(rdfFileDetails.getResultDataFile())) {
//  	                    if (stepDto.getStep() != null) {
//  	                        ObjectId stepObjectId = stepObjectIdMap.get(stepDto.getStep());
//  	                        if (stepObjectId != null) {
//  	                            stepObjectIdMap.put(stepDto.getStep(), stepObjectId);
//  	                        }
//  	                    }
//  	                    if (stepDto.getdStarInfo() != null) {
//  	                        ObjectId failedStepObjectId = stepObjectIdMap.get(stepDto.getStep());
//  	                        if (failedStepObjectId != null) {
//  	                            failedStepObjectIdMap.put(stepDto.getStep(), failedStepObjectId);
//  	                        }
//  	                    }
//  	                }
//  	            }
//
//  	            collection.updateOne(
//  	                    Filters.eq("_id", objectId),
//  	                    new Document("$set", new Document("step", stepObjectIdMap).append("failedStep", failedStepObjectIdMap))
//  	            );
//  	        }
//  	    }
//  	    
//  	    return rdfFileInfoObjectId; // Return the ObjectId
//  	}
    
    
    
    private static List<RdfFileDetailsDto> parseProjectDetails(String filePath) {
        List<RdfFileDetailsDto> projectDetailsList = new ArrayList<>();
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
            List<String> step = null;
            List<String> failedStep = null;

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
                    RdfFileDetailsDto projectDetails = new RdfFileDetailsDto(
                            project, systemDatabaseFile, userDatabaseFile, systemMacroFile,
                            userMacroFile, testPlanFile, resultDataFile, dateOfExecution, timeOfExecution, step, failedStep);
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
        // Split using both forward slash (for Linux) and backslash (for Windows)
        String[] parts = filePath.split("[/\\\\]");
        String fileName = parts[parts.length - 1];
        return fileName.replaceAll("[\\\\/:*?\"<>|;]", "_");
    }

}