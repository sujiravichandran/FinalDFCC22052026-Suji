package com.teclever.dfcc.Controller.ui;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.dashboard.DashBoardDetailsDTO;
import com.teclever.dfcc.dashboard.DashboardManagement;
import com.teclever.dfcc.dashboard.PQTSessionDetailsDTO;
import com.teclever.dfcc.dashboard.ProductionDashboardDetails;
import com.teclever.dfcc.dashboard.ProductionSessionDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultSessionStagesDetailsResponse;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.model.DetailedData;
import com.teclever.dfcc.model.SessionData;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
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

	private CustomTableView<SessionData> detailedDataTableView;

	private String UUT_ID;

	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();

	private TableViewFactory<UnitData> unitDataFactory = new UnitDataTableViewFactory1();

	private TableViewFactory<DetailedData> detailedDataFactory = new DetailedDataTableViewFactory();
	private CustomTableView<UnitData> unitDataTableView;

	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();

	private BLSTempData bLSTempData = new BLSTempData();

	private GridPane bottomMidTopGridPane;

	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("DASHBOARD");
	private Button otherTabs = new Button("Other Tabs");
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
	private Label recordedTemp = new Label("TEMPERATURE RECORDED");
	// Based on Sridhar requirement sai changed name on 09102025
	private Label currentFailureStage = new Label("FAILURE  HISTORY");
	private ScrollPane tableScrollPane1 = new ScrollPane();
	private ScrollPane tableScrollPane2 = new ScrollPane();
	private DashboardManagement DashboardManagement = new DashboardManagement();
	private String SESSION_ID;
	private String STAGE_ID;

	private DashboardManagement dashboardManagement = new DashboardManagement();
	private UserCenterContentController userCenterContentController = UserCenterContentController.getInstance();

	public DashboardController() {
		StateMachine.confirmTestFileCompletedProperty().addListener((obs, oldVal, newVal) -> {

			String UUT_ID = currentSessionDetails.getUutId();
			

			ProductionDashboardDetails details = new ProductionDashboardDetails();

			ProductionSessionDetailsDTO sessionData = DashboardManagement.getProductionDetailsForDashBoard(UUT_ID);

			Platform.runLater(() -> {
				try {
					SESSION_ID= currentSessionDetails.getSessionId();
					refreshDashboard(UUT_ID);
					getCurrentUnitResultData(currentSessionDetails.getUutId());
					if(newVal) {
						createCurrentFailureResultTable();
						getCurrentSessionResultData();
						
					}
					
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			

		});
		SESSION_ID= currentSessionDetails.getSessionId();
		String UUT_ID = currentSessionDetails.getUutId();
		slNoLabel2.setText(currentSessionDetails.getDfccSerialNumber());
		DashboardManagement DashboardManagement = new DashboardManagement();

		ProductionDashboardDetails details = new ProductionDashboardDetails();

		ProductionSessionDetailsDTO sessionData = DashboardManagement.getProductionDetailsForDashBoard(UUT_ID);
		Platform.runLater(() -> {
			try {
				refreshDashboard(UUT_ID);
				getCurrentUnitResultData(currentSessionDetails.getUutId());
				createCurrentFailureResultTable();
				getCurrentSessionResultData();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		

	}

	private void refreshDashboard(String UUT_ID) throws java.text.ParseException {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				DashBoardDetailsDTO res = dashboardManagement.getDashboardDetails(UUT_ID);
				String serialNo = res.getSerialNo();

				if (currentSessionDetails.getDfccSerialNumber().equals(serialNo)) {

					List<ProductionDashboardDetails> sessionList = res.getProductionSessionDetailsDTOList();
//	        ////System.out.println("Check Session list: " + sessionList);

					if (sessionList != null && !sessionList.isEmpty()) {

						String lastTestedDateStr = res.getLastTestedDate();
						if (lastTestedDateStr != null) {

							SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
							SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

							Date parsedDate = inputFormat.parse(lastTestedDateStr);
							String formattedDate = outputFormat.format(parsedDate);
							Platform.runLater(() -> {
								lastTestedDateLabel.setText(formattedDate);
//				////System.out.println("Formatted Last Tested Date: " + formattedDate);
							});
						} else {
							Platform.runLater(() -> {
								productionLabel2.setText("Test is Not Started");
								lastTestedDateLabel.setText("-");
							});
						}
						// --- Find Latest Production Session ---
						DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
						DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

						List<ProductionDashboardDetails> validSessions = sessionList.stream()
								.filter(s -> s.getStartTime() != null
										&& !"Not Started".equalsIgnoreCase(s.getStartTime().trim()))
								.toList();

//	            ////System.out.println("Valid Sessions: " + validSessions);

						if (!validSessions.isEmpty()) {
							ProductionDashboardDetails latestSession = validSessions.stream()
									.max(Comparator
											.comparing(s -> LocalDateTime.parse(s.getStartTime(), inputFormatter)))
									.orElse(null);

							if (latestSession != null) {
								Platform.runLater(() -> {
									LocalDateTime latestDate = LocalDateTime.parse(latestSession.getStartTime(),
											inputFormatter);
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
							String lastTestedDateStr = res.getLastTestedDate();

							if (lastTestedDateStr != null) {
								try {
									SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
									SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

									Date parsedDate = inputFormat.parse(lastTestedDateStr);
									String formattedDate = outputFormat.format(parsedDate);

									lastTestedDateLabel.setText(formattedDate);
									productionLabel2.setText("Test is Not Started");

								} catch (java.text.ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								productionLabel2.setText("Test is Not Started");
							}
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
		fourthRow.setPercentHeight(40);

		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(42);

		dashBoardMainContainerGridPane.setVgap(5);

//		////System.out.println("Entred Dahboard");

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

	private StackPane dashboardStackPane = new StackPane();

	private HBox headingHbox() {
		pageHeading.getStyleClass().add("dashboard-top-header");
		headingHbox.setAlignment(Pos.CENTER);
		headingHbox.getChildren().addAll(pageHeading);

		otherTabs.setOnMouseClicked(event -> {
			// Clear previous content
			dashboardStackPane.getChildren().clear();

			// Create new page and add to the stack
			DashboardControllerCenter dashboardControllerCenter = new DashboardControllerCenter();
			GridPane newPage = dashboardControllerCenter.createDashboardCenterMainContainerGridPane();
			dashboardStackPane.getChildren().add(newPage);

			// Make sure the stack pane is added only once to your main container
			if (!dashBoardMainContainerGridPane.getChildren().contains(dashboardStackPane)) {
				dashBoardMainContainerGridPane.add(dashboardStackPane, 0, 1, 1, 4); // spans rows as needed
			}
		});

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
		firstColumn.setPercentWidth(70);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(30);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(85);

		dashboardSessionExecutedGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		dashboardSessionExecutedGridPane.getRowConstraints().addAll(firstRow, secondRow);

		dashboardSessionExecutedGridPane.setHgap(7);
		dashboardSessionExecutedGridPane.setPadding(new Insets(5));

		dashboardSessionExecutedGridPane
				.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");

		dashboardSessionExecutedGridPane.getStyleClass().add("midheader-label-left");
		dashboardSessionExecutedGridPane.getStyleClass().add("midheader-hbox");
		dashboardSessionExecutedGridPane.setAlignment(Pos.TOP_LEFT);
		dashboardSessionExecutedGridPane.add(sessionExecuted, 0, 0);
		dashboardSessionExecutedGridPane.add(createSessionContent(), 0, 1);
		dashboardSessionExecutedGridPane.add(recordedTemp, 1, 0);

		dashboardSessionExecutedGridPane.add(bLSTempData.createBlsTempDataMainContainerGridPane(), 1, 1);
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

		    try {

		        ObservableList<UnitData> selectedItems = unitDataTableView.getSelectedItems();

		        for (UnitData rowData : selectedItems) {

		            UserCenterContentController userCenterContentController = 
		                    UserCenterContentController.getInstance();

		            bottomMidTopGridPane = (GridPane) dashBoardMainContainerGridPane
		                    .getParent().getParent().getParent();

		            UserCenterContentController.currentSessionName = rowData.getSessionName();

		            userCenterContentController.createUserCenterContent(
		                    bottomMidTopGridPane,
		                    "Session Results",
		                    rowData.getId(),
		                    null
		            );

		            break;
		        }

		    } catch (Exception e) {

		        e.printStackTrace(); // optional for debugging

		        Alert alert = new Alert(Alert.AlertType.WARNING);
		        alert.setTitle("Navigation Required");
		        alert.setHeaderText("Unable to open Session Results");
		        alert.setContentText("Please click any menu from the left panel and try again to see the result.");
		        alert.showAndWait();
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
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(85);

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

	private ScrollPane tableScrollPane = new ScrollPane();

	private ObservableList<SessionData> sessionDataList = FXCollections.observableArrayList();
	private TableViewFactory<SessionData> sessionDataFactory = new SessionDataTableViewFactory();

//	private void getCurrentSessionResultData() {
//		Task<Void> task = new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
//
////				ResultSessionStagesDetailsResponse response = resultExecutionManagement
////						.getStagesDetailsForSession(SESSION_ID);
//				
//				ResultSessionStagesDetailsResponse response = dashboardManagement.getStagesFailureDetailsForSession(SESSION_ID);
//				
//				
////            		////System.out.println("getCurrentSessionResultData" + SESSION_ID);
////            		////System.out.println("Response Code" + response.getCode() );
////            		////System.out.println("getResultSessionStagesDetailsDTOList" + response.getResultSessionStagesDetailsDTOList().size());
//				if (response.getCode() == 1 && response.getResultSessionStagesDetailsDTOList() != null) {
//					int i = 1;
//					SimpleDateFormat dbForm = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
//					SimpleDateFormat displayForm = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//					for (ResultSessionStagesDetailsDTO data : response.getResultSessionStagesDetailsDTOList()) {
//						SessionData newSessionData = new SessionData();
//
//						newSessionData.setId(data.getStageId());
//						newSessionData.setTestMode(data.getTestMode());
//						newSessionData.setSlNo(String.valueOf(i));
//						newSessionData.setStage(data.getStage());
//						newSessionData.setStatus(data.getStatus());
//						newSessionData.setResult(data.getResult());
//						newSessionData.setNoOfFilesExecuted(String.valueOf(data.getNoOfFilesExecuted()));
//						newSessionData.setFailedFiles(String.valueOf(data.getFailedFiles()));
////            				newSessionData.setStartTime(data.getStartTime());
////            				////System.out.println("time-----"+data.getStartTime());
////            				newSessionData.setEndTime(data.getEndTime());
////            				////System.out.println("time ---"+data.getEndTime());
//						// changed by sai 11112025
//						String start = data.getStartTime() != null ? data.getStartTime().toString().trim() : "";
//						if (start.isEmpty() || start.equalsIgnoreCase("Not Started") || start.equalsIgnoreCase("-")) {
//							newSessionData.setStartTime(start.isEmpty() ? "N/A" : start);
//						} else {
//							try {
//								Date parseStart = dbForm.parse(start);
//								// //System.out.println("---"+parseStart);
//								newSessionData.setStartTime(displayForm.format(parseStart));
//
//							} catch (Exception e) {
//								newSessionData.setStartTime("Invalid Date");
//							}
//						}
//						String end = data.getEndTime() != null ? data.getEndTime().toString().trim() : "";
////            			    ////System.out.println("dbend  SAI-"+data.getEndTime());
//						if (end.isEmpty() || end.equalsIgnoreCase("Not Started") || end.equals("-")
//								|| end.equalsIgnoreCase("Running")) {
//							newSessionData.setEndTime(end.isEmpty() ? "N/A" : end);
//						} else {
//							try {
//								Date parseEnd = dbForm.parse(end);
//								// //System.out.println();
//								newSessionData.setEndTime(displayForm.format(parseEnd));
//
//							} catch (Exception e) {
//								newSessionData.setEndTime("Invalid Date");
//							}
//						}
//						newSessionData.setTimeTakenForExecution(data.getTimeTakenForExecution());
//						newSessionData.setSessionType(data.getStageMappingId());
//						i++;
//						sessionDataList.add(newSessionData);
//					}
//
////            			////System.out.println("sessionDataList" + sessionDataList.size());
//				} else if (response.getCode() == 0) {
//					Notifications.showErrorAlert(response.geteMsg());
//				}
//
//				if (sessionDataList.size() > 0) {
////                	////System.out.println("sessionDataList Inside" + sessionDataList.size());
//					Platform.runLater(() -> createCurrentFailureResultTable());
//				}
//
//				return null;
//			}
//		};
//
//		task.setOnFailed(evt -> {
////			hideProgressIndicator();
////            ////System.out.println("Entred setOnFailed");
//			task.getException().printStackTrace();
//		});
//
////		task.setOnSucceeded(evt -> hideProgressIndicator());
//
//		task.setOnRunning(evt -> {
//			if (DFCCConstant.isJarBuild) {
////            	////System.out.println("Entred setOnRunning");
////				showProgressIndicator();
//			}
//		});
//		// 20112025
////		task.setOnRunning(evt -> showProgressIndicator());
//		new Thread(task).start();
//	}
	
	private void getCurrentSessionResultData() {

	    Task<Void> task = new Task<>() {
	        @Override
	        protected Void call() throws Exception {

	            ResultSessionStagesDetailsResponse response =
	                    dashboardManagement.getStagesFailureDetailsForSession(SESSION_ID);
	            if (response.getCode() == 1 && response.getResultSessionStagesDetailsDTOList() != null) {

	                sessionDataList.clear();

	                int i = 1;
	                SimpleDateFormat dbForm = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	                SimpleDateFormat displayForm = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	                for (ResultSessionStagesDetailsDTO data : response.getResultSessionStagesDetailsDTOList()) {

	                    SessionData newSessionData = new SessionData();
	                    
	                    newSessionData.setId(data.getStageId());
	                    newSessionData.setTestMode(data.getTestMode());
	                    newSessionData.setSlNo(String.valueOf(i));
	                    newSessionData.setStage(data.getStage());
	                    newSessionData.setStatus(data.getStatus());
	                    newSessionData.setResult(data.getResult());
	                    newSessionData.setNoOfFilesExecuted(String.valueOf(data.getNoOfFilesExecuted()));
	                    newSessionData.setFailedFiles(String.valueOf(data.getFailedFiles()));

	                    String start = data.getStartTime() != null ? data.getStartTime().toString().trim() : "";
	                    if (start.isEmpty() || start.equalsIgnoreCase("Not Started") || start.equalsIgnoreCase("-")) {
	                        newSessionData.setStartTime(start.isEmpty() ? "N/A" : start);
	                    } else {
	                        try {
	                            Date parseStart = dbForm.parse(start);
	                            newSessionData.setStartTime(displayForm.format(parseStart));
	                        } catch (Exception e) {
	                            newSessionData.setStartTime("Invalid Date");
	                        }
	                    }

	                    String end = data.getEndTime() != null ? data.getEndTime().toString().trim() : "";
	                    if (end.isEmpty() || end.equalsIgnoreCase("Not Started")
	                            || end.equals("-") || end.equalsIgnoreCase("Running")) {
	                        newSessionData.setEndTime(end.isEmpty() ? "N/A" : end);
	                    } else {
	                        try {
	                            Date parseEnd = dbForm.parse(end);
	                            newSessionData.setEndTime(displayForm.format(parseEnd));
	                        } catch (Exception e) {
	                            newSessionData.setEndTime("Invalid Date");
	                        }
	                    }

	                    newSessionData.setTimeTakenForExecution(data.getTimeTakenForExecution());
	                    newSessionData.setSessionType(data.getStageMappingId());

	                    sessionDataList.add(newSessionData);
	                    i++;
	                }
	            } else {
	                Platform.runLater(() -> Notifications.showErrorAlert(response.geteMsg()));
	            }

	            return null;
	        }
	    };

//	    task.setOnRunning(e -> Platform.runLater(this::showProgressIndicator));
	    task.setOnSucceeded(e -> {
	        Platform.runLater(() -> {
	            createCurrentFailureResultTable(); // UI update
//	            hideProgressIndicator();
//	          /  DFCCConstant.dashBoardLoading = false;
	        });
	    });

	    task.setOnFailed(e -> {
	        Platform.runLater(() -> {
//	            hideProgressIndicator();
	            task.getException().printStackTrace();
	            Notifications.showErrorAlert("Failed to load session data");
//	            DFCCConstant.dashBoardLoading = false;
	        });
	    });

	    new Thread(task).start();
	}

	private ScrollPane createCurrentFailureResultTable() {

//		Platform.runLater(() -> {
//			currentSessionResultGridPane.getScene().setCursor(Cursor.WAIT);
//	         setControlsDisabled(currentSessionResultGridPane.getScene().getRoot(), true);
//		 });

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				detailedDataTableView = sessionDataFactory.createTableView(sessionDataList, true, false);

//				Label tablePlaceholderLabel = new Label("Select any unit data from unit result table..");
//				tablePlaceholderLabel.setStyle("-fx-font-size:20px;");
//				detailedDataTableView.setPlaceholder(tablePlaceholderLabel);

//	    		Changed by Vignesh 31-07-25 for moving icon location
				if (!detailedDataTableView.getColumns().isEmpty()) {
					TableColumn<SessionData, ?> lastColumn = detailedDataTableView.getColumns()
							.get(detailedDataTableView.getColumns().size() - 1);
					// sessionDataTableView.getColumns().removeLast();
					// Mani Changes By Vignesh Comment
					detailedDataTableView.getColumns().remove(lastColumn);
					detailedDataTableView.getColumns().add(0, lastColumn);
				}

				detailedDataTableView.getColumns().forEach(column -> {
					if (!column.getText().isEmpty()) {
//	    				SAI ADDED
						String colName = column.getText();
//	    	            ////System.out.println("Sai :"  + colName);

//	    				column.setMinWidth(column.getText().length()*14);
						switch (colName) {
						case "SL NO":
							column.setMinWidth(100);
							column.setMaxWidth(100);
							break;
						case "TEST MODE":
							column.setMinWidth(300);
							column.setMaxWidth(300);
							break;
						case "STAGE":
							column.setText("SESSION NAME");
							column.setMinWidth(150);
							column.setMaxWidth(150);
							break;
						case "STATUS":
							column.setMinWidth(140);
							column.setMaxWidth(140);
							break;
						case "RESULT":
							column.setMinWidth(120);
							column.setMaxWidth(120);
							break;
						case "NO OF FILES EXECUTED":
							column.setMinWidth(210);
							column.setMaxWidth(210);
							break;
						case "FAILED FILES":
							column.setMinWidth(120);
							column.setMaxWidth(120);
							break;
						case "START TIME":
							column.setMinWidth(250);
							column.setMaxWidth(250);
							break;
						case "END TIME":
							column.setMinWidth(250);
							column.setMaxWidth(250);
							break;
						case "TIME TAKEN FOR EXECUTION":
							column.setMinWidth(270);
							column.setMaxWidth(270);
							break;

						default:
							column.setMinWidth(120);
							column.setMaxWidth(120);
							break;
						}
						updateSessionData((TableColumn<SessionData, String>) column);

					}
				});

				detailedDataTableView.getColumns().removeIf(column -> column.getText().equalsIgnoreCase("Session Type")
						|| column.getId() != null && column.getId().equalsIgnoreCase("sessionType"));

				detailedDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {

				    try {

				        ObservableList<SessionData> selectedItems = detailedDataTableView.getSelectedItems();

				        for (SessionData rowData : selectedItems) {

				            GridPane bottomMidTopGridPane = (GridPane) dashBoardMainContainerGridPane
				                    .getParent().getParent().getParent();

				            userCenterContentController.createUserCenterContent(
				                    bottomMidTopGridPane,
				                    "Stage Results",
				                    SESSION_ID,
				                    rowData
				            );

				            break;
				        }

				    } catch (Exception e) {

				        e.printStackTrace(); // optional for debugging

				        Alert alert = new Alert(Alert.AlertType.WARNING);
				        alert.setTitle("Navigation Required");
				        alert.setHeaderText("Unable to open Stage Results");
				        alert.setContentText("Please click any menu from the left panel and try again to see the result.");
				        alert.showAndWait();
				    }
				});

				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					tableScrollPane.setContent(detailedDataTableView);
					tableScrollPane.setFitToHeight(true);
//					 DFCCConstant.dashBoardLoading = false;
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
//				 DFCCConstant.dashBoardLoading = false;
			}
		};
		
		new Thread(task).start();

		return tableScrollPane;
	}
	
//	private ScrollPane createCurrentFailureResultTable() {
//
//	    Platform.runLater(() -> {
//
//	        detailedDataTableView = sessionDataFactory.createTableView(sessionDataList, true, false);
//
//	        // Move last column to first
//	        if (!detailedDataTableView.getColumns().isEmpty()) {
//	            TableColumn<SessionData, ?> lastColumn =
//	                    detailedDataTableView.getColumns().get(detailedDataTableView.getColumns().size() - 1);
//
//	            detailedDataTableView.getColumns().remove(lastColumn);
//	            detailedDataTableView.getColumns().add(0, lastColumn);
//	        }
//
//	        // Column sizing
//	        detailedDataTableView.getColumns().forEach(column -> {
//	            if (!column.getText().isEmpty()) {
//
//	                String colName = column.getText();
//
//	                switch (colName) {
//	                    case "SL NO" -> column.setPrefWidth(100);
//	                    case "TEST MODE" -> column.setPrefWidth(300);
//	                    case "STAGE" -> column.setPrefWidth(150);
//	                    case "STATUS" -> column.setPrefWidth(140);
//	                    case "RESULT" -> column.setPrefWidth(120);
//	                    case "NO OF FILES EXECUTED" -> column.setPrefWidth(210);
//	                    case "FAILED FILES" -> column.setPrefWidth(120);
//	                    case "START TIME" -> column.setPrefWidth(250);
//	                    case "END TIME" -> column.setPrefWidth(250);
//	                    case "TIME TAKEN FOR EXECUTION" -> column.setPrefWidth(270);
//	                    default -> column.setPrefWidth(120);
//	                }
//
//	                updateSessionData((TableColumn<SessionData, String>) column);
//	            }
//	        });
//
//	        // Remove unwanted columns
//	        detailedDataTableView.getColumns().removeIf(column ->
//	                column.getText().equalsIgnoreCase("Session Type") ||
//	                        (column.getId() != null && column.getId().equalsIgnoreCase("sessionType"))
//	        );
//
//	        // Click event
//	        detailedDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
//	            try {
//	                ObservableList<SessionData> selectedItems = detailedDataTableView.getSelectedItems();
//
//	                for (SessionData rowData : selectedItems) {
//
//	                    GridPane bottomMidTopGridPane =
//	                            (GridPane) dashBoardMainContainerGridPane.getParent().getParent().getParent();
//
//	                    userCenterContentController.createUserCenterContent(
//	                            bottomMidTopGridPane,
//	                            "Stage Results",
//	                            SESSION_ID,
//	                            rowData
//	                    );
//	                    break;
//	                }
//
//	            } catch (Exception e) {
//	                e.printStackTrace();
//
//	                Alert alert = new Alert(Alert.AlertType.WARNING);
//	                alert.setTitle("Navigation Required");
//	                alert.setHeaderText("Unable to open Stage Results");
//	                alert.setContentText("Please click any menu from the left panel and try again.");
//	                alert.showAndWait();
//	            }
//	        });
//
//	        tableScrollPane.setContent(detailedDataTableView);
//	        tableScrollPane.setFitToHeight(true);
//	    });
//
//	    return tableScrollPane;
//	}

	private ProgressIndicator progressIndicator = new ProgressIndicator();
	private VBox box = new VBox();

//	private void showProgressIndicator() {
//		GridPane parentStackPane = (GridPane) dashBoardMainContainerGridPane.getParent().getParent();
//		box.getChildren().add(progressIndicator);
//		box.setAlignment(Pos.CENTER);
//		parentStackPane.getChildren().add(box);
//	}

//	private void hideProgressIndicator() {
//		GridPane parentStackPane = (GridPane) dashBoardMainContainerGridPane.getParent().getParent();
//		if (parentStackPane.getChildren().contains(box)) {
//			parentStackPane.getChildren().remove(box);
//		}
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
						if ("TEST MODE".equalsIgnoreCase(column.getText())
								|| "STAGE".equalsIgnoreCase(column.getText())) {
							Platform.runLater(() -> {
								label.setWrapText(true);
							});
						} else {
							label.setWrapText(false);
						}
//                    label.setWrapText(false);

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

}
