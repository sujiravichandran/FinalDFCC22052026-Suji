package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.model.TestSummary;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class SessionTestingController {
	private GridPane sessionTestingMainGridPane = new GridPane();
	private GridPane sessionTestingHeadingGridPane = new GridPane();
	private GridPane sessionTestingTreeviewGridPane = new GridPane();
	private GridPane sessionTestingListMainGridPane = new GridPane();
	private GridPane sessionTestingResultsGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();
	
	private TreeView<Label> sessionTreeView = new TreeView<>();
	
	private ObservableList<String> l1_sessionList = FXCollections.observableArrayList("Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2");
	private ObservableList<String> l2_sessionList = FXCollections.observableArrayList("Sub Session 11", "Sub Session 12");
	private ObservableList<String> l3_sessionList = FXCollections.observableArrayList("Sub Session 111", "Sub Session 112");
	private ObservableList<String> l4_sessionList = FXCollections.observableArrayList("Sub Session 1111", "Sub Session 1112");
	
	private ObservableList<String> testList = FXCollections.observableArrayList("test-1", "test-2", "test-3", "test-4","test-1", "test-2", "test-3", "test-4");
	
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
    
    private ObservableList<TestSummary> testSummaryList = FXCollections.observableArrayList();

    
	public GridPane createSessionTestingGridPane() {
		getSessionTestData();
		sessionTestingMainGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/SessionTesting.css").toExternalForm());
		sessionTestingMainGridPane.getStyleClass().add("session-testing-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);
		
		sessionTestingMainGridPane.setPadding(new Insets(10));
		sessionTestingMainGridPane.setVgap(5);
		sessionTestingMainGridPane.setHgap(5);
		sessionTestingMainGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		sessionTestingMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		
		sessionTestingMainGridPane.add(createHeadingBox(), 0, 0, 2, 1);
		sessionTestingMainGridPane.add(createSessionTestingLeftSide(), 0, 1);
		sessionTestingMainGridPane.add(createSessionTestingRightSide(), 1, 1);
		sessionTestingMainGridPane.add(createSessionTestingResultsGridPane(), 0, 2, 2, 1);
				
		return sessionTestingMainGridPane;
	}
	
	private void getSessionTestData() {
	    List<StageObject> stageList = StateMachine.getStageDatalist();
	    ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);

	    Map<String, StageObject> l1Stages = new HashMap<>();
	    Map<String, StageIdName> l2Stages = new HashMap<>();
	    Map<String, StageIdName> l3Stages = new HashMap<>();
	    Map<String, StageIdName> l4Stages = new HashMap<>();
	    Map<String, StageIdName> l5Stages = new HashMap<>();

	    // Populate the maps with stage objects
	    observableStageList.forEach(stage -> {
	        String l1StageName = stage.getL1StageName().trim();
	        if (!("Self Test".equalsIgnoreCase(l1StageName)) && !("LRU Test".equalsIgnoreCase(l1StageName))) {
	            SessionTestStateObject.addL1StageMap(stage.getL1StageId(), l1StageName);
	        }
	    });

	    observableStageList.forEach(stage -> {
	        String l1StageId = stage.getL1StageId();
	        String l2StageId = stage.getL2StageId();
	        String l3StageId = stage.getL3StageId();
	        String l4StageId = stage.getL4StageId();
	        String l5StageId = stage.getL5StageId();

	        if (l2StageId != null && SessionTestStateObject.getL1StageMap().containsKey(l1StageId)) {
	            StageIdName l2StageObject = new StageIdName();
	            l2StageObject.setParentId(l1StageId);
	            l2StageObject.setStageId(l2StageId);
	            l2StageObject.setStageName(stage.getL2StageName());
	            if (l3StageId == null && stage.getTestTypeId() != null) {
	                l2StageObject.setTestTypeId(stage.getTestTypeId());
	            }
	            SessionTestStateObject.addL2StageMap(l2StageId, l2StageObject);
	        }

	        if (l3StageId != null && SessionTestStateObject.getL2StageMap().containsKey(l2StageId)) {
	            StageIdName l3StageObject = new StageIdName();
	            l3StageObject.setParentId(l2StageId);
	            l3StageObject.setStageId(l3StageId);
	            l3StageObject.setStageName(stage.getL3StageName());
	            if (l4StageId == null && stage.getTestTypeId() != null) {
	                l3StageObject.setTestTypeId(stage.getTestTypeId());
	            }
	            SessionTestStateObject.addL3StageMap(l3StageId, l3StageObject);
	        }

	        if (l4StageId != null && SessionTestStateObject.getL3StageMap().containsKey(l3StageId)) {
	            StageIdName l4StageObject = new StageIdName();
	            l4StageObject.setParentId(l3StageId);
	            l4StageObject.setStageId(l4StageId);
	            l4StageObject.setStageName(stage.getL4StageName());
	            if (l5StageId == null && stage.getTestTypeId() != null) {
	                l4StageObject.setTestTypeId(stage.getTestTypeId());
	            }
	            SessionTestStateObject.addL4StageMap(l4StageId, l4StageObject);
	        }

	        if (l5StageId != null && SessionTestStateObject.getL4StageMap().containsKey(l4StageId)) {
	            StageIdName l5StageObject = new StageIdName();
	            l5StageObject.setParentId(l4StageId);
	            l5StageObject.setStageId(l5StageId);
	            l5StageObject.setStageName(stage.getL5StageName());
	            if (stage.getTestTypeId() != null) {
	                l5StageObject.setTestTypeId(stage.getTestTypeId());
	            }
	            SessionTestStateObject.addL5StageMap(l5StageId, l5StageObject);
	        }
	    });

	    // Print statements (if needed, otherwise remove)
//	    SessionTestStateObject.getL1StageMap().forEach((key, value) -> System.out.println(key + "       " + value));
//	    SessionTestStateObject.getL2StageMap().forEach((key, value) -> System.out.println(key + "       " + value.getParentId() + "         " + value.getStageName()));
//	    SessionTestStateObject.getL3StageMap().forEach((key, value) -> System.out.println(key + "       " + value.getParentId() + "         " + value.getStageName()));
//	    SessionTestStateObject.getL4StageMap().forEach((key, value) -> System.out.println(key + "       " + value.getParentId() + "         " + value.getStageName()));
//	    SessionTestStateObject.getL5StageMap().forEach((key, value) -> System.out.println(key + "       " + value.getParentId() + "         " + value.getStageName()));
	}


	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		sessionTestingHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingHeadingGridPane.getRowConstraints().addAll(firstRow);
	
		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("SESSION TESTING");
		title.getStyleClass().add("session-testing-title");
		titleBox.getChildren().add(title);
		
		sessionTestingHeadingGridPane.add(titleBox, 0, 0);
		
		return sessionTestingHeadingGridPane;
	}

	private GridPane createSessionTestingLeftSide() {
		sessionTestingTreeviewGridPane.getStyleClass().add("session-testing-left-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		sessionTestingTreeviewGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingTreeviewGridPane.getRowConstraints().addAll(firstRow);
		
		sessionTestingTreeviewGridPane.add(createTreeView(), 0, 0);
		
		return sessionTestingTreeviewGridPane;
	}
	

	private GridPane createSessionTestingRightSide() {
//		sessionTestingListMainGridPane.getStyleClass().add("session-testing-right-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(80);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(20);
		

		sessionTestingListMainGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingListMainGridPane.getRowConstraints().addAll(firstRow, secondRow);
		sessionTestingListMainGridPane.setHgap(5);
	    sessionTestingListMainGridPane.setVgap(5);
		sessionTestingListMainGridPane.add(createTestListView(), 0, 0);
		sessionTestingListMainGridPane.add(createButtonBox(), 0, 1);
		
		return sessionTestingListMainGridPane;
	}

	private VBox createTestListView() {
	    selectAllCheckBox.getStyleClass().addAll("session-testing-checkbox","select-all-checkbox");
	    testListView.getStyleClass().add("session-testing-list-view");
	   
	    for (String test : testList) {
	        CheckBox newCheckBox = new CheckBox(test);
	        newCheckBox.getStyleClass().add("session-testing-checkbox");
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
	    testListVBox.getStyleClass().add("session-testing-right-container");
	    return testListVBox;
	}
	
	private HBox createButtonBox() {
		buttonHBox.getStyleClass().add("session-testing-right-container");
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
		
		startButton.setOnAction(e ->{
			fetchDataFromBackend();
		});
		
		repeatCountLabel.setText("Repeat Count");
		repeatCountLabel.getStyleClass().add("session-testing-repeat-count-label");
		repeatCountTextField.getStyleClass().add("session-testing-repeat-count-input");
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

	private GridPane createSessionTestingResultsGridPane() {
		sessionTestingResultsGridPane.getStyleClass().add("session-testing-result-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		sessionTestingResultsGridPane.getColumnConstraints().addAll(firstColumn);
		sessionTestingResultsGridPane.getRowConstraints().addAll(firstRow);
		
		sessionTestingResultsGridPane.add(createResultTableView(), 0, 0);
		
		return sessionTestingResultsGridPane;
	}
	
	private TreeView<Label> createTreeView() {
		TreeItem<Label> rootItem = new TreeItem<>();
		rootItem.setExpanded(true);
		rootItem.setGraphic(null);
		 		
		for(Map.Entry<String, String> l1_stage : SessionTestStateObject.getL1StageMap().entrySet()) {
//			System.out.println(l1_stage.getKey()+"      "+l1_stage.getValue());
			Label newL1StageLabel = new Label(l1_stage.getValue());
			newL1StageLabel.getStyleClass().add("l1_stage-label");
			TreeItem<Label> sessionItem = new TreeItem<Label>(newL1StageLabel);
			createL2Stage(sessionItem,l1_stage);
            rootItem.getChildren().add(sessionItem);
		}
		
		sessionTreeView.setRoot(rootItem);
		sessionTreeView.getStyleClass().add("session-tree-view");
		sessionTreeView.setShowRoot(false);
		
		sessionTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				Label selectedLabel = newValue.getValue();

				if (newValue.getChildren().isEmpty()) {
					System.out.println(selectedLabel.getText());
					System.out.println(selectedLabel.getId());
				}
			}
		});
		
		return sessionTreeView;
	}

	private void createL2Stage(TreeItem<Label> l1_root, Entry<String, String> l1_stage) {		
//		System.out.println();
		for(Map.Entry<String, StageIdName> l2_stage : SessionTestStateObject.getL2StageMap().entrySet()) {    
//			System.out.println(l1_stage.getKey() +"     "+l2_stage.getValue().getParentId()+"    "+l2_stage.getValue().getStageId()+"    "+l2_stage.getValue().getStageName());
			if(l1_stage.getKey().equals(l2_stage.getValue().getParentId())) {
				Label newL2StageLabel = new Label(l2_stage.getValue().getStageName());
				newL2StageLabel.setId(l2_stage.getKey());
				newL2StageLabel.getStyleClass().add("l1_stage-label");
	            TreeItem<Label> childItem = new TreeItem<>(newL2StageLabel);
	            createL3Stage(childItem , l2_stage);
	            l1_root.getChildren().add(childItem); 
			} 
		}
	}
	
	private void createL3Stage(TreeItem<Label> l2_root, Entry<String, StageIdName> l2_stage) {
//		System.out.println();
		for(Map.Entry<String, StageIdName> l3_stage : SessionTestStateObject.getL3StageMap().entrySet()) {    
//			System.out.println(l2_stage.getKey() +"     "+l3_stage.getValue().getParentId()+"    "+l3_stage.getValue().getStageId()+"    "+l3_stage.getValue().getStageName());
			if(l2_stage.getKey().equals(l3_stage.getValue().getParentId())) {
				Label newL3StageLabel = new Label(l3_stage.getValue().getStageName());
				newL3StageLabel.setId(l3_stage.getKey());
				newL3StageLabel.getStyleClass().add("l1_stage-label");
	            TreeItem<Label> childItem = new TreeItem<>(newL3StageLabel);
	            createL4Stage(childItem , l3_stage);
	            l2_root.getChildren().add(childItem); 
			} 
		}
	}
	
	private void createL4Stage(TreeItem<Label> l3_root, Entry<String, StageIdName> l3_stage) {
//		System.out.println();
		for(Map.Entry<String, StageIdName> l4_stage : SessionTestStateObject.getL4StageMap().entrySet()) {    
//			System.out.println(l3_stage.getKey() +"     "+l4_stage.getValue().getParentId()+"    "+l4_stage.getValue().getStageId()+"    "+l4_stage.getValue().getStageName());
			if(l3_stage.getKey().equals(l4_stage.getValue().getParentId())) {
				Label newL4StageLabel = new Label(l4_stage.getValue().getStageName());
				newL4StageLabel.setId(l4_stage.getKey());
				newL4StageLabel.getStyleClass().add("l1_stage-label");
	            TreeItem<Label> childItem = new TreeItem<>(newL4StageLabel);
	            createL5Stage(childItem , l4_stage);
	            l3_root.getChildren().add(childItem); 
			} 
		}
	}
	
	private void createL5Stage(TreeItem<Label> l4_root, Entry<String, StageIdName> l4_stage) {
//		System.out.println();
		for(Map.Entry<String, StageIdName> l5_stage : SessionTestStateObject.getL5StageMap().entrySet()) {    
//			System.out.println(l4_stage.getKey() +"     "+l5_stage.getValue().getParentId()+"    "+l5_stage.getValue().getStageId()+"    "+l5_stage.getValue().getStageName());
			if(l4_stage.getKey().equals(l5_stage.getValue().getParentId())) {
				Label newL5StageLabel = new Label(l5_stage.getValue().getStageName());
				newL5StageLabel.setId(l5_stage.getKey());
				newL5StageLabel.getStyleClass().add("l1_stage-label");
	            TreeItem<Label> childItem = new TreeItem<>(newL5StageLabel);
	            l4_root.getChildren().add(childItem); 
			} 
		}
	}

//	private void createL2Stage(TreeItem<Label> l1_root) {	
//		Label newLabel = l1_root.getValue();
//		if(newLabel.getText().equals("Session 1")) {
//			for (String l2_stage : l2_sessionList) {
//				Label newL2StageLabel = new Label(l2_stage);
//				newL2StageLabel.getStyleClass().add("l1_stage-label");
//	            TreeItem<Label> childItem = new TreeItem<>(newL2StageLabel);
//	            createL3Stage(childItem);
//	            l1_root.getChildren().add(childItem);
//	        }
//		}
//	}

//	private void createL3Stage(TreeItem<Label> l2_root) {
//		Label newLabel = l2_root.getValue();
//		if(newLabel.getText().equals("Sub Session 11")) {
//			for (String l3_stage : l3_sessionList) {
//				Label newL3StageLabel = new Label(l3_stage);
//				newL3StageLabel.getStyleClass().add("l1_stage-label");
//	            TreeItem<Label> childItem = new TreeItem<>(newL3StageLabel);
//	            createL4Stage(childItem);
//	            l2_root.getChildren().add(childItem);
//	        }
//		}
//	}
	

//	private void createL4Stage(TreeItem<Label> l3_root) {
//		Label newLabel = l3_root.getValue();
//		if(newLabel.getText().equals("Sub Session 111")) {
//			for (String l4_stage : l4_sessionList) {
//				Label newL4StageLabel = new Label(l4_stage);
//				newL4StageLabel.getStyleClass().add("l1_stage-label");
//	            TreeItem<Label> childItem = new TreeItem<>(newL4StageLabel);
//	            l3_root.getChildren().add(childItem);
//	        }
//		}
//	}
	

	private TableView<TestSummary> createResultTableView() {
		TableView<TestSummary> tableView = new TableView<>();
		tableView.getStylesheets()
		.add(getClass().getResource("/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		 tableView.setPrefHeight(900); 

		TableColumn<TestSummary, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<TestSummary, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<TestSummary, String> channelColumn = new TableColumn<>("Channel");
		channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
		setupchannelColumn(channelColumn);
		testSummaryList.addListener((ListChangeListener<? super TestSummary>) change -> {
			while (change.next()) {
			  if (change.wasAdded()) {
			      int lastIndex = testSummaryList.size()-1;
			  	Platform.runLater(() -> {
			          tableView.scrollTo(lastIndex);
			          tableView.getSelectionModel().select(lastIndex);
			          tableView.getFocusModel().focus(lastIndex);
			      });
			  }
			}
			});

		tableView.getColumns().addAll(fileNameColumn, resultColumn, channelColumn);
		tableView.setItems(testSummaryList); 
		
		return tableView;
	}

	private void setupchannelColumn(TableColumn<TestSummary, String> resultColumn) {
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setCellFactory(column -> new TableCell<TestSummary, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					if ("OK".equalsIgnoreCase(item)) {
						setText("Passed");
						setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}
	
	private void fetchDataFromBackend() {

	    new Thread(() -> {
			int i =0;
	        while (true) {
	            TestSummary newSummary = new TestSummary("file-"+i, "OK", "Channel");
	            
	            addTestSummary(newSummary);
	            
	            try {
	                Thread.sleep(9000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            i++;
	        }
	    }).start();
	}
	
	public void addTestSummary(TestSummary testSummary) {
	    Platform.runLater(() -> testSummaryList.add(testSummary));
	}

	
}





















//package com.teclever.dfcc.Controller.ui;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.mysql.cj.xdevapi.Result;
//import com.teclever.dfcc.model.TestSummary;
//import com.teclever.dfcc.stateMachine.StateMachine;
//import com.teclever.dfcc.stateMachine.StateMachine.TestResult;
//
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ListChangeListener;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TableCell;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import javafx.scene.control.TreeItem;
//import javafx.scene.control.TreeView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//import javafx.scene.layout.VBox;
//
//public class SessionTestingController {
//	private GridPane sessionTestingMainGridPane = new GridPane();
//	private GridPane sessionTestingHeadingGridPane = new GridPane();
//	private GridPane sessionTestingTreeviewGridPane = new GridPane();
//	private GridPane sessionTestingListMainGridPane = new GridPane();
//	private GridPane sessionTestingResultsGridPane = new GridPane();
//
//	private HBox titleBox = new HBox();
//	private Label title = new Label();
//	
//	private TreeView<Label> sessionTreeView = new TreeView<>();
//	
//	private ObservableList<String> l1_sessionList = FXCollections.observableArrayList("Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2","Session 1", "Session 2");
//	private ObservableList<String> l2_sessionList = FXCollections.observableArrayList("Sub Session 11", "Sub Session 12");
//	private ObservableList<String> l3_sessionList = FXCollections.observableArrayList("Sub Session 111", "Sub Session 112");
//	private ObservableList<String> l4_sessionList = FXCollections.observableArrayList("Sub Session 1111", "Sub Session 1112");
//	
//	private ObservableList<String> testList = FXCollections.observableArrayList("test-1", "test-2", "test-3", "test-4");
//	
//	private CheckBox selectAllCheckBox = new CheckBox("Select All");
//  private List<CheckBox> checkBoxes = new ArrayList<>();
//  private ListView<CheckBox> testListView = new ListView<>();
//  private VBox testListVBox = new VBox();
//  private HBox buttonHBox = new HBox(20);
//  private Button startButton = new Button("Start");
//  private Button stopButton = new Button("Stop");
//  private Button pauseButton = new Button("Pause");
//  
//  private HBox repeatCountVBox = new HBox(5);
//  private Label repeatCountLabel = new Label();
//  private TextField repeatCountTextField = new TextField();
//  
//  private ObservableList<TestResult> testSummaryList = FXCollections.observableArrayList();
//
//  
//	public GridPane createSessionTestingGridPane() {
//		sessionTestingMainGridPane.getStylesheets()
//				.add(getClass().getResource("/com/teclever/dfcc/ui/css/SessionTesting.css").toExternalForm());
//		sessionTestingMainGridPane.getStyleClass().add("session-testing-container");
//		
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(60);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(33);
//		
//		sessionTestingMainGridPane.setPadding(new Insets(10));
//		sessionTestingMainGridPane.setVgap(5);
//		sessionTestingMainGridPane.setHgap(5);
//		sessionTestingMainGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
//		sessionTestingMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
//		
//		sessionTestingMainGridPane.add(createHeadingBox(), 0, 0, 2, 1);
//		sessionTestingMainGridPane.add(createSessionTestingLeftSide(), 0, 1);
//		sessionTestingMainGridPane.add(createSessionTestingRightSide(), 1, 1);
//		sessionTestingMainGridPane.add(createSessionTestingResultsGridPane(), 0, 2, 2, 1);
//		
//		return sessionTestingMainGridPane;
//	}
//	
//	private GridPane createHeadingBox() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		sessionTestingHeadingGridPane.getColumnConstraints().addAll(firstColumn);
//		sessionTestingHeadingGridPane.getRowConstraints().addAll(firstRow);
//	
//		titleBox.setAlignment(Pos.CENTER_LEFT);
//		title.setText("SESSION TESTING");
//		title.getStyleClass().add("session-testing-title");
//		titleBox.getChildren().add(title);
//		
//		sessionTestingHeadingGridPane.add(titleBox, 0, 0);
//		
//		return sessionTestingHeadingGridPane;
//	}
//
//	private GridPane createSessionTestingLeftSide() {
//		sessionTestingTreeviewGridPane.getStyleClass().add("session-testing-left-container");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		sessionTestingTreeviewGridPane.getColumnConstraints().addAll(firstColumn);
//		sessionTestingTreeviewGridPane.getRowConstraints().addAll(firstRow);
//		
//		sessionTestingTreeviewGridPane.add(createTreeView(), 0, 0);
//		
//		return sessionTestingTreeviewGridPane;
//	}
//	
//
//	private GridPane createSessionTestingRightSide() {
////		sessionTestingListMainGridPane.getStyleClass().add("session-testing-right-container");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(80);
//		
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(20);
//		
//
//		sessionTestingListMainGridPane.getColumnConstraints().addAll(firstColumn);
//		sessionTestingListMainGridPane.getRowConstraints().addAll(firstRow, secondRow);
//		sessionTestingListMainGridPane.setHgap(5);
//	    sessionTestingListMainGridPane.setVgap(5);
//		sessionTestingListMainGridPane.add(createTestListView(), 0, 0);
//		sessionTestingListMainGridPane.add(createButtonBox(), 0, 1);
//		
//		return sessionTestingListMainGridPane;
//	}
//
//	private VBox createTestListView() {
//	    selectAllCheckBox.getStyleClass().addAll("session-testing-checkbox","select-all-checkbox");
//	    testListView.getStyleClass().add("session-testing-list-view");
//	   
//	    for (String test : testList) {
//	        CheckBox newCheckBox = new CheckBox(test);
//	        newCheckBox.getStyleClass().add("session-testing-checkbox");
//	        newCheckBox.setWrapText(true);
//	        checkBoxes.add(newCheckBox);
//	        testListView.getItems().add(newCheckBox);
//	        
//	        newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//	        	boolean anySelected = false;
//	        	for(CheckBox checkBox : checkBoxes) {
//	        		if(checkBox.isSelected()) {
//	        			anySelected = true;
//	        		}
//	        	}
//	        	if(anySelected) {
//	        		startButton.setDisable(false);
//	        	}else {
//	        		startButton.setDisable(true);
//	        	}
//	        });
//	    }
//
//	    selectAllCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//	        for (CheckBox checkBox : checkBoxes) {
//	            checkBox.setSelected(newValue);
//	        }
//	        if(newValue) {
//	        	startButton.setDisable(false);
//	        }else {
//				startButton.setDisable(true);
//			}
//	        
//	    });
//	    	  
//	    testListVBox.getChildren().addAll(selectAllCheckBox,testListView);
//	    testListVBox.getStyleClass().add("session-testing-right-container");
//	    return testListVBox;
//	}
//	
//	private HBox createButtonBox() {
//		buttonHBox.getStyleClass().add("session-testing-right-container");
//	    Image playImage = new Image(getClass().getResourceAsStream("/Resources/Images/play.png"));
//	    Image stopImage = new Image(getClass().getResourceAsStream("/Resources/Images/stop.png"));
//	    Image pauseImage = new Image(getClass().getResourceAsStream("/Resources/Images/pause.png"));
//
//	    ImageView playImageView = new ImageView(playImage);
//	    playImageView.getStyleClass().add("button-image");
//	    playImageView.setFitHeight(25);
//	    playImageView.setFitWidth(25);
//	    playImageView.setPreserveRatio(true);
//	    playImageView.setSmooth(true);
//	    
//		ImageView stopImageView = new ImageView(stopImage);
//		stopImageView.getStyleClass().add("button-image");
//		stopImageView.setFitHeight(25);
//		stopImageView.setFitWidth(25);
//		stopImageView.setPreserveRatio(true);
//		stopImageView.setSmooth(true);
//		
//		ImageView pauseImageView = new ImageView(pauseImage);
//		pauseImageView.getStyleClass().add("button-image");
//		pauseImageView.setFitHeight(25);
//		pauseImageView.setFitWidth(25);
//		pauseImageView.setPreserveRatio(true);
//		pauseImageView.setSmooth(true);
//		
//
//		startButton.setGraphic(playImageView);
//		startButton.setGraphicTextGap(10);
//		stopButton.setGraphic(stopImageView);
//		stopButton.setGraphicTextGap(10);
//		pauseButton.setGraphic(pauseImageView);
//		pauseButton.setGraphicTextGap(10);
//		
//		startButton.setDisable(true);
//		stopButton.setDisable(true);
//		pauseButton.setDisable(true);
//		
//		startButton.setOnAction(e ->{
//			fetchDataFromBackend();
//		});
//		
//		repeatCountLabel.setText("Repeat Count");
//		repeatCountLabel.getStyleClass().add("session-testing-repeat-count-label");
//		repeatCountTextField.getStyleClass().add("session-testing-repeat-count-input");
//		repeatCountTextField.setText("1");
//		
//		repeatCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//          if (!newValue.matches("\\d*")) {
//              repeatCountTextField.setText(oldValue);
//          } else if (newValue.length() > 3) {
//              repeatCountTextField.setText(oldValue);
//          }
//      });
//		 
//		repeatCountVBox.setAlignment(Pos.CENTER);
//		repeatCountVBox.getChildren().addAll(repeatCountLabel,repeatCountTextField);
//		
//		buttonHBox.setAlignment(Pos.CENTER);
//		buttonHBox.getChildren().addAll(repeatCountVBox,startButton,pauseButton,stopButton);
//		return buttonHBox;
//	}
//
//	private GridPane createSessionTestingResultsGridPane() {
//		sessionTestingResultsGridPane.getStyleClass().add("session-testing-result-container");
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		sessionTestingResultsGridPane.getColumnConstraints().addAll(firstColumn);
//		sessionTestingResultsGridPane.getRowConstraints().addAll(firstRow);
//		
//		sessionTestingResultsGridPane.add(createResultTableView(), 0, 0);
//		
//		return sessionTestingResultsGridPane;
//	}
//	
//	private TreeView<Label> createTreeView() {
//		TreeItem<Label> rootItem = new TreeItem<>();
//		rootItem.setExpanded(true);
//		rootItem.setGraphic(null);
//		 
//		for( String l1_stage : l1_sessionList) {
//			Label newL1StageLabel = new Label(l1_stage);
//			newL1StageLabel.getStyleClass().add("l1_stage-label");
//			TreeItem<Label> sessionItem = new TreeItem<Label>(newL1StageLabel);
//			createL2Stage(sessionItem);
//          rootItem.getChildren().add(sessionItem);
//		}
//		
//		sessionTreeView.setRoot(rootItem);
//		sessionTreeView.getStyleClass().add("session-tree-view");
//		sessionTreeView.setShowRoot(false);
//		
//		sessionTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			if(newValue != null) {
//				Label selectedLabel = newValue.getValue();
//
//				if (newValue.getChildren().isEmpty()) {
//					System.out.println(selectedLabel.getText());
//				}
//			}
//		});
//		
//		return sessionTreeView;
//	}
//
//	private void createL2Stage(TreeItem<Label> l1_root) {	
//		Label newLabel = l1_root.getValue();
//		if(newLabel.getText().equals("Session 1")) {
//			for (String l2_stage : l2_sessionList) {
//				Label newL2StageLabel = new Label(l2_stage);
//				newL2StageLabel.getStyleClass().add("l1_stage-label");
//	            TreeItem<Label> childItem = new TreeItem<>(newL2StageLabel);
//	            createL3Stage(childItem);
//	            l1_root.getChildren().add(childItem);
//	        }
//		}
//	}
//
//	private void createL3Stage(TreeItem<Label> l2_root) {
//		Label newLabel = l2_root.getValue();
//		if(newLabel.getText().equals("Sub Session 11")) {
//			for (String l3_stage : l3_sessionList) {
//				Label newL3StageLabel = new Label(l3_stage);
//				newL3StageLabel.getStyleClass().add("l1_stage-label");
//	            TreeItem<Label> childItem = new TreeItem<>(newL3StageLabel);
//	            createL4Stage(childItem);
//	            l2_root.getChildren().add(childItem);
//	        }
//		}
//	}
//	
//	private void createL4Stage(TreeItem<Label> l3_root) {
//		Label newLabel = l3_root.getValue();
//		if(newLabel.getText().equals("Sub Session 111")) {
//			for (String l4_stage : l4_sessionList) {
//				Label newL4StageLabel = new Label(l4_stage);
//				newL4StageLabel.getStyleClass().add("l1_stage-label");
//	            TreeItem<Label> childItem = new TreeItem<>(newL4StageLabel);
//	            l3_root.getChildren().add(childItem);
//	        }
//		}
//	}
//	
//	
//	
//	private TableView<StateMachine.TestResult> createResultTableView() {
//		TableView<StateMachine.TestResult> tableView = new TableView<>();
//		tableView.getStylesheets()
//		.add(getClass().getResource("/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());
//		tableView.getStyleClass().add("check-sum-table");
//		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//		
//		 tableView.setPrefHeight(900); 
//
//		TableColumn<StateMachine.TestResult, String> fileNameColumn = new TableColumn<>("File Name");
//		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
//		fileNameColumn.setReorderable(false);
//		fileNameColumn.setSortable(false);
//		fileNameColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<StateMachine.TestResult, String> resultColumn = new TableColumn<>("Result");
//		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
//		resultColumn.setReorderable(false);
//		resultColumn.setSortable(false);
//		resultColumn.setStyle("-fx-alignment: CENTER;");
//		
//		TableColumn<StateMachine.TestResult, String> faultPinColumn = new TableColumn<>("Fault Pin Suggestion");
//		faultPinColumn.setCellValueFactory(new PropertyValueFactory<>("faultPinSuggestion"));
//		faultPinColumn.setReorderable(false);
//		faultPinColumn.setSortable(false);
//		faultPinColumn.setStyle("-fx-alignment: CENTER;");
//		
//		TableColumn<StateMachine.TestResult, String> interfaceSignalColumn = new TableColumn<>("Interface Signal");
//		interfaceSignalColumn.setCellValueFactory(new PropertyValueFactory<>("interfaceSignal"));
//		interfaceSignalColumn.setReorderable(false);
//		interfaceSignalColumn.setSortable(false);
//		interfaceSignalColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<StateMachine.TestResult, String> channelColumn = new TableColumn<>("Channel");
//		channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
//		channelColumn.setReorderable(false);
//		channelColumn.setSortable(false);
//		channelColumn.setStyle("-fx-alignment: CENTER;");
//		
//		StateMachine.getTestFiles().addListener((ListChangeListener<? super StateMachine.TestResult>) change -> {
//          while (change.next()) {
//              if (change.wasAdded()) {
//                  int lastIndex = StateMachine.getTestFiles().size()-1;
//              	Platform.runLater(() -> {
//                      tableView.scrollTo(lastIndex);
//                      tableView.getSelectionModel().select(lastIndex);
//                      tableView.getFocusModel().focus(lastIndex);
//                  });
//              }
//          }
//      });	
//		
//		
//		tableView.getColumns().addAll(fileNameColumn, resultColumn, faultPinColumn, interfaceSignalColumn, channelColumn  );
//		tableView.setItems(StateMachine.getTestFiles()); 
//		
//		return tableView;
//	}
//
//	private void fetchDataFromBackend() {
//	    new Thread(() -> {
//			int i =0;
//	        while (true) {
//	            StateMachine.TestResult newTestResult = new StateMachine.TestResult("file-"+i, "OK", "falut pin-"+i, "interface-"+i,"Channel");
//	            Platform.runLater(() -> StateMachine.addTestFile(newTestResult));
//	            try {
//	                Thread.sleep(2000);
//	            } catch (InterruptedException e) {
//	                e.printStackTrace();
//	            }
//	            i++;
//	        }
//	    }).start();
//	}
//	
//}










