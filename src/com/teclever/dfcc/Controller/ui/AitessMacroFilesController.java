package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.dto.AddCustomFileResponse;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.filemanagement.CustomFileAddManagement;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.model.AitessMacroFiles;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AitessMacroFilesController {
	private GridPane macroFilesParentGridPane = new GridPane();
	private GridPane macroFilesTitleGridPane = new GridPane();
	private GridPane macroFileTableGridPane = new GridPane();

	private AitessConfigHeader configHeader = new AitessConfigHeader("AITESS");
	private MacroFileManagement macroFileManagement = new MacroFileManagement();
	private CustomFileAddManagement customFileAddManagement = new CustomFileAddManagement();

	private TableViewFactory<AitessMacroFiles> userFactory = new MacroFilesTableViewFactory();
	private CustomTableView<AitessMacroFiles> customTableView_macroFiles;
	private ObservableList<AitessMacroFiles> tableData = FXCollections.observableArrayList();

	private String RUN_CONFIG_ID;
	public AitessMacroFilesController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				this.RUN_CONFIG_ID = newRunConfigId;
				setAitessMacroFilesTableData(newRunConfigId);
			} else {
				tableData.clear();
			}
		});
	}

	public GridPane macroFilesConfigParentGrid() {

		macroFilesParentGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/AitessMacroFiles.css").toExternalForm());
		macroFilesParentGridPane.getStyleClass().add("macroFiles-parent-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(88);

		macroFilesParentGridPane.setPadding(new Insets(10));
		macroFilesParentGridPane.setVgap(5);

		macroFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		macroFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		macroFilesParentGridPane.add(macroFilesTopContainer(), 0, 0);
		macroFilesParentGridPane.add(configHeader.aitessMiddleContainer(), 0, 1);
		macroFilesParentGridPane.add(createAitessMacroFilesTable(), 0, 2);
		return macroFilesParentGridPane;

	}

	private GridPane macroFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		macroFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		macroFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		macroFilesTitleGridPane.add(headerHbox(), 0, 0);
		macroFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return macroFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("MACRO FILES");
		headerLabel.getStyleClass().add("macroFiles-headerLabel");

		HBox headerLabelHbox = new HBox(10);
		headerLabelHbox.setAlignment(Pos.CENTER_LEFT);
		headerLabelHbox.getChildren().add(headerLabel);
		return headerLabelHbox;
	}

	private HBox createButtonHbox() {

		Button addFileButton = new Button("ADD FILE");
		addFileButton.setOnAction(e -> onClickAddFileButton());
		HBox headerButtonHbox = new HBox(10);

		headerButtonHbox.setAlignment(Pos.CENTER_RIGHT);
		headerButtonHbox.getChildren().add(addFileButton);
		return headerButtonHbox;
	}

	private GridPane createAitessMacroFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		macroFileTableGridPane.getColumnConstraints().addAll(firstColumn);
		macroFileTableGridPane.getRowConstraints().addAll(firstRow);
		macroFileTableGridPane.getStyleClass().add("macroFiles-Container");

		return macroFileTableGridPane;
	}

	private void onClickAddFileButton() {
		if(RUN_CONFIG_ID!=null) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
//		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.sym"));
		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(macroFilesParentGridPane.getScene().getWindow());
		List<String> filePaths = new ArrayList<>();
		if (selectedFiles != null) {
			for (File file : selectedFiles) {
				filePaths.add(file.getAbsolutePath());
			}

			AddCustomFileResponse res = customFileAddManagement.addCustomFiles(RUN_CONFIG_ID, filePaths, "macros");
			if (res.getResponseCode() == 1) {
	            	 tableData.clear();
				setAitessMacroFilesTableData(RUN_CONFIG_ID);
			} else {
				Notifications.showErrorAlert("Files not added");
			}
		}
		}else {
			Notifications.showWarningAlert("Please select UUT Type and Test Type");
		}
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
//			System.out.println(macroDetails.getMacroName());
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

//					List<AitessMacroFiles.AitessMacroDetails> fileDetails = detailsList.stream()
//						    .filter(detail -> detail.getFileName().equals(rowData.getFileName()) && !detail.getMacroName().equals("--"))
//						    .collect(Collectors.toList());
					List<AitessMacroFiles.AitessMacroDetails> fileDetails = detailsList.stream()
							.filter(detail -> detail.getFileName().equals(rowData.getFileName()))
							.collect(Collectors.toList());
					System.out.println("200----"+fileDetails.size());
					if(fileDetails.size() <2) {
						Notifications.showWarningAlert("No data in selected file");
					}else {
						AitessMacroPopupController controller = addStagePopup.getController();
						controller.setMacroDetails(fileDetails);
						Stage stage = new Stage();
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.initStyle(StageStyle.UNDECORATED);
						stage.centerOnScreen();
						stage.setScene(new Scene(root));
						stage.showAndWait();
					}
					

					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		customTableView_macroFiles.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessMacroFiles> selectedItems = customTableView_macroFiles.getSelectedItems();
			for (AitessMacroFiles rowData : selectedItems) {
				Response res = customFileAddManagement.deleteFile(rowData.getFileName(), "macros");
				if (res.getResponseCode() == 1) {
					tableData.clear();
					setAitessMacroFilesTableData(RUN_CONFIG_ID);
				} else {
					Notifications.showErrorAlert("File not deleted");
				}
			}
		});

		macroFileTableGridPane.getChildren().clear();
		macroFileTableGridPane.add(customTableView_macroFiles, 0, 0);
	}
}


class MacroFilesTableViewFactory implements TableViewFactory<AitessMacroFiles> {
	@Override
	public CustomTableView<AitessMacroFiles> createTableView(ObservableList<AitessMacroFiles> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessMacroFiles.class, addUserColumn, addCheckboxColumn);
	}
}
