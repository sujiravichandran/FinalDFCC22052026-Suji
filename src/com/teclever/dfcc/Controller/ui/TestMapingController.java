package com.teclever.dfcc.Controller.ui;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.StagesFilesDTO;
import com.teclever.dfcc.datastore.dto.StagesFilesResponseDTO;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TestMapingController {
    private ObservableList<TestFileDto> testFiles = FXCollections.observableArrayList();
    private ObservableList<TestFileDto> filteredList = FXCollections.observableArrayList();
    
    private ObservableList<TestFileDto> selectedTestFileList = FXCollections.observableArrayList();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private ListView<CheckBox> testListView = new ListView<>();

    private TestPlanFileManagement testFileManagement = new TestPlanFileManagement();
    private StageConfiguration stageConfig = new StageConfiguration();

    private TextArea displayTextArea = new TextArea();;
    private VBox testMapVBox = new VBox(10);

    private Button saveTestFilesButton, selectAllButton;
    private String STAGE_ID;
    
    public VBox TestMappingView(String runConfigID, String stageId) {
        this.STAGE_ID = stageId;

        selectedTestFileList.clear();
        displayTextArea.clear();
        
        getTestFiles(runConfigID);
        testMapVBox.getChildren().clear();
        displayTextArea.setEditable(false);
        displayTextArea.getStyleClass().add("test-mapping-textArea");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("test-mapping-textField");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterList(newValue));

        saveTestFilesButton = new Button("Save");
        saveTestFilesButton.setOnAction(event -> onSaveSelectedFiles());

        selectAllButton = new Button("Select All");
        selectAllButton.setOnAction(event -> toggleSelectAll());

        HBox searcherHBox = new HBox(10);
        searcherHBox.setAlignment(Pos.CENTER_LEFT);
        searcherHBox.getChildren().addAll(searchField, saveTestFilesButton, selectAllButton);
  
        testMapVBox.getChildren().addAll(displayTextArea, searcherHBox, createTestListCheckbox());
        testMapVBox.getStyleClass().add("stageConfig-bottom-view-container-list");
        testMapVBox.setAlignment(Pos.CENTER);
        testMapVBox.setPadding(new Insets(10));
        return testMapVBox;
    }



	private void getTestFiles(String runConfigID) {
        if (runConfigID == null) {
            return;
        }

        StagesFilesResponseDTO getResponse = stageConfig.getTestFilesByStageLevel(STAGE_ID);
        List<StagesFilesDTO> stagesFileDTO = getResponse.getResponseList();
        List<String> idList = new ArrayList<>();

        
        if (stagesFileDTO != null) {
            testMapVBox.setDisable(false);
            for (StagesFilesDTO stagesFile : stagesFileDTO) {
                if (stagesFile.getLevelStage().equals(STAGE_ID)) {
                    idList.add(stagesFile.getTestFileId());
                }
            }
        }

        List<TestFileDto> testFileDtos = testFileManagement.getAllTestFiles(runConfigID);

        for (TestFileDto fileDto : testFileDtos) {
            String fullPath = fileDto.getTestFileName();
            String fileName = Paths.get(fullPath).getFileName().toString();
            fileDto.setTestFileName(fileName); 
        }
        
        
        List<TestFileDto> sortedTestFileDtos = testFileDtos.stream()
                .sorted(Comparator.comparing(fileDto -> Paths.get(fileDto.getTestFileName()).getFileName().toString()))
                .collect(Collectors.toList());
        
        if (idList != null) {
            for (String testId : idList) {
                for (TestFileDto dto : testFileDtos) {
                    if (dto.getTestFileId().equals(testId)) {
                        selectedTestFileList.add(dto);
                    }
                }
            }
        }
        
        testFiles.setAll(sortedTestFileDtos);
        updatedisplayTextArea();
    }
  

    private void filterList(String keyword) {
        filteredList.clear();
        for (TestFileDto file : testFiles) {
            if (file.getTestFileName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(file);
            }
        }
        setTestListViewData(filteredList);
    }

    private void toggleSelectAll() {
        if (selectAllButton.getText().equals("Select All")) {
            selectAll();
            selectAllButton.setText("Deselect All");
        } else {
            deselectAll();
            selectAllButton.setText("Select All");
        }
    }

    private void selectAll() {
    	for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(false);
        }
    	for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(true);
        }
        updatedisplayTextArea();
    }

    private void deselectAll() {
    	for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(false);
        }
        updatedisplayTextArea();
    }

   

    private void updatedisplayTextArea() {
    	displayTextArea.clear();
    	StringBuilder selectedFiles = new StringBuilder();
    	for(TestFileDto file : selectedTestFileList) {
    		selectedFiles.append(file.getTestFileName()).append("\n");
    	}
    	displayTextArea.setText(selectedFiles.toString());
    }

    private void onSaveSelectedFiles() {
	    List<String> selectedTestFileIds = new ArrayList<>();
	    if(selectedTestFileList.size() == 0) {
	    	Notifications.showWarningAlert("Please Select Test File...");
	    	return;
	    }
	    for (TestFileDto file : selectedTestFileList) {
	    	selectedTestFileIds.add(file.getTestFileId());
	    }
	    if (STAGE_ID != null) {
	        Response res = stageConfig.addTestFilesToStage(selectedTestFileIds, STAGE_ID);
	        if(res.getResponseCode() == 1) {
		         Notifications.showSuccessAlert("Test File Successfully Configured....");
	        }else {
		         Notifications.showErrorAlert(res.getResponseMessage());
	        }
	    } else {
	        Notifications.showErrorAlert("Not Saved");
	    }
    }

 
    
    
    private ListView<CheckBox> createTestListCheckbox() {
    	setTestListViewData(testFiles);
		return testListView;
	}
    private void setTestListViewData(ObservableList<TestFileDto> fileList) {
		testListView.getItems().clear();
		checkBoxes.clear();
		for(TestFileDto file : fileList) {
			String test = file.getTestFileName().replace("_", "__");	        
		    CheckBox newCheckBox = new CheckBox(test);

		    newCheckBox.setId(file.getTestFileId());
	        newCheckBox.setUserData(file);
	        newCheckBox.setWrapText(true);
	        if(selectedTestFileList.contains(file)) {
	        	newCheckBox.setSelected(true);
	        }
	        checkBoxes.add(newCheckBox);
	        testListView.getItems().add(newCheckBox);
	        
	        newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
	        	if(newValue) {
		        	selectedTestFileList.add(file);
	        	}else {
		        	selectedTestFileList.remove(file);
	        	}
	            updatedisplayTextArea();
	        });
		}
	}

}





//package com.teclever.dfcc.Controller.ui;
//
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import com.teclever.datastore.dto.Response;
//import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
//import com.teclever.dfcc.datastore.dto.StagesFilesDTO;
//import com.teclever.dfcc.datastore.dto.StagesFilesResponseDTO;
//import com.teclever.dfcc.datastore.dto.TestFileDto;
//import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
//import com.teclever.dfcc.utils.Notifications;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.ListCell;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//
//public class TestMapingController {
//    private ObservableList<TestFileDto> testFiles = FXCollections.observableArrayList();
//    private ListView<TestFileDto> listView;
//    private ObservableList<TestFileDto> filteredList = FXCollections.observableArrayList();
//    
//    private ObservableList<TestFileDto> selectedTestFileList = FXCollections.observableArrayList();
//
//    private TestPlanFileManagement testFileManagement = new TestPlanFileManagement();
//    private StageConfiguration stageConfig = new StageConfiguration();
//
//    private TextArea displayTextArea;
//    private VBox testMapVBox = new VBox(10);
//
//    private Button saveTestFilesButton, selectAllButton;
//    private String STAGE_ID;
//    private String RUN_CONFIG_ID;
//
//    private void getTestFiles(String runConfigID) {
//        if (runConfigID == null) {
//            return;
//        }
//
//        StagesFilesResponseDTO getResponse = stageConfig.getTestFilesByStageLevel(STAGE_ID);
//        List<StagesFilesDTO> stagesFileDTO = getResponse.getResponseList();
//        List<String> idList = new ArrayList<>();
//
//        if (stagesFileDTO != null) {
//            testMapVBox.setDisable(false);
//            for (StagesFilesDTO stagesFile : stagesFileDTO) {
//                if (stagesFile.getLevelStage().equals(STAGE_ID)) {
//                    idList.add(stagesFile.getTestFileId());
//                }
//            }
//        }
//
//        List<TestFileDto> testFileDtos = testFileManagement.getAllTestFiles(runConfigID);
// 
//        
//        
//        for (TestFileDto fileDto : testFileDtos) {
////            if (idList.contains(fileDto.getTestFileId())) {
////                fileDto.setSelected(true);
////            }
//            String fullPath = fileDto.getTestFileName(); // Assuming getTestFilePath() gives the full path
//            String fileName = Paths.get(fullPath).getFileName().toString();
//            fileDto.setTestFileName(fileName); // set the display name
//        }
//        
//        
//        List<TestFileDto> sortedTestFileDtos = testFileDtos.stream()
//                .sorted(Comparator.comparing(fileDto -> Paths.get(fileDto.getTestFileName()).getFileName().toString()))
//                .collect(Collectors.toList());
//        
//        if (idList != null) {
//            for (String testId : idList) {
//                for (TestFileDto dto : testFileDtos) {
//                    if (dto.getTestFileId().equals(testId)) {
//                        System.out.println("Selected File: " + dto.getTestFileName());
//                        selectedTestFileList.add(dto);
//                        dto.setSelected(true);
//                    }
//                }
//            }
//        }
//
//        testFiles.setAll(sortedTestFileDtos);
//    }
//
//    public void refresh() {
//        if (displayTextArea != null) {
//            testFiles.clear();
//            displayTextArea.clear();
//        }
//    }
//
//    public VBox TestMappingView(String runConfigID, String stageId) {
//        this.RUN_CONFIG_ID = runConfigID;
//        this.STAGE_ID = stageId;
//
//        System.out.println("1111");
//        selectedTestFileList.clear();
//        
//        getTestFiles(runConfigID);
//        testMapVBox.getChildren().clear();
//        displayTextArea = new TextArea();
//        displayTextArea.setEditable(false);
//        displayTextArea.getStyleClass().add("test-mapping-textArea");
//
//        TextField searchField = new TextField();
//        searchField.setPromptText("Search...");
//        searchField.getStyleClass().add("test-mapping-textField");
//        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterList(newValue));
//
//        saveTestFilesButton = new Button("Save Selected Files");
//        saveTestFilesButton.setOnAction(event -> onSaveSelectedFiles());
//
//        selectAllButton = new Button("Select All");
//        selectAllButton.setOnAction(event -> toggleSelectAll());
//
//        HBox searcherHBox = new HBox(10);
//        searcherHBox.setAlignment(Pos.CENTER_LEFT);
//        searcherHBox.getChildren().addAll(searchField, saveTestFilesButton, selectAllButton);
//
//        listView = new ListView<>();
//        listView.setCellFactory(param -> new CheckBoxListCell());
//        listView.setItems(testFiles);
//
//        testMapVBox.getChildren().addAll(displayTextArea, searcherHBox, listView);
//        testMapVBox.getStyleClass().add("stageConfig-bottom-view-container-list");
//        testMapVBox.setAlignment(Pos.CENTER);
//        testMapVBox.setPadding(new Insets(10));
//        return testMapVBox;
//    }
//
//    private void filterList(String keyword) {
//        filteredList.clear();
//        for (TestFileDto file : testFiles) {
//            if (file.getTestFileName().toLowerCase().contains(keyword.toLowerCase())) {
//                filteredList.add(file);
//            }
//        }
//        listView.setItems(filteredList);
//    }
//
//    private void toggleSelectAll() {
//        if (selectAllButton.getText().equals("Select All")) {
//            selectedTestFileList.clear();
//            selectAll();
//            selectAllButton.setText("Deselect All");
//        } else {
//            selectedTestFileList.clear();
//            deselectAll();
//            selectAllButton.setText("Select All");
//        }
//    }
//
//    private void selectAll() {
//        ObservableList<TestFileDto> items = listView.getItems();
//        for (TestFileDto item : items) {
//            item.setSelected(true);
//            selectedTestFileList.add(item);
//        }
//        listView.refresh();
//        updatedisplayTextArea();
//    }
//
//    private void deselectAll() {
//        ObservableList<TestFileDto> items = listView.getItems();
//        for (TestFileDto item : items) {
//            item.setSelected(false);
//        }
//        listView.refresh();
//        updatedisplayTextArea();
//    }
//
//    class CheckBoxListCell extends ListCell<TestFileDto> {
//        protected void updateItem(TestFileDto item, boolean empty) {
//            super.updateItem(item, empty);
//            if (empty || item == null) {
//                setText(null);
//                setGraphic(null);
//            } else {
//                CheckBox checkBox = new CheckBox(item.getTestFileName());
//                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//                    item.setSelected(newValue);
//                    checkBox.setSelected(item.isSelected());
//                    if(newValue) {
//                    	selectedTestFileList.add(item);
//                    }else {
//						selectedTestFileList.remove(item);
//					}
//                    updatedisplayTextArea();
//                });
//                setGraphic(checkBox);
//            }
//        }
//    }
//
//    private void updatedisplayTextArea() {
//    	displayTextArea.clear();
//    	StringBuilder selectedFiles = new StringBuilder();
//    	for(TestFileDto file : selectedTestFileList) {
//    		System.out.println(file.getTestFileName());
//    		selectedFiles.append(file.getTestFileName()).append("\n");
//    	}
//    	System.out.println();
//    	displayTextArea.setText(selectedFiles.toString());
//
////        StringBuilder selectedFiles = new StringBuilder();
////        for (TestFileDto file : testFiles) {
////            if (file.isSelected()) {
////                selectedFiles.append(file.getTestFileName()).append("\n");
////            }
////        }
////        displayTextArea.setText(selectedFiles.toString());
//    }
//
//    private void onSaveSelectedFiles() {
////        List<String> selectedTestFileIds = new ArrayList<>();
////        for (TestFileDto file : testFiles) {
////            if (file.isSelected()) {
////                selectedTestFileIds.add(file.getTestFileId());
////            }
////        }
////        if (STAGE_ID != null) {
////            Response res = stageConfig.addTestFilesToStage(selectedTestFileIds, STAGE_ID);
////             Notifications.showSuccessAlert("Saved successfully");
////        } else {
////            Notifications.showErrorAlert("Not saved-missing stageID");
////        }
//    }
//
//    public boolean isInitialized() {
//        return displayTextArea != null;
//    }
//}







//public class TestMapingController {
//	private ObservableList<TestFileDto> testFiles = FXCollections.observableArrayList();
//	private ListView<TestFileDto> listView;
//	private ObservableList<TestFileDto> filteredList = FXCollections.observableArrayList();
//
//	private TestPlanFileManagement testFileManagement = new TestPlanFileManagement();
//	private StageConfiguration stageConfig = new StageConfiguration();
//
//	private TextArea displayTextArea;
//	private VBox testMapVBox = new VBox(10);
//
//	private Button saveTestFilesButton, selectAllButton;
//	private String STAGE_ID;
//	private String RUN_CONFIG_ID;
//
//	private void getTestFiles(String runConfigID) {
//		if (runConfigID == null) {
//			return;
//		}
//
//		StagesFilesResponseDTO getResponse = stageConfig.getTestFilesByStageLevel(STAGE_ID);
//		List<StagesFilesDTO> stagesFileDTO = getResponse.getResponseList();
//		List<String> idList = new ArrayList<>();
//
//		if (stagesFileDTO != null) {
//			testMapVBox.setDisable(false);
//			for (StagesFilesDTO stagesFile : stagesFileDTO) {
//				if (stagesFile.getLevelStage().equals(STAGE_ID)) {
//					idList.add(stagesFile.getTestFileId());
//				}
//			}
//		}
//
//		List<TestFileDto> testFileDtos = testFileManagement.getAllTestFiles(runConfigID);
//		for (TestFileDto fileDto : testFileDtos) {
//			if (idList.contains(fileDto.getTestFileId())) {
//				fileDto.setSelected(true);
//			}
//			String fullPath = fileDto.getTestFileName(); // Assuming getFilePath() gives the full path
//	        String fileName = Paths.get(fullPath).getFileName().toString();
//	        fileDto.setTestFileName(fileName); 
//		}
//
//		testFiles.setAll(testFileDtos);
//	}
//
//	public void refresh() {
//		if (displayTextArea != null) {
//			testFiles.clear();
//			displayTextArea.clear();
//		}
//	}
//
//	public VBox TestMappingView( String runConfigID, String stageId) {
//		this.RUN_CONFIG_ID = runConfigID;
//		this.STAGE_ID = stageId;
//		
//		getTestFiles(runConfigID);
//		testMapVBox.getChildren().clear();
//		displayTextArea = new TextArea();
//		displayTextArea.setEditable(false);
//		displayTextArea.getStyleClass().add("test-mapping-textArea");
//
//		TextField searchField = new TextField();
//		searchField.setPromptText("Search...");
//		searchField.getStyleClass().add("test-mapping-textField");
//		searchField.textProperty().addListener((observable, oldValue, newValue) -> filterList(newValue));
//
//		saveTestFilesButton = new Button("Save Selected Files");
//		saveTestFilesButton.setOnAction(event -> onSaveSelectedFiles());
//
//		selectAllButton = new Button("Select All");
//		selectAllButton.setOnAction(event -> toggleSelectAll());
//
//		HBox searcherHBox = new HBox(10);
//		searcherHBox.setAlignment(Pos.CENTER_LEFT);
//		searcherHBox.getChildren().addAll(searchField, saveTestFilesButton, selectAllButton);
//
//		listView = new ListView<>();
//		listView.setCellFactory(param -> new CheckBoxListCell());
//		listView.setItems(testFiles);
//
//		testMapVBox.getChildren().addAll(displayTextArea, searcherHBox, listView);
//		testMapVBox.getStyleClass().add("stageConfig-bottom-view-container-list");
//		testMapVBox.setAlignment(Pos.CENTER);
//		testMapVBox.setPadding(new Insets(10));
//		return testMapVBox;
//	}
//
//	private void filterList(String keyword) {
//		filteredList.clear();
//		for (TestFileDto file : testFiles) {
//			if (file.getTestFileName().toLowerCase().contains(keyword.toLowerCase())) {
//				filteredList.add(file);
//			}
//		}
//		listView.setItems(filteredList);
//	}
//
//	private void toggleSelectAll() {
//		if (selectAllButton.getText().equals("Select All")) {
//			selectAll();
//			selectAllButton.setText("Deselect All");
//		} else {
//			deselectAll();
//			selectAllButton.setText("Select All");
//		}
//	}
//
//	private void selectAll() {
//		ObservableList<TestFileDto> items = listView.getItems();
//		for (TestFileDto item : items) {
//			item.setSelected(true);
//		}
//		listView.refresh();
//		updatedisplayTextArea();
//	}
//
//	private void deselectAll() {
//		ObservableList<TestFileDto> items = listView.getItems();
//		for (TestFileDto item : items) {
//			item.setSelected(false);
//		}
//		listView.refresh();
//		updatedisplayTextArea();
//	}
//
//	class CheckBoxListCell extends ListCell<TestFileDto> {
//		protected void updateItem(TestFileDto item, boolean empty) {
//			super.updateItem(item, empty);
//			if (empty || item == null) {
//				setText(null);
//				setGraphic(null);
//			} else {
//				CheckBox checkBox = new CheckBox(item.getTestFileName());
//				checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//					item.setSelected(newValue);
//					updatedisplayTextArea();
//				});
//				checkBox.setSelected(item.isSelected());
//				setGraphic(checkBox);
//			}
//		}
//	}
//
//	private void updatedisplayTextArea() {
//		StringBuilder selectedFiles = new StringBuilder();
//		for (TestFileDto file : testFiles) {
//			if (file.isSelected()) {
//				selectedFiles.append(file.getTestFileName()).append("\n");
//			}
//		}
//		displayTextArea.setText(selectedFiles.toString());
//	}
//
//	private void onSaveSelectedFiles() {
//		List<String> selectedTestFileIds = new ArrayList<>();
//		for (TestFileDto file : testFiles) {
//			if (file.isSelected()) {
//				selectedTestFileIds.add(file.getTestFileId());
//			}
//		}
//		if(STAGE_ID!=null) {
//			Response res = stageConfig.addTestFilesToStage(selectedTestFileIds, STAGE_ID);
////			Notifications.showSuccessAlert("Saved successfully");
//		}else {
//			Notifications.showErrorAlert("Not saved-missing stageID");
//		}
//
//	}
//
//	public boolean isInitialized() {
//		return displayTextArea != null;
//	}
//}
