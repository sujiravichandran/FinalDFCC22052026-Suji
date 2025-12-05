package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.itextpdf.text.DocumentException;
import com.teclever.datastore.entities.BuildConfiguration;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.buildconfiguration.BuildConfigurationManagement;
import com.teclever.dfcc.buildconfiguration.BuildConfigurationReport;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.model.BriefData;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

class BuildConfiguration02DataTableViewFactory implements TableViewFactory<BuildConfigurationPopup2.BuildConfiguration1> {
	@Override
	public CustomTableView<BuildConfigurationPopup2.BuildConfiguration1> createTableView(
			ObservableList<BuildConfigurationPopup2.BuildConfiguration1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {

		return new CustomTableView<>(items, BuildConfigurationPopup2.BuildConfiguration1.class, addUserColumn,
				addCheckboxColumn);
	}
}

class BuildConfiguration12DataTableViewFactory implements TableViewFactory<BuildConfigurationPopup2.BuildConfiguration1> {
	@Override
	public CustomTableView<BuildConfigurationPopup2.BuildConfiguration1> createTableView(
			ObservableList<BuildConfigurationPopup2.BuildConfiguration1> items, boolean addUserColumn,
			boolean addCheckboxColumn) {

		return new CustomTableView<>(items, BuildConfigurationPopup2.BuildConfiguration1.class, addUserColumn,
				addCheckboxColumn);
	}
}

public class BuildConfigurationPopup2 {

	private StackPane buildConfigStackPane = new StackPane();
	private ObservableList<BuildConfiguration1> buildConfigDataList = FXCollections.observableArrayList();
	private ObservableList<BuildConfiguration1> buildConfigDataList1 = FXCollections.observableArrayList();
	private CustomTableView<BuildConfiguration1> buildConfigDataTableView;
	private TableViewFactory<BuildConfiguration1> buildDataFactory = new BuildConfiguration02DataTableViewFactory();

	private CustomTableView<BuildConfiguration1> buildConfigDataTableView1;
	private TableViewFactory<BuildConfiguration1> buildDataFactory1 = new BuildConfiguration12DataTableViewFactory();

	private Label serialNo = new Label("Serial No: ");
	private Label serialNo2 = new Label();
	private Label versionNo = new Label("Version No: ");
	private TextField versionNo2 = new TextField();
	private Label dateLabel = new Label("Date: ");
	private DatePicker datePicker = new DatePicker();

	private Button close = new Button("Close");
	private Button save = new Button("Svae/Print");
	private HBox closeButtonHbox = new HBox(10);
	private HBox saveButtonHbox = new HBox(10);
	private GridPane closeButtonGridPane = new GridPane();

	private GridPane topGridPane = new GridPane();
	private HBox serialNoHbox = new HBox(10);
	private HBox versionNoHbox = new HBox(10);
	private HBox dateHbox = new HBox(10);
	private String date;
	private Stage parentStage;

	private BuildConfigurationManagement buildConfigurationManagement = new BuildConfigurationManagement();

	public static class BuildConfiguration1 {

		private final StringProperty channel = new SimpleStringProperty();
		private final StringProperty description = new SimpleStringProperty();
		private final StringProperty partNumber = new SimpleStringProperty();
		private final StringProperty serialNumber = new SimpleStringProperty();

		public StringProperty channelProperty() {
			return channel;
		}

		public String getChannel() {
			return channel.get();
		}

		public void setChannel(String v) {
			channel.set(v);
		}

		public StringProperty descriptionProperty() {
			return description;
		}

		public String getDescription() {
			return description.get();
		}

		public void setDescription(String v) {
			description.set(v);
		}

		public StringProperty partNumberProperty() {
			return partNumber;
		}

		public String getPartNumber() {
			return partNumber.get();
		}

		public void setPartNumber(String v) {
			partNumber.set(v);
		}

		public StringProperty serialNumberProperty() {
			return serialNumber;
		}

		public String getSerialNumber() {
			return serialNumber.get();
		}

		public void setSerialNumber(String v) {
			serialNumber.set(v);
		}
	}

	public GridPane createBuildConfigurationMainPopupContainerGridPane() {
		GridPane mainPane = new GridPane();
		mainPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataAnalysis.css").toExternalForm());
		mainPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		mainPane.setHgap(10);
		mainPane.setVgap(10);

		mainPane.getColumnConstraints().addAll(firstColumn);
		mainPane.getRowConstraints().addAll(firstRow);
//		dataAnalysisBuildConfigurationMainContainerGridPane.setPadding(new Insets(10, 10, 10, 10));

//		dataAnalysisBuildConfigurationMainContainerGridPane.add(topGridPane(), 0, 0);
		mainPane.add(createBuildConfigContent(), 0, 0);

		return mainPane;
	}

	private StackPane createBuildConfigContent() {
		buildConfigStackPane.getStyleClass().add("tab-content-container");
		buildConfigStackPane.getChildren().clear();
		if (!DFCCConstant.selectedFetchBuildConfig.equalsIgnoreCase("Fetch")) {
			buildConfigStackPane.getChildren().add(createBuildConfigDataTable());
		} else {
			buildConfigStackPane.getChildren().add(createBuildConfigurationDataTable());
		}

		return buildConfigStackPane;
	}

//	public GridPane topGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(20);
//
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(20);
//
//		ColumnConstraints thirdColumn = new ColumnConstraints();
//		thirdColumn.setPercentWidth(20);
//		
//		ColumnConstraints fourthColumn = new ColumnConstraints();
//		fourthColumn.setPercentWidth(20);
//		
//		ColumnConstraints fivthColumn = new ColumnConstraints();
//		fivthColumn.setPercentWidth(20);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//		topGridPane.getStyleClass().add("dataanalysis-testing-right-container");
//
//		topGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn, fivthColumn);
//		topGridPane.getRowConstraints().add(firstRow);
//
//		topGridPane.add(serialNoHbox(), 0, 0);
//		topGridPane.add(versionNoHbox(), 1, 0);
//		topGridPane.add(dateHbox(), 2, 0);
//		topGridPane.add(saveButtonHbox(), 3, 0);
//		topGridPane.add(closeButtonHbox(), 4, 0);
//
//		return topGridPane;
//
//	}

	private HBox serialNoHbox() {

		serialNoHbox.setAlignment(Pos.CENTER);
		serialNo2.setText(DFCCConstant.selectedSNoBuildConfig);
		serialNoHbox.getChildren().addAll(serialNo, serialNo2);

		return serialNoHbox;
	}

	private HBox versionNoHbox() {
		versionNoHbox.setAlignment(Pos.CENTER);
		versionNoHbox.setPadding(new Insets(20));
		versionNoHbox.getChildren().addAll(versionNo, versionNo2);

		return versionNoHbox;
	}

	private HBox dateHbox() {
		dateHbox.setAlignment(Pos.CENTER);
		dateHbox.setPadding(new Insets(20));
		dateHbox.getChildren().addAll(dateLabel, datePicker);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		datePicker.setPromptText("Select Date");

		datePicker.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate date) {
				return (date != null) ? formatter.format(date) : "";
			}

			@Override
			public LocalDate fromString(String string) {
				if (string == null || string.trim().isEmpty())
					return null;
				return LocalDate.parse(string, formatter);
			}
		});

		datePicker.setDayCellFactory(dp -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				if (date != null && date.isAfter(LocalDate.now())) {
					setDisable(true);
				}
			}
		});

		return dateHbox;
	}

	public GridPane closeButtonGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		closeButtonGridPane.getStyleClass().add("dataanalysis-testing-right-container2");

		closeButtonGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		closeButtonGridPane.getRowConstraints().add(firstRow);

		closeButtonGridPane.add(saveButtonHbox(), 0, 0);
		closeButtonGridPane.add(closeButtonHbox(), 1, 0);

		return closeButtonGridPane;

	}

	private HBox closeButtonHbox() {
		HBox closeButtonHbox = new HBox();
		closeButtonHbox.setAlignment(Pos.CENTER_LEFT);
		closeButtonHbox.getChildren().add(close);
		closeButtonHbox.setPadding(new Insets(10));

		close.setOnAction(e -> {

			Stage popupStage = (Stage) close.getScene().getWindow();
			popupStage.close();

			if (parentStage != null) {
				parentStage.close();
			}
		});

		return closeButtonHbox;
	}

	private ScrollPane createBuildConfigDataTable() {

		buildConfigDataList.clear();

		ScrollPane tableScrollPane = new ScrollPane(buildConfigDataTableView);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				String[] channels = { "CH#1", "CH#2", "CH#3", "CH#4" };

				String[] defaultDescriptionsCh1 = { "Analog-1-Left", "Analog-1-Right", "Analog-2", "Power Supply",
						"Digital Card", "Flex Assembly CH-1" };

				String[] defaultPNCh1 = { "1104 000 927 42 / 04", "1104 000 927 42 / 04", "1104 000 929 36 / 03",
						"1100 024 499 72 / 05", "1104 001 025 39 / 07", "1104 001 339 67 / 02" };

				String[] defaultSNCh1 = { "102", "110", "044", "141", "105", "020" };

				String[] defaultDescriptionsCh2 = { "Analog-1-Left", "Analog-1-Right", "Analog-2", "Power Supply",
						"Digital Card", "Flex Assembly CH-2" };

				String[] defaultPNCh2 = { "1104 000 927 42 / 04", "1104 000 927 42 / 04", "1104 000 929 36 / 03",
						"1100 024 499 72 / 05", "1104 001 025 39 / 07", "1104 001 339 67 / 02" };

				String[] defaultSNCh2 = { "111", "113", "058", "133", "115", "020" };

				String[] defaultDescriptionsCh3 = { "Analog-1-Left", "Analog-1-Right", "Analog-2", "Power Supply",
						"Digital Card", "Flex Assembly CH-3" };

				String[] defaultPNCh3 = { "1104 000 927 42 / 04", "1104 000 927 42 / 04", "1104 000 929 36 / 03",
						"1100 024 499 72 / 05", "1104 001 025 39 / 07", "1104 001 339 67 / 02" };

				String[] defaultSNCh3 = { "114", "125", "066", "144", "120", "020" };

				String[] defaultDescriptionsCh4 = { "Analog-1-Left", "Analog-1-Right", "Analog-2", "Power Supply",
						"Digital Card", "Flex Assembly CH-4", "Mother Board", "Dip brazed chassis", "Electrical Chassis Assy." };

				String[] defaultPNCh4 = { "1104 000 927 42 / 04", "1104 000 927 42 / 04", "1104 000 929 36 / 03",
						"1100 000 499 72 / 05", "1104 001 025 39 / 07 / 04", "1104 001 339 67 / 02", "1104 001 343 55 / 01","1104 001 335 79 / 02","1104 001 338 70 / 04"};

				String[] defaultSNCh4 = { "132", "079", "047", "123", "119", "020", "025", "DFCC/Mk1-60","017" };

				// Loop through channels and assign respective array
				for (String channel : channels) {

					String[] selectedDescriptions;
					String[] selectedPartNo;
					String[] serialNo;
					switch (channel) {
					case "CH#1":
						selectedDescriptions = defaultDescriptionsCh1;
						selectedPartNo = defaultPNCh1;
						serialNo = defaultSNCh1;
						break;
					case "CH#2":
						selectedDescriptions = defaultDescriptionsCh2;
						selectedPartNo = defaultPNCh2;
						serialNo = defaultSNCh2;
						break;
					case "CH#3":
						selectedDescriptions = defaultDescriptionsCh3;
						selectedPartNo = defaultPNCh3;
						serialNo = defaultSNCh3;
						break;
					case "CH#4":
						selectedDescriptions = defaultDescriptionsCh4;
						selectedPartNo = defaultPNCh4;
						serialNo = defaultSNCh4;
						break;
					default:
						continue;
					}

					for (int i = 0; i < selectedDescriptions.length; i++) {

						BuildConfiguration1 row = new BuildConfiguration1();

						row.setChannel(i == 0 ? channel : "");

						row.setDescription(selectedDescriptions[i]);
						row.setPartNumber(selectedPartNo[i]);
						row.setSerialNumber(serialNo[i]);

						buildConfigDataList.add(row);
					}
				}

				// Create table after data is ready
				buildConfigDataTableView = buildDataFactory.createTableView(buildConfigDataList, false, false);

				// Column width settings
				buildConfigDataTableView.getColumns().forEach(column -> {

					switch (column.getText()) {

					case "CHANNEL":
						column.setMinWidth(100);
						column.setMaxWidth(100);
						break;

					case "DESCRIPTION":
						column.setMinWidth(470);
						column.setMaxWidth(470);
						break;

					case "PART NUMBER":

						column.setText("PART NUMBER / KEY SHEET ISSUE LEVEL");
						column.setMinWidth(650);
						column.setMaxWidth(650);
						break;

					case "SERIAL NUMBER":
						column.setMinWidth(346);
						column.setMaxWidth(346);
						break;
					}
				});

				tableScrollPane.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					tableScrollPane.setContent(buildConfigDataTableView);
					buildConfigDataTableView.setEditable(true);

					// PART NUMBER (col 2)
					TableColumn<BuildConfiguration1, String> partNumberColumn = (TableColumn<BuildConfiguration1, String>) buildConfigDataTableView
							.getColumns().get(2);

					partNumberColumn.setCellValueFactory(new PropertyValueFactory<>("partNumber"));
					partNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					partNumberColumn.setOnEditCommit(event -> {
						BuildConfiguration1 row = event.getRowValue();
						row.setPartNumber(event.getNewValue());
					});

					// SERIAL NUMBER (col 3)
					TableColumn<BuildConfiguration1, String> serialNumberColumn = (TableColumn<BuildConfiguration1, String>) buildConfigDataTableView
							.getColumns().get(3);

					serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
					serialNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
					serialNumberColumn.setOnEditCommit(event -> {
						BuildConfiguration1 row = event.getRowValue();
						row.setSerialNumber(event.getNewValue());
					});

					tableScrollPane.setFitToHeight(true);
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
	}

	private BuildConfiguration mapToDto(List<BuildConfiguration1> rows) {

		BuildConfiguration dto = new BuildConfiguration();
		String lastChannel = "";

		for (BuildConfiguration1 row : rows) {

			String ch = row.getChannel();

			if (ch != null && !ch.trim().isEmpty()) {
				lastChannel = ch;
			} else {
				ch = lastChannel;
			}

			String desc = row.getDescription();
			String pn = row.getPartNumber();
			String sn = row.getSerialNumber();

			switch (ch) {

			case "CH#1":
				switch (desc) {
				case "Analog-1-Left":
					dto.setCh1Analog1LeftPartNumber(pn);
					dto.setCh1Analog1LeftSerialNo(sn);
					break;

				case "Analog-1-Right":
					dto.setCh1Analog1RightPartNumber(pn);
					dto.setCh1Analog1RightSerialNo(sn);
					break;

				case "Analog-2":
					dto.setCh1Analog2PartNumber(pn);
					dto.setCh1Analog2SerialNo(sn);
					break;

				case "Power Supply":
					dto.setCh1poweSupplyPartNumber(pn);
					dto.setCh1poweSupplySerialNo(sn);
					break;

				case "Digital Card":
					dto.setCh1cpuAssemblyPartNumber(pn);
					dto.setCh1cpuAssemblySerialNo(sn);
					break;

				case "Flex Assembly CH-1":
					dto.setCh1flexAssemblyPartNumber(pn);
					dto.setCh1flexAssemblySerialNo(sn);
					break;
				}
				break;

			case "CH#2":
				switch (desc) {
				case "Analog-1-Left":
					dto.setCh2Analog1LeftPartNumber(pn);
					dto.setCh2Analog1LeftSerialNo(sn);
					break;

				case "Analog-1-Right":
					dto.setCh2Analog1RightPartNumber(pn);
					dto.setCh2Analog1RightSerialNo(sn);
					break;

				case "Analog-2":
					dto.setCh2Analog2PartNumber(pn);
					dto.setCh2Analog2SerialNo(sn);
					break;

				case "Power Supply":
					dto.setCh2poweSupplyPartNumber(pn);
					dto.setCh2poweSupplySerialNo(sn);
					break;

				case "Digital Card":
					dto.setCh2cpuAssemblyPartNumber(pn);
					dto.setCh2cpuAssemblySerialNo(sn);
					break;

				case "Flex Assembly CH-2":
					dto.setCh2flexAssemblyPartNumber(pn);
					dto.setCh2flexAssemblySerialNo(sn);
					break;
				}
				break;

			case "CH#3":
				switch (desc) {
				case "Analog-1-Left":
					dto.setCh3Analog1LeftPartNumber(pn);
					dto.setCh3Analog1LeftSerialNo(sn);
					break;

				case "Analog-1-Right":
					dto.setCh3Analog1RightPartNumber(pn);
					dto.setCh3Analog1RightSerialNo(sn);
					break;

				case "Analog-2":
					dto.setCh3Analog2PartNumber(pn);
					dto.setCh3Analog2SerialNo(sn);
					break;

				case "Power Supply":
					dto.setCh3poweSupplyPartNumber(pn);
					dto.setCh3poweSupplySerialNo(sn);
					break;

				case "Digital Card":
					dto.setCh3cpuAssemblyPartNumber(pn);
					dto.setCh3cpuAssemblySerialNo(sn);
					break;

				case "Flex Assembly CH-3":
					dto.setCh3flexAssemblyPartNumber(pn);
					dto.setCh3flexAssemblySerialNo(sn);
					break;
				}
				break;

			case "CH#4":
				switch (desc) {
				case "Analog-1-Left":
					dto.setCh4Analog1LeftPartNumber(pn);
					dto.setCh4Analog1LeftSerialNo(sn);
					break;

				case "Analog-1-Right":
					dto.setCh4Analog1RightPartNumber(pn);
					dto.setCh4Analog1RightSerialNo(sn);
					break;

				case "Analog-2":
					dto.setCh4Analog2PartNumber(pn);
					dto.setCh4Analog2SerialNo(sn);
					break;

				case "Power Supply":
					dto.setCh4poweSupplyPartNumber(pn);
					dto.setCh4poweSupplySerialNo(sn);
					break;

				case "Digital Card":
					dto.setCh4cpuAssemblyPartNumber(pn);
					dto.setCh4cpuAssemblySerialNo(sn);
					break;
					

				case "Flex Assembly CH-4":
					dto.setCh4flexAssemblyPartNumber(pn);
					dto.setCh4flexAssemblySerialNo(sn);
					break;

				case "Mother Board":
					dto.setMotherboardPartNumber(pn);
					dto.setMotherboardSerialNumber(sn);
					break;

				case "Dip brazed chassis":
					dto.setChasisPartNumber(pn);
					dto.setChasisSerialNumber(sn);
					break;
					
				case "Electrical Chassis Assy.":
					dto.setAssemblyPartNumber(pn);
					dto.setAssemblySerialNumber(sn);
					break;
				}
				break;
			}
		}

		return dto;
	}

	public void saveButton(String serialNo, String versionName) {

		if (!DFCCConstant.selectedFetchBuildConfig.equalsIgnoreCase("Fetch")) {
			BuildConfiguration dto = mapToDto(buildConfigDataList);
			buildConfigurationManagement.addBuildConfiguration(serialNo, versionName, dto);
		} else {
			BuildConfiguration dto1 = mapToDto(buildConfigDataList1);
			buildConfigurationManagement.addBuildConfiguration(serialNo, versionName, dto1);

		}

	}

//	from db fetching:

	private void convertToTableRows(BuildConfiguration config) {

		// ---------- CH#1 ----------
		addRow("CH#1", "Analog-1-Left", config.getCh1Analog1LeftPartNumber(), config.getCh1Analog1LeftSerialNo());
		addRow("", "Analog-1-Right", config.getCh1Analog1RightPartNumber(), config.getCh1Analog1RightSerialNo());
		addRow("", "Analog-2", config.getCh1Analog2PartNumber(), config.getCh1Analog2SerialNo());
		addRow("", "Power Supply", config.getCh1poweSupplyPartNumber(), config.getCh1poweSupplySerialNo());
		addRow("", "Digital Card", config.getCh1cpuAssemblyPartNumber(), config.getCh1cpuAssemblySerialNo());
		addRow("", "Flex Assembly CH-1", config.getCh1flexAssemblyPartNumber(), config.getCh1flexAssemblySerialNo());

		// ---------- CH#2 ----------
		addRow("CH#2", "Analog-1-Left", config.getCh2Analog1LeftPartNumber(), config.getCh2Analog1LeftSerialNo());
		addRow("", "Analog-1-Right", config.getCh2Analog1RightPartNumber(), config.getCh2Analog1RightSerialNo());
		addRow("", "Analog-2", config.getCh2Analog2PartNumber(), config.getCh2Analog2SerialNo());
		addRow("", "Power Supply", config.getCh2poweSupplyPartNumber(), config.getCh2poweSupplySerialNo());
		addRow("", "Digital Card", config.getCh2cpuAssemblyPartNumber(), config.getCh2cpuAssemblySerialNo());
		addRow("", "Flex Assembly CH-2", config.getCh2flexAssemblyPartNumber(), config.getCh2flexAssemblySerialNo());

		// ---------- CH#3 ----------
		addRow("CH#3", "Analog-1-Left", config.getCh3Analog1LeftPartNumber(), config.getCh3Analog1LeftSerialNo());
		addRow("", "Analog-1-Right", config.getCh3Analog1RightPartNumber(), config.getCh3Analog1RightSerialNo());
		addRow("", "Analog-2", config.getCh3Analog2PartNumber(), config.getCh3Analog2SerialNo());
		addRow("", "Power Supply", config.getCh3poweSupplyPartNumber(), config.getCh3poweSupplySerialNo());
		addRow("", "Digital Card", config.getCh3cpuAssemblyPartNumber(), config.getCh3cpuAssemblySerialNo());
		addRow("", "Flex Assembly CH-3", config.getCh3flexAssemblyPartNumber(), config.getCh3flexAssemblySerialNo());

		// ---------- CH#4 ----------
		addRow("CH#4", "Analog-1-Left", config.getCh4Analog1LeftPartNumber(), config.getCh4Analog1LeftSerialNo());
		addRow("", "Analog-1-Right", config.getCh4Analog1RightPartNumber(), config.getCh4Analog1RightSerialNo());
		addRow("", "Analog-2", config.getCh4Analog2PartNumber(), config.getCh4Analog2SerialNo());
		addRow("", "Power Supply", config.getCh4poweSupplyPartNumber(), config.getCh4poweSupplySerialNo());
		addRow("", "Digital Card", config.getCh4cpuAssemblyPartNumber(), config.getCh4cpuAssemblySerialNo());
		addRow("", "Flex Assembly CH-4", config.getCh4flexAssemblyPartNumber(), config.getCh4flexAssemblySerialNo());
		addRow("", "Mother Board", config.getMotherboardPartNumber(), config.getMotherboardSerialNumber());
		addRow("", "Dip brazed chassis", config.getChasisPartNumber(), config.getChasisSerialNumber());
		addRow("", "Electrical Chassis Assy.", config.getAssemblyPartNumber(), config.getAssemblySerialNumber());
	}

	private void addRow(String channel, String desc, String pn, String sn) {
		BuildConfiguration1 row = new BuildConfiguration1();
		row.setChannel(channel);
		row.setDescription(desc);
		row.setPartNumber(pn);
		row.setSerialNumber(sn);
		buildConfigDataList1.add(row);
	}

	private ScrollPane createBuildConfigurationDataTable() {
		buildConfigDataList1.clear();

		ScrollPane tableScrollPane = new ScrollPane(buildConfigDataTableView1);

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				BuildConfiguration config = buildConfigurationManagement.getBuildConfiguration(
						DFCCConstant.selectedSNoBuildConfig, DFCCConstant.selectedVersionNoBuildConfig);
				// Convert BuildConfiguration → table rows
				convertToTableRows(config);

				// Create table
				buildConfigDataTableView1 = buildDataFactory1.createTableView(buildConfigDataList1, false, false);

				// Set column widths
				buildConfigDataTableView1.getColumns().forEach(column -> {
					switch (column.getText()) {

					case "CHANNEL":
						column.setMinWidth(100);
						column.setMaxWidth(100);
						break;

					case "DESCRIPTION":
						column.setMinWidth(470);
						column.setMaxWidth(470);
						break;

					case "PART NUMBER":
						column.setText("PART NUMBER / KEY SHEET ISSUE LEVEL");
						column.setMinWidth(650);
						column.setMaxWidth(650);
						break;

					case "SERIAL NUMBER":
						column.setMinWidth(346);
						column.setMaxWidth(346);
						break;

					default:
						break;
					}
				});

				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					tableScrollPane.setContent(buildConfigDataTableView1);
					tableScrollPane.setFitToHeight(true);

					buildConfigDataTableView1.setEditable(true);

					// PART NUMBER column (index 2)
					TableColumn<BuildConfiguration1, String> partNumberCol = (TableColumn<BuildConfiguration1, String>) buildConfigDataTableView1
							.getColumns().get(2);

					partNumberCol.setCellValueFactory(new PropertyValueFactory<>("partNumber"));
					partNumberCol.setCellFactory(TextFieldTableCell.forTableColumn());
					partNumberCol.setOnEditCommit(event -> {
						BuildConfiguration1 row = event.getRowValue();
						row.setPartNumber(event.getNewValue());
					});

					// SERIAL NUMBER column (index 3)
					TableColumn<BuildConfiguration1, String> serialNumberCol = (TableColumn<BuildConfiguration1, String>) buildConfigDataTableView1
							.getColumns().get(3);

					serialNumberCol.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
					serialNumberCol.setCellFactory(TextFieldTableCell.forTableColumn());
					serialNumberCol.setOnEditCommit(event -> {
						BuildConfiguration1 row = event.getRowValue();
						row.setSerialNumber(event.getNewValue());
					});
					Platform.runLater(() -> Notifications.showSuccessAlert("Build Configuration fetched successfully"));
				});
			}

			@Override
			protected void failed() {
				Throwable ex = getException();
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};

		new Thread(task).start();
		return tableScrollPane;
	}

	private HBox saveButtonHbox() {
		HBox saveButtonHbox = new HBox();
		saveButtonHbox.setAlignment(Pos.CENTER_RIGHT);
		saveButtonHbox.setPadding(new Insets(10));
		saveButtonHbox.getChildren().add(save);

		save.setOnAction(e -> {

			if (versionNo2.getText().trim().isEmpty()) {
				Notifications.showErrorAlert("Please enter version");
				return;
			}
			BuildConfiguration dto = mapToDto(buildConfigDataList);

			buildConfigurationManagement.addBuildConfiguration(serialNo2.getText(), versionNo2.getText(), dto);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate selectedDate = datePicker.getValue();

			if (selectedDate != null) {
				date = selectedDate.format(formatter);
			} else {
				Notifications.showErrorAlert("Please select Date");
				return;
			}

			Notifications.showSuccessAlert("Build Configuration saved successfully");

			BuildConfigurationReport buildConfigurationReport = new BuildConfigurationReport();
			try {
				buildConfigurationReport.generateBuildConfigurationReport(serialNo2.getText(), date,
						versionNo2.getText(), DFCCConstant.selectedUutBuildConfig);
			} catch (DocumentException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		});
		return saveButtonHbox;
	}
}
