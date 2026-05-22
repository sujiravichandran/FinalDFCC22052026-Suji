package com.teclever.dfcc.Controller.ui;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.model.RdfFileCopy;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class RdfFileCopyPopupController {

	@FXML
	public AnchorPane rdfFileCopyMainContainer;
	@FXML
	private HBox rdfFileCopyHeadingHBox;
	@FXML
	private VBox rdfFileCopyMidVBox;

	private Label headingLabel = new Label("Move RDF Files");
	private HBox buttonHBox = new HBox(15);
	private Button copyButton = new Button("Move Files");
	private Button closeButton = new Button("Close");
	private TableView<RdfFileCopy> tableView = new TableView<>();

	private VBox pathLabelVBox = new VBox(5);
	private HBox currentDirHBox = new HBox(5);
	private HBox copyDirHBox = new HBox(5);
	private HBox noteHBox = new HBox(5);

	private Label currentDirLabel = new Label("Current Directory");
	private Label currentDirPath = new Label("----");
	private Label copyDirLabel = new Label("Copy Directory");
	private Label copyDirPath = new Label("----");
	private Label noteLabel = new Label("Note:\n"
			+ " *Click the Move option to move the selected files to the respective output folder.\n"
			+" *Click the Close option to return to the application.\n" 
			+" *Unselected files will be moved to the Miscellaneous folder.");
	
	
	
	private ObservableList<RdfFileCopy> tableData = FXCollections.observableArrayList();
	private String sessionId = currentSessionDetails.getSessionId();
	private String stageId = SessionTestStateObject.getPopupStageId();
	public static List <CopyFileDTO>rdfFilesListtoShow = new ArrayList<CopyFileDTO>();
	

	private SessionFileManagement sessionFileManagement = new SessionFileManagement();

	@FXML
	private void initialize() {
		
		
		currentDirPath.setWrapText(true);
	    copyDirPath.setWrapText(true);

	    
	    currentDirPath.prefWidthProperty().bind(currentDirHBox.widthProperty());
	    copyDirPath.prefWidthProperty().bind(copyDirHBox.widthProperty());

		
		
	    headingLabel.getStyleClass().add("title");
		
	    rdfFileCopyHeadingHBox.getChildren().add(headingLabel);
	    rdfFileCopyHeadingHBox.setAlignment(Pos.CENTER);
	    createMidContainer();
	    getRdfFileDetails();
	    DFCCConstant.closedFileMove = false;
	    DFCCConstant. continueAfterPopupAction = false;

	    // Center the popup window
	    Platform.runLater(() -> centerPopupWindow());
	}

	private void centerPopupWindow() {
		
	    Stage stage = (Stage) rdfFileCopyMainContainer.getScene().getWindow();

	    // Get screen dimensions
	    double screenWidth = Screen.getPrimary().getBounds().getWidth();
	    double screenHeight = Screen.getPrimary().getBounds().getHeight();

	    // Get popup dimensions
	    double windowWidth = stage.getWidth();
	    double windowHeight = stage.getHeight();

	    // Calculate center position
	    double centerX = (screenWidth - windowWidth) / 2;
	    double centerY = (screenHeight - windowHeight) / 2;

	    // Set the popup's position
	    stage.setX(centerX);
	    stage.setY(centerY);
	}

	public void createMidContainer() {
	    tableView.getStyleClass().add("rdf-file-table");
	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    TableColumn<RdfFileCopy, Boolean> selectColumn = new TableColumn<>("");
	    selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
	    selectColumn.setReorderable(false);
	    selectColumn.setSortable(false);
	    selectColumn.setStyle("-fx-alignment: CENTER;");

	    selectColumn.setPrefWidth(60);
	    selectColumn.setMinWidth(60);
	    selectColumn.setMaxWidth(60);

	    // Custom rendering for the checkbox
	    selectColumn.setCellFactory(tc -> new TableCell<RdfFileCopy, Boolean>() {
	        private final CheckBox checkBox = new CheckBox();

	        @Override
	        protected void updateItem(Boolean item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty) {
	                setGraphic(null);
	            } else {
	                checkBox.setSelected(item != null && item);
	                checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
	                    RdfFileCopy rdfFileCopy = getTableRow().getItem();
	                    if (rdfFileCopy != null) {
	                        rdfFileCopy.setSelected(isNowSelected);
	                    }
	                });
	                setGraphic(checkBox);
	            }
	        }
	    });

	    TableColumn<RdfFileCopy, String> fileNameColumn = new TableColumn<>("File Path");
	    fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
	    fileNameColumn.setReorderable(false);
	    fileNameColumn.setSortable(false);
	    fileNameColumn.setStyle("-fx-alignment: CENTER;");

	    TableColumn<RdfFileCopy, String> statusColumn = new TableColumn<>("Status");
	    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

	    tableView.getColumns().addAll(selectColumn, fileNameColumn, statusColumn);
	    

	    // Create "Select All" Button
	    Button selectAllButton = new Button("Select All");
	    selectAllButton.setOnAction(e -> {
	        boolean allSelected = tableView.getItems().stream().allMatch(RdfFileCopy::isSelected);

	        if (allSelected) {
	           
	            for (RdfFileCopy item : tableView.getItems()) {
	                item.setSelected(false);
	            }
	            selectAllButton.setText("Select All");
	        } else {
	           
	        for (RdfFileCopy item : tableView.getItems()) {
	            item.setSelected(true);
	        }
	            selectAllButton.setText("Deselect All");
	        }
	        tableView.refresh();
	    });

	    HBox buttonBox = createButtonBox();
	    buttonBox.getChildren().add(0, selectAllButton); // Add the button at the beginning

	    rdfFileCopyMidVBox.setSpacing(50);	    
	    rdfFileCopyMidVBox.getChildren().addAll(tableView, createLabelBox(), buttonBox);
	}


	private VBox createLabelBox() {
		currentDirLabel.setPrefWidth(500);
		copyDirLabel.setPrefWidth(500);
		

		currentDirLabel.getStyleClass().add("rdf-file-path-label");
		currentDirPath.getStyleClass().add("rdf-file-path-label");
		copyDirLabel.getStyleClass().add("rdf-file-path-label");
		copyDirPath.getStyleClass().add("rdf-file-path-label");
		copyDirPath.getStyleClass().add("rdf-file-path-label");
		noteLabel.getStyleClass().add("rdf-file-path-label");
		
		noteHBox.getChildren().addAll(noteLabel);
		currentDirHBox.getChildren().addAll(currentDirLabel, currentDirPath);
		copyDirHBox.getChildren().addAll(copyDirLabel, copyDirPath);

		pathLabelVBox.getChildren().addAll(noteHBox, currentDirHBox, copyDirHBox);

		return pathLabelVBox;
	}
	
	private void handlePopupFinished(boolean isCloseClicked) {
	    DFCCConstant.closedFileMove = isCloseClicked;

	    // Reset all necessary state
	    DFCCConstant.rdfMoveCanceled = isCloseClicked;
	    // Close the popup stage
	    Stage stage = (Stage) closeButton.getScene().getWindow();
	    stage.close();
	}
	
	

	private HBox createButtonBox() {
		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.getChildren().addAll(copyButton, closeButton);

		closeButton.setOnAction(e -> {
			handlePopupFinished(true);
			StateMachine.setTestState(TestState.STOPPED);
			StateMachine.setConfirmTestFileCompleted(false);
			StateMachine.setCancelTest(true);
			Stage stage = (Stage) closeButton.getScene().getWindow();
			stage.close();
			
			return;
//			handleClosePopup(true);
		});
		
		
//Before Suji Change
//		copyButton.setOnAction(e -> {
//			List<CopyFileDTO> pathList = new ArrayList<>();
//			for (RdfFileCopy rdfFile : tableView.getItems()) {
//				if (rdfFile.isSelected()) {
//					CopyFileDTO newFilePath = new CopyFileDTO();
//					newFilePath.setRdfFileNamewithPath(rdfFile.getFilePath());
//					pathList.add(newFilePath);
//				}
//			}
//			handleCopyingRdfFiles(pathList);
//		});

		// After Suji Change
		copyButton.setOnAction(e -> {
//			////System.out.println("Entred Move File Button Clikc Method");
//			boolean atLeastOneSelected = tableView.getItems().stream().anyMatch(RdfFileCopy::isSelected);
//		    if (!atLeastOneSelected) {
//		        Notifications.showErrorAlert("Please select at least one file before moving.");
//		        return;
//		    }
			Platform.runLater(() -> {
			handlePopupFinished(false);
			});
			AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement
					.getInstance();
			
				if (StateMachine.isRdfMoveLogout()) {
//					////System.out.println("Entred RDF Logout NEW VALUE" + StateMachine.isRdfMoveLogout());
					List<CopyFileDTO> pathList1 = new ArrayList<>();
					for (RdfFileCopy rdfFile : tableView.getItems()) {
						CopyFileDTO newFilePath = new CopyFileDTO();
						if (rdfFile.isSelected()) {
							newFilePath.setFlag(true);
							newFilePath.setStageId(rdfFile.getStageId());

						} else {
							newFilePath.setFlag(false);
							newFilePath.setStageId(rdfFile.getStageId());

						}
						newFilePath.setStagePath(rdfFile.getStagePath());
						newFilePath.setRdfFileNamewithPath(rdfFile.getFilePath());
						pathList1.add(newFilePath);
					}
					handleCopyingRdfFiles(pathList1);
//					////System.out.println("Entred RDF Logout Front");
					aitessProcessControlManagement.endAllProcessOnLogout();
					Platform.exit();
					System.exit(0);
				StateMachine.setRdfMoveLogout(false);
				return;
				}
				
		
			
			List<CopyFileDTO> pathList = new ArrayList<>();
			for (RdfFileCopy rdfFile : tableView.getItems()) {
				CopyFileDTO newFilePath = new CopyFileDTO();
				if (rdfFile.isSelected()) {
					newFilePath.setFlag(true);
					newFilePath.setStageId(rdfFile.getStageId());

				} else {
					newFilePath.setFlag(false);
					newFilePath.setStageId(rdfFile.getStageId());

				}
				newFilePath.setStagePath(rdfFile.getStagePath());
				newFilePath.setRdfFileNamewithPath(rdfFile.getFilePath());
				pathList.add(newFilePath);
			}
			handleCopyingRdfFiles(pathList);
			StateMachine.setPreviouslySelectedStageId(DFCCConstant.currentTestStageId);
			
			DFCCConstant.continueAfterPopupAction();
		});

		return buttonHBox;
	}

	private void handleClosePopup(boolean showAlert) {
		if(showAlert) {			
			Notifications.showConfirmationDialog("Confirmation Window", "Some RDF files have failed, close the window without copying them?", ()->{
				if(SessionTestStateObject.getIsLogoutFileCopyPopupOpened().get()) {
					SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(false);
				}
				DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
				Stage stage = (Stage) rdfFileCopyMainContainer.getScene().getWindow();
				stage.close();
			});
		}else {
//			////System.out.println("Closing Popup........");
			Stage stage = (Stage) rdfFileCopyMainContainer.getScene().getWindow();
			DFCCConstant.FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
			stage.close();
//			////System.out.println("Closed...........");
			if(SessionTestStateObject.getIsLogoutFileCopyPopupOpened().get()) {
				SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(false);
			}
		}
	}

	private void getRdfFileDetails() {
	
//		////System.out.println("SessionId : "+sessionId+"   "+"StageId : "+stageId);
//		CopyingListDTO response = sessionFileManagement.getShowPopupContent(sessionId, stageId);
		
		
		

	
		//	List<CopyFileDTO> rdfList = response.getLst();
//		////System.out.println("DFCCConstant.FailedStagesRdfPaths.size"+rdfFilesListtoShow);
		String outPut = "";
				String stagePath = "";
			if (rdfFilesListtoShow.size()>0) {
				for (CopyFileDTO rdfFile : rdfFilesListtoShow) {
					
					outPut = rdfFile.getRdfFilePath();
					stagePath = rdfFile.getStagePath();
					
					
//					////System.out.println("PowerAutoStageId"+rdfFile.getStageId());
					RdfFileCopy newRdfFile = new RdfFileCopy(rdfFile.getRdfFileNamewithPath(), rdfFile.getStatus(),
							false,rdfFile.getStageId(),rdfFile.getStagePath());
					tableData.add(newRdfFile);
				}
				
//				////System.out.println("Current Dir Path Check:   " + outPut );
//				////System.out.println("Copy Dir Path Check:   " + stagePath );
				
				
				currentDirPath.setText("-" + outPut);
				copyDirPath.setText("-" +stagePath );
//				
//				currentDirPath.setPrefWidth(500);
//				copyDirPath.setPrefWidth(500);
				
				
				
				tableView.setItems(tableData);
			} else {
				Platform.runLater(() -> Notifications.showErrorAlert("RDF files are empty"));
			}

	
	}


	private void handleCopyingRdfFiles(List<CopyFileDTO> pathList) {
		if (pathList.size() > 0) {
			Task<Void> copyTask = new Task<Void>() {
			    @Override
			    protected Void call() throws Exception {
//			        Response response = sessionFileManagement.copyingSelectedFile(pathList, sessionId, stageId);
			        Response response = sessionFileManagement.copyingSelectedFile(pathList);

//			        ////System.out.println("Response code after copying rdf files : "+ response.getResponseCode());
			        
			        if (response.getResponseCode() == 1) {
			        	Platform.runLater(() -> handleClosePopup(false));	           
			        } else if (response.getResponseCode() == 0) {
			            Platform.runLater(() -> Notifications.showErrorAlert(response.getResponseMessage()));
			            if(SessionTestStateObject.getIsLogoutFileCopyPopupOpened().get()) {
			    			SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(false);
			    		}
			        }
			        
			        return null;
			    }
			};

			new Thread(copyTask).start();
		}else {
			Notifications.showWarningAlert("Please select an RDF file to copy. No file has been selected.");
		}
	}
}