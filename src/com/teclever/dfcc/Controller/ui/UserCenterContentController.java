package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.SessionStageMapResponse;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.SessionData;
import com.teclever.dfcc.model.StageIdName;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UserCenterContentController {

	private static UserCenterContentController instance;
	static String currentSessionName="";
	
	private StackPane centerStackPane = new StackPane();
	private StackPane dashboardStackPane = new StackPane();
	private StackPane testingStackPane = new StackPane();
	private StackPane resultsStackPane = new StackPane();
	private StackPane selfTestStackPane = new StackPane();
	private StackPane lruTestStackPane = new StackPane();
	private StackPane sessionTestingStackPane = new StackPane();
	private StackPane advancedTestingStackPane = new StackPane();
	private StackPane trialsConfigStackPane = new StackPane();
	private StackPane currentExecutionResultStackPane = new StackPane();
	private StackPane currentSessionResultStackPane = new StackPane();
	private StackPane currentUnitResultStackPane = new StackPane();
	private StackPane currentStageResultStackPane = new StackPane();
	private StackPane pqtReportStackPane = new StackPane();
	private StackPane essReportStackPane = new StackPane();
	private StackPane datapackReportStackPane = new StackPane();
	private StackPane configurationStackPane = new StackPane();
	private StackPane logBookStackPane = new StackPane();
	private StackPane reportsUploadStackPane = new StackPane();
	private StackPane advancedDataAnalysisStackPane = new StackPane();
	
	
	private SessionManagement sessionManagement = new SessionManagement();

	public UserCenterContentController() {
		
		getAllStagesData();
//		centerStackPane.getChildren().addAll(dashboardStackPane);
		initializeRdfFileCopyPopup();
	}
	public static UserCenterContentController getInstance() {
		if (instance == null) {
			synchronized (UserCenterContentController.class) {
				if (instance == null) {
					instance = new UserCenterContentController();
				}
			}
		}
		return instance;
	}
//	Changed by Vignesh 31-07-25 for showing stage name in result page
	public void createUserCenterContent(GridPane bottomMidTopGridPane, String selectedMenu ,String sessionId, SessionData rowData) {
		
		GridPane main = (GridPane) bottomMidTopGridPane.getParent().getParent();
		GridPane child = (GridPane) main.getChildren().get(0);
		TreeView<Label> menuItem = (TreeView<Label>) child.getChildren().get(0);
		VBox subMenuItem = (VBox) child.getChildren().get(1);

				
		for (TreeItem<Label> childItem : menuItem.getRoot().getChildren()) {
		    Label item = childItem.getValue();

		    for (TreeItem<Label> childSubItem : childItem.getChildren()) {
		        Label subItem = childSubItem.getValue();
		        if (subItem.getText().equals(selectedMenu)) {
		            menuItem.getSelectionModel().select(childSubItem);
		        } else {
		            menuItem.getSelectionModel().clearSelection(menuItem.getRow(childSubItem));
		        }
		    }

		    if (childItem.getChildren().isEmpty()) {
		        if (item.getText().equals(selectedMenu)) {
		            menuItem.getSelectionModel().select(childItem);
		        } else {
		            menuItem.getSelectionModel().clearSelection(menuItem.getRow(childItem));
		        }
		    }
		}
		
		for (Node node : subMenuItem.getChildren()) {
			if (node instanceof Label) {
				Label newLabel = (Label) node;
				if(!newLabel.getText().equals(selectedMenu)) {					
					((Label) node).getStyleClass().remove("selected");
				}
			}
		}

//System.out.println("Selected menu " +selectedMenu );
		switch (selectedMenu) {
		case "Dashboard":
			if (!centerStackPane.getChildren().contains(dashboardStackPane)) {
				DashboardControllerCenter dashboardControllerCenter = new DashboardControllerCenter();
				dashboardStackPane.getChildren().add(dashboardControllerCenter.createDashboardCenterMainContainerGridPane());
				centerStackPane.getChildren().add(dashboardStackPane);
			} else {
				dashboardStackPane.toFront();
			}
			
			break;
		case "Testing":
			testingStackPane.toFront();
			break;
		case "Results":
			resultsStackPane.toFront();
			break;
		case "Self Test":
			if (!centerStackPane.getChildren().contains(selfTestStackPane)) {
				SelfTestController selfTestController = new SelfTestController();
				selfTestStackPane.getChildren().add(selfTestController.createSelfTestMainContainerGridPane());
				centerStackPane.getChildren().add(selfTestStackPane);
			} else {
				selfTestStackPane.toFront();
			}

			break;

		case "SRU/LRU Test":
			if (!centerStackPane.getChildren().contains(lruTestStackPane)) {
				LRUTestingController lruTestController = new LRUTestingController();
				lruTestStackPane.getChildren().add(lruTestController.createlruTestMainContainerGridPane());
				centerStackPane.getChildren().add(lruTestStackPane);
			} else {
				lruTestStackPane.toFront();
			}
			
			break;
			
		case "Session Testing":
			if (!centerStackPane.getChildren().contains(sessionTestingStackPane)) {
				SessionTestingController sessionTestingController = new SessionTestingController();
				sessionTestingStackPane.getChildren().add(sessionTestingController.createSessionTestingGridPane(false));
				centerStackPane.getChildren().add(sessionTestingStackPane);
			} else {
				sessionTestingStackPane.toFront();
			}
			
			break;
			
		case "Advanced Testing":
			if (!centerStackPane.getChildren().contains(advancedTestingStackPane)) {
				AdvancedTestingController advancedTestingController = new AdvancedTestingController();
				advancedTestingStackPane.getChildren().add(advancedTestingController.createAdvancedTestingGridPane());
				centerStackPane.getChildren().add(advancedTestingStackPane);
			} else {
				advancedTestingStackPane.toFront();
			}
			
			break;
			
		case "OFP-Loading":
			if (!centerStackPane.getChildren().contains(advancedTestingStackPane)) {
				AdvancedTestingController advancedTestingController = new AdvancedTestingController();
				advancedTestingStackPane.getChildren().add(advancedTestingController.createAdvancedTestingGridPane());
				centerStackPane.getChildren().add(advancedTestingStackPane);
			} else {
				advancedTestingStackPane.toFront();
			}
			
			break;
			
			
		case "Trials Config" :
			if (!centerStackPane.getChildren().contains(trialsConfigStackPane)) {
				TrialsConfigurationController trialsConfigurationController = new TrialsConfigurationController();
				trialsConfigStackPane.getChildren().add(trialsConfigurationController.createTrialsConfigMainGridPane());
				centerStackPane.getChildren().add(trialsConfigStackPane);
			} else {
				trialsConfigStackPane.toFront();
			}
			
			break;
			
		case "Trials Testing":
			if (!centerStackPane.getChildren().contains(sessionTestingStackPane)) {
				SessionTestingController sessionTestingController = new SessionTestingController();
				SessionManagement sessionManagement = new SessionManagement();
				if(!sessionManagement.getFinalizeStatus()) {
					Notifications.showWarningAlert("Please finalize the trials configuration on the trials config page if you want to run trials testing..");
					return;
				}
				getAllStagesData();
				sessionTestingStackPane.getChildren().add(sessionTestingController.createSessionTestingGridPane(true));
				centerStackPane.getChildren().add(sessionTestingStackPane);
			} else {
				sessionTestingStackPane.toFront();
			}
			
			break;
					
		case "Current Execution" :			
			CurrentExecutionResultController currentExcecutionResultController = new CurrentExecutionResultController();
			if (centerStackPane.getChildren().contains(currentExecutionResultStackPane)) {
				currentExecutionResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentExecutionResultStackPane);
			}
			currentExecutionResultStackPane.getChildren().add(currentExcecutionResultController.createCurrentExecutionResultGridPane(null,null));
			centerStackPane.getChildren().add(currentExecutionResultStackPane);
			
			break;
	
			
		case "Unit Results" :
			CurrentUnitResultController currentUnitResultController = new CurrentUnitResultController();
			if (centerStackPane.getChildren().contains(currentUnitResultStackPane)) {
				currentUnitResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentUnitResultStackPane);
			}
			currentUnitResultStackPane.getChildren().add(currentUnitResultController.createcurrentUnitResultGridPane());
			centerStackPane.getChildren().add(currentUnitResultStackPane);
			
			break;

		case "Session Results" :
			CurrentSessionResultController currentSessionResultController = new CurrentSessionResultController();
			if (centerStackPane.getChildren().contains(currentSessionResultStackPane)) {
				if(sessionId == null) {
					currentSessionResultStackPane.toFront();
					break ;
				}
				currentSessionResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentSessionResultStackPane);
			}
			currentSessionResultStackPane.getChildren().add(currentSessionResultController.createCurrentSessionResultGridPane(sessionId));
			centerStackPane.getChildren().add(currentSessionResultStackPane);	
			
			break;
		case "Data Analysis":
			
			DataAnalysisController dataAnalysisController = new DataAnalysisController();
			StackPane fullScreenPane = new StackPane(
			    dataAnalysisController.createDashboardMainContainerGridPane()
			);

			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(fullScreenPane));
			stage.setMaximized(true); 
			stage.show();
			break;	
			
			
		case "Stage Results" :	
			CurrentStageResultController currentStageResultController = new CurrentStageResultController();
			if (centerStackPane.getChildren().contains(currentStageResultStackPane)) {
				if(sessionId == null && rowData == null) {
					currentStageResultStackPane.toFront();
					break ;
				}
				currentStageResultStackPane.getChildren().clear();
				centerStackPane.getChildren().remove(currentStageResultStackPane);
			}
//			Changed by Vignesh 31-07-25 for showing stage name in result page
			currentStageResultStackPane.getChildren().add(currentStageResultController.createCurrentExecutionResultGridPane(sessionId,rowData));
			centerStackPane.getChildren().add(currentStageResultStackPane);	
			
			break;
			
		case "PQT Report" :				
			if (!centerStackPane.getChildren().contains(pqtReportStackPane)) {
				ReportController reportControllerForPQT = new ReportController();
				pqtReportStackPane.getChildren().add(reportControllerForPQT.createReportGridPane("PQT REPORT"));
				centerStackPane.getChildren().add(pqtReportStackPane);
			} else {
				pqtReportStackPane.toFront();
			}
			
			break;
			
		case "ESS Report" :				
			if (!centerStackPane.getChildren().contains(essReportStackPane)) {
				ReportController reportControllerForESS = new ReportController();
				essReportStackPane.getChildren().add(reportControllerForESS.createReportGridPane("ESS REPORT"));
				centerStackPane.getChildren().add(essReportStackPane);
			} else {
				essReportStackPane.toFront();
			}
			
			break;

		case "Datapack Report" :			
			if (!centerStackPane.getChildren().contains(datapackReportStackPane)) {
				ReportController reportControllerForDatapack = new ReportController();
				datapackReportStackPane.getChildren().add(reportControllerForDatapack.createReportGridPane("DATAPACK REPORT"));
				centerStackPane.getChildren().add(datapackReportStackPane);
			} else {
				datapackReportStackPane.toFront();
			}
			
			break;

		case "Upload" :	
			if (!centerStackPane.getChildren().contains(reportsUploadStackPane)) {
				ReportsUploadController uploadController = new ReportsUploadController();
				reportsUploadStackPane.getChildren().add(uploadController.createUploadReportGridPane(selectedMenu));
				centerStackPane.getChildren().add(reportsUploadStackPane);
			} else {
				reportsUploadStackPane.toFront();
			}
			
			break;
			
		case "Data Backup" :	
			openDataBackupPopup();			
			break;
			
			
		case "Configuration":
			if (!centerStackPane.getChildren().contains(configurationStackPane)) {
				ConfigurationController configurationController = new ConfigurationController();
				configurationStackPane.getChildren().add(configurationController.createConfigurationGridPane());
				centerStackPane.getChildren().add(configurationStackPane);
			} else {
				configurationStackPane.toFront();
			}
			
			break;
			
		case "Log Book":
			if (!centerStackPane.getChildren().contains(logBookStackPane)) {
				LogBookController logBookController = new LogBookController();
				logBookStackPane.getChildren().add(logBookController.createLogBookMainGridPane());
				centerStackPane.getChildren().add(logBookStackPane);
			} else {
				logBookStackPane.toFront();
			}
			
			break;

		case "End Session":
			EndRemarksController endRemarksController = new EndRemarksController();
			endRemarksController.endSessionPopup();
		   
		    break;
		
		
		
	case "Check Sum":
		CheckSumController checkSumController = new CheckSumController();
		checkSumController.createCheckSumDataPopup();
	   
	    break;
		
			
		}

		if (!bottomMidTopGridPane.getChildren().contains(centerStackPane)) {
			bottomMidTopGridPane.getChildren().add(centerStackPane);
		}
	}

	private void clearAllData() {
		StateMachine.resetStateMachine();
		SelfTestStateObject.resetSelfTestStateObject();
		LRUTestStateObject.resetLRUTestStateObject();
		SessionTestStateObject.resetSessionTestStateObject();
		AdvancedTestStateObject.resetAdvancedTestStateObject();
		
		Platform.exit();
	}
	
	private void initializeRdfFileCopyPopup() {
	    SessionTestStateObject.isRdfFileCopyPopupStatusProperty().addListener((observable, oldValue, newValue) -> {
	        if (newValue) {
	            Platform.runLater(() -> {
	                try {
	                    FXMLLoader rdfFileCopyPopup = new FXMLLoader(getClass()
	                            .getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/RdfFileCopy.fxml"));
	                    Parent root = rdfFileCopyPopup.load();

	                    Stage stage = new Stage();
	                    stage.initModality(Modality.APPLICATION_MODAL);
	                    stage.initStyle(StageStyle.UNDECORATED);
	                    stage.centerOnScreen();

	                    SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(false);

	                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	                    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 1000) / 2;
	                    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 500) / 2;
	                    stage.setX(centerX);
	                    stage.setY(centerY);

	                    stage.setScene(new Scene(root));
	                    stage.showAndWait();

	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            });
	        }
	    });
	}

	
	public static void showEndRemarksDialog(String title, String contentText, Runnable onConfirm) {
//		Platform.runLater(() -> {
//			Alert alert = new Alert(AlertType.CONFIRMATION);
//			alert.setTitle(title);
//			alert.setHeaderText(null);
//			alert.setHeight(300);
//			alert.setWidth(500);
//			alert.setContentText(contentText);
//
//			TextArea endRemarksTextArea = new TextArea();
//			endRemarksTextArea.setPromptText("Enter End Remarks");
//			endRemarksTextArea.setPrefHeight(300);
//			endRemarksTextArea.setPrefWidth(500);
//			endRemarksTextArea.setWrapText(true);
//			
//
//			VBox inputDialog = new VBox();
//			inputDialog.getChildren().add(endRemarksTextArea);
//			alert.getDialogPane().setContent(inputDialog);
//
//			endRemarksTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
//			endRemarksTextArea.setText(newValue.length() > 50 ? newValue.substring(0, 50) : newValue);
//			});
//
//			ButtonType buttonTypeSave = new ButtonType("Save");
//			ButtonType buttonTypeCancel = new ButtonType("Cancel");
//
//			alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeCancel);
//
//			Button saveButton = (Button) alert.getDialogPane().lookupButton(buttonTypeSave);
//
//			saveButton.addEventFilter(ActionEvent.ACTION, event -> {
//				userInput = endRemarksTextArea.getText();
//
//				if (userInput == null || userInput.trim().isEmpty()) {
//					Alert alertText = new Alert(AlertType.INFORMATION);
//					alertText.setHeaderText(null);
//					alertText.setContentText("Please Enter END REMARKS");
//					alertText.showAndWait();
//
//					event.consume();
//					Platform.exit();
//		        	System.exit(0);
//				} else {
//					System.out.println("userInput: " + userInput);
//					onConfirm.run();
//				}
//			});
//
//			alert.showAndWait().ifPresent(response -> {
//				if (response == buttonTypeCancel) {
//					alert.close();
//				}
//			});
//		});
	}
	
	private void openDataBackupPopup() {
		try {
			FXMLLoader addUserPopup = new FXMLLoader(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/DataBackup.fxml"));
			Parent root = addUserPopup.load();
			Stage stage = new Stage();
			stage.setTitle("Session Data Backup");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);

			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		    double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - 750) / 2;
		    double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - 200) / 2;
		    stage.setX(centerX);
		    stage.setY(centerY);
			
			stage.setScene(new Scene(root));
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void getAllStagesData() {
		SessionStageMapResponse data = sessionManagement
				.getAllSessionStageMapping(currentSessionDetails.getSessionId());
		DFCCConstant.stageIdStatus = sessionManagement.getStageIdStatus(currentSessionDetails.getSessionId());
		if (data.getResponse().getResponseCode() == 1) {
			StateMachine.setStageDatalist(data.getListOfStageObject());
			getSessionTestData();
		} else {
			Debug.printDebug("Error in getAllStagesData : " + data.getResponse().getResponseMessage());
		}
	}

	private void getSessionTestData() {
		List<StageObject> stageList = StateMachine.getStageDatalist();
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);

		observableStageList.stream().filter(stage -> {
			return !(stage.isDefaultStatus() || stage.isAdvanceStatus());
		}).forEach(stage -> {
			String l1StageId = stage.getL1StageId();
			SessionTestStateObject.addL1StageMap(l1StageId, stage);
			SessionTestStateObject.addL1MandatoryStatus(l1StageId, stage.isMandatoryStatus());
			SessionTestStateObject.addL1ContinueWithErrorStatus(l1StageId, stage.isContinueWithErrorStatus());
		});

		observableStageList.stream().forEach(stage -> {
			String l1StageId = stage.getL1StageId();
			String l2StageId = stage.getL2StageId();
			String l3StageId = stage.getL3StageId();
			String l4StageId = stage.getL4StageId();
			String l5StageId = stage.getL5StageId();
			if (l2StageId != null && SessionTestStateObject.getL1StageMap().containsKey(l1StageId)) {
				StageIdName l2StageObject = new StageIdName();
				l2StageObject.setParentId(l1StageId);
				l2StageObject.setStageId(l2StageId);
				l2StageObject.setStageName(stage.getL2StageName());
				if (l3StageId == null && stage.getTestTypeId() != null) {
					l2StageObject.setTestTypeId(stage.getTestTypeId());
					//Start Here Mani Added 29-08-2025
					String l2Status = "";
					if (DFCCConstant.stageIdStatus.get(l2StageId).contains("completed")) {
						l2Status = "COMPLETED";
					} else {
						l2Status = "pending";
					}
					//Ended Here ....
					SessionTestStateObject.getEndLeafMap().put(l2StageObject, l2Status);
					//Mani Commented 29-08-2025
					//SessionTestStateObject.getEndLeafMap().put(l2StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l2StageId);
				}
				SessionTestStateObject.addL2StageMap(l2StageId, l2StageObject);
			}

			if (l3StageId != null && SessionTestStateObject.getL2StageMap().containsKey(l2StageId)) {
				StageIdName l3StageObject = new StageIdName();
				l3StageObject.setParentId(l2StageId);
				l3StageObject.setStageId(l3StageId);
				l3StageObject.setStageName(stage.getL3StageName());
				if (l4StageId == null && stage.getTestTypeId() != null) {
					l3StageObject.setTestTypeId(stage.getTestTypeId());
					//Mani Added 29-08-25
					String l3Status = "";
					if (DFCCConstant.stageIdStatus.get(l3StageId).contains("completed")) {
						l3Status = "COMPLETED";
					} else {
						l3Status = "pending";
					}
					SessionTestStateObject.getEndLeafMap().put(l3StageObject, l3Status);
					//Ended Here
					
					//Mani Commented
					//SessionTestStateObject.getEndLeafMap().put(l3StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l3StageId);
				}
				SessionTestStateObject.addL3StageMap(l3StageId, l3StageObject);
			}

			if (l4StageId != null && SessionTestStateObject.getL3StageMap().containsKey(l3StageId)) {
				StageIdName l4StageObject = new StageIdName();
				l4StageObject.setParentId(l3StageId);
				l4StageObject.setStageId(l4StageId);
				l4StageObject.setStageName(stage.getL4StageName());
				if (l5StageId == null && stage.getTestTypeId() != null) {
					l4StageObject.setTestTypeId(stage.getTestTypeId());
					
					
					//Mani Added 29-08-25
					String l4Status = "";
					if (DFCCConstant.stageIdStatus.get(l4StageId).contains("completed")) {
						l4Status = "COMPLETED";
					} else {
						l4Status = "pending";
					}
					SessionTestStateObject.getEndLeafMap().put(l4StageObject, l4Status);
					//Ended Here
					
					//Mani Commented
					//SessionTestStateObject.getEndLeafMap().put(l4StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l4StageId);
				}
				SessionTestStateObject.addL4StageMap(l4StageId, l4StageObject);
			}

			if (l5StageId != null && SessionTestStateObject.getL4StageMap().containsKey(l4StageId)) {
				StageIdName l5StageObject = new StageIdName();
				l5StageObject.setParentId(l4StageId);
				l5StageObject.setStageId(l5StageId);
				l5StageObject.setStageName(stage.getL5StageName());
				if (stage.getTestTypeId() != null) {
					l5StageObject.setTestTypeId(stage.getTestTypeId());
					
					//Mani Added 29-08-25
					String l5Status = "";
					if (DFCCConstant.stageIdStatus.get(l5StageId).contains("completed")) {
						l5Status = "COMPLETED";
					} else {
						l5Status = "pending";
					}
					SessionTestStateObject.getEndLeafMap().put(l5StageObject, l5Status);
					//Ended Here
					
					//Mani Commented
					//SessionTestStateObject.getEndLeafMap().put(l5StageObject, stage.getStatus());
					SessionTestStateObject.addEndLeafToL1StagesWithEndLeadId(l1StageId, l5StageId);
				}
				SessionTestStateObject.addL5StageMap(l5StageId, l5StageObject);
			}
		});
	}


}