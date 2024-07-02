package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class AdvancedTestingHWATPTesting {
	
	private GridPane tab1MainGridPane = new GridPane();
	private VBox leftSideVBox = new VBox(20);
	private GridPane rightSideGridPane = new GridPane();
	
	private ObservableList<String> stageList = FXCollections.observableArrayList("Group-1", "Group-2", "Group-3", "Group-4","Group-1", "Group-2", "Group-3", "Group-4","Group-1", "Group-2", "Group-3", "Group-4");
	
	private ObservableList<String> testList = FXCollections.observableArrayList("test-1", "test-2", "test-3", "test-4","test-1", "test-2", "test-3", "test-4","test-1", "test-2", "test-3", "test-4");
	
    private List<RadioButton> stageListRadioButtons = new ArrayList<>();
    private ListView<RadioButton> stageListView = new ListView<>();
	
	private CheckBox selectAllCheckBox = new CheckBox("Select All");
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private ListView<CheckBox> testListView = new ListView<>();
    private VBox testListVBox = new VBox();
    private HBox buttonHBox = new HBox(20);
    private Button startButton = new Button("Start");
    private Button stopButton = new Button("Stop");
    private Button pauseButton = new Button("Pause");
    
    private HBox repeatCountVBox = new HBox(5);
    private Label repeatCountLabel = new Label();
    private TextField repeatCountTextField = new TextField();

	public GridPane createAdvancedTestingTab1GridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(40);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(60);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		tab1MainGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		tab1MainGridPane.getRowConstraints().addAll(firstRow);
		
		tab1MainGridPane.setHgap(5);
	    
		tab1MainGridPane.add(createLeftSide(), 0, 0);
		tab1MainGridPane.add(createRightSide(), 1, 0);
		
		return tab1MainGridPane;
	}

//	private VBox createLeftSide() {
//	    leftSideVBox.getStyleClass().add("advanced-testing-left-container");
//	    
//	    for (String label : stageList) {
//            RadioButton radioButton = new RadioButton(label);
//            radioButton.getStyleClass().add("radio-button-style");
//            leftSideVBox.getChildren().add(radioButton);
//        }
//	    leftSideVBox.setAlignment(Pos.CENTER);
//
//		return leftSideVBox;
//	}
	
	
	private VBox createLeftSide() {
	    leftSideVBox.getStyleClass().add("advanced-testing-left-container");
	    stageListView.getStyleClass().add("advanced-testing-radio-list-view"); 

	    ToggleGroup toggleGroup = new ToggleGroup(); 

	    for (String test : stageList) {
	        RadioButton newRadioButton = new RadioButton(test);
	        newRadioButton.getStyleClass().add("advanced-testing-radio-button");
	        newRadioButton.setWrapText(true);
	        newRadioButton.setToggleGroup(toggleGroup); 
	        stageListRadioButtons.add(newRadioButton);
	        stageListView.getItems().add(newRadioButton);
	    }

	    toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
	        if (newValue != null) {
	            RadioButton selectedRadioButton = (RadioButton) newValue;
	            System.out.println("Selected RadioButton: " + selectedRadioButton.getText());
	        }
	    });

	    leftSideVBox.setAlignment(Pos.CENTER);
	    leftSideVBox.getChildren().addAll(stageListView);
	    return leftSideVBox;
	}


	
	private GridPane createRightSide() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(80);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(20);
		

		rightSideGridPane.getColumnConstraints().addAll(firstColumn);
		rightSideGridPane.getRowConstraints().addAll(firstRow, secondRow);
		rightSideGridPane.setVgap(5);
	    rightSideGridPane.add(createTestListView(), 0, 0);
	    rightSideGridPane.add(createButtonBox(), 0, 1);
		return rightSideGridPane;
	}

		private VBox createTestListView() {
	    selectAllCheckBox.getStyleClass().addAll("advanced-testing-checkbox","select-all-checkbox");
	    testListView.getStyleClass().add("advanced-testing-list-view");
	   
	    for (String test : testList) {
	        CheckBox newCheckBox = new CheckBox(test);
	        newCheckBox.getStyleClass().add("advanced-testing-checkbox");
	        newCheckBox.setWrapText(true);
	        checkBoxes.add(newCheckBox);
	        testListView.getItems().add(newCheckBox);
	        
	        newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
	        	boolean anySelected = false;
	        	for(CheckBox checkBox : checkBoxes) {
	        		if(checkBox.isSelected()) {
	        			anySelected = true;
	        		}
	        	}
	        	if(anySelected) {
	        		startButton.setDisable(false);
	        	}else {
	        		startButton.setDisable(true);
	        	}
	        });
	    }

	    selectAllCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
	        for (CheckBox checkBox : checkBoxes) {
	            checkBox.setSelected(newValue);
	        }
	        if(newValue) {
	        	startButton.setDisable(false);
	        }else {
				startButton.setDisable(true);
			}
	        
	    });
	    	  
	    testListVBox.getChildren().addAll(selectAllCheckBox,testListView);
	    testListVBox.getStyleClass().add("advanced-testing-right-container");
	    return testListVBox;
	}

		private HBox createButtonBox() {
			buttonHBox.getStyleClass().add("advanced-testing-right-container");
		    Image playImage = new Image(getClass().getResourceAsStream("/Resources/Images/play.png"));
		    Image stopImage = new Image(getClass().getResourceAsStream("/Resources/Images/stop.png"));
		    Image pauseImage = new Image(getClass().getResourceAsStream("/Resources/Images/pause.png"));

		    ImageView playImageView = new ImageView(playImage);
		    playImageView.getStyleClass().add("button-image");
		    playImageView.setFitHeight(25);
		    playImageView.setFitWidth(25);
		    playImageView.setPreserveRatio(true);
		    playImageView.setSmooth(true);
		    
			ImageView stopImageView = new ImageView(stopImage);
			stopImageView.getStyleClass().add("button-image");
			stopImageView.setFitHeight(25);
			stopImageView.setFitWidth(25);
			stopImageView.setPreserveRatio(true);
			stopImageView.setSmooth(true);
			
			ImageView pauseImageView = new ImageView(pauseImage);
			pauseImageView.getStyleClass().add("button-image");
			pauseImageView.setFitHeight(25);
			pauseImageView.setFitWidth(25);
			pauseImageView.setPreserveRatio(true);
			pauseImageView.setSmooth(true);
			

			startButton.setGraphic(playImageView);
			startButton.setGraphicTextGap(10);
			stopButton.setGraphic(stopImageView);
			stopButton.setGraphicTextGap(10);
			pauseButton.setGraphic(pauseImageView);
			pauseButton.setGraphicTextGap(10);
			
			startButton.setDisable(true);
			stopButton.setDisable(true);
			pauseButton.setDisable(true);
			
//			startButton.setOnAction(e ->{
//				fetchDataFromBackend();
//			});
			
			repeatCountLabel.setText("Repeat Count");
			repeatCountLabel.getStyleClass().add("advanced-testing-repeat-count-label");
			repeatCountTextField.getStyleClass().add("advanced-testing-repeat-count-input");
			repeatCountTextField.setText("1");
			
			repeatCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
	            if (!newValue.matches("\\d*")) {
	                repeatCountTextField.setText(oldValue);
	            } else if (newValue.length() > 3) {
	                repeatCountTextField.setText(oldValue);
	            }
	        });
			 
			repeatCountVBox.setAlignment(Pos.CENTER);
			repeatCountVBox.getChildren().addAll(repeatCountLabel,repeatCountTextField);
			
			buttonHBox.setAlignment(Pos.CENTER);
			buttonHBox.getChildren().addAll(repeatCountVBox,startButton,pauseButton,stopButton);
			return buttonHBox;
		}
}
