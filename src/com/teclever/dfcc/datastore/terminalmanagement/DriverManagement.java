package com.teclever.dfcc.datastore.terminalmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
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
	
	 // Validating Driver Card API from output queue
    public DriverCardDetailsResponse validateDriverCardFromQueue(BlockingQueue<String> outputQueue, int aitessId) {
        List<DriverCard> databaseDriverCards = getDriverCardDetailsBasedOnAitess(aitessId);
        List<DriverCard> parsedDriverCards = parseOutputFromQueue(outputQueue, aitessId);
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

        if (!allPassed) {
            response.setResponseMessage("Validating Card Status FAILED");
        } else {
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
	

	 // Parsing output from blocking queue
    private List<DriverCard> parseOutputFromQueue(BlockingQueue<String> outputQueue, int aitessId) {
        List<DriverCard> driverCards = new ArrayList<>();
        
        // Patterns for matching card initialization lines
        Pattern cardNamePattern = Pattern.compile("\\*{12}Initializing\\s+(\\w+)\\s+Card\\*{12}");
        Pattern dynamicPattern = null;

        String line;
        String cardName = null;
        String cardIdentificationText = null;

        try {
            while ((line = outputQueue.take()) != null) {
                Matcher cardNameMatcher = cardNamePattern.matcher(line);

                if (cardNameMatcher.find()) {
                    cardName = cardNameMatcher.group(1);
                    System.out.println("Card Name: " + cardName);

                    cardIdentificationText = getCardIdentificationTextByCardName(aitessId, cardName);
                    if (cardIdentificationText != null) {
                        if (!cardIdentificationText.contains("##NUM##")) {
                            // Count occurrences of card identification text in subsequent lines
                            int cardCount = countStringOccurrences(outputQueue, cardIdentificationText);
                            driverCards.add(new DriverCard(cardName, String.valueOf(cardCount)));
                        } else {
                            String dynamicPatternString = cardIdentificationText.replace("##NUM##", "(\\d+)");
                            dynamicPattern = Pattern.compile(dynamicPatternString);
                        }
                    } else {
                        System.out.println("No card details found for the card: " + cardName);
                        driverCards.add(new DriverCard(cardName, "0")); // Set card count to 0
                    }
                } else if (dynamicPattern != null) {
                    // Read lines to find the card count using the dynamic pattern
                    Matcher dynamicMatcher = dynamicPattern.matcher(line);
                    if (dynamicMatcher.find()) {
                        int numOfCards = Integer.parseInt(dynamicMatcher.group(1));
                        driverCards.add(new DriverCard(cardName, String.valueOf(numOfCards)));
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return driverCards;
    }

 // Helper method to count occurrences of a string in subsequent lines
    private int countStringOccurrences(BlockingQueue<String> outputQueue, String searchString) throws InterruptedException {
        int count = 0;
        String line;
        while ((line = outputQueue.take()) != null) {
            if (line.contains(searchString)) {
                count++;
            } else {
                // Stop counting if the line doesn't contain the search string
                break;
            }
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
