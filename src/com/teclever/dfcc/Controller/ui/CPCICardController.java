package com.teclever.dfcc.Controller.ui;

import java.io.IOException;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.CardDetailsDTO;
import com.teclever.dfcc.datastore.dto.CardDetailsResponseDTO;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.model.CPCICard;
import com.teclever.dfcc.utils.CustomTableView;
import com.teclever.dfcc.utils.Notifications;
import com.teclever.dfcc.utils.TableViewFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class CPCICardTableViewFactory implements TableViewFactory<CPCICard> {
	@Override
	public CustomTableView<CPCICard> createTableView(ObservableList<CPCICard> items, boolean addUserColumn,
			boolean addCheckboxColumn) {
		return new CustomTableView<>(items, CPCICard.class, addUserColumn, addCheckboxColumn);
	}
}

public class CPCICardController {
	private GridPane cpciCardMainGridPane = new GridPane();
	private GridPane cpciCardTitleGridPane = new GridPane();
	private GridPane cpciCardTableGridPane = new GridPane();
	private GridPane cpciCardOptionGridPane = new GridPane();
	
	private Button addUserBtn = new Button();
	
	private ComboBox<String> uut_type_field;
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	private int AITESS_ID;

	private ComboBox<String> driver_type_field ;
	private ObservableList<AitessConfigurationDto> driverDataList = FXCollections.observableArrayList();
	private ObservableList<String> driverNameList = FXCollections.observableArrayList();
		
	private Label driverVersionLabel = new Label("DRIVER VERSION");
	
	private TableViewFactory<CPCICard> cpciCardFactory = new CPCICardTableViewFactory();
	private CustomTableView<CPCICard> customTableView_cpciCard;
	
	AitessConfigurationManagement aitessConfigurationManagement = new AitessConfigurationManagement();
	
	
	
	
	public CPCICardController() {
		uut_type_field = new ComboBox<>();
		driver_type_field = new ComboBox<>();
		initializeUUTTypeComboBox();
		initializeCpciCardTable();
	}
	
	public void refreshCpciCardList() {
		getCpciCardTableData();
	}

	public GridPane createcpciCardConfigGridPane() {
			cpciCardMainGridPane.getStylesheets()
					.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/CPCICard.css").toExternalForm());
			cpciCardMainGridPane.getStyleClass().add("cpci-card-container");
			
			ColumnConstraints firstColumn = new ColumnConstraints();
			firstColumn.setPercentWidth(100);

			RowConstraints firstRow = new RowConstraints();
			firstRow.setPercentHeight(5);
			RowConstraints secondRow = new RowConstraints();
			secondRow.setPercentHeight(7);
			RowConstraints  thirdRow= new RowConstraints();
			thirdRow.setPercentHeight(88);
			
			cpciCardMainGridPane.setPadding(new Insets(10));
			cpciCardMainGridPane.setVgap(5);
			cpciCardMainGridPane.getColumnConstraints().addAll(firstColumn);
			cpciCardMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

			cpciCardMainGridPane.add(createCpciCardTitleGridPane(), 0, 0);
			cpciCardMainGridPane.add(createCpciCardOptionPane(), 0, 1);
			cpciCardMainGridPane.add(createCpciCardTable(), 0, 2);

		return cpciCardMainGridPane;
	}
		private GridPane createCpciCardTitleGridPane() {
			ColumnConstraints firstColumn = new ColumnConstraints();
			firstColumn.setPercentWidth(50);
			ColumnConstraints secondColumn = new ColumnConstraints();
			secondColumn.setPercentWidth(50);

			RowConstraints firstRow = new RowConstraints();
			firstRow.setPercentHeight(100);

			cpciCardTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
			cpciCardTitleGridPane.getRowConstraints().addAll(firstRow);

			HBox titleBox = new HBox();
			titleBox.setAlignment(Pos.CENTER_LEFT);
			Label title = new Label("cPCI CARDS");
			title.getStyleClass().add("cpci-card-title");
			titleBox.getChildren().add(title);

			HBox addUserBox = new HBox();
			addUserBox.setAlignment(Pos.CENTER_RIGHT);
			addUserBtn.setText("+ ADD cPCI CARD");;
			addUserBtn.setDisable(true);
			addUserBox.getChildren().add(addUserBtn);

			addUserBtn.setOnAction(e -> {
				handleAddEditButtonClicked(null);
			});

			cpciCardTitleGridPane.add(titleBox, 0, 0);
			cpciCardTitleGridPane.add(addUserBox, 1, 0);


			return cpciCardTitleGridPane;
		}
		private GridPane createCpciCardOptionPane() {
			ColumnConstraints firstColumn = new ColumnConstraints();
			firstColumn.setPercentWidth(15);
			ColumnConstraints secondColumn = new ColumnConstraints();
			secondColumn.setPercentWidth(15);
			ColumnConstraints thirdColumn = new ColumnConstraints();
			thirdColumn.setPercentWidth(70);

			RowConstraints firstRow = new RowConstraints();
			firstRow.setPercentHeight(100);

			cpciCardOptionGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
			cpciCardOptionGridPane.getRowConstraints().addAll(firstRow);
			cpciCardOptionGridPane.setAlignment(Pos.CENTER);
			cpciCardOptionGridPane.setPadding(new Insets(10));

			cpciCardOptionGridPane.add(createUutTypeField(), 0, 0);
			cpciCardOptionGridPane.add(createDriverTypeField(), 1, 0);
			cpciCardOptionGridPane.add(createDriverVersion(), 2, 0);


			cpciCardOptionGridPane.getStyleClass().add("cpci-card-option-container");
			return cpciCardOptionGridPane;
		}
		private HBox createUutTypeField() {
			uut_type_field.setPromptText("UUT TYPE");

			HBox uutTypeHBox = new HBox(10);
			uutTypeHBox.setAlignment(Pos.CENTER);
			uutTypeHBox.getChildren().add(uut_type_field);

			return uutTypeHBox;
		}
		
		private HBox createDriverTypeField() {
			driver_type_field.setPromptText("DRIVER TYPE");

			HBox testTypeHBox = new HBox(10);
			testTypeHBox.setAlignment(Pos.CENTER);
			testTypeHBox.getChildren().add(driver_type_field);

			return testTypeHBox;
		}
		
		private HBox createDriverVersion() {
			driverVersionLabel.setPrefWidth(600);
			driverVersionLabel.setAlignment(Pos.CENTER);

			Tooltip aitessTooltip = new Tooltip();
			aitessTooltip.textProperty().bind(driverVersionLabel.textProperty());
			driverVersionLabel.setTooltip(aitessTooltip);

			HBox aitessTypeHBox = new HBox(10);
			aitessTypeHBox.setAlignment(Pos.CENTER_LEFT);
			aitessTypeHBox.getChildren().add(driverVersionLabel);
			driverVersionLabel.getStyleClass().add("label_field");

			return aitessTypeHBox;
		}
		
		private GridPane createCpciCardTable() {
			ColumnConstraints firstColumn = new ColumnConstraints();
			firstColumn.setPercentWidth(100);

			RowConstraints firstRow = new RowConstraints();
			firstRow.setPercentHeight(100);

			cpciCardTableGridPane.getColumnConstraints().addAll(firstColumn);
			cpciCardTableGridPane.getRowConstraints().addAll(firstRow);

			return cpciCardTableGridPane;
		}
		
		
		private void initializeUUTTypeComboBox() {
			uutDataList = FXCollections.observableArrayList(aitessConfigurationManagement.getAllUUT());
			for (UUTMasterDetailsDto uut : uutDataList) {
				uutTypeList.add(uut.getUutType());
			}
			uut_type_field.setItems(uutTypeList);
			uut_type_field.setOnAction((event) -> {
				if(driverDataList.size() >0) {
					driverDataList.clear();
					AITESS_ID = 0;
					driver_type_field.getItems().clear();
					addUserBtn.setDisable(true);
					initializeCpciCardTable();
				}
				this.UUT_ID = fetchUutId(uut_type_field.getValue());
				initializeDriverTypeComboBox();
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
		

		private void initializeDriverTypeComboBox() {
			driverDataList = FXCollections.observableArrayList(aitessConfigurationManagement.getAitessConfig(UUT_ID));
			
			for(AitessConfigurationDto driver : driverDataList) {
				driverNameList.add(driver.getDriverName());
			}
			driver_type_field.setItems(driverNameList);
			driver_type_field.setOnAction(e -> { 
				fetchAitessIdAndDriverVersion();
			});
		}

		private void fetchAitessIdAndDriverVersion() {
			for(AitessConfigurationDto driver : driverDataList) {
				if (driver.getDriverName().equals(driver_type_field.getValue())) {
					driverVersionLabel.setText(driver.getDriverVersion());
					this.AITESS_ID = driver.getAitessId();
					addUserBtn.setDisable(false);
					getCpciCardTableData();
				}
			}
		}
		
	    private void initializeCpciCardTable() {
	        ObservableList<CPCICard> tableData = FXCollections.observableArrayList();
	        customTableView_cpciCard = cpciCardFactory.createTableView(tableData, true, false);
	        cpciCardTableGridPane.add(customTableView_cpciCard, 0, 0);
	    }

		private void getCpciCardTableData() {
			CardDetailsResponseDTO cpciCardList = aitessConfigurationManagement.getCardDetailsByAitessId(AITESS_ID);
			ObservableList<CPCICard> tableData = FXCollections.observableArrayList();

			if(cpciCardList.getCardLst() != null) {
				for (CardDetailsDTO list : cpciCardList.getCardLst()) {
					CPCICard cardData = new CPCICard();
					cardData.setId(list.getCardDetailsId());
					cardData.setCardName(list.getCardName());
					cardData.setCardIdentificationText(list.getCardIdentificationText());
					cardData.setTotalCards(list.getTotalNumberOfCards());
					tableData.add(cardData);
				}
			}
			
			customTableView_cpciCard = cpciCardFactory.createTableView(tableData, true, false);
			
			customTableView_cpciCard.addEventHandler(CustomTableView.EDIT_BUTTON_CLICKED_EVENT, event -> {
				ObservableList<CPCICard> selectedItems = customTableView_cpciCard.getSelectedItems();
				for (CPCICard rowData : selectedItems) {
					handleAddEditButtonClicked(rowData);
				}
			});
			
			customTableView_cpciCard.addEventHandler(CustomTableView.DELETE_BUTTON_CLICKED_EVENT, event -> {
				ObservableList<CPCICard> selectedItems = customTableView_cpciCard.getSelectedItems();
				for (CPCICard rowData : selectedItems) {
					handleDeleteButtonClicked(rowData);
				}
			});
			
			cpciCardTableGridPane.add(customTableView_cpciCard, 0, 0);	
		}
		

		private void handleAddEditButtonClicked(CPCICard cardData) {
			try {
				FXMLLoader addUserPopup = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/AddCPCICard.fxml"));
				Parent root = addUserPopup.load();

				AddCPCICardController addCPCICardController = addUserPopup.getController();
				addCPCICardController.setCpciCardData(AITESS_ID, cardData);
				addCPCICardController.setMainPageController(this);

				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UNDECORATED);

				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 400) / 2;
			    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 350) / 2;
			    stage.setX(centerX);
			    stage.setY(centerY);
				
				stage.setScene(new Scene(root));
				stage.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void handleDeleteButtonClicked(CPCICard deteteCard) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete cPCI Card: " + deteteCard.getCardName() + "?");

			ButtonType buttonTypeYes = new ButtonType("Yes");
			ButtonType buttonTypeNo = new ButtonType("No");

			alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

			alert.showAndWait().ifPresent(buttonType -> {
				if (buttonType == buttonTypeYes) {
					deleteCpciCard(deteteCard.getId());
				}
			});
		}

		private void deleteCpciCard(int cardId) {
			Response response = aitessConfigurationManagement.deleteCardDetails(cardId);
			if (response.getResponseCode() != 0) {
				Notifications.showSuccessAlert("cPCI Card deleted");
				refreshCpciCardList();			
			} else {
				Notifications.showErrorAlert(response.getResponseMessage());
			}
		}

	
}
