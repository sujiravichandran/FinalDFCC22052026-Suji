package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.RunConfigurationManagement;
import com.teclever.dfcc.datastore.dto.DownloadFileDto;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.RunConfigurationDto;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.dto.TestTypeMasterDetailsDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.model.AitessDownloadCode;
import com.teclever.dfcc.model.AitessMacroFiles;
import com.teclever.dfcc.model.AitessMacroFiles.AitessMacroDetails;
import com.teclever.dfcc.model.AitessTestFiles;
import com.teclever.dfcc.utils.AitessConfigHeader;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AitessTestFilesController {
	private GridPane testFilesParentGridPane = new GridPane();
	private GridPane testFilesTitleGridPane = new GridPane();
	private GridPane testFileTableGridPane = new GridPane();

	private AitessConfigHeader configHeader = new AitessConfigHeader("AITESS");
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();

	private TableViewFactory<AitessTestFiles> userFactory = new TestFilesTableViewFactory();
	private CustomTableView<AitessTestFiles> customTableView_testFiles;
	private ObservableList<AitessTestFiles> tableData = FXCollections.observableArrayList();
	
	public AitessTestFilesController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				setAitessTestFilesTableData(newRunConfigId);
			} else {
				tableData.clear();
			}
		});
	}

	public GridPane testFilesConfigParentGrid() {
		testFilesParentGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/AitessTestFiles.css").toExternalForm());
		testFilesParentGridPane.getStyleClass().add("testFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		testFilesParentGridPane.setPadding(new Insets(10));
		testFilesParentGridPane.setVgap(5);

		testFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		testFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		testFilesParentGridPane.add(testFilesTopContainer(), 0, 0);
		testFilesParentGridPane.add(configHeader.aitessMiddleContainer(), 0, 1);
		testFilesParentGridPane.add(createAitessTestFilesTable(), 0, 2);

		return testFilesParentGridPane;
	}

	private GridPane testFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		testFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		testFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		testFilesTitleGridPane.add(headerHbox(), 0, 0);
		testFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return testFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("TEST FILES");
		headerLabel.getStyleClass().add("testFiles-headerLabel");

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

	private GridPane createAitessTestFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		testFileTableGridPane.getColumnConstraints().addAll(firstColumn);
		testFileTableGridPane.getRowConstraints().addAll(firstRow);
		testFileTableGridPane.getStyleClass().add("testFiles-Container");
		
		return testFileTableGridPane;
	}

	private void onClickAddFileButton() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(testFilesParentGridPane.getScene().getWindow());
//		 if (selectedFile != null) {
//	            String filePath = selectedFile.getAbsolutePath();
//	            VDDResponse response = vddManagement.extractingVDDFile(filePath);
//	            if(response.getResponse().getResponseCode()==1) {
//	            	Notifications.showSuccessAlert("File Uploaded Successfully");
//	            	refreshVddConfigList();
//	            }else if(response.getResponse().getResponseCode()==0) {
//	            	Notifications.showSuccessAlert(response.getResponse().getResponseMessage());
//	            }
//	      }	
	}

	private void setAitessTestFilesTableData(String runConfigId) {
		List<TestFileDto> testFileList = testPlanFileManagement.getAllTestFiles(runConfigId);

		for (TestFileDto testFileDto : testFileList) {
			AitessTestFiles testFileData = new AitessTestFiles();
			testFileData.setFileName(testFileDto.getTestFileName());
			tableData.add(testFileData);
		}

		customTableView_testFiles = userFactory.createTableView(tableData, true, false);
		customTableView_testFiles.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessTestFiles> selectedItems = customTableView_testFiles.getSelectedItems();
			for (AitessTestFiles rowData : selectedItems) {
			}
		});
		testFileTableGridPane.add(customTableView_testFiles, 0, 0);
	}
}

class TestFilesTableViewFactory implements TableViewFactory<AitessTestFiles> {
	@Override
	public CustomTableView<AitessTestFiles> createTableView(ObservableList<AitessTestFiles> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessTestFiles.class, addUserColumn, addCheckboxColumn);
	}
}
