package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;

import javafx.application.Platform;
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
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class AdminDashboardController {
	private GridPane bottomMainGridPane = new GridPane();
	private GridPane bottomGridPane = new GridPane();
	private GridPane bottomMidTopGridPane = new GridPane();

	AdminCenterContentController adminCenterContentController = new AdminCenterContentController();
	UserManagementController userManagementController = new UserManagementController();
	
	public GridPane createAdminDashboard() {
		bottomMainGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/UserDashboard.css").toExternalForm());
		bottomMainGridPane.setHgap(10);

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(17);
		ColumnConstraints bottomMidColumn = new ColumnConstraints();
		bottomMidColumn.setPercentWidth(83);

		RowConstraints bottomRow = new RowConstraints();
		bottomRow.setPercentHeight(100);

		bottomMainGridPane.getColumnConstraints().addAll(bottomLeftColumn, bottomMidColumn);
		bottomMainGridPane.getRowConstraints().add(bottomRow);

		bottomMainGridPane.add(createBottomleftGridPane(), 0, 0);
		bottomMainGridPane.add(createBottomMidGridPane(), 1, 0);

		return bottomMainGridPane;

	}

	private GridPane createBottomleftGridPane() {
		GridPane bottomLeftGridPane = new GridPane();
		bottomLeftGridPane.setVgap(10);

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(100);

		RowConstraints bottomLeftTopRow = new RowConstraints();
		bottomLeftTopRow.setPercentHeight(93);

		RowConstraints bottomLeftBottomRow = new RowConstraints();
		bottomLeftBottomRow.setPercentHeight(7);

		bottomLeftGridPane.getColumnConstraints().add(bottomLeftColumn);
		bottomLeftGridPane.getRowConstraints().addAll(bottomLeftTopRow, bottomLeftBottomRow);

		bottomLeftGridPane.add(createMenuBox(), 0, 0);
		bottomLeftGridPane.add(createButtonBox(), 0, 1);

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

		        if (selectedItem.getChildren().isEmpty()) {
		            adminCenterContentController.createAdminCenterContent(bottomMidTopGridPane, selectedLabel.getText());
		        }

		        if (!selectedItem.getChildren().isEmpty()) {
		            selectedItem.getChildren().forEach(subMenuItem -> {
		            });
		        }
		    }
		});
		
		if(UserData.getRoleId().equals("RL_ID_1")) {
			addTreeItemWithChildren(rootItem, "User Management", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/dashboard.png", null);
			addTreeItemWithChildren(rootItem, "VDD Config", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/testing.png", null);
			addTreeItemWithChildren(rootItem, "Fault Code Config", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/results.png", null);
			addTreeItemWithChildren(rootItem, "AITESS Config", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/advance_testing.png",
					new String[] { "AITESS Version", "Run Config", "Test Files", "Symbol Files", "Macro Files", "Download Code"});
			addTreeItemWithChildren(rootItem, "OFP Config", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/reports.png",
					new String[] { "OFP Version", "Test Files-OFP", "Symbol Files-OFP", "Macro Files-OFP", "Download Plan" });
			addTreeItemWithChildren(rootItem, "Stage Config", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/self_test.png", null);
			addTreeItemWithChildren(rootItem, "MACRO Buttons", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/lru_test.png", null);
			addTreeItemWithChildren(rootItem, "cPCI card's Details", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/test_summary.png", null);
			addTreeItemWithChildren(rootItem, "Utility", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/history_reports.png",
					new String[] { "Launch type", "Admin Password" ,"CheckSum Data" });
		}else if (UserData.getRoleId().equals("RL_ID_2")) {
			addTreeItemWithChildren(rootItem, "User Management", DFCCConstant.JARSTRING+"/Resources/Images/menuImages/dashboard.png", null);
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

		logoutBox.setOnMouseClicked(e -> Platform.exit());
		
		bottomButtonGridPane.add(logoutBox, 0, 0);

		return bottomButtonGridPane;

	}

	
	private GridPane createBottomMidGridPane() {
		bottomGridPane.setVgap(10);
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		bottomGridPane.getColumnConstraints().addAll(firstColumn);
		bottomGridPane.getRowConstraints().addAll(firstRow);
				

		bottomGridPane.add(createBottomMidContentArea(),0, 0);
		
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
		
		Node userManagementPane = userManagementController.createUserManagemenGridPane();
        bottomMidTopGridPane.add(userManagementPane, 0, 0);

        return bottomMidTopGridPane;
		


	}
}
