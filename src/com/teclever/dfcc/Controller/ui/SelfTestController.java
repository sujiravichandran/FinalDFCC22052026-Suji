package com.teclever.dfcc.Controller.ui;

import java.util.List;
import java.util.Random;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestCardData;
import com.teclever.dfcc.stateMachine.SelfTestStateObject.SelfTestResult;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SelfTestController {

	private GridPane selfTestMainContainerGridPane = new GridPane();

	private HBox headingHbox = new HBox(10);

	private HBox topButton = new HBox(10);

	private GridPane headingGridPane = new GridPane();

	private GridPane midContainerGridPane = new GridPane();

	private HBox midTopHbox1 = new HBox(10);

	private HBox midTopHbox2 = new HBox(10);

	private VBox midTopVbox1 = new VBox(10);

	private VBox midTopVbox2 = new VBox(10);

	private VBox midTopVbox3 = new VBox(10);


	private Label pageHeading = new Label("SELF TEST");
	private Label rack2 = new Label("cPCI");
	private Label rack1 = new Label("RACK-1");
	
	private TableView<SelfTestResult> selfTestTable = new TableView<>();

	public GridPane createSelfTestMainContainerGridPane() {

		selfTestMainContainerGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/SelfTest.css").toExternalForm());
		selfTestMainContainerGridPane.getStyleClass().add("selfTest-main-container");

		getData();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);

		selfTestMainContainerGridPane.setVgap(5);

		selfTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		selfTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		selfTestMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
		selfTestMainContainerGridPane.add(headingGridPane(), 0, 0);
		selfTestMainContainerGridPane.add(selfTestMidContainer(), 0, 1);
		selfTestMainContainerGridPane.add(selfTestBottomContainer(), 0, 2);

		return selfTestMainContainerGridPane;
	}

	private void getData() {
		List<StageObject> stageList = StateMachine.getStageDatalist();
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);


		 observableStageList.stream()
         .filter(stage -> "Self Test".equalsIgnoreCase(stage.getL1StageName()))
         .forEach(stage -> {
             SelfTestCardData newCard = new SelfTestCardData(stage.getL3StageId(), stage.getL3StageName(), 0);
             if ("RACK-1".equalsIgnoreCase(stage.getL2StageName())) {
                 SelfTestStateObject.addSelfTestRack1Card(newCard);
             } else if ("cPCI".equalsIgnoreCase(stage.getL2StageName())) {
                 SelfTestStateObject.addSelfTestcPCICard(newCard);
             }
         });
		
		
//		for (StageObject stage : observableStageList) {
// 
//            String l1Id = stage.getL1StageId();
//            String l1Name = stage.getL1StageName();
//
//            String l2Id = stage.getL2StageId();
//            String l2Name = stage.getL2StageName();
//
//            String l3Id = stage.getL3StageId();
//            String l3Name = stage.getL3StageName();
//
//            // Ensure the structure for each level
//            hierarchyMap.computeIfAbsent(l1Id, k -> new HashMap<>())
//                        .computeIfAbsent(l2Id, k -> new ArrayList<>())
//                        .add(l3Id + " - " + l3Name);
//        }
//
//		for (String l1Key : hierarchyMap.keySet()) {
//			System.out.println("Level 1: " + l1Key);
//			Map<String, List<String>> l2Map = hierarchyMap.get(l1Key);
//
//			for (String l2Key : l2Map.keySet()) {
//				System.out.println("  Level 2: " + l2Key);
//				List<String> l3List = l2Map.get(l2Key);
//
//				for (String l5Value : l3List) {
//					System.out.println("        Level 3: " + l5Value);
//				}
//			}
//
//		}
	}

	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);
		headingGridPane.getStyleClass().add("selfTest-top-container");

		headingGridPane.add(headingHbox(), 0, 0);
		headingGridPane.add(topButton(), 1, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {

		pageHeading.getStyleClass().add("headerLabel");
//		pageHeading.setPadding(new Insets(0, 0, 0, 0));
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	private HBox topButton() {
		Button startTest = new Button("START TEST");
		topButton.setPadding(new Insets(0, 5, 0, 0));
		topButton.setAlignment(Pos.CENTER_RIGHT);
		topButton.getChildren().add(startTest);
		
//		startTest.setOnAction(e ->{		
//			SelfTestStateObject.updateSelfTestRack1Cardstatus("L3_"+answer, null, 1);
//			SelfTestStateObject.updateSelfTestRack1Cardstatus("L3_211", null, 1);
//			SelfTestStateObject.updateSelfTestcPCICardstatus("L3_221", null, 1);
//		});
		
		startTest.setOnAction(e -> {
		    Timeline timeline = new Timeline();
		    for (int i = 200; i <= 218; i++) {
		        int index = i;
		        KeyFrame keyFrame = new KeyFrame(Duration.seconds(i - 200+3), event -> {
		            Platform.runLater(() -> {
		            	Random random = new Random();
		            	int randomValue = random.nextInt(2) + 1;
		                SelfTestStateObject.updateSelfTestRack1Cardstatus("L3_" + index, null, randomValue);
		                String result ;
		                if(randomValue == 1) {
		                	result = "ok";
		                }else {
		                	result = "not ok";
		                }
		                SelfTestStateObject.addSelfTestResult(new SelfTestStateObject.SelfTestResult("L3_" + index, "File-"+index, result));
		            });
		        });
		        timeline.getKeyFrames().add(keyFrame);
		    }
		    for (int i = 219; i <= 221; i++) {
		        int index = i;
		        KeyFrame keyFrame = new KeyFrame(Duration.seconds(i - 200+3), event -> {
		            Platform.runLater(() -> {
		            	Random random = new Random();
		            	int randomValue = random.nextInt(2) + 1;
		                SelfTestStateObject.updateSelfTestcPCICardstatus("L3_" + index, null, randomValue);
		            });
		        });
		        timeline.getKeyFrames().add(keyFrame);
		    }
		    timeline.play();
		});


		
		return topButton;
	}

	private GridPane selfTestMidContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33.33);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33.33);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33.33);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(8);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(92);

		midContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		midContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);

		midContainerGridPane.add(midTopHbox1(), 0, 0, 2, 1);

		midContainerGridPane.add(midTopHbox2(), 2, 0);

		midContainerGridPane.add(midTopVbox1(), 0, 1);

		midContainerGridPane.add(midTopVbox2(), 1, 1);

		midContainerGridPane.add(midTopVbox3(), 2, 1);

		midContainerGridPane.setHgap(5);

		midContainerGridPane.setVgap(5);

		midContainerGridPane.getStyleClass().add("selfTest-mid-Container");
		midContainerGridPane.setAlignment(Pos.CENTER);
		return midContainerGridPane;
	}

	private HBox midTopHbox1() {

		rack1.getStyleClass().add("racklabel");
		midTopHbox1.getStyleClass().add("rackhbox");
		midTopHbox1.setAlignment(Pos.CENTER);
		midTopHbox1.getChildren().add(rack1);

		return midTopHbox1;
	}

	private HBox midTopHbox2() {

		rack2.getStyleClass().add("racklabel");

		midTopHbox2.getStyleClass().add("rackhbox");
		midTopHbox2.setAlignment(Pos.CENTER);
		midTopHbox2.getChildren().add(rack2);

		return midTopHbox2;
	}

	private VBox midTopVbox1() {
		ObservableList<SelfTestStateObject.SelfTestCardData> rack1LeftList= SelfTestStateObject.getSelfTestRack1Card();
	
		    midTopVbox1.setPadding(new Insets(0, 5, 0, 5));
		    midTopVbox1.getStyleClass().add("rackVbox");
		    midTopVbox1.setAlignment(Pos.CENTER);
		    
		    int count = 8;
		    for (int i = 0; i <= count; i++) {
		        SelfTestStateObject.SelfTestCardData cardData = rack1LeftList.get(i);
		        Label label = new Label(cardData.getCardName());
		        label.setId(cardData.getCardId());
		        label.getStyleClass().add("selTest-label-left");
		        label.setMaxWidth(Double.MAX_VALUE);
		        label.setPrefHeight(35);
		        label.setPadding(new Insets(5));
		        
		        cardData.statusProperty().addListener((observable, oldValue, newValue) -> {        	
		        	if(newValue.intValue() == 1) {
		        		label.setStyle("-fx-background-color:green;");	
		        	}else if(newValue.intValue() == 2) {
		        		label.setStyle("-fx-background-color:red;");	
		        	}
		        });
		        

		        
		        midTopVbox1.getChildren().add(label);	  
		        
		    }
		return midTopVbox1;
	}

	private VBox midTopVbox2() {
		ObservableList<SelfTestStateObject.SelfTestCardData> rack1RightList= SelfTestStateObject.getSelfTestRack1Card();
				
	    midTopVbox2.setPadding(new Insets(0, 5, 0, 5));
	    midTopVbox2.getStyleClass().add("rackVbox");
	    midTopVbox2.setAlignment(Pos.CENTER);
	    
	    int count = 18;
	    for (int i = 9; i <= count; i++) {
	        SelfTestStateObject.SelfTestCardData cardData = rack1RightList.get(i);
	        Label label = new Label(cardData.getCardName());
	        label.setId(cardData.getCardId());
	        label.getStyleClass().add("selTest-label-left");
	        label.setMaxWidth(Double.MAX_VALUE);
	        label.setPrefHeight(32);
	        label.setPadding(new Insets(0 ,5, 0, 5));
	        
	        cardData.statusProperty().addListener((observable, oldValue, newValue) -> {
	        	if(newValue.intValue() == 1) {
	        		label.setStyle("-fx-background-color:green;");	
	        	}else if(newValue.intValue() == 2) {
	        		label.setStyle("-fx-background-color:red;");	
	        	}
	        });
	        
	        midTopVbox2.getChildren().add(label);
	    }
	return midTopVbox2;
	}

	private VBox midTopVbox3() {
		ObservableList<SelfTestStateObject.SelfTestCardData> cPCIList= SelfTestStateObject.getSelfTestcPCICard();
		
		
	    midTopVbox3.setPadding(new Insets(0, 5, 0, 5));
	    midTopVbox3.getStyleClass().add("rackVbox");
	    midTopVbox3.setAlignment(Pos.CENTER);
	    
	    int count = 2;
	    for (int i = 0; i <= count; i++) {
	        SelfTestStateObject.SelfTestCardData cardData = cPCIList.get(i);
	        Label label = new Label(cardData.getCardName());
	        label.setId(cardData.getCardId());
	        label.getStyleClass().add("selTest-label-left");
	        label.setMaxWidth(Double.MAX_VALUE);
	        label.setPrefHeight(35);
	        label.setPadding(new Insets(5));
	        
	        cardData.statusProperty().addListener((observable, oldValue, newValue) -> {
	        	if(newValue.intValue() == 1) {
	        		label.setStyle("-fx-background-color:green;");	
	        	}else if(newValue.intValue() == 2) {
	        		label.setStyle("-fx-background-color:red;");	
	        	}
	        });
	        
	        midTopVbox3.getChildren().add(label);
	    }
		return midTopVbox3;
	}
	
	private TableView<SelfTestResult> selfTestBottomContainer() {
		selfTestTable = createTableView();
		
		return selfTestTable;

	}

	private TableView<SelfTestResult> createTableView() {
		TableView<SelfTestResult> tableView = new TableView<>();
		tableView.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setPrefHeight(900);

		TableColumn<SelfTestResult, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<SelfTestResult, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setStyle("-fx-alignment: CENTER;");
		rewriteColumn(resultColumn);
		
		SelfTestStateObject.getTestResults().addListener((ListChangeListener<? super SelfTestResult>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					int lastIndex = SelfTestStateObject.getTestResults().size() - 1;
					Platform.runLater(() -> {
						tableView.scrollTo(lastIndex);
						tableView.getSelectionModel().select(lastIndex);
						tableView.getFocusModel().focus(lastIndex);
					});
				}
			}
		});
		
		tableView.getColumns().addAll(fileNameColumn, resultColumn);
		tableView.setItems(SelfTestStateObject.getTestResults());

		return tableView;
	}
	
	private void rewriteColumn(TableColumn<SelfTestResult, String> resultColumn) {
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setCellFactory(column -> new TableCell<SelfTestResult, String>() {
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


}
