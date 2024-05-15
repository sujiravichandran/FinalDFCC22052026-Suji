package com.teclever.dfcc.UserManagement;

import org.mindrot.jbcrypt.BCrypt;

public class passwordEncryptor {
	
	    public String encryptPassword(String enteredPassword) {
	    	
			return BCrypt.hashpw(enteredPassword, BCrypt.gensalt());
	  
	    }

}
