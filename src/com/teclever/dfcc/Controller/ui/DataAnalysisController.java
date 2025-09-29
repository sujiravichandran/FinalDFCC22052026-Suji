package com.teclever.dfcc.Controller.ui;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.text.Text;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisDTO;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataAnalysisManagement;
import com.teclever.dfcc.advanceddataanalysis.AdvancedDataUnitsDetailsDTO;
import com.teclever.dfcc.datastore.dto.ResultUnitSessionDetailsDTO;
import com.teclever.dfcc.model.AdvancedDataAnalysis;
import com.teclever.dfcc.model.AdvancedDataAnalysis2;
import com.teclever.dfcc.model.BrowseFileDetailedData;
import com.teclever.dfcc.resultstore.dto.FilesFetchFailsDTO;
import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class DataAnalysisTableViewFactory implements TableViewFactory<AdvancedDataAnalysis> {
	@Override
	public CustomTableView<AdvancedDataAnalysis> createTableView(ObservableList<AdvancedDataAnalysis> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AdvancedDataAnalysis.class, addUserColumn, addCheckboxColumn);
	}
}

class DataAnalysisTableViewFactory2 implements TableViewFactory<AdvancedDataAnalysis2> {
	@Override
	public CustomTableView<AdvancedDataAnalysis2> createTableView(ObservableList<AdvancedDataAnalysis2> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, AdvancedDataAnalysis2.class, addUserColumn, addCheckboxColumn);
	}
}

class BrowseTableViewFactory implements TableViewFactory<BrowseFileDetailedData> {
	@Override
	public CustomTableView<BrowseFileDetailedData> createTableView(ObservableList<BrowseFileDetailedData> items,
			boolean addUserColumn, boolean addCheckboxColumn) {
		return new CustomTableView<>(items, BrowseFileDetailedData.class, addUserColumn, addCheckboxColumn);
	}
}

public class DataAnalysisController {

	private GridPane dataAnalysisMainContainerGridPane = new GridPane();
	private GridPane dataAnalysisOneAllContainerGridPane = new GridPane();
	private GridPane dataAnalysisOneContainerGridPane = new GridPane();
	private GridPane dataAnalysisTwoContainerGridPane = new GridPane();
	private HBox headingHbox = new HBox(10);
	private GridPane headingGridPane = new GridPane();
	private GridPane closeButtonGridPane = new GridPane();
	private GridPane browseButtonGridPane = new GridPane();
	private HBox closeButtonHbox = new HBox(10);
	private Button closeButton = new Button("Close");

	private Button browseButton = new Button("Click to Browse Folder");
	private HBox browseButtonHbox = new HBox(10);

	private Label pageHeading = new Label("DATA ANAYSIS");
	private AdvancedDataAnalysisManagement advancedDataAnalysisManagement = new AdvancedDataAnalysisManagement();

	private ObservableList<AdvancedDataAnalysis> dataAnalysisOneDataList = FXCollections.observableArrayList();
	private ObservableList<AdvancedDataAnalysis2> dataAnalysisTwoDataList = FXCollections.observableArrayList();
	private CustomTableView<AdvancedDataAnalysis> dataAnalysisOneDataTableView;
	private CustomTableView<AdvancedDataAnalysis2> dataAnalysisTwoDataTableView;
	private TableViewFactory<AdvancedDataAnalysis> advancedDataAnalysisFactory = new DataAnalysisTableViewFactory();
	private TableViewFactory<AdvancedDataAnalysis2> advancedDataAnalysisFactory2 = new DataAnalysisTableViewFactory2();

	private Label totalSessionsStage = new Label("Total Sessions Stages");
	private Label noofunitsTested = new Label("No of Units Tested");

	private ObservableList<BrowseFileDetailedData> browseDataList = FXCollections.observableArrayList();
	private CustomTableView<BrowseFileDetailedData> browseDataTableView;
	private TableViewFactory<BrowseFileDetailedData> browseDataFactory = new BrowseTableViewFactory();
	
	private static List<FilesFetchFailsDTO> responseList = new ArrayList<>();
	
	
	

	public GridPane createDashboardMainContainerGridPane() {

		dataAnalysisMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataAnalysis.css").toExternalForm());
		dataAnalysisMainContainerGridPane.getStyleClass().add("dashboard-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(30);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(57);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(6);


		dataAnalysisMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		dataAnalysisMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);
		dataAnalysisMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
		dataAnalysisMainContainerGridPane.add(headingGridPane(), 0, 0);
		dataAnalysisMainContainerGridPane.add(dataAnalysisOneAllGridPane(), 0, 1);
		dataAnalysisMainContainerGridPane.add(dataAnalysisTwoGridPane(), 0, 2);
		dataAnalysisMainContainerGridPane.add(closeButtonGridPane(), 0, 3);

		return dataAnalysisMainContainerGridPane;
	}

	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().add(firstRow);

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		headingHbox.getStyleClass().add("dataanalysis-testing-right-container");
		headingHbox.setAlignment(Pos.CENTER);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	public GridPane closeButtonGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		closeButtonGridPane.getColumnConstraints().addAll(firstColumn);
		closeButtonGridPane.getRowConstraints().add(firstRow);
		closeButtonGridPane.add(closeButtonHbox(), 0, 0);

		return closeButtonGridPane;

	}

	private HBox closeButtonHbox() {

		closeButtonHbox.getStyleClass().add("dataanalysis-testing-right-container");
		closeButtonHbox.setAlignment(Pos.CENTER);
		closeButtonHbox.getChildren().add(closeButton);
		closeButtonHbox.setMaxWidth(Double.MAX_VALUE);
		closeButton.setOnAction(e -> {
			Stage stage = (Stage) closeButton.getScene().getWindow();
			stage.close();
		});

		return closeButtonHbox;
	}

	private GridPane dataAnalysisOneAllGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(80);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(20);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(100);

		dataAnalysisOneAllContainerGridPane.setHgap(15);
		dataAnalysisOneAllContainerGridPane.setPadding(new Insets(5));
		dataAnalysisOneAllContainerGridPane.setAlignment(Pos.CENTER);
		dataAnalysisOneAllContainerGridPane.add(dataAnalysisOneGridPane(), 0, 0);
		dataAnalysisOneAllContainerGridPane.add(browseButtonGridePane(), 1, 0);

		return dataAnalysisOneAllContainerGridPane;
	}

	private GridPane dataAnalysisOneGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);

		dataAnalysisOneContainerGridPane.setHgap(15);
		dataAnalysisOneContainerGridPane.setPadding(new Insets(5));

//		dataAnalysisOneContainerGridPane
//				.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");

		dataAnalysisOneContainerGridPane.getStyleClass().add("midheader-label-left");
		dataAnalysisOneContainerGridPane.getStyleClass().add("midheader-hbox");
//		dataAnalysisOneContainerGridPane.setAlignment(Pos.CENTER);
		dataAnalysisOneContainerGridPane.add(totalSessionsStage, 0, 0);
		dataAnalysisOneContainerGridPane.add(createDataAnalysisOneResultTable(), 0, 1);

		return dataAnalysisOneContainerGridPane;
	}

	public ScrollPane createDataAnalysisOneResultTable() {
		dataAnalysisOneDataList.clear();

		ScrollPane tableScrollPane2 = new ScrollPane(dataAnalysisOneDataTableView);
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				AdvancedDataAnalysisDTO response = new AdvancedDataAnalysisDTO();
				response = advancedDataAnalysisManagement.getAdvancedDataDetails(currentSessionDetails.getUutId());

				if (response.getUnitsDetails().size() > 0) {
					int i = 1;
					for (AdvancedDataUnitsDetailsDTO data : response.getUnitsDetails()) {

						AdvancedDataAnalysis newDetailedData = new AdvancedDataAnalysis();

						newDetailedData.setSlNo(String.valueOf(i));
						newDetailedData.setCount(data.getCount());
						newDetailedData.setSessionTypeName(data.getSessionTypeName());
						newDetailedData.setUttType(data.getSessionUutType());
						dataAnalysisOneDataList.add(newDetailedData);

						i++;
					}
				} else if (response.getUnitsDetails().size() < 0) {
					Notifications.showErrorAlert("No Data");
				}

				dataAnalysisOneDataTableView = advancedDataAnalysisFactory.createTableView(dataAnalysisOneDataList,
						false, false);

				dataAnalysisOneDataTableView.getColumns().forEach(column -> {
//					column.setMinWidth(column.getText().length() * 14);
					String colNamne = column.getText();
					switch (colNamne) {
					case "SL NO":
						column.setMinWidth(100);
						column.setMaxWidth(100);
						break;
					case "COUNT":
						column.setMinWidth(100);
						column.setMaxWidth(100);
						break;
					case "SESSION TYPE NAME":
						column.setMinWidth(200);
						column.setMaxWidth(200);
						break;
					case "UTT TYPE":
						column.setMinWidth(200);
						column.setMaxWidth(200);
						break;

					}
				});

//		if(detailedDataList.size() == 0) {
//			tableScrollPane2.setFitToWidth(true);
//		}

				tableScrollPane2.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					tableScrollPane2.setContent(dataAnalysisOneDataTableView);
					tableScrollPane2.setFitToHeight(true);
				});

			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};
		new Thread(task).start();

		return tableScrollPane2;
	}

	private GridPane dataAnalysisTwoGridPane() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(95);

		dataAnalysisTwoContainerGridPane.setHgap(15);
		dataAnalysisTwoContainerGridPane.setPadding(new Insets(5));

		dataAnalysisOneContainerGridPane
				.setStyle("-fx-border-radius: 10;" + "-fx-border-color: black;" + "-fx-border-width: 2;");

		dataAnalysisTwoContainerGridPane.getStyleClass().add("midheader-label-left");
		dataAnalysisTwoContainerGridPane.getStyleClass().add("midheader-hbox");
		dataAnalysisTwoContainerGridPane.setAlignment(Pos.TOP_LEFT);
		dataAnalysisTwoContainerGridPane.add(noofunitsTested, 0, 0);
		dataAnalysisTwoContainerGridPane.add(createDataAnalysisTwoResultTable(), 0, 1);

		return dataAnalysisTwoContainerGridPane;
	}

	public ScrollPane createDataAnalysisTwoResultTable() {
		dataAnalysisTwoDataList.clear();

		ScrollPane tableScrollPane3 = new ScrollPane(dataAnalysisTwoDataTableView);
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				AdvancedDataAnalysisDTO response = new AdvancedDataAnalysisDTO();
				response = advancedDataAnalysisManagement.getAdvancedDataDetails(currentSessionDetails.getUutId());

				if (response.getUnitsDetails().size() > 0) {
					int i = 1;
					for (ResultUnitSessionDetailsDTO data : response.getSessionList()) {

						AdvancedDataAnalysis2 newDetailedData = new AdvancedDataAnalysis2();

						newDetailedData.setSlNo(String.valueOf(i));
						newDetailedData.setUnitSlNo(data.getSessionSlNo());
						newDetailedData.setSessionType(data.getSessionType());
						newDetailedData.setSessionName(data.getSessionName());
						newDetailedData.setStartDate(data.getStartTime());
						newDetailedData.setEndDate(data.getEndTime());

						dataAnalysisTwoDataList.add(newDetailedData);

						i++;
					}
				} else if (response.getUnitsDetails().size() < 0) {
					Notifications.showErrorAlert("No Data");
				}

				dataAnalysisTwoDataTableView = advancedDataAnalysisFactory2.createTableView(dataAnalysisTwoDataList,
						false, false);

//				if (!dataAnalysisTwoDataTableView.getColumns().isEmpty()) {
//				    int lastIndex = dataAnalysisTwoDataTableView.getColumns().size() - 1;
//				    dataAnalysisTwoDataTableView.getColumns().remove(lastIndex);
//				}

				dataAnalysisTwoDataTableView.getColumns().forEach(column -> {
//					column.setMinWidth(column.getText().length() * 14);
					String colNamne2 = column.getText();
					switch (colNamne2) {
					case "SL NO":
						column.setMinWidth(100);
						column.setMaxWidth(100);
						break;
					case "UNIT SL NO":
						column.setMinWidth(400);
						column.setMaxWidth(400);
						break;
					case "SESSION TYPE":
						column.setMinWidth(200);
						column.setMaxWidth(200);
						break;
					case "SESSION NAME":
						column.setMinWidth(800);
						column.setMaxWidth(800);
						break;
					case "START DATE":
						column.setMinWidth(200);
						column.setMaxWidth(200);
						break;
					case "END DATE":
						column.setMinWidth(200);
						column.setMaxWidth(200);
						break;

					}
				});

//		if(detailedDataList.size() == 0) {
//			tableScrollPane2.setFitToWidth(true);
//		}

				tableScrollPane3.setFitToWidth(true);

				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {

					tableScrollPane3.setContent(dataAnalysisTwoDataTableView);
					tableScrollPane3.setFitToHeight(true);
				});

			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};
		new Thread(task).start();

		return tableScrollPane3;
	}

	private GridPane browseButtonGridePane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		browseButtonGridPane.getColumnConstraints().addAll(firstColumn);
		browseButtonGridPane.getRowConstraints().add(firstRow);
		browseButtonGridPane.getStyleClass().add("selfTest-top-container");

		browseButtonGridPane.add(browseButtonHbox(null), 0, 0);

		return browseButtonGridPane;

	}

	private HBox browseButtonHbox(StackPane displayStackPane) {
		HBox browseButtonHbox = new HBox();
		browseButtonHbox.setAlignment(Pos.CENTER);

		browseButton.setMaxWidth(Double.MAX_VALUE);
		browseButton.setMaxHeight(Double.MAX_VALUE);
		HBox.setHgrow(browseButton, Priority.ALWAYS);
		browseButtonHbox.getChildren().add(browseButton);

		browseButton.setStyle("-fx-border-radius: 10;" + "-fx-background-color: #cccfcd;" + "-fx-border-width: 2;");

		browseButtonHbox.getStyleClass().add("dataanalysis-testing-right-container");

		browseButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select a Folder");

			File selectedDirectory = directoryChooser.showDialog(((Stage) browseButton.getScene().getWindow()));

			if (selectedDirectory != null) {
				
				responseList = new ArrayList<>();
				String folderPath = selectedDirectory.getAbsolutePath();
				responseList = advancedDataAnalysisManagement.getAllFailsByFolder(folderPath);
				
				// create stage AFTER folder selection and pass it to the factory method
				
				
				Stage stage = new Stage();
				stage.initStyle(StageStyle.UNDECORATED);
				StackPane fullScreenPane = createBrowseFailureResultTable(stage); // <-- pass stage
				stage.setScene(new Scene(fullScreenPane));
				stage.setMaximized(true);
				stage.show();
			}
		});

		return browseButtonHbox;
	}

	public StackPane createBrowseFailureResultTable(Stage stage) {
		ScrollPane tableScrollPane2 = new ScrollPane(browseDataTableView);

	    // Existing Close button
	    Button closeButton = new Button("Close");
	    closeButton.setOnAction(e -> stage.close());

	    // New Download Report button
	    Button downloadButton = new Button("Download Report");
	    downloadButton.setOnAction(e -> {
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Save Report");
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
	        File file = fileChooser.showSaveDialog(stage);

	        if (file != null) {
	            try {
	                // Create PDF writer and document
	                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
	                PdfDocument pdfDoc = new PdfDocument(writer);
	                Document document = new Document(pdfDoc, PageSize.A3.rotate()); // Landscape mode
	                document.setMargins(20, 20, 20, 20);

	                // Add title
	                Paragraph title = new Paragraph("Browse Failure Report")
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(16)
	                        .setBold();
	                document.add(title);
	                document.add(new Paragraph("\n"));

	                int columns = browseDataTableView.getColumns().size();
	                float[] columnWidths = new float[columns];
	                Arrays.fill(columnWidths, 2f);  // equal widths

	                Table table = new Table(columnWidths);
	                table.setWidth(UnitValue.createPercentValue(100));
	                table.setKeepTogether(false); 

	                // Headers
	                for (TableColumn<?, ?> col : browseDataTableView.getColumns()) {
	                    table.addHeaderCell(new Cell()
	                        .add(new Paragraph(col.getText()).setBold().setFontSize(10))
	                        .setTextAlignment(TextAlignment.CENTER));
	                }

	                // Rows
	                for (Object item : browseDataTableView.getItems()) {
	                    for (TableColumn<?, ?> col : browseDataTableView.getColumns()) {
	                        @SuppressWarnings("unchecked")
	                        TableColumn<Object, Object> column = (TableColumn<Object, Object>) col;

	                        Object cellData = column.getCellData(item);
	                        String text = (cellData == null) ? "" : cellData.toString();
	                        table.addCell(new Cell()
	                            .add(new Paragraph(text).setFontSize(9))
	                            .setTextAlignment(TextAlignment.LEFT));
	                    }
	                }

	                document.add(table);	
	                document.close();

	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	    });

	    // Add both buttons to the HBox
	    HBox closeButtonBox = new HBox(10, downloadButton, closeButton); // 10px spacing between buttons
	    closeButtonBox.getStyleClass().add("dataanalysis-testing-right-container");
	    closeButtonBox.setAlignment(Pos.CENTER);
	    closeButtonBox.setPadding(new Insets(10));

	    BorderPane borderPane = new BorderPane();
	    borderPane.setCenter(tableScrollPane2);
	    borderPane.setBottom(closeButtonBox);
	    browseDataList.clear();
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				
				if (responseList.size()>0) {
					int i = 1;
					for (FilesFetchFailsDTO data : responseList) {
						BrowseFileDetailedData newDetailedData = new BrowseFileDetailedData();

						newDetailedData.setSlNo(String.valueOf(i));
						String fullPath = data.getResultDataFile();
						String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
						newDetailedData.setRdfName(fileName);
						newDetailedData.setTpgphNo(data.getTpgph());
						newDetailedData.setStepName(data.getStep());
						 newDetailedData.setExpectedValue(data.getExpectedValue());
						newDetailedData.setMeasuredValueCh1_Ch2_Ch3_Ch4(data.getFaultyChannel());
						newDetailedData.setFaultySRU(data.getFaultySRU());
						newDetailedData.setUnit(data.getUnit());
						newDetailedData.setSignalName(data.getSignalName());
						newDetailedData.setFaultyChannel(data.getdStarChannels());
						newDetailedData.setFilePath(data.getFilePath());
						
						String expectedVal = data.getExpectedValue();
						if (expectedVal != null && !expectedVal.isEmpty()) {
						    String[] expec = expectedVal.split(",");
						    if (expec.length >= 2) {
						        String outputLow = expec[0].replaceAll("[()]", "").trim();
						        String outputHigh = expec[1].replaceAll("[()]", "").trim();
						        
						        newDetailedData.setExpectedMinValue(outputLow);
						        newDetailedData.setExpectedMaxValue(outputHigh);  
						    }
						} else {
						    newDetailedData.setExpectedMinValue("-");
						    newDetailedData.setExpectedMaxValue("-");  
						}


						browseDataList.add(newDetailedData);
						i++;
					}
				}

				BrowseTableViewFactory factory = new BrowseTableViewFactory();
				browseDataTableView = factory.createTableView(browseDataList, false, false);

				browseDataTableView.getColumns().forEach(column -> {
				    column.setMinWidth(column.getText().length() * 25);
				    if (column.getText().equalsIgnoreCase("File Path") ||column.getText().equalsIgnoreCase("Faulty Channel") || column.getText().equalsIgnoreCase("Rdf Name") || column.getText().equalsIgnoreCase("Signal Name") ||column.getText().equalsIgnoreCase("Measured Value") ) {
				    	
				        TableColumn<BrowseFileDetailedData, String> stringColumn = (TableColumn<BrowseFileDetailedData, String>) column;

				        stringColumn.setCellFactory(col -> new TableCell<BrowseFileDetailedData, String>() {
				            private final Text text = new Text();
				            
				            {
				            	Platform.runLater(() -> {
				            	setAlignment(Pos.CENTER);
					            setGraphic(text);
					            setPrefHeight(Control.USE_COMPUTED_SIZE);
				                text.wrappingWidthProperty().bind(col.widthProperty().subtract(5));
				                
				            	});
				            	
				            }

				            @Override
				            protected void updateItem(String item, boolean empty) {
				                super.updateItem(item, empty);
				                if (empty || item == null) {
				                    text.setText("");
				                } else {
				                    text.setText(item);
				                }
				            }
				        });
				    }
				});

				browseDataTableView.setFixedCellSize(-1); 
				tableScrollPane2.setFitToHeight(true);


				tableScrollPane2.setFitToHeight(true);
				return null;
			}

			@Override
			protected void succeeded() {
				Platform.runLater(() -> {
					tableScrollPane2.setContent(browseDataTableView);
					tableScrollPane2.setFitToHeight(true);
				});
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> Notifications.showErrorAlert("Failed to retrieve data"));
			}
		};
		new Thread(task).start();

		return new StackPane(borderPane);
	}
	
	
	

}
