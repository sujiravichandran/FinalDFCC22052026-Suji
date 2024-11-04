package com.teclever.dfcc.Controller.ui;

import java.util.Date;
import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.MacroConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.LogOutFileCopyResponse;
import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.filemanagement.Aitess2ConfigManagement;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp;
import com.teclever.dfcc.stateMachine.StateMachine.channelAECTemp;
import com.teclever.dfcc.stateMachine.StateMachine.channelSCTemp;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import javafx.util.Duration;

public class UserDashboardController {

	private GridPane bottomMainGridPane = new GridPane();
	private GridPane bottomGridPane = new GridPane();
	private GridPane bottomMidTopGridPane = new GridPane();

	private ObservableMap<String, ChannelTemperature> boardTemperatureMap;
	private List<MacroButtonMapDto> macroButtonList;

	UserCenterContentController centerContentController = UserCenterContentController.getInstance();
	SessionManagement sessionManagement = new SessionManagement();
	MacroConfigurationManagement macroConfigurationManagement = new MacroConfigurationManagement();
	AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
	Aitess2ConfigManagement aitess2ConfigManagement = new Aitess2ConfigManagement();
	CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
	
	private GridPane terminalGridPane = new GridPane();
	private TextField terminaTextField = new TextField();
	private Button enterButton = new Button("ENTER");
	private Button yesButton = new Button("YES");
	private Button noButton = new Button("NO");
	private StackPane terminalStackPane;
	TerminalController terminalController1 = new TerminalController();
	
    public UserDashboardController() {
    	terminalStackPane = terminalController1.createTerminalStackPane();
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
					new String[] { "PQT Report", "ESS Report", "Datapack Report", "Upload" });

		} else if (UserData.getRoleId().equals("RL_ID_4")) {

			addTreeItemWithChildren(rootItem, "Testing",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/testing.png",
					new String[] { "Self Test", "SRU/LRU Test" });
			addTreeItemWithChildren(rootItem, "Results",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);
			addTreeItemWithChildren(rootItem, "Test Summary",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/advance_testing.png", null);
			addTreeItemWithChildren(rootItem, "Reports",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png", null);
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
			if (StateMachine.getTestState() == TestState.PENDING || StateMachine.getTestState() == TestState.STOPPED || StateMachine.getTestState() == TestState.COMPLETED) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						StateMachine.getCurrentUserLogin() + " logged out");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement
						.getInstance();

				Notifications.showConfirmationDialog("Logout Confirmation", "Are you sure you want to log out and close the application?", () -> {
		         SessionFileManagement session = new SessionFileManagement();
		         LogOutFileCopyResponse response = session.copyingFileWhileLogOut(StateMachine.currentSessionDetails.getSessionId());
		         if(response.getCode() == 1) {
		        	 aitessProcessControlManagement.endAllProcessOnLogout();
		        	 Platform.exit();
		        	 System.exit(0);
		         }else if(response.getCode() == 0) {
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
		         }else if(response.getCode() == 100) {		        	 
		        	 SessionTestStateObject.isLogoutFileCopyPopupOpenedProperty().addListener((observable, oldValue, newValue) ->{
		        		 if(!newValue) {
		        			 aitessProcessControlManagement.endAllProcessOnLogout();
		        			 Platform.exit();
		        			 System.exit(0);
		        		 }
		        	 });
		        	 SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(true);
		        	 SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
		         }
		        });
			} else if (StateMachine.getTestState() == TestState.PAUSED || StateMachine.getTestState() == TestState.RUNNING) {
				Notifications.showWarningAlert(
						"Please stop " + StateMachine.getRunningTestName() + " test before log out and close the application");
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
		newLabel.setPadding(new Insets(0,0,0,5));
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
		firstRow.setPercentHeight(20);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(28);

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
		GridPane bottomRightMidFirstGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(40);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);

		bottomRightMidFirstGridPane.getColumnConstraints().add(firstColumn);
		bottomRightMidFirstGridPane.getRowConstraints().addAll(firstRow, secondRow);

		VBox firstRowBox = new VBox(10);
		firstRowBox.setAlignment(Pos.CENTER_LEFT);
		firstRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
		firstRowBox.prefHeightProperty().bind(firstRow.prefHeightProperty());
		Label titleLabel = new Label("DFCC");
		titleLabel.getStyleClass().add("dfcc-title");
		firstRowBox.getChildren().add(titleLabel);

		VBox secondRowBox = new VBox(10);
		secondRowBox.setAlignment(Pos.CENTER_LEFT);
		secondRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
		secondRowBox.prefHeightProperty().bind(secondRow.prefHeightProperty());
		Label statusLabel = new Label("DFCC Power OFF");

		secondRowBox.setOnMouseClicked(e -> {
			if (!checkAitessStatus.isBothAitessOn()) {
				return;
			}
			if (statusLabel.getText().toLowerCase().contains("on")) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on DFCC power ON button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

				UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
				UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
						currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
						StateMachine.getCurrentUserLogin(), new Date(), "DFCC gets powered OFF");
				uutLogbookManagement.addUUTLogBook(uutLogBookDto);
				aitessProcessControlManagement.WriteDfccPowerOffCommandToAitess2();

				Platform.runLater(() -> {
					statusLabel.setText("DFCC Power OFF");
					dfccCheckStatus.getDfccPowerStatus().set(false);
				});

			} else if (statusLabel.getText().toLowerCase().contains("off")) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on DFCC power OFF button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

				UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
				UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
						currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
						StateMachine.getCurrentUserLogin(), new Date(), "DFCC gets powered ON");
				uutLogbookManagement.addUUTLogBook(uutLogBookDto);
				aitessProcessControlManagement.WriteDfccPowerOnCommandToAitess2();

				Platform.runLater(() -> {
					statusLabel.setText("DFCC Power ON");
					dfccCheckStatus.getDfccPowerStatus().set(true);
				});

			}
		});

		dfccCheckStatus.dfccPowerStatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				secondRowBox.getStyleClass().remove("dfcc-status-box-off");
				secondRowBox.getStyleClass().add("dfcc-status-box-on");
			} else {
				secondRowBox.getStyleClass().remove("dfcc-status-box-on");
				secondRowBox.getStyleClass().add("dfcc-status-box-off");
			}
		});
		secondRowBox.getStyleClass().addAll("dfcc-status-box", "dfcc-status-box-off");
		statusLabel.getStyleClass().add("dfcc-status");
		secondRowBox.getChildren().add(statusLabel);

		bottomRightMidFirstGridPane.add(firstRowBox, 0, 0);
		bottomRightMidFirstGridPane.add(secondRowBox, 0, 1);

		return bottomRightMidFirstGridPane;
	}

	private VBox createBottomRightMidSecond() {
		boardTemperatureMap = boardChannelTemp.getBoardTemperatureMap();

		VBox bottomRightMidSecondBox = new VBox(10);
		bottomRightMidSecondBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("Temperature");
		titleLabel.getStyleClass().add("right-common-title");

		ComboBox<String> temperatureComboBox = new ComboBox<String>();
		if (currentSessionDetails.getUutId().equals("UUT1")) {
			temperatureComboBox.getItems().addAll("SC", "AEC");
		}

		HBox temperatureTitleHBox = new HBox(5);
		HBox.setHgrow(temperatureComboBox, Priority.ALWAYS);
		temperatureComboBox.setMaxWidth(Double.MAX_VALUE);
		temperatureTitleHBox.getChildren().addAll(titleLabel, temperatureComboBox);

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
			if (newValue != null && currentSessionDetails.getUutId().equals("UUT1")) {
				if (!checkAitessStatus.isBothAitessOn()) {
					return;
				}
				Platform.runLater(() -> {
					ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
					ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
							currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
							currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
							"clicked on " + newValue + " temperature");
					appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

					setMK1Temp(newValue, box1, box2, box3, box4);
				});
			}
		});

		boardTemperatureMap.addListener((MapChangeListener<String, ChannelTemperature>) change -> {
			populateTemperatureComboBox(temperatureComboBox, change.getKey());
			if (temperatureComboBox.getValue() != null) {
				if (temperatureComboBox.getValue().equals(change.getKey())) {
					Platform.runLater(() -> {
						setMk1AandMk2Temp(change.getValueAdded(), box1, box2, box3, box4);
					});
				}
			}
		});

		bottomRightMidSecondBox.getChildren().addAll(temperatureTitleHBox, bottomRightMidSecondGridPane);

		return bottomRightMidSecondBox;

	}

	private void populateTemperatureComboBox(ComboBox<String> temperatureComboBox, String key) {
		if (!currentSessionDetails.getUutId().equals("UUT1")) {
			if (!temperatureComboBox.getItems().contains(key)) {
				Platform.runLater(() -> {
					temperatureComboBox.getItems().add(key);
				});
			}
		}
	}

	private void setMk1AandMk2Temp(ChannelTemperature valueAdded, VBox box1, VBox box2, VBox box3, VBox box4) {
		Label label1 = (Label) box1.getChildren().get(0);
		Label label2 = (Label) box2.getChildren().get(0);
		Label label3 = (Label) box3.getChildren().get(0);
		Label label4 = (Label) box4.getChildren().get(0);

		label1.textProperty().bind(valueAdded.channel1TempProperty());
		label2.textProperty().bind(valueAdded.channel2TempProperty());
		label3.textProperty().bind(valueAdded.channel3TempProperty());
		label4.textProperty().bind(valueAdded.channel4TempProperty());
	}


	private void setMK1Temp(String selectedItem, VBox box1, VBox box2, VBox box3, VBox box4) {
		Label label1 = (Label) box1.getChildren().get(0);
		Label label2 = (Label) box2.getChildren().get(0);
		Label label3 = (Label) box3.getChildren().get(0);
		Label label4 = (Label) box4.getChildren().get(0);

		if (selectedItem.equalsIgnoreCase("sc")) {
			label1.textProperty().bind(channelSCTemp.channel1TemperatureProperty());
			label2.textProperty().bind(channelSCTemp.channel2TemperatureProperty());
			label3.textProperty().bind(channelSCTemp.channel3TemperatureProperty());
			label4.textProperty().bind(channelSCTemp.channel4TemperatureProperty());
		} else {
			label1.textProperty().bind(channelAECTemp.channel1TemperatureProperty());
			label2.textProperty().bind(channelAECTemp.channel2TemperatureProperty());
			label3.textProperty().bind(channelAECTemp.channel3TemperatureProperty());
			label4.textProperty().bind(channelAECTemp.channel4TemperatureProperty());
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
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box1.getStyleClass().remove("power-status-box-on");
				box1.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel2StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box2.getStyleClass().remove("power-status-box-off");
				box2.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box2.getStyleClass().remove("power-status-box-on");
				box2.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel3StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box3.getStyleClass().remove("power-status-box-off");
				box3.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box3.getStyleClass().remove("power-status-box-on");
				box3.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel4StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box4.getStyleClass().remove("power-status-box-off");
				box4.getStyleClass().add("power-status-box-on");
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
		box.getChildren().add(label1);
		box.getStyleClass().add("bottom-conatiner-button");

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
		terminalGridPane.getColumnConstraints().addAll(firstColumn,secondColumn,thirdColumn,fourthColumn,fifthColumn);
		terminalGridPane.getRowConstraints().addAll(firstRow);
		
		terminaTextField.setDisable(true);
		enterButton.setDisable(true);
		yesButton.setDisable(true);
		noButton.setDisable(true);
		
		Image minImage = new Image(DFCCConstant.JARSTRING+"/Resources/Images/up-arrow1.png");
	    ImageView minImageView = new ImageView(minImage);
	    minImageView.getStyleClass().add("terminal-image");
	    
	    minImageView.setFitWidth(50);
	    minImageView.setFitHeight(50);
	    
	    minImageView.setOnMouseClicked((MouseEvent event) -> {
	        handleOpenTerminal();
	    });
	    
      StateMachine.userActionFlagProperty().addListener((observable, oldValue, newValue) ->{
	    	if(newValue) {
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
        
        animateNewStackPaneToFront();
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

	    ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, translateTransition, fadeTransition);
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
}
