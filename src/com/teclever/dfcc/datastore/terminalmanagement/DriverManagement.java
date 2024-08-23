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
	
	
	//new logic 
	public DriverCard parseLineNEW(String outputLine, String cardIdentificationText) {
	    CardDetailsService cd = new CardDetailsService();
	    Response response = new Response();

	    // Fetch card name from the db based on the cardIdentificationText
	    String dbCardName = cd.getCardNameByIdentificationText(cardIdentificationText);
	    System.out.println("Parsed cardName from DB ::  " + dbCardName);

	    if (dbCardName == null) {
	        response.setResponseCode(100);
	        response.setResponseMessage("FAILURE: Card identification text not found in the database");
	        return new DriverCard(null, "FAILURE", response);
	    }

	    if (cardIdentificationText != null && outputLine.contains(cardIdentificationText)) {
	        // Card identification text found in the outputLine
	        response.setResponseCode(1);
	        response.setResponseMessage("SUCCESS");
	        return new DriverCard(dbCardName, "CARD MATCHED", response);
	    }

	    // Card identification text not found in outputLine, but still return dbCardName
	    response.setResponseCode(0);
	    response.setResponseMessage("FAILURE: Card identification text not found");
	    return new DriverCard(dbCardName, "CARD NOT MATCHED", response);
	}

	
	public DriverCard parseLineAIM(String outputLine) {
	    Response response = new Response();
        String dbCardName = "aim_mil"; 

	    if (outputLine != null && outputLine.contains("aim_mil")) {
	        response.setResponseCode(1);
	        response.setResponseMessage("SUCCESS");
	        return new DriverCard(dbCardName, "CARD MATCHED", response);
	    }

	    response.setResponseCode(0);
	    response.setResponseMessage("FAILURE: aim_mil not found");
	    return new DriverCard(dbCardName, "CARD NOT MATCHED", response);
	}

	
	
	
	
		}
		
	
	

