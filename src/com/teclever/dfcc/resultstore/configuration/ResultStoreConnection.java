package com.teclever.dfcc.resultstore.configuration;

import com.mongodb.client.MongoDatabase;
import dbConnection.DBConnection;

public class ResultStoreConnection {
	
	
	 	private static final String DEFAULT_USERNAME = "root";
	    private static final String DEFAULT_PASSWORD = "root";
	    private static final String DEFAULT_HOST = "localhost";
	    private static final int DEFAULT_PORT = 27017;
	    private static final String DEFAULT_AUTH_SOURCE = "admin";
	    private static final String DEFAULT_DATABASE_NAME = "dfcc";
	    
	    private static DBConnection dbConnection= new DBConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_HOST, DEFAULT_PORT, DEFAULT_AUTH_SOURCE, DEFAULT_DATABASE_NAME);

//	    public ResultStoreConnection() {
//	        dbConnection = new DBConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_HOST, DEFAULT_PORT, DEFAULT_AUTH_SOURCE, DEFAULT_DATABASE_NAME);
//	        System.out.println(dbConnection.getClass());
//	    }

	    public static MongoDatabase getDatabase() {
	        return dbConnection.getDatabase();
	    }
    
}
