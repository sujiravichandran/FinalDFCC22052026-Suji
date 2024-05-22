package com.teclever.dfcc.UserManagement;

import org.mindrot.jbcrypt.BCrypt;

public class passwordDecryptor {
	
	    public boolean checkPassword(String enteredPassword, String storedHashedPassword) {
	    
	        return BCrypt.checkpw(enteredPassword, storedHashedPassword);
	    }
}
