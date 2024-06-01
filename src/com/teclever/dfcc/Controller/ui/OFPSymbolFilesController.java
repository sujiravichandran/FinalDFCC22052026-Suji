package com.teclever.dfcc.Controller.ui;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.AddCustomFileResponse;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.filemanagement.CustomFileAddManagement;
import com.teclever.dfcc.datastore.filemanagement.SymbolFileManagement;
import com.teclever.dfcc.model.AitessSymbolFiles;
import com.teclever.dfcc.model.AitessSymbolFiles.AitessSymbolDetails;
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

public class OFPSymbolFilesController {
	
	private GridPane ofpSymbolFilesParentGridPane = new GridPane();
	private GridPane ofpSymbolFilesTitleGridPane = new GridPane();
	private GridPane ofpSymbolFilesTableGridPane = new GridPane();
	
	private SymbolFileManagement symbolFileManagement = new SymbolFileManagement();
	private AitessConfigHeader configHeader = new AitessConfigHeader("OFP");
	private CustomFileAddManagement customFileAddManagement=new CustomFileAddManagement();

	
	private TableViewFactory<AitessSymbolFiles> userFactory = new OFPSymbolFilesTableViewFactory();
	private CustomTableView<AitessSymbolFiles> customTableView_symbolFiles;
	private ObservableList<AitessSymbolFiles> tableData = FXCollections.observableArrayList();
	
	private String RUN_CONFIG_ID;
	public OFPSymbolFilesController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				this.RUN_CONFIG_ID = newRunConfigId;
				tableData.clear();
				setAitessSymbolFilesTableData(newRunConfigId);
			} else {
				tableData.clear();
			}
		});
	}
	
	public GridPane ofpSymbolFileParentGrid() {
		ofpSymbolFilesParentGridPane.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/OFPSymbolFiles.css").toExternalForm());
		ofpSymbolFilesParentGridPane.getStyleClass().add("ofpSymbolFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(88);

		ofpSymbolFilesParentGridPane.setPadding(new Insets(10));
		ofpSymbolFilesParentGridPane.setVgap(5);

		ofpSymbolFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		ofpSymbolFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		ofpSymbolFilesParentGridPane.add(ofpSymbolFilesTopContainer(), 0, 0);
		ofpSymbolFilesParentGridPane.add(configHeader.aitessMiddleContainer(), 0, 1);
		ofpSymbolFilesParentGridPane.add(createOFPSymbolFilesTable(), 0, 2);

		return ofpSymbolFilesParentGridPane;
	}



	private GridPane ofpSymbolFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpSymbolFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		ofpSymbolFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		ofpSymbolFilesTitleGridPane.add(headerHbox(), 0, 0);
		ofpSymbolFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return ofpSymbolFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("SYMBOL FILES");
		headerLabel.getStyleClass().add("ofpSymbolFiles-headerLabel");

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
	private void onClickAddFileButton() {
		if(RUN_CONFIG_ID!=null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select File");
//			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.sym"));
			List<File> selectedFiles = fileChooser.showOpenMultipleDialog(ofpSymbolFilesParentGridPane.getScene().getWindow());
			List<String> filePaths = new ArrayList<>();
			if (selectedFiles != null) {
				for (File file : selectedFiles) {
					filePaths.add(file.getAbsolutePath());
				}

				AddCustomFileResponse res = customFileAddManagement.addCustomFiles(RUN_CONFIG_ID, filePaths, "symbols");
				if (res.getResponseCode() == 1) {
//		            	 tableData.clear();
					setAitessSymbolFilesTableData(RUN_CONFIG_ID);
				} else {
					Notifications.showErrorAlert("Files not added");
				}
			}
		}else {
			Notifications.showWarningAlert("Please select UUT Type");
		}
		
	}
	


	private GridPane createOFPSymbolFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpSymbolFilesTableGridPane.getColumnConstraints().addAll(firstColumn);
		ofpSymbolFilesTableGridPane.getRowConstraints().addAll(firstRow);
		ofpSymbolFilesTableGridPane.getStyleClass().add("ofpSymbolFiles-container");
		return ofpSymbolFilesTableGridPane;
	
	}
	private void setAitessSymbolFilesTableData(String runConfigId) {
		List<SymbolDto> symbolDtoList = symbolFileManagement.getAllSymbols(runConfigId);
		List<AitessSymbolDetails> detailsList = new ArrayList<>();

		for (SymbolDto symbols : symbolDtoList) {
			AitessSymbolFiles existingFile = tableData.stream()
					.filter(f -> f.getFileName().equals(symbols.getFileName())).findFirst().orElse(null);

			if (existingFile == null) {
				AitessSymbolFiles newFile = new AitessSymbolFiles();
				newFile.setFileName(symbols.getFileName());
				tableData.add(newFile);
			}

			AitessSymbolDetails symboldetails = new AitessSymbolDetails();
			symboldetails.setFileName(symbols.getFileName());
			symboldetails.setMax(symbols.getMax());
			symboldetails.setMin(symbols.getMin());
			symboldetails.setSymbolName(symbols.getSymbolName());
			symboldetails.setSymbolType(symbols.getSymbolType());
			detailsList.add(symboldetails);
		}
		customTableView_symbolFiles = userFactory.createTableView(tableData, true, false);
		customTableView_symbolFiles.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessSymbolFiles> selectedItems = customTableView_symbolFiles.getSelectedItems();
			for (AitessSymbolFiles rowData : selectedItems) {
				try {
					FXMLLoader addStagePopup = new FXMLLoader(
							getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/AitessSymbolPopup.fxml"));
					Parent root = addStagePopup.load();

					
					List<AitessSymbolDetails> fileDetails = detailsList.stream()
							.filter(detail -> detail.getFileName().equals(rowData.getFileName()))
							.collect(Collectors.toList());
					if(fileDetails.size() <2) {
						Notifications.showWarningAlert("No data in selected file");
					}else {
						AitessSymbolPopupController controller =  addStagePopup.getController();
						controller.setSymbolDetails(fileDetails);
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

		customTableView_symbolFiles.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessSymbolFiles> selectedItems = customTableView_symbolFiles.getSelectedItems();
			for (AitessSymbolFiles rowData : selectedItems) {
				Response res = customFileAddManagement.deleteFile(rowData.getFileName(), "symbols");
				if (res.getResponseCode() == 1) {
					tableData.clear();
					setAitessSymbolFilesTableData(RUN_CONFIG_ID);
				} else {
					Notifications.showErrorAlert("File not deleted");
				}
			}
		});
		ofpSymbolFilesTableGridPane.getChildren().clear();
		ofpSymbolFilesTableGridPane.add(customTableView_symbolFiles, 0, 0);
	}

}
class OFPSymbolFilesTableViewFactory implements TableViewFactory<AitessSymbolFiles> {
	@Override
	public CustomTableView<AitessSymbolFiles> createTableView(ObservableList<AitessSymbolFiles> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessSymbolFiles.class, addUserColumn, addCheckboxColumn);
	}
}
