package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.model.AitessTestFiles;
import com.teclever.dfcc.utils.AitessConfigHeader;
import com.teclever.dfcc.utils.CustomTableView;
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

public class OFPTestFilesController {
	
	private GridPane ofpTestFilesParentGridPane = new GridPane();
	private GridPane ofpTestFilesTitleGridPane = new GridPane();
	private GridPane ofpTestFilesTableGridPane = new GridPane();
	
	private TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	private AitessConfigHeader configHeader = new AitessConfigHeader("OFP");
	
	private TableViewFactory<AitessTestFiles> userFactory = new OFPTestFilesTableViewFactory();
	private CustomTableView<AitessTestFiles> customTableView_testFiles;
	private ObservableList<AitessTestFiles> tableData = FXCollections.observableArrayList();
	
	public OFPTestFilesController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				tableData.clear();
				setAitessTestFilesTableData(newRunConfigId);
			} else {
				tableData.clear();
			}
		});
	}
	
	public GridPane ofpTestFileParentGrid() {
		ofpTestFilesParentGridPane.getStylesheets()
		.add(getClass().getResource("/com/teclever/dfcc/ui/css/OFPTestFiles.css").toExternalForm());
		ofpTestFilesParentGridPane.getStyleClass().add("ofpTestFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		ofpTestFilesParentGridPane.setPadding(new Insets(10));
		ofpTestFilesParentGridPane.setVgap(5);

		ofpTestFilesParentGridPane.getColumnConstraints().addAll(firstColumn);
		ofpTestFilesParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		ofpTestFilesParentGridPane.add(ofpTestFilesTopContainer(), 0, 0);
		ofpTestFilesParentGridPane.add(configHeader.aitessMiddleContainer(), 0, 1);
		ofpTestFilesParentGridPane.add(createOFPTestFilesTable(), 0, 2);

		return ofpTestFilesParentGridPane;
	}



	private GridPane ofpTestFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpTestFilesTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		ofpTestFilesTitleGridPane.getRowConstraints().addAll(firstRow);

		ofpTestFilesTitleGridPane.add(headerHbox(), 0, 0);
		ofpTestFilesTitleGridPane.add(createButtonHbox(), 1, 0);

		return ofpTestFilesTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("TEST FILES");
		headerLabel.getStyleClass().add("ofpTestFiles-headerLabel");

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
	
	


	private GridPane createOFPTestFilesTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpTestFilesTableGridPane.getColumnConstraints().addAll(firstColumn);
		ofpTestFilesTableGridPane.getRowConstraints().addAll(firstRow);
		ofpTestFilesTableGridPane.getStyleClass().add("ofpTestFiles-container");
		return ofpTestFilesTableGridPane;
	
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
		ofpTestFilesTableGridPane.add(customTableView_testFiles, 0, 0);
	}

}
class OFPTestFilesTableViewFactory implements TableViewFactory<AitessTestFiles> {
	@Override
	public CustomTableView<AitessTestFiles> createTableView(ObservableList<AitessTestFiles> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessTestFiles.class, addUserColumn, addCheckboxColumn);
	}
}
