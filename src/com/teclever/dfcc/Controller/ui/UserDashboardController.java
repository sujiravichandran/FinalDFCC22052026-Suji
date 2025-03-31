package com.teclever.dfcc.Controller.ui;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.MacroConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.LogOutFileCopyResponse;
import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.dto.ValidateResponse;
import com.teclever.dfcc.datastore.filemanagement.Aitess2ConfigManagement;
import com.teclever.dfcc.datastore.filemanagement.ChecksumManagement;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.CheckSumList;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTempAEC;
import com.teclever.dfcc.stateMachine.StateMachine.channelAECTemp;
import com.teclever.dfcc.stateMachine.StateMachine.channelSCTemp;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;
import com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Notifications;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UserDashboardController {

	Boolean checkSumFinalResult = true;

	protected static final TestState RUNNING = null;
	private static final TestState PENDING = null;
	private GridPane bottomMainGridPane = new GridPane();
	private GridPane bottomGridPane = new GridPane();
	private GridPane bottomMidTopGridPane = new GridPane();

	private ObservableMap<String, ChannelTemperature> scBoardTemperatureMap;
	private ObservableMap<String, ChannelTemperature> aecBoardTemperatureMap;
	
	private ObservableMap<String, channelSCTemp> scBoardTemperatureMapMk1;
	private ObservableMap<String, channelSCTemp> aecBoardTemperatureMapMk1;
	
	
	
	private List<MacroButtonMapDto> macroButtonList;
	ChecksumManagement checksumManagement = new ChecksumManagement();

	UserCenterContentController centerContentController = UserCenterContentController.getInstance();
	SessionManagement sessionManagement = new SessionManagement();
	MacroConfigurationManagement macroConfigurationManagement = new MacroConfigurationManagement();
	AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
	Aitess2ConfigManagement aitess2ConfigManagement = new Aitess2ConfigManagement();
	CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
	private final SystemConfigManagement systemConfigManagement = new SystemConfigManagement();

	private GridPane terminalGridPane = new GridPane();
	private TextField terminaTextField = new TextField();
	private Button enterButton = new Button("ENTER");
	private Button yesButton = new Button("YES");
	private Button noButton = new Button("NO");
	private StackPane terminalStackPane;
	private Rectangle background = new Rectangle(80, 30, Color.RED);
	private Circle toggleButton = new Circle(12, Color.WHITE);
	private Label toggleLabel = new Label("OFF");
	private StackPane stack = new StackPane();
	private HBox toggleSwitch = new HBox();
	TerminalController terminalController1 = new TerminalController();
	
	private MapChangeListener<String, ChannelTemperature> scListener;
	private MapChangeListener<String, ChannelTemperature> aecListener;
	
	private MapChangeListener<String, channelSCTemp> scListenerMk1;
	private MapChangeListener<String, channelAECTemp> aecListenerMk1;

	public UserDashboardController() {
		terminalStackPane = terminalController1.createTerminalStackPane();
//		CheckToggleStatus();
		Platform.runLater(() -> {
		    System.out.println("Updated Test State: " + StateMachine.getTestState());
		    initialize();
		});
		
	}
	
	public void initialize() {
		updateUI(StateMachine.getTestState());

	    StateMachine.testStateProperty().addListener((obs, oldState, newState) -> {
	        updateUI(newState);
	    });
	}
	
	private void updateUI(TestState state) {
	    Platform.runLater(() -> {
	        if (state == TestState.RUNNING) {
	            toggleButton.setDisable(true);
	        }else {
	        	toggleButton.setDisable(false);
	        }
	    });
	}
	
	public GridPane createUserDashboard() {
		aitess2ConfigManagement.getAllDfccStatusCommand();
		bottomMainGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/UserDashboard.css").toExternalForm());
		bottomMainGridPane.setHgap(10);
		bottomMainGridPane.setId("bottomMainGridPane");
		bottomMidTopGridPane.setId("bottomMidTopGridPane");

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(17);
		ColumnConstraints bottomMidColumn = new ColumnConstraints();
		bottomMidColumn.setPercentWidth(66);
		ColumnConstraints bottomRightColumn = new ColumnConstraints();
		bottomRightColumn.setPercentWidth(17);

		RowConstraints bottomRow = new RowConstraints();
		bottomRow.setPercentHeight(100);

		bottomMainGridPane.getColumnConstraints().addAll(bottomLeftColumn, bottomMidColumn, bottomRightColumn);
		bottomMainGridPane.getRowConstraints().add(bottomRow);

		bottomMainGridPane.add(createBottomleftGridPane(), 0, 0);
		bottomMainGridPane.add(createBottomMidGridPane(), 1, 0);
		bottomMainGridPane.add(createBottomRightGridPane(), 2, 0);
		
		return bottomMainGridPane;

	}

	private GridPane createBottomleftGridPane() {
		GridPane bottomLeftGridPane = new GridPane();
		bottomLeftGridPane.setVgap(10);

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(100);

		RowConstraints bottomLeftTopRow = new RowConstraints();
		bottomLeftTopRow.setPercentHeight(69);

		RowConstraints bottomLeftMidRow = new RowConstraints();
		bottomLeftMidRow.setPercentHeight(24);

		RowConstraints bottomLeftBottomRow = new RowConstraints();
		bottomLeftBottomRow.setPercentHeight(7);

		bottomLeftGridPane.getColumnConstraints().add(bottomLeftColumn);
		bottomLeftGridPane.getRowConstraints().addAll(bottomLeftTopRow, bottomLeftMidRow, bottomLeftBottomRow);

		bottomLeftGridPane.add(createMenuBox(), 0, 0);
		bottomLeftGridPane.add(createSubMenuBox(), 0, 1);
		bottomLeftGridPane.add(createButtonBox(), 0, 2);

		return bottomLeftGridPane;
	}

	private TreeView<Label> createMenuBox() {
		TreeItem<Label> rootItem = new TreeItem<>();
		rootItem.setExpanded(true);
		TreeView<Label> menuTreeView = new TreeView<>(rootItem);
		menuTreeView.getStyleClass().add("menu-container");
		menuTreeView.setShowRoot(false);

		menuTreeView.setOnMouseClicked(event -> {
			TreeItem<Label> selectedItem = menuTreeView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				Label selectedLabel = selectedItem.getValue();

				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on " + selectedLabel.getText() + " menu");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

				if (selectedItem.getChildren().isEmpty()) {
					centerContentController.createUserCenterContent(bottomMidTopGridPane, selectedLabel.getText(), null,
							null);
				}

				if (!selectedItem.getChildren().isEmpty()) {
					selectedItem.getChildren().forEach(subMenuItem -> {
					});
				}
			}
		});

		if (UserData.getRoleId().equals("RL_ID_3")) {
			addTreeItemWithChildren(rootItem, "Dashboard",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/dashboard.png", null);

			if (currentSessionDetails.getSessionTypeID().equals("ST4")) {
				addTreeItemWithChildren(rootItem, "Testing",
						DFCCConstant.JARSTRING + "/Resources/Images/menuImages/testing.png", new String[] { "Self Test",
								"SRU/LRU Test", "Trials Config", "Trials Testing", "Advanced Testing" });
			} else {
				addTreeItemWithChildren(rootItem, "Testing",
						DFCCConstant.JARSTRING + "/Resources/Images/menuImages/testing.png",
						new String[] { "Self Test", "SRU/LRU Test", "Session Testing", "Advanced Testing" });
			}

			addTreeItemWithChildren(rootItem, "Results",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png",
					new String[] { "Current Execution", "Unit Results", "Session Results", "Stage Results" });
			addTreeItemWithChildren(rootItem, "Data Analysis",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/advance_testing.png", null);
			addTreeItemWithChildren(rootItem, "Reports",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png",
					new String[] { "PQT Report", "ESS Report", "Datapack Report", "Upload", "Data Backup" });

		} else if (UserData.getRoleId().equals("RL_ID_4")) {

			addTreeItemWithChildren(rootItem, "Testing",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/testing.png",
					new String[] { "Self Test", "SRU/LRU Test" });
			addTreeItemWithChildren(rootItem, "Results",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png",
					new String[] { "Current Execution", "Unit Results", "Session Results", "Stage Results" });
			addTreeItemWithChildren(rootItem, "Test Summary",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/advance_testing.png", null);
			addTreeItemWithChildren(rootItem, "Reports",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png",
					new String[] { "Data Backup" });
			addTreeItemWithChildren(rootItem, "History Reports",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png", null);
		}

		menuTreeView.setPadding(new Insets(5, 10, 5, 10));

		return menuTreeView;
	}

	private void addTreeItemWithChildren(TreeItem<Label> parent, String text, String imagePath, String[] children) {
		Image menuImage = new Image(imagePath);
		Label newMenuItem = new Label(text);
		ImageView menuImageView = new ImageView(menuImage);
		menuImageView.setFitWidth(newMenuItem.getFont().getSize() + 30);
		menuImageView.setFitHeight(newMenuItem.getFont().getSize() + 30);
		menuImageView.getStyleClass().add("menu-image");

		newMenuItem.setGraphic(menuImageView);
		newMenuItem.getStyleClass().add("menu-item");

		TreeItem<Label> menuItem = new TreeItem<>(newMenuItem);

		if (children != null) {
			for (String child : children) {
				Label newSubMenuItem = new Label(child);
				TreeItem<Label> childItem = new TreeItem<>(newSubMenuItem);
				menuItem.getChildren().add(childItem);
				newSubMenuItem.getStyleClass().add("sub-menu-item");
			}
		}
		parent.getChildren().add(menuItem);
	}

	private VBox createSubMenuBox() {
		VBox middleMenuBox = new VBox(3);
		middleMenuBox.setAlignment(Pos.CENTER);
		middleMenuBox.getStyleClass().add("middle-menu");

		String[] labelsText = { "Configuration", "Utility", "End Session", "Log Book" };

		for (String labelText : labelsText) {
			Label label = new Label(labelText);
			label.prefWidthProperty().bind(middleMenuBox.widthProperty());
			label.getStyleClass().add("middle-menu-item");

			label.setOnMouseClicked(event -> {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on " + labelText);
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

				if (labelText == "End Session") {
					UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
					UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
							currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
							StateMachine.getCurrentUserLogin(), new Date(),
							"session " + currentSessionDetails.getSessionName() + " ended");
					uutLogbookManagement.addUUTLogBook(uutLogBookDto);
				}

				if (labelText == "Close Session") {
					UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
					UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
							currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
							StateMachine.getCurrentUserLogin(), new Date(),
							"session " + currentSessionDetails.getSessionName() + " closed");
					uutLogbookManagement.addUUTLogBook(uutLogBookDto);
				}
				centerContentController.createUserCenterContent(bottomMidTopGridPane, labelText, null, labelText);
				for (Node node : middleMenuBox.getChildren()) {
					if (node instanceof Label) {
						((Label) node).getStyleClass().remove("selected");
					}
				}
				label.getStyleClass().add("selected");
			});

			middleMenuBox.getChildren().add(label);
		}
		return middleMenuBox;
	}

	private GridPane createButtonBox() {
		GridPane bottomButtonGridPane = new GridPane();
		bottomButtonGridPane.setHgap(10);
		bottomButtonGridPane.setVgap(10);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomButtonGridPane.getColumnConstraints().addAll(firstColumn);
		bottomButtonGridPane.getRowConstraints().addAll(firstRow);

		VBox logoutBox = new VBox();
		logoutBox.setAlignment(Pos.CENTER);
		Label logoutLabel = new Label("Logout");
		logoutBox.getChildren().add(logoutLabel);
		logoutBox.getStyleClass().add("logout-button");
		logoutLabel.getStyleClass().add("logout-text");

		logoutBox.setOnMouseClicked(e -> {
			
			
			if (StateMachine.getTestState() == TestState.PENDING || StateMachine.getTestState() == TestState.STOPPED
					|| StateMachine.getTestState() == TestState.COMPLETED) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						StateMachine.getCurrentUserLogin() + " logged out");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement
						.getInstance();

				Notifications.showConfirmationDialog("Logout Confirmation",
						"Are you sure you want to log out and close the application?", () -> {
							SessionFileManagement session = new SessionFileManagement();
							LogOutFileCopyResponse response = session
									.copyingFileWhileLogOut(StateMachine.currentSessionDetails.getSessionId());
//		         System.out.println("Response code for copied or not : "+response.getCode() );
							if (response.getCode() == 1) {
								aitessProcessControlManagement.endAllProcessOnLogout();
								Platform.exit();
								System.exit(0);
							} else if (response.getCode() == 0) {
								Alert alert = new Alert(AlertType.ERROR);
								alert.setTitle("Error Dialog");
								alert.setHeaderText(null);
								alert.setContentText("Something went wrong! The application will now close.");

								alert.setOnCloseRequest(event -> {
									aitessProcessControlManagement.endAllProcessOnLogout();
									Platform.exit();
									System.exit(0);
								});

								alert.showAndWait();
							} else if (response.getCode() == 100) {
								SessionTestStateObject.isLogoutFileCopyPopupOpenedProperty()
										.addListener((observable, oldValue, newValue) -> {
											if (!newValue) {
												aitessProcessControlManagement.endAllProcessOnLogout();
												Platform.exit();
												System.exit(0);
											}
										});
								SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(true);
								SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
							}
						});
			} else if (StateMachine.getTestState() == TestState.PAUSED
					|| StateMachine.getTestState() == TestState.RUNNING) {
				Notifications.showWarningAlert("Please stop " + StateMachine.getRunningTestName()
						+ " test before log out and close the application");
			}
		});

		bottomButtonGridPane.add(logoutBox, 0, 0);

		return bottomButtonGridPane;

	}

	private GridPane createBottomRightGridPane() {
		GridPane bottomRightGridPane = new GridPane();
		bottomRightGridPane.setVgap(10);

		ColumnConstraints bottomRightColumn = new ColumnConstraints();
		bottomRightColumn.setPercentWidth(100);

		RowConstraints bottomRightTopRow = new RowConstraints();
		bottomRightTopRow.setPercentHeight(15);

		RowConstraints bottomRightMidRow = new RowConstraints();
		bottomRightMidRow.setPercentHeight(60);

		RowConstraints bottomRightBottomRow = new RowConstraints();
		bottomRightBottomRow.setPercentHeight(28);

		RowConstraints bottomRightBottomLastRow = new RowConstraints();
		bottomRightBottomLastRow.setPercentHeight(7);

		bottomRightGridPane.getColumnConstraints().add(bottomRightColumn);
		bottomRightGridPane.getRowConstraints().addAll(bottomRightTopRow, bottomRightMidRow, bottomRightBottomRow,
				bottomRightBottomLastRow);

		bottomRightGridPane.add(createRightTop(), 0, 0);
		bottomRightGridPane.add(createRightMidFirst(), 0, 1);
		bottomRightGridPane.add(createRigthMidSecond(), 0, 2);
		bottomRightGridPane.add(createRightBottom(), 0, 3);
		

		return bottomRightGridPane;
	}

	private VBox createRightTop() {
		VBox bottomRightTopBox = new VBox();
		bottomRightTopBox.setAlignment(Pos.CENTER_LEFT);
		bottomRightTopBox.getStyleClass().add("right-first-container");

		Label titleLabel = new Label("Session Details");
		titleLabel.getStyleClass().add("right-top-title");

		GridPane bottomRightTopGridPane = new GridPane();
		bottomRightTopGridPane.setVgap(5);
		bottomRightTopGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomRightTopGridPane.getColumnConstraints().addAll(firstColumn);
		bottomRightTopGridPane.getRowConstraints().addAll(firstRow);

		Label newLabel = new Label();

		Tooltip tooltip = new Tooltip(currentSessionDetails.getSessionName());
		Tooltip.install(newLabel, tooltip);
		tooltip.setShowDelay(Duration.ZERO);
		tooltip.setHideDelay(Duration.ZERO);

		newLabel.setText(currentSessionDetails.getSessionName());
		newLabel.setPrefWidth(290);
		newLabel.setWrapText(true);
		newLabel.setPadding(new Insets(0, 0, 0, 5));
		newLabel.getStyleClass().add("session-name-label");

		bottomRightTopBox.getChildren().addAll(titleLabel, newLabel);

		return bottomRightTopBox;
	}

	private Pane createRightMidFirst() {
		Pane bottomRightMidPane = new Pane();

		GridPane bottomRightMidGridPane = new GridPane();

		bottomRightMidGridPane.getStyleClass().add("right-second-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(38);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(26);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(26);

		bottomRightMidGridPane.getColumnConstraints().add(firstColumn);
		bottomRightMidGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);

		bottomRightMidGridPane.add(createBottomRightMidFirst(), 0, 0);
		bottomRightMidGridPane.add(createBottomRightMidSecond(), 0, 1);
		bottomRightMidGridPane.add(createBottomRightMidThird(), 0, 2);
		bottomRightMidGridPane.add(createBottomRightMidFourth(), 0, 3);

		bottomRightMidPane.getChildren().add(bottomRightMidGridPane);
		return bottomRightMidGridPane;
	}

	private GridPane createBottomRightMidFirst() {
//		GridPane bottomRightMidFirstGridPane = new GridPane();
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(40);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(60);
//
//		bottomRightMidFirstGridPane.getColumnConstraints().add(firstColumn);
//		bottomRightMidFirstGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		VBox firstRowBox = new VBox(10);
//		firstRowBox.setAlignment(Pos.CENTER_LEFT);
//		firstRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
//		firstRowBox.prefHeightProperty().bind(firstRow.prefHeightProperty());
//		Label titleLabel = new Label("DFCC");
//		titleLabel.getStyleClass().add("dfcc-title");
//		firstRowBox.getChildren().add(titleLabel);
//
//		VBox secondRowBox = new VBox(10);
//		secondRowBox.setAlignment(Pos.CENTER_LEFT);
//		secondRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
//		secondRowBox.prefHeightProperty().bind(secondRow.prefHeightProperty());
//		Label statusLabel = new Label("DFCC Power OFF");
//
//		secondRowBox.setOnMouseClicked(e -> {
//			if (!checkAitessStatus.isBothAitessOn()) {
//				return;
//			}
//			if (statusLabel.getText().toLowerCase().contains("on")) {
//				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
//				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
//						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
//						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
//						"clicked on DFCC power ON button");
//				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
//
//				UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
//				UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
//						currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
//						StateMachine.getCurrentUserLogin(), new Date(), "DFCC gets powered OFF");
//				uutLogbookManagement.addUUTLogBook(uutLogBookDto);
//				aitessProcessControlManagement.WriteDfccPowerOffCommandToAitess2();
//
//				Platform.runLater(() -> {
//					statusLabel.setText("DFCC Power OFF");
//					dfccCheckStatus.getDfccPowerStatus().set(false);
//				});
//
//			} else if (statusLabel.getText().toLowerCase().contains("off")) {
//				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
//				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
//						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
//						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
//						"clicked on DFCC power OFF button");
//				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
//
//				UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
//				UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
//						currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
//						StateMachine.getCurrentUserLogin(), new Date(), "DFCC gets powered ON");
//				uutLogbookManagement.addUUTLogBook(uutLogBookDto);
//				aitessProcessControlManagement.WriteDfccPowerOnCommandToAitess2();
//
//				Platform.runLater(() -> {
//					statusLabel.setText("DFCC Power ON");
//					dfccCheckStatus.getDfccPowerStatus().set(true);
//				});
//
//			}
//		});
//
//		dfccCheckStatus.dfccPowerStatusProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue) {
//				secondRowBox.getStyleClass().remove("dfcc-status-box-off");
//				secondRowBox.getStyleClass().add("dfcc-status-box-on");
//			} else {
//				secondRowBox.getStyleClass().remove("dfcc-status-box-on");
//				secondRowBox.getStyleClass().add("dfcc-status-box-off");
//			}
//		});
//		secondRowBox.getStyleClass().addAll("dfcc-status-box", "dfcc-status-box-off");
//		statusLabel.getStyleClass().add("dfcc-status");
//		secondRowBox.getChildren().add(statusLabel);
//
//		bottomRightMidFirstGridPane.add(firstRowBox, 0, 0);
//		bottomRightMidFirstGridPane.add(secondRowBox, 0, 1);
//
//		return bottomRightMidFirstGridPane;

		GridPane bottomRightMidFirstGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomRightMidFirstGridPane.getColumnConstraints().add(firstColumn);
		bottomRightMidFirstGridPane.getRowConstraints().addAll(firstRow);

		HBox firstRowBox = new HBox(10);
		firstRowBox.setAlignment(Pos.CENTER_LEFT);

		firstRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
		firstRowBox.prefHeightProperty().bind(firstRow.prefHeightProperty());

		Label titleLabel = new Label("DFCC Power");
		titleLabel.getStyleClass().add("dfcc-title");

		HBox toggleSwitch = createToggleSwitch();

		firstRowBox.getChildren().addAll(titleLabel, toggleSwitch);
		bottomRightMidFirstGridPane.add(firstRowBox, 0, 0);

		return bottomRightMidFirstGridPane;
	}


	private HBox createToggleSwitch() {
		background.setArcWidth(30);
		background.setArcHeight(30);

		toggleButton.setTranslateX(-26);

		toggleLabel.setTextFill(Color.WHITE);
		toggleLabel.setStyle("-fx-font-size:16px; -fx-font-weight: bold; -fx-padding:0px 5px");

		stack.getChildren().addAll(background, toggleLabel, toggleButton);

		toggleSwitch.setSpacing(0);
		toggleSwitch.getChildren().add(stack);

		// Set the initial alignment of the label
		StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);

		toggleButton.setOnMouseClicked(event -> {
			onClickToggle(background, toggleButton, toggleLabel);
		});
		toggleButton.setCursor(Cursor.HAND);

		dfccCheckStatus.dfccPowerStatusProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(newValue);
			TranslateTransition transition = new TranslateTransition(Duration.millis(200), toggleButton);
			
			if (dfccCheckStatus.getDfccPowerStatus().get()) {
				Platform.runLater(() -> {
				transition.setToX(26);
				background.setFill(Color.GREEN);
				toggleLabel.setText("ON");
				StackPane.setAlignment(toggleLabel, Pos.CENTER_LEFT);
				transition.play();
			});
			}
			
			else {
				Platform.runLater(() -> {
				transition.setToX(-26);
				background.setFill(Color.RED);
				toggleLabel.setText("OFF");
				StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
				transition.play();
				});
			}
		});

		return toggleSwitch;
	}
	
	private void onClickToggle(Rectangle background2, Circle toggleButton, Label toggleLabel) {
		if (!checkAitessStatus.isBothAitessOn()) {
			return;
		}
		if (StateMachine.isAllowToggle()) {
			StateMachine.setAllowToggle(false);
			if (dfccCheckStatus.getDfccPowerStatus().get()) {				
				aitessProcessControlManagement.WriteDfccPowerOffCommandToAitess2();
			} else {
				Dialog<ButtonType> dialog = new Dialog<>();
				dialog.setTitle("Confirmation Dialog");
				dialog.setContentText("Please ensure the cooler switch is turned ON.");
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
				dialog.showAndWait();
				aitessProcessControlManagement.WriteDfccPowerOnCommandToAitess2();
			}
		}
	}

	private final Random random = new Random();

	private void generateData() {
		int n = random.nextInt(100);
		ChannelTemperature a = new ChannelTemperature();
		a.setChannel1Temp("a" + n);
		a.setChannel2Temp("a" + n);
		a.setChannel3Temp("a" + n);
		a.setChannel4Temp("a" + n);
		ChannelTemperature b = new ChannelTemperature();
		b.setChannel1Temp("b" + n);
		b.setChannel2Temp("b" + n);
		b.setChannel3Temp("b" + n);
		b.setChannel4Temp("b" + n);
		ChannelTemperature c = new ChannelTemperature();
		c.setChannel1Temp("c" + n);
		c.setChannel2Temp("c" + n);
		c.setChannel3Temp("c" + n);
		c.setChannel4Temp("c" + n);
		ChannelTemperature d = new ChannelTemperature();
		d.setChannel1Temp("d" + n);
		d.setChannel2Temp("d" + n);
		d.setChannel3Temp("d" + n);
		d.setChannel4Temp("d" + n);

		boardChannelTemp.addBoardTemperatureMap("DFCC_TEMP_AN2", a);
		boardChannelTemp.addBoardTemperatureMap("AN1L_BRD_TEMP", b);
		boardChannelTemp.addBoardTemperatureMap("AN1R_BRD_TEMP", c);
		boardChannelTemp.addBoardTemperatureMap("DM_BRD_TEMP", d);

		ChannelTemperature a1 = new ChannelTemperature();
		a1.setChannel1Temp("aa" + n);
		a1.setChannel2Temp("aa" + n);
		a1.setChannel3Temp("aa" + n);
		a1.setChannel4Temp("aa" + n);
		ChannelTemperature b1 = new ChannelTemperature();
		b1.setChannel1Temp("bb" + n);
		b1.setChannel2Temp("bb" + n);
		b1.setChannel3Temp("bb" + n);
		b1.setChannel4Temp("bb" + n);
		ChannelTemperature c1 = new ChannelTemperature();
		c1.setChannel1Temp("cc" + n);
		c1.setChannel2Temp("cc" + n);
		c1.setChannel3Temp("cc" + n);
		c1.setChannel4Temp("cc" + n);
		ChannelTemperature d1 = new ChannelTemperature();
		d1.setChannel1Temp("dd" + n);
		d1.setChannel2Temp("dd" + n);
		d1.setChannel3Temp("dd" + n);
		d1.setChannel4Temp("dd" + n);

		boardChannelTempAEC.addBoardTemperatureMap("DFCC_TEMP_AN2", a1);
		boardChannelTempAEC.addBoardTemperatureMap("AN1L_BRD_TEMP", b1);
		boardChannelTempAEC.addBoardTemperatureMap("AN1R_BRD_TEMP", c1);
		boardChannelTempAEC.addBoardTemperatureMap("DM_BRD_TEMP", d1);

		channelSCTemp.setChannel1Temperature("1S" + n);
		channelSCTemp.setChannel2Temperature("2S" + n);
		channelSCTemp.setChannel3Temperature("3S" + n);
		channelSCTemp.setChannel4Temperature("4S" + n);

		channelAECTemp.setChannel1Temperature("1A" + n);
		channelAECTemp.setChannel2Temperature("2A" + n);
		channelAECTemp.setChannel3Temperature("3A" + n);
		channelAECTemp.setChannel4Temperature("4A" + n);
	}

	private VBox createBottomRightMidSecond() {

//		Timeline timeline = new Timeline(
//	            new KeyFrame(Duration.seconds(5), event -> generateData())
//	        );
//		timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
//        timeline.play();

		VBox bottomRightMidSecondBox = new VBox(10);
		bottomRightMidSecondBox.setAlignment(Pos.CENTER_LEFT);

//		Temperature Combo Box
		Label titleLabel = new Label("Temperature");
		titleLabel.getStyleClass().add("right-common-title");

		ComboBox<String> temperatureComboBox = new ComboBox<String>();
		temperatureComboBox.getItems().addAll("SC", "AEC");

		HBox temperatureTitleHBox = new HBox(5);
		HBox.setHgrow(temperatureComboBox, Priority.ALWAYS);
		temperatureComboBox.setMaxWidth(Double.MAX_VALUE);
		temperatureTitleHBox.getChildren().addAll(titleLabel, temperatureComboBox);

//		Board Combo Box
		Label boardLabel = new Label("Board Name");
		boardLabel.getStyleClass().add("right-common-title");

		ComboBox<String> boardComboBox = new ComboBox<String>();
		boardComboBox.getItems().addAll("DFCC_TEMP_AN2", "AN1L_BRD_TEMP", "AN1R_BRD_TEMP", "DM_BRD_TEMP");

		HBox boardTitleHBox = new HBox(5);
		HBox.setHgrow(boardComboBox, Priority.ALWAYS);
		boardComboBox.setMaxWidth(Double.MAX_VALUE);
		boardTitleHBox.getChildren().addAll(boardLabel, boardComboBox);

		GridPane bottomRightMidSecondGridPane = new GridPane();
		bottomRightMidSecondGridPane.setVgap(5);
		bottomRightMidSecondGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);

		bottomRightMidSecondGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		bottomRightMidSecondGridPane.getRowConstraints().addAll(firstRow, secondRow);

		VBox box1 = new VBox();
		box1.setAlignment(Pos.CENTER);
		Label label1 = new Label("N/A");
		box1.getChildren().add(label1);
		box1.getStyleClass().add("temp-box");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("N/A");
		box2.getChildren().add(label2);
		box2.getStyleClass().add("temp-box");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("N/A");
		box3.getChildren().add(label3);
		box3.getStyleClass().add("temp-box");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("N/A");
		box4.getChildren().add(label4);
		box4.getStyleClass().add("temp-box");

		bottomRightMidSecondGridPane.add(box1, 0, 0);
		bottomRightMidSecondGridPane.add(box2, 1, 0);
		bottomRightMidSecondGridPane.add(box3, 0, 1);
		bottomRightMidSecondGridPane.add(box4, 1, 1);

		temperatureComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			if (!checkAitessStatus.isBothAitessOn()) {
//				return;
//			}
			Platform.runLater(() -> {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on " + newValue + " temperature");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				if (currentSessionDetails.getUutId().equals("UUT1")) {
					setMK1Temp(newValue, box1, box2, box3, box4);
				} else {
					getMK1AandMk2TempData(temperatureComboBox.getValue(), boardComboBox.getValue(), box1, box2, box3,
							box4);
				}
			});
		});

		boardComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (!currentSessionDetails.getUutId().equals("UUT1")) {
//				if (!checkAitessStatus.isBothAitessOn()) {
//					return;
//				}
				Platform.runLater(() -> {
					ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
					ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
							currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
							currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
							"clicked on " + newValue + " temperature board");
					appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

					getMK1AandMk2TempData(temperatureComboBox.getValue(), newValue, box1, box2, box3, box4);
				});
			}
		});

		temperatureComboBox.setValue("SC");
		boardComboBox.setValue("DFCC_TEMP_AN2");

		if (currentSessionDetails.getUutId().equals("UUT1")) {
			bottomRightMidSecondBox.getChildren().addAll(temperatureTitleHBox, bottomRightMidSecondGridPane);
		} else {
			bottomRightMidSecondBox.getChildren().addAll(temperatureTitleHBox, boardTitleHBox,
					bottomRightMidSecondGridPane);
		}

		return bottomRightMidSecondBox;

	}

	private void getMK1AandMk2TempData(String temp, String board, VBox box1, VBox box2, VBox box3, VBox box4) {
		Label label1 = (Label) box1.getChildren().get(0);
		Label label2 = (Label) box2.getChildren().get(0);
		Label label3 = (Label) box3.getChildren().get(0);
		Label label4 = (Label) box4.getChildren().get(0);

		label1.textProperty().unbind();
		label2.textProperty().unbind();
		label3.textProperty().unbind();
		label4.textProperty().unbind();

		if (temp.equalsIgnoreCase("sc") && boardChannelTemp.getBoardTemperatureMap().get(board) != null) {
			double maxValue = boardChannelTemp.getMaxValue();
			double minValue = boardChannelTemp.getMinValue();
			label1.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel1Temp());
			label2.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel2Temp());
			label3.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel3Temp());
			label4.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel4Temp());
			try {
				double value1 = Double.parseDouble(label1.getText());
				double value2 = Double.parseDouble(label2.getText());
				double value3 = Double.parseDouble(label3.getText());
				double value4 = Double.parseDouble(label4.getText());

				System.out.println("-- " + value1);
				System.out.println("-- " + value2);
				System.out.println("-- " + value3);
				System.out.println("-- " + value4);

				if ((value1 <= 0 && value1 < 1) || (value2 <= 0 && value2 < 1) || (value3 <= 0 && value3 < 1)
						|| (value4 <= 0 && value4 < 1)) {

					box1.getStyleClass().add("temp-box-off");
					box2.getStyleClass().add("temp-box-off");
					box3.getStyleClass().add("temp-box-off");
					box4.getStyleClass().add("temp-box-off");

				}

				if (value1 < minValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value1 > maxValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value2 < minValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value2 > maxValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value3 < minValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value3 > maxValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value4 < minValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value4 > maxValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

			} catch (NumberFormatException | NullPointerException e) {
				System.err.println("Invalid numeric value in labels: " + label1.getText() + ", " + "" + label2.getText()
						+ ", " + "" + label3.getText() + ", " + "" + label4.getText());
			}

		} else if (temp.equalsIgnoreCase("aec") && boardChannelTempAEC.getBoardTemperatureMap().get(board) != null) {
			double maxValue = boardChannelTempAEC.getMaxValue();
			double minValue = boardChannelTempAEC.getMinValue();
			label1.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel1Temp());
			label2.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel2Temp());
			label3.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel3Temp());
			label4.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel4Temp());

			try {
				double value1 = Double.parseDouble(label1.getText());
				double value2 = Double.parseDouble(label2.getText());
				double value3 = Double.parseDouble(label3.getText());
				double value4 = Double.parseDouble(label4.getText());

				if (value1 < minValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value1 > maxValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value2 < minValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value2 > maxValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value3 < minValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value3 > maxValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value4 < minValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value4 > maxValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}
			} catch (NumberFormatException | NullPointerException e) {
				System.err.println("Invalid numeric value in labels: " + label1.getText() + ", " + "" + label2.getText()
						+ ", " + "" + label3.getText() + ", " + "" + label4.getText());
			}

		}
		scBoardTemperatureMap = boardChannelTemp.getBoardTemperatureMap();
		aecBoardTemperatureMap = boardChannelTempAEC.getBoardTemperatureMap();

		if (scListener != null) {
			scBoardTemperatureMap.removeListener(scListener);
		}
		if (aecListener != null) {
			aecBoardTemperatureMap.removeListener(aecListener);
		}

		if (temp.equalsIgnoreCase("sc")) {
			scListener = change -> {
				if (board.equals(change.getKey())) {
					Platform.runLater(() -> {
						setMk1AandMk2Temp(change.getValueAdded(), box1, box2, box3, box4);
					});
				}
			};
			scBoardTemperatureMap.addListener(scListener);
		} else if (temp.equalsIgnoreCase("aec")) {
			aecListener = change -> {
				if (board.equals(change.getKey())) {
					Platform.runLater(() -> {
						setMk1AandMk2Temp(change.getValueAdded(), box1, box2, box3, box4);
					});
				}
			};
			aecBoardTemperatureMap.addListener(aecListener);
		}
	}

	private void setMk1AandMk2Temp(ChannelTemperature valueAdded, VBox box1, VBox box2, VBox box3, VBox box4) {
		Label label1 = (Label) box1.getChildren().get(0);
		Label label2 = (Label) box2.getChildren().get(0);
		Label label3 = (Label) box3.getChildren().get(0);
		Label label4 = (Label) box4.getChildren().get(0);
		double maxValue = boardChannelTemp.getMaxValue();
		double minValue = boardChannelTemp.getMinValue();
		label1.textProperty().bind(valueAdded.channel1TempProperty());
		label2.textProperty().bind(valueAdded.channel2TempProperty());
		label3.textProperty().bind(valueAdded.channel3TempProperty());
		label4.textProperty().bind(valueAdded.channel4TempProperty());

		try {
			double value1 = Double.parseDouble(label1.getText());
			double value2 = Double.parseDouble(label2.getText());
			double value3 = Double.parseDouble(label3.getText());
			double value4 = Double.parseDouble(label4.getText());

			if ((value1 <= 0 && value1 < 1) || (value2 <= 0 && value2 < 1) || (value3 <= 0 && value3 < 1)
					|| (value4 <= 0 && value4 < 1)) {

				box1.getStyleClass().add("temp-box-off");
				box2.getStyleClass().add("temp-box-off");
				box3.getStyleClass().add("temp-box-off");
				box4.getStyleClass().add("temp-box-off");

			}

			if (value1 < minValue) {
				box1.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));
			} else if (value1 > maxValue) {
				box1.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box1.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

			if (value2 < minValue) {
				box2.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));
			} else if (value2 > maxValue) {
				box2.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box2.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

			if (value3 < minValue) {
				box3.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));
			} else if (value3 > maxValue) {
				box3.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box3.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

			if (value4 < minValue) {
				box4.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));
			} else if (value4 > maxValue) {
				box4.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box4.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

		} catch (NumberFormatException | NullPointerException e) {
			System.err.println("Invalid numeric value in labels: " + label1.getText() + ", " + "" + label2.getText()
					+ ", " + "" + label3.getText() + ", " + "" + label4.getText());
		}

	}
	
	
//Before Suji Change
//	private void setMK1Temp(String selectedItem, VBox box1, VBox box2, VBox box3, VBox box4) {
//		Label label1 = (Label) box1.getChildren().get(0);
//		Label label2 = (Label) box2.getChildren().get(0);
//		Label label3 = (Label) box3.getChildren().get(0);
//		Label label4 = (Label) box4.getChildren().get(0);
//		
//
//		if (selectedItem.equalsIgnoreCase("sc")) {
//			label1.textProperty().bind(channelSCTemp.channel1TemperatureProperty());
//			label2.textProperty().bind(channelSCTemp.channel2TemperatureProperty());
//			label3.textProperty().bind(channelSCTemp.channel3TemperatureProperty());
//			label4.textProperty().bind(channelSCTemp.channel4TemperatureProperty());
//		} else {
//			label1.textProperty().bind(channelAECTemp.channel1TemperatureProperty());
//			label2.textProperty().bind(channelAECTemp.channel2TemperatureProperty());
//			label3.textProperty().bind(channelAECTemp.channel3TemperatureProperty());
//			label4.textProperty().bind(channelAECTemp.channel4TemperatureProperty());
//		}
//		
//
//		
//
//		UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
//		UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
//				currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
//				StateMachine.getCurrentUserLogin(), new Date(),
//				selectedItem + " temperature fetched [" + "CH1: " + label1.getText() + ", CH2: " + label2.getText()
//						+ ", CH3: " + label3.getText() + ", CH4: " + label4.getText() + "]");
//		uutLogbookManagement.addUUTLogBook(uutLogBookDto);
//	}
	
//	after Suji Change

	private void setMK1Temp(String selectedItem, VBox box1, VBox box2, VBox box3, VBox box4) {
        Label label1 = (Label) box1.getChildren().get(0);
        Label label2 = (Label) box2.getChildren().get(0);
        Label label3 = (Label) box3.getChildren().get(0);
        Label label4 = (Label) box4.getChildren().get(0);

        box1.setStyle("-fx-background-color: #f2e82e;");
        box2.setStyle("-fx-background-color: #f2e82e;");
        box3.setStyle("-fx-background-color: #f2e82e;");
        box4.setStyle("-fx-background-color: #f2e82e;");
        
        Platform.runLater(() -> {
        	System.out.println("Entred SC Runlater Method Out");
        channelSCTemp.channel1TemperatureProperty().addListener((obs, oldVal, newVal) -> label1.setText(newVal));
        channelSCTemp.channel2TemperatureProperty().addListener((obs, oldVal, newVal) -> label2.setText(newVal));
        channelSCTemp.channel3TemperatureProperty().addListener((obs, oldVal, newVal) -> label3.setText(newVal));
        channelSCTemp.channel4TemperatureProperty().addListener((obs, oldVal, newVal) -> label4.setText(newVal));
        });
        
        Platform.runLater(() -> {
        	System.out.println("Entred AEC Runlater Method out");
        channelAECTemp.channel1TemperatureProperty().addListener((obs, oldVal, newVal) -> label1.setText(newVal));
        channelAECTemp.channel2TemperatureProperty().addListener((obs, oldVal, newVal) -> label2.setText(newVal));
        channelAECTemp.channel3TemperatureProperty().addListener((obs, oldVal, newVal) -> label3.setText(newVal));
        channelAECTemp.channel4TemperatureProperty().addListener((obs, oldVal, newVal) -> label4.setText(newVal));
    	});
        if (selectedItem.equalsIgnoreCase("sc")) {

            label1.textProperty().bind(channelSCTemp.channel1TemperatureProperty());
            box1.styleProperty().bind(
                Bindings.createStringBinding(() -> "-fx-background-color: " + channelSCTemp.channel1BackgroundColorProperty().get(),
                                            channelSCTemp.channel1BackgroundColorProperty())
            );
            label2.textProperty().bind(channelSCTemp.channel2TemperatureProperty());
            box2.styleProperty().bind(
                Bindings.createStringBinding(() -> "-fx-background-color: " + channelSCTemp.channel2BackgroundColorProperty().get(),
                                            channelSCTemp.channel2BackgroundColorProperty())
            );
            label3.textProperty().bind(channelSCTemp.channel3TemperatureProperty());
            box3.styleProperty().bind(
                Bindings.createStringBinding(() -> "-fx-background-color: " + channelSCTemp.channel3BackgroundColorProperty().get(),
                                            channelSCTemp.channel3BackgroundColorProperty())
            );
            label4.textProperty().bind(channelSCTemp.channel4TemperatureProperty());
            box4.styleProperty().bind(
                Bindings.createStringBinding(() -> "-fx-background-color: " + channelSCTemp.channel4BackgroundColorProperty().get(),
                                            channelSCTemp.channel4BackgroundColorProperty())
            );
            
            Platform.runLater(() -> {
            	System.out.println("Entred SC Runlater Method");
            channelSCTemp.channel1TemperatureProperty().addListener((obs, oldVal, newVal) -> label1.setText(newVal));
            channelSCTemp.channel2TemperatureProperty().addListener((obs, oldVal, newVal) -> label2.setText(newVal));
            channelSCTemp.channel3TemperatureProperty().addListener((obs, oldVal, newVal) -> label3.setText(newVal));
            channelSCTemp.channel4TemperatureProperty().addListener((obs, oldVal, newVal) -> label4.setText(newVal));
            });
            
            
        } else {
            label1.textProperty().bind(channelAECTemp.channel1TemperatureProperty());
            // Bind the background color of the VBox (instead of the Label)
            box1.styleProperty().bind(
                Bindings.createStringBinding(() -> "-fx-background-color: " + channelAECTemp.channel1BackgroundColorProperty().get(),
                                             channelAECTemp.channel1BackgroundColorProperty())
            );
            label2.textProperty().bind(channelAECTemp.channel2TemperatureProperty());
            box2.styleProperty().bind(
                    Bindings.createStringBinding(() -> "-fx-background-color: " + channelAECTemp.channel2BackgroundColorProperty().get(),
                                                channelAECTemp.channel2BackgroundColorProperty())
                );
            label3.textProperty().bind(channelAECTemp.channel3TemperatureProperty());
            box3.styleProperty().bind(
                    Bindings.createStringBinding(() -> "-fx-background-color: " + channelAECTemp.channel3BackgroundColorProperty().get(),
                                                channelAECTemp.channel3BackgroundColorProperty())
                );
            label4.textProperty().bind(channelAECTemp.channel4TemperatureProperty());
            box4.styleProperty().bind(
                    Bindings.createStringBinding(() -> "-fx-background-color: " + channelAECTemp.channel4BackgroundColorProperty().get(),
                                                channelAECTemp.channel4BackgroundColorProperty())
                );
            Platform.runLater(() -> {
            	System.out.println("Entred AEC Runlater Method");
            channelAECTemp.channel1TemperatureProperty().addListener((obs, oldVal, newVal) -> label1.setText(newVal));
            channelAECTemp.channel2TemperatureProperty().addListener((obs, oldVal, newVal) -> label2.setText(newVal));
            channelAECTemp.channel3TemperatureProperty().addListener((obs, oldVal, newVal) -> label3.setText(newVal));
            channelAECTemp.channel4TemperatureProperty().addListener((obs, oldVal, newVal) -> label4.setText(newVal));
        	});
        }
        
      
        
        
        UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
        UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
                currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
                StateMachine.getCurrentUserLogin(), new Date(),
                selectedItem + " temperature fetched [" + "CH1: " + label1.getText() + ", CH2: " + label2.getText()
                        + ", CH3: " + label3.getText() + ", CH4: " + label4.getText() + "]");
        uutLogbookManagement.addUUTLogBook(uutLogBookDto);
    }
	

	private VBox createBottomRightMidThird() {
		VBox bottomRightMidThirdBox = new VBox();
		bottomRightMidThirdBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("DFCC power on status");
		titleLabel.getStyleClass().add("right-common-title");

		GridPane bottomRightMidThirdGridPane = new GridPane();
		bottomRightMidThirdGridPane.setVgap(5);
		bottomRightMidThirdGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(50);

		bottomRightMidThirdGridPane.getColumnConstraints().addAll(firstColumn, thirdColumn);
		bottomRightMidThirdGridPane.getRowConstraints().addAll(firstRow, thirdRow);

		VBox box1 = new VBox();
		box1.setAlignment(Pos.CENTER);
		Label label1 = new Label("CH-1");
		box1.getChildren().add(label1);
		box1.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("CH-2");
		box2.getChildren().add(label2);
		box2.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("CH-3");
		box3.getChildren().add(label3);
		box3.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("CH-4");
		box4.getChildren().add(label4);
		box4.getStyleClass().addAll("power-status-box", "power-status-box-off");

		powerOnStatus.channel1StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box1.getStyleClass().remove("power-status-box-off");
				box1.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box1.getStyleClass().remove("power-status-box-on");
				box1.getStyleClass().add("power-status-box-off");
			}
		});

		powerOnStatus.channel2StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box2.getStyleClass().remove("power-status-box-off");
				box2.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box2.getStyleClass().remove("power-status-box-on");
				box2.getStyleClass().add("power-status-box-off");
			}
		});

		powerOnStatus.channel3StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box3.getStyleClass().remove("power-status-box-off");
				box3.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box3.getStyleClass().remove("power-status-box-on");
				box3.getStyleClass().add("power-status-box-off");
			}
		});

		powerOnStatus.channel4StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box4.getStyleClass().remove("power-status-box-off");
				box4.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box4.getStyleClass().remove("power-status-box-on");
				box4.getStyleClass().add("power-status-box-off");
			}
		});

		bottomRightMidThirdGridPane.add(box1, 0, 0);
		bottomRightMidThirdGridPane.add(box2, 1, 0);
		bottomRightMidThirdGridPane.add(box3, 0, 1);
		bottomRightMidThirdGridPane.add(box4, 1, 1);

		bottomRightMidThirdBox.getChildren().addAll(titleLabel, bottomRightMidThirdGridPane);

		return bottomRightMidThirdBox;
	}

	private VBox createBottomRightMidFourth() {
		VBox bottomRightMidFourthBox = new VBox();
		bottomRightMidFourthBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("Online status");
		titleLabel.getStyleClass().add("right-common-title");

		GridPane bottomRightMidFourthGridPane = new GridPane();
		bottomRightMidFourthGridPane.setVgap(5);
		bottomRightMidFourthGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);

		bottomRightMidFourthGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		bottomRightMidFourthGridPane.getRowConstraints().addAll(firstRow, secondRow);

		VBox box1 = new VBox();
		box1.setAlignment(Pos.CENTER);
		Label label1 = new Label("CH-1");
		box1.getChildren().add(label1);
		box1.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("CH-2");
		box2.getChildren().add(label2);
		box2.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("CH-3");
		box3.getChildren().add(label3);
		box3.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("CH-4");
		box4.getChildren().add(label4);
		box4.getStyleClass().addAll("power-status-box", "power-status-box-off");

		OnlineStatus.channel1StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box1.getStyleClass().remove("power-status-box-off");
				box1.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box1.getStyleClass().remove("power-status-box-on");
				box1.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel2StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box2.getStyleClass().remove("power-status-box-off");
				box2.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box2.getStyleClass().remove("power-status-box-on");
				box2.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel3StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box3.getStyleClass().remove("power-status-box-off");
				box3.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box3.getStyleClass().remove("power-status-box-on");
				box3.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel4StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box4.getStyleClass().remove("power-status-box-off");
				box4.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box4.getStyleClass().remove("power-status-box-on");
				box4.getStyleClass().add("power-status-box-off");
			}
		});

		bottomRightMidFourthGridPane.add(box1, 0, 0);
		bottomRightMidFourthGridPane.add(box2, 1, 0);
		bottomRightMidFourthGridPane.add(box3, 0, 1);
		bottomRightMidFourthGridPane.add(box4, 1, 1);

		bottomRightMidFourthBox.getChildren().addAll(titleLabel, bottomRightMidFourthGridPane);

		return bottomRightMidFourthBox;
	}

	private void CheckToggleStatus() {

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				while (true) {
					Platform.runLater(() -> {
						System.out.println("Entred online");
						OnlineStatus.setChannel1Status("online");
						OnlineStatus.setChannel2Status("online");
						OnlineStatus.setChannel3Status("online");
						OnlineStatus.setChannel4Status("online");
						//checkChannelOnlineStatus();
						
						System.out.println("Test State thread" +StateMachine.getTestState() );
					});

					Thread.sleep(5000);

					Platform.runLater(() -> {
						System.out.println("Entred offline");
						OnlineStatus.setChannel1Status("offline");
						OnlineStatus.setChannel2Status("offline");
						OnlineStatus.setChannel3Status("offline");
						OnlineStatus.setChannel4Status("offline");
						//checkChannelOnlineStatus();
					});
					Thread.sleep(5000);
				}
			}
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();

	}

	private void checkChannelOnlineStatus() {
	    TranslateTransition transition = new TranslateTransition(Duration.millis(200), toggleButton);

	    boolean allChannelsOnline = OnlineStatus.getChannel1Status().equalsIgnoreCase("online")
	            && OnlineStatus.getChannel2Status().equalsIgnoreCase("online")
	            && OnlineStatus.getChannel3Status().equalsIgnoreCase("online")
	            && OnlineStatus.getChannel4Status().equalsIgnoreCase("online");

//	    if (allChannelsOnline) {
//	        if (!isOn) { 
//	            transition.setToX(26);
//	            background.setFill(Color.GREEN);
//	            toggleLabel.setText("ON");
//	            StackPane.setAlignment(toggleLabel, Pos.CENTER_LEFT);
//	            isOn = true; 
//	        }
//	    } else {
//	        transition.setToX(-26);
//	        background.setFill(Color.RED);
//	        toggleLabel.setText("OFF");
//	        StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
//	        isOn = false;
//	    }
//	    System.out.println("IS ON " +isOn );
//	    transition.play();
	}


	private GridPane createRigthMidSecond() {
		GridPane rightMidSecondGridPane = new GridPane();
		rightMidSecondGridPane.setVgap(5);
		rightMidSecondGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(25);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(25);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(25);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(25);

		rightMidSecondGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		rightMidSecondGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);

		int i = 0;
		String[] labels = { "INIT DFCC", "Power Reset", "Erase EEPROM", "Cycle Power", "Check OFP", "Check WDM",
				"Check OFP 2", "Check WDM 2" };

		macroButtonList = macroConfigurationManagement.getAllMacroButtonsByUutId(currentSessionDetails.getUutId());

		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 2; col++) {
				VBox box = new VBox();
				box.setAlignment(Pos.CENTER);

				Tooltip tooltip = new Tooltip(macroButtonList.get(i).getButtonName());
				Tooltip.install(box, tooltip);
				tooltip.setShowDelay(Duration.ZERO);
				tooltip.setHideDelay(Duration.ZERO);

				final int x = i;
				Label label = new Label(macroButtonList.get(i).getButtonName());
				label.setUserData(macroButtonList.get(i).getCommand());

				box.setOnMouseClicked(e -> {
					if (!label.getUserData().toString().equals("<NOT SET>")) {
						if (!checkAitessStatus.isBothAitessOn()) {
							return;
						}
						ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
						ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
								currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
								currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
								"clicked on " + macroButtonList.get(x).getButtonName() + " macro button");
						appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
						UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
						UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
								currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
								StateMachine.getCurrentUserLogin(), new Date(),
								"macro command " + label.getUserData().toString() + " executed");
						uutLogbookManagement.addUUTLogBook(uutLogBookDto);

						aitessProcessControlManagement.WriteMacroCommandToAitess2(label.getUserData().toString());
					} else {
						ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
						ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
								currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
								currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
								"clicked on " + macroButtonList.get(x).getButtonName() + " macro button");
						appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
						Notifications.showErrorAlert("Macro Button Not Configured");
						UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
						UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
								currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
								StateMachine.getCurrentUserLogin(), new Date(),
								"no macro command found for " + macroButtonList.get(x).getButtonName());
						uutLogbookManagement.addUUTLogBook(uutLogBookDto);
					}
				});

				box.getChildren().add(label);
				box.getStyleClass().add("third-conatiner-button");
				rightMidSecondGridPane.add(box, col, row);
				i++;
			}
		}

		return rightMidSecondGridPane;
	}

	private GridPane createRightBottom() {
		GridPane rightBottomGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		rightBottomGridPane.getColumnConstraints().addAll(firstColumn);
		rightBottomGridPane.getRowConstraints().addAll(firstRow);

		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		Label label1 = new Label("Check sum Info");
		label1.setCursor(Cursor.HAND);
		box.getChildren().add(label1);

		box.getStyleClass().add("bottom-conatiner-button");

		label1.setOnMouseClicked(event -> {
			CheckSumController checkSumController = new CheckSumController();
			checkSumController.createCheckSumDataPopup();
			
			GridPane checkSumData = checkSumController.createCheckSumDataPopup();
			bottomMainGridPane.add(checkSumData, 0, 0, 3, 1);
		});

		rightBottomGridPane.add(box, 0, 0);

		return rightBottomGridPane;
	}

	private GridPane createBottomMidGridPane() {
		bottomGridPane.setVgap(10);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(93);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);

		bottomGridPane.getColumnConstraints().addAll(firstColumn);
		bottomGridPane.getRowConstraints().addAll(firstRow, secondRow);

		Button showTerminalButton = new Button("SHOW TERMINAL");
		showTerminalButton.setMaxWidth(Double.MAX_VALUE);
		showTerminalButton.setMaxHeight(Double.MAX_VALUE);
		showTerminalButton.getStyleClass().add("show-terminal-button");

		showTerminalButton.setOnAction(e -> {
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on SHOW TERMINAL button");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			centerContentController.createUserCenterContent(bottomMidTopGridPane, "Show Terminal", null, null);
		});

		bottomGridPane.add(createBottomMidContentArea(), 0, 0);
		bottomGridPane.add(createTerminalContainer(), 0, 1);

		return bottomGridPane;
	}

	private GridPane createTerminalContainer() {
		terminalGridPane.getStyleClass().add("terminal-bottom-container");
		terminaTextField.getStyleClass().add("terminal-input");
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
		terminalGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fifthColumn);
		terminalGridPane.getRowConstraints().addAll(firstRow);

		terminaTextField.setDisable(true);
		enterButton.setDisable(true);
		yesButton.setDisable(true);
		noButton.setDisable(true);

		Image minImage = new Image(DFCCConstant.JARSTRING + "/Resources/Images/up-arrow1.png");
		ImageView minImageView = new ImageView(minImage);
		minImageView.getStyleClass().add("terminal-image");

		minImageView.setFitWidth(50);
		minImageView.setFitHeight(50);

		minImageView.setOnMouseClicked((MouseEvent event) -> {
			handleOpenTerminal();
		});

		StateMachine.userActionFlagProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				handleOpenTerminal();
				StateMachine.getUserActionFlag().set(false);
			}
		});

		terminalGridPane.add(terminaTextField, 0, 0);
		terminalGridPane.add(enterButton, 1, 0);
		terminalGridPane.add(yesButton, 2, 0);
		terminalGridPane.add(noButton, 3, 0);
		terminalGridPane.add(minImageView, 4, 0);

		return terminalGridPane;
	}

	private void handleOpenTerminal() {
		bottomMainGridPane.setOpacity(0.5);

		StackPane parentStackPane = (StackPane) bottomMainGridPane.getParent();
		if (!parentStackPane.getChildren().contains(terminalStackPane)) {
			parentStackPane.getChildren().add(terminalStackPane);
		}

		Platform.runLater(() -> {
			animateNewStackPaneToFront();
		});
	}

//	public void animateNewStackPaneToFront() {
//	    terminalStackPane.toFront();
//	    
//	    FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), terminalStackPane);
//	    fadeTransition.setFromValue(0); 
//	    fadeTransition.setToValue(1); 
//	    
//	    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500), terminalStackPane);
//	    translateTransition.setFromY(terminalStackPane.getHeight());
//	    translateTransition.setToY(0);
//	    
//	    ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, translateTransition);
//	    
//	    parallelTransition.play();
//	}

//	public void animateNewStackPaneToFront() {
//	    terminalStackPane.toFront();
//	    GridPane newGridPane = (GridPane) terminalStackPane.getChildren().get(0);
//	    GridPane newGridPane1 = (GridPane) newGridPane.getChildren().get(0);
//	    GridPane newGridPane2 = (GridPane) newGridPane1.getChildren().get(0);
//	    
////	    FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), newGridPane2);
////	    fadeTransition.setFromValue(0); 
////	    fadeTransition.setToValue(1); 
////	    fadeTransition.setInterpolator(Interpolator.EASE_IN);
////	    
////	    fadeTransition.play();
//	    
////	    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), newGridPane2);
////	    translateTransition.setFromY(newGridPane2.getHeight());
////	    translateTransition.setToY(0);
////	    
////	    translateTransition.play();
//	    	    
//	}

	public void animateNewStackPaneToFront() {
		terminalStackPane.toFront();
		GridPane newGridPane = (GridPane) terminalStackPane.getChildren().get(0);
		GridPane newGridPane1 = (GridPane) newGridPane.getChildren().get(0);
		GridPane newGridPane2 = (GridPane) newGridPane1.getChildren().get(0);

		newGridPane2.setScaleY(0.0);

		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), newGridPane2);
		scaleTransition.setFromY(0.0);
		scaleTransition.setToY(1.0);

		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), newGridPane2);
		translateTransition.setFromY(newGridPane2.getHeight() / 2);
		translateTransition.setToY(0);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), newGridPane2);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);

		ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, translateTransition,
				fadeTransition);
		parallelTransition.play();
	}

	private GridPane createBottomMidContentArea() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomMidTopGridPane.getColumnConstraints().addAll(firstColumn);
		bottomMidTopGridPane.getRowConstraints().addAll(firstRow);

		bottomMidTopGridPane.getStyleClass().add("center-container");

		return bottomMidTopGridPane;

	}


	private void setupStatusColumn(TableColumn<CheckSumList, String> statusColumn) {
		statusColumn.setReorderable(false);
		statusColumn.setSortable(false);
		statusColumn.setCellFactory(column -> new TableCell<CheckSumList, String>() {
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
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					} else if ("No File".equalsIgnoreCase(item)) {
						setText("No File");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					} else if ("Extra File".equalsIgnoreCase(item)) {
						setText("Extra File");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}

	private TableView<CheckSumList> createTableView(ObservableList<CheckSumList> checkSumTableData) {
		TableView<CheckSumList> tableView = new TableView<>();
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tableView.setPrefHeight(900);

		TableColumn<CheckSumList, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> checkSumValueColumn = new TableColumn<>("CheckSum Value");
		checkSumValueColumn.setCellValueFactory(new PropertyValueFactory<>("checkSumValue"));
		checkSumValueColumn.setReorderable(false);
		checkSumValueColumn.setSortable(false);
		checkSumValueColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		setupStatusColumn(statusColumn);

		tableView.getColumns().addAll(fileNameColumn, checkSumValueColumn, statusColumn);
		tableView.setItems(checkSumTableData);

		return tableView;
	}

	private VBox createCheckSumDataBox(ObservableList<CheckSumList> checkSumTableData) {
		VBox checkSumDataBox = new VBox(10);
		checkSumDataBox.setAlignment(Pos.CENTER);
		checkSumDataBox.getStyleClass().add("check-sum-data-box");
		checkSumDataBox.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		HBox checkSumTitleBox = new HBox();
		checkSumTitleBox.setAlignment(Pos.CENTER);
		Label checkSumLabel = new Label("CheckSum Details");
		checkSumLabel.getStyleClass().add("check-sum-title");
		checkSumTitleBox.getChildren().add(checkSumLabel);

		TableView<CheckSumList> tableView = createTableView(checkSumTableData);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		Button okButton = new Button("OK");
		okButton.getStyleClass().add("check-sum-ok-btn");
		buttonBox.getChildren().add(okButton);

		okButton.setOnAction(e -> {
			for (CheckSumList checksum : checkSumTableData) {
				if (!"ok".equalsIgnoreCase(checksum.getStatus())) {
					checkSumFinalResult = false;
				}
			}
			if (checkSumFinalResult) {
				Parent parent = checkSumDataBox.getParent();
				if (parent instanceof GridPane) {
					GridPane parent1 = (GridPane) parent.getParent();
					parent1.getChildren().remove(parent);
				}
			} else {
				Platform.exit();
			}
		});

		checkSumDataBox.getChildren().addAll(checkSumTitleBox, tableView, buttonBox);

		return checkSumDataBox;
	}



	public GridPane getCheckSumDataUserLogin() {

		if (DFCCConstant.isJarBuild) {

			ValidateResponse response = checksumManagement.compare();

			ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

			if (response.getResponse().getResponseCode() == 0) {
				Notifications.showErrorAlert(response.getResponse().getResponseMessage());
				Platform.exit();
			} else if (response.getResponse().getResponseCode() == 1) {
				for (CheckSum data : response.getCheckSumList()) {
					CheckSumList checkSumUiDto = new CheckSumList();
					checkSumUiDto.setFileName(data.getFile());
					checkSumUiDto.setCheckSumValue(data.getChecksumValue());
					checkSumUiDto.setStatus(data.getMsg());
					checkSumTableData.add(checkSumUiDto);
				}
			}

			GridPane newGridPane = new GridPane();
			newGridPane.setStyle(
					"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
			setupGridPane(newGridPane);

			VBox checkSumDataBox = createCheckSumDataBox(checkSumTableData);

			newGridPane.add(checkSumDataBox, 1, 1);

			return newGridPane;

		} else {
			ValidateResponse checkSumData = systemConfigManagement.validateConfig();
			List<CheckSum> checkSumDataList = checkSumData.getCheckSumList();
			ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

			if (checkSumData.getResponse().getResponseCode() == 0) {
				Notifications.showErrorAlert(checkSumData.getResponse().getResponseMessage());
				Platform.exit();
			} else if (checkSumData.getResponse().getResponseCode() == 1) {
				for (CheckSum data : checkSumDataList) {
					CheckSumList checkSumUiDto = new CheckSumList();
					checkSumUiDto.setFileName(data.getFile());
					checkSumUiDto.setCheckSumValue(data.getChecksumValue());
					checkSumUiDto.setStatus(data.getMsg());
					checkSumTableData.add(checkSumUiDto);
				}
			}

			GridPane newGridPane = new GridPane();
			newGridPane.setStyle(
					"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
			setupGridPane(newGridPane);

			VBox checkSumDataBox = createCheckSumDataBox(checkSumTableData);

			newGridPane.add(checkSumDataBox, 1, 1);

			return newGridPane;

		}

	}



private void setupGridPane(GridPane gridPane) {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(2);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(96);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(2);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(90);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(5);

		gridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		gridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
	}






class ToggleSwitch extends HBox {

	private final Label label = new Label();
	private final Button button = new Button();

	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);

	public SimpleBooleanProperty switchOnProperty() {
		return switchedOn;
	}

	private void init() {

		label.setText("OFF");

		getChildren().addAll(label, button);
		button.setOnAction((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		label.setOnMouseClicked((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		setStyle();
		bindProperties();
	}

	private void setStyle() {
		// Default Width
		setWidth(50);
		label.setAlignment(Pos.CENTER);
		label.setStyle("-fx-background-color: red; -fx-text-fill:black; -fx-background-radius: 10px;");
		setAlignment(Pos.CENTER_LEFT);
	}

	private void bindProperties() {
		label.prefWidthProperty().bind(widthProperty().divide(2));
		label.prefHeightProperty().bind(heightProperty());
		button.prefWidthProperty().bind(widthProperty().divide(2));
		button.prefHeightProperty().bind(heightProperty());
	}

	public ToggleSwitch() {
		init();
		switchedOn.addListener((a, b, c) -> {
			if (c) {
				label.setText("ON");
				setStyle("-fx-background-color: green;");
				label.toFront();
			} else {
				label.setText("OFF");
				setStyle("-fx-background-color: red;");
				button.toFront();
			}
		});
	}
}}