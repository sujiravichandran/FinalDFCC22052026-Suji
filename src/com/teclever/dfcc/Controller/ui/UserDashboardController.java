										package com.teclever.dfcc.Controller.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class UserDashboardController {

	private GridPane bottomMainGridPane = new GridPane();
	private GridPane bottomGridPane = new GridPane();
	private GridPane bottomMidTopGridPane = new GridPane();

	UserCenterContentController centerContentController = new UserCenterContentController();
	
	public GridPane createUserDashboard() {
		bottomMainGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/UserDashboard.css").toExternalForm());
		bottomMainGridPane.setHgap(10);

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

		menuTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Label selectedLabel = newValue.getValue();
				System.out.println("Selected Label: " + selectedLabel.getText());

				centerContentController.createUserCenterContent(bottomMidTopGridPane,selectedLabel.getText());
				
				if (!newValue.getChildren().isEmpty()) {
					newValue.getChildren().forEach(subMenuItem -> {
					});
				}
			}
		});

		addTreeItemWithChildren(rootItem, "Dashboard", "/Resources/Images/menuImages/dashboard.png", null);
		addTreeItemWithChildren(rootItem, "Testing", "/Resources/Images/menuImages/testing.png",
				new String[] { "SubMenu1", "SubMenu2" });
		addTreeItemWithChildren(rootItem, "Results", "/Resources/Images/menuImages/results.png",
				new String[] { "SubMenu1", "SubMenu2" });
		addTreeItemWithChildren(rootItem, "Advanced Data", "/Resources/Images/menuImages/advance_testing.png",
				new String[] { "SubMenu1", "SubMenu2" });
		addTreeItemWithChildren(rootItem, "Reports", "/Resources/Images/menuImages/reports.png",
				new String[] { "SubMenu1", "SubMenu2" });
		addTreeItemWithChildren(rootItem, "Self Test", "/Resources/Images/menuImages/self_test.png",
				new String[] { "SubMenu1", "SubMenu2" });
		addTreeItemWithChildren(rootItem, "LRU Testing", "/Resources/Images/menuImages/lru_test.png",
				new String[] { "SubMenu1", "SubMenu2" });
		addTreeItemWithChildren(rootItem, "Test Summary", "/Resources/Images/menuImages/test_summary.png",
				new String[] { "SubMenu1", "SubMenu2" });
		addTreeItemWithChildren(rootItem, "History Reports", "/Resources/Images/menuImages/history_reports.png",
				new String[] { "SubMenu1", "SubMenu2" });

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
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomButtonGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		bottomButtonGridPane.getRowConstraints().addAll(firstRow);

		VBox logoutBox = new VBox();
		logoutBox.setAlignment(Pos.CENTER);
		Label logoutLabel = new Label("Logout");
		logoutBox.getChildren().add(logoutLabel);
		logoutBox.getStyleClass().add("logout-button");
		logoutLabel.getStyleClass().add("logout-text");

		VBox exitBox = new VBox();
		exitBox.setAlignment(Pos.CENTER);
		Label exitLabel = new Label("Exit");
		exitBox.getChildren().add(exitLabel);
		exitBox.getStyleClass().add("exit-button");
		exitLabel.getStyleClass().add("exit-text");

		bottomButtonGridPane.add(logoutBox, 0, 0);
		bottomButtonGridPane.add(exitBox, 1, 0);

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

		Pane newPane = new Pane();
		newPane.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
		newPane.prefHeightProperty().bind(bottomRightTopRow.prefHeightProperty());

		Pane newPane1 = new Pane();
		newPane1.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
		newPane1.prefHeightProperty().bind(bottomRightMidRow.prefHeightProperty());

		Pane newPane2 = new Pane();
		newPane2.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
		newPane2.prefHeightProperty().bind(bottomRightBottomRow.prefHeightProperty());

		Pane newPane3 = new Pane();
		newPane3.prefWidthProperty().bind(bottomRightColumn.prefWidthProperty());
		newPane3.prefHeightProperty().bind(bottomRightBottomLastRow.prefHeightProperty());

		newPane.setStyle("-fx-background-color: black;");
		newPane1.setStyle("-fx-background-color: white;");
		newPane2.setStyle("-fx-background-color: green;");
		newPane3.setStyle("-fx-background-color: pink;");

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
		firstRow.setPercentHeight(22);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(26);

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
		Label statusLabel = new Label("DFCC Power ON");
		secondRowBox.getStyleClass().add("dfcc-status-box");
		statusLabel.getStyleClass().add("dfcc-status");
		secondRowBox.getChildren().add(statusLabel);

		bottomRightMidFirstGridPane.add(firstRowBox, 0, 0);
		bottomRightMidFirstGridPane.add(secondRowBox, 0, 1);

		return bottomRightMidFirstGridPane;
	}

	private VBox createBottomRightMidSecond() {
		VBox bottomRightMidSecondBox = new VBox();
		bottomRightMidSecondBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("Temperature");
		titleLabel.getStyleClass().add("right-common-title");

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
		Label label1 = new Label("36.5");
		box1.getChildren().add(label1);
		box1.getStyleClass().add("temp-box");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("36.5");
		box2.getChildren().add(label2);
		box2.getStyleClass().add("temp-box");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("36.5");
		box3.getChildren().add(label3);
		box3.getStyleClass().add("temp-box");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("36.5");
		box4.getChildren().add(label4);
		box4.getStyleClass().add("temp-box");

		bottomRightMidSecondGridPane.add(box1, 0, 0);
		bottomRightMidSecondGridPane.add(box2, 1, 0);
		bottomRightMidSecondGridPane.add(box3, 0, 1);
		bottomRightMidSecondGridPane.add(box4, 1, 1);

		bottomRightMidSecondBox.getChildren().addAll(titleLabel, bottomRightMidSecondGridPane);

		return bottomRightMidSecondBox;

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
		box1.getStyleClass().add("power-status-box");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("CH-2");
		box2.getChildren().add(label2);
		box2.getStyleClass().add("power-status-box");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("CH-3");
		box3.getChildren().add(label3);
		box3.getStyleClass().add("power-status-box");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("CH-4");
		box4.getChildren().add(label4);
		box4.getStyleClass().add("power-status-box");

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
		box1.getStyleClass().add("power-status-box");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("CH-2");
		box2.getChildren().add(label2);
		box2.getStyleClass().add("power-status-box");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("CH-3");
		box3.getChildren().add(label3);
		box3.getStyleClass().add("power-status-box");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("CH-4");
		box4.getChildren().add(label4);
		box4.getStyleClass().add("power-status-box");

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

		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 2; col++) {
				VBox box = new VBox();
				box.setAlignment(Pos.CENTER);
				Label label = new Label(labels[i]);
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
		
		Pane newPane = new Pane();
		newPane.getStyleClass().add("center-container");
		

		bottomGridPane.add(createBottomMidContentArea(),0, 0);
		bottomGridPane.add(newPane, 0, 1);
		
		return bottomGridPane;
	}

	
	
	private GridPane createBottomMidContentArea(){
		
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
//				.add(getClass().getResource("/com/teclever/dfcc/ui/css/UserDashboard.css").toExternalForm());
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
////               System.out.println("Submenu Items:");
//					newValue.getChildren().forEach(subMenuItem -> {
////            	   Label subMenuLabel = subMenuItem.getValue();
////            	   System.out.println(" -" + subMenuLabel.getText());
//					});
//				}
//			}
//		});
//
//		addTreeItemWithChildren(rootItem, "Dashboard", "/Resources/Images/menuImages/dashboard.png", null);
//		addTreeItemWithChildren(rootItem, "Testing", "/Resources/Images/menuImages/testing.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Results", "/Resources/Images/menuImages/results.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Advanced Data", "/Resources/Images/menuImages/advance_testing.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Reports", "/Resources/Images/menuImages/reports.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Self Test", "/Resources/Images/menuImages/self_test.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "LRU Testing", "/Resources/Images/menuImages/lru_test.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "Test Summary", "/Resources/Images/menuImages/test_summary.png",
//				new String[] { "SubMenu1", "SubMenu2" });
//		addTreeItemWithChildren(rootItem, "History Reports", "/Resources/Images/menuImages/history_reports.png",
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
