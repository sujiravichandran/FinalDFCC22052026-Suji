package com.teclever.dfcc.Controller.ui;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javafx.scene.text.Text;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisDTO;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataUnitsDetailsDTO;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.model.AdvancedDataAnalysis;
import com.teclever.dfcc.model.AdvancedDataAnalysis2;
import com.teclever.dfcc.model.BrowseFileDetailedData;
import com.teclever.dfcc.resultstore.dto.FilesFetchFailsDTO;
import com.teclever.dfcc.resultstore.dto.StepDto;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class DataAnalysisController {

	private GridPane dataAnalysisMainContainerGridPane = new GridPane();
	
	private GridPane bottomGridPane = new GridPane();
	
	private GridPane dataAnalysisrightMidContainerGridPane = new GridPane();

	private GridPane bottomMidTopGridPane = new GridPane();
	private GridPane closeButtonGridPane = new GridPane();

	private HBox closeButtonHbox = new HBox(10);
	private Button closeButton = new Button("Close");
	
	private HBox headingHbox = new HBox(10);
	private GridPane headingGridPane = new GridPane();
	private Label pageHeading = new Label("ADVANCED DATA ANALYSIS");
	private DataAnalysisCenterContentController dataAnalysisCenterContentController = new DataAnalysisCenterContentController();


	public GridPane createDashboardMainContainerGridPane() {

		dataAnalysisMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataAnalysis.css").toExternalForm());
		dataAnalysisMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints dataLeftColumn = new ColumnConstraints();
		dataLeftColumn.setPercentWidth(17);
		ColumnConstraints dataMidColumn = new ColumnConstraints();
		dataMidColumn.setPercentWidth(83);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(86);
		
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(7);
		
		dataAnalysisMainContainerGridPane.setHgap(10);
		dataAnalysisMainContainerGridPane.setVgap(10);

		dataAnalysisMainContainerGridPane.getColumnConstraints().addAll(dataLeftColumn, dataMidColumn);
		dataAnalysisMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		dataAnalysisMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));
		
		dataAnalysisMainContainerGridPane.add(headingGridPane(), 0, 0, 2, 1);
		dataAnalysisMainContainerGridPane.add(createDataAnalysisLeftGridPane(), 0, 1);
		dataAnalysisMainContainerGridPane.add(createBottomMidGridPane(), 1, 1);
		dataAnalysisMainContainerGridPane.add(closeButtonGridPane(), 0, 2, 2, 1);
	

		return dataAnalysisMainContainerGridPane;
	}
	
	
	private GridPane createBottomMidGridPane() {
		bottomGridPane.setVgap(10);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomGridPane.getColumnConstraints().addAll(firstColumn);
		bottomGridPane.getRowConstraints().addAll(firstRow);

		return bottomGridPane;
	}
	
	

	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().add(firstRow);

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		headingHbox.getStyleClass().add("dataanalysis-testing-right-container");
		headingHbox.setAlignment(Pos.CENTER);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	private GridPane createDataAnalysisLeftGridPane() {
		GridPane bottomLeftGridPane = new GridPane();
		bottomLeftGridPane.setVgap(10);

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(100);

		RowConstraints bottomLeftTopRow = new RowConstraints();
		bottomLeftTopRow.setPercentHeight(100);


		bottomLeftGridPane.getColumnConstraints().add(bottomLeftColumn);
		bottomLeftGridPane.getRowConstraints().addAll(bottomLeftTopRow);

		bottomLeftGridPane.add(createMenuBox(), 0, 0);


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
					dataAnalysisCenterContentController.createUserCenterContent(bottomGridPane, selectedLabel.getText(), null,
							null);
				}

				if (!selectedItem.getChildren().isEmpty()) {
					selectedItem.getChildren().forEach(subMenuItem -> {
					});
				}
			}
		});

			addTreeItemWithChildren(rootItem, "Data Analysis",DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);
			addTreeItemWithChildren(rootItem, "Unit Failure",DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);
			addTreeItemWithChildren(rootItem, "Build Configuration",DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);
			addTreeItemWithChildren(rootItem, "Advanced Results",DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);
			addTreeItemWithChildren(rootItem, "Link Files",DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);
			addTreeItemWithChildren(rootItem, "Manual Testing",DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);

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


	public GridPane closeButtonGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		closeButtonGridPane.getColumnConstraints().addAll(firstColumn);
		closeButtonGridPane.getRowConstraints().add(firstRow);
		closeButtonGridPane.add(closeButtonHbox(), 0, 0);

		return closeButtonGridPane;

	}

	private HBox closeButtonHbox() {

		closeButtonHbox.getStyleClass().add("dataanalysis-testing-right-container");
		closeButtonHbox.setAlignment(Pos.CENTER);
		closeButtonHbox.getChildren().add(closeButton);
		closeButtonHbox.setMaxWidth(Double.MAX_VALUE);
		closeButton.setOnAction(e -> {
			Stage stage = (Stage) closeButton.getScene().getWindow();
			stage.close();
		});

		return closeButtonHbox;
	}

	

}
