package com.teclever.dfcc.Controller.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.StageIdName;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class ReportTreeviewController {

	private ReportController reportController;
	private GridPane reportLeftGridPane = new GridPane();
	private TreeView<HBox> reportTreeView = new TreeView<>();

	private ObservableMap<String, StageObject> l1StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private ObservableMap<String, StageIdName> l2StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private ObservableMap<String, StageIdName> l3StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private ObservableMap<String, StageIdName> l4StageMap = FXCollections.observableMap(new LinkedHashMap<>());
	private ObservableMap<String, StageIdName> l5StageMap = FXCollections.observableMap(new LinkedHashMap<>());

	private SessionManagement sessionManagement = new SessionManagement();

	public ReportTreeviewController(ReportController reportController) {
		this.reportController = reportController;
	}

	public GridPane createReportLeftGridPane() {
		reportLeftGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Report.css").toExternalForm());
		reportLeftGridPane.getStyleClass().add("report-top-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		reportLeftGridPane.getColumnConstraints().addAll(firstColumn);
		reportLeftGridPane.getRowConstraints().addAll(firstRow);

		return reportLeftGridPane;
	}

	public void initializeReportTreeView(String sessionId) {
		reportLeftGridPane.getChildren().clear();
		if (sessionId != null) {
			reportLeftGridPane.add(createReportTreeView(sessionId), 0, 0);
		}
	}

	private TreeView<HBox> createReportTreeView(String sessionId) {
		reportTreeView.setRoot(null);
		getAllStagesBasedonSessionId(sessionId);
		reportTreeView.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SessionTesting.css").toExternalForm());

		TreeItem<HBox> rootItem = new TreeItem<>();
		rootItem.setExpanded(false);

		for (Entry<String, StageObject> l1_stage : l1StageMap.entrySet()) {
			HBox l1HBox = createHBoxWithLabelAndButton(l1_stage.getValue().getL1StageName(),
					l1_stage.getValue().getL1StageId());
			l1HBox.setUserData(l1_stage.getValue());
			l1HBox.setId(l1_stage.getValue().getL1StageId());
			TreeItem<HBox> sessionItem = new TreeItem<>(l1HBox);
			createL2Stage(sessionItem, l1_stage);
			rootItem.getChildren().add(sessionItem);
		}

		reportTreeView.setRoot(rootItem);
		reportTreeView.getStyleClass().add("session-tree-view");
		reportTreeView.setShowRoot(false);

		reportTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				HBox selectedHBox = newValue.getValue();

			}
		});

		return reportTreeView;
	}

	private void createL2Stage(TreeItem<HBox> l1_root, Entry<String, StageObject> l1_stage) {
		for (Entry<String, StageIdName> l2_stage : l2StageMap.entrySet()) {
			if (l1_stage.getKey().equals(l2_stage.getValue().getParentId())) {
				HBox l2HBox = createHBoxWithLabelAndButton(l2_stage.getValue().getStageName(), l2_stage.getKey());
				l2HBox.setId(l2_stage.getValue().getStageId());
				l2HBox.setUserData(l2_stage);
				TreeItem<HBox> childItem = new TreeItem<>(l2HBox);
				createL3Stage(childItem, l2_stage);
				l1_root.getChildren().add(childItem);
				childItem.setExpanded(false);
			}
		}
	}

	private void createL3Stage(TreeItem<HBox> l2_root, Entry<String, StageIdName> l2_stage) {
		for (Entry<String, StageIdName> l3_stage : l3StageMap.entrySet()) {
			if (l2_stage.getKey().equals(l3_stage.getValue().getParentId())) {
				HBox l3HBox = createHBoxWithLabelAndButton(l3_stage.getValue().getStageName(), l3_stage.getKey());
				l3HBox.setId(l3_stage.getValue().getStageId());
				l3HBox.setUserData(l3_stage);
				TreeItem<HBox> childItem = new TreeItem<>(l3HBox);
				createL4Stage(childItem, l3_stage);
				l2_root.getChildren().add(childItem);
				childItem.setExpanded(false);
			}
		}
	}

	private void createL4Stage(TreeItem<HBox> l3_root, Entry<String, StageIdName> l3_stage) {
		for (Entry<String, StageIdName> l4_stage : l4StageMap.entrySet()) {
			if (l3_stage.getKey().equals(l4_stage.getValue().getParentId())) {
				HBox l4HBox = createHBoxWithLabelAndButton(l4_stage.getValue().getStageName(), l4_stage.getKey());
				l4HBox.setId(l4_stage.getValue().getStageId());
				l4HBox.setUserData(l4_stage);
				TreeItem<HBox> childItem = new TreeItem<>(l4HBox);
				createL5Stage(childItem, l4_stage);
				l3_root.getChildren().add(childItem);
				childItem.setExpanded(false);
			}
		}
	}

	private void createL5Stage(TreeItem<HBox> l4_root, Entry<String, StageIdName> l4_stage) {
		for (Entry<String, StageIdName> l5_stage : l5StageMap.entrySet()) {
			if (l4_stage.getKey().equals(l5_stage.getValue().getParentId())) {
				HBox l5HBox = createHBoxWithLabelAndButton(l5_stage.getValue().getStageName(), l4_stage.getKey());
				l5HBox.setId(l5_stage.getValue().getStageId());
				l5HBox.setUserData(l5_stage);
				TreeItem<HBox> childItem = new TreeItem<>(l5HBox);
				l4_root.getChildren().add(childItem);
				childItem.setExpanded(false);
			}
		}
	}

	private HBox createHBoxWithLabelAndButton(String stageName, String stageId) {
		HBox hbox = new HBox();
		Label label = new Label(stageName);
		label.getStyleClass().add("l1_stage-label");

		Label addLabel = new Label("+");
		addLabel.getStyleClass().add("add-button");
		addLabel.setId(stageId);

		addLabel.setVisible(false);

		hbox.setOnMouseEntered(event -> addLabel.setVisible(true));
		hbox.setOnMouseExited(event -> addLabel.setVisible(false));

		addLabel.setOnMouseClicked(e -> {
			TreeItem<HBox> currentItem = reportTreeView.getSelectionModel().getSelectedItem();

			if (currentItem != null) {
				StringBuilder parentIds = new StringBuilder();
				while (currentItem != null) {
					HBox currentHBox = currentItem.getValue();
					if (currentHBox != null && currentHBox.getId() != null) {
						parentIds.insert(0, currentHBox.getId() + " -> ");
					}
					currentItem = currentItem.getParent();
				}
				if (parentIds.length() > 0) {
					parentIds.setLength(parentIds.length() - 4);
				}
//		        System.out.println("Parent IDs: " + parentIds.toString()); 
				String[] idComponents = parentIds.toString().split(" -> ");

				Platform.runLater(() -> {
					reportController.updateListViewWithSelectedLabel(idComponents);
				});

			}
		});

		hbox.getChildren().addAll(label, addLabel);
		return hbox;
	}

	private void getAllStagesBasedonSessionId(String sessionId) {
		SessionStageMapResponse data = sessionManagement.getAllSessionStageMapping(sessionId);
		if (data.getResponse().getResponseCode() == 1) {
			List<StageObject> stageList = data.getListOfStageObject();
			getSessionTestData(stageList);
		} else {
			System.out.println("Error in getAllStagesData : " + data.getResponse().getResponseMessage());
		}
	}

	private void getSessionTestData(List<StageObject> stageList) {
		l1StageMap.clear();
		l2StageMap.clear();
		l3StageMap.clear();
		l4StageMap.clear();
		l5StageMap.clear();

		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);

		observableStageList.stream().filter(stage -> {
			return !(stage.isDefaultStatus() || stage.isAdvanceStatus());
		}).forEach(stage -> {
			String l1StageId = stage.getL1StageId();
			l1StageMap.put(l1StageId, stage);
		});

		observableStageList.stream().forEach(stage -> {
			String l1StageId = stage.getL1StageId();
			String l2StageId = stage.getL2StageId();
			String l3StageId = stage.getL3StageId();
			String l4StageId = stage.getL4StageId();
			String l5StageId = stage.getL5StageId();
			if (l2StageId != null && l1StageMap.containsKey(l1StageId)) {
				StageIdName l2StageObject = new StageIdName();
				l2StageObject.setParentId(l1StageId);
				l2StageObject.setStageId(l2StageId);
				l2StageObject.setStageName(stage.getL2StageName());
				if (l3StageId == null && stage.getTestTypeId() != null) {
					l2StageObject.setTestTypeId(stage.getTestTypeId());
				}
				l2StageMap.put(l2StageId, l2StageObject);
			}

			if (l3StageId != null && l2StageMap.containsKey(l2StageId)) {
				StageIdName l3StageObject = new StageIdName();
				l3StageObject.setParentId(l2StageId);
				l3StageObject.setStageId(l3StageId);
				l3StageObject.setStageName(stage.getL3StageName());
				if (l4StageId == null && stage.getTestTypeId() != null) {
					l3StageObject.setTestTypeId(stage.getTestTypeId());
				}
				l3StageMap.put(l3StageId, l3StageObject);
			}

			if (l4StageId != null && l3StageMap.containsKey(l3StageId)) {
				StageIdName l4StageObject = new StageIdName();
				l4StageObject.setParentId(l3StageId);
				l4StageObject.setStageId(l4StageId);
				l4StageObject.setStageName(stage.getL4StageName());
				if (l5StageId == null && stage.getTestTypeId() != null) {
					l4StageObject.setTestTypeId(stage.getTestTypeId());
				}
				l4StageMap.put(l4StageId, l4StageObject);
			}

			if (l5StageId != null && l4StageMap.containsKey(l4StageId)) {
				StageIdName l5StageObject = new StageIdName();
				l5StageObject.setParentId(l4StageId);
				l5StageObject.setStageId(l5StageId);
				l5StageObject.setStageName(stage.getL5StageName());
				if (stage.getTestTypeId() != null) {
					l5StageObject.setTestTypeId(stage.getTestTypeId());
				}
				l5StageMap.put(l5StageId, l5StageObject);
			}
		});
	}

}

//package com.teclever.dfcc.Controller.ui;
//
//import java.util.Map.Entry;
//
//import com.teclever.dfcc.DFCCConstant;
//import com.teclever.dfcc.datastore.dto.StageObject;
//import com.teclever.dfcc.model.StageIdName;
//import com.teclever.dfcc.stateMachine.SessionTestStateObject;
//
//import javafx.application.Platform;
//import javafx.scene.control.Label;
//import javafx.scene.control.TreeItem;
//import javafx.scene.control.TreeView;
//import javafx.scene.layout.HBox;
//
//public class ReportTreeviewController {
//
//	private ReportController reportController;
//	private TreeView<HBox> reportTreeView = new TreeView<>();
//
//	 public ReportTreeviewController(ReportController reportController) {
//	        this.reportController = reportController; 
//	    }
//
//	public TreeView<HBox> createReportTreeView() {
//		reportTreeView.getStylesheets().add(getClass()
//				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SessionTesting.css").toExternalForm());
//
//		TreeItem<HBox> rootItem = new TreeItem<>();
//		rootItem.setExpanded(true);
//		rootItem.setGraphic(null);
//
//		for (Entry<String, StageObject> l1_stage : SessionTestStateObject.getL1StageMap().entrySet()) {
//			HBox l1HBox = createHBoxWithLabelAndButton(l1_stage.getValue().getL1StageName(),l1_stage.getValue().getL1StageId());
//			l1HBox.setUserData(l1_stage.getValue());
//			l1HBox.setId(l1_stage.getValue().getL1StageId());
//			TreeItem<HBox> sessionItem = new TreeItem<>(l1HBox);
//			createL2Stage(sessionItem, l1_stage);
//			rootItem.getChildren().add(sessionItem);
//		}
//
//		reportTreeView.setRoot(rootItem);
//		reportTreeView.getStyleClass().add("session-tree-view");
//		reportTreeView.setShowRoot(false);
//
//		reportTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue != null) {
//				HBox selectedHBox = newValue.getValue();
//
//			}
//		});
//
//		return reportTreeView;
//	}
//
//	private void createL2Stage(TreeItem<HBox> l1_root, Entry<String, StageObject> l1_stage) {
//		for (Entry<String, StageIdName> l2_stage : SessionTestStateObject.getL2StageMap().entrySet()) {
//			if (l1_stage.getKey().equals(l2_stage.getValue().getParentId())) {
//				HBox l2HBox = createHBoxWithLabelAndButton(l2_stage.getValue().getStageName(),l2_stage.getKey());
//				l2HBox.setId(l2_stage.getValue().getStageId());
//				l2HBox.setUserData(l2_stage);
//				TreeItem<HBox> childItem = new TreeItem<>(l2HBox);
//				createL3Stage(childItem, l2_stage);
//				l1_root.getChildren().add(childItem);
//			}
//		}
//	}
//
//	private void createL3Stage(TreeItem<HBox> l2_root, Entry<String, StageIdName> l2_stage) {
//		for (Entry<String, StageIdName> l3_stage : SessionTestStateObject.getL3StageMap().entrySet()) {
//			if (l2_stage.getKey().equals(l3_stage.getValue().getParentId())) {
//				HBox l3HBox = createHBoxWithLabelAndButton(l3_stage.getValue().getStageName(),l3_stage.getKey());
//				l3HBox.setId(l3_stage.getValue().getStageId());
//				l3HBox.setUserData(l3_stage);
//				TreeItem<HBox> childItem = new TreeItem<>(l3HBox);
//				createL4Stage(childItem, l3_stage);
//				l2_root.getChildren().add(childItem);
//			}
//		}
//	}
//
//	private void createL4Stage(TreeItem<HBox> l3_root, Entry<String, StageIdName> l3_stage) {
//		for (Entry<String, StageIdName> l4_stage : SessionTestStateObject.getL4StageMap().entrySet()) {
//			if (l3_stage.getKey().equals(l4_stage.getValue().getParentId())) {
//				HBox l4HBox = createHBoxWithLabelAndButton(l4_stage.getValue().getStageName(),l4_stage.getKey());
//				l4HBox.setId(l4_stage.getValue().getStageId());
//				l4HBox.setUserData(l4_stage);
//				TreeItem<HBox> childItem = new TreeItem<>(l4HBox);
//				createL5Stage(childItem, l4_stage);
//				l3_root.getChildren().add(childItem);
//			}
//		}
//	}
//
//	private void createL5Stage(TreeItem<HBox> l4_root, Entry<String, StageIdName> l4_stage) {
//		for (Entry<String, StageIdName> l5_stage : SessionTestStateObject.getL5StageMap().entrySet()) {
//			if (l4_stage.getKey().equals(l5_stage.getValue().getParentId())) {
//				HBox l5HBox = createHBoxWithLabelAndButton(l5_stage.getValue().getStageName(),l4_stage.getKey());
//				l5HBox.setId(l5_stage.getValue().getStageId());
//				l5HBox.setUserData(l5_stage);
//				TreeItem<HBox> childItem = new TreeItem<>(l5HBox);
//				l4_root.getChildren().add(childItem);
//			}
//		}
//	}
//
//	private HBox createHBoxWithLabelAndButton(String stageName, String stageId) {
//		HBox hbox = new HBox();
//		Label label = new Label(stageName);
//		label.getStyleClass().add("l1_stage-label");
//
//		Label addLabel = new Label("+");
//		addLabel.getStyleClass().add("add-button");
//		addLabel.setId(stageId);
//		
//		addLabel.setVisible(false); 
//		
//		hbox.setOnMouseEntered(event -> addLabel.setVisible(true)); 
//		hbox.setOnMouseExited(event -> addLabel.setVisible(false)); 
//		
//		
//		addLabel.setOnMouseClicked(e -> {
//		    TreeItem<HBox> currentItem = reportTreeView.getSelectionModel().getSelectedItem();
//
//		    if (currentItem != null) {
//		        StringBuilder parentIds = new StringBuilder();
//		        while (currentItem != null) {
//		            HBox currentHBox = currentItem.getValue();
//		            if (currentHBox != null && currentHBox.getId() != null) {
//		                parentIds.insert(0,currentHBox.getId() + " -> "); 
//		            }
//		            currentItem = currentItem.getParent(); 
//		        }
//		        if (parentIds.length() > 0) {
//		            parentIds.setLength(parentIds.length() - 4); 
//		        }
////		        System.out.println("Parent IDs: " + parentIds.toString()); 
//		        String[] idComponents = parentIds.toString().split(" -> ");
//		        		        
//		        Platform.runLater(() -> {	            
//		            reportController.updateListViewWithSelectedLabel(idComponents);
//		        });
//		        
//		    }
//		});
//
//
//
//		hbox.getChildren().addAll(label, addLabel);
//		return hbox;
//	}
//
//	
//}
//
