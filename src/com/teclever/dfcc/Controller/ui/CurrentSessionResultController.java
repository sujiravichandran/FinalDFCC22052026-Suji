package com.teclever.dfcc.Controller.ui;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsResponse;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.model.SessionData;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class SessionDataTableViewFactory implements TableViewFactory<SessionData> {
	@Override
	public CustomTableView<SessionData> createTableView(ObservableList<SessionData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, SessionData.class, addUserColumn, addCheckboxColumn);
	}
}

public class CurrentSessionResultController {

    private GridPane currentSessionResultGridPane = new GridPane();
    private GridPane currentSessionResultHeadingGridPane = new GridPane();
    private GridPane currentSessionResultTableGridPane = new GridPane();
	
    private HBox titleBox = new HBox();
	private Label title = new Label();
	private HBox buttonBox = new HBox();
	private Button viewAllButton = new Button("View All Stages");
	
	private ScrollPane tableScrollPane = new ScrollPane();
	
	 private ProgressIndicator progressIndicator = new ProgressIndicator();
	 private VBox box = new VBox();
	
	private ObservableList<SessionData> sessionDataList = FXCollections.observableArrayList();
	
	private TableViewFactory<SessionData> sessionDataFactory = new SessionDataTableViewFactory();
	private CustomTableView<SessionData> sessionDataTableView ;
	
	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	
	private String SESSION_ID ;
	private UserCenterContentController userCenterContentController = UserCenterContentController.getInstance();

	
//    public GridPane createCurrentSessionResultGridPane(String id) {
//    	SESSION_ID = id ;
//		getCurrentSessionResultData();
//    	currentSessionResultGridPane.getStylesheets()
//				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/CurrentExecutionResults.css").toExternalForm());
//    	currentSessionResultGridPane.getStyleClass().add("current-execution-result-container");
//
//        ColumnConstraints firstColumn = new ColumnConstraints();
//        firstColumn.setPercentWidth(100);
//
//        RowConstraints firstRow = new RowConstraints();
//        firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(93);
//
//		currentSessionResultGridPane.setPadding(new Insets(5));
//		currentSessionResultGridPane.getColumnConstraints().addAll(firstColumn);
//		currentSessionResultGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//
////		currentSessionResultGridPane.add(createHeadingBox(), 0, 0);
//		Label stageNameLabel = new Label(UserCenterContentController.currentSessionName);
//		stageNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
//		HBox titleBar = new HBox(stageNameLabel);
//		titleBar.setAlignment(Pos.CENTER);
//		titleBar.setPadding(new Insets(10, 0, 10, 0));
//		currentExecutionResultGridPane.add(titleBar, 0, 0);
//
//		currentSessionResultGridPane.add(createCurrentSessionResultTableGridPane(), 0, 2);
//        return currentSessionResultGridPane;
//    }
	// srini 2/8/25
	public GridPane createCurrentSessionResultGridPane(String id) {
	    SESSION_ID = id;

	    // Step 1: Fetch session data BEFORE UI components are loaded.
	    getCurrentSessionResultData();

	    // Step 2: Apply CSS styles
	    currentSessionResultGridPane.getStylesheets()
	            .add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CurrentExecutionResults.css")
	            .toExternalForm());
	    currentSessionResultGridPane.getStyleClass().add("current-execution-result-container");

	    // Step 3: Configure layout constraints
	    ColumnConstraints column = new ColumnConstraints();
	    column.setPercentWidth(100);
	    currentSessionResultGridPane.getColumnConstraints().add(column);

	    RowConstraints row1 = new RowConstraints();
	    row1.setPercentHeight(7);
	    RowConstraints row2 = new RowConstraints();
	    row2.setPercentHeight(93);
	    currentSessionResultGridPane.getRowConstraints().addAll(row1, row2);

	    currentSessionResultGridPane.setPadding(new Insets(5));

	    // Step 4: Create and add TitleBar (with session name)
	    
	    currentSessionResultGridPane.add(createHeadingBox(), 0, 0);

	    Label sessionTitleLabel = new Label(UserCenterContentController.currentSessionName);
	    sessionTitleLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
	    HBox titleBar = new HBox(sessionTitleLabel);
	    titleBar.setAlignment(Pos.CENTER);
	    titleBar.setPadding(new Insets(10, 0, 10, 0));
	    currentSessionResultGridPane.add(titleBar, 0, 0); // Title at Row 0

	    // Step 5: Add table (in a wrapper GridPane)
	    currentSessionResultGridPane.add(createCurrentSessionResultTableGridPane(), 0, 1); // Table at Row 1

	    return currentSessionResultGridPane;
	}
  
    private void showProgressIndicator() {
		StackPane parentStackPane= (StackPane) currentSessionResultTableGridPane.getParent().getParent();
		box.getChildren().add(progressIndicator);
		box.setAlignment(Pos.CENTER);
		parentStackPane.getChildren().add(box);
	}
	

	private void hideProgressIndicator() {
		StackPane parentStackPane= (StackPane) currentSessionResultTableGridPane.getParent().getParent();
		if(parentStackPane.getChildren().contains(box)) {
			parentStackPane.getChildren().remove(box);
		}
	}
    
//    private void getCurrentSessionResultData() {
//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                
//                	ResultSessionStagesDetailsResponse response = resultExecutionManagement.getStagesDetailsForSession(SESSION_ID);
//            		System.out.println("getCurrentSessionResultData" + SESSION_ID);
//            		System.out.println("Response Code" + response.getCode() );
//            		System.out.println("getResultSessionStagesDetailsDTOList" + response.getResultSessionStagesDetailsDTOList().size());
//            		if(response.getCode() == 1 && response.getResultSessionStagesDetailsDTOList() != null) {
//            			int i = 1;
//            			for(ResultSessionStagesDetailsDTO data : response.getResultSessionStagesDetailsDTOList()) {
//            				SessionData newSessionData = new SessionData();
//            				
//            				newSessionData.setId(data.getStageId());
//            				newSessionData.setTestMode(data.getTestMode());
//            				newSessionData.setSlNo(String.valueOf(i));
//            				newSessionData.setStage(data.getStage());
//            				newSessionData.setStartTime(data.getStartTime());
//            				newSessionData.setEndTime(data.getEndTime());
//            				newSessionData.setStatus(data.getStatus());
//            				newSessionData.setResult(data.getResult());
//            				newSessionData.setTimeTakenForExecution(data.getTimeTakenForExecution());
//            				newSessionData.setNoOfFilesExecuted(String.valueOf(data.getNoOfFilesExecuted()));
//            				newSessionData.setFailedFiles(String.valueOf(data.getFailedFiles()));
//            				i++;
//            				sessionDataList.add(newSessionData);
//            			}
//            			System.out.println("sessionDataList" + sessionDataList.size());
//            		}else if(response.getCode() == 0) {
//            			Notifications.showErrorAlert(response.geteMsg());
//            		}
//                  
//                if (sessionDataList.size() > 0) {
//                	System.out.println("sessionDataList Inside" + sessionDataList.size());
//	                Platform.runLater(() -> createCurrentSessionResultTable());
//	            }
//                
//                return null;
//            }
//        };
//
//        task.setOnFailed(evt -> {
//            hideProgressIndicator();
//            System.out.println("Entred setOnFailed");
//            task.getException().printStackTrace();
//        });
//
//        task.setOnSucceeded(evt ->
//        hideProgressIndicator());
//
//        task.setOnRunning(evt -> {
//            if (DFCCConstant.isJarBuild) {
//            	System.out.println("Entred setOnRunning");
//                showProgressIndicator();
//            }
//        });
//
//        new Thread(task).start();
//    }
    
	private void getCurrentSessionResultData() {
		ResultSessionStagesDetailsResponse response = resultExecutionManagement.getStagesDetailsForSession(SESSION_ID);
		if(response.getCode() == 1 && response.getResultSessionStagesDetailsDTOList() != null) {
			int i = 1;
			for(ResultSessionStagesDetailsDTO data : response.getResultSessionStagesDetailsDTOList()) {
				
				
				//Karthik
				String formattedStartDateTime = "";
				String formattedEndDateTime = "";

				if (!data.getStartTime().equals("") && !data.getEndTime().equals("")) {

					DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT; // Use ISO_INSTANT for Instant parsing

					// Parse both start and end times as Instant (supports nanoseconds).
					String startDateTime = data.getStartTime();
					String endDateTime = data.getEndTime();

					if (!startDateTime.endsWith("Z")) {
						startDateTime += "Z";
					}
					if (!endDateTime.endsWith("Z")) {
						endDateTime += "Z";
					}

					Instant d1 = Instant.from(formatter.parse(startDateTime));
					Instant d2 = Instant.from(formatter.parse(endDateTime));
					// MANI
					DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
					// Format the parsed instants into the desired format
					formattedStartDateTime = d1.atZone(ZoneId.of("UTC")).format(outputFormatter);
					formattedEndDateTime = d2.atZone(ZoneId.of("UTC")).format(outputFormatter);
				} else {
					formattedStartDateTime = data.getStartTime();  // Setting empty string for now need to revisit
					formattedEndDateTime = data.getEndTime();
					
				}
				
				 
				
				SessionData newSessionData = new SessionData();
				
				newSessionData.setId(data.getStageId());
	
				newSessionData.setTestMode(data.getTestMode());
				newSessionData.setSlNo(String.valueOf(i));
				newSessionData.setStage(data.getStage());
//				newSessionData.setStartTime(data.getStartTime());
//				newSessionData.setEndTime(data.getEndTime());
				newSessionData.setStartTime(formattedStartDateTime);  //Karthik
				newSessionData.setEndTime(formattedEndDateTime);	//Karthik
				newSessionData.setStatus(data.getStatus());
				newSessionData.setResult(data.getResult());
				newSessionData.setTimeTakenForExecution(data.getTimeTakenForExecution());
				newSessionData.setNoOfFilesExecuted(String.valueOf(data.getNoOfFilesExecuted()));
				newSessionData.setFailedFiles(String.valueOf(data.getFailedFiles()));
				i++;
				sessionDataList.add(newSessionData);
			}
		}else if(response.getCode() == 0) {
			Notifications.showErrorAlert(response.geteMsg());
		}
	}
    
   

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		currentSessionResultHeadingGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		currentSessionResultHeadingGridPane.getRowConstraints().addAll(firstRow);
	
		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("SESSION RESULTS");
		title.getStyleClass().add("current-execution-result-title");
		titleBox.getChildren().add(title);
		
		currentSessionResultHeadingGridPane.add(titleBox, 0, 0);
		currentSessionResultHeadingGridPane.add(createViewAllButtonButton(), 1, 0);
		
		return currentSessionResultHeadingGridPane;
	}
	
	public HBox createViewAllButtonButton() {
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(viewAllButton);
		
		viewAllButton.setOnAction(e ->{
			
			GridPane bottomMidTopGridPane = (GridPane) currentSessionResultGridPane.getParent().getParent().getParent();
			userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Stage Results", SESSION_ID,null);							

		});
		
		return buttonBox;
	}
	
	private GridPane createCurrentSessionResultTableGridPane() {
		currentSessionResultTableGridPane.getStyleClass().add("current-execution-result-tabs-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		currentSessionResultTableGridPane.setPadding(new Insets(5));
		
		currentSessionResultTableGridPane.getColumnConstraints().addAll(firstColumn);
		currentSessionResultTableGridPane.getRowConstraints().addAll(firstRow);
		
		currentSessionResultTableGridPane.add(createCurrentSessionResultTable(), 0, 0);
		return currentSessionResultTableGridPane;
	}
	
	private ScrollPane createCurrentSessionResultTable() {
		
//		Platform.runLater(() -> {
//			currentSessionResultGridPane.getScene().setCursor(Cursor.WAIT);
//	         setControlsDisabled(currentSessionResultGridPane.getScene().getRoot(), true);
//		 });

	    Task<Void> task = new Task<Void>() {
	        @Override
	        protected Void call() throws Exception {

	            sessionDataTableView = sessionDataFactory.createTableView(sessionDataList, true, false);

	            Label tablePlaceholderLabel = new Label("Select any unit data from unit result table..");
	            tablePlaceholderLabel.setStyle("-fx-font-size:20px;");
	            sessionDataTableView.setPlaceholder(tablePlaceholderLabel);

//	    		Changed by Vignesh 31-07-25 for moving icon location
	    		if(!sessionDataTableView.getColumns().isEmpty()) {
	    			TableColumn<SessionData, ?> lastColumn = sessionDataTableView.getColumns().get(sessionDataTableView.getColumns().size() - 1);
	    			//sessionDataTableView.getColumns().removeLast();
	    			//Mani Changes By Vignesh Comment
	    			sessionDataTableView.getColumns().remove(lastColumn);

	    			sessionDataTableView.getColumns().add(0, lastColumn);	
	    		}

	            sessionDataTableView.getColumns().forEach(column -> {   
	    			if(!column.getText().isEmpty()) {				
	    				column.setMinWidth(column.getText().length()*14);
	    				updateSessionData((TableColumn<SessionData, String>) column);
	    			}
	            });

	            sessionDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
	                ObservableList<SessionData> selectedItems = sessionDataTableView.getSelectedItems();
	                for (SessionData rowData : selectedItems) {
	                    GridPane bottomMidTopGridPane = (GridPane) currentSessionResultGridPane.getParent().getParent().getParent();
//	                    Changed by Vignesh 31-07-25 for displaying stage name in stage result
	                    userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Stage Results", SESSION_ID, rowData);
	                    System.out.println(rowData);
	                    System.out.println("Session Name"+SESSION_ID);
	                    break;
	                }
	            });

	           
	            

	            return null;  
	        }
	    

	    @Override
		protected void succeeded() {
	    	Platform.runLater(() -> {
            	
 	               tableScrollPane.setContent(sessionDataTableView);
 	              tableScrollPane.setFitToHeight(true);
 	            });
//	    	Platform.runLater(() -> {
//	    		currentSessionResultGridPane.getScene().setCursor(Cursor.DEFAULT);
//		        setControlsDisabled(currentSessionResultGridPane.getScene().getRoot(), false);
//	        });
               
           
		}

		@Override
		protected void failed() {
//			Platform.runLater(() -> {
//				currentSessionResultGridPane.getScene().setCursor(Cursor.DEFAULT);
//		        setControlsDisabled(currentSessionResultGridPane.getScene().getRoot(), false);
//	        });
			Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
		}
	};
	new Thread(task).start();
	   

	    return tableScrollPane;  
	}

	
//	private ScrollPane createCurrentSessionResultTable() {
//		
//		sessionDataTableView = sessionDataFactory.createTableView(sessionDataList, true, false);
//		
//		Label tablePlaceholderLabel = new Label("Select any unit data from unit result table..");
//		tablePlaceholderLabel.setStyle("-fx-font-size:20px;");
//		sessionDataTableView.setPlaceholder(tablePlaceholderLabel);
//		
//		sessionDataTableView.getColumns().forEach(column -> {   
//			if(!column.getText().isEmpty()) {				
//				column.setMinWidth(column.getText().length()*14);
//				updateSessionData((TableColumn<SessionData, String>) column);
//			}
//        });
//		
//		sessionDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> { 
//			ObservableList<SessionData> selectedItems = sessionDataTableView.getSelectedItems();
//			for (SessionData rowData : selectedItems) {				
//				GridPane bottomMidTopGridPane = (GridPane) currentSessionResultGridPane.getParent().getParent().getParent();
//				userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Stage Results", SESSION_ID,rowData.getId());							
//				break ;
//			}
//		});
//		
//		
//		tableScrollPane.setContent(sessionDataTableView);
//		tableScrollPane.setFitToHeight(true);
//		return tableScrollPane;
//	}
		
	private void updateSessionData(TableColumn<SessionData, String> column) {
	    column.setCellFactory(col -> new TableCell<SessionData, String>() {
	        private Label label;

	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (item == null || empty) {
	                setText(null);
	                setGraphic(null);
	            } else {
	                if (label == null) {
	                    label = new Label();
	                    label.setWrapText(false);

	                    if (column.getText().equalsIgnoreCase("stage")) {
	                        label.setAlignment(Pos.CENTER_LEFT); 
	                        setStyle("-fx-alignment: CENTER_LEFT;"); 
	                    } else {
	                        label.setAlignment(Pos.CENTER); 
	                        setStyle("-fx-alignment: CENTER;");
	                    }

	                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	                }
	                label.setText(item);
	                label.setStyle("-fx-text-fill: #000000;");
	                label.setMinWidth(label.getText().length() * 12);
	                setGraphic(label);
	                this.setMinWidth(label.getText().length() * 12);
	                col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
	            }
	        }
	    });
	}
	
	private void setControlsDisabled(Node root, boolean disabled) {
	    for (Node node : root.lookupAll("*")) {
	        if (node instanceof Control) {
	            ((Control) node).setDisable(disabled);
	        }
	    }
	}

}
