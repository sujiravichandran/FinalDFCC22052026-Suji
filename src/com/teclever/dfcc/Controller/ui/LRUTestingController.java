package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.model.LRUTest;
import com.teclever.dfcc.model.SelfTest;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

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
	private HBox buttonHbox = new HBox(10);
	private HBox goButton = new HBox();
	private HBox nogoButton = new HBox();
	
	private GridPane lrumidContainerGridPane = new GridPane();
	private HBox midTopHbox1 = new HBox();
	private Label lruLeftLabel= new Label("SRU Identification and Isolation");
	private HBox midTopHbox2 = new HBox();
	private Label lruLeftLabe2= new Label("GO and NOGO TEST");
	private HBox midTopHbox3 = new HBox();
	private Label lruLeftLabe3= new Label("Sub Test / Interface:");
	private GridPane sruIsolationGridPane = new GridPane();
	private Label mandatoryTest= new Label("Mandatory Test");
	private Button spilLink= new Button("Spil Link");
	private Button pBit= new Button("PBIT");
	private Button powerSupply= new Button("Power Supply");
	private Button adInterface= new Button("A/D-D/A Interface");
	
	private Label sruTest= new Label("SRU Test");
	private Button digital= new Button("Digital");
	private Button analog1R= new Button("Analog-1 Right");
	private Button analog1L= new Button("Analog-1 Left");
	private Button analog2= new Button("Analog 2");
	
	private VBox goNOGO = new VBox(30);
	
	private GridPane subTestGridPane = new GridPane();
	private CheckBox subTest = new CheckBox("Select All Sub-Test:");
	private TextArea subTestTextArea = new TextArea();
	private HBox startTestHBox= new HBox();
	
	private HBox testSummaryHbox = new HBox(30);
	
	private HBox bottomHbox = new HBox(30);
	private ObservableList<LRUTest> lruTestTableData = FXCollections.observableArrayList();

	
public GridPane createlruTestMainContainerGridPane() {
		
	lruTestMainContainerGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/LRUTest.css").toExternalForm());
	lruTestMainContainerGridPane.getStyleClass().add("lruTest-main-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(0.5);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(100);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(0.5);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(0.5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(99);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(0.5);
		
		lruTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		lruTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		
		lruTestMainContainerGridPane.add(lruContainerGridPane(), 1, 1);
		
	
		return lruTestMainContainerGridPane;
	
}

public GridPane lruContainerGridPane() {
	ColumnConstraints firstColumn = new ColumnConstraints();
	firstColumn.setPercentWidth(100);
	
	RowConstraints firstRow = new RowConstraints();
	firstRow.setPercentHeight(0.5);
	RowConstraints secondRow = new RowConstraints();
	secondRow.setPercentHeight(7);
	RowConstraints thirdRow = new RowConstraints();
	thirdRow.setPercentHeight(1);
	RowConstraints fourthRow = new RowConstraints();
	fourthRow.setPercentHeight(40);
	RowConstraints fifthRow = new RowConstraints();
	fifthRow.setPercentHeight(1);
	RowConstraints sixthRow = new RowConstraints();
	sixthRow.setPercentHeight(7);
	RowConstraints seventhRow = new RowConstraints();
	seventhRow.setPercentHeight(1);
	RowConstraints eigthRow = new RowConstraints();
	eigthRow.setPercentHeight(43);
	RowConstraints ninethRow = new RowConstraints();
	ninethRow.setPercentHeight(0.5);
	
	lruContainerGridPane.getColumnConstraints().addAll(firstColumn);
	lruContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow,fourthRow,fifthRow,sixthRow,seventhRow,eigthRow,ninethRow );
	
	lruContainerGridPane.add(lruheadingGridPane(),0,1);
	lruContainerGridPane.add(lruTestMidContainer(),0,3);
	lruContainerGridPane.add(testSummaryHbox(),0,5);
	lruContainerGridPane.add(lruTestBottomContainer(),0,7);
	
	return lruContainerGridPane;
	
}


public GridPane lruheadingGridPane() {
	ColumnConstraints firstColumn = new ColumnConstraints();
	firstColumn.setPercentWidth(50);
	
	ColumnConstraints secondColumn = new ColumnConstraints();
	secondColumn.setPercentWidth(50);

	RowConstraints firstRow = new RowConstraints();
	firstRow.setPercentHeight(100);
	
	headingGridPane.getStyleClass().add("lruTest-top-container");
	headingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
	headingGridPane.getRowConstraints().addAll(firstRow);
	
	headingGridPane.add(headingHbox(), 0, 0);
	headingGridPane.add(buttonHbox(), 1,0);

	return headingGridPane;

}

private HBox headingHbox() {
	Label pageHeading = new Label("LRU TEST");
	pageHeading.getStyleClass().add("lrutest-top-header");
	headingHbox.setAlignment(Pos.CENTER_LEFT);
	headingHbox.getChildren().add(pageHeading);
	
	return headingHbox;
}

private HBox buttonHbox() {

	buttonHbox.setAlignment(Pos.CENTER_RIGHT);
	buttonHbox.getChildren().addAll(goButton(), nogoButton());
	return buttonHbox;
}

private HBox goButton() {
	Button goTest = new Button("GO");

	goButton.setAlignment(Pos.CENTER_LEFT);
	goButton.getChildren().add(goTest);
	return goButton;
}

private HBox nogoButton() {
	Button nogoTest = new Button("NOGO");

	nogoButton.setAlignment(Pos.CENTER_RIGHT);
	nogoButton.getChildren().add(nogoTest);
	return nogoButton;
}


private GridPane lruTestMidContainer() {
	ColumnConstraints firstColumn = new ColumnConstraints();
	firstColumn.setPercentWidth(20);
	
	ColumnConstraints secondColumn = new ColumnConstraints();
	secondColumn.setPercentWidth(20);
	
	ColumnConstraints thirdColumn = new ColumnConstraints();
	thirdColumn.setPercentWidth(20);
	
	ColumnConstraints fourthColumn = new ColumnConstraints();
	fourthColumn.setPercentWidth(40);
	
	RowConstraints firstRow = new RowConstraints();
	firstRow.setPercentHeight(10);
	
	RowConstraints secondRow = new RowConstraints();
	secondRow.setPercentHeight(90);
	
	lrumidContainerGridPane.setHgap(5);
	lrumidContainerGridPane.setVgap(5);
	
	lrumidContainerGridPane.getStyleClass().add("lruTest-mid-container");
	lrumidContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn );
	lrumidContainerGridPane.getRowConstraints().addAll(firstRow,secondRow );
	
	
	 lrumidContainerGridPane.add(midTopHbox1(), 0, 0,2,1);  // Merge columns 0 and 1
	 lrumidContainerGridPane.add(midTopHbox2(), 2, 0);        // Add to column 2
	 lrumidContainerGridPane.add(midTopHbox3(), 3, 0);
	 lrumidContainerGridPane.add(sruIsolationGridPane(),0, 1,2,1);
	 lrumidContainerGridPane.add(goNOGO(),2,1);
	 lrumidContainerGridPane.add(subTestGridPane(),3,1);
	 
	 
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

private HBox midTopHbox3() {
	lruLeftLabe3.getStyleClass().add("midheader-label");
	midTopHbox3.getStyleClass().add("midheader-hbox");
	midTopHbox3.setAlignment(Pos.CENTER);
	midTopHbox3.getChildren().add(lruLeftLabe3);
	return midTopHbox3;
}


private GridPane sruIsolationGridPane() {
	
	ColumnConstraints firstColumn = new ColumnConstraints();
	firstColumn.setPercentWidth(50);
	ColumnConstraints secondColumn = new ColumnConstraints();
	secondColumn.setPercentWidth(50);
	

	RowConstraints firstRow = new RowConstraints();
	firstRow.setPercentHeight(20);
	RowConstraints secondRow = new RowConstraints();
	secondRow.setPercentHeight(20);
	RowConstraints thirdRow = new RowConstraints();
	thirdRow.setPercentHeight(20);
	RowConstraints fourthRow = new RowConstraints();
	fourthRow.setPercentHeight(20);
	RowConstraints fivthRow = new RowConstraints();
	fivthRow.setPercentHeight(20);
	
//	sruIsolationGridPane.setVgap(5);
	sruIsolationGridPane.setHgap(5);
	sruIsolationGridPane.setPadding(new Insets(0,5,0,5));
	
	sruIsolationGridPane.getStyleClass().add("mid-Gridepane-content");
	sruIsolationGridPane.getColumnConstraints().addAll(firstColumn, secondColumn );
	sruIsolationGridPane.getRowConstraints().addAll(firstRow,secondRow, thirdRow,fourthRow, fivthRow  );
	
	sruIsolationGridPane.add(mandatoryTest(), 0, 0);
	sruIsolationGridPane.add(spilLink(), 0, 1);
	sruIsolationGridPane.add(pBit(), 0, 2);
	sruIsolationGridPane.add(powerSupply(), 0, 3);
	sruIsolationGridPane.add(adInterface(), 0, 4);

	sruIsolationGridPane.add(sruTest(), 1, 0);
	sruIsolationGridPane.add(digital(), 1, 1);
	sruIsolationGridPane.add(analog1R(), 1, 2);
	sruIsolationGridPane.add(analog1L(), 1, 3);
	sruIsolationGridPane.add(analog2(), 1, 4);
	
	return sruIsolationGridPane;
}


private Label mandatoryTest() {
	mandatoryTest.getStyleClass().add("midlabel-content");
	mandatoryTest.setMaxWidth(Double.MAX_VALUE);
	mandatoryTest.setAlignment(Pos.CENTER);
	return mandatoryTest;
	
}


private Button spilLink()
{
	spilLink.getStyleClass().add("test-Button");
	spilLink.setMaxWidth(Double.MAX_VALUE);
	spilLink.setPrefHeight(35);
	spilLink.setAlignment(Pos.CENTER);
	return spilLink;
	
}

private Button pBit()
{
	pBit.getStyleClass().add("test-Button");
	pBit.setMaxWidth(Double.MAX_VALUE);
	pBit.setPrefHeight(35);
	pBit.setAlignment(Pos.CENTER);
	return pBit;
	
}


private Button powerSupply()
{
	powerSupply.getStyleClass().add("test-Button");
	powerSupply.setMaxWidth(Double.MAX_VALUE);
	powerSupply.setPrefHeight(35);
	powerSupply.setAlignment(Pos.CENTER);
	return powerSupply;
}

private Button adInterface()
{
	adInterface.getStyleClass().add("test-Button");
	adInterface.setMaxWidth(Double.MAX_VALUE);
	adInterface.setPrefHeight(35);
	adInterface.setAlignment(Pos.CENTER);
	return adInterface;
}

private Label sruTest() {
	sruTest.getStyleClass().add("midlabel-content");
	sruTest.setMaxWidth(Double.MAX_VALUE);
	sruTest.setAlignment(Pos.CENTER);
	return sruTest;
}


private Button digital()
{
	digital.getStyleClass().add("test-Button");
	digital.setMaxWidth(Double.MAX_VALUE);
	digital.setPrefHeight(35);
	digital.setAlignment(Pos.CENTER);
	return digital;
}

private Button analog1R()
{
	analog1R.getStyleClass().add("test-Button");
	analog1R.setMaxWidth(Double.MAX_VALUE);
	analog1R.setPrefHeight(35);
	analog1R.setAlignment(Pos.CENTER);
	return analog1R;
}


private Button analog1L()
{
	analog1L.getStyleClass().add("test-Button");
	analog1L.setMaxWidth(Double.MAX_VALUE);
	analog1L.setPrefHeight(35);
	analog1L.setAlignment(Pos.CENTER);
	return analog1L;
}

private Button analog2()
{
	analog2.getStyleClass().add("test-Button");
	analog2.setMaxWidth(Double.MAX_VALUE);
	analog2.setPrefHeight(35);
	analog2.setAlignment(Pos.CENTER);
	return analog2;
}
private VBox goNOGO() {
	Button completeTest= new Button("Complete Test");
	completeTest.getStyleClass().add("test-Button");
	completeTest.setMaxWidth(Double.MAX_VALUE);
	completeTest.setPrefHeight(35);
	
	Button ofpLoading= new Button("OFP Loading");
	ofpLoading.getStyleClass().add("test-Button");
	ofpLoading.setMaxWidth(Double.MAX_VALUE);
	ofpLoading.setPrefHeight(35);
	
	Button piCheck= new Button("PI Check");
	piCheck.getStyleClass().add("test-Button");
	piCheck.setMaxWidth(Double.MAX_VALUE);
	piCheck.setPrefHeight(35);
	
	goNOGO.setPadding(new Insets(5));
	goNOGO.getStyleClass().add("test-Button-vbox");
	goNOGO.setAlignment(Pos.CENTER);
	goNOGO.getChildren().addAll(completeTest, ofpLoading, piCheck );
	return goNOGO;
}

 
 private GridPane subTestGridPane() {
	 ColumnConstraints firstColumn = new ColumnConstraints();
	 firstColumn.setPercentWidth(100);
	 
	 RowConstraints firstRow = new RowConstraints();
	 firstRow.setPercentHeight(10);
	 
	 RowConstraints secondRow = new RowConstraints();
	 secondRow.setPercentHeight(70);
	 
	 RowConstraints thirdRow = new RowConstraints();
	 thirdRow.setPercentHeight(20);
	 
	 
	 subTestGridPane.getStyleClass().add("mid-Gridepane-content");
	 subTestGridPane.getColumnConstraints().addAll(firstColumn );
	 subTestGridPane.getRowConstraints().addAll(firstRow,secondRow, thirdRow  );
	 subTestGridPane.add(subTest(), 0, 0);
	 subTestGridPane.add(subTestTextArea(), 0, 1);
	 subTestGridPane.add(startTestHBox(), 0, 2);
	 
	return subTestGridPane; 
	 
 }
 
 private CheckBox subTest() {
	 
	 subTest.getStyleClass().add("checkBox-midcontainer");
	 subTest.setPadding(new Insets(0, 5, 0, 5));
	 subTest.setMaxWidth(Double.MAX_VALUE);
	 subTest.setPrefHeight(35);
	 subTest.setAlignment(Pos.CENTER_LEFT);
     return subTest;

	 
 }
 
 private TextArea subTestTextArea (){
	 
	 subTestTextArea.getStyleClass().add("textarea-midcontainer");
	 subTestTextArea.setMaxWidth(Double.MAX_VALUE);
	
	 subTestTextArea.setPrefHeight(35);
	 subTestGridPane.setPadding(new Insets(0, 5, 0, 5));
	return subTestTextArea; 
 }
 
 private HBox startTestHBox() {

	 Button startTest= new Button("Start Test");
	 startTest.getStyleClass().add("test-Button");
	 startTest.setPrefHeight(35);
	 startTestHBox.setAlignment(Pos.CENTER);
	 startTestHBox.getChildren().addAll(startTest );
	 
	return startTestHBox;
 }
 
 
 private HBox testSummaryHbox() {
		
	 Button testSummary= new Button("Test Summary");
	 testSummary.getStyleClass().add("test-Button");
	 testSummary.setPrefHeight(35);
//	 testSummaryHbox.setPadding(new Insets(0, 0, 0, 5));
	 testSummaryHbox.setAlignment(Pos.CENTER_LEFT);
	 testSummaryHbox.getStyleClass().add("test-summary");
	 testSummaryHbox.getChildren().addAll(testSummary );
	 
	return testSummaryHbox;
	 
 }
 
 private TableView<LRUTest> createTableView(ObservableList<LRUTest> lruTestTableData) {
		TableView<LRUTest> tableView = new TableView<>();
		tableView.getStyleClass().add("lru-table .table-cell");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setPrefWidth(1250);
		tableView.setPrefHeight(900); 

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
		
		
		tableView.getColumns().addAll(fileNameColumn,resultColumn,faultPinSuggestionColumn, interfaceSignalColumn, channelColumn );
		tableView.setItems(lruTestTableData);

		return tableView;
	}
 
 
private HBox lruTestBottomContainer() {
	
	TableView<LRUTest> tableView = createTableView(lruTestTableData);
	bottomHbox.getStyleClass().add("lruTest-bottom-container");
	bottomHbox.getChildren().addAll(tableView);
	


	return bottomHbox;

}

	
	
	
	
	
}
