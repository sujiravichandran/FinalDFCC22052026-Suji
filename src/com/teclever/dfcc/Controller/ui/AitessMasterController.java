package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.List;

import com.teclever.datastore.response.AitessConfigurationResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.model.Aitess;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
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

	GridPane aitessMasterGridPane = new GridPane();
	HBox headingHbox = new HBox(10);
	GridPane headingGridPane = new GridPane();
	HBox midHbox = new HBox(30);

	HBox bottomHbox = new HBox(30);

	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();

	public AitessMasterController() {
		setupDisplayTable();
	}

	public void refresh() {
		setupDisplayTable();
	}

	public GridPane aitessMasterGridPane() {

		aitessMasterGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/AitessMaster.css").toExternalForm());
		aitessMasterGridPane.getStyleClass().add("aitessMaster-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);

		aitessMasterGridPane.setPadding(new Insets(10));
		aitessMasterGridPane.setVgap(5);

		aitessMasterGridPane.getColumnConstraints().addAll(firstColumn);
		aitessMasterGridPane.getRowConstraints().addAll(firstRow, secondRow);

		aitessMasterGridPane.add(headingGridPane(), 0, 0);
		aitessMasterGridPane.add(aitessMasterBottomContainer(), 0, 1);
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
		Label pageHeading = new Label("AITESS VERSION");
		pageHeading.getStyleClass().add("headerLabel");
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);
		return headingHbox;
	}

	private HBox createButtonHbox() {

		Button addButton = new Button("ADD AITESS CONFIGURATION");
		addButton.setOnAction(e -> onClickGETButton());
		midHbox.getChildren().addAll(addButton);
		midHbox.setAlignment(Pos.CENTER_RIGHT);
		return midHbox;
	}


	private void onClickGETButton() {
		try {
			FXMLLoader loader = new FXMLLoader(
					this.getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/AddAitess.fxml"));
			Parent root = loader.load();

			Stage popupStage = new Stage();
			AddAitessController controller = loader.getController();
			controller.setMainPageController(this);
			popupStage.initModality(Modality.APPLICATION_MODAL);
			popupStage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(root);
			
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 400) / 2;
		    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 500) / 2;
		    popupStage.setX(centerX);
		    popupStage.setY(centerY);
		    
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void setupDisplayTable() {
		String css = this.getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CustomTableView.css").toExternalForm();
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
			aitessData.setId(aitess.getAitessId());
			driverData.add(aitessData);
		}
		AitessTableViewFactory driverFactory = new AitessTableViewFactory();
		CustomTableView customTableView = driverFactory.createTableView(driverData, true, false);

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
		bottomHbox.getStyleClass().add("aitessMaster-Container");

		return bottomHbox;
	}
	

	private void handleDeleteButtonClicked(Aitess aitessDto) {
		String title = "Confirmation Dialog";
		String contentText = "Are you sure you want to delete Aitess Run Configuration: " + aitessDto.getId() + "?";

		Notifications.showConfirmationDialog(title, contentText, () -> deleteAitess(aitessDto.getId()));
	}

	private void deleteAitess(int AitessId) {
		AitessConfigurationManagement aitessConfManagement = new AitessConfigurationManagement();
		AitessConfigurationResponse response =  aitessConfManagement.deleteAitessConfig(AitessId);
		
		if(response.getResponseCode() == 1) {
			Notifications.showSuccessAlert(response.getResponseMessage());
		}else if(response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
		
		setupDisplayTable();
	}

}
