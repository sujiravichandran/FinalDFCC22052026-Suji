package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.Aitess;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class AitessTableViewFactory implements TableViewFactory<Aitess> {

	@Override
	public CustomTableView<Aitess> createTableView(ObservableList<Aitess> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, Aitess.class, addUserColumn, addCheckboxColumn);

	}
}

public class AitessMasterController {
//	public static String UUTdropdownValue;
//	Map<String, String> uutNameIdMap = DFCCConstant.getUutNameIdMap();
//	Map<String, String> uutIdNameMap = DFCCConstant.getUutIdNameMap();

	GridPane aitessMasterGridPane = new GridPane();
	HBox headingHbox = new HBox(10);
	GridPane headingGridPane = new GridPane();
	HBox midHbox = new HBox(30);
//	private ComboBox<String> uutTypeField;

	HBox bottomHbox = new HBox(30);

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	public AitessMasterController() {
//		uutTypeField = new ComboBox<>();
//		loadUUTTypes();
		setupDisplayTable();
		
//		aitessMasterBottomContainer();
	}

	public void refresh() {
		setupDisplayTable();
	}

	public GridPane aitessMasterGridPane() {

		aitessMasterGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AitessMaster.css").toExternalForm());
		aitessMasterGridPane.getStyleClass().add("aitessMaster-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(95);

		aitessMasterGridPane.setPadding(new Insets(10));
		aitessMasterGridPane.setVgap(5);

		aitessMasterGridPane.getColumnConstraints().addAll(firstColumn);
		aitessMasterGridPane.getRowConstraints().addAll(firstRow, secondRow);

		aitessMasterGridPane.add(headingGridPane(), 0, 0);
		aitessMasterGridPane.add(aitessMasterBottomContainer(), 0, 1);
//		aitessMasterGridPane.add(aitessMasterBottomContainer(), 0, 2);
		return aitessMasterGridPane;
	}
	
	

	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);

		headingGridPane.add(headingHbox(), 0, 0);
		headingGridPane.add(createButtonHbox(), 1, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		Label pageHeading = new Label("AITESS MASTER");
		pageHeading.getStyleClass().add("headerLabel");
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);
		return headingHbox;
	}
	
	private HBox createButtonHbox() {

		Button addButton = new Button("+ADD");
		addButton.setOnAction(e -> onClickGETButton());
		midHbox.getChildren().addAll( addButton);
		midHbox.setAlignment(Pos.CENTER_RIGHT);
		return midHbox;
	}

/*	private HBox aitessMasterMidContainer() {
//		Label uutLabel = new Label("Select UUT Type:");
//		uutLabel.getStyleClass().add("aitessMaster-combobox-Label");

		Button addButton = new Button("+ADD");
		addButton.setOnAction(e -> onClickGETButton());

//		uutTypeField.setOnAction((event) -> uUTdropdownAction());

		midHbox.getChildren().addAll( addButton);
		midHbox.getStyleClass().add("aitessMaster-Container");
		midHbox.setAlignment(Pos.CENTER);
		return midHbox;
	}

	*/
	
	private void onClickGETButton() {
//	    String selectedUUTType = uutTypeField.getValue();
	    
//	    if (selectedUUTType == null || selectedUUTType.isEmpty()) {
//	        // Show alert because UUT type is not selected
//	        Alert alert = new Alert(AlertType.WARNING);
//	        alert.setTitle("Warning");
//	        alert.setHeaderText(null);
//	        alert.setContentText("Please select a UUT Type.");
//
//	        alert.showAndWait();
//	    } else {
	        try {
	            FXMLLoader loader = new FXMLLoader(
	                    this.getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/AddAitess.fxml"));
	            Parent root = loader.load();

	            Stage popupStage = new Stage();
	            AddAitessController controller = loader.getController();
	            controller.setMainPageController(this);
	            popupStage.initModality(Modality.APPLICATION_MODAL);
	            popupStage.initStyle(StageStyle.UNDECORATED);
	            Scene scene = new Scene(root);
	            popupStage.setScene(scene);
	            popupStage.showAndWait();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	

	
	

//	private void loadUUTTypes() {
//		try {
//			ArrayList<String> uutTypeList = new ArrayList<String>();
//			UUTMasterDetailsDto[] uutDataList = this.configManager.getAllUUT();
//			UUTMasterDetailsDto[] uUTMasterDetailsDtoArray = uutDataList;
//			int n = uutDataList.length;
//			int n2 = 0;
//			while (n2 < n) {
//				UUTMasterDetailsDto uutType = uUTMasterDetailsDtoArray[n2];
//				uutTypeList.add(uutType.getUutType());
//				++n2;
//			}
//			ObservableList types = FXCollections.observableArrayList(uutTypeList);
//			this.uutTypeField.setItems(types);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void deleteAitess(int aitessId) {
//	    try {
//	        AitessConfigurationDto[] response = configManager.deleteAitessConfig(aitessId);
//	        if (response.length == 1) {
//	            Notifications.showSuccessAlert("Successfully Deleted");
//	            removeRowFromTable(tableModel, aitessId);
//	            
//	        } 
//	    } catch (Exception e) {
//	        Notifications.showErrorAlert("Error deleting Aitess configuration: " + e.getMessage());
//	    }
//	}
//
//	private void handleDeleteAitessButtonClicked(Aitess aitess) {
//	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//	    alert.setTitle("Confirmation Dialog");
//	    alert.setHeaderText(null);
//	    alert.setContentText("Are you sure you want to delete this Aitess configuration?");
//
//	    ButtonType buttonTypeYes = new ButtonType("Yes");
//	    ButtonType buttonTypeNo = new ButtonType("No");
//
//	    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
//
//	    alert.showAndWait().ifPresent(buttonType -> {
//	        if (buttonType == buttonTypeYes) {
//	            deleteAitess(aitess.getAitessId());
//	            System.out.println("DELETED AITESS" + aitess.getAitessId());
//	        }
//	    });
//	}
//
//	private void removeRowFromTable(DefaultTableModel tableModel, int aitessId) {
//	    for (int i = 0; i < tableModel.getRowCount(); i++) {
//	        if ((int) tableModel.getValueAt(i, ID_COLUMN_INDEX) == aitessId) {
//	            tableModel.removeRow(i);
//	            break;
//	        }
//	        uUTdropdownAction();
//	    }
//	    // Alternatively, you might need to refresh the entire table
//	    // reloadTableData();
//	}

	private void handleDeleteButtonClicked(Aitess aitessDto) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText(
				"Are you sure you want to delete Aitess Run Configuration: " + aitessDto.getAitessId() + "?");

		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeNo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

		alert.showAndWait().ifPresent(buttonType -> {
			if (buttonType == buttonTypeYes) {
				deleteAitess(aitessDto.getAitessId());
			}
		});
	}

	private void deleteAitess(int AitessId) {
		AitessConfigurationManagement aitessConfManagement = new AitessConfigurationManagement();
		aitessConfManagement.deleteAitessConfig(AitessId);
		
		setupDisplayTable();
	}

	private void setupDisplayTable() {
		String css = this.getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/CustomTableView.css").toExternalForm();
		this.bottomHbox.getStylesheets().add(css);
		AitessConfigurationManagement aitessConfiguration = new AitessConfigurationManagement();
		List<AitessConfigurationDto> lst = aitessConfiguration.getAitessConfig();
		ObservableList<Aitess> driverData = FXCollections.observableArrayList();

		for (AitessConfigurationDto aitess : lst) {
			Aitess aitessData = new Aitess();
			aitessData.setAitessCommand(aitess.getAitessCommand());
			aitessData.setAitessName(aitess.getAitessName());
			aitessData.setAitessVersion(aitess.getAitessVersion());
			aitessData.setDriverName(aitess.getDriverName());
			aitessData.setLoadDriverCommand(aitess.getLoadDriverCommand());
			aitessData.setUnloadDriverCommand(aitess.getUnloadDriverCommand());
			aitessData.setAitessId(aitess.getAitessId());
			driverData.add(aitessData);
		}
		AitessTableViewFactory driverFactory = new AitessTableViewFactory();
		CustomTableView customTableView = driverFactory.createTableView(driverData, true, false);
		
		customTableView.hideColumn("AITESS ID");

		customTableView.setPrefWidth(1613.0);
		customTableView.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<Aitess> selectedItems = customTableView.getSelectedItems();
			for (Aitess aitess : selectedItems) {
				handleDeleteButtonClicked(aitess);
			}
		});

		this.bottomHbox.getChildren().clear();
		this.bottomHbox.getChildren().add(customTableView);
	}

	private HBox aitessMasterBottomContainer() {
//		 AitessTableViewFactory driverFactory = new AitessTableViewFactory();
//		CustomTableView customTableView = driverFactory.createTableView(driverData, true, false);
//       customTableView.setPrefWidth(1613.0);
//       customTableView.setPrefHeight(744.0);
		bottomHbox.getStyleClass().add("aitessMaster-Container");

		return bottomHbox;

	}

//	void uUTdropdownAction() {
//
//		AitessConfigurationManagement configurationManagement = new AitessConfigurationManagement();
////		UUTdropdownValue = (String) this.uutTypeField.getValue();
//		List<AitessConfigurationDto> lst = configurationManagement.getAitessConfig();
////		this.setupDisplayTable(UUTdropdownValue);
//
//	}

}
