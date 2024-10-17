package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.List;

import com.teclever.datastore.response.OfpConfigurationResponse;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.model.OFP;
import com.teclever.dfcc.utils.AitessConfigHeader;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OFPMasterController {
	private GridPane ofpMasterParentGridPane = new GridPane();
	private GridPane ofpMasterTitleGridPane = new GridPane();
	private GridPane ofpMasterTableGridPane = new GridPane();

	private String UUT_ID;

	private OfpConfigurationManagement ofpConfig = new OfpConfigurationManagement();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private AitessConfigHeader configHeader = new AitessConfigHeader("OFPMASTER");

	private TableViewFactory<OFP> userFactory = new OFPMasterTableViewFactory();
	private CustomTableView<OFP> customTableView_ofpMaster;
	private ObservableList<OFP> tableData = FXCollections.observableArrayList();

	public OFPMasterController() {
		configHeader.uutIdProperty().addListener((obs, oldUutId, newUutId) -> {
			if (newUutId != null) {
				tableData.clear();
				this.UUT_ID=newUutId;
				setOfpMasterTableData(newUutId);
			} else {
				tableData.clear();
			}
		});
	}
	public void refresh() {
		tableData.clear();
		setOfpMasterTableData(UUT_ID);
	}

	public GridPane ofpMasterConfigParentGrid() {
		ofpMasterParentGridPane.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/OFPMaster.css").toExternalForm());
		ofpMasterParentGridPane.getStyleClass().add("ofpMaster-parent-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(88);

		ofpMasterParentGridPane.setPadding(new Insets(10));
		ofpMasterParentGridPane.setVgap(5);

		ofpMasterParentGridPane.getColumnConstraints().addAll(firstColumn);
		ofpMasterParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		ofpMasterParentGridPane.add(ofpMasterTopContainer(), 0, 0);
		ofpMasterParentGridPane.add(configHeader.aitessMiddleContainer(), 0, 1);
		ofpMasterParentGridPane.add(createOFPMasterTable(), 0, 2);

		return ofpMasterParentGridPane;
	}


	private GridPane ofpMasterTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpMasterTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		ofpMasterTitleGridPane.getRowConstraints().addAll(firstRow);

		ofpMasterTitleGridPane.add(headerHbox(), 0, 0);
		ofpMasterTitleGridPane.add(createButtonHbox(), 1, 0);

		return ofpMasterTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("OFP VERSIONS");
		headerLabel.getStyleClass().add("ofpMaster-headerLabel");

		HBox headerLabelHbox = new HBox(10);
		headerLabelHbox.setAlignment(Pos.CENTER_LEFT);
		headerLabelHbox.getChildren().add(headerLabel);
		return headerLabelHbox;
	}

	private HBox createButtonHbox() {

		Button addFileButton = new Button("ADD OFP");
		addFileButton.setOnAction(e -> onClickAddOFPButton());
		HBox headerButtonHbox = new HBox(10);

		headerButtonHbox.setAlignment(Pos.CENTER_RIGHT);
		headerButtonHbox.getChildren().add(addFileButton);
		return headerButtonHbox;
	}

	private void onClickAddOFPButton() {
		if(UUT_ID!=null) {
			try {
				FXMLLoader addOfpPopup = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/AddOFP.fxml"));
				Parent root = addOfpPopup.load();
				AddOFPController controller = addOfpPopup.getController();
				controller.setUutID(UUT_ID);
				controller.setMainPageController(this);

				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.centerOnScreen();
				stage.setScene(new Scene(root));
				stage.showAndWait();
				refresh();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			Notifications.showWarningAlert("Please select UUT Type");
		}
		
	}

	private GridPane createOFPMasterTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpMasterTableGridPane.getColumnConstraints().addAll(firstColumn);
		ofpMasterTableGridPane.getRowConstraints().addAll(firstRow);
		ofpMasterTableGridPane.getStyleClass().add("ofpMaster-container");
		return ofpMasterTableGridPane;

	}
	
	private void handleDeleteButtonClicked(OFP ofpDto) {
		String title = "Confirmation Dialog";
		String contentText = "Are you sure you want to delete OFP Configuration: " + ofpDto.getOfpName() + "?";

		Notifications.showConfirmationDialog(title, contentText, () -> deleteOfp(ofpDto.getOfpConfigId()));
	}
	
	private void deleteOfp(String ofpConfigId) {		
		OfpConfigurationResponse response = ofpConfig.deleteOfpConfig(ofpConfigId);
		if(response.getResponseCode() == 1) {
			Notifications.showSuccessAlert(response.getResponseMessage());
			refresh();
		}else if(response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	
	private void setOfpMasterTableData(String uutId) {
		List<OfpConfigurationDto> getOfpConfigList=ofpConfig.getOfpConfig(uutId);
		for (OfpConfigurationDto ofpConfigDto : getOfpConfigList) {
			OFP ofpData=new OFP();
			ofpData.setOfpName(ofpConfigDto.getOfpName());
			ofpData.setOfpVersion(ofpConfigDto.getOfpVersion());
			ofpData.setConfigFile(ofpConfigDto.getConfigFile());
			ofpData.setOfpConfigId(ofpConfigDto.getOfpConfigId());
			tableData.add(ofpData);
		}

		customTableView_ofpMaster = userFactory.createTableView(tableData, true, false);
		customTableView_ofpMaster.hideColumn("OFP CONFIG ID");
		
		customTableView_ofpMaster.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<OFP> selectedItems = customTableView_ofpMaster.getSelectedItems();
			for (OFP rowData : selectedItems) {
				
				handleDeleteButtonClicked(rowData);
			}
		});
		ofpMasterTableGridPane.getChildren().clear();
		ofpMasterTableGridPane.add(customTableView_ofpMaster, 0, 0);
	}

}
class OFPMasterTableViewFactory implements TableViewFactory<OFP> {
	@Override
	public CustomTableView<OFP> createTableView(ObservableList<OFP> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, OFP.class, addUserColumn, addCheckboxColumn);
	}
}
