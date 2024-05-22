package com.teclever.dfcc.Controller.ui;

import java.util.List;

import com.teclever.dfcc.datastore.dto.DownloadFileDto;
import com.teclever.dfcc.datastore.filemanagement.DownloadFileManagement;
import com.teclever.dfcc.model.AitessDownloadCode;
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

public class OFPDownloadCodeController {
	
	private GridPane ofpDownloadCodeParentGridPane = new GridPane();
	private GridPane ofpDownloadCodeTitleGridPane = new GridPane();
	private GridPane ofpDownloadCodeTableGridPane = new GridPane();
	
	private DownloadFileManagement downloadFileManagement = new DownloadFileManagement();
	private AitessConfigHeader configHeader = new AitessConfigHeader("OFP");
	
	private TableViewFactory<AitessDownloadCode> userFactory = new OFPDownloadCodeTableViewFactory();
	private CustomTableView<AitessDownloadCode> customTableView_downloadCode;
	private ObservableList<AitessDownloadCode> tableData = FXCollections.observableArrayList();
	
	public OFPDownloadCodeController() {
		configHeader.runConfigIdProperty().addListener((obs, oldRunConfigId, newRunConfigId) -> {
			if (newRunConfigId != null) {
				tableData.clear();
				setAitessDownloadCodeTableData(newRunConfigId);
			} else {
				tableData.clear();
			}
		});
	}
	
	public GridPane ofpDownloadCodeParentGrid() {
		ofpDownloadCodeParentGridPane.getStylesheets()
		.add(getClass().getResource("/com/teclever/dfcc/ui/css/OFPDownloadCode.css").toExternalForm());
		ofpDownloadCodeParentGridPane.getStyleClass().add("ofpDownloadCode-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

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
//		addFileButton.setOnAction(e -> onClickAddFileButton());
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
	private void setAitessDownloadCodeTableData(String runConfigId) {
		List<DownloadFileDto> doenloadFileDtoList = downloadFileManagement.getAllDownloadFiles(runConfigId);

		for (DownloadFileDto downloadFileDto : doenloadFileDtoList) {
			AitessDownloadCode downloadCodeData = new AitessDownloadCode();
			downloadCodeData.setFileName(downloadFileDto.getDownloadFileName());
			tableData.add(downloadCodeData);
		}
		customTableView_downloadCode = userFactory.createTableView(tableData, true, false);

		customTableView_downloadCode.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<AitessDownloadCode> selectedItems = customTableView_downloadCode.getSelectedItems();
			for (AitessDownloadCode rowData : selectedItems) {
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

