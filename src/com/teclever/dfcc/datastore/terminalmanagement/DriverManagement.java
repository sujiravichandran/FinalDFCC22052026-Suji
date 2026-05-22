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
			//Edited By Anuj For AIML Loading Issues...
			for (Object[] result : results) {
				String cardName = (String) result[0];
				if (!cardName.equals("1553B MODULE")) {
					String totalNumberOfCards = String.valueOf(result[1]);
					String cardIdentificationText = (String) result[2];
					driverCardDetails.add(new DbDriverCard(cardName, totalNumberOfCards, cardIdentificationText));
				}
			}
			session.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return driverCardDetails;
	}
	
	public DriverCard parseLineNEWtrim(String outputLine, String cardIdentificationText) {
	    CardDetailsService cd = new CardDetailsService();
	    Response response = new Response();

	    String dbCardName = cd.getCardNameByIdentificationText(cardIdentificationText);

	    if (dbCardName == null) {
	        response.setResponseCode(100);
	        response.setResponseMessage("FAILURE: Card identification text not found in the database");
	        return new DriverCard(null, "FAILURE", response);
	    }

	    String trimmedOutputLine = outputLine.trim();
	    
	    if (cardIdentificationText != null) {
	        String trimmedCardIdentificationText = cardIdentificationText.trim();

	        if (trimmedOutputLine.contains(trimmedCardIdentificationText)) {
	            response.setResponseCode(1);
	            response.setResponseMessage("SUCCESS");
	            return new DriverCard(dbCardName, "CARD MATCHED", response);
	        }
	    }

	    response.setResponseCode(0);
	    response.setResponseMessage("FAILURE: Card identification text not found");
	    return new DriverCard(dbCardName, "CARD NOT MATCHED", response);
	}
	
	//New Method With Aitess Id
	public DriverCard parseLineNEWtrim(String outputLine, String cardIdentificationText,int aitessId) {
	    CardDetailsService cd = new CardDetailsService();
	    Response response = new Response();

	    String dbCardName = cd.getCardNameByIdentificationTextWithAitessId(cardIdentificationText,aitessId);

	    if (dbCardName == null) {
	        response.setResponseCode(100);
	        response.setResponseMessage("FAILURE: Card identification text not found in the database");
	        ////System.out.println("Mani Suscept Card Identification Text "+"FAILURE");
	        return new DriverCard(null, "FAILURE", response);
	    }

	    String trimmedOutputLine = outputLine.trim();
	    
	    if (cardIdentificationText != null) {
	        String trimmedCardIdentificationText = cardIdentificationText.trim();

	        if (trimmedOutputLine.contains(trimmedCardIdentificationText)) {
	            response.setResponseCode(1);
	            response.setResponseMessage("SUCCESS");
	            return new DriverCard(dbCardName, "CARD MATCHED", response);
	        }
	    }

	    response.setResponseCode(0);
	    response.setResponseMessage("FAILURE: Card identification text not found");
	    return new DriverCard(dbCardName, "CARD NOT MATCHED", response);
	}

	
	public DriverCard parseLineAIM(String outputLine,String cardText) {
		
		////System.out.println("INSIDE PARSE LINE AIM "+cardText);
		Response response = new Response();
		String dbCardName = "1553B MODULE";
		

//		////System.out.println("cardText  ::"+cardText);
		if (outputLine != null && outputLine.contains(cardText)) {
			////System.out.println("FROM PARSING OUTPUT LINE"+outputLine);
			response.setResponseCode(1);
			response.setResponseMessage("SUCCESS");
			return new DriverCard(dbCardName, "CARD MATCHED", response);
		}
		////System.out.println("Suji Card Response 0:::: chck");
		response.setResponseCode(0);
		response.setResponseMessage("FAILURE: aim_mil not found");
		return new DriverCard(dbCardName, "CARD NOT MATCHED", response);
	}

	
	
	
	
		}
		
	
	

