package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.response.MacroButtonMapResponse;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.MacroConfigurationManagement;
import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class MacroButtonsController {
    private GridPane macroButtonsMainGridPane = new GridPane();
    private GridPane macroButtonsTitleGridPane = new GridPane();
    private GridPane macroButtonsListGridPane = new GridPane();
    private GridPane macroButtonsOptionGridPane = new GridPane();
    
    private ComboBox<String> uut_type_field = new ComboBox<>();
    private ObservableList<UUTMasterDetailsDto> uutDataList = FXCollections.observableArrayList();
    private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
    private String UUT_ID;
    
    AitessConfigurationManagement aitessConfigurationManagement = new AitessConfigurationManagement();
    MacroConfigurationManagement macroConfigurationManagement = new MacroConfigurationManagement();
   
    private List<TextField> buttonNameFields = new ArrayList<>();
    private List<TextField> buttonCommandFields = new ArrayList<>();
    private List<MacroButtonMapDto> macroButtonList;
    
    private VBox macroButtonListBox = new VBox(10);
    
    public MacroButtonsController() {
        initializeUUTTypeComboBox();
    }

    public GridPane createMacroButtonsMainGridPane() {
        macroButtonsMainGridPane.getStylesheets()
                .add(getClass().getResource("/com/teclever/dfcc/ui/css/MacroButtons.css").toExternalForm());
        macroButtonsMainGridPane.getStyleClass().add("macro-button-container");
        
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(100);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(7);
        RowConstraints secondRow = new RowConstraints();
        secondRow.setPercentHeight(7);
        RowConstraints thirdRow = new RowConstraints();
        thirdRow.setPercentHeight(86);
        
        macroButtonsMainGridPane.setPadding(new Insets(10));
        macroButtonsMainGridPane.setVgap(5);
        macroButtonsMainGridPane.getColumnConstraints().addAll(firstColumn);
        macroButtonsMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);

        macroButtonsMainGridPane.add(createMacroButtonsTitleGridPane(), 0, 0);
        macroButtonsMainGridPane.add(createMacroButtonsOptionPane(), 0, 1);
        macroButtonsMainGridPane.add(createMacroButtonsListBox(), 0, 2);
        return macroButtonsMainGridPane;
    }

    private GridPane createMacroButtonsTitleGridPane() {
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(50);
        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setPercentWidth(50);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(100);

        macroButtonsTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
        macroButtonsTitleGridPane.getRowConstraints().addAll(firstRow);

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("MACRO BUTTONS");
        title.getStyleClass().add("macro-button-title");
        titleBox.getChildren().add(title);

        HBox addUserBox = new HBox();
        addUserBox.setAlignment(Pos.CENTER_RIGHT);
        Button addUserBtn = new Button("SAVE");
        addUserBtn.getStyleClass().add("macro-button-save-btn");
        addUserBox.getChildren().add(addUserBtn);

        addUserBtn.setOnAction(e -> {
            saveMacroButtonData();
        });

        macroButtonsTitleGridPane.add(titleBox, 0, 0);
        macroButtonsTitleGridPane.add(addUserBox, 1, 0);

        return macroButtonsTitleGridPane;
    }

    private GridPane createMacroButtonsOptionPane() {
        macroButtonsOptionGridPane.getStyleClass().add("macro-button-option-container");
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(17);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setPercentHeight(100);
        
        macroButtonsOptionGridPane.getColumnConstraints().addAll(firstColumn);
        macroButtonsOptionGridPane.getRowConstraints().addAll(firstRow);
        
        macroButtonsOptionGridPane.add(createUutTypeField(), 0, 0);
        
        return macroButtonsOptionGridPane;
    }
    
    private HBox createUutTypeField() {
        uut_type_field.setPromptText("UUT TYPE");

        HBox uutTypeHBox = new HBox(10);
        uutTypeHBox.setAlignment(Pos.CENTER);
        uutTypeHBox.getChildren().add(uut_type_field);

        return uutTypeHBox;
    }
    
    private void initializeUUTTypeComboBox() {       
        uutDataList = FXCollections.observableArrayList(aitessConfigurationManagement.getAllUUT());
        for (UUTMasterDetailsDto uut : uutDataList) {
            uutTypeList.add(uut.getUutType());
        }
        uut_type_field.setItems(uutTypeList);
        uut_type_field.setOnAction((event) -> {
            this.UUT_ID = fetchUutId(uut_type_field.getValue());
            getMacroButtonList();
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

    private GridPane createMacroButtonsListBox() {
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100);
        macroButtonsListGridPane.getColumnConstraints().add(column);
        macroButtonsListGridPane.getStyleClass().add("macro-button-list-container");
        createMacroButtonListTitle();
        macroButtonsListGridPane.add(macroButtonListBox, 0, 0);
        return macroButtonsListGridPane;
    }
    
    private void getMacroButtonList() {
        macroButtonListBox.getChildren().clear();   
        createMacroButtonListTitle();
        buttonNameFields.clear();;
        buttonCommandFields.clear();;
        if (UUT_ID != null) {
            macroButtonList = macroConfigurationManagement.getAllMacroButtonsByUutId(UUT_ID);
            for(MacroButtonMapDto button : macroButtonList) {
                createMacroButtonBox(button);
            }
        }
        if (!macroButtonsListGridPane.getChildren().contains(macroButtonListBox)) {
            macroButtonsListGridPane.add(macroButtonListBox, 0, 0);
        }
    }

    private void createMacroButtonListTitle() {
        macroButtonListBox.setPadding(new Insets(20));
        HBox macroButtonListTitle = new HBox(200);  
        macroButtonListTitle.getStyleClass().add("macro-button-list-title-box");

        Label buttonNo = new Label("BUTTON NO");
        buttonNo.setPrefWidth(200);
        buttonNo.getStyleClass().add("macro-button-list-title");
        Label buttonName = new Label("BUTTON NAME");
        buttonName.setPrefWidth(300);
        buttonName.getStyleClass().add("macro-button-list-title");
        Label buttonCommand = new Label("BUTTON COMMAND");
        buttonCommand.setPrefWidth(300);
        buttonCommand.getStyleClass().add("macro-button-list-title");
      
        macroButtonListTitle.getChildren().addAll(buttonNo, buttonName, buttonCommand);

        macroButtonListBox.getChildren().add(macroButtonListTitle);
    }

    private void createMacroButtonBox(MacroButtonMapDto button) {
        HBox macroButtonBox = new HBox(200); 
        macroButtonBox.getStyleClass().add("macro-button-list-data-box");

        Label buttonNoLabel = new Label(String.valueOf(button.getButtonNumber()));
        buttonNoLabel.setPrefWidth(200);
        buttonNoLabel.getStyleClass().add("macro-button-list-data-label");
        TextField buttonNameField = new TextField(button.getButtonName());
        buttonNameField.setPrefWidth(300);
        buttonNameField.getStyleClass().add("macro-button-list-data-input");
        TextField buttonCommandField = new TextField(button.getCommand());
        buttonCommandField.setPrefWidth(300);
        buttonCommandField.getStyleClass().add("macro-button-list-data-input");
        
        buttonNameField.setId(button.getButtonId());
        buttonCommandField.setId(button.getButtonId());
        
        buttonNameFields.add(buttonNameField);
        buttonCommandFields.add(buttonCommandField);
        
        macroButtonBox.getChildren().addAll(buttonNoLabel, buttonNameField, buttonCommandField);

        macroButtonListBox.getChildren().add(macroButtonBox);
    }
    
    private void saveMacroButtonData() {
        List<MacroButtonMapDto> updatedMacroButtonList = new ArrayList<>();
        
        for (int i = 0; i < macroButtonList.size(); i++) {
            MacroButtonMapDto updatedButton = macroButtonList.get(i);
            updatedButton.setButtonId(buttonNameFields.get(i).getId());
            updatedButton.setButtonName(buttonNameFields.get(i).getText());
            updatedButton.setCommand(buttonCommandFields.get(i).getText());
            updatedButton.setUutId(UUT_ID); 
            
            updatedMacroButtonList.add(updatedButton);
        }
        for(MacroButtonMapDto button : updatedMacroButtonList) {
        	System.out.println("--------------");
        	System.out.println(button.getButtonId());
        	System.out.println(button.getButtonName());
        	System.out.println(button.getCommand());
        	System.out.println(UUT_ID);
        	System.out.println("--------------");
        }
        
        MacroButtonMapResponse response = macroConfigurationManagement.updateMacroButtonMap(updatedMacroButtonList);
        if(response.getResponseCode() == 1){
            Notifications.showSuccessAlert("Macro Buttons Added Successfully...");
            getMacroButtonList();
        }else if(response.getResponseCode() == 0) {
			Notifications.showErrorAlert(response.getResponseMessage());
		}
    }
}







//package com.teclever.dfcc.Controller.ui;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
//import com.teclever.dfcc.datastore.configurationmanagement.MacroConfigurationManagement;
//import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;
//import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//
//public class MacroButtonsController {
//    private GridPane macroButtonsMainGridPane = new GridPane();
//    private GridPane macroButtonsTitleGridPane = new GridPane();
//    private GridPane macroButtonsTableGridPane = new GridPane();
//    private GridPane macroButtonsOptionGridPane = new GridPane();
//    
//	private ComboBox<String> uut_type_field = new ComboBox<String>();
//	private ObservableList<UUTMasterDetailsDto> uutDataList = FXCollections.observableArrayList();
//	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
//	private String UUT_ID;
//    
//    AitessConfigurationManagement aitessConfigurationManagement = new AitessConfigurationManagement();
//    MacroConfigurationManagement macroConfigurationManagement = new MacroConfigurationManagement();
//    
//   
//    private List<TextField> buttonNameFields = new ArrayList<>();
//    private List<TextField> buttonCommandFields = new ArrayList<>();
//    private List<MacroButtonMapDto> macroButtonList;
//    
//    public MacroButtonsController() {
//    	initializeUUTTypeComboBox();
//    }
//
//    public GridPane createMacroButtonsMainGridPane() {
//        macroButtonsMainGridPane.getStylesheets()
//                .add(getClass().getResource("/com/teclever/dfcc/ui/css/MacroButtons.css").toExternalForm());
//        macroButtonsMainGridPane.getStyleClass().add("macro-button-container");
//        
//        ColumnConstraints firstColumn = new ColumnConstraints();
//        firstColumn.setPercentWidth(100);
//
//        RowConstraints firstRow = new RowConstraints();
//        firstRow.setPercentHeight(7);
//        RowConstraints secondRow = new RowConstraints();
//        secondRow.setPercentHeight(7);
//        RowConstraints thirdRow = new RowConstraints();
//        thirdRow.setPercentHeight(86);
//        
//        macroButtonsMainGridPane.setPadding(new Insets(10));
//        macroButtonsMainGridPane.setVgap(5);
//        macroButtonsMainGridPane.getColumnConstraints().addAll(firstColumn);
//        macroButtonsMainGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
//
//        macroButtonsMainGridPane.add(createMacroButtonsTitleGridPane(), 0, 0);
//        macroButtonsMainGridPane.add(createMacroButtonsOptionPane(), 0, 1);
//        macroButtonsMainGridPane.add(createMacroButtonsTable(), 0, 2);
//        return macroButtonsMainGridPane;
//    }
//
//    private GridPane createMacroButtonsTitleGridPane() {
//        ColumnConstraints firstColumn = new ColumnConstraints();
//        firstColumn.setPercentWidth(50);
//        ColumnConstraints secondColumn = new ColumnConstraints();
//        secondColumn.setPercentWidth(50);
//
//        RowConstraints firstRow = new RowConstraints();
//        firstRow.setPercentHeight(100);
//
//        macroButtonsTitleGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//        macroButtonsTitleGridPane.getRowConstraints().addAll(firstRow);
//
//        HBox titleBox = new HBox();
//        titleBox.setAlignment(Pos.CENTER_LEFT);
//        Label title = new Label("MACRO BUTTONS");
//        title.getStyleClass().add("macro-button-title");
//        titleBox.getChildren().add(title);
//
//        HBox addUserBox = new HBox();
//        addUserBox.setAlignment(Pos.CENTER_RIGHT);
//        Button addUserBtn = new Button("SAVE");
//        addUserBtn.getStyleClass().add("macro-button-save-btn");
//        addUserBox.getChildren().add(addUserBtn);
//
//        addUserBtn.setOnAction(e -> {
//            saveMacroButtonData();
//        });
//
//        macroButtonsTitleGridPane.add(titleBox, 0, 0);
//        macroButtonsTitleGridPane.add(addUserBox, 1, 0);
//
//        return macroButtonsTitleGridPane;
//    }
//
//    private GridPane createMacroButtonsOptionPane() {
//        macroButtonsOptionGridPane.getStyleClass().add("macro-button-option-container");
//        ColumnConstraints firstColumn = new ColumnConstraints();
//        firstColumn.setPercentWidth(17);
//
//        RowConstraints firstRow = new RowConstraints();
//        firstRow.setPercentHeight(100);
//        
//        macroButtonsOptionGridPane.getColumnConstraints().addAll(firstColumn);
//        macroButtonsOptionGridPane.getRowConstraints().addAll(firstRow);
//        
//        macroButtonsOptionGridPane.add(createUutTypeField(), 0, 0);
//        
//        return macroButtonsOptionGridPane;
//    }
//    
//    private HBox createUutTypeField() {
//		uut_type_field.setPromptText("UUT TYPE");
//
//		HBox uutTypeHBox = new HBox(10);
//		uutTypeHBox.setAlignment(Pos.CENTER);
//		uutTypeHBox.getChildren().add(uut_type_field);
//
//		return uutTypeHBox;
//	}
//    private void initializeUUTTypeComboBox() {   	
//    	uutDataList = FXCollections.observableArrayList(aitessConfigurationManagement.getAllUUT());
//		for (UUTMasterDetailsDto uut : uutDataList) {
//			uutTypeList.add(uut.getUutType());
//		}
//		uut_type_field.setItems(uutTypeList);
//		uut_type_field.setOnAction((event) -> {
//			this.UUT_ID = fetchUutId(uut_type_field.getValue());
//			getMacroButtonList();
//		});
//    }
//    
//    private String fetchUutId(String uutType) {
//		for (UUTMasterDetailsDto uut : uutDataList) {
//			if (uut.getUutType().equals(uutType)) {
//				return uut.getUutId();
//			}
//		}
//		return null;
//	}
//
//    private void getMacroButtonList() {
//        if (UUT_ID != null) {
//            macroButtonList = macroConfigurationManagement.getAllMacroButtonsByUutId(UUT_ID);
//            updateMacroButtonsTable(macroButtonList);
//        }
//    }
//
//    private void updateMacroButtonsTable(List<MacroButtonMapDto> macroButtonList) {
//        macroButtonsTableGridPane.getChildren().clear();
//        macroButtonsTableGridPane.getRowConstraints().clear();
//        buttonNameFields.clear();
//        buttonCommandFields.clear();
//        
//        
//        
//        int row = 0;
//        for (MacroButtonMapDto button : macroButtonList) {
//            HBox hbox = new HBox();
//            hbox.setAlignment(Pos.CENTER_LEFT);
//            hbox.setSpacing(10);  // Set spacing between elements in the HBox
//            hbox.setPadding(new Insets(5, 0, 5, 0));
//            
//            Label buttonNumberLabel = new Label(String.valueOf(button.getButtonNumber()));
//            TextField buttonNameField = new TextField(button.getButtonName());
//            TextField buttonCommandField = new TextField(button.getCommand());
//            
//            buttonNameField.setId(button.getButtonId());
//            buttonCommandField.setId(button.getButtonId());
//            
//            buttonNameFields.add(buttonNameField);
//            buttonCommandFields.add(buttonCommandField);
//
//            // Add margin to TextField to ensure space between columns
//            HBox.setMargin(buttonNumberLabel, new Insets(0, 10, 0, 0)); // Margin for button number
//            HBox.setMargin(buttonNameField, new Insets(0, 10, 0, 0));   // Margin for button name
//            HBox.setMargin(buttonCommandField, new Insets(0, 10, 0, 0)); // Margin for command field
//
//            hbox.getChildren().addAll(buttonNumberLabel, buttonNameField, buttonCommandField);
//            
//            RowConstraints rowConstraints = new RowConstraints();
//            macroButtonsTableGridPane.getRowConstraints().add(rowConstraints);
//            macroButtonsTableGridPane.add(hbox, 0, row);
//            row++;
//        }
//    }
//
//
//    private void saveMacroButtonData() {
//        List<MacroButtonMapDto> updatedMacroButtonList = new ArrayList<>();
//
//        for (int i = 0; i < macroButtonList.size(); i++) {
//            MacroButtonMapDto originalButton = macroButtonList.get(i);
//            TextField buttonNameField = buttonNameFields.get(i);
//            TextField buttonCommandField = buttonCommandFields.get(i);
//            
//            
//            MacroButtonMapDto updatedButton = new MacroButtonMapDto();
//            updatedButton.setButtonId(originalButton.getButtonId());
//            updatedButton.setButtonNumber(originalButton.getButtonNumber());
//            updatedButton.setButtonName(buttonNameField.getText());
//            updatedButton.setCommand(buttonCommandField.getText());
//            updatedButton.setUutId(UUT_ID);            
//
//            updatedMacroButtonList.add(updatedButton);
//        }
//        
//        for( MacroButtonMapDto button:updatedMacroButtonList) {
//    		System.out.println(button.getButtonId());
//    		System.out.println(button.getButtonNumber());
//    		System.out.println(button.getButtonName());
//    		System.out.println(button.getCommand());
//        }
//
//        List<MacroButtonMapDto> response = macroConfigurationManagement.updateMacroButtonMap(updatedMacroButtonList);
//
//        for( MacroButtonMapDto button:response) {
//    		System.out.println(button.getResponse().getResponseCode());
//        }
//    }
//
//    private GridPane createMacroButtonsTable() {
//        ColumnConstraints column = new ColumnConstraints();
//        column.setPercentWidth(100);
//        macroButtonsTableGridPane.getColumnConstraints().add(column);
//
//        return macroButtonsTableGridPane;
//    }
//}
