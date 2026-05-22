package com.teclever.dfcc.resultstore.resultmanagement;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.teclever.dfcc.resultstore.configuration.ResultStoreConnection;
import com.teclever.dfcc.resultstore.dto.ResultDto;
import com.teclever.dfcc.utils.Debug;

public class ResultManagement {

	public static List<ResultDto> getResult(String sessionId, ObjectId specificObjectId, String testFileId) {
		List<ResultDto> resultList = new ArrayList<>();
		MongoDatabase database = ResultStoreConnection.getDatabase();

		MongoCollection<Document> rdfFileInfoCollection = database.getCollection(sessionId);
		Document rdfFileInfoDoc = rdfFileInfoCollection
				.find(and(eq("sessionId", sessionId), eq("_id", specificObjectId))).first();

//	    Suji changed for D* issue based on step & without step in detail data
		if (rdfFileInfoDoc != null) {
			ObjectId refObjectId = rdfFileInfoDoc.getObjectId("RefObjectId");
			String refCollectionName = rdfFileInfoDoc.getString("RefCollectionName");

			String collectionName = getCollectionName(refCollectionName);
			collectionName = collectionName + "_" + testFileId;

			MongoCollection<Document> resultDataCollection = database.getCollection(collectionName);
//			////System.out.println("MongoDB Collection Name" + resultDataCollection);
			Document resultDataDoc = resultDataCollection.find(eq("_id", refObjectId)).first();
//			////System.out.println("MongoDB resultDataDoc" + resultDataDoc);
			if (resultDataDoc != null) {
				String resultDataFile = resultDataDoc.getString("resultDataFile");
				String[] resultDataParts = resultDataFile.split("/");
				String fileName = resultDataParts[resultDataParts.length - 1];

				// Get the failedStep map
				Map<String, ObjectId> failedStepMap = resultDataDoc.get("failedStep", Map.class);

//				////System.out.println("Mongodb failedStepMap " + failedStepMap);

				Set<ObjectId> processedIds = new HashSet<>();

				List<Document> allSteps = resultDataCollection.find(Filters.gte("_id", refObjectId))
						.into(new ArrayList<>());
				boolean skipFirstDocument = true;

				for (Document stepDoc : allSteps) {
					if (skipFirstDocument) {
						skipFirstDocument = false;
						continue;
					}

					if (stepDoc.containsKey("project"))
						break;

					String measuredValue = null;
					String faultyChannel = null;

					Map<String, String> faultyChannels = stepDoc.get("faultyChannel", Map.class);
					if (faultyChannels != null && !faultyChannels.isEmpty()) {
						for (Map.Entry<String, String> entry : faultyChannels.entrySet()) {
							faultyChannel = entry.getKey();
							measuredValue = entry.getValue();
						}

						String tpgph = stepDoc.getString("tpgph");
						String unit = stepDoc.getString("unit");
						String signalName = stepDoc.getString("signalName");
						String expectedValue = stepDoc.getString("expectedValue");
						String faultySRU = stepDoc.getString("faultySRU");
						String dStarInfo = stepDoc.getString("dStarInfo");
//						Added by Suji
						String stepName = stepDoc.getString("step");
//						Exit

//						Pattern pattern = Pattern.compile("\\((.*?)\\)");
//						Matcher matcher = pattern.matcher(dStarInfo);
						Pattern pattern = Pattern.compile("\\((.*)\\)"); 
						if(signalName == null || !signalName.contains("Wait for condition timed out."))
						{	
						Matcher matcher = pattern.matcher(dStarInfo);
						List<String> formattedChannels = new ArrayList<>();

//						if (matcher.find()) {
//							String[] parts = matcher.group(1).trim().split(",");
//							for (int i = 0; i < parts.length; i++) {
//								String channelValue = parts[i].trim();
//								if (channelValue.contains("down") || channelValue.contains("offline")
//										|| channelValue.startsWith("*")) {
//									if (channelValue.startsWith("*")) {
//										channelValue = channelValue.substring(1);
//									}
//									formattedChannels.add("CH" + (i + 1) + ": " + channelValue);
//									////System.out.println("Check ResulMgmnt" + formattedChannels);
//								}
//							}
//						}
						if (matcher.find()) {
						    String insideParentheses = matcher.group(1);
						    String[] parts = insideParentheses.split(",", -1); 
//						    ////System.out.println("Parts = " + Arrays.toString(parts));

						    for (int i = 0; i < parts.length; i++) {
						        String channelValue = parts[i].trim();
//						        ////System.out.println("D* info Check: " + channelValue);

						        if (channelValue.equalsIgnoreCase("passed")) continue;

						        if (channelValue.contains("offline") || channelValue.contains("diff") || channelValue.startsWith("*")) {
						            if (channelValue.startsWith("*")) {
						                channelValue = channelValue.substring(1).trim();
						            }
						            formattedChannels.add("CH" + (i + 1) + ": " + channelValue);
						        }
						    }

//						    ////System.out.println("Formatted Channels Final = " + formattedChannels);
						}
						
						

						ResultDto resultDto = new ResultDto(tpgph, stepName, expectedValue, measuredValue, unit,
								signalName, faultyChannels, fileName, faultySRU);
						resultDto.setdStarChannels(formattedChannels);
						resultList.add(resultDto);
						}
						else
						{
							ResultDto resultDto = new ResultDto(tpgph, stepName, expectedValue, measuredValue, unit,
									signalName, faultyChannels, fileName, faultySRU);
							
							resultList.add(resultDto);
							
						}
					}
				}
//Exit

			} else {
				Debug.printDebug("Document not found in collection: " + collectionName);
			}
		} else {
			Debug.printDebug("No documents found in rdf_file_info collection for testRunId: " + sessionId);
		}

		return resultList;
	}

	// git
//	//GET API
//	public static List<ResultDto> getResult(String sessionId, ObjectId specificObjectId) {
//	    List<ResultDto> resultList = new ArrayList<>();
//	    MongoDatabase database = ResultStoreConnection.getDatabase();
//
//	    MongoCollection<Document> rdfFileInfoCollection = database.getCollection(sessionId);
//	    Document rdfFileInfoDoc = rdfFileInfoCollection.find(and(eq("sessionId", sessionId), eq("_id", specificObjectId))).first();
//
//	    if (rdfFileInfoDoc != null) {
//	        ObjectId refObjectId = rdfFileInfoDoc.getObjectId("RefObjectId");
//	        String refCollectionName = rdfFileInfoDoc.getString("RefCollectionName");
//
//	        String collectionName = getCollectionName(refCollectionName);
//
//	        MongoCollection<Document> resultDataCollection = database.getCollection(collectionName);
//	        Document resultDataDoc = resultDataCollection.find(eq("_id", refObjectId)).first();
//
//	        if (resultDataDoc != null) {
//	            String resultDataFile = resultDataDoc.getString("resultDataFile");
//	            String[] resultDataParts = resultDataFile.split("/");
//	            String fileName = resultDataParts[resultDataParts.length - 1];
//
//	            // Get the failedStep map
//	            Map<String, ObjectId> failedStepMap = resultDataDoc.get("failedStep", Map.class);
//	            
//	            // Check if failedStepMap is empty
//	            if (failedStepMap != null && !failedStepMap.isEmpty()) {
//	                // Iterate through each failedStep ObjectId and fetch details for each step
//	                for (Map.Entry<String, ObjectId> entry : failedStepMap.entrySet()) {
//	                    ObjectId stepObjectId = entry.getValue();
//	                    Document stepDoc = resultDataCollection.find(eq("_id", stepObjectId)).first();
//	                    if (stepDoc != null) {
//	                        String stepName = entry.getKey();
//	                        String measuredValue = null;
//	                        String faultyChannel = null;
//
//	                        // Extract faultyChannel and measuredValue
//	                        Map<String, String> faultyChannels = stepDoc.get("faultyChannel", Map.class);
//	                        if (faultyChannels != null) {
//	                            for (Map.Entry<String, String> faultyChannelEntry : faultyChannels.entrySet()) {
//	                                faultyChannel = faultyChannelEntry.getKey();
//	                                measuredValue = faultyChannelEntry.getValue();
//	                            }
//	                        }
//
//	                        String tpgph = stepDoc.getString("tpgph");
//	                        String unit = stepDoc.getString("unit");
//	                        String signalName = stepDoc.getString("signalName");
//	                        String expectedValue = stepDoc.getString("expectedValue");
//	                        String faultySRU = stepDoc.getString("faultySRU");
//	                        String dStarInfo = stepDoc.getString("dStarInfo");
//	                        // Regular expression to extract content inside the parentheses
//	                        Pattern pattern = Pattern.compile("\\((.*?)\\)");  // Non-greedy match inside parentheses
//	                        Matcher matcher = pattern.matcher(dStarInfo);
//                            List<String> formattedChannels = new ArrayList<>();
//
//	                        // Extract the content inside parentheses if found
//	                        if (matcher.find()) {
//	                            String contentInsideParentheses = matcher.group(1).trim();  // Get the matched group (the content inside parentheses)
//	                            
//	                            // Split by commas and trim spaces to extract individual parts
//	                            String[] parts = contentInsideParentheses.split(",");
//	                            
//	                            // List to store the formatted channel output
//	                            
//	                            // Iterate over the parts and label each one
//	                            for (int i = 0; i < parts.length; i++) {
//	                                String channelValue = parts[i].trim();  // Remove any leading or trailing spaces
//	                                // Check if the value is either "down", "offline", or starts with "*"
//	                                if (channelValue.contains("down") || channelValue.contains("offline") || channelValue.startsWith("*")) {
//	                                    // If it starts with '*' remove it
//	                                    if (channelValue.startsWith("*")) {
//	                                        channelValue = channelValue.substring(1);  // Remove the '*'
//	                                    }
//	                                    
//	                                    // Add the formatted channel to the output list
//	                                    formattedChannels.add("CH" + (i + 1) + ": " + channelValue);
//	                                }
//	                            }
//	                            
//	                            // Prepare the final output as a string
//	                            //String result = String.join(", ", formattedChannels);
//	                            
//	                            // Print the formatted output
//	                        } else {
//	                            ////System.out.println("No content inside parentheses found.");
//	                        }
//
//	                        ResultDto resultDto = new ResultDto(tpgph, stepName, expectedValue, measuredValue, unit, signalName, faultyChannels, fileName,faultySRU);
//	                        resultDto.setdStarChannels(formattedChannels);
//	                        resultList.add(resultDto);
//	                    }
//	                }
//	            } else {
//	                // Fetch all documents from the resultDataCollection
//	                List<Document> allSteps = resultDataCollection.find(Filters.gte("_id", refObjectId))
//	                                    .into(new ArrayList<>());
//	                
//	                // Flag to skip the first document
//	                boolean skipFirstDocument = true;
//
//	                for (Document stepDoc : allSteps) {
//	                    // If this is the first document, skip it
//	                    if (skipFirstDocument) {
//	                        skipFirstDocument = false;
//	                        continue;
//	                    }
//
//	                    // Check if the document contains the "project" field
//	                    if (stepDoc.containsKey("project")) {
//	                        break;
//	                    }
//
//	                    String stepName = null;
//	                    String measuredValue = null;
//	                    String faultyChannel = null;
//
//	                    // Extract faultyChannel and measuredValue if present
//	                    Map<String, String> faultyChannels = stepDoc.get("faultyChannel", Map.class);
//
//	                    // Only proceed if faultyChannels is not null or empty
//	                    if (faultyChannels != null && !faultyChannels.isEmpty()) {
//	                        // Iterate over the faultyChannels map to get key-value pairs
//	                        for (Map.Entry<String, String> faultyChannelEntry : faultyChannels.entrySet()) {
//	                            faultyChannel = faultyChannelEntry.getKey();
//	                            measuredValue = faultyChannelEntry.getValue();
//	                        }
//
//	                        // Extract other fields
//	                        String tpgph = stepDoc.getString("tpgph");
//	                        String unit = stepDoc.getString("unit");
//	                        String signalName = stepDoc.getString("signalName");
//	                        String expectedValue = stepDoc.getString("expectedValue");
//	                        String faultySRU = stepDoc.getString("faultySRU");
//	                        String dStarInfo = stepDoc.getString("dStarInfo");
//	                     // Regular expression to extract content inside the parentheses
//	                        Pattern pattern = Pattern.compile("\\((.*?)\\)");  // Non-greedy match inside parentheses
//	                        Matcher matcher = pattern.matcher(dStarInfo);
//                            List<String> formattedChannels = new ArrayList<>();
//
//	                        // Extract the content inside parentheses if found
//	                        if (matcher.find()) {
//	                            String contentInsideParentheses = matcher.group(1).trim();  // Get the matched group (the content inside parentheses)
//	                            
//	                            // Split by commas and trim spaces to extract individual parts
//	                            String[] parts = contentInsideParentheses.split(",");
//	                            
//	                            // List to store the formatted channel output
////	                            List<String> formattedChannels = new ArrayList<>();
//	                            
//	                            // Iterate over the parts and label each one
//	                            for (int i = 0; i < parts.length; i++) {
//	                                String channelValue = parts[i].trim();  // Remove any leading or trailing spaces
//	                                // Check if the value is either "down", "offline", or starts with "*"
//	                                if (channelValue.contains("down") || channelValue.contains("offline") || channelValue.startsWith("*")) {
//	                                    // If it starts with '*' remove it
//	                                    if (channelValue.startsWith("*")) {
//	                                        channelValue = channelValue.substring(1);  // Remove the '*'
//	                                    }
//	                                    
//	                                    // Add the formatted channel to the output list
//	                                    formattedChannels.add("CH" + (i + 1) + ": " + channelValue);
//	                                }
//	                            }
//	                            
//	                            // Prepare the final output as a string
//	                            String result = String.join(", ", formattedChannels);
//	                            
//	                            // Print the formatted output
//	                        }
//
//	                        // Create the ResultDto object and add it to the result list
//	                        ResultDto resultDto = new ResultDto(tpgph, stepName, expectedValue, measuredValue, unit, signalName, faultyChannels, fileName, faultySRU);
//	                        resultDto.setdStarChannels(formattedChannels);
//	                        resultList.add(resultDto);
//	                    }
//	                }
//	            }
//
//	        } else {
//	            Debug.printDebug("Document not found in collection: " + collectionName);
//	        }
//	    } else {
//	        Debug.printDebug("No documents found in rdf_file_info collection for testRunId: " + sessionId);
//	    }
//
//	    return resultList;
//	}

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
				resultDataFile = resultDataFile.substring(0, rdfIndex + 3) + "_"
						+ resultDataFile.substring(semicolonIndex + 1);
			}
		}

		return resultDataFile;
	}
}
