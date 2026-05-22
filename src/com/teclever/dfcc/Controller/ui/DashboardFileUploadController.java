package com.teclever.dfcc.Controller.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.DashBoardFilesUploading;
import com.teclever.datastore.service.DashBoardFilesUploadServices;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.dashboard.DashboardManagement;
import com.teclever.dfcc.model.DashboardFilesUpload;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

class DashBoardFilesUploadTableViewFactory implements TableViewFactory<DashboardFilesUpload> {
	@Override
	public CustomTableView<DashboardFilesUpload> createTableView(ObservableList<DashboardFilesUpload> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, DashboardFilesUpload.class, addUserColumn, addCheckboxColumn);
	}
}

public class DashboardFileUploadController {
	private String currentUut;
	private String currentSNo;

	public DashboardFileUploadController() {
		currentUut = StateMachine.currentSessionDetails.getUutId();
		currentSNo = StateMachine.currentSessionDetails.getDfccSerialNumber();
		setDashboardFilesUploadTableData();

	}

	private GridPane dashboardFilesUploadParentGridPane = new GridPane();
	private GridPane fileUploadTableGridPane = new GridPane();

	private TableViewFactory<DashboardFilesUpload> fileUploadFactory = new DashBoardFilesUploadTableViewFactory();
	private CustomTableView<DashboardFilesUpload> fileUploadTableView;
	private ObservableList<DashboardFilesUpload> fileUploadTableData = FXCollections.observableArrayList();
	private Stage parentStage;

	private DashboardManagement dashBoardFilesUploadManagement = new DashboardManagement();

	public GridPane dashboardFilesUploadConfigParentGrid() {
		dashboardFilesUploadParentGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/AitessTestFiles.css")
						.toExternalForm());
		dashboardFilesUploadParentGridPane.getStyleClass().add("testFiles-parent-container");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);

		dashboardFilesUploadParentGridPane.setPadding(new Insets(10));
		dashboardFilesUploadParentGridPane.setVgap(5);

		dashboardFilesUploadParentGridPane.getColumnConstraints().addAll(firstColumn);
		dashboardFilesUploadParentGridPane.getRowConstraints().addAll(firstRow, secondRow);

		dashboardFilesUploadParentGridPane.add(createButtonHbox(), 0, 0);
		dashboardFilesUploadParentGridPane.add(fileUploadTableGridPane, 0, 1);
//		dashboardFilesUploadParentGridPane.add(closeButtonHbox(), 0, 2);

		return dashboardFilesUploadParentGridPane;
	}

	private void setDashboardFilesUploadTableData() {

		fileUploadTableData.clear();

		List<DashBoardFilesUploading> dashBoardFilesUploadingList = dashBoardFilesUploadManagement
				.getDashboardFiles(currentUut, currentSNo);

		if (dashBoardFilesUploadingList != null && !dashBoardFilesUploadingList.isEmpty()) {

			for (DashBoardFilesUploading dashBoardFilesUploading : dashBoardFilesUploadingList) {

				DashboardFilesUpload dashboardFilesUpload = new DashboardFilesUpload();

				dashboardFilesUpload.setsNo(dashBoardFilesUploading.getSerialNo());

				dashboardFilesUpload.setFileName(dashBoardFilesUploading.getFileDetails());

				fileUploadTableData.add(dashboardFilesUpload);
			}
		}

		fileUploadTableView = fileUploadFactory.createTableView(fileUploadTableData, true, false);

		fileUploadTableView.getColumns().forEach(column -> {
			String colNamne = column.getText();
//			////System.out.println(colNamne);
			switch (colNamne) {
			case "FILE NAME":
				column.setMinWidth(800);
				column.setMaxWidth(800);
				break;
			case "":
				column.setMinWidth(200);
				column.setMaxWidth(200);
				break;

			default:
//				column.setMinWidth(120);
//				column.setMaxWidth(120);
				break;
			}
			
			GridPane.setVgrow(fileUploadTableView, Priority.ALWAYS);
			GridPane.setHgrow(fileUploadTableView, Priority.ALWAYS);

		});

		fileUploadTableView.getColumns().removeIf(col -> col.getText().equalsIgnoreCase("UUT ID") ||col.getText().equalsIgnoreCase("S NO") );

		TableColumn<DashboardFilesUpload, Void> viewColumn = new TableColumn<>("View");

		viewColumn.setCellFactory(col -> new TableCell<DashboardFilesUpload, Void>() {

			private final Button viewButton = new Button("View");

			{
				viewButton.setOnAction(event -> {

					DashboardFilesUpload rowData = getTableRow().getItem();

					if (rowData != null) {
						openUploadedFile(rowData.getFileName());
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : viewButton);
			}
		});

		fileUploadTableView.addEventHandler(

				CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {

					DashboardFilesUpload selectedRow = fileUploadTableView.getSelectionModel().getSelectedItem();

					if (selectedRow != null) {
						handleDeleteFile(selectedRow);
					}
				});

		viewColumn.setPrefWidth(200);
		fileUploadTableView.getColumns().add(viewColumn);
		
		


		fileUploadTableGridPane.add(fileUploadTableView, 0, 0);
	}

	private void handleDeleteFile(DashboardFilesUpload selectedRow) {

		if (selectedRow == null) {
			return;
		}

		String fileDetails = selectedRow.getFileName();

		DashBoardFilesUploadServices service = new DashBoardFilesUploadServices();

		Response response = service.deleteFileUpload(currentUut, currentSNo, fileDetails);

		if (response.getResponseCode() == 1) {
			// remove from UI table
			fileUploadTableData.remove(selectedRow);
		} else {
			////System.out.println(response.getResponseMessage());
		}
	}

	private void openUploadedFile(String filePath) {
		try {
			File file = new File(filePath);

			if (file.exists()) {
				try {
					String os = System.getProperty("os.name").toLowerCase();
					if (os.contains("win")) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.OPEN)) {
							desktop.open(file);
						} else {
							Debug.printDebug("Open action not supported on this platform.");
						}
					} else if (os.contains("nix") || os.contains("nux")) {
						File absoluteFile = file.isAbsolute() ? file : file.getAbsoluteFile();
						new ProcessBuilder("xdg-open", absoluteFile.getAbsolutePath()).start();
					} else {
						Debug.printDebug("Unsupported OS: " + os);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					Debug.printDebug("Error while opening file: " + e1.getMessage());
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HBox createButtonHbox() {

		Button addFileButton = new Button("ADD FILES");
		addFileButton.setOnAction(e -> onClickAddFileButton());

		HBox headerButtonHbox = new HBox(10);

		headerButtonHbox.setAlignment(Pos.CENTER_RIGHT);
		headerButtonHbox.getChildren().add(addFileButton);
		return headerButtonHbox;
	}

	private void onClickAddFileButton() {

		currentUut = StateMachine.currentSessionDetails.getUutId();
		currentSNo = StateMachine.currentSessionDetails.getDfccSerialNumber();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

		List<File> selectedFiles = fileChooser
				.showOpenMultipleDialog(dashboardFilesUploadParentGridPane.getScene().getWindow());

		if (selectedFiles == null || selectedFiles.isEmpty()) {
			return;
		}

		for (File file : selectedFiles) {

			DashBoardFilesUploading dbObj = new DashBoardFilesUploading();
			dbObj.setUutType(currentUut);
			dbObj.setSerialNo(currentSNo);
			dbObj.setFileDetails(file.getAbsolutePath());
//	        Response response = dashBoardFilesUploadManagement.addingBuildConfiguration(currentUut,currentSNo,file.getAbsolutePath());
			Response response = dashBoardFilesUploadManagement.addDashboardFile(currentUut, currentSNo,
					file.getAbsolutePath());

			if (response.getResponseCode() != 1) {
				////System.out.println("Failed to upload file: " + file.getName());
			}
		}

		// Refresh table after upload
		setDashboardFilesUploadTableData();
	}

	private HBox closeButtonHbox() {

		Button close = new Button("Close");

		HBox closeButtonHbox = new HBox(close);
		closeButtonHbox.setAlignment(Pos.CENTER);
		closeButtonHbox.setPadding(new Insets(10));

		close.setOnAction(e -> {

			StateMachine.setDashboardBuildConfig(false);

			Stage popupStage = (Stage) close.getScene().getWindow();
			popupStage.close();
//	        headingGridPane.getChildren().clear();   
//	        buildConfigurationMidContainerGridPane.getChildren().clear();
//	        topThirdGridPane.getChildren().clear();
//	        topSecondGridPane.getChildren().clear();
//	        topThirdGridPane.getChildren().clear();
//            dataAnalysisBuildConfigurationMainContainerGridPane
//                    .getChildren().clear();

			if (parentStage != null) {
				parentStage.close();

			}
		});

		return closeButtonHbox;
	}

}
