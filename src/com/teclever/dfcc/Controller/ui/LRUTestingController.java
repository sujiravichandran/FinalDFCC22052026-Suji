package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.model.LRUTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class LRUTestingController {

	private GridPane lruTestMainContainerGridPane = new GridPane();

	private GridPane lruContainerGridPane = new GridPane();

	private GridPane headingGridPane = new GridPane();

	private HBox headingHbox = new HBox();

	private GridPane lrumidContainerGridPane = new GridPane();
	private HBox midTopHbox1 = new HBox();
	private Label lruLeftLabel = new Label("SRU Identification and Isolation");
	private HBox midTopHbox2 = new HBox();
	private Label lruLeftLabe2 = new Label("GO and NOGO TEST");
	private HBox midTopHbox3 = new HBox();
	private Label lruLeftLabe3 = new Label("Sub Test / Interface:");
	
	private GridPane sruIsolationGridPane = new GridPane();
	
	private Label mandatoryTest = new Label("Mandatory Test");
	private Button spilLink = new Button("Spil Link");
	private Button pBit = new Button("PBIT");
	private Button powerSupply = new Button("Power Supply");
	private Button adInterface = new Button("A/D-D/A Interface");

	private GridPane sruSubTestGridPane = new GridPane();
	private Label sruTest = new Label("SRU Test");
	private Button digital = new Button("Digital");
	private Button analog1R = new Button("Analog-1 Right");
	private Button analog1L = new Button("Analog-1 Left");
	private Button analog2 = new Button("Analog 2");

	private GridPane goNOGOGridPane = new GridPane();

	private GridPane subTestGridPane = new GridPane();
	private CheckBox subTest = new CheckBox("Select All Sub-Test:");
	private TextArea subTestTextArea = new TextArea();

	private HBox hboxLabel = new HBox();
	private HBox goNoGoLabel = new HBox();
	private HBox nogoButton = new HBox();
	private HBox startTestHBox = new HBox();
	
	private HBox sruTestHBox = new HBox();
	private HBox digitalHBox = new HBox();
	private HBox analog1RHBox = new HBox();
	private HBox analog1LHBox = new HBox();
	private HBox analog2HBox = new HBox();
	
	private VBox subTestVBox= new VBox();
	private VBox goNoGoVBox= new VBox(20);
	
	private HBox textAreaHBox = new HBox();

	private GridPane bottomGridPane = new GridPane();
	private ObservableList<LRUTest> lruTestTableData = FXCollections.observableArrayList();

	public GridPane createlruTestMainContainerGridPane() {

		lruTestMainContainerGridPane.getStylesheets()
				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LRUTest.css").toExternalForm());
		lruTestMainContainerGridPane.getStyleClass().add("lruTest-main-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(50);
		
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(43);
		
		lruTestMainContainerGridPane.setVgap(5);
		
		lruTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		lruTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		lruTestMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));

		lruTestMainContainerGridPane.add(lruheadingGridPane(), 0, 0);
		lruTestMainContainerGridPane.add(lruTestMidContainer(), 0, 1);
		lruTestMainContainerGridPane.add(lruTestBottomContainer(), 0, 2);
		
		
		return lruTestMainContainerGridPane;

	}
	

	public GridPane lruheadingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getStyleClass().add("lruTest-top-container");
		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}
	private HBox headingHbox() {
		Label pageHeading = new Label("LRU TEST");
		pageHeading.getStyleClass().add("lrutest-top-header");
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}
	
	private GridPane lruTestMidContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(20);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(20);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(40);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(20);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(90);

		lrumidContainerGridPane.setHgap(5);
		lrumidContainerGridPane.setVgap(5);
//		lrumidContainerGridPane.setPadding(new Insets(20, 20, 20, 20));

		lrumidContainerGridPane.getStyleClass().add("lruTest-mid-container");
		lrumidContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		lrumidContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);

		lrumidContainerGridPane.add(midTopHbox1(), 0, 0,3,1);
		lrumidContainerGridPane.add(midTopHbox2(), 3, 0); 
		lrumidContainerGridPane.add(sruIsolationGridPane(), 0, 1);
		lrumidContainerGridPane.add(sruSubTestGridPane(),1, 1,2,1);
		lrumidContainerGridPane.add(goNOGOGridPane(), 3, 1);
	
//		lrumidContainerGridPane.add(sruSubTestGridPane(), 3, 1);
//		lrumidContainerGridPane.add(hboxButton(), 0, 2, 3, 1);
//		lrumidContainerGridPane.add(startTestHBox(), 3,2);
//		
		

		return lrumidContainerGridPane;

	}
	
	private HBox midTopHbox1() {
		lruLeftLabel.getStyleClass().add("midheader-label");
		midTopHbox1.getStyleClass().add("midheader-hbox");
		midTopHbox1.setAlignment(Pos.CENTER);
		midTopHbox1.getChildren().add(lruLeftLabel);
		return midTopHbox1;
	}
	private HBox midTopHbox2() {
		lruLeftLabe2.getStyleClass().add("midheader-label");
		midTopHbox2.getStyleClass().add("midheader-hbox");
		midTopHbox2.setAlignment(Pos.CENTER);
		midTopHbox2.getChildren().add(lruLeftLabe2);
		return midTopHbox2;
	}

//	private HBox midTopHbox3() {
//		lruLeftLabe3.getStyleClass().add("midheader-label");
//		midTopHbox3.getStyleClass().add("midheader-hbox");
//		midTopHbox3.setAlignment(Pos.CENTER);
//		midTopHbox3.getChildren().add(lruLeftLabe3);
//		return midTopHbox3;
//	}
	
	private GridPane sruIsolationGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);


		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(16);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(21);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(21);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(21);
		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(21);
		
//		subTestGridPane.setHgap(20);
		subTestGridPane.setPadding(new Insets(5, 20, 10, 20));

		subTestGridPane.getStyleClass().add("mid-Gridepane-content");
		subTestGridPane.getColumnConstraints().addAll(firstColumn);
		subTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow);
		
		subTestGridPane.add(mandatoryTest(), 0, 0);
		subTestGridPane.add(spilLink(), 0, 1);
		subTestGridPane.add(pBit(), 0, 2);
		subTestGridPane.add(powerSupply(), 0, 3);
		subTestGridPane.add(adInterface(), 0, 4);
		
		return subTestGridPane;

	}
	
	private Label mandatoryTest() {
		mandatoryTest.getStyleClass().add("midlabel-content");
		mandatoryTest.setMaxWidth(Double.MAX_VALUE);
		mandatoryTest.setAlignment(Pos.CENTER);
		return mandatoryTest;

	}

	private Button spilLink() {
//	spilLink.getStyleClass().add("test-Button");
		spilLink.setMaxWidth(Double.MAX_VALUE);
//	spilLink.setPrefHeight(35);
		spilLink.setAlignment(Pos.CENTER);
		return spilLink;

	}

	private Button pBit() {
//	pBit.getStyleClass().add("test-Button");
		pBit.setMaxWidth(Double.MAX_VALUE);
//	pBit.setPrefHeight(35);
		pBit.setAlignment(Pos.CENTER);
		return pBit;

	}

	private Button powerSupply() {
//	powerSupply.getStyleClass().add("test-Button");
		powerSupply.setMaxWidth(Double.MAX_VALUE);
//	powerSupply.setPrefHeight(35);
		powerSupply.setAlignment(Pos.CENTER);
		return powerSupply;
	}

	private Button adInterface() {
//	adInterface.getStyleClass().add("test-Button");
		adInterface.setMaxWidth(Double.MAX_VALUE);
//	adInterface.setPrefHeight(35);
		adInterface.setAlignment(Pos.CENTER);
		return adInterface;
	}

	
	private GridPane sruSubTestGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(18);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(18);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(18);
		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(18);
		RowConstraints SixthRow = new RowConstraints();
		SixthRow.setPercentHeight(18);
		
		sruSubTestGridPane.setHgap(20);
		sruSubTestGridPane.setPadding(new Insets(5, 20, 10, 20));

		sruSubTestGridPane.getStyleClass().add("mid-Gridepane-content");
		sruSubTestGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
		sruSubTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow,SixthRow);
		
		sruSubTestGridPane.add(sruTest(), 0, 0,2,1);
		sruSubTestGridPane.add(digital(), 0, 1);
		sruSubTestGridPane.add(analog1R(), 0, 2);
		sruSubTestGridPane.add(analog1L(), 0, 3);
		sruSubTestGridPane.add(analog2(), 0, 4);
		
		sruSubTestGridPane.add(subTestVBox(), 1, 1,1,4);
	    sruSubTestGridPane.add(startTestHBox(), 0, 5, 2, 1);
	  
		return sruSubTestGridPane;

	}
	
	private Label sruTest() {
		sruTest.getStyleClass().add("midlabel-content");
		sruTest.setMaxWidth(Double.MAX_VALUE);
		sruTest.setAlignment(Pos.CENTER);
		return sruTest;
	}

	private Button digital() {
//	digital.getStyleClass().add("test-Button");
		digital.setMaxWidth(Double.MAX_VALUE);
//	digital.setPrefHeight(35);
		digital.setAlignment(Pos.CENTER);
		return digital;
	}

	private Button analog1R() {
//	analog1R.getStyleClass().add("test-Button");
		analog1R.setMaxWidth(Double.MAX_VALUE);
//	analog1R.setPrefHeight(35);
		analog1R.setAlignment(Pos.CENTER);
		return analog1R;
	}

	private Button analog1L() {
//	analog1L.getStyleClass().add("test-Button");
		analog1L.setMaxWidth(Double.MAX_VALUE);
//	analog1L.setPrefHeight(35);
		analog1L.setAlignment(Pos.CENTER);
		return analog1L;
	}

	private Button analog2() {
//	analog2.getStyleClass().add("test-Button");
		analog2.setMaxWidth(Double.MAX_VALUE);
//	analog2.setPrefHeight(35);
		analog2.setAlignment(Pos.CENTER);
		return analog2;
	}
	
private VBox subTestVBox() {
	
	subTestVBox.getChildren().addAll(subTest(),textAreaHBox());
	
	return subTestVBox;
}

	private CheckBox subTest() {

		subTest.getStyleClass().add("checkBox-midcontainer");
		subTest.setPadding(new Insets(5, 0, 0, 0));
		subTest.setMaxWidth(Double.MAX_VALUE);
		subTest.setPrefHeight(35);
		subTest.setAlignment(Pos.CENTER_LEFT);
		return subTest;

	}
	
	private HBox textAreaHBox() {
		
		textAreaHBox.setAlignment(Pos.CENTER);
		textAreaHBox.setPadding(new Insets(17, 0, 0, 0));
//		textAreaHBox.getStyleClass().add("lruTest-top-container");
		subTestTextArea.setMaxWidth(Double.MAX_VALUE);
		textAreaHBox.getChildren().add(subTestTextArea());
		return textAreaHBox;
	}

	private TextArea subTestTextArea() {

		subTestTextArea.getStyleClass().add("textarea-midcontainer");
//		subTestTextArea.setPrefHeight(100);
//		subTestTextArea.setMaxWidth(Double.MAX_VALUE);
//		subTestTextArea.setAlignment(Pos.CENTER);
//		subTestTextArea.setPadding(new Insets(20, 20, 20, 20));
		return subTestTextArea;
	}


	private HBox startTestHBox() {
		Button startTest = new Button("Start Test");
		startTest.setPrefWidth(200);
//		startTestHBox.getStyleClass().add("midheader-hbox");
//		startTestHBox.setPadding(new Insets(5,0,5,0));
		startTestHBox.setAlignment(Pos.CENTER);
		startTestHBox.getChildren().add(startTest);

		return startTestHBox;
	}

	private GridPane goNOGOGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(18);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(18);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(18);
		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(18);
		RowConstraints SixthRow = new RowConstraints();
		SixthRow.setPercentHeight(18);

		goNOGOGridPane.setPadding(new Insets(5, 20, 10, 20));
		goNOGOGridPane.getStyleClass().add("mid-Gridepane-content");
		goNOGOGridPane.getColumnConstraints().addAll(firstColumn);
		goNOGOGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow, SixthRow);
//		goNOGOGridPane.add(completeTest(), 0, 1);
//		goNOGOGridPane.add(piCheck(), 0, 2);
//		goNOGOGridPane.add(ofpLoading(), 0, 3);
		goNOGOGridPane.add(goNoGoVBox(), 0, 1, 1, 4);
		goNOGOGridPane.add(goNoGoLabel(), 0, 5);
		

		return goNOGOGridPane;
	}
	
	private VBox goNoGoVBox() {
//		goNoGoVBox.setPadding(new Insets(5));
		goNoGoVBox.getChildren().addAll( completeTest() ,piCheck(), ofpLoading());
		goNoGoVBox.setAlignment(Pos.CENTER);
		return goNoGoVBox;
	}

	private Button completeTest() {
		Button completeTest = new Button("Complete Test");
		completeTest.getStyleClass().add("test-Button");
		completeTest.setMaxWidth(Double.MAX_VALUE);
		return completeTest;
	}

	private Button piCheck() {
		Button piCheck = new Button("PI Check");
		piCheck.getStyleClass().add("test-Button");
		piCheck.setMaxWidth(Double.MAX_VALUE);
		return piCheck;
	}

	private Button ofpLoading() {
		Button ofpLoading = new Button("OFP Loading");
		ofpLoading.getStyleClass().add("test-Button");
		ofpLoading.setMaxWidth(Double.MAX_VALUE);
		return ofpLoading;
	}

	
	private HBox goNoGoLabel() {
		Label goNoGo = new Label("GO/NOGO");
		goNoGo.getStyleClass().add("label-gonogo");
		goNoGo.setPadding(new Insets(5, 0, 5, 0));
		goNoGo.setPrefWidth(200);
		goNoGo.setAlignment(Pos.CENTER);
		goNoGoLabel.setAlignment(Pos.CENTER);
		goNoGoLabel.getChildren().add(goNoGo);
		return goNoGoLabel;
	}
	
	private GridPane lruTestBottomContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomGridPane.getColumnConstraints().addAll(firstColumn);
		bottomGridPane.getRowConstraints().addAll(firstRow);
		
		bottomGridPane.add(createTableView(), 0, 0);

		return bottomGridPane;

	}
	

	private TableView<LRUTest> createTableView() {
		TableView<LRUTest> tableView = new TableView<>();
		tableView.getStylesheets()
		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//		tableView.setPrefWidth(1250);
//		tableView.setPrefHeight(900);

		TableColumn<LRUTest, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<LRUTest, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<LRUTest, String> faultPinSuggestionColumn = new TableColumn<>("Fault Pin Suggestion");
		faultPinSuggestionColumn.setCellValueFactory(new PropertyValueFactory<>("faultPinSuggestion"));
		faultPinSuggestionColumn.setReorderable(false);
		faultPinSuggestionColumn.setSortable(false);
		faultPinSuggestionColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<LRUTest, String> interfaceSignalColumn = new TableColumn<>("Interface Signal");
		interfaceSignalColumn.setCellValueFactory(new PropertyValueFactory<>("interfaceSignal"));
		interfaceSignalColumn.setReorderable(false);
		interfaceSignalColumn.setSortable(false);
		interfaceSignalColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<LRUTest, String> channelColumn = new TableColumn<>("Channel");
		channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
		channelColumn.setReorderable(false);
		channelColumn.setSortable(false);
		channelColumn.setStyle("-fx-alignment: CENTER;");

		tableView.getColumns().addAll(fileNameColumn, resultColumn, faultPinSuggestionColumn, interfaceSignalColumn,
				channelColumn);

		tableView.setItems(lruTestTableData);

		return tableView;
	}




	
	
	}
