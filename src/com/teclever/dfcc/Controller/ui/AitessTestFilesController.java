package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.AddCustomFileResponse;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.filemanagement.CustomFileAddManagement;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.model.AitessTestFiles;
import com.teclever.dfcc.utils.AitessConfigHeader;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

public class AitessTestFilesController {
	private GridPane testFilesParentGridPane = new GridPane();
	private GridPane testFilesTitleGridPane = new GridPane();
	private GridPane testFileTableGridPane = new GridPane();

	private AitessConfigHeader configHeader = new AitessConfigHeader("AITESS");
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	private CustomFileAddManagement customFileAddManagement = new CustomFileAddManagement();

	private TableViewFactory<AitessTestFiles> userFactory = new TestFilesTableViewFactory();
	private CustomTableView<AitessTestFiles> customTableView_testFiles;
	private ObservableList<AitessTestFiles> tableData = FXCollections.observableArrayList();

	private String RUN_CONFIG_ID;

	public AitessTestFilesController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				this.RUN_CONFIG_ID = newRunConfigId;
				tableData.clear();
				setAitessTestFilesTableData(newRunConfigId);
			} else {
				tableData.clear();
			}
		});
	}

	public GridPane testFilesConfigParentGrid() {
		testFilesParentGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/AitessTestFiles.css").toExternalForm());
		testFilesParentGridPane.getStyleClass().add("testFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(88);

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
		if(RUN_CONFIG_ID!=null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select File");
//			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.txt"));
			List<File> selectedFiles = fileChooser.showOpenMultipleDialog(testFilesParentGridPane.getScene().getWindow());
			List<String> filePaths = new ArrayList<>();
			if (selectedFiles != null) {
				for (File file : selectedFiles) {
					filePaths.add(file.getAbsolutePath());
				}

				AddCustomFileResponse res = customFileAddManagement.addCustomFiles(RUN_CONFIG_ID, filePaths, "tpf");
				if (res.getResponseCode() == 1) {
					tableData.clear();
					setAitessTestFilesTableData(RUN_CONFIG_ID);
				} else {
					Notifications.showErrorAlert("Files not added");
				}
			}
		}else {
			Notifications.showWarningAlert("Please select UUT Type and Test Type");
		}
		

	}

	private void setAitessTestFilesTableData(String runConfigId) {
		System.out.println("Aitess Run Config Id");
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
				Response res = customFileAddManagement.deleteFile(rowData.getFileName(), "tpf");
				if (res.getResponseCode() == 1) {
					tableData.clear();
					setAitessTestFilesTableData(RUN_CONFIG_ID);
				} else {
					Notifications.showErrorAlert("File not deleted");
				}
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
