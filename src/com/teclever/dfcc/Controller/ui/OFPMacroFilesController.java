package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.model.AitessMacroFiles;
import com.teclever.dfcc.model.AitessMacroFiles.AitessMacroDetails;
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

public class OFPMacroFilesController {
	
	private GridPane ofpMacroFilesParentGridPane = new GridPane();
	private GridPane ofpMacroFilesTitleGridPane = new GridPane();
	private GridPane ofpMacroFilesTableGridPane = new GridPane();
	
	private MacroFileManagement macroFileManagement = new MacroFileManagement();
	private AitessConfigHeader configHeader = new AitessConfigHeader("OFP");
	
	private TableViewFactory<AitessMacroFiles> userFactory = new OFPMacroFilesTableViewFactory();
	private CustomTableView<AitessMacroFiles> customTableView_macroFiles;
	private ObservableList<AitessMacroFiles> tableData = FXCollections.observableArrayList();
	
	public OFPMacroFilesController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				tableData.clear();
				setAitessMacroFilesTableData(newRunConfigId);
			} else {
				tableData.clear();
			}
		});
	}
	
	public GridPane ofpMacroFileParentGrid() {
		ofpMacroFilesParentGridPane.getStylesheets()
		.add(getClass().getResource("/com/teclever/dfcc/ui/css/OFPMacroFiles.css").toExternalForm());
		ofpMacroFilesParentGridPane.getStyleClass().add("ofpMacroFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		ofpMacroFilesParentGridPane.setPadding(new Insets(10));
		ofpMacroFilesParentGridPane.setVgap(5);

		ofpMacroFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		ofpMacroFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		ofpMacroFilesParentGridPane.add(ofpMacroFilesTopContainer(), 0, 0);
		ofpMacroFilesParentGridPane.add(configHeader.aitessMiddleContainer(), 0, 1);
		ofpMacroFilesParentGridPane.add(createOFPMacroFilesTable(), 0, 2);

		return ofpMacroFilesParentGridPane;
	}



	private GridPane ofpMacroFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpMacroFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		ofpMacroFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		ofpMacroFilesTitleGridPane.add(headerHbox(), 0, 0);
		ofpMacroFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return ofpMacroFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("MACRO FILES");
		headerLabel.getStyleClass().add("ofpMacroFiles-headerLabel");

		HBox headerLabelHbox = new HBox(10);
		headerLabelHbox.setAlignment(Pos.CENTER_LEFT);
		headerLabelHbox.getChildren().add(headerLabel);
		return headerLabelHbox;
	}

	private HBox createButtonHbox() {

		Button addFileButton = new Button("ADD FILE");
//		addFileButton.setOnAction(e -> onClickAddFileButton());
		HBox headerButtonHbox = new HBox(10);

		headerButtonHbox.setAlignment(Pos.CENTER_RIGHT);
		headerButtonHbox.getChildren().add(addFileButton);
		return headerButtonHbox;
	}
	
	


	private GridPane createOFPMacroFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpMacroFilesTableGridPane.getColumnConstraints().addAll(firstColumn);
		ofpMacroFilesTableGridPane.getRowConstraints().addAll(firstRow);
		ofpMacroFilesTableGridPane.getStyleClass().add("ofpMacroFiles-container");
		return ofpMacroFilesTableGridPane;
	
	}
	private void setAitessMacroFilesTableData(String runConfigId) {
		List<MacroDto> macroFileDtoList = macroFileManagement.getAllMacros(runConfigId);
		List<AitessMacroFiles.AitessMacroDetails> detailsList = new ArrayList<>();

		for (MacroDto macroDto : macroFileDtoList) {
			AitessMacroFiles existingFile = tableData.stream()
					.filter(f -> f.getFileName().equals(macroDto.getFileName())).findFirst().orElse(null);

			if (existingFile == null) {
				AitessMacroFiles newFile = new AitessMacroFiles();
				newFile.setFileName(macroDto.getFileName());
				tableData.add(newFile);
			}

			AitessMacroFiles.AitessMacroDetails macroDetails = new AitessMacroFiles.AitessMacroDetails();
			macroDetails.setFileName(macroDto.getFileName());
			macroDetails.setMacroName(macroDto.getMacroName());
			detailsList.add(macroDetails);
		}

		customTableView_macroFiles = userFactory.createTableView(tableData, true, false);
		customTableView_macroFiles.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessMacroFiles> selectedItems = customTableView_macroFiles.getSelectedItems();
			for (AitessMacroFiles rowData : selectedItems) {
				try {
					FXMLLoader addStagePopup = new FXMLLoader(
							getClass().getResource("/com/teclever/dfcc/ui/fxml/AitessMacroPopup.fxml"));
					Parent root = addStagePopup.load();

					AitessMacroPopupController controller = addStagePopup.getController();
					List<AitessMacroFiles.AitessMacroDetails> fileDetails = detailsList.stream()
							.filter(detail -> detail.getFileName().equals(rowData.getFileName()))
							.collect(Collectors.toList());
					controller.setMacroDetails(fileDetails);

					Stage stage = new Stage();
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.initStyle(StageStyle.UNDECORATED);
					stage.centerOnScreen();
					stage.setScene(new Scene(root));
					stage.showAndWait();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		customTableView_macroFiles.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessMacroFiles> selectedItems = customTableView_macroFiles.getSelectedItems();
			for (AitessMacroFiles rowData : selectedItems) {
//				Response res = customFileAddManagement.deleteFile(rowData.getFileName(), "symbols");
//				if (res.getResponseCode() == 1) {
//					tableData.clear();
//					setSymbolFileTableData(RUN_CONFIG_ID);
//				} else {
//					Notifications.showErrorAlert("File not deleted");
//				}
			}
		});
		ofpMacroFilesTableGridPane.getChildren().clear();
		ofpMacroFilesTableGridPane.add(customTableView_macroFiles, 0, 0);
	}

}
class OFPMacroFilesTableViewFactory implements TableViewFactory<AitessMacroFiles> {
	@Override
	public CustomTableView<AitessMacroFiles> createTableView(ObservableList<AitessMacroFiles> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessMacroFiles.class, addUserColumn, addCheckboxColumn);
	}
}

