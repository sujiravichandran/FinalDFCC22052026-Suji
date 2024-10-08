package com.teclever.dfcc.resultstore.resultmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.teclever.dfcc.resultstore.configuration.ResultStoreConnection;
import com.teclever.dfcc.resultstore.dto.ResultDto;

public class ResultManagement {

	//GET API
	public static List<ResultDto> getResult(String sessionId, ObjectId specificObjectId) {
	    List<ResultDto> resultList = new ArrayList<>();
	    MongoDatabase database = ResultStoreConnection.getDatabase();

	    MongoCollection<Document> rdfFileInfoCollection = database.getCollection(sessionId);
	    Document rdfFileInfoDoc = rdfFileInfoCollection.find(and(eq("sessionId", sessionId), eq("_id", specificObjectId))).first();

	    if (rdfFileInfoDoc != null) {
	        ObjectId refObjectId = rdfFileInfoDoc.getObjectId("RefObjectId");
	        String refCollectionName = rdfFileInfoDoc.getString("RefCollectionName");

	        String collectionName = getCollectionName(refCollectionName);

	        MongoCollection<Document> resultDataCollection = database.getCollection(collectionName);
	        Document resultDataDoc = resultDataCollection.find(eq("_id", refObjectId)).first();

	        if (resultDataDoc != null) {
	            String resultDataFile = resultDataDoc.getString("resultDataFile");
	            String[] resultDataParts = resultDataFile.split("/");
	            String fileName = resultDataParts[resultDataParts.length - 1];

	            // Get the failedStep map
	            Map<String, ObjectId> failedStepMap = resultDataDoc.get("failedStep", Map.class);
	            
	            // Check if failedStepMap is empty
	            if (failedStepMap != null && !failedStepMap.isEmpty()) {
	                // Iterate through each failedStep ObjectId and fetch details for each step
	                for (Map.Entry<String, ObjectId> entry : failedStepMap.entrySet()) {
	                    ObjectId stepObjectId = entry.getValue();
	                    Document stepDoc = resultDataCollection.find(eq("_id", stepObjectId)).first();
	                    if (stepDoc != null) {
	                        String stepName = entry.getKey();
	                        String measuredValue = null;
	                        String faultyChannel = null;

	                        // Extract faultyChannel and measuredValue
	                        Map<String, String> faultyChannels = stepDoc.get("faultyChannel", Map.class);
	                        if (faultyChannels != null) {
	                            for (Map.Entry<String, String> faultyChannelEntry : faultyChannels.entrySet()) {
	                                faultyChannel = faultyChannelEntry.getKey();
	                                measuredValue = faultyChannelEntry.getValue();
	                            }
	                        }

	                        String tpgph = stepDoc.getString("tpgph");
	                        String unit = stepDoc.getString("unit");
	                        String signalName = stepDoc.getString("signalName");
	                        String expectedValue = stepDoc.getString("expectedValue");
	                        String faultySRU = stepDoc.getString("faultySRU");

	                        ResultDto resultDto = new ResultDto(tpgph, stepName, expectedValue, measuredValue, unit, signalName, faultyChannels, fileName,faultySRU);
	                        resultList.add(resultDto);
	                    }
	                }
	            } else {
	                    // If failedStepMap is empty, fetch all documents from resultDataCollection
	                    List<Document> allSteps = resultDataCollection.find().into(new ArrayList<>());

	                    // Flag to skip the first document
	                    boolean skipFirstDocument = true;

	                    for (Document stepDoc : allSteps) {
	                        if (skipFirstDocument) {
	                            skipFirstDocument = false; // Skip this iteration and move to the next document
	                            continue;
	                        }

	                        String stepName = null; 
	                        String measuredValue = null;
	                        String faultyChannel = null;

	                        // Extract faultyChannel and measuredValue
	                        Map<String, String> faultyChannels = stepDoc.get("faultyChannel", Map.class);
	                        if (faultyChannels != null) {
	                            for (Map.Entry<String, String> faultyChannelEntry : faultyChannels.entrySet()) {
	                                faultyChannel = faultyChannelEntry.getKey();
	                                measuredValue = faultyChannelEntry.getValue();
	                            }
	                        }

	                        String tpgph = stepDoc.getString("tpgph");
	                        String unit = stepDoc.getString("unit");
	                        String signalName = stepDoc.getString("signalName");
	                        String expectedValue = stepDoc.getString("expectedValue");
	                        String faultySRU = stepDoc.getString("faultySRU");

	                        ResultDto resultDto = new ResultDto(tpgph, stepName, expectedValue, measuredValue, unit, signalName, faultyChannels, fileName,faultySRU);
	                        resultList.add(resultDto);
	                    }
	                }

	        } else {
	            System.out.println("Document not found in collection: " + collectionName);
	        }
	    } else {
	        System.out.println("No documents found in rdf_file_info collection for testRunId: " + sessionId);
	    }

	    return resultList;
	}


    // Method to derive the collection name from the resultDataFile value
    private static String getCollectionName(String resultDataFile) {
        // Find the index of "rdf" in the resultDataFile
        int rdfIndex = resultDataFile.indexOf("rdf");

        // If "rdf" is found in the resultDataFile
        if (rdfIndex != -1) {
            // Replace "_" with "." if it's immediately before "rdf"
            if (rdfIndex > 0 && resultDataFile.charAt(rdfIndex - 1) == '_') {
                resultDataFile = resultDataFile.substring(0, rdfIndex - 1) + "." + resultDataFile.substring(rdfIndex);
            }

            // Replace ";" with "_" if it's immediately after "rdf"
            int semicolonIndex = resultDataFile.indexOf(";", rdfIndex);
            if (semicolonIndex != -1 && semicolonIndex == rdfIndex + 3) {
                resultDataFile = resultDataFile.substring(0, rdfIndex + 3) + "_" + resultDataFile.substring(semicolonIndex + 1);
            }
        }

        return resultDataFile;
    }
}
