package com.teclever.dfcc.Controller.ui;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.LevelOneDto;
import com.teclever.dfcc.datastore.dto.SessionMasterDTO;
import com.teclever.dfcc.datastore.dto.StageMasterLevelOneResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.StageOne;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StageReorderPopupController {

	@FXML
	private AnchorPane stageReorderContainer;
	@FXML
	private Label headingLabel;
	@FXML
	private HBox comboBoxContainer;
	@FXML
	private ComboBox<String> uutTypeComboBox;
	@FXML
	private ComboBox<String> sessionTypeComboBox;
	@FXML
	private ListView<StageOne> l1StageListView;
	@FXML
	private HBox buttonBox;
	@FXML
	private Button saveButton;
	@FXML
	private Button closeButton;

	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;

	private ObservableList<SessionMasterDTO> sessionDataList;
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private String SESSION_TYPE_ID;

	private AitessConfigurationManagement aitessConfig = new AitessConfigurationManagement();
	private StageConfiguration stageConfig = new StageConfiguration();
	private SessionManagement sessionManagement = new SessionManagement();

	@FXML
	private void initialize() {	
		initializeUUTTypeComboBox();
		initializeSessionTypeComboBox();
		sessionTypeComboBox.setDisable(true);
		initializeButtonBox();
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(aitessConfig.getAllUUT());
		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}
		uutTypeComboBox.setItems(uutTypeList);
		uutTypeComboBox.setOnAction((event) -> {
			UUT_ID = fetchUutId(uutTypeComboBox.getValue());
			refreshSessionTypeComboBox();
			sessionTypeComboBox.setDisable(false);
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

	private void initializeSessionTypeComboBox() {
		sessionDataList = FXCollections.observableArrayList(stageConfig.getSessionMasterList());
		for (SessionMasterDTO sessionType : sessionDataList) {
			sessionTypeList.add(sessionType.getSessionTypeName());
		}
		sessionTypeComboBox.setItems(sessionTypeList);
		sessionTypeComboBox.setOnAction((event) -> {
			SESSION_TYPE_ID = fetchSessionTypeId(sessionTypeComboBox.getValue());
			initializeListView();
		});
	}

	private String fetchSessionTypeId(String sessionType) {
		for (SessionMasterDTO type : sessionDataList) {
			if (type.getSessionTypeName().equals(sessionType)) {
				return type.getSessionMasterId();
			}
		}
		return null;
	}

	private void refreshSessionTypeComboBox() {
		sessionTypeList.clear();
		sessionTypeComboBox.setPromptText("Session Type");
		sessionDataList = FXCollections.observableArrayList(stageConfig.getSessionMasterList());
		for (SessionMasterDTO sessionType : sessionDataList) {
			sessionTypeList.add(sessionType.getSessionTypeName());
		}
		sessionTypeComboBox.setItems(sessionTypeList);
	}

	private void initializeListView() {
		l1StageListView.getStyleClass().add("stage-reorder-listview");
	    l1StageListView.setCellFactory(param -> {
	        ListCell<StageOne> cell = new ListCell<StageOne>() {
	            @Override
	            protected void updateItem(StageOne item, boolean empty) {
	                super.updateItem(item, empty);
	                if (empty || item == null) {
	                    setText(null);
	                } else {
	                    setText(item.getL1_name());
	                    setId(item.getId());
	                }
	            }
	        };

	        cell.setOnDragDetected(event -> {
	            if (cell.getItem() == null) {
	                return;
	            }

	            Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
	            ClipboardContent content = new ClipboardContent();
	            content.putString(cell.getItem().getId());
	            dragboard.setContent(content);
	            event.consume();
	        });

	        cell.setOnDragOver(event -> {
	            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
	                event.acceptTransferModes(TransferMode.MOVE);
	            }
	            event.consume();
	        });

	        cell.setOnDragEntered(event -> {
	            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
	                cell.setOpacity(0.3);
	            }
	        });

	        cell.setOnDragExited(event -> {
	            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
	                cell.setOpacity(1);
	            }
	        });

	        cell.setOnDragDropped(event -> {
	            if (cell.getItem() == null) {
	                return;
	            }

	            Dragboard dragboard = event.getDragboard();
	            if (dragboard.hasString()) {
	                ObservableList<StageOne> items = l1StageListView.getItems();
	                int draggedIndex = -1;
	                int dropIndex = items.indexOf(cell.getItem());

	                for (int i = 0; i < items.size(); i++) {
	                    if (items.get(i).getId().equals(dragboard.getString())) {
	                        draggedIndex = i;
	                        break;
	                    }
	                }

	                if (draggedIndex != -1 && dropIndex != -1 && draggedIndex != dropIndex) {
	                    StageOne draggedItem = items.remove(draggedIndex);
	                    items.add(dropIndex, draggedItem);
	                }

	                event.setDropCompleted(true);
	                l1StageListView.getSelectionModel().select(dropIndex);
	            } else {
	                event.setDropCompleted(false);
	            }
	            event.consume();
	        });

	        cell.setOnDragDone(DragEvent::consume);

	        return cell;
	    });

	    ObservableList<StageOne> l1StageData = getl1StageData();
	    setTreeViewData(l1StageData);
	}


	private ObservableList<StageOne> getl1StageData() {
		ObservableList<StageOne> stageList = FXCollections.observableArrayList();
		StageMasterLevelOneResponse response = sessionManagement.getLevelOneStageMasterBySessionId(UUT_ID,
				SESSION_TYPE_ID);
		if (response.getResponse().getResponseCode() == 1) {
			for (LevelOneDto levelOneDto : response.getLevelOneResponse()) {
				if (UUT_ID.equals(levelOneDto.getUutId())) {
					StageOne stage = new StageOne();
					stage.setL1_name(levelOneDto.getStageName());
					stage.setId(levelOneDto.getLevelOneId());
					stage.setDefault(levelOneDto.isDefaultStatus());
					stage.setMandatory(levelOneDto.isMandatoryStatus());
					stage.setContinueWithError(levelOneDto.isContinueWithErrorStatus());
					stage.setAdvancedTest(levelOneDto.isAdvanceTestStatus());
					stageList.add(stage);
				}
			}
		}
		return stageList;
	}
	
	private void setTreeViewData(ObservableList<StageOne> l1StageData) {
		if(l1StageData != null && l1StageData.size() >0) {
			l1StageListView.setItems(l1StageData);
		}else {
			Notifications.showErrorAlert("Data fetching failed");
		}
	}
	
	private void initializeButtonBox() {
		closeButton.setOnAction(e -> {
			handleClosePopup();
		});
		
		saveButton.setOnAction(e -> {
			ObservableList<String> levelOneIdList = FXCollections.observableArrayList();
			for(StageOne l1StageData : l1StageListView.getItems()) {
				levelOneIdList.add(l1StageData.getId());
			}
			
			if(levelOneIdList.size() == 0) {
				Notifications.showWarningAlert("Level one stages not available.");
				return ;
			}
			
			Response response = stageConfig.addSessionTypeOrder(UUT_ID, SESSION_TYPE_ID, levelOneIdList);
			if(response.getResponseCode() == 1) {
				Notifications.showSuccessAlert(response.getResponseMessage());
			}else if(response.getResponseCode() == 0) {
				Notifications.showErrorAlert(response.getResponseMessage());
			}
		});
	}

	private void handleClosePopup() {
		Stage stage = (Stage) stageReorderContainer.getScene().getWindow();
		stage.close();
	}
}
