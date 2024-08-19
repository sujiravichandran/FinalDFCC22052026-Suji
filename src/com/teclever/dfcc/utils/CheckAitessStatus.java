package com.teclever.dfcc.utils;

import com.teclever.dfcc.stateMachine.StateMachine;

public class CheckAitessStatus {

	public boolean isBothAitessOn() {
		if(StateMachine.isAitess1Launched() && StateMachine.isAitess2Launched()) {
			return true;
		}else {		
			Notifications.showWarningAlert("Aitess is currently loading/switching. Please try again in a moment...");
			return false;	
		}
	}
	
}
