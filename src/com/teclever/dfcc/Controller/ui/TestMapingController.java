package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TestMapingController {
	private ObservableList<TestFileDto> testFiles = FXCollections.observableArrayList();
//			new TestFileDto("1", "TestFilename", "RunPath1"), new TestFileDto("2", "TestFile2", "RunPath2"),
//			new TestFileDto("3", "TestFile3", "RunPath3"), new TestFileDto("4", "file", "RunPath3"),
//			new TestFileDto("5", "abcd", "RunPath3"), new TestFileDto("6", "abcde", "RunPath1"),
//			new TestFileDto("7", "xxx1234", "RunPath2"), new TestFileDto("8", "xxABC", "RunPath3"),
//			new TestFileDto("9", "file789", "RunPath3"), new TestFileDto("10", "Test 009", "RunPath3"));
	private ListView<TestFileDto> listView;
	private ObservableList<TestFileDto> filteredList = FXCollections.observableArrayList();
	private ObservableList<TestFileDto> newList = FXCollections.observableArrayList();

	private TestPlanFileManagement testFileManagement = new TestPlanFileManagement();
	private StageConfiguration stageConfig = new StageConfiguration();

	private Notifications notify;
	private TextArea displayTextArea;
	private Button saveTestFilesButton, selectAllButton;
	private String TEST_TYPE_ID;

	private void getTestFiles(String runConfigID) {
        StagesFilesResponseDTO getResponse = stageConfig.getTestFilesByStageLevel("L2_002");
        List<StagesFilesDTO> stagesFileDTO = getResponse.getResponseList();
        List<String> idList = new ArrayList<>();

        for (StagesFilesDTO stagesFile : stagesFileDTO) {
        	System.err.println("dto: "+stagesFile.getTestFileId());
            if (stagesFile.getLevelStage().equals("L2_002")) {
            	idList.add(stagesFile.getTestFileId());
            }
        }
        List<TestFileDto> testFileDtos = testFileManagement.getAllTestFiles(runConfigID);
        for (TestFileDto fileDto : testFileDtos) {
            if (idList.contains(fileDto.getTestFileId())) {
                fileDto.setSelected(true);
            }
        }

        // Finally, set the testFiles observable list
        testFiles.setAll(testFileDtos);
	}

  
	
	
//	private void getTestFiles() {
//		StagesFilesResponseDTO getResponse = stageConfig.getTestFilesByStageLevel("L2_002");
//		System.out.println("getRESPONSEListSize: " + getResponse.getResponseList().size());
//		System.out.println("getRESPONSE: " + getResponse.getCode());
//
//		List<StagesFilesDTO> stagesFileDTO = getResponse.getResponseList();
//
//	}

	public VBox TestMappingView(String uutID, String runConfigID, String testTypeID, String stageId) {
		System.out.println("RECEIVER: " + uutID + " : " + runConfigID + ":" + testTypeID + ":" + stageId);
		this.TEST_TYPE_ID = testTypeID;
		getTestFiles(runConfigID);

//		List<TestFileDto> testFileDtos = testFileManagement.getAllTestFiles(runConfigID);
//		testFiles = FXCollections.observableArrayList(testFileManagement.getAllTestFiles(runConfigID));

		VBox testMapVBox = new VBox(10);
		displayTextArea = new TextArea();
		displayTextArea.setEditable(false);
		displayTextArea.getStyleClass().add("test-mapping-textArea");

		TextField searchField = new TextField();
		searchField.setPromptText("Search...");
		searchField.getStyleClass().add("test-mapping-textField");
		searchField.textProperty().addListener((observable, oldValue, newValue) -> filterList(newValue));

		saveTestFilesButton = new Button("Save Selected Files");
		saveTestFilesButton.setOnAction(event -> onSaveSelectedFiles());

		selectAllButton = new Button("Select All");
		selectAllButton.setOnAction(event -> toggleSelectAll());

		HBox searcherHBox = new HBox(10);
		searcherHBox.setAlignment(Pos.CENTER_LEFT);
		searcherHBox.getChildren().addAll(searchField, saveTestFilesButton, selectAllButton);

		listView = new ListView<>();
		listView.setCellFactory(param -> new CheckBoxListCell());
		listView.setItems(testFiles);

		testMapVBox.getChildren().addAll(displayTextArea, searcherHBox, listView);
		testMapVBox.getStyleClass().add("stageConfig-bottom-view-container-list");
		testMapVBox.setAlignment(Pos.CENTER);
		testMapVBox.setPadding(new Insets(10));
//		updatedisplayTextArea();
		return testMapVBox;
	}

	private void filterList(String keyword) {
		filteredList.clear();
		for (TestFileDto file : testFiles) {
			if (file.getTestFileName().toLowerCase().contains(keyword.toLowerCase())) {
				filteredList.add(file);
			}
		}
		listView.setItems(filteredList);
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
		ObservableList<TestFileDto> items = listView.getItems();
		for (TestFileDto item : items) {
			item.setSelected(true);
		}
		listView.refresh();
		updatedisplayTextArea();
	}

	private void deselectAll() {
		ObservableList<TestFileDto> items = listView.getItems();
		for (TestFileDto item : items) {
			item.setSelected(false);
		}
		listView.refresh();
		updatedisplayTextArea();
	}

	class CheckBoxListCell extends ListCell<TestFileDto> {
		protected void updateItem(TestFileDto item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				CheckBox checkBox = new CheckBox(item.getTestFileName());
				checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
					item.setSelected(newValue);
					updatedisplayTextArea();
				});
				checkBox.setSelected(item.isSelected());
				setGraphic(checkBox);
			}
		}
	}

	private void updatedisplayTextArea() {
		StringBuilder selectedFiles = new StringBuilder();
		for (TestFileDto file : testFiles) {
			if (file.isSelected()) {
				selectedFiles.append(file.getTestFileName()).append("\n");
			}
		}
		displayTextArea.setText(selectedFiles.toString());
	}

	private void onSaveSelectedFiles() {
		System.out.println("Selected TestFileIds:");
		List<String> selectedTestFileIds = new ArrayList<>();
		for (TestFileDto file : testFiles) {
			if (file.isSelected()) {
				selectedTestFileIds.add(file.getTestFileId());
			}
		}

		Response res = stageConfig.addTestFilesToStage(selectedTestFileIds, "L2_002");
		System.out.println(res.getResponseCode());
		System.out.println(selectedTestFileIds);
		notify.showSuccessAlert("Saved successfully");

//		System.out.println("Selected Files:");
//		for (TestFileDto file : testFiles) {
//			if (file.isSelected()) {
////				stageId, listof fileId
//				System.out.println("TestFileDto format: " + file);
//			}
//		}
//		notify.showSuccessAlert("Saved successfully");
	}
}