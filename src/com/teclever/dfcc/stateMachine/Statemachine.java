package com.teclever.dfcc.stateMachine;

import javafx.event.ActionEvent;

public class Statemachine {

	 private enum State {
	        USERTYPE,
	        TESTING,
	        RESULT,
	        REPORT
	        
	    }
	
	
	 private State currentState;

	    public void start() {
	        currentState = State.USERTYPE;
	    }
	        public void handle(ActionEvent event) {
                switch (currentState) {
                    case USERTYPE:
                        currentState = State.TESTING;
                        break;
                    case TESTING:
                        currentState = State.RESULT;
                        break;
                    case RESULT:
                        currentState = State.REPORT;
                        break;
                    case REPORT:
                        currentState = State.USERTYPE;
                        break;
                }
	        }
}
