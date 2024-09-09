package com.teclever.dfcc.Controller.ui;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.MacroConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.filemanagement.Aitess2ConfigManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp;
import com.teclever.dfcc.stateMachine.StateMachine.channelAECTemp;
import com.teclever.dfcc.stateMachine.StateMachine.channelSCTemp;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

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

	public GridPane createUserDashboard() {
		getAllStagesData();
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

	private void getAllStagesData() {
		SessionStageMapResponse data = sessionManagement
				.getAllSessionStageMapping(currentSessionDetails.getSessionId());
		if (data.getResponse().getResponseCode() == 1) {
			StateMachine.setStageDatalist(data.getListOfStageObject());
			getSessionTestData();
		} else {
			System.out.println("Error in getAllStagesData : " + data.getResponse().getResponseMessage());
		}
	}
	
	private void getSessionTestData() {
		List<StageObject> stageList = StateMachine.getStageDatalist();
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);

		observableStageList.stream().filter(stage -> {
			return !(stage.isDefaultStatus() || stage.isAdvanceStatus());
		}).forEach(stage -> {
			String l1StageId = stage.getL1StageId();
			SessionTestStateObject.addL1StageMap(l1StageId, stage);
			SessionTestStateObject.addL1MandatoryStatus(l1StageId, stage.isMandatoryStatus());
			SessionTestStateObject.addL1ContinueWithErrorStatus(l1StageId, stage.isContinueWithErrorStatus());
		});

		observableStageList.stream().forEach(stage -> {
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
					SessionTestStateObject.getEndLeafMap().put(l2StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l2StageId);
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
					SessionTestStateObject.getEndLeafMap().put(l3StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l3StageId);
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
					SessionTestStateObject.getEndLeafMap().put(l4StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l4StageId);
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
					SessionTestStateObject.getEndLeafMap().put(l5StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l5StageId);
				}
				SessionTestStateObject.addL5StageMap(l5StageId, l5StageObject);
			}
		});
	}

	private GridPane createBottomleftGridPane() {
		GridPane bottomLeftGridPane = new GridPane();
		bottomLeftGridPane.setVgap(10);

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(100);

		RowConstraints bottomLeftTopRow = new RowConstraints();
		bottomLeftTopRow.setPercentHeight(63);

		RowConstraints bottomLeftMidRow = new RowConstraints();
		bottomLeftMidRow.setPercentHeight(30);

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

//		menuTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue != null) {
//				Label selectedLabel = newValue.getValue();
//				System.out.println("Selected Label: " + selectedLabel.getText());
//
//				centerContentController.createUserCenterContent(bottomMidTopGridPane,selectedLabel.getText());
//				
//				if (!newValue.getChildren().isEmpty()) {
//					newValue.getChildren().forEach(subMenuItem -> {
//					});
//				}
//			}
//		});

		menuTreeView.setOnMouseClicked(event -> {
			TreeItem<Label> selectedItem = menuTreeView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				Label selectedLabel = selectedItem.getValue();
//		        System.out.println("Selected Label: " + selectedLabel.getText());

				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on " + selectedLabel.getText() + " menu");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

				if (selectedItem.getChildren().isEmpty()) {
					centerContentController.createUserCenterContent(bottomMidTopGridPane, selectedLabel.getText(), null, null);
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
					new String[] {"Current Execution" ,"Unit Results"  , "Session Results" , "Stage Results"});
			addTreeItemWithChildren(rootItem, "Data Analysis",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/advance_testing.png", null);
			addTreeItemWithChildren(rootItem, "Reports",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png",
//					new String[] { "Session Report", "Advanced Report", "UUT/Datapack" });
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

//		addTreeItemWithChildren(rootItem, "Dashboard", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/dashboard.png", null);
//		addTreeItemWithChildren(rootItem, "Testing", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/testing.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Results", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/results.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Advanced Data", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/advance_testing.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Reports", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/reports.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Self Test", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/self_test.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "LRU Testing", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/lru_test.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Test Summary", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/test_summary.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "History Reports", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/history_reports.png",
//				new String[] { "SubMenu1", "SubMenu2" });

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

		String[] labelsText = { "Configuration", "Utility", "End Session", "Close Session", "Log Book" };

		for (String labelText : labelsText) {
			Label label = new Label(labelText);
			label.prefWidthProperty().bind(middleMenuBox.widthProperty());
			label.getStyleClass().add("middle-menu-item");

			label.setOnMouseClicked(event -> {
				System.out.println("Label clicked: " + labelText);
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
			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), StateMachine.getCurrentUserLogin() + " logged out");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();

			aitessProcessControlManagement.endAllProcessOnLogout();
			Platform.exit();
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

		Label titleLabel = new Label("SC Card Status");
		titleLabel.getStyleClass().add("right-top-title");

		GridPane bottomRightTopGridPane = new GridPane();
		bottomRightTopGridPane.setVgap(5);
		bottomRightTopGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(50);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);

		bottomRightTopGridPane.getColumnConstraints().addAll(firstColumn);
		bottomRightTopGridPane.getRowConstraints().addAll(firstRow, secondRow);

		GridPane onlineGridPane = new GridPane();
		onlineGridPane.getStyleClass().add("online-card-box");
		ColumnConstraints onlineFirstColumn = new ColumnConstraints();
		onlineFirstColumn.setPercentWidth(80);
		ColumnConstraints onlineSecondColumn = new ColumnConstraints();
		onlineSecondColumn.setPercentWidth(20);
		RowConstraints onlineFirstRow = new RowConstraints();
		onlineFirstRow.setPercentHeight(100);

		onlineGridPane.getColumnConstraints().addAll(onlineFirstColumn, onlineSecondColumn);
		onlineGridPane.getRowConstraints().addAll(onlineFirstRow);

		Label onlineCard = new Label("Card Online");
		onlineCard.getStyleClass().add("online-card-name");

		Label onlineCardCount = new Label("10");
		onlineCardCount.getStyleClass().add("card-count");

		onlineGridPane.add(onlineCard, 0, 0);
		onlineGridPane.add(onlineCardCount, 1, 0);

		GridPane offlineGridPane = new GridPane();
		offlineGridPane.getStyleClass().add("offline-card-box");
		ColumnConstraints offlineFirstColumn = new ColumnConstraints();
		offlineFirstColumn.setPercentWidth(80);
		ColumnConstraints offlineSecondColumn = new ColumnConstraints();
		offlineSecondColumn.setPercentWidth(20);
		RowConstraints offlineFirstRow = new RowConstraints();
		offlineFirstRow.setPercentHeight(100);

		offlineGridPane.getColumnConstraints().addAll(offlineFirstColumn, offlineSecondColumn);
		offlineGridPane.getRowConstraints().addAll(offlineFirstRow);

		Label offlineCard = new Label("Card Offline");
		offlineCard.getStyleClass().add("offline-card-name");

		Label offlineCardCount = new Label("5");
		offlineCardCount.getStyleClass().add("card-count");

		offlineGridPane.add(offlineCard, 0, 0);
		offlineGridPane.add(offlineCardCount, 1, 0);

		bottomRightTopGridPane.add(onlineGridPane, 0, 0);
		bottomRightTopGridPane.add(offlineGridPane, 0, 1);

		bottomRightTopBox.getChildren().addAll(titleLabel, bottomRightTopGridPane);

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
			if(!checkAitessStatus.isBothAitessOn()) {
				return ;
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
//		else {
//			temperatureComboBox.getItems().addAll("Board-1", "Board-2", "Board-3", "Board-4");
//		}

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

//		updateMK1Temp();
//		updateMK1AandMK2Temp();

		temperatureComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && currentSessionDetails.getUutId().equals("UUT1")) {
				if(!checkAitessStatus.isBothAitessOn()) {
					return ;
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

//	private void updateMK1Temp() {
//		new Thread(() -> {
//			for (int i = 0; i < 10000; i++) {
//				try {
//					Random random = new Random();
//					Platform.runLater(() -> {
//						channelAECTemp.setChannel1Temperature(String.format("AE :%01d", random.nextInt(9) + 10));
//						channelAECTemp.setChannel2Temperature(String.format("AE :%01d", random.nextInt(9) + 10));
//						channelAECTemp.setChannel3Temperature(String.format("AE :%01d", random.nextInt(9) + 10));
//						channelAECTemp.setChannel4Temperature(String.format("AE :%01d", random.nextInt(9) + 10));
//						channelSCTemp.setChannel1Temperature(String.format("SC :%01d", random.nextInt(9) + 10));
//						channelSCTemp.setChannel2Temperature(String.format("SC :%01d", random.nextInt(9) + 10));
//						channelSCTemp.setChannel3Temperature(String.format("SC :%01d", random.nextInt(9) + 10));
//						channelSCTemp.setChannel4Temperature(String.format("SC :%01d", random.nextInt(9) + 10));
//					});
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}
//
//	private void updateMK1AandMK2Temp() {
//
//		new Thread(() -> {
//			for (int i = 0; i < 10000; i++) {
//				try {
//					Random random = new Random();
//					Platform.runLater(() -> {
//
//						ChannelTemperature temp1 = new ChannelTemperature(
//								String.format("b1 :%01d", random.nextInt(9) + 10),
//								String.format("b1 :%01d", random.nextInt(9) + 10),
//								String.format("b1 :%01d", random.nextInt(9) + 10),
//								String.format("b1 :%01d", random.nextInt(9) + 10));
//						ChannelTemperature temp2 = new ChannelTemperature(
//								String.format("b2 :%01d", random.nextInt(9) + 10),
//								String.format("b2 :%01d", random.nextInt(9) + 10),
//								String.format("b2 :%01d", random.nextInt(9) + 10),
//								String.format("b2 :%01d", random.nextInt(9) + 10));
//						ChannelTemperature temp3 = new ChannelTemperature(
//								String.format("b3 :%01d", random.nextInt(9) + 10),
//								String.format("b3 :%01d", random.nextInt(9) + 10),
//								String.format("b3 :%01d", random.nextInt(9) + 10),
//								String.format("b3 :%01d", random.nextInt(9) + 10));
//						ChannelTemperature temp4 = new ChannelTemperature(
//								String.format("b4 :%01d", random.nextInt(9) + 10),
//								String.format("b4 :%01d", random.nextInt(9) + 10),
//								String.format("b4 :%01d", random.nextInt(9) + 10),
//								String.format("b4 :%01d", random.nextInt(9) + 10));
//
//						boardChannelTemp.addBoardTemperatureMap("Board-1", temp1);
//						boardChannelTemp.addBoardTemperatureMap("Board-2", temp2);
//						boardChannelTemp.addBoardTemperatureMap("Board-3", temp3);
//						boardChannelTemp.addBoardTemperatureMap("Board-4", temp4);
//
//					});
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}

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
				final int x = i;
				Label label = new Label(macroButtonList.get(i).getButtonName());
				label.setUserData(macroButtonList.get(i).getCommand());

				box.setOnMouseClicked(e -> {
					if (!label.getUserData().toString().equals("<NOT SET>")) {
						if(!checkAitessStatus.isBothAitessOn()) {
							return ;
						}
						ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
						ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
								currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
								currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
								"clicked on " + macroButtonList.get(x).getButtonName() + " macro button");
						appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
//						Notifications.showSuccessAlert("Selected Macro Command : " + label.getUserData().toString());
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
		bottomGridPane.add(showTerminalButton, 0, 1);

		return bottomGridPane;
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


//package com.teclever.dfcc.Controller.ui;
//
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.control.Label;
//import javafx.scene.control.TreeItem;
//import javafx.scene.control.TreeView;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.RowConstraints;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//
//public class UserDashboardController {
//
//	private GridPane bottomMainGridPane = new GridPane();
//	private GridPane bottomGridPane = new GridPane();
//	private GridPane bottomMidTopGridPane = new GridPane();
//	private GridPane bottomMidTopDataGridPane = new GridPane();
//	
//	private StackPane bottomContentStackPane = new StackPane();
//
//	private final UserContentArea userContentArea = new UserContentArea();
//	
//	
//	public GridPane createDashboard() {
//		bottomMainGridPane.getStylesheets()
//				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/UserDashboard.css").toExternalForm());
//		bottomMainGridPane.setHgap(10);
//
//		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
//		bottomLeftColumn.setPercentWidth(17);
//		ColumnConstraints bottomMidColumn = new ColumnConstraints();
//		bottomMidColumn.setPercentWidth(66);
//		ColumnConstraints bottomRightColumn = new ColumnConstraints();
//		bottomRightColumn.setPercentWidth(17);
//
//		RowConstraints bottomRow = new RowConstraints();
//		bottomRow.setPercentHeight(100);
//
//		bottomMainGridPane.getColumnConstraints().addAll(bottomLeftColumn, bottomMidColumn, bottomRightColumn);
//		bottomMainGridPane.getRowConstraints().add(bottomRow);
//
//		bottomMainGridPane.add(createBottomleftGridPane(), 0, 0);
//		bottomMainGridPane.add(createBottomMidGridPane(), 1, 0);
//		bottomMainGridPane.add(createBottomRightGridPane(), 2, 0);
//
//		return bottomMainGridPane;
//
//	}
//
//	private GridPane createBottomleftGridPane() {
//		GridPane bottomLeftGridPane = new GridPane();
//		bottomLeftGridPane.setVgap(10);
//
//		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
//		bottomLeftColumn.setPercentWidth(100);
//
//		RowConstraints bottomLeftTopRow = new RowConstraints();
//		bottomLeftTopRow.setPercentHeight(63);
//
//		RowConstraints bottomLeftMidRow = new RowConstraints();
//		bottomLeftMidRow.setPercentHeight(30);
//
//		RowConstraints bottomLeftBottomRow = new RowConstraints();
//		bottomLeftBottomRow.setPercentHeight(7);
//
//		bottomLeftGridPane.getColumnConstraints().add(bottomLeftColumn);
//		bottomLeftGridPane.getRowConstraints().addAll(bottomLeftTopRow, bottomLeftMidRow, bottomLeftBottomRow);
//
//		bottomLeftGridPane.add(createMenuBox(), 0, 0);
//		bottomLeftGridPane.add(createSubMenuBox(), 0, 1);
//		bottomLeftGridPane.add(createButtonBox(), 0, 2);
//
//		return bottomLeftGridPane;
//	}
//
//	private TreeView<Label> createMenuBox() {
//		TreeItem<Label> rootItem = new TreeItem<>();
//		rootItem.setExpanded(true);
//		TreeView<Label> menuTreeView = new TreeView<>(rootItem);
//		menuTreeView.getStyleClass().add("menu-container");
//		menuTreeView.setShowRoot(false);
//
//		menuTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue != null) {
//				Label selectedLabel = newValue.getValue();
//				System.out.println("Selected Label: " + selectedLabel.getText());
//
//				createBottomMidTopGridPane(selectedLabel.getText());
//				if (!newValue.getChildren().isEmpty()) {
////             System.out.println("Submenu Items:");
//					newValue.getChildren().forEach(subMenuItem -> {
////          	   Label subMenuLabel = subMenuItem.getValue();
////          	   System.out.println(" -" + subMenuLabel.getText());
//					});
//				}
//			}
//		});
//
//		addTreeItemWithChildren(rootItem, "Dashboard", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/dashboard.png", null);
//		addTreeItemWithChildren(rootItem, "Testing", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/testing.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Results", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/results.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Advanced Data", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/advance_testing.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Reports", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/reports.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Self Test", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/self_test.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "LRU Testing", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/lru_test.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Test Summary", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/test_summary.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "History Reports", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/history_reports.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//
//		menuTreeView.setPadding(new Insets(5, 10, 5, 10));
//
//		return menuTreeView;
//	}
//
//	private void addTreeItemWithChildren(TreeItem<Label> parent, String text, String imagePath, String[] children) {
//		Image menuImage = new Image(imagePath);
//		Label newMenuItem = new Label(text);
//		ImageView menuImageView = new ImageView(menuImage);
//		menuImageView.setFitWidth(newMenuItem.getFont().getSize() + 30);
//		menuImageView.setFitHeight(newMenuItem.getFont().getSize() + 30);
//		menuImageView.getStyleClass().add("menu-image");
//
//		newMenuItem.setGraphic(menuImageView);
//		newMenuItem.getStyleClass().add("menu-item");
//
//		TreeItem<Label> menuItem = new TreeItem<>(newMenuItem);
//
//		if (children != null) {
//			for (String child : children) {
//				Label newSubMenuItem = new Label(child);
//				TreeItem<Label> childItem = new TreeItem<>(newSubMenuItem);
//				menuItem.getChildren().add(childItem);
//				newSubMenuItem.getStyleClass().add("sub-menu-item");
//			}
//		}
//		parent.getChildren().add(menuItem);
//	}
//
//	private VBox createSubMenuBox() {
//		VBox middleMenuBox = new VBox(3);
//		middleMenuBox.setAlignment(Pos.CENTER);
//		middleMenuBox.getStyleClass().add("middle-menu");
//
//		String[] labelsText = { "Configuration", "Utility", "End Session", "Close Session", "Log Book" };
//
//		for (String labelText : labelsText) {
//			Label label = new Label(labelText);
//			label.prefWidthProperty().bind(middleMenuBox.widthProperty());
//			label.getStyleClass().add("middle-menu-item");
//
//			label.setOnMouseClicked(event -> {
//				System.out.println("Label clicked: " + labelText);
//				for (Node node : middleMenuBox.getChildren()) {
//					if (node instanceof Label) {
//						((Label) node).getStyleClass().remove("selected");
//					}
//				}
//				label.getStyleClass().add("selected");
//			});
//
//			middleMenuBox.getChildren().add(label);
//		}
//		return middleMenuBox;
//	}
//
//	private GridPane createButtonBox() {
//		GridPane bottomButtonGridPane = new GridPane();
//		bottomButtonGridPane.setHgap(10);
//		bottomButtonGridPane.setVgap(10);
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		bottomButtonGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		bottomButtonGridPane.getRowConstraints().addAll(firstRow);
//
//		VBox logoutBox = new VBox();
//		logoutBox.setAlignment(Pos.CENTER);
//		Label logoutLabel = new Label("Logout");
//		logoutBox.getChildren().add(logoutLabel);
//		logoutBox.getStyleClass().add("logout-button");
//		logoutLabel.getStyleClass().add("logout-text");
//
//		VBox exitBox = new VBox();
//		exitBox.setAlignment(Pos.CENTER);
//		Label exitLabel = new Label("Exit");
//		exitBox.getChildren().add(exitLabel);
//		exitBox.getStyleClass().add("exit-button");
//		exitLabel.getStyleClass().add("exit-text");
//
//		bottomButtonGridPane.add(logoutBox, 0, 0);
//		bottomButtonGridPane.add(exitBox, 1, 0);
//
//		return bottomButtonGridPane;
//
//	}
//
//	private GridPane createBottomRightGridPane() {
//		GridPane bottomRightGridPane = new GridPane();
//		bottomRightGridPane.setVgap(10);
//
//		ColumnConstraints bottomRightColumn = new ColumnConstraints();
//		bottomRightColumn.setPercentWidth(100);
//
//		RowConstraints bottomRightTopRow = new RowConstraints();
//		bottomRightTopRow.setPercentHeight(15);
//
//		RowConstraints bottomRightMidRow = new RowConstraints();
//		bottomRightMidRow.setPercentHeight(60);
//
//		RowConstraints bottomRightBottomRow = new RowConstraints();
//		bottomRightBottomRow.setPercentHeight(28);
//
//		RowConstraints bottomRightBottomLastRow = new RowConstraints();
//		bottomRightBottomLastRow.setPercentHeight(7);
//
//		Pane newPane = new Pane();
//		newPane.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
//		newPane.prefHeightProperty().bind(bottomRightTopRow.prefHeightProperty());
//
//		Pane newPane1 = new Pane();
//		newPane1.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
//		newPane1.prefHeightProperty().bind(bottomRightMidRow.prefHeightProperty());
//
//		Pane newPane2 = new Pane();
//		newPane2.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
//		newPane2.prefHeightProperty().bind(bottomRightBottomRow.prefHeightProperty());
//
//		Pane newPane3 = new Pane();
//		newPane3.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
//		newPane3.prefHeightProperty().bind(bottomRightBottomLastRow.prefHeightProperty());
//
//		newPane.setStyle("-fx-background-color: black;");
//		newPane1.setStyle("-fx-background-color: white;");
//		newPane2.setStyle("-fx-background-color: green;");
//		newPane3.setStyle("-fx-background-color: pink;");
//
//		bottomRightGridPane.getColumnConstraints().add(bottomRightColumn);
//		bottomRightGridPane.getRowConstraints().addAll(bottomRightTopRow, bottomRightMidRow, bottomRightBottomRow,
//				bottomRightBottomLastRow);
//
//		bottomRightGridPane.add(createRightTop(), 0, 0);
//		bottomRightGridPane.add(createRightMidFirst(), 0, 1);
//		bottomRightGridPane.add(createRigthMidSecond(), 0, 2);
//		bottomRightGridPane.add(createRightBottom(), 0, 3);
//
//		return bottomRightGridPane;
//	}
//
//	private VBox createRightTop() {
//		VBox bottomRightTopBox = new VBox();
//		bottomRightTopBox.setAlignment(Pos.CENTER_LEFT);
//		bottomRightTopBox.getStyleClass().add("right-first-container");
//
//		Label titleLabel = new Label("SC Card Status");
//		titleLabel.getStyleClass().add("right-top-title");
//
//		GridPane bottomRightTopGridPane = new GridPane();
//		bottomRightTopGridPane.setVgap(5);
//		bottomRightTopGridPane.setHgap(5);
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(50);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(50);
//
//		bottomRightTopGridPane.getColumnConstraints().addAll(firstColumn);
//		bottomRightTopGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		GridPane onlineGridPane = new GridPane();
//		onlineGridPane.getStyleClass().add("online-card-box");
//		ColumnConstraints onlineFirstColumn = new ColumnConstraints();
//		onlineFirstColumn.setPercentWidth(80);
//		ColumnConstraints onlineSecondColumn = new ColumnConstraints();
//		onlineSecondColumn.setPercentWidth(20);
//		RowConstraints onlineFirstRow = new RowConstraints();
//		onlineFirstRow.setPercentHeight(100);
//
//		onlineGridPane.getColumnConstraints().addAll(onlineFirstColumn, onlineSecondColumn);
//		onlineGridPane.getRowConstraints().addAll(onlineFirstRow);
//
//		Label onlineCard = new Label("Card Online");
//		onlineCard.getStyleClass().add("online-card-name");
//
//		Label onlineCardCount = new Label("10");
//		onlineCardCount.getStyleClass().add("card-count");
//
//		onlineGridPane.add(onlineCard, 0, 0);
//		onlineGridPane.add(onlineCardCount, 1, 0);
//
//		GridPane offlineGridPane = new GridPane();
//		offlineGridPane.getStyleClass().add("offline-card-box");
//		ColumnConstraints offlineFirstColumn = new ColumnConstraints();
//		offlineFirstColumn.setPercentWidth(80);
//		ColumnConstraints offlineSecondColumn = new ColumnConstraints();
//		offlineSecondColumn.setPercentWidth(20);
//		RowConstraints offlineFirstRow = new RowConstraints();
//		offlineFirstRow.setPercentHeight(100);
//
//		offlineGridPane.getColumnConstraints().addAll(offlineFirstColumn, offlineSecondColumn);
//		offlineGridPane.getRowConstraints().addAll(offlineFirstRow);
//
//		Label offlineCard = new Label("Card Offline");
//		offlineCard.getStyleClass().add("offline-card-name");
//
//		Label offlineCardCount = new Label("5");
//		offlineCardCount.getStyleClass().add("card-count");
//
//		offlineGridPane.add(offlineCard, 0, 0);
//		offlineGridPane.add(offlineCardCount, 1, 0);
//
//		bottomRightTopGridPane.add(onlineGridPane, 0, 0);
//		bottomRightTopGridPane.add(offlineGridPane, 0, 1);
//
//		bottomRightTopBox.getChildren().addAll(titleLabel, bottomRightTopGridPane);
//
//		return bottomRightTopBox;
//	}
//
//	private Pane createRightMidFirst() {
//		Pane bottomRightMidPane = new Pane();
//
//		GridPane bottomRightMidGridPane = new GridPane();
//
//		bottomRightMidGridPane.getStyleClass().add("right-second-container");
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(22);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(26);
//
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(26);
//
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(26);
//
//		bottomRightMidGridPane.getColumnConstraints().add(firstColumn);
//		bottomRightMidGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);
//
//		bottomRightMidGridPane.add(createBottomRightMidFirst(), 0, 0);
//		bottomRightMidGridPane.add(createBottomRightMidSecond(), 0, 1);
//		bottomRightMidGridPane.add(createBottomRightMidThird(), 0, 2);
//		bottomRightMidGridPane.add(createBottomRightMidFourth(), 0, 3);
//
//		bottomRightMidPane.getChildren().add(bottomRightMidGridPane);
//		return bottomRightMidGridPane;
//	}
//
//	private GridPane createBottomRightMidFirst() {
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
//		Label statusLabel = new Label("DFCC Power ON");
//		secondRowBox.getStyleClass().add("dfcc-status-box");
//		statusLabel.getStyleClass().add("dfcc-status");
//		secondRowBox.getChildren().add(statusLabel);
//
//		bottomRightMidFirstGridPane.add(firstRowBox, 0, 0);
//		bottomRightMidFirstGridPane.add(secondRowBox, 0, 1);
//
//		return bottomRightMidFirstGridPane;
//	}
//
//	private VBox createBottomRightMidSecond() {
//		VBox bottomRightMidSecondBox = new VBox();
//		bottomRightMidSecondBox.setAlignment(Pos.CENTER_LEFT);
//
//		Label titleLabel = new Label("Temperature");
//		titleLabel.getStyleClass().add("right-common-title");
//
//		GridPane bottomRightMidSecondGridPane = new GridPane();
//		bottomRightMidSecondGridPane.setVgap(5);
//		bottomRightMidSecondGridPane.setHgap(5);
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(50);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(50);
//
//		bottomRightMidSecondGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		bottomRightMidSecondGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		VBox box1 = new VBox();
//		box1.setAlignment(Pos.CENTER);
//		Label label1 = new Label("36.5");
//		box1.getChildren().add(label1);
//		box1.getStyleClass().add("temp-box");
//
//		VBox box2 = new VBox();
//		box2.setAlignment(Pos.CENTER);
//		Label label2 = new Label("36.5");
//		box2.getChildren().add(label2);
//		box2.getStyleClass().add("temp-box");
//
//		VBox box3 = new VBox();
//		box3.setAlignment(Pos.CENTER);
//		Label label3 = new Label("36.5");
//		box3.getChildren().add(label3);
//		box3.getStyleClass().add("temp-box");
//
//		VBox box4 = new VBox();
//		box4.setAlignment(Pos.CENTER);
//		Label label4 = new Label("36.5");
//		box4.getChildren().add(label4);
//		box4.getStyleClass().add("temp-box");
//
//		bottomRightMidSecondGridPane.add(box1, 0, 0);
//		bottomRightMidSecondGridPane.add(box2, 1, 0);
//		bottomRightMidSecondGridPane.add(box3, 0, 1);
//		bottomRightMidSecondGridPane.add(box4, 1, 1);
//
//		bottomRightMidSecondBox.getChildren().addAll(titleLabel, bottomRightMidSecondGridPane);
//
//		return bottomRightMidSecondBox;
//
//	}
//
//	private VBox createBottomRightMidThird() {
//		VBox bottomRightMidThirdBox = new VBox();
//		bottomRightMidThirdBox.setAlignment(Pos.CENTER_LEFT);
//
//		Label titleLabel = new Label("DFCC power on status");
//		titleLabel.getStyleClass().add("right-common-title");
//
//		GridPane bottomRightMidThirdGridPane = new GridPane();
//		bottomRightMidThirdGridPane.setVgap(5);
//		bottomRightMidThirdGridPane.setHgap(5);
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints thirdColumn = new ColumnConstraints();
//		thirdColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(50);
//
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(50);
//
//		bottomRightMidThirdGridPane.getColumnConstraints().addAll(firstColumn, thirdColumn);
//		bottomRightMidThirdGridPane.getRowConstraints().addAll(firstRow, thirdRow);
//
//		VBox box1 = new VBox();
//		box1.setAlignment(Pos.CENTER);
//		Label label1 = new Label("CH-1");
//		box1.getChildren().add(label1);
//		box1.getStyleClass().add("power-status-box");
//
//		VBox box2 = new VBox();
//		box2.setAlignment(Pos.CENTER);
//		Label label2 = new Label("CH-2");
//		box2.getChildren().add(label2);
//		box2.getStyleClass().add("power-status-box");
//
//		VBox box3 = new VBox();
//		box3.setAlignment(Pos.CENTER);
//		Label label3 = new Label("CH-3");
//		box3.getChildren().add(label3);
//		box3.getStyleClass().add("power-status-box");
//
//		VBox box4 = new VBox();
//		box4.setAlignment(Pos.CENTER);
//		Label label4 = new Label("CH-4");
//		box4.getChildren().add(label4);
//		box4.getStyleClass().add("power-status-box");
//
//		bottomRightMidThirdGridPane.add(box1, 0, 0);
//		bottomRightMidThirdGridPane.add(box2, 1, 0);
//		bottomRightMidThirdGridPane.add(box3, 0, 1);
//		bottomRightMidThirdGridPane.add(box4, 1, 1);
//
//		bottomRightMidThirdBox.getChildren().addAll(titleLabel, bottomRightMidThirdGridPane);
//
//		return bottomRightMidThirdBox;
//	}
//
//	private VBox createBottomRightMidFourth() {
//		VBox bottomRightMidFourthBox = new VBox();
//		bottomRightMidFourthBox.setAlignment(Pos.CENTER_LEFT);
//
//		Label titleLabel = new Label("Online status");
//		titleLabel.getStyleClass().add("right-common-title");
//
//		GridPane bottomRightMidFourthGridPane = new GridPane();
//		bottomRightMidFourthGridPane.setVgap(5);
//		bottomRightMidFourthGridPane.setHgap(5);
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(50);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(50);
//
//		bottomRightMidFourthGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		bottomRightMidFourthGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		VBox box1 = new VBox();
//		box1.setAlignment(Pos.CENTER);
//		Label label1 = new Label("CH-1");
//		box1.getChildren().add(label1);
//		box1.getStyleClass().add("power-status-box");
//
//		VBox box2 = new VBox();
//		box2.setAlignment(Pos.CENTER);
//		Label label2 = new Label("CH-2");
//		box2.getChildren().add(label2);
//		box2.getStyleClass().add("power-status-box");
//
//		VBox box3 = new VBox();
//		box3.setAlignment(Pos.CENTER);
//		Label label3 = new Label("CH-3");
//		box3.getChildren().add(label3);
//		box3.getStyleClass().add("power-status-box");
//
//		VBox box4 = new VBox();
//		box4.setAlignment(Pos.CENTER);
//		Label label4 = new Label("CH-4");
//		box4.getChildren().add(label4);
//		box4.getStyleClass().add("power-status-box");
//
//		bottomRightMidFourthGridPane.add(box1, 0, 0);
//		bottomRightMidFourthGridPane.add(box2, 1, 0);
//		bottomRightMidFourthGridPane.add(box3, 0, 1);
//		bottomRightMidFourthGridPane.add(box4, 1, 1);
//
//		bottomRightMidFourthBox.getChildren().addAll(titleLabel, bottomRightMidFourthGridPane);
//
//		return bottomRightMidFourthBox;
//	}
//
//	private GridPane createRigthMidSecond() {
//		GridPane rightMidSecondGridPane = new GridPane();
//		rightMidSecondGridPane.setVgap(5);
//		rightMidSecondGridPane.setHgap(5);
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(25);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(25);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(25);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(25);
//
//		rightMidSecondGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		rightMidSecondGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);
//
//		int i = 0;
//		String[] labels = { "INIT DFCC", "Power Reset", "Erase EEPROM", "Cycle Power", "Check OFP", "Check WDM",
//				"Check OFP 2", "Check WDM 2" };
//
//		for (int row = 0; row < 4; row++) {
//			for (int col = 0; col < 2; col++) {
//				VBox box = new VBox();
//				box.setAlignment(Pos.CENTER);
//				Label label = new Label(labels[i]);
//				box.getChildren().add(label);
//				box.getStyleClass().add("third-conatiner-button");
//				rightMidSecondGridPane.add(box, col, row);
//				i++;
//			}
//		}
//
//		return rightMidSecondGridPane;
//	}
//
//	private GridPane createRightBottom() {
//		GridPane rightBottomGridPane = new GridPane();
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		rightBottomGridPane.getColumnConstraints().addAll(firstColumn);
//		rightBottomGridPane.getRowConstraints().addAll(firstRow);
//
//		VBox box = new VBox();
//		box.setAlignment(Pos.CENTER);
//		Label label1 = new Label("Check sum Info");
//		box.getChildren().add(label1);
//		box.getStyleClass().add("bottom-conatiner-button");
//
//		rightBottomGridPane.add(box, 0, 0);
//
//		return rightBottomGridPane;
//	}
//
//	
//	
//	
//
//
//	private GridPane createBottomMidGridPane() {
//		bottomGridPane.setVgap(10);
//		
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(93);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(7);
//		
//		bottomGridPane.getColumnConstraints().addAll(firstColumn);
//		bottomGridPane.getRowConstraints().addAll(firstRow, secondRow);
//		
//		Pane newPane = new Pane();
//		newPane.getStyleClass().add("center-container");
//		
//
//		bottomGridPane.add(createBottomMidContentArea(),0, 0);
//		bottomGridPane.add(newPane, 0, 1);
//		
//		return bottomGridPane;
//	}
//
//	
//	
//	private GridPane createBottomMidContentArea(){
//		
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//		
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		bottomMidTopGridPane.getColumnConstraints().addAll(firstColumn);
//		bottomMidTopGridPane.getRowConstraints().addAll(firstRow);
//		
//		bottomMidTopGridPane.getStyleClass().add("center-container");
//		
//		bottomMidTopGridPane.getChildren().add(createBottomMidTopGridPane("Dashboard"));
//		
//		return bottomMidTopGridPane;
//
//
//	}
//	
//	
//
//
//	private GridPane createBottomMidTopGridPane(String selectedMenu) {
//		bottomMidTopDataGridPane.setStyle("-fx-background-color:white;");
//		
////		    if (selectedMenu.equals("Dashboard")) {
////		        Label dashboardLabel = new Label("Dashboard Content");
////		        bottomMidTopDataGridPane.add(dashboardLabel, 0, 0);
////		        dashboardLabel.setStyle("-fx-background-color:red;");
////		    } else {
////		        Label testingLabel = new Label(selectedMenu);
////		        bottomMidTopDataGridPane.add(testingLabel, 0, 0);
////		        testingLabel.setStyle("-fx-background-color:blue;");
////		    }
//		
//		 bottomMidTopDataGridPane.add(userContentArea.createUserContentData(selectedMenu), 0, 0);
//		  return bottomMidTopDataGridPane;
//	}
//	
//
//}
//	
//	
//	
////	GridPane bottomMidGridPane = new GridPane();
////	
////	private GridPane createBottomMidGridPane(String selectedMenu) {
////	    bottomMidGridPane.getStyleClass().add("bottom-mid-pane");
////	    if (selectedMenu.equals("Dashboard")) {
////	        Label dashboardLabel = new Label("Dashboard Content");
////	        bottomMidGridPane.add(dashboardLabel, 0, 0);
////	        dashboardLabel.setStyle("-fx-background-color:red;");
////	    } else {
////	        Label testingLabel = new Label(selectedMenu);
////	        bottomMidGridPane.add(testingLabel, 0, 0);
////	        testingLabel.setStyle("-fx-background-color:blue;");
////	    } 
////	    return bottomMidGridPane;
////	}
//	
//
//
//
//
//
//
//
