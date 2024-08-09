package com.teclever.dfcc.datastore.terminalmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.service.CardDetailsService;
import com.teclever.dfcc.datastore.dto.DbDriverCard;
import com.teclever.dfcc.datastore.dto.DriverCard;

public class DriverManagement {

	public List<DbDriverCard> getDriverCardDetailsBasedOnAitess(int aitessId) {
		List<DbDriverCard> driverCardDetails = new ArrayList<>();
		try {
			SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();
			Query<Object[]> query = session.createQuery(
					"SELECT cardName, totalNumberOfCards, cardIdentificationText FROM CardDetails WHERE aitessId = :aitessId AND deleteStatus = :deleteStatus",
					Object[].class);
			query.setParameter("aitessId", aitessId);
			query.setParameter("deleteStatus", false);
			List<Object[]> results = query.list();
			for (Object[] result : results) {
				String cardName = (String) result[0];
				String totalNumberOfCards = String.valueOf(result[1]);
				String cardIdentificationText = (String) result[2];
				driverCardDetails.add(new DbDriverCard(cardName, totalNumberOfCards, cardIdentificationText));
			}
			session.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return driverCardDetails;
	}

	public DriverCard parseLine(String outputLine, String cardIdentificationText) {
		DriverCard dr = new DriverCard();
		Pattern cardNamePattern = Pattern.compile("\\*{12}Initializing\\s+(\\w+)\\s+Card\\*{12}");
		Matcher cardNameMatcher = cardNamePattern.matcher(outputLine);
		Response response = new Response();
		if (cardNameMatcher.find()) {
			String cardName = cardNameMatcher.group(1);
			System.out.println("Card Name: " + cardName);

			if (cardIdentificationText != null) {
				if (cardIdentificationText.contains("##NUM##")) {
					// Replace ##NUM## with a capturing group for the number
					String dynamicPatternString = cardIdentificationText.replace("##NUM##", "(\\d+)");
					Pattern dynamicPattern = Pattern.compile(dynamicPatternString);
					Matcher dynamicMatcher = dynamicPattern.matcher(outputLine);

					if (dynamicMatcher.find()) {
						String numOfCards = dynamicMatcher.group(1);
						response.setResponseCode(1);
						response.setResponseMessage("SUCCESS");

						return new DriverCard(cardName, numOfCards, response);
					}
				} else {
					// Static pattern, check if output line contains the cardIdentificationText
					if (outputLine.contains(cardIdentificationText)) {
						response.setResponseCode(1);
						response.setResponseMessage("SUCCESS");
						return new DriverCard(cardName, "1", response);
					}
				}
			} else {
				System.out.println("No card details found for the card: " + cardName);
				response.setResponseCode(1);
				response.setResponseMessage("SUCCESS");
				return new DriverCard(cardName, "0", response); // Set card count to 0
			}
		}
		response.setResponseCode(0);
		response.setResponseMessage("FAILURE");
		return new DriverCard(null, null, response);
	}
	
	public DriverCard parseLine1(String outputLine, String cardIdentificationText) {
	    CardDetailsService cd = new CardDetailsService();
	    Response response = new Response();

	    // Fetch card name from the db based on the cardIdentificationText
	    String dbCardName = cd.getCardNameByIdentificationText(cardIdentificationText);
	    System.out.println("Parsed cardName from DB ::  " + dbCardName);

	    if (dbCardName == null) {
	        response.setResponseCode(0);
	        response.setResponseMessage("FAILURE");
	        return new DriverCard(null, null, response);
	    }

	    if (cardIdentificationText != null && outputLine.contains(cardIdentificationText)) {
	        // Extract the numeric value from the line if the cardIdentificationText is present
	        Pattern numericPattern = Pattern.compile("\\d+");
	        Matcher numericMatcher = numericPattern.matcher(outputLine);

	        String numOfCards = "0"; // Default to 0 if no number is found
	        if (numericMatcher.find()) {
	            numOfCards = numericMatcher.group();
	        }

	        response.setResponseCode(1);
	        response.setResponseMessage("SUCCESS");
	        return new DriverCard(dbCardName, numOfCards, response);
	    }

	    // If cardIdentificationText is not found in outputLine
	    response.setResponseCode(0);
	    response.setResponseMessage("FAILURE");
	    return new DriverCard(null, null, response);
	}
		}
		
	
	

