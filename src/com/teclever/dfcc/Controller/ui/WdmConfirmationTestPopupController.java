package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class WdmConfirmationTestPopupController {


	    @FXML
	    private Button cancel_button;

	    @FXML
	    private Label headerLabel;

	    @FXML
	    private Button oKButton;

	    @FXML
	    private Label wdmch1;

	    @FXML
	    private Label wdmch2;

	    @FXML
	    private Label wdmch3;

	    @FXML
	    private Label wdmch4;
	
	    private String wdmCh1 = StateMachine.WDMStatus.getChannel1Status();
		private String wdmCh2 = StateMachine.WDMStatus.getChannel2Status();
		private String wdmCh3 = StateMachine.WDMStatus.getChannel3Status();
		private String wdmCh4 = StateMachine.WDMStatus.getChannel4Status();
	
	
		@FXML
		public void initialize() {
			oKButton.setOnAction(e -> handleTestContinueButtonAction());
			cancel_button.setOnAction(e -> handleTestCancelButtonAction());

			wdmch1.setText(wdmCh1);
			wdmch2.setText(wdmCh2);
			wdmch3.setText(wdmCh3);
			wdmch4.setText(wdmCh4);
		}
	
	
		@FXML
		private void handleTestContinueButtonAction() {
			
			Stage stage = (Stage) oKButton.getScene().getWindow();
			StateMachine.setTestState(TestState.RUNNING);
			stage.close();
			
		}
	
	
		@FXML
		private void handleTestCancelButtonAction() {
			
			Stage stage = (Stage) cancel_button.getScene().getWindow();
			StateMachine.setTestState(TestState.STOPPED);
			stage.close();


		}
	
	
	
	
	
	
	
	
}
