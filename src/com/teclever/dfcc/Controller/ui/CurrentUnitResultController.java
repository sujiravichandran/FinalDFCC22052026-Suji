package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.UnitData;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

class UnitDataTableViewFactory implements TableViewFactory<UnitData> {
	@Override
	public CustomTableView<UnitData> createTableView(ObservableList<UnitData> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, UnitData.class, addUserColumn, addCheckboxColumn);
	}
}

public class CurrentUnitResultController {

	private GridPane currentUnitResultGridPane = new GridPane();
	private GridPane currentUnitResultHeadingGridPane = new GridPane();
	private GridPane currentUnitResultOptionGridPane = new GridPane();
	private GridPane currentUnitResultTableGridPane = new GridPane();

	private HBox titleBox = new HBox();
	private Label title = new Label();

	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	private ScrollPane tableScrollPane = new ScrollPane();

	private ObservableList<UnitData> unitDataList = FXCollections.observableArrayList();

	private TableViewFactory<UnitData> unitDataFactory = new UnitDataTableViewFactory();
	private CustomTableView<UnitData> unitDataTableView;

	private ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();

	public CurrentUnitResultController() {
    	getCurrentUnitResultData(currentSessionDetails.getUutId());
	}

	public GridPane createcurrentUnitResultGridPane() {
		currentUnitResultGridPane.getStylesheets()
				.add(getClass()
						.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/CurrentExecutionResults.css")
						.toExternalForm());
		currentUnitResultGridPane.getStyleClass().add("current-execution-result-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(86);

		currentUnitResultGridPane.setVgap(5);
		currentUnitResultGridPane.setPadding(new Insets(5));
		currentUnitResultGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

		currentUnitResultGridPane.add(createHeadingBox(), 0, 0);
		currentUnitResultGridPane.add(createComboBoxGridPane(), 0, 1);
		currentUnitResultGridPane.add(createcurrentUnitResultTableGridPane(), 0, 2);
		
		initializeUUTTypeComboBox();
		
		return currentUnitResultGridPane;
	}

	private void getCurrentUnitResultData(String uutId) {
		unitDataList.clear();
		ResultUnitSessionDetailsResponse response = resultExecutionManagement.getSessionDetailsForResultsByUnit(uutId);
		if (response.getCode() == 1 && response.getResultUnitSessionDetailsDTOList() != null) {
			int i = 1;
			for (ResultUnitSessionDetailsDTO data : response.getResultUnitSessionDetailsDTOList()) {
				UnitData newUnitData = new UnitData();

				newUnitData.setId(data.getSessionId());
				newUnitData.setSlNo(String.valueOf(i));
				newUnitData.setSessionType(data.getSessionType());
				newUnitData.setSessionName(data.getSessionName());
				newUnitData.setStartTime(data.getStartTime());
				newUnitData.setEndTime(data.getEndTime());
				newUnitData.setSessionStatus(data.getSessionStatus());
				newUnitData.setSessionResult(data.getSessionResults());
				newUnitData.setStartRemarks(data.getStratRemarks());
				newUnitData.setEndRemarks(data.getEndRemarks());
				i++;
				unitDataList.add(newUnitData);
			}
		} else if (response.getCode() == 0) {
			Notifications.showErrorAlert(response.getMsg());
		}
		
		tableScrollPane.setFitToWidth(unitDataList.size() == 0);
	}

	private GridPane createHeadingBox() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		currentUnitResultHeadingGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultHeadingGridPane.getRowConstraints().addAll(firstRow);

		titleBox.setAlignment(Pos.CENTER_LEFT);
		title.setText("UNIT RESULTS");
		title.getStyleClass().add("current-execution-result-title");
		titleBox.getChildren().add(title);

		currentUnitResultHeadingGridPane.add(titleBox, 0, 0);

		return currentUnitResultHeadingGridPane;
	}

	private GridPane createComboBoxGridPane() {
		currentUnitResultOptionGridPane.getStyleClass().add("current-execution-result-tabs-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(20);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		currentUnitResultOptionGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultOptionGridPane.getRowConstraints().addAll(firstRow);

		currentUnitResultOptionGridPane.add(createUutBox(), 0, 0);

		return currentUnitResultOptionGridPane;
	}

	private HBox createUutBox() {
		uutTypeField.setPromptText("UUT TYPE");

        HBox uutTypeHBox = new HBox(10);
        uutTypeHBox.setAlignment(Pos.CENTER);
        uutTypeHBox.getChildren().add(uutTypeField);

        return uutTypeHBox;
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(aitessConfig.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeField.setItems(uutTypeList);
		uutTypeField.setValue(currentSessionDetails.getUutType());

		uutTypeField.setOnAction((event) -> {
			UUT_ID = fetchUutId(uutTypeField.getValue());
			if (UUT_ID != null) {
				getCurrentUnitResultData(UUT_ID);
			}
		});
	}
	
	private String fetchUutId(String uutType) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutType().equals(uutType)) {
				return uut.getUutId();
			}
		}
		return null;
	}

	private GridPane createcurrentUnitResultTableGridPane() {
		currentUnitResultTableGridPane.getStyleClass().add("current-execution-result-tabs-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		currentUnitResultTableGridPane.setPadding(new Insets(5));

		currentUnitResultTableGridPane.getColumnConstraints().addAll(firstColumn);
		currentUnitResultTableGridPane.getRowConstraints().addAll(firstRow);

		currentUnitResultTableGridPane.add(createCurrentUnitResultTable(), 0, 0);
		return currentUnitResultTableGridPane;
	}

	private ScrollPane createCurrentUnitResultTable() {

		unitDataTableView = unitDataFactory.createTableView(unitDataList, true, false);

		unitDataTableView.getColumns().forEach(column -> {
			if (!column.getText().isEmpty()) {
				column.setMinWidth(column.getText().length() * 12);
//				updateUnitData((TableColumn<UnitData, String>) column);
			}
		});

		unitDataTableView.addEventHandler(CustomTableView.VIEW_BUTTON_CLICKED_EVENT, event -> {
			ObservableList<UnitData> selectedItems = unitDataTableView.getSelectedItems();
			for (UnitData rowData : selectedItems) {
				UserCenterContentController userCenterContentController = UserCenterContentController.getInstance();

				GridPane bottomMidTopGridPane = (GridPane) currentUnitResultGridPane.getParent().getParent()
						.getParent();
				userCenterContentController.createUserCenterContent(bottomMidTopGridPane, "Session Results",
						rowData.getId(), null);
				break;
			}
		});

		tableScrollPane.setContent(unitDataTableView);
		tableScrollPane.setFitToHeight(true);
		return tableScrollPane;
	}

//	private void updateUnitData(TableColumn<UnitData, String> column) {
//		column.setCellFactory(col -> new TableCell<UnitData, String>() {
//			private Label label;
//
//			@Override
//			protected void updateItem(String item, boolean empty) {
//				super.updateItem(item, empty);
//				if (item == null || empty) {
//					setText(null);
//					setGraphic(null);
//				} else {
//					if (label == null) {
//						label = new Label();
//						label.setWrapText(false);
//						label.setAlignment(Pos.CENTER);
//						setStyle("-fx-alignment: CENTER;");
//						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//					}
//					label.setText(item);
//					label.setStyle("-fx-text-fill: white;");
//					label.setMinWidth(label.getText().length() * 12);
//					setGraphic(label);
//					this.setMinWidth(label.getText().length() * 12);
//					col.setMinWidth(Math.max(col.getMinWidth(), label.getMinWidth()));
//				}
//			}
//		});
//	}

}
