package com.teclever.dfcc.Controller.ui;

import java.util.Map.Entry;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ReportTreeviewController {

	private TreeView<Label> reportTreeView = new TreeView<>();

	public TreeView<Label> createReportTreeView() {
		reportTreeView.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SessionTesting.css").toExternalForm());
		
		TreeItem<Label> rootItem = new TreeItem<>();
		rootItem.setExpanded(true);
		rootItem.setGraphic(null);

		for (Entry<String, StageObject> l1_stage : SessionTestStateObject.getL1StageMap().entrySet()) {
			Label newL1StageLabel = new Label(l1_stage.getValue().getL1StageName());
			newL1StageLabel.setUserData(l1_stage.getValue());
			newL1StageLabel.getStyleClass().add("l1_stage-label");
			TreeItem<Label> sessionItem = new TreeItem<Label>(newL1StageLabel);
			createL2Stage(sessionItem, l1_stage);
			rootItem.getChildren().add(sessionItem);
		}

		reportTreeView.setRoot(rootItem);
		reportTreeView.getStyleClass().add("session-tree-view");
		reportTreeView.setShowRoot(false);

		reportTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Label selectedLabel = newValue.getValue();

//				if (newValue.getChildren().isEmpty()) {
//					Entry<String, StageIdName> userData = (Entry<String, StageIdName>) selectedLabel.getUserData();
//					getTestListByStageId(userData.getValue().getStageId(), userData.getValue().getTestTypeId());
//					selectedStageId = userData.getValue().getStageId();
//					selectedTestTypeId = userData.getValue().getTestTypeId();
//				}
			}
		});

		return reportTreeView;
	}
	
	private void createL2Stage(TreeItem<Label> l1_root, Entry<String, StageObject> l1_stage) {
		for (Entry<String, StageIdName> l2_stage : SessionTestStateObject.getL2StageMap().entrySet()) {
			if (l1_stage.getKey().equals(l2_stage.getValue().getParentId())) {
				Label newL2StageLabel = new Label(l2_stage.getValue().getStageName());
				newL2StageLabel.setId(l2_stage.getKey());
				newL2StageLabel.setUserData(l2_stage);
				newL2StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL2StageLabel);
				createL3Stage(childItem, l2_stage);
				l1_root.getChildren().add(childItem);
			}
		}
	}

	private void createL3Stage(TreeItem<Label> l2_root, Entry<String, StageIdName> l2_stage) {
		for (Entry<String, StageIdName> l3_stage : SessionTestStateObject.getL3StageMap().entrySet()) {
			if (l2_stage.getKey().equals(l3_stage.getValue().getParentId())) {
				Label newL3StageLabel = new Label(l3_stage.getValue().getStageName());
				newL3StageLabel.setId(l3_stage.getKey());
				newL3StageLabel.setUserData(l3_stage);
				newL3StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL3StageLabel);
				createL4Stage(childItem, l3_stage);
				l2_root.getChildren().add(childItem);
			}
		}
	}

	private void createL4Stage(TreeItem<Label> l3_root, Entry<String, StageIdName> l3_stage) {
		for (Entry<String, StageIdName> l4_stage : SessionTestStateObject.getL4StageMap().entrySet()) {
			if (l3_stage.getKey().equals(l4_stage.getValue().getParentId())) {
				Label newL4StageLabel = new Label(l4_stage.getValue().getStageName());
				newL4StageLabel.setId(l4_stage.getKey());
				newL4StageLabel.setUserData(l4_stage);
				newL4StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL4StageLabel);
				createL5Stage(childItem, l4_stage);
				l3_root.getChildren().add(childItem);
			}
		}
	}

	private void createL5Stage(TreeItem<Label> l4_root, Entry<String, StageIdName> l4_stage) {
		for (Entry<String, StageIdName> l5_stage : SessionTestStateObject.getL5StageMap().entrySet()) {
			if (l4_stage.getKey().equals(l5_stage.getValue().getParentId())) {
				Label newL5StageLabel = new Label(l5_stage.getValue().getStageName());
				newL5StageLabel.setUserData(l5_stage);
				newL5StageLabel.setId(l5_stage.getKey());
				newL5StageLabel.getStyleClass().add("l1_stage-label");
				TreeItem<Label> childItem = new TreeItem<>(newL5StageLabel);
				l4_root.getChildren().add(childItem);
			}
		}
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
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TreeItem;
//import javafx.scene.control.TreeView;
//import javafx.scene.layout.HBox;
//
//public class ReportTreeviewController {
//
//	private TreeView<HBox> reportTreeView = new TreeView<>();
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
//			HBox l1HBox = createHBoxWithLabelAndButton(l1_stage.getValue().getL1StageName());
//			l1HBox.setUserData(l1_stage.getValue());
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
//				// Perform actions with selectedHBox or button inside
//			}
//		});
//
//		return reportTreeView;
//	}
//	
//	private void createL2Stage(TreeItem<HBox> l1_root, Entry<String, StageObject> l1_stage) {
//		for (Entry<String, StageIdName> l2_stage : SessionTestStateObject.getL2StageMap().entrySet()) {
//			if (l1_stage.getKey().equals(l2_stage.getValue().getParentId())) {
//				HBox l2HBox = createHBoxWithLabelAndButton(l2_stage.getValue().getStageName());
//				l2HBox.setId(l2_stage.getKey());
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
//				HBox l3HBox = createHBoxWithLabelAndButton(l3_stage.getValue().getStageName());
//				l3HBox.setId(l3_stage.getKey());
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
//				HBox l4HBox = createHBoxWithLabelAndButton(l4_stage.getValue().getStageName());
//				l4HBox.setId(l4_stage.getKey());
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
//				HBox l5HBox = createHBoxWithLabelAndButton(l5_stage.getValue().getStageName());
//				l5HBox.setId(l5_stage.getKey());
//				l5HBox.setUserData(l5_stage);
//				TreeItem<HBox> childItem = new TreeItem<>(l5HBox);
//				l4_root.getChildren().add(childItem);
//			}
//		}
//	}
//
//	private HBox createHBoxWithLabelAndButton(String stageName) {
//		HBox hbox = new HBox();
//		Label label = new Label(stageName);
//		label.setPrefWidth(300);
//		label.getStyleClass().add("l1_stage-label");  
//		
//		Button button = new Button("+");
//		
//		Label addLabel = new Label("+");
//		addLabel.getStyleClass().add("l1_stage-label");
//		
//		hbox.getChildren().addAll(label, addLabel);
//		return hbox;
//	}
//}
