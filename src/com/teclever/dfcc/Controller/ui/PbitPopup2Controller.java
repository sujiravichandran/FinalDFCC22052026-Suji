package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PbitPopup2Controller {

	
	   @FXML
	    private HBox addOfpHeading;

	    @FXML
	    private Label headerLabel;

	    @FXML
	    private Button oKButton;

	    @FXML
	    private Label ofpch1;

	    @FXML
	    private Label ofpch2;

	    @FXML
	    private Label ofpch3;

	    @FXML
	    private Label ofpch4;

	    @FXML
	    private AnchorPane selcetOfp;

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

		private String ofpCh1 = StateMachine.OFPversionStatus.getChannel1Status();
		private String ofpCh2 = StateMachine.OFPversionStatus.getChannel2Status();
		private String ofpCh3 = StateMachine.OFPversionStatus.getChannel3Status();
		private String ofpCh4 = StateMachine.OFPversionStatus.getChannel4Status();

		@FXML
		public void initialize() {
			oKButton.setOnAction(e -> handleOkButtonAction());

			wdmch1.setText(wdmCh1);
			wdmch2.setText(wdmCh2);
			wdmch3.setText(wdmCh3);
			wdmch4.setText(wdmCh4);

			ofpch1.setText(ofpCh1);
			ofpch2.setText(ofpCh2);
			ofpch3.setText(ofpCh3);
			ofpch4.setText(ofpCh4);

		}

	
		@FXML
		private void handleOkButtonAction() {
			Stage stage = (Stage) oKButton.getScene().getWindow();
			StateMachine.setTestState(TestState.STOPPED);
			stage.close();

		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
