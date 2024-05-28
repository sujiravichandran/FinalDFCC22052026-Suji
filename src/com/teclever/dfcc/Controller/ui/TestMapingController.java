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
	private ListView<TestFileDto> listView;
	private ObservableList<TestFileDto> filteredList = FXCollections.observableArrayList();
	private ObservableList<TestFileDto> newList = FXCollections.observableArrayList();

	private TestPlanFileManagement testFileManagement = new TestPlanFileManagement();
	private StageConfiguration stageConfig = new StageConfiguration();

	private Notifications notify;
	private TextArea displayTextArea;
	private VBox testMapVBox = new VBox(10);

	private Button saveTestFilesButton, selectAllButton;
	private String STAGE_ID;
	private String RUN_CONFIG_ID;

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
			if (idList.contains(fileDto.getTestFileId())) {
				fileDto.setSelected(true);
			}
		}

		testFiles.setAll(testFileDtos);
	}

	public void refresh() {
		if (displayTextArea != null) {
			testFiles.clear();
			displayTextArea.clear();
		}
	}

	public VBox TestMappingView( String runConfigID, String stageId) {
		System.out.println("RECEIVER: "+ runConfigID + ":" + stageId);
		this.RUN_CONFIG_ID = runConfigID;
		this.STAGE_ID = stageId;
		
		getTestFiles(runConfigID);
		testMapVBox.getChildren().clear();
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
		List<String> selectedTestFileIds = new ArrayList<>();
		for (TestFileDto file : testFiles) {
			if (file.isSelected()) {
				selectedTestFileIds.add(file.getTestFileId());
			}
		}
		if(STAGE_ID!=null) {
			Response res = stageConfig.addTestFilesToStage(selectedTestFileIds, STAGE_ID);
			Notifications.showSuccessAlert("Saved successfully");
		}else {
			Notifications.showErrorAlert("Not saved-missing stageID");
		}

	}

	public boolean isInitialized() {
		return displayTextArea != null;
	}
}
