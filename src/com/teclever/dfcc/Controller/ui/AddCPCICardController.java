package com.teclever.dfcc.Controller.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.CardDetailsDTO;
import com.teclever.dfcc.model.CPCICard;
import com.teclever.dfcc.utils.Notifications;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddCPCICardController implements Initializable {

	@FXML
	private AnchorPane addCpciCardContainer;
	
	private VBox addCpciCardFormBox = new VBox(5);
	private HBox headingBox = new HBox();
	private Label titleLabel = new Label();
	private Label cardNameLabel = new Label();
	private TextField cardNameTextField = new TextField(); 
	private Label cardIdentificationLabel = new Label();
	private TextField cardIdentificationTextField = new TextField();
	private Label totalCardLabel = new Label();
	private TextField totalCardTextField = new TextField();
	private HBox buttonBox = new HBox(50);
	private Button closeButton = new Button("Close");
	private Button saveButton = new Button("Save");	
	private int AITESS_ID;
	private CPCICard cardData;


	
	AdminCenterContentController adminCenterContentController = new AdminCenterContentController();
	AitessConfigurationManagement aitessConfigurationManagement = new AitessConfigurationManagement();
	
	private CPCICardController mainPageController;
	
	public void setMainPageController(CPCICardController mainPageController) {
		this.mainPageController = mainPageController;
	}

	public void setCpciCardData(int aitessId, CPCICard data) {
		cardData = data;
		AITESS_ID =aitessId ;
		createAddCpciCardPopup(aitessId);
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		addCpciCardContainer.getStylesheets().add(getClass().getResource("/com/teclever/dfcc/ui/css/AddCPCICard.css").toExternalForm());
		setNumericInputOnly(totalCardTextField);
	}

	 private void setNumericInputOnly(TextField textField) {
	        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
	            if (!event.getCharacter().matches("\\d")) {
	                event.consume();
	            }
	        });
	    }

	private void createAddCpciCardPopup(int aitessId) {
		addCpciCardFormBox.setPrefWidth(400);
		addCpciCardFormBox.setPadding(new Insets(20)); 
		headingBox.setAlignment(Pos.CENTER);

		
		titleLabel.setText("ADD cPCI CARD");
		titleLabel.getStyleClass().add("cpci-card-title");
		headingBox.getChildren().add(titleLabel);
		
		cardNameLabel.setText("Card Name");
		cardNameLabel.getStyleClass().add("cpci-card-label");
		cardNameTextField.getStyleClass().add("cpci-card-input");
		
		cardIdentificationLabel.setText("Card Identification Text");
		cardIdentificationLabel.getStyleClass().add("cpci-card-label");
		cardIdentificationTextField.getStyleClass().add("cpci-card-input");
		
		totalCardLabel.setText("Total Card No");
		totalCardLabel.getStyleClass().add("cpci-card-label");
		totalCardTextField.getStyleClass().add("cpci-card-input");
		
		closeButton.getStyleClass().add("cpci-card-btn");
		saveButton.getStyleClass().add("cpci-card-btn");
		
		saveButton.setOnAction(e ->{
			saveCPCICardData();
		});
		
		closeButton.setOnAction(e ->{
			Stage stage = (Stage) addCpciCardContainer.getScene().getWindow();
			stage.close();
		});
		
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getStyleClass().add("cpci-card-btn-box");
		buttonBox.getChildren().addAll(closeButton,saveButton);
		
		if(cardData != null) {
			setEditCardData();
		}
		
		addCpciCardFormBox.getChildren().addAll(headingBox,cardNameLabel,cardNameTextField,
				cardIdentificationLabel,cardIdentificationTextField,totalCardLabel,totalCardTextField,buttonBox);
		
		addCpciCardContainer.getChildren().add(addCpciCardFormBox);
	}


	private void saveCPCICardData() {
		String cardName = cardNameTextField.getText();
		String cardIdentification = cardIdentificationTextField.getText();
		String totalCardText = totalCardTextField.getText();
		int totalCard = Integer.parseInt(totalCardText);
		
		CardDetailsDTO cardData = new CardDetailsDTO();
		cardData.setAitessId(AITESS_ID);
		cardData.setCardName(cardName);
		cardData.setCardIdentificationText(cardIdentification);
		cardData.setTotalNumberOfCards(totalCard);
		
		System.out.println(AITESS_ID+"---"+cardName+"---"+cardIdentification+"---"+totalCard);
		
		Response response = aitessConfigurationManagement.saveCardConfigDetail(cardData);
		if(response.getResponseCode() == 1) {
			mainPageController.refreshCpciCardList();
			Stage stage = (Stage) addCpciCardContainer.getScene().getWindow();
			stage.close();
		}else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
	}
	
	private void setEditCardData() {
		
		if(cardData != null) {
			titleLabel.setText("UPDATE cPCI CARD");
			cardNameTextField.setText(cardData.getCardName());	
			cardIdentificationTextField.setText(cardData.getCardIdentificationText());
			totalCardTextField.setText(String.valueOf(cardData.getTotalCards()));	
			
			saveButton.setText("Update");
			saveButton.setOnAction(e ->{
				System.out.println("in");
				updateCPCICardData();
			});
		}
	}

	private void updateCPCICardData() {
		System.err.println("update");
		String cardName = cardNameTextField.getText();
		String cardIdentification = cardIdentificationTextField.getText();
		String totalCardText = totalCardTextField.getText();
		int totalCard = Integer.parseInt(totalCardText);
		
		CardDetailsDTO editedCardData = new CardDetailsDTO();
		editedCardData.setCardDetailsId(cardData.getId());
		editedCardData.setAitessId(AITESS_ID);
		editedCardData.setCardName(cardName);
		editedCardData.setCardIdentificationText(cardIdentification);
		editedCardData.setTotalNumberOfCards(totalCard);

		
		Response response = aitessConfigurationManagement.updateCardConfigDetail(editedCardData);
		if(response.getResponseCode() == 1) {
			mainPageController.refreshCpciCardList();
			Stage stage = (Stage) addCpciCardContainer.getScene().getWindow();
			stage.close();
		}else {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
		
	}

}


