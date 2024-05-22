package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.Aitess;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
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
	public static String UUTdropdownValue;
	Map<String, String> uutNameIdMap = DFCCConstant.getUutNameIdMap();
	Map<String, String> uutIdNameMap = DFCCConstant.getUutIdNameMap();

	GridPane aitessMasterGridPane = new GridPane();
	HBox headingHbox = new HBox(10);
	GridPane headingGridPane = new GridPane();
	HBox midHbox = new HBox(30);
	private ComboBox<String> uutTypeField;

	HBox bottomHbox = new HBox(30);

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	public AitessMasterController() {
		uutTypeField = new ComboBox<>();
		loadUUTTypes();
		setupDisplayTable(UUTdropdownValue);

	}
	public void refresh() {
		uUTdropdownAction();
	}

	public GridPane aitessMasterGridPane() {

		aitessMasterGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/AitessMaster.css").toExternalForm());

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		aitessMasterGridPane.setPadding(new Insets(10));
		aitessMasterGridPane.setVgap(5);

		aitessMasterGridPane.getColumnConstraints().addAll(firstColumn);
		aitessMasterGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		aitessMasterGridPane.add(headingGridPane(), 0, 0);
		aitessMasterGridPane.add(aitessMasterMidContainer(), 0, 1);
		aitessMasterGridPane.add(aitessMasterBottomContainer(), 0, 2);
		return aitessMasterGridPane;
	}

	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		Label pageHeading = new Label("AITESS MASTER");
		pageHeading.getStyleClass().add("headerLabel");
//		pageHeading.setPadding(new Insets(0, 0, 0, 20));
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);
		return headingHbox;
	}

	private HBox aitessMasterMidContainer() {
		Label uutLabel = new Label("Select UUT Type:");
		uutLabel.getStyleClass().add("aitessMaster-combobox-Label");

		Button addButton = new Button("+ADD");
		addButton.setOnAction(e -> onClickGETButton());
		
		uutTypeField.setOnAction((event) -> uUTdropdownAction());

		midHbox.getChildren().addAll(uutLabel, uutTypeField, addButton);
		midHbox.getStyleClass().add("aitessMaster-Container");
		midHbox.setAlignment(Pos.CENTER);
		return midHbox;
	}

	private void onClickGETButton() {
		try {
			FXMLLoader loader = new FXMLLoader(
					this.getClass().getResource("/com/teclever/dfcc/ui/fxml/AddAitess.fxml"));
			Parent root =  loader.load();
			
			Stage popupStage = new Stage();
			AddAitessController controller=loader.getController();
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

	private void loadUUTTypes() {
		try {
			ArrayList<String> uutTypeList = new ArrayList<String>();
			UUTMasterDetailsDto[] uutDataList = this.configManager.getAllUUT();
			System.out.println("loadUUTTypes method -------   " + uutDataList.length);
			UUTMasterDetailsDto[] uUTMasterDetailsDtoArray = uutDataList;
			int n = uutDataList.length;
			int n2 = 0;
			while (n2 < n) {
				UUTMasterDetailsDto uutType = uUTMasterDetailsDtoArray[n2];
				System.out.println("UUT Type--------   " + uutType.getUutType());
				uutTypeList.add(uutType.getUutType());
				++n2;
			}
			ObservableList types = FXCollections.observableArrayList(uutTypeList);
			this.uutTypeField.setItems(types);
		} catch (Exception e) {
			System.out.println("loadUUTTypes exception  " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void deleteAitess(int aitessId) {
		try {
			AitessConfigurationDto[] response = configManager.deleteAitessConfig(aitessId);
			if (response.length == 1) {
				Notifications.showSuccessAlert("Successfully Deleted");
//	                AitessMasterController();  // Reload the configurations
			} else {
//	                Notifications.showErrorAlert("Error deleting Aitess configuration: " + response[0].getResponseMessage());
			}
		} catch (Exception e) {
			Notifications.showErrorAlert("Error deleting Aitess configuration: " + e.getMessage());
		}
	}

	private void handleDeleteAitessButtonClicked(Aitess aitess) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete this Aitess configuration?");

		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeNo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

		alert.showAndWait().ifPresent(buttonType -> {
			if (buttonType == buttonTypeYes) {
				deleteAitess(aitess.getAitessId());

				System.out.println("DELETED AITESS" + aitess.getAitessId());
			}
		});
	}

	private void setupDisplayTable(String uutId) {
		System.out.println("Entered Display");
		String css = this.getClass().getResource("/com/teclever/dfcc/ui/css/CustomTableView.css").toExternalForm();
		this.bottomHbox.getStylesheets().add(css);
		AitessConfigurationManagement aitessConfiguration = new AitessConfigurationManagement();
		List<AitessConfigurationDto> lst = aitessConfiguration.getAitessConfig(this.uutNameIdMap.get(UUTdropdownValue));
		ObservableList<Aitess> driverData = FXCollections.observableArrayList();
		
		for (AitessConfigurationDto aitess : lst) {
			Aitess aitessData = new Aitess();
			System.out.println("Aitess Config" + aitessData.getUutType());
			aitessData.setUutType(this.uutIdNameMap.get(aitess.getUutId()));
			aitessData.setAitessCommand(aitess.getAitessCommand());
			aitessData.setAitessName(aitess.getAitessName());
			aitessData.setAitessVersion(aitess.getAitessVersion());
			aitessData.setDriverName(aitess.getDriverName());
			aitessData.setDriverVersion(aitess.getDriverVersion());
			aitessData.setDriverCommand(aitess.getDriverCommand());
			aitessData.setAitessId(aitess.getAitessId());
			driverData.add(aitessData);
		}
		AitessTableViewFactory driverFactory = new AitessTableViewFactory();
		CustomTableView customTableView = driverFactory.createTableView(driverData, true, false);

		customTableView.setPrefWidth(1613.0);
		customTableView.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<Aitess> selectedItems = customTableView.getSelectedItems();
			for (Aitess aitess : selectedItems) {
				handleDeleteAitessButtonClicked(aitess);
			}
		});

		this.bottomHbox.getChildren().clear();
		this.bottomHbox.getChildren().add(customTableView);
	}

	private HBox aitessMasterBottomContainer() {
//		 AitessTableViewFactory driverFactory = new AitessTableViewFactory();
//		CustomTableView customTableView = driverFactory.createTableView(driverData, true, false);
//        customTableView.setPrefWidth(1613.0);
//        customTableView.setPrefHeight(744.0);
		bottomHbox.getStyleClass().add("aitessMaster-Container");

		return bottomHbox;

	}

	void uUTdropdownAction() {
		
			AitessConfigurationManagement configurationManagement = new AitessConfigurationManagement();
			UUTdropdownValue = (String) this.uutTypeField.getValue();
			System.out.println("UUT TYPESSS " + UUTdropdownValue);
			List<AitessConfigurationDto> lst = configurationManagement.getAitessConfig(UUTdropdownValue);
			System.out.println("lst Size" + lst.size());
			this.setupDisplayTable(UUTdropdownValue);
	
		
		
	}

}
