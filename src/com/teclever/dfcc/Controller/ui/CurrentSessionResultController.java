package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsResponse;
import com.teclever.dfcc.model.SessionData;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

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
	
	private ObservableList<SessionData> sessionDataList = FXCollections.observableArrayList();
	
	private TableViewFactory<SessionData> sessionDataFactory = new SessionDataTableViewFactory();
	private CustomTableView<SessionData> sessionDataTableView ;
	
	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	
	private String SESSION_ID ;
	private UserCenterContentController userCenterContentController = UserCenterContentController.getInstance();

	
    public GridPane createCurrentSessionResultGridPane(String id) {
    	SESSION_ID = id ;
		getCurrentSessionResultData();
    	currentSessionResultGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/CurrentExecutionResults.css").toExternalForm());
    	currentSessionResultGridPane.getStyleClass().add("current-execution-result-container");

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(100);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		currentSessionResultGridPane.setPadding(new Insets(5));
		currentSessionResultGridPane.getColumnConstraints().addAll(firstColumn);
		currentSessionResultGridPane.getRowConstraints().addAll(firstRow, secondRow);


		currentSessionResultGridPane.add(createHeadingBox(), 0, 0);
		currentSessionResultGridPane.add(createCurrentSessionResultTableGridPane(), 0, 1);
        return currentSessionResultGridPane;
    }
    
	private void getCurrentSessionResultData() {
		ResultSessionStagesDetailsResponse response = resultExecutionManagement.getStagesDetailsForSession(SESSION_ID);
		if(response.getCode() == 1 && response.getResultSessionStagesDetailsDTOList() != null) {
			int i = 1;
			for(ResultSessionStagesDetailsDTO data : response.getResultSessionStagesDetailsDTOList()) {
				SessionData newSessionData = new SessionData();
				
				newSessionData.setId(data.getStageId());
				newSessionData.setSlNo(String.valueOf(i));
				newSessionData.setStage(data.getStage());
				newSessionData.setStartTime(data.getStartTime());
				newSessionData.setEndTime(data.getEndTime());
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
	
	private HBox createViewAllButtonButton() {
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
		sessionDataTableView = sessionDataFactory.createTableView(sessionDataList, true, false);
		
		Label tablePlaceholderLabel = new Label("Select any unit data from unit result table..");
		tablePlaceholderLabel.setStyle("-fx-font-size:20px;");
		sessionDataTableView.setPlaceholder(tablePlaceholderLabel);
		
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
				userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Stage Results", SESSION_ID,rowData.getId());							
				break ;
			}
		});
		
		
		tableScrollPane.setContent(sessionDataTableView);
		tableScrollPane.setFitToHeight(true);
		return tableScrollPane;
	}
		
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
	                label.setStyle("-fx-text-fill: white;");
	                label.setMinWidth(label.getText().length() * 12);
	                setGraphic(label);
	                this.setMinWidth(label.getText().length() * 12);
	                col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
	            }
	        }
	    });
	}

}
