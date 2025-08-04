package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentTestDetails;
import com.teclever.dfcc.utils.CheckAitessStatus;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class TerminalController {
    private StackPane terminalStackPane = new StackPane();
    private GridPane terminalMainGridPane = new GridPane();
    private GridPane terminalContentGridPane = new GridPane();
    private GridPane terminaltextAreaGridPane = new GridPane();

    private TextArea textArea = new TextArea();
	private GridPane terminalGridPane = new GridPane();
	private TextField terminalTextField = new TextField();
	private Button enterButton = new Button("ENTER");
	private Button yesButton = new Button("YES");
	private Button noButton = new Button("NO");
	
	AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
	CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
	
	public TerminalController() {
		AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
		Platform.runLater(() -> {
			textArea.setEditable(false);
			System.out.println("Entred Terminal Controller");
            aitessProcessControlManagement.launchAitess(currentTestDetails.getTestType(), textArea);           
        });		
		
		terminalStackPane.setOnMouseClicked(event -> {
            if (event.getTarget() instanceof GridPane) {
            	Platform.runLater(()->{            		
            		animateStackPaneToBack();
            	});
            }
        });
	}


    public StackPane createTerminalStackPane() {
        terminalStackPane.getChildren().add(createTerminalMainGridPane());
        return terminalStackPane;
    }

    private GridPane createTerminalMainGridPane() {
    	terminalMainGridPane.setHgap(10);
    	terminalMainGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/TerminalController.css").toExternalForm());
    	terminalMainGridPane.getStyleClass().add("terminal-main-container");
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(17);
        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setPercentWidth(66);
        ColumnConstraints thirdColumn = new ColumnConstraints();
        thirdColumn.setPercentWidth(17);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(100);

        terminalMainGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
        terminalMainGridPane.getRowConstraints().add(firstRow);
        
        terminalMainGridPane.add(createTerminalContentGridPane(), 1, 0);

        return terminalMainGridPane;
    }

	private GridPane createTerminalContentGridPane() {
		terminalContentGridPane.setVgap(12);
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(100);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(25);
        RowConstraints secondRow = new RowConstraints();
        secondRow.setPercentHeight(70);
        RowConstraints thirdRow = new RowConstraints();
        thirdRow.setPercentHeight(7);

        terminalContentGridPane.getColumnConstraints().addAll(firstColumn);
        terminalContentGridPane.getRowConstraints().addAll(firstRow,secondRow,thirdRow);
                
        terminalContentGridPane.add(createTerminalTextArea(), 0, 1);
      	terminalContentGridPane.add(createTerminalBottomBox(), 0, 2);
      	
		return terminalContentGridPane;
	}

	
	private GridPane createTerminalTextArea() {
		
		terminaltextAreaGridPane.getStyleClass().add("terminal-textarea-container");
		textArea.getStyleClass().add("terminal-textarea");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		terminaltextAreaGridPane.getColumnConstraints().addAll(firstColumn);
		terminaltextAreaGridPane.getRowConstraints().addAll(firstRow);
		
		terminaltextAreaGridPane.add(textArea, 0, 0);
				
		return terminaltextAreaGridPane;
	}


	
	private GridPane createTerminalBottomBox() {
		terminalGridPane.getStyleClass().add("terminal-bottom-container");
		terminalTextField.getStyleClass().add("terminal-input");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(71);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(8);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(8);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(8);
		ColumnConstraints fifthColumn = new ColumnConstraints();
		fifthColumn.setPercentWidth(5);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
				
		terminalGridPane.setHgap(20);
		terminalGridPane.getColumnConstraints().addAll(firstColumn,secondColumn,thirdColumn,fourthColumn,fifthColumn);
		terminalGridPane.getRowConstraints().addAll(firstRow);
		
		Image minImage = new Image(DFCCConstant.JARSTRING+"/Resources/Images/down-arrow1.png");
	    ImageView minImageView = new ImageView(minImage);
	    minImageView.getStyleClass().add("terminal-image");
	    
	    minImageView.setFitWidth(50);
	    minImageView.setFitHeight(50);

	    minImageView.setOnMouseClicked((MouseEvent event) -> {	 
	    	animateStackPaneToBack();
	    });
	    	    
	    enterButton.setOnAction( e ->{
	    	String inputCommand = terminalTextField.getText() + "\n";
	    	handleSendCommand(inputCommand); 	
	    });
	    
	    yesButton.setOnAction( e ->{
	    	
//		    Edited By: SUJI
//			Change Made for Point:22(Mail:7-Jul-Observations_in_testing_Teclever_Date_SAT))
//			Change Made On:When 'Y' is pressed it is not immediately displayed in terminal window.
		
			
			StateMachine.setResponceYesTerminal(true);
			handleSendCommand("Y" + "\n");
//			yesButton.setDisable(true);

//			StateMachine.yesFromTerminalProperty().addListener((observable, oldValue, newValue) -> {
//				System.out.println("yesFromTerminal changed from " + oldValue + " to " + newValue);
//
//				if (newValue) {
//					yesButton.setDisable(false);
//				}
//			});
	    });
	    
//		Exit;
//		Point:22
	    
	    noButton.setOnAction( e ->{
	    	handleSendCommand("N" + "\n"); 	
	    });
	    
	    terminalTextField.setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.ENTER && isStackPaneOnTop() && terminalTextField.getText() != null && !terminalTextField.getText().isEmpty()) {           
	            String inputCommand = terminalTextField.getText() + "\n";
		    	handleSendCommand(inputCommand); 
	        }
	    });
	    
	    
	    Platform.runLater(() -> {
	        terminalTextField.requestFocus();
	        terminalTextField.setFocusTraversable(true);
	    });
	    
		terminalGridPane.add(terminalTextField, 0, 0);
		terminalGridPane.add(enterButton, 1, 0);
		terminalGridPane.add(yesButton, 2, 0);
		terminalGridPane.add(noButton, 3, 0);
		terminalGridPane.add(minImageView, 4, 0);
		
		return terminalGridPane;
	}
		
	private void animateStackPaneToBack() {
	    StackPane parentStackPane = (StackPane) terminalStackPane.getParent();
	    
	    for (Node node : parentStackPane.getChildren()) {
	        if (node instanceof GridPane) {
	            GridPane bottomMainGridPane = (GridPane) node;
	            bottomMainGridPane.setOpacity(1); 
	        }
	    }
	    	    
	    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), terminaltextAreaGridPane);
	    scaleTransition.setFromY(1.0); 
	    scaleTransition.setToY(0.0); 
	    
	    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), terminaltextAreaGridPane);
	    translateTransition.setFromY(0);  
	    translateTransition.setToY(terminaltextAreaGridPane.getHeight() / 2); 
	    
	    FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), terminaltextAreaGridPane);
	    fadeTransition.setFromValue(1.0); 
	    fadeTransition.setToValue(0.0); 
	    
	    ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, translateTransition, fadeTransition);
	    parallelTransition.setOnFinished(e->{
		    terminalStackPane.toBack();
	    });
	    parallelTransition.play();

	}

	
	private void handleSendCommand(String command) {
		if(!checkAitessStatus.isBothAitessOn()) {
			return ;
		}
		Platform.runLater(()->{	
			aitessProcessControlManagement.WriteAitess1Command(command);		
			terminalTextField.clear();
			terminalTextField.requestFocus();
			terminalTextField.setFocusTraversable(true);
		});
	}

	private boolean isStackPaneOnTop() {
	    StackPane parentStackPane = (StackPane) terminalStackPane.getParent();
	    if (parentStackPane != null && parentStackPane.getChildren().size() > 0) {
	        Node topNode = parentStackPane.getChildren().get(parentStackPane.getChildren().size() - 1);
	        return topNode == terminalStackPane;
	    }
	    return false;
	}

}


