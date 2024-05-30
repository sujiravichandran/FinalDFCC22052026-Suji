package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.model.CheckSumList;
import com.teclever.dfcc.model.SelfTest;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;



public class SelfTestController {
	
	private GridPane selfTestMainContainerGridPane = new GridPane();
	
	private HBox headingHbox = new HBox(10);
	
	private HBox topButton = new HBox(10);
	
	private GridPane headingGridPane = new GridPane();
	
	private GridPane midContainerGridPane = new GridPane();
	
	private HBox midTopHbox1 = new HBox(10);
	
	private HBox midTopHbox2 = new HBox(10);
	
	private VBox midTopVbox1 = new VBox(10);
	
	private VBox midTopVbox2 = new VBox(10);
	
	private VBox midTopVbox3 = new VBox(10);
	
	private HBox bottomHbox = new HBox(30);
	
	private Label pageHeading = new Label("SELF TEST");
	private Label rack2= new Label("CPci");
	private Label rack1= new Label("RACK - 1");
	private ObservableList<SelfTest> selfTestTableData = FXCollections.observableArrayList();

	
	public GridPane createSelfTestMainContainerGridPane() {
		
		selfTestMainContainerGridPane.getStylesheets()
				.add(getClass().getResource("/com/teclever/dfcc/ui/css/SelfTest.css").toExternalForm());
		selfTestMainContainerGridPane.getStyleClass().add("selfTest-main-container");
		
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(60);
		
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(33);
		
		selfTestMainContainerGridPane.setVgap(5);
		
		selfTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		selfTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		selfTestMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
		selfTestMainContainerGridPane.add(headingGridPane(), 0, 0);
		selfTestMainContainerGridPane.add(selfTestMidContainer(), 0, 1);
		selfTestMainContainerGridPane.add(selfTestBottomContainer(), 0, 2);
		
		return selfTestMainContainerGridPane;
	}
	
	public GridPane headingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);
		headingGridPane.getStyleClass().add("selfTest-top-container");

		headingGridPane.add(headingHbox(), 0, 0);
		headingGridPane.add(topButton(), 1, 0);
		

		return headingGridPane;

	}
	
	private HBox headingHbox() {
		
		pageHeading.getStyleClass().add("headerLabel");
//		pageHeading.setPadding(new Insets(0, 0, 0, 0));
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);
		
		return headingHbox;
	}
	
	private HBox topButton() {
		Button startTest = new Button("START TEST");
		topButton.setPadding(new Insets(0, 5, 0, 0));
		topButton.setAlignment(Pos.CENTER_RIGHT);
		topButton.getChildren().add(startTest);
		return topButton;
	}
	
	private GridPane selfTestMidContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(33.33);
		
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(33.33);
		
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(33.33);
		
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(8);
		
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(92);
		
		midContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn );
		midContainerGridPane.getRowConstraints().addAll(firstRow,secondRow );
		
		midContainerGridPane.add(midTopHbox1(), 0, 0,2,1);
		
		midContainerGridPane.add(midTopHbox2(), 2, 0);
		
		midContainerGridPane.add(midTopVbox1(), 0, 1);
		
		midContainerGridPane.add(midTopVbox2(), 1,1);
		
		midContainerGridPane.add(midTopVbox3(), 2,1);
		
		midContainerGridPane.setHgap(5);
		
		midContainerGridPane.setVgap(5);
		
		midContainerGridPane.getStyleClass().add("selfTest-mid-Container");
		midContainerGridPane.setAlignment(Pos.CENTER);
		return midContainerGridPane;
	}
	
	
	private HBox midTopHbox1() {
		
		
		rack1.getStyleClass().add("racklabel");
		midTopHbox1.getStyleClass().add("rackhbox");
		midTopHbox1.setAlignment(Pos.CENTER);
		midTopHbox1.getChildren().add(rack1);
		
		return midTopHbox1;
	}
	
	private HBox midTopHbox2() {
		
		
		rack2.getStyleClass().add("racklabel");
		
		midTopHbox2.getStyleClass().add("rackhbox");
		midTopHbox2.setAlignment(Pos.CENTER);
		midTopHbox2.getChildren().add(rack2);
		
		return midTopHbox2;
	}
	
	private VBox midTopVbox1() {
		Label label1= new Label("Board-1(RUD)");
		label1.getStyleClass().add("selTest-label-left");
		label1.setMaxWidth(Double.MAX_VALUE);
		label1.setPrefHeight(35);
		
		Label label2= new Label("Board-2(LIE)");
		label2.getStyleClass().add("selTest-label-left");
		label2.setMaxWidth(Double.MAX_VALUE);
		label2.setPrefHeight(35);
		
		
		Label label3= new Label("Board-3(LOE)");
		label3.getStyleClass().add("selTest-label-left");
		label3.setMaxWidth(Double.MAX_VALUE);
		label3.setPrefHeight(35);
		
		Label label4= new Label("Board-4(RIE)");
		label4.getStyleClass().add("selTest-label-left");
		label4.setMaxWidth(Double.MAX_VALUE);
		label4.setPrefHeight(35);
		
		Label label5= new Label("Board-5(ROE)");
		label5.getStyleClass().add("selTest-label-left");
		label5.setMaxWidth(Double.MAX_VALUE);
		label5.setPrefHeight(35);

		Label label6= new Label("Board-6(LIS, LMS, LOS)");
		label6.getStyleClass().add("selTest-label-left");
		label6.setMaxWidth(Double.MAX_VALUE);
		label6.setPrefHeight(35);
		
		Label label7= new Label("Board-7(RIS, RMS, ROS)");
		label7.getStyleClass().add("selTest-label-left");
		label7.setMaxWidth(Double.MAX_VALUE);
		label7.setPrefHeight(35);
		
		Label label8= new Label("Board-8(PCS, RPS)");
		label8.getStyleClass().add("selTest-label-left");
		label8.setMaxWidth(Double.MAX_VALUE);
		label8.setPrefHeight(35);
		
		Label label9= new Label("Board-9(AOSS-1,AOSS-2,LA,RA");
		label9.getStyleClass().add("selTest-label-left");
		label9.setMaxWidth(Double.MAX_VALUE);
		label9.setPrefHeight(35);
		
		midTopVbox1.setPadding(new Insets(0, 5, 0, 5));
		midTopVbox1.getStyleClass().add("rackVbox");
		midTopVbox1.setAlignment(Pos.CENTER);
		midTopVbox1.getChildren().addAll(label1, label2, label3, label4, label5, label6,label7, label8, label9);
		return midTopVbox1;
	}
	
private VBox midTopVbox2() {
		
	Label label10= new Label("Board-10(CWP, SPDCM, REFPRB)");
	label10.getStyleClass().add("selTest-label-right");
	label10.setMaxWidth(Double.MAX_VALUE);
	label10.setPrefHeight(30);
	
	Label label11= new Label("Board-11(ASA, DPSC)");
	label11.getStyleClass().add("selTest-label-right");
	label11.setMaxWidth(Double.MAX_VALUE);
	label11.setPrefHeight(30);
	
	Label label12= new Label("Board-12(GSE, FTI)");
	label12.getStyleClass().add("selTest-label-right");
	label12.setMaxWidth(Double.MAX_VALUE);
	label12.setPrefHeight(30);
	
	Label label13= new Label("Board-13(AD, AMD,CSD,SSCDR,MIP)");
	label13.getStyleClass().add("selTest-label-right");
	label13.setMaxWidth(Double.MAX_VALUE);
	label13.setPrefHeight(30);
	
	Label label14= new Label("Board-14(LG,FCP,FTU)");
	label14.getStyleClass().add("selTest-label-right");
	label14.setMaxWidth(Double.MAX_VALUE);
	label14.setPrefHeight(30);

	Label label15= new Label("Board-15(RSA-1)");
	label15.getStyleClass().add("selTest-label-right");
	label15.setMaxWidth(Double.MAX_VALUE);
	label15.setPrefHeight(30);
	
	Label label16= new Label("Board-16(RSA-2)");
	label16.getStyleClass().add("selTest-label-right");
	label16.setMaxWidth(Double.MAX_VALUE);
	label16.setPrefHeight(30);
	
	Label label17= new Label("Board-17(APP,AOA EXC MON)");
	label17.getStyleClass().add("selTest-label-right");
	label17.setMaxWidth(Double.MAX_VALUE);
	label17.setPrefHeight(30);
	
	Label label18= new Label("Board-18(OPEN/GND,AO,DIFF AI/AO");
	label18.getStyleClass().add("selTest-label-right");
	label18.setMaxWidth(Double.MAX_VALUE);
	label18.setPrefHeight(35);
	
	Label label19= new Label("Board-19(SPARE LVDT, 28V/OPEN,OPEN/GND");
	label19.getStyleClass().add("selTest-label-right");
	label19.setMaxWidth(Double.MAX_VALUE);
	label19.setPrefHeight(30);
	
	midTopVbox2.setPadding(new Insets(0, 5, 0, 5));
	midTopVbox2.getStyleClass().add("rackVbox");
	midTopVbox2.setAlignment(Pos.CENTER);
	midTopVbox2.getChildren().addAll(label10, label11, label12, label13, label14, label15,label16, label17, label18,label19);
	
	
		midTopVbox2.getStyleClass().add("rackVbox");
		midTopVbox2.setAlignment(Pos.CENTER);
		
		return midTopVbox2;
	}

private VBox midTopVbox3() {
	
	Label label1= new Label("1553B");
	label1.getStyleClass().add("selTest-label-right");
	label1.setMaxWidth(Double.MAX_VALUE);
	
	Label label2= new Label("Connect Loopback P39 and P50");
	label2.getStyleClass().add("selTest-label-right");
	label2.setMaxWidth(Double.MAX_VALUE);
	
	Label label3= new Label("RS422_1");
	label3.getStyleClass().add("selTest-label-right");
	label3.setMaxWidth(Double.MAX_VALUE);
	
	Label label4= new Label("RS422_2");
	label4.getStyleClass().add("selTest-label-right");
	label4.setMaxWidth(Double.MAX_VALUE);
	
	midTopVbox3.setPadding(new Insets(0, 5, 0, 5));
	midTopVbox3.getStyleClass().add("rackVbox");
	midTopVbox3.setAlignment(Pos.CENTER);
	midTopVbox3.getChildren().addAll(label1, label2, label3, label4);
	
	
	midTopVbox3.getStyleClass().add("rackVbox");
	midTopVbox3.setAlignment(Pos.CENTER);
	
	return midTopVbox3;
}
	
private TableView<SelfTest> createTableView(ObservableList<SelfTest> selfTestTableData) {
	TableView<SelfTest> tableView = new TableView<>();
	tableView.getStyleClass().add("selfTest-table .table-cell");
	tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	tableView.setPrefWidth(1250);
	 tableView.setPrefHeight(900); 

	TableColumn<SelfTest, String> fileNameColumn = new TableColumn<>("File Name");
	fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
	fileNameColumn.setReorderable(false);
	fileNameColumn.setSortable(false);
	fileNameColumn.setStyle("-fx-alignment: CENTER;");

	TableColumn<SelfTest, String> resultColumn = new TableColumn<>("Result");
	resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
	resultColumn.setReorderable(false);
	resultColumn.setSortable(false);
	resultColumn.setStyle("-fx-alignment: CENTER;");


	tableView.getColumns().addAll(fileNameColumn,resultColumn );
	tableView.setItems(selfTestTableData);

	return tableView;
}



	private HBox selfTestBottomContainer() {
		TableView<SelfTest> tableView = createTableView(selfTestTableData);
		
		bottomHbox.getStyleClass().add("selfTest-bottom-Container");
		bottomHbox.getChildren().addAll(tableView);

		return bottomHbox;

	}
}	
	

	

	
	
	
	
