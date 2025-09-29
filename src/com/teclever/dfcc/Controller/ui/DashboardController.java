package com.teclever.dfcc.Controller.ui;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.protobuf.TextFormat.ParseException;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.dashboard.DashBoardDetailsDTO;
import com.teclever.dfcc.dashboard.DashboardManagement;
import com.teclever.dfcc.dashboard.PQTSessionDetailsDTO;
import com.teclever.dfcc.dashboard.ProductionDashboardDetails;
import com.teclever.dfcc.dashboard.ProductionSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.model.DetailedData;
import com.teclever.dfcc.model.UnitData;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

class UnitDataTableViewFactory1 implements TableViewFactory<UnitData> {
	@Override
	public CustomTableView<UnitData> createTableView(ObservableList<UnitData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, UnitData.class, addUserColumn, addCheckboxColumn);
	}
}

public class DashboardController {



	private GridPane dashBoardMainContainerGridPane = new GridPane();
	private GridPane dashboardFirstContainerGridPane = new GridPane();
	private GridPane dashboardSecondContainerGridPane = new GridPane();
	
	private GridPane slNoGridPane = new GridPane();
	private GridPane latestProduction = new GridPane();
	
	private GridPane dashboardPqtGridPane = new GridPane();
	private GridPane dashboardLasttestedGridPane = new GridPane();

	private GridPane dashboardSessionExecutedGridPane = new GridPane();
	private GridPane dashboardCurrentFailureStageGridPane = new GridPane();

	private StackPane dashboardSessionDataStackPane = new StackPane();
	private StackPane dashboardFailureDataStackPane = new StackPane();
	
	private ObservableList<DetailedData> detailedDataList = FXCollections.observableArrayList();

	private CustomTableView<DetailedData> detailedDataTableView;

	private String UUT_ID;

	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();

	private TableViewFactory<UnitData> unitDataFactory = new UnitDataTableViewFactory();

	private TableViewFactory<DetailedData> detailedDataFactory = new DetailedDataTableViewFactory();
	private CustomTableView<UnitData> unitDataTableView;

	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();

	private GridPane bottomMidTopGridPane;

	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("DASHBOARD");
	private HBox headingHbox = new HBox(10);
	private HBox productionHbox = new HBox(10);
	private HBox productionHbox2 = new HBox(10);
	
	private Label slNoLabel = new Label("Current Unit SL No : ");
	private Label slNoLabel2 = new Label();
	
	private Label productionLabel = new Label("Latest Production Test Date: ");
	private Label productionLabel2 = new Label();
	private Label pqtLabel = new Label("PQT: ");
	private Label pqtLabel1 = new Label();
	private Label pqtDateLabel = new Label("Date: ");
	private Label pqtDateLabel1 = new Label();

	private Label lastTestedLabel = new Label("Latest Tested Date : ");
	private Label lastTestedDateLabel = new Label();

	private Label sessionExecuted = new Label("SESSION EXECUTED");
	private Label currentFailureStage = new Label("FAILURE's OBSERVED IN THIS UNIT");
	private ScrollPane tableScrollPane1 = new ScrollPane();
	private ScrollPane tableScrollPane2 = new ScrollPane();

	private String SESSION_ID;
	private String STAGE_ID;

	private DashboardManagement dashboardManagement = new DashboardManagement();

	public DashboardController() {
		StateMachine.confirmTestFileCompletedProperty().addListener((obs, oldVal, newVal) -> {
			
			
				String UUT_ID = currentSessionDetails.getUutId();
				DashboardManagement DashboardManagement = new DashboardManagement();

				ProductionDashboardDetails details = new ProductionDashboardDetails();

				ProductionSessionDetailsDTO sessionData = DashboardManagement.getProductionDetailsForDashBoard(UUT_ID);

				
					Platform.runLater(() -> {
					try {
						refreshDashboard(UUT_ID);
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					});
				
				getCurrentUnitResultData(currentSessionDetails.getUutId());	
				createCurrentFailureResultTable();
				
			
		});
		
		String UUT_ID = currentSessionDetails.getUutId();
		slNoLabel2.setText(currentSessionDetails.getDfccSerialNumber());
		DashboardManagement DashboardManagement = new DashboardManagement();

		ProductionDashboardDetails details = new ProductionDashboardDetails();

		ProductionSessionDetailsDTO sessionData = DashboardManagement.getProductionDetailsForDashBoard(UUT_ID);
		Platform.runLater(() -> {
			try {
				refreshDashboard(UUT_ID);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			});
		getCurrentUnitResultData(currentSessionDetails.getUutId());	
		createCurrentFailureResultTable();
		
	}

	
	private void refreshDashboard(String UUT_ID) throws java.text.ParseException {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
	    DashBoardDetailsDTO res = dashboardManagement.getDashboardDetails(UUT_ID);
	    String serialNo = res.getSerialNo();

	    if (currentSessionDetails.getDfccSerialNumber().equals(serialNo)) {
	    	
	        List<ProductionDashboardDetails> sessionList = res.getProductionSessionDetailsDTOList();
//	        System.out.println("Check Session list: " + sessionList);

	        if (sessionList != null && !sessionList.isEmpty()) {

				String lastTestedDateStr = res.getLastTestedDate();
				if(lastTestedDateStr != null) {

				SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
				SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date parsedDate = inputFormat.parse(lastTestedDateStr);
				String formattedDate = outputFormat.format(parsedDate);
				Platform.runLater(() -> {
				lastTestedDateLabel.setText(formattedDate);
//				System.out.println("Formatted Last Tested Date: " + formattedDate);
				});
				}else {
					Platform.runLater(() -> {
					 productionLabel2.setText("Test is Not Started");
					 lastTestedDateLabel.setText("-");
					});
				}
	            // --- Find Latest Production Session ---
	            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	            List<ProductionDashboardDetails> validSessions = sessionList.stream()
	                .filter(s -> s.getStartTime() != null && !"Not Started".equalsIgnoreCase(s.getStartTime().trim()))
	                .toList();

//	            System.out.println("Valid Sessions: " + validSessions);

	            if (!validSessions.isEmpty()) {
	                ProductionDashboardDetails latestSession = validSessions.stream()
	                    .max(Comparator.comparing(s -> LocalDateTime.parse(s.getStartTime(), inputFormatter)))
	                    .orElse(null);

	                if (latestSession != null) {
	                	Platform.runLater(() -> {
	                    LocalDateTime latestDate = LocalDateTime.parse(latestSession.getStartTime(), inputFormatter);
	                    productionLabel2.setText(latestDate.format(outputFormatter));
	                	});
	                }
	            } else {
	            	Platform.runLater(() -> {
	                productionLabel2.setText("Test is Not Started");
	            	});
	            }
	        } else {
	        	Platform.runLater(() -> {
	            productionLabel2.setText("Test is Not Started");
	        	});
	        }
	    }

	    // --- PQT ---
	    PQTSessionDetailsDTO pqtData = dashboardManagement.getPQTDetailsForDashBoard(UUT_ID);
	    if (pqtData.isPqtConducted()) {
	    	Platform.runLater(() -> {
	        pqtLabel1.setText("YES");
	        pqtLabel1.setStyle("-fx-text-fill: green;");
	        pqtDateLabel1.setText(pqtData.getStartTime());
	    	});
	    } else {
	    	Platform.runLater(() -> {
	        pqtLabel1.setText("NO");
	        pqtLabel1.setStyle("-fx-text-fill: red;");
	        pqtDateLabel1.setText("-");
	    	});
	    }
		return null;
			}
		};
		new Thread(task).start();
	}
	

	public GridPane createDashboardMainContainerGridPane() {

		dashBoardMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Dashboard.css").toExternalForm());
		dashBoardMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(7);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(42);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(42);

		dashBoardMainContainerGridPane.setVgap(5);

//		System.out.println("Entred Dahboard");

		dashBoardMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		dashBoardMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow);
		dashBoardMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
		dashBoardMainContainerGridPane.add(headingGridPane(), 0, 0);
		dashBoardMainContainerGridPane.add(dashboardFirst(), 0, 1);
		dashBoardMainContainerGridPane.add(dashboardSecond(), 0, 2);
		dashBoardMainContainerGridPane.add(dashboardSessionExecutedGridPane(), 0, 3);
		dashBoardMainContainerGridPane.add(dashboardCurrentFailureExecutedGridPane(), 0, 4);

		return dashBoardMainContainerGridPane;
	}

	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().add(firstRow);
		headingGridPane.getStyleClass().add("selfTest-top-container");

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		pageHeading.getStyleClass().add("dashboard-top-header");
		headingHbox.setAlignment(Pos.CENTER);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	private GridPane dashboardFirst() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		dashboardFirstContainerGridPane.setHgap(15);
		dashboardFirstContainerGridPane.setPadding(new Insets(5));

		dashboardFirstContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		dashboardFirstContainerGridPane.getRowConstraints().addAll(firstRow);

		dashboardFirstContainerGridPane.add(slNoHbox(), 0, 0);
		dashboardFirstContainerGridPane.add(productionDateHbox(), 1, 0);

		return dashboardFirstContainerGridPane;

	}

	
	private GridPane slNoHbox() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		slNoGridPane.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");
		slNoLabel2.getStyleClass().add("midheader-label-right");
		slNoLabel.getStyleClass().add("midheader-label-left");
		
		slNoGridPane.getStyleClass().add("midheader-hbox");
		
		slNoGridPane.setAlignment(Pos.CENTER);
		slNoGridPane.add(slNoLabel, 0, 0); // column 0
		slNoGridPane.add(slNoLabel2, 1, 0); // column 1
		
		return slNoGridPane;
	
	}
	
	private GridPane productionDateHbox() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		latestProduction.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");
		
		productionLabel.getStyleClass().add("midheader-label-left");
		productionLabel2.getStyleClass().add("midheader-label-right");
		
		latestProduction.getStyleClass().add("midheader-hbox");
		
		
		
		latestProduction.setAlignment(Pos.CENTER);
		latestProduction.add(productionLabel, 0, 0); // column 0
		latestProduction.add(productionLabel2, 1, 0); // column 1
		
		return latestProduction;
	
	}

	

	private GridPane dashboardSecond() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		dashboardSecondContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		dashboardSecondContainerGridPane.getRowConstraints().addAll(firstRow);

		dashboardSecondContainerGridPane.setHgap(15);
		dashboardSecondContainerGridPane.setPadding(new Insets(5));

		dashboardSecondContainerGridPane.add(pqtHbox(), 0, 0);
		dashboardSecondContainerGridPane.add(lastTestedHbox2(), 1, 0);

		return dashboardSecondContainerGridPane;

	}

	private GridPane pqtHbox() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(20);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(20);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(30);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(30);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		dashboardPqtGridPane.setHgap(15);
		dashboardPqtGridPane.setPadding(new Insets(5));

		dashboardPqtGridPane.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");
		
		pqtLabel.getStyleClass().add("midheader-label-left");
		pqtLabel1.getStyleClass().add("midheader-label-left");
		pqtDateLabel.getStyleClass().add("midheader-label-left");
		pqtDateLabel1.getStyleClass().add("midheader-label-right");
		
		dashboardPqtGridPane.getStyleClass().add("midheader-hbox");
		dashboardPqtGridPane.setAlignment(Pos.CENTER);
		dashboardPqtGridPane.add(pqtLabel, 0, 0); // column 0
		dashboardPqtGridPane.add(pqtLabel1, 1, 0); // column 1
		dashboardPqtGridPane.add(pqtDateLabel, 2, 0); // column 2
		dashboardPqtGridPane.add(pqtDateLabel1, 3, 0); // column 3
		return dashboardPqtGridPane;
	}

	private GridPane lastTestedHbox2() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		dashboardLasttestedGridPane
				.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");
		
		lastTestedDateLabel.getStyleClass().add("midheader-label-right");
		lastTestedLabel.getStyleClass().add("midheader-label-left");
		
		dashboardLasttestedGridPane.getStyleClass().add("midheader-hbox");
		dashboardLasttestedGridPane.setAlignment(Pos.CENTER);
		dashboardLasttestedGridPane.add(lastTestedLabel, 0, 0);
		dashboardLasttestedGridPane.add(lastTestedDateLabel, 1, 0);

		return dashboardLasttestedGridPane;
	}

	private GridPane dashboardSessionExecutedGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);

		dashboardSessionExecutedGridPane.setHgap(15);
		dashboardSessionExecutedGridPane.setPadding(new Insets(5));

		dashboardSessionExecutedGridPane
				.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");

		dashboardSessionExecutedGridPane.getStyleClass().add("midheader-label-left");
		dashboardSessionExecutedGridPane.getStyleClass().add("midheader-hbox");
		dashboardSessionExecutedGridPane.setAlignment(Pos.TOP_LEFT);
		dashboardSessionExecutedGridPane.add(sessionExecuted, 0, 0);
		dashboardSessionExecutedGridPane.add(createSessionContent(), 0, 1);

		return dashboardSessionExecutedGridPane;
	}
	
	private StackPane createSessionContent() {
//		dashboardSessionDataStackPane.getStyleClass().add("tab-content-container");
//		dashboardSessionDataStackPane.getChildren().clear();
		dashboardSessionDataStackPane.getChildren().add(createCurrentUnitResultTable());
		return dashboardSessionDataStackPane;
	}


	private void getCurrentUnitResultData(String uutId) {
		unitDataList.clear();

		ResultUnitSessionDetailsResponse response = dashboardManagement.getSessionDetailsForResultsByUnit(uutId);
		if (response.getCode() == 1 && response.getResultUnitSessionDetailsDTOList() != null) {
			int i = 1;
			for (ResultUnitSessionDetailsDTO data : response.getResultUnitSessionDetailsDTOList()) {
				UnitData newUnitData = new UnitData();

				newUnitData.setId(data.getSessionId());
				newUnitData.setSlNo(String.valueOf(i));
				newUnitData.setSessionType(data.getSessionType());
				newUnitData.setSessionName(data.getSessionName());
				newUnitData.setStartTime(data.getStartTime());
				newUnitData.setEndTime(data.getEndTime());
				newUnitData.setSessionStatus(data.getSessionStatus());
				newUnitData.setSessionResult(data.getSessionResults());
				newUnitData.setStartRemarks(data.getStratRemarks());
				newUnitData.setEndRemarks(data.getEndRemarks());
				i++;
				unitDataList.add(newUnitData);
			}
		} else if (response.getCode() == 0) {
			Notifications.showErrorAlert(response.getMsg());
		}

//		tableScrollPane1.setFitToWidth(unitDataList.size() == 0);
	}

	private ScrollPane createCurrentUnitResultTable() {

		unitDataTableView = unitDataFactory.createTableView(unitDataList, true, false);

//		Changed by Vignesh 31-07-25 for moving icon location
		if (!unitDataTableView.getColumns().isEmpty()) {
			TableColumn<UnitData, ?> lastColumn = unitDataTableView.getColumns()
					.get(unitDataTableView.getColumns().size() - 1);
			// unitDataTableView.getColumns().removeLast();
			// Mani Changes By Vignesh Comment
			unitDataTableView.getColumns().remove(lastColumn);
			unitDataTableView.getColumns().add(0, lastColumn);
		}

		unitDataTableView.getColumns().forEach(column -> {
			if (!column.getText().isEmpty()) {
				column.setMinWidth(column.getText().length() * 14);
			}
		});
//Berfore Changing for Loadung cursor
		unitDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {

			ObservableList<UnitData> selectedItems = unitDataTableView.getSelectedItems();
			for (UnitData rowData : selectedItems) {

				UserCenterContentController userCenterContentController = UserCenterContentController.getInstance();

				bottomMidTopGridPane = (GridPane) dashBoardMainContainerGridPane.getParent().getParent().getParent();
				UserCenterContentController.currentSessionName = rowData.getSessionName(); // srini 2/8/25

				userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Session Results",
						rowData.getId(), null);
				break;
			}

		});

		
		tableScrollPane1.setContent(unitDataTableView);
		tableScrollPane1.setFitToHeight(true);
		return tableScrollPane1;
	}

	private GridPane dashboardCurrentFailureExecutedGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);

		dashboardCurrentFailureStageGridPane.setHgap(15);
		dashboardCurrentFailureStageGridPane.setPadding(new Insets(5));

		dashboardCurrentFailureStageGridPane
				.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");

		dashboardCurrentFailureStageGridPane.getStyleClass().add("midheader-label-left");
		dashboardCurrentFailureStageGridPane.getStyleClass().add("midheader-hbox");
		dashboardCurrentFailureStageGridPane.setAlignment(Pos.TOP_LEFT);
		dashboardCurrentFailureStageGridPane.add(currentFailureStage, 0, 0);
		dashboardCurrentFailureStageGridPane.add(createFailureContent(), 0, 1);

		return dashboardCurrentFailureStageGridPane;
	}
	
	private StackPane createFailureContent() {
//		dashboardFailureDataStackPane.getStyleClass().add("tab-content-container");
//		dashboardFailureDataStackPane.getChildren().clear();
		dashboardFailureDataStackPane.getChildren().add(createCurrentFailureResultTable());
		return dashboardFailureDataStackPane;
	}

	public ScrollPane createCurrentFailureResultTable() {
		

	    tableScrollPane2 = new ScrollPane(detailedDataTableView);
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				detailedDataTableView = detailedDataFactory.createTableView(detailedDataList, false, false);
				ResultDetailedResponse response = new ResultDetailedResponse();
				response = resultExecutionManagement.getResultExecutionListDetailedListForUnit(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber());

				if (response.getCode() == 1 && response.getResultDetailedList() != null) {
					int i = 1;
					detailedDataList.clear();

					for (ResultDetailedDTO data : response.getResultDetailedList()) {

						DetailedData newDetailedData = new DetailedData();

						newDetailedData.setSlNo(String.valueOf(i));
						newDetailedData.setTestName(data.getTestName());
						newDetailedData.setRdfName(data.getRdfName());
						newDetailedData.setTpgphNo(data.getTpgph());
						newDetailedData.setStepNo(data.getStepName());
						newDetailedData.setExpectedValue(data.getExpectedValue());
						newDetailedData.setMeasuredValueCh1_Ch2_Ch3_Ch4(data.getFaultyChannel());
						newDetailedData.setFaultySru(data.getFaultySRU());
						newDetailedData.setUnit(data.getUnit());
						newDetailedData.setSignalName(data.getSignalName());
						newDetailedData.setFaultyChannelValue(data.getFaultyChannelValue());
						newDetailedData.setTestMode(data.getTestMode());
						detailedDataList.add(newDetailedData);

						i++;
					}
				} else if (response.getCode() == 0 && response.geteMsg() != null && response.geteMsg().equals("")) {
					Notifications.showErrorAlert(response.getMsg());
				}

				

				detailedDataTableView.getColumns().forEach(column -> {
					column.setMinWidth(column.getText().length() * 14);
//			updateDetailedData((TableColumn<DetailedData, String>) column);
					
					 String colName=column.getText();
                     switch(colName) {
				case "SL NO":
					column.setMinWidth(100);
					column.setMaxWidth(100);
					break;
				case "TEST MODE":
					column.setMinWidth(320);
					column.setMaxWidth(320);
					break;
				case "TEST NAME":
					column.setMinWidth(250);
					column.setMaxWidth(250);
					break;
				case "RDF NAME":
					column.setMinWidth(250);
					column.setMaxWidth(250);
					break;
				case "TPGPH NO":
					column.setMinWidth(150);
					column.setMaxWidth(150);
					break;
				case "STEP NO":
					column.setMinWidth(150);
					column.setMaxWidth(150);
					break;
				case "SIGNAL NAME":
					column.setMinWidth(300);
					column.setMaxWidth(300);
					break;
				case "EXPECTED VALUE":
					column.setMinWidth(200);
					column.setMaxWidth(200);
					break;
				case "MEASURED VALUE CH1_CH2_CH3_CH4":
					column.setMinWidth(250);
					column.setMaxWidth(250);
					break;
				case "FAULTY SRU":
					column.setMinWidth(130);
					column.setMaxWidth(130);
					break;
				case "UNIT":
					column.setMinWidth(100);
					column.setMaxWidth(100);
					break;
				case "FAULTY CHANNEL VALUE":
					column.setMinWidth(300);
					column.setMaxWidth(300);
					break;


				default:
					column.setMinWidth(200);
					column.setMaxWidth(200);
					break;
				}
                    
                    
                
				});


				tableScrollPane2.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					tableScrollPane2.setContent(detailedDataTableView);
					tableScrollPane2.setFitToHeight(true);
				});

			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};
		new Thread(task).start();

		return tableScrollPane2;
	}
	
	

}
