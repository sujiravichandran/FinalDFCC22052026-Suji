package com.teclever.dfcc.datastore.terminalmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.dto.DriverCard;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;

public class DriverManagement {


	//Validating Driver Card  API
    public DriverCardDetailsResponse validateDriverCard(String filePath, int aitessId) {
        List<DriverCard> databaseDriverCards = getDriverCardDetailsBasedOnAitess(aitessId);
        List<DriverCard> parsedDriverCards = parseFile(filePath, aitessId);
        List<DriverCard> resultDriverCards = new ArrayList<>();

        Map<String, String> dbCardMap = databaseDriverCards.stream()
                .collect(Collectors.toMap(DriverCard::getCardName, DriverCard::getTotalNumberOfCards));

        boolean allPassed = true;
        for (DriverCard parsedCard : parsedDriverCards) {
            String dbCardCount = dbCardMap.get(parsedCard.getCardName());
            if (dbCardCount != null && dbCardCount.equals(parsedCard.getTotalNumberOfCards())) {
                parsedCard.setMsg("OK");
            } else {
                parsedCard.setMsg("NOT OK");
                allPassed = false;
            }
            resultDriverCards.add(parsedCard);
        }

        DriverCardDetailsResponse driverCardResponse = new DriverCardDetailsResponse();

        Response response = new Response();

        if(!allPassed) {
             response.setResponseMessage("Validating Card Status FAILED");
        }else {
        	response.setResponseMessage("Validating Card Status COMPLETED");
        }

            response.setResponseCode(1);
	        driverCardResponse.setDriverCardDetails(resultDriverCards);
        return driverCardResponse;
    }

	private  String getCardIdentificationTextByCardName(int aitessId, String cardName) {
        String cardIdentificationText = null;
        try {
            SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction transaction = null;

            transaction = session.beginTransaction();
            Query<String> query = session.createQuery(
                    "SELECT cardIdentificationText FROM CardDetails WHERE aitessId = :aitessId AND cardName = :cardName AND deleteStatus = :deleteStatus",
                    String.class);
            query.setParameter("aitessId", aitessId);
            query.setParameter("cardName", cardName);
            query.setParameter("deleteStatus", false);
            cardIdentificationText = query.uniqueResult();
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cardIdentificationText;
    }


	  private  List<DriverCard> parseFile(String filePath, int aitessId) {
	        List<DriverCard> driverCards = new ArrayList<>();

	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            Pattern cardNamePattern = Pattern.compile("\\*{12}Initializing\\s+(\\w+)\\s+Card\\*{12}");
	            String cardName = null;
	            String cardIdentificationText = null;
	            Pattern dynamicPattern = null;

	            while ((line = br.readLine()) != null) {
	                Matcher cardNameMatcher = cardNamePattern.matcher(line);

	                if (cardNameMatcher.find()) {
	                    cardName = cardNameMatcher.group(1);
	                    System.out.println("Card Name: " + cardName);

	                    cardIdentificationText = getCardIdentificationTextByCardName(aitessId, cardName);
	                    if (cardIdentificationText != null) {
	                        if (!cardIdentificationText.contains("##NUM##")) {
	                            // Store the current position
	                            br.mark(10000); // assuming 10000 is sufficient buffer size
	                            int cardCount = countStringOccurrences(br, cardIdentificationText);
	                            driverCards.add(new DriverCard(cardName, String.valueOf(cardCount)));
	                            // Reset the reader to the position marked before counting
	                            br.reset();
	                        } else {
	                            String dynamicPatternString = cardIdentificationText.replace("##NUM##", "(\\d+)");
	                            dynamicPattern = Pattern.compile(dynamicPatternString);
	                        }
	                    } else {
	                        System.out.println("No card details found for the card: " + cardName);
	                        driverCards.add(new DriverCard(cardName, "0")); // Set card count to 0
	                        // Reset state as no valid card identification text was found
	                        cardName = null;
	                        cardIdentificationText = null;
	                        dynamicPattern = null;
	                    }
	                } else if (dynamicPattern != null) {
	                    // Read the lines to find the card count using the dynamic pattern
	                    Matcher dynamicMatcher = dynamicPattern.matcher(line);
	                    if (dynamicMatcher.find()) {
	                        int numOfCards = Integer.parseInt(dynamicMatcher.group(1));
	                        // System.out.println("Number of Cards: " + numOfCards);
	                        driverCards.add(new DriverCard(cardName, String.valueOf(numOfCards)));
	                        // Reset state after adding the card
	                        cardName = null;
	                        cardIdentificationText = null;
	                        dynamicPattern = null;
	                    }
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return driverCards;
	    }

	    private  int countStringOccurrences(BufferedReader br, String pattern) throws IOException {
	        int count = 0;
	        String line;
	        StringBuilder concatenatedLines = new StringBuilder();

	        while ((line = br.readLine()) != null) {
	            if (line.matches("\\*{12}Initializing\\s+\\w+\\s+Card\\*{12}")) {
	                // Stop reading if the next card name is found
	                // Reset the reader to the position marked before counting
	                br.reset();
	                break;
	            }
	            concatenatedLines.append(line).append("\n");
	        }

	        String concatenatedText = concatenatedLines.toString();
	        int index = 0;
	        while ((index = concatenatedText.indexOf(pattern, index)) != -1) {
	            index += pattern.length();
	            count++;
	        }
	        return count;
	    }


	 private List<DriverCard> getDriverCardDetailsBasedOnAitess(int aitessId) {
		    List<DriverCard> driverCardDetails = new ArrayList<>();

		    try {
		        SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
		        Session session = sessionFactory.openSession();
		        Transaction transaction = null;

		        transaction = session.beginTransaction();
		        Query<Object[]> query = session.createQuery(
		                "SELECT cardName, totalNumberOfCards FROM CardDetails WHERE aitessId = :aitessId AND deleteStatus = :deleteStatus",
		                Object[].class);
		        query.setParameter("aitessId", aitessId);
		        query.setParameter("deleteStatus", false);

		        List<Object[]> results = query.list();
		        for (Object[] result : results) {
		            String cardName = (String) result[0];
		            String totalNumberOfCards = String.valueOf(result[1]);
		            driverCardDetails.add(new DriverCard(cardName, totalNumberOfCards));
		        }

		        session.getTransaction().commit();
		    } catch (Exception ex) {
		        ex.printStackTrace();
		    }

		    return driverCardDetails;
		}



}

