package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.ReportCofigurationManagement;
import com.teclever.dfcc.datastore.dto.ReportConfigDto;
import com.teclever.dfcc.datastore.dto.ReportConfigResponse;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class ReportController {

	private GridPane reportMainGridPane = new GridPane();
	private GridPane reportHeadingGridPane = new GridPane();
	private GridPane reportBottomGridPane = new GridPane();
	private GridPane reportBottomLeftGridPane = new GridPane();
	private GridPane reportBottomRightGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();
	private HBox buttonBox = new HBox();
	private Button downloadButton = new Button("Download");

	private ListView<ReportConfigDto> listView = new ListView<>();;
	private String REPORT_TYPE = null;
	private ObservableList<ReportConfigDto> reportData = FXCollections.observableArrayList();

	private ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();

	public GridPane createReportGridPane(String reportType) {
		if ("PQT REPORT".equals(reportType)) {
			REPORT_TYPE = "PQT";
		} else if ("ESS REPORT".equals(reportType)) {
			REPORT_TYPE = "ESS";
		} else {
			REPORT_TYPE = "DataPack";
		}
		reportMainGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Report.css").toExternalForm());
		reportMainGridPane.getStyleClass().add("report-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(93);

		reportMainGridPane.setPadding(new Insets(5));
		reportMainGridPane.getColumnConstraints().addAll(firstColumn);
		reportMainGridPane.getRowConstraints().addAll(firstRow, secondRow);

		reportMainGridPane.add(createHeadingBox(reportType), 0, 0);
		reportMainGridPane.add(createBottomGridPane(), 0, 1);

		getSavedReportData();

		return reportMainGridPane;
	}

	private GridPane createHeadingBox(String reportType) {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportHeadingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		reportHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText(reportType);
		title.getStyleClass().add("report-title");
		titleBox.getChildren().add(title);

		reportHeadingGridPane.add(titleBox, 0, 0);
		reportHeadingGridPane.add(createDownloadButton(), 1, 0);

		return reportHeadingGridPane;
	}

	private HBox createDownloadButton() {
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().add(downloadButton);

		downloadButton.setOnAction(e -> {
		});

		return buttonBox;
	}

	private GridPane createBottomGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportBottomGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		reportBottomGridPane.getRowConstraints().addAll(firstRow);
		reportBottomGridPane.setHgap(5);

		reportBottomGridPane.add(createLeftGridPane(), 0, 0);
		reportBottomGridPane.add(createRightGridPane(), 1, 0);

		return reportBottomGridPane;
	}

	private GridPane createLeftGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportBottomLeftGridPane.getColumnConstraints().addAll(firstColumn);
		reportBottomLeftGridPane.getRowConstraints().addAll(firstRow);

		ReportTreeviewController reportTreeviewController = new ReportTreeviewController(this);

		reportBottomLeftGridPane.add(reportTreeviewController.createReportTreeView(), 0, 0);
		return reportBottomLeftGridPane;
	}

	private GridPane createRightGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportBottomRightGridPane.getColumnConstraints().addAll(firstColumn);
		reportBottomRightGridPane.getRowConstraints().addAll(firstRow);

		reportBottomRightGridPane.add(createListView(), 0, 0);
		return reportBottomRightGridPane;
	}

	private ListView<ReportConfigDto> createListView() {
		return listView;
	}

	private void updateListView() {
		listView.setItems(reportData);
		
		listView.setCellFactory(lv -> new ListCell<ReportConfigDto>() {
			private final HBox hbox = new HBox();
			private final Text text = new Text();
			private final Button deleteButton = new Button("Remove");
			
			{
				deleteButton.setVisible(false);

				hbox.setAlignment(Pos.CENTER_LEFT);
				HBox.setHgrow(text, Priority.ALWAYS);
				hbox.getChildren().addAll(text, deleteButton);

				setOnMouseEntered(event -> deleteButton.setVisible(true));
				setOnMouseExited(event -> deleteButton.setVisible(false));

				deleteButton.setOnAction(event -> {
					handleDeleteReportData(hbox.getId());
				});
			}
			
			@Override
			protected void updateItem(ReportConfigDto item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					StringBuilder hierarchy = new StringBuilder();

					if(item.getLevelOneName() != null ) {
						hierarchy.append(item.getLevelOneName() +" -> ");
					}
					if(item.getLevelTwoName() != null) {
						hierarchy.append(item.getLevelTwoName() +" -> ");
					}
					if(item.getLevelThreeName() != null) {
						hierarchy.append(item.getLevelThreeName() +" -> ");
					}
					if(item.getLevelFourName() != null) {
						hierarchy.append(item.getLevelFourName() +" -> ");
					}
					if(item.getLevelFiveName() != null) {
						hierarchy.append(item.getLevelFiveName() +" -> ");
					}
								        
					if(item.getFileName() != null) {
						hierarchy.append(item.getFileName());
					}
					
					hbox.setId(item.getReportConfigId());
					text.setText(hierarchy.toString());
					setGraphic(hbox);
				}
			}
		});
		
	}
	
	private void handleDeleteReportData(String id) {
		Response response = reportCofigurationManagement.deleteFileName(id);
		
		if(response.getResponseCode() == 1) {
			getSavedReportData();
		}else if(response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}

	public void updateListViewWithSelectedLabel(String[] idComponents) {
		ReportConfigDto newReportConfig = new ReportConfigDto();
		for (int i = 0; i < idComponents.length; i++) {
			if (i == 0)
				newReportConfig.setLevelOneId(idComponents[i]);
			if (i == 1)
				newReportConfig.setLevelTwoId(idComponents[i]);
			if (i == 2)
				newReportConfig.setLevelThreeId(idComponents[i]);
			if (i == 3)
				newReportConfig.setLevelFourId(idComponents[i]);
			if (i == 4)
				newReportConfig.setLevelFiveId(idComponents[i]);
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a Configuration File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(reportMainGridPane.getScene().getWindow());
		List<String> selectedFilePath = new ArrayList<>();
		if (selectedFiles != null && !selectedFiles.isEmpty()) {
			for (File file : selectedFiles) {
				selectedFilePath.add(file.getAbsolutePath());
			}
			newReportConfig.setFileNameWitFullPath(selectedFilePath);
		}
		newReportConfig.setReportType(REPORT_TYPE);

		handleSaveReportdData(newReportConfig);
	}

	private void handleSaveReportdData(ReportConfigDto newReportConfig) {
		Response response = reportCofigurationManagement.addReportConfig(newReportConfig);
		if (response.getResponseCode() == 1) {
			getSavedReportData();
		} else if (response.getResponseCode() == 0) {
//			Notifications.showErrorAlert(response.getResponse().getResponseMessage());
		}
	}

	private void getSavedReportData() {
		ReportConfigResponse response = reportCofigurationManagement.getAllReportConfig(REPORT_TYPE);
		if (response.getResponse().getResponseCode() == 1) {
			reportData.clear();
			reportData = FXCollections.observableArrayList(response.getListOfReportConfigDto());
			updateListView();
		} else if (response.getResponse().getResponseCode() == 0) {
//			Notifications.showErrorAlert(response.getResponse().getResponseMessage());
		}
	}

}

//package com.teclever.dfcc.Controller.ui;
//
//import java.io.File;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.prefs.Preferences;
//
//import com.teclever.dfcc.DFCCConstant;
//
//import javafx.collections.FXCollections;
//import javafx.collections.MapChangeListener;
//import javafx.collections.ObservableMap;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListCell;
//import javafx.scene.control.ListView;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.RowConstraints;
//import javafx.scene.text.Text;
//import javafx.stage.FileChooser;
//
//public class ReportController {
//
//	private GridPane reportMainGridPane = new GridPane();
//	private GridPane reportHeadingGridPane = new GridPane();
//	private GridPane reportBottomGridPane = new GridPane();
//	private GridPane reportBottomLeftGridPane = new GridPane();
//	private GridPane reportBottomRightGridPane = new GridPane();
//
//	private HBox titleBox = new HBox();
//	private Label title = new Label();
//	private HBox buttonBox = new HBox();
//	private Button downloadButton = new Button("Download");
//
//	private ObservableMap<String, CustomValue> map = FXCollections.observableMap(new LinkedHashMap<>());
//	private ListView<Entry<String, CustomValue>> listView;
//
//	public GridPane createReportGridPane(String reportType) {
//
//		map.addListener((MapChangeListener<String, CustomValue>) change -> {
//			updateListView();
//		});
//
//		reportMainGridPane.getStylesheets().add(getClass()
//				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Report.css").toExternalForm());
//		reportMainGridPane.getStyleClass().add("report-container");
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(93);
//
//		reportMainGridPane.setPadding(new Insets(5));
//		reportMainGridPane.getColumnConstraints().addAll(firstColumn);
//		reportMainGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		reportMainGridPane.add(createHeadingBox(reportType), 0, 0);
//		reportMainGridPane.add(createBottomGridPane(), 0, 1);
//
//		return reportMainGridPane;
//	}
//
//	private GridPane createHeadingBox(String reportType) {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		reportHeadingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		reportHeadingGridPane.getRowConstraints().addAll(firstRow);
//
//		titleBox.setAlignment(Pos.CENTER_LEFT);
//		title.setText(reportType);
//		title.getStyleClass().add("report-title");
//		titleBox.getChildren().add(title);
//
//		reportHeadingGridPane.add(titleBox, 0, 0);
//		reportHeadingGridPane.add(createDownloadButton(), 1, 0);
//
//		return reportHeadingGridPane;
//	}
//
//	private HBox createDownloadButton() {
//		buttonBox.setAlignment(Pos.CENTER_RIGHT);
//		buttonBox.getChildren().add(downloadButton);
//
//		downloadButton.setOnAction(e -> {
//			for (Map.Entry<String, CustomValue> entry : map.entrySet()) {
//			    CustomValue value = entry.getValue();
//			    System.out.println("StageName: " + value.getName() + ", Path: " + value.getPath());
//			}
//
//		});
//
//		return buttonBox;
//	}
//
//	private GridPane createBottomGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		reportBottomGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		reportBottomGridPane.getRowConstraints().addAll(firstRow);
//		reportBottomGridPane.setHgap(5);
//
//		reportBottomGridPane.add(createLeftGridPane(), 0, 0);
//		reportBottomGridPane.add(createRightGridPane(), 1, 0);
//
//		return reportBottomGridPane;
//	}
//
//	private GridPane createLeftGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		reportBottomLeftGridPane.getColumnConstraints().addAll(firstColumn);
//		reportBottomLeftGridPane.getRowConstraints().addAll(firstRow);
//
////		ReportTreeviewController reportTreeviewController = new ReportTreeviewController(this);
//		ReportTreeviewController reportTreeviewController = new ReportTreeviewController();
//
//		reportBottomLeftGridPane.add(reportTreeviewController.createReportTreeView(), 0, 0);
//		return reportBottomLeftGridPane;
//	}
//
//	private GridPane createRightGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		reportBottomRightGridPane.getColumnConstraints().addAll(firstColumn);
//		reportBottomRightGridPane.getRowConstraints().addAll(firstRow);
//
//		reportBottomRightGridPane.add(createListView(), 0, 0);
//		return reportBottomRightGridPane;
//	}
//
//	private ListView<Entry<String, CustomValue>> createListView() {
//		listView = new ListView<>();
//		updateListView();
//		return listView;
//	}
//
//	private void updateListView() {
//		listView.getItems().setAll(map.entrySet());
//
//		listView.setCellFactory(lv -> new ListCell<Map.Entry<String, CustomValue>>() {
//			private final HBox hbox = new HBox();
//			private final Text text = new Text();
//			private final Button deleteButton = new Button("Remove");
//
//			{
//				deleteButton.setVisible(false);
//
//				hbox.setAlignment(Pos.CENTER_LEFT);
//				HBox.setHgrow(text, Priority.ALWAYS);
//				hbox.getChildren().addAll(text, deleteButton);
//
//				setOnMouseEntered(event -> deleteButton.setVisible(true));
//				setOnMouseExited(event -> deleteButton.setVisible(false));
//
//				deleteButton.setOnAction(event -> {
//					Map.Entry<String, CustomValue> entry = getItem();
//					getListView().getItems().remove(entry);
//					map.remove(entry.getKey());
//				});
//			}
//
//			@Override
//			protected void updateItem(Map.Entry<String, CustomValue> entry, boolean empty) {
//				super.updateItem(entry, empty);
//				if (empty || entry == null) {
//					setText(null);
//					setGraphic(null);
//				} else {
//					text.setText(entry.getValue().getFullStagePath() + " " + entry.getValue().getPath());
//					setGraphic(hbox);
//				}
//			}
//		});
//	}
//
//	public void updateListViewWithSelectedLabel(String stageId, String selectedLabelText, String fullStagePath) {
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Select a Configuration File");
//		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
//		File file = fileChooser.showOpenDialog(reportMainGridPane.getScene().getWindow());
//		if (file != null) {
//			String filePath = file.getAbsolutePath();
//			map.put(stageId, new CustomValue(selectedLabelText , filePath, fullStagePath));
//		}
//	}
//
//	public class CustomValue {
//		private String name;
//		private String path;
//		private String fullStagePath;
//
//		public CustomValue(String name, String path ,String fullStagePath) {
//			this.name = name;
//			this.path = path;
//			this.fullStagePath = fullStagePath;
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		public String getPath() {
//			return path;
//		}
//		
//		public String getFullStagePath() {
//			return fullStagePath;
//		}
//
//		@Override
//		public String toString() {
//			return name + " - " + path;
//		}
//	}
//
//}

//package com.teclever.dfcc.Controller.ui;
//
//import com.teclever.dfcc.DFCCConstant;
//
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//
//public class ReportPQTController {
//
//	private GridPane pqtResultMainGridPane = new GridPane();
//	private GridPane pqtResultHeadingGridPane = new GridPane();
//	private GridPane pqtResultBottomGridPane = new GridPane();
//	private GridPane pqtResultBottomLeftGridPane = new GridPane();
//	private GridPane pqtResultBottomRightGridPane = new GridPane();
//	
//    private HBox titleBox = new HBox();
//	private Label title = new Label();
//	private HBox buttonBox = new HBox();
//	private Button downloadButton = new Button("Download");
//
//	public GridPane createPQTResultGridPane(String reportType) {
//		pqtResultMainGridPane.getStylesheets()
//				.add(getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Report.css").toExternalForm());
//		pqtResultMainGridPane.getStyleClass().add("report-container");
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(93);
//
//		pqtResultMainGridPane.setPadding(new Insets(5));
//		pqtResultMainGridPane.getColumnConstraints().addAll(firstColumn);
//		pqtResultMainGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		pqtResultMainGridPane.add(createHeadingBox(reportType), 0, 0);
//		pqtResultMainGridPane.add(createBottomGridPane(), 0, 1);
//
//		return pqtResultMainGridPane;
//	}
//
//	private GridPane createHeadingBox(String reportType) {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		pqtResultHeadingGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
//		pqtResultHeadingGridPane.getRowConstraints().addAll(firstRow);
//	
//		titleBox.setAlignment(Pos.CENTER_LEFT);
//		title.setText(reportType);
//		title.getStyleClass().add("report-title");
//		titleBox.getChildren().add(title);
//		
//		pqtResultHeadingGridPane.add(titleBox, 0, 0);
//		pqtResultHeadingGridPane.add(createDownloadButton(), 1, 0);
//		
//		return pqtResultHeadingGridPane;
//	}
//	
//	private HBox createDownloadButton() {
//		buttonBox.setAlignment(Pos.CENTER_RIGHT);
//		buttonBox.getChildren().add(downloadButton);
//		
//		downloadButton.setOnAction(e ->{
//
//		});
//		
//		return buttonBox;
//	}
//
//	private GridPane createBottomGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		pqtResultBottomGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
//		pqtResultBottomGridPane.getRowConstraints().addAll(firstRow);
//		pqtResultBottomGridPane.setHgap(5);
//		
//		pqtResultBottomGridPane.add(createLeftGridPane(), 0, 0);
//		pqtResultBottomGridPane.add(createRightGridPane(), 1, 0);
//				
//		return pqtResultBottomGridPane;
//	}
//
//	private GridPane createLeftGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		
//		pqtResultBottomLeftGridPane.getColumnConstraints().addAll(firstColumn);
//		pqtResultBottomLeftGridPane.getRowConstraints().addAll(firstRow);
//		
//		ReportTreeviewController reportTreeviewController = new ReportTreeviewController();
//		
//		pqtResultBottomLeftGridPane.add(reportTreeviewController.createReportTreeView(), 0, 0);		
//		return pqtResultBottomLeftGridPane;
//	}
//
//	private GridPane createRightGridPane() {
//		pqtResultBottomRightGridPane.setStyle("-fx-background-color:green;");
//		return pqtResultBottomRightGridPane;
//	}
//
//}
