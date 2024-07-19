package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.AddCustomFileResponse;
import com.teclever.dfcc.datastore.dto.DownloadFileDto;
import com.teclever.dfcc.datastore.filemanagement.CustomFileAddManagement;
import com.teclever.dfcc.datastore.filemanagement.DownloadFileManagement;
import com.teclever.dfcc.model.AitessDownloadCode;
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

public class OFPDownloadCodeController {
	
	private GridPane ofpDownloadCodeParentGridPane = new GridPane();
	private GridPane ofpDownloadCodeTitleGridPane = new GridPane();
	private GridPane ofpDownloadCodeTableGridPane = new GridPane();
	
	private DownloadFileManagement downloadFileManagement = new DownloadFileManagement();
	private AitessConfigHeader configHeader = new AitessConfigHeader("OFP");
	private CustomFileAddManagement customFileAddManagement = new CustomFileAddManagement();

	private TableViewFactory<AitessDownloadCode> userFactory = new OFPDownloadCodeTableViewFactory();
	private CustomTableView<AitessDownloadCode> customTableView_downloadCode;
	private ObservableList<AitessDownloadCode> tableData = FXCollections.observableArrayList();
	private String RUN_CONFIG_ID;
	
	public OFPDownloadCodeController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				this.RUN_CONFIG_ID = newRunConfigId;
				tableData.clear();
				setAitessDownloadCodeTableData(newRunConfigId);
			} else {
				this.RUN_CONFIG_ID = newRunConfigId;
				tableData.clear();
			}
		});
	}
	
	public GridPane ofpDownloadCodeParentGrid() {
		ofpDownloadCodeParentGridPane.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/OFPDownloadCode.css").toExternalForm());
		ofpDownloadCodeParentGridPane.getStyleClass().add("ofpDownloadCode-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(88);

		ofpDownloadCodeParentGridPane.setPadding(new Insets(10));
		ofpDownloadCodeParentGridPane.setVgap(5);

		ofpDownloadCodeParentGridPane.getColumnConstraints().addAll(firstColumn);
		ofpDownloadCodeParentGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		ofpDownloadCodeParentGridPane.add(ofpTestFilesTopContainer(), 0, 0);
		ofpDownloadCodeParentGridPane.add(configHeader.aitessMiddleContainer(), 0, 1);
		ofpDownloadCodeParentGridPane.add(createOFPDownloadCodeTable(), 0, 2);

		return ofpDownloadCodeParentGridPane;
	}



	private GridPane ofpTestFilesTopContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpDownloadCodeTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		ofpDownloadCodeTitleGridPane.getRowConstraints().addAll(firstRow);

		ofpDownloadCodeTitleGridPane.add(headerHbox(), 0, 0);
		ofpDownloadCodeTitleGridPane.add(createButtonHbox(), 1, 0);

		return ofpDownloadCodeTitleGridPane;
	}

	private HBox headerHbox() {
		Label headerLabel = new Label("DOWNLOAD CODE");
		headerLabel.getStyleClass().add("ofpDownloadCode-headerLabel");

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
	
	


	private GridPane createOFPDownloadCodeTable() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		ofpDownloadCodeTableGridPane.getColumnConstraints().addAll(firstColumn);
		ofpDownloadCodeTableGridPane.getRowConstraints().addAll(firstRow);
		ofpDownloadCodeTableGridPane.getStyleClass().add("ofpDownloadCode-container");
		return ofpDownloadCodeTableGridPane;
	
	}
	private void onClickAddFileButton() {
		if(RUN_CONFIG_ID!=null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select File");
//			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.txt"));
			List<File> selectedFiles = fileChooser.showOpenMultipleDialog(ofpDownloadCodeParentGridPane.getScene().getWindow());
			List<String> filePaths = new ArrayList<>();
			if (selectedFiles != null) {
				for (File file : selectedFiles) {
					filePaths.add(file.getAbsolutePath());
				}

				AddCustomFileResponse res = customFileAddManagement.addCustomFiles(RUN_CONFIG_ID, filePaths, "download");
				if (res.getResponseCode() == 1) {
					tableData.clear();
					setAitessDownloadCodeTableData(RUN_CONFIG_ID);
				} else {
					Notifications.showErrorAlert("Files not added");
				}
			}
		}else {
			Notifications.showWarningAlert("Please select UUT Type and OFP Name");
		}

}
	private void setAitessDownloadCodeTableData(String runConfigId) {
		List<DownloadFileDto> downloadFileDtoList = downloadFileManagement.getAllDownloadFiles(runConfigId);

		for (DownloadFileDto downloadFileDto : downloadFileDtoList) {
			AitessDownloadCode downloadCodeData = new AitessDownloadCode();
			downloadCodeData.setFileName(downloadFileDto.getDownloadFileName());
			tableData.add(downloadCodeData);
		}
		customTableView_downloadCode = userFactory.createTableView(tableData, true, false);

		customTableView_downloadCode.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessDownloadCode> selectedItems = customTableView_downloadCode.getSelectedItems();
			for (AitessDownloadCode rowData : selectedItems) {
				Response res = customFileAddManagement.deleteFile(rowData.getFileName(), "download");
				if (res.getResponseCode() == 1) {
					tableData.clear();
					setAitessDownloadCodeTableData(RUN_CONFIG_ID);
				} else {
					Notifications.showErrorAlert("File not deleted");
				}
			}
		});
		ofpDownloadCodeTableGridPane.add(customTableView_downloadCode, 0, 0);
	}

}
class OFPDownloadCodeTableViewFactory implements TableViewFactory<AitessDownloadCode> {
	@Override
	public CustomTableView<AitessDownloadCode> createTableView(ObservableList<AitessDownloadCode> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AitessDownloadCode.class, addUserColumn, addCheckboxColumn);
	}
}

