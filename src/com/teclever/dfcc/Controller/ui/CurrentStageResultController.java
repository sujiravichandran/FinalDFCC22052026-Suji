package com.teclever.dfcc.Controller.ui;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.model.BriefData;
import com.teclever.dfcc.model.DetailedData;
import com.teclever.dfcc.reportgeneration.ReportGeneration;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class BriefDataTableViewFactory1 implements TableViewFactory<BriefData> {
	@Override
	public CustomTableView<BriefData> createTableView(ObservableList<BriefData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, BriefData.class, addUserColumn, addCheckboxColumn);
	}
}

class DetailedDataTableViewFactory1 implements TableViewFactory<DetailedData> {
	@Override
	public CustomTableView<DetailedData> createTableView(ObservableList<DetailedData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, DetailedData.class, addUserColumn, addCheckboxColumn);
	}
}

public class CurrentStageResultController {

	private GridPane currentExecutionResultGridPane = new GridPane();
	private GridPane currentExecutionResultHeadingGridPane = new GridPane();
	private GridPane currentExecutionResultTabsGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();
	private HBox buttonBox = new HBox();
	private Button downloadButton = new Button("Download");

	private TabPane currentExecutionResultTabPane = new TabPane();

	private StackPane briefDataStackPane = new StackPane();
	private StackPane detailedDataStackPane = new StackPane();

	private TableViewFactory<BriefData> briefDataFactory = new BriefDataTableViewFactory();
	private TableViewFactory<DetailedData> detailedDataFactory = new DetailedDataTableViewFactory();

	private ObservableList<BriefData> briefDataList = FXCollections.observableArrayList();
	private ObservableList<DetailedData> detailedDataList = FXCollections.observableArrayList();

	private CustomTableView<BriefData> briefDataTableView;
	private CustomTableView<DetailedData> detailedDataTableView;

	private String currentTab = "tab1";

	ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	ReportGeneration reportGeneration = new ReportGeneration();

	private String SESSION_ID;
	private String STAGE_ID;

	public GridPane createCurrentExecutionResultGridPane(String sessionId, String stageId) {
		SESSION_ID = sessionId;
		STAGE_ID = stageId;

		currentExecutionResultGridPane.getStylesheets()
				.add(getClass()
						.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CurrentExecutionResults.css")
						.toExternalForm());
		currentExecutionResultGridPane.getStyleClass().add("current-execution-result-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		currentExecutionResultGridPane.setPadding(new Insets(5));
		currentExecutionResultGridPane.getColumnConstraints().addAll(firstColumn);
		currentExecutionResultGridPane.getRowConstraints().addAll(firstRow, secondRow);

		currentExecutionResultGridPane.add(createHeadingBox(), 0, 0);
		currentExecutionResultGridPane.add(createCurrentExecutionResultTabsGridPane(), 0, 1);
		return currentExecutionResultGridPane;
	}

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		currentExecutionResultHeadingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		currentExecutionResultHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("STAGE RESULTS" );
		title.getStyleClass().add("current-execution-result-title");
		titleBox.getChildren().add(title);

		currentExecutionResultHeadingGridPane.add(titleBox, 0, 0);
		currentExecutionResultHeadingGridPane.add(createDownloadButton(), 1, 0);

		return currentExecutionResultHeadingGridPane;
	}

	ViewReportController viewReportController = new ViewReportController();
	private HBox createDownloadButton() {
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(downloadButton);
		downloadButton.setOnAction(e -> {
			Dialog<String> dialog = new Dialog<>();
			dialog.setTitle("Download Confirmation");
			dialog.setHeaderText("Please select the type of report you want to download:");

			// Create the radio buttons
			RadioButton briefDataRadio = new RadioButton("Brief Data");
			RadioButton detailedDataRadio = new RadioButton("Detailed Data");
			ToggleGroup toggleGroup = new ToggleGroup();
			briefDataRadio.setToggleGroup(toggleGroup);
			detailedDataRadio.setToggleGroup(toggleGroup);

			briefDataRadio.setSelected(true);

			VBox vbox = new VBox(briefDataRadio, detailedDataRadio);
			vbox.setSpacing(10);
			vbox.setPadding(new Insets(10));
			vbox.setAlignment(Pos.CENTER_LEFT);

			dialog.getDialogPane().setContent(vbox);

			ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
			ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == okButtonType) {
					return briefDataRadio.isSelected() ? "Brief" : "Detailed";
				}
				return null;
			});

			dialog.showAndWait().ifPresent(result -> {
				if ("Brief".equals(result)) {
					Consumer<Response> onDownloadComplete = (response) -> {

						Platform.runLater(() -> {
							viewReportController.viewReportPopup(response);			
							});
					};
					if (STAGE_ID != null && SESSION_ID != null) {
						downloadReport(SESSION_ID, STAGE_ID, true, false, onDownloadComplete);
						logDownload("Brief Data");
					} else if(STAGE_ID == null && SESSION_ID != null){
						downloadReport(SESSION_ID, null, true, false, onDownloadComplete);
						logDownload("Brief Data");
					} else {
						downloadReport(currentSessionDetails.getSessionId(), null, true, true, onDownloadComplete);
						logDownload("Brief Data");
					}
				} else {
					
					Consumer<Response> onDownloadComplete = (response) -> {

						Platform.runLater(() -> {
							viewReportController.viewReportPopup(response);			
							});
					};
					if (STAGE_ID != null && SESSION_ID != null) {
						downloadReport(SESSION_ID, STAGE_ID, false, false, onDownloadComplete);
						logDownload("Brief Data");
					} else if(STAGE_ID == null && SESSION_ID != null){
						downloadReport(SESSION_ID, null, false, false, onDownloadComplete);
						logDownload("Brief Data");
					} else {
						downloadReport(currentSessionDetails.getSessionId(), null , false, true, onDownloadComplete);
						logDownload("Brief Data");
					}
				}
			});
		});
		return buttonBox;
	}

	private void logDownload(String reportType) {
		ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
		ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
				currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
				StateMachine.getCurrentUserLogin(), new Date(), "Clicked on " + reportType + " Download button");
		appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
	}

	private void downloadReport(String sessionId, String stageId, boolean isBrief, boolean isCurrentSession,
			Consumer<Response> onDownloadComplete) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				AtomicReference<Response> responseRef = new AtomicReference<>(null);
				if (isBrief) {
					if(isCurrentSession) {
						responseRef.set(reportGeneration.generateBreifReportForCurrentExecution(sessionId));
					}else if(sessionId != null && stageId != null) {
						responseRef.set(reportGeneration.generateBreifReportForCurrentExecution(sessionId, stageId));
					}else if(sessionId != null && stageId == null ) {
						responseRef.set(reportGeneration.generateBreifReportForCurrentSession(sessionId));
					}
				} else {
					if(isCurrentSession) {
						responseRef.set(reportGeneration.generateDetailedReportForCurrentExecution(sessionId));
					}else if(sessionId != null && stageId != null) {
						responseRef.set(reportGeneration.generateDetailedReportForCurrentExecution(sessionId, stageId));
						
					}else if(sessionId != null && stageId == null ) {
						responseRef.set(reportGeneration.generateDetailedReportForCurrentSession(sessionId));
						
					}
				}


				if (responseRef.get() != null) {
					Platform.runLater(() -> onDownloadComplete.accept(responseRef.get()));
				}
				return null;
			}
		};

		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	private GridPane createCurrentExecutionResultTabsGridPane() {
		currentExecutionResultTabsGridPane.getStyleClass().add("current-execution-result-tabs-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		currentExecutionResultTabsGridPane.setPadding(new Insets(5));

		currentExecutionResultTabsGridPane.getColumnConstraints().addAll(firstColumn);
		currentExecutionResultTabsGridPane.getRowConstraints().addAll(firstRow);

		currentExecutionResultTabsGridPane.add(createCurrentExecutionResultTabs(), 0, 0);
		return currentExecutionResultTabsGridPane;
	}

	private TabPane createCurrentExecutionResultTabs() {
		Tab tab1 = new Tab("Brief Data");
		Tab tab2 = new Tab("Detailed Data");

		StackPane tab2StackPane = createTab2Content();
		StackPane tab1StackPane = createTab1Content();

		tab1.setContent(tab1StackPane);
		tab1.setClosable(false);

		tab2.setContent(tab2StackPane);
		tab2.setClosable(false);

		currentExecutionResultTabPane.getTabs().addAll(tab1, tab2);

		currentExecutionResultTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
			if (newTab == tab2) {
				createTab2Content();
				currentTab = "tab2";
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Detailed data button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			} else {
				createTab1Content();
				currentTab = "tab1";
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on Brief data button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			}
		});

//		createTab1Content();

		return currentExecutionResultTabPane;
	}

	private StackPane createTab1Content() {
		briefDataStackPane.getStyleClass().add("tab-content-container");
		briefDataStackPane.getChildren().clear();
		briefDataStackPane.getChildren().add(createBriefDataTable());
		return briefDataStackPane;
	}

	private StackPane createTab2Content() {
		detailedDataStackPane.getStyleClass().add("tab-content-container");
		detailedDataStackPane.getChildren().clear();
		detailedDataStackPane.getChildren().add(createDetailedDataTable());
		return detailedDataStackPane;
	}
	
	private void setControlsDisabled(Node root, boolean disabled) {
	    for (Node node : root.lookupAll("*")) {
	        if (node instanceof Control) {
	            ((Control) node).setDisable(disabled);
	        }
	    }
	}

	private ScrollPane createBriefDataTable() {
		briefDataList.clear();
		
		Platform.runLater(() -> {
    		currentExecutionResultGridPane.getScene().setCursor(Cursor.WAIT);
    		currentExecutionResultGridPane.getScene().getRoot().setDisable(true);
        });
		
		ScrollPane tableScrollPane = new ScrollPane(briefDataTableView);
		   Task<Void> task = new Task<Void>() {
		        @Override
		        protected Void call() throws Exception {
		ResultExecutionResponse response = new ResultExecutionResponse();
		
		
		if (STAGE_ID != null && SESSION_ID != null) {
			response = resultExecutionManagement.getResultExecutionListBriefListForSelectedStages(SESSION_ID, STAGE_ID);
	
		} 
		else if(STAGE_ID == null && SESSION_ID != null){
			response = resultExecutionManagement.getResultExecutionListBriefListForStages(SESSION_ID);
		}
		
		Platform.runLater(() -> {
			currentExecutionResultGridPane.getScene().setCursor(Cursor.WAIT);
			currentExecutionResultGridPane.getScene().getRoot().setDisable(true);
		 });

	 
		if (response.getCode() == 1 && response.getResultDTOList() != null) {
			int i = 1;
			for (ResultExecutionDTO data : response.getResultDTOList()) {

				BriefData newBriefData = new BriefData();

				newBriefData.setId(data.getTestFileId());
				newBriefData.setSlNo(String.valueOf(i));
				newBriefData.setExecutedFileName(data.getRdfFile());
				newBriefData.setTimeOfExecution(data.getEndTime());
				newBriefData.setResult(data.getStatus());
				newBriefData.setTestMode(data.getTestMode());

				briefDataList.add(newBriefData);
				i++;
			}
		}

		briefDataTableView = briefDataFactory.createTableView(briefDataList, false, false);
		
			Label tablePlaceholderLabel = new Label("Select any session data from session result table..");
			tablePlaceholderLabel.setStyle("-fx-font-size:20px;");
			briefDataTableView.setPlaceholder(tablePlaceholderLabel);
		

		briefDataTableView.getColumns().forEach(column -> {
			column.setMinWidth(column.getText().length() * 18);
			updateBriefData((TableColumn<BriefData, String>) column);
		});

		


		if(briefDataList.size() == 0) {
			tableScrollPane.setFitToWidth(true);
		}
		
		tableScrollPane.setFitToHeight(true);
	      return null;  
	        }
		
	        @Override
			protected void succeeded() {
		    	Platform.runLater(() -> {
	            	
	 	               tableScrollPane.setContent(briefDataTableView);
	 	              tableScrollPane.setFitToHeight(true);
	 	            });
		    	Platform.runLater(() -> {
		    		currentExecutionResultGridPane.getScene().setCursor(Cursor.DEFAULT);
		    		currentExecutionResultGridPane.getScene().getRoot().setDisable(false);
		        });
	               
	           
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> {
					currentExecutionResultGridPane.getScene().setCursor(Cursor.DEFAULT);
					currentExecutionResultGridPane.getScene().getRoot().setDisable(false);
		        });
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};
		new Thread(task).start();
		
		return tableScrollPane;
	}

	private void updateBriefData(TableColumn<BriefData, String> column) {
		column.setCellFactory(col -> new TableCell<BriefData, String>() {
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
						label.setAlignment(Pos.CENTER);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						setStyle("-fx-alignment: CENTER;");
					}
					label.setText(item);
					label.setStyle("-fx-text-fill: white; ");
					label.setMinWidth(label.getText().length() * 18);
					setGraphic(label);
					this.setMinWidth(label.getText().length() * 18);
					col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
				}
			}
		});
	}

	public ScrollPane createDetailedDataTable() {
		detailedDataList.clear();
		
		Platform.runLater(() -> {
			currentExecutionResultGridPane.getScene().setCursor(Cursor.WAIT);
			currentExecutionResultGridPane.getScene().getRoot().setDisable(true);;
	    });
		
		ScrollPane tableScrollPane = new ScrollPane(detailedDataTableView);
		 Task<Void> task = new Task<Void>() {
		        @Override
		        protected Void call() throws Exception {
		ResultDetailedResponse response = new ResultDetailedResponse();
		
		if (STAGE_ID != null && SESSION_ID != null) {
			response = resultExecutionManagement.getResultExecutionDetailedListForStages(SESSION_ID, STAGE_ID);
		} 
		else if(STAGE_ID == null && SESSION_ID != null){
			response = resultExecutionManagement.getResultExecutionDetailedListForStages(SESSION_ID);
		}else {
			response = resultExecutionManagement
					.getResultExecutionDetailedListForStages(currentSessionDetails.getSessionId());
		}

		if (response.getCode() == 1 && response.getResultDetailedList() != null) {
			int i = 1;
			for (ResultDetailedDTO data : response.getResultDetailedList()) {

				DetailedData newDetailedData = new DetailedData();

				newDetailedData.setSlNo(String.valueOf(i));
				newDetailedData.setTestName(data.getTestName());
				newDetailedData.setRdfName(data.getRdfName());
				newDetailedData.setTpgphNo(data.getTpgph());
				newDetailedData.setStepNo(data.getStepName());
				newDetailedData.setExpectedValue(data.getExpectedValue());
				newDetailedData.setMeasuredValueCh1_Ch2_Ch3_Ch4(data.getFaultyChannel());
				newDetailedData.setUnit(data.getUnit());
				newDetailedData.setSignalName(data.getSignalName());
				newDetailedData.setFaultySru(data.getFaultySRU());
				newDetailedData.setTestMode(data.getTestMode());
				newDetailedData.setFaultyChannelValue(data.getFaultyChannelValue());
				detailedDataList.add(newDetailedData);

				i++;
			}
		}else if(response.getCode() == 0) {
			Notifications.showErrorAlert(response.getMsg());
		}

		detailedDataTableView = detailedDataFactory.createTableView(detailedDataList, false, false);

		
			Label tablePlaceholderLabel = new Label("Select any session data from session result table..");
			tablePlaceholderLabel.setStyle("-fx-font-size:20px;");
			detailedDataTableView.setPlaceholder(tablePlaceholderLabel);
		

		detailedDataTableView.getColumns().forEach(column -> {
			column.setMinWidth(column.getText().length() * 14);
			updateDetailedData((TableColumn<DetailedData, String>) column);
		});

		
		tableScrollPane.setFitToHeight(true);
		  return null;  
		        }
			
		        @Override
				protected void succeeded() {
			    	Platform.runLater(() -> {
		            	
		 	               tableScrollPane.setContent(detailedDataTableView);
		 	              tableScrollPane.setFitToHeight(true);
		 	            });
			    	Platform.runLater(() -> {
			    		currentExecutionResultGridPane.getScene().setCursor(Cursor.DEFAULT);
			    		currentExecutionResultGridPane.getScene().getRoot().setDisable(false);
			        });
		               
		           
				}

				@Override
				protected void failed() {
					Platform.runLater(() -> {
						currentExecutionResultGridPane.getScene().setCursor(Cursor.DEFAULT);
						currentExecutionResultGridPane.getScene().getRoot().setDisable(false);
			        });
					Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
				}
			};
			new Thread(task).start();
		
		
		return tableScrollPane;
	}

	private void updateDetailedData(TableColumn<DetailedData, String> column) {
		
		
		column.setCellFactory(col -> new TableCell<DetailedData, String>() {
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
						label.setAlignment(Pos.CENTER);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						setStyle("-fx-alignment: CENTER;");
					}
					label.setText(item);
					label.setStyle("-fx-text-fill: white;");
					label.setMinWidth(label.getText().length() * 14);
					setGraphic(label);
					this.setMinWidth(label.getText().length() * 14);
					col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
				}
			}
		});
	}
	
	
}
