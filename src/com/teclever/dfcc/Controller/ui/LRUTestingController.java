package com.teclever.dfcc.Controller.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.OfpConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.OfpConfigurationDto;
import com.teclever.dfcc.datastore.dto.StageObject;
import com.teclever.dfcc.datastore.dto.TestFileResponse;
import com.teclever.dfcc.datastore.dto.TestProcessDto;
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestResult;
import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestRunningCard;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Debug;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.util.Callback;

public class LRUTestingController {

	private static final Object RUNNING = null;

	private static final TestState PENDING = null;

	private static final boolean COMPLETED = false;

	private static final Shape ROUND = null;
	
	
	private GridPane lruTestMainContainerGridPane = new GridPane();

	private GridPane headingGridPane = new GridPane();

	private HBox headingHbox = new HBox();

	private GridPane lrumidContainerGridPane = new GridPane();
	private HBox midTopHbox1 = new HBox();
	private Label lruLeftLabel = new Label("SRU Identification and Isolation");
	private HBox midTopHbox2 = new HBox();
	private Label lruLeftLabe2 = new Label("GO and NOGO TEST");

	private Label mandatoryTest = new Label("Mandatory Test");


	private GridPane sruSubTestGridPane = new GridPane();
	private Label sruTest = new Label("SRU Test");
	private VBox sruCardVBox = new VBox(10);

	private GridPane goNOGOGridPane = new GridPane();

	private GridPane subTestGridPane = new GridPane();

	private HBox goNoGoLabel = new HBox(10);
	private HBox startButtonHBox = new HBox();

	private VBox goNoGoVBox = new VBox(25);
	private Label goLabel= new Label("GO");
	private Label noGoLabel = new Label("NOGO");

	private HBox buttonHBox = new HBox(5);
	private Button startButton = new Button("Start");
	private Button stopButton = new Button("Stop");
	private Button pauseButton = new Button("Pause");
	private Button runAllButton = new Button("Run All");
	
	private GridPane sruTestCheckBoxList = new GridPane();
	private VBox selectedListVBox = new VBox(5);

	private GridPane bottomGridPane = new GridPane();
	
	private int currentIndex = 0;

	TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
	TestProcessManagement testProcessManagement = new TestProcessManagement();
	RunConfigurationService runConfigurationService = new RunConfigurationService();
	CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
	
	
	
	private String UUT_ID;
    private String ofpConfigId;
    

    private ObservableList<OfpConfigurationDto> ofpVersionDataList;
    private ObservableList<String> ofpVersionList = FXCollections.observableArrayList();

    private StringProperty RUN_CONFIG_ID = new SimpleStringProperty();
    
    private OfpConfigurationManagement ofpConfig = new OfpConfigurationManagement();
    FaultCodeConfiguration faultCodeConfiguration = new FaultCodeConfiguration();
    AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
	
	private VBox buttonMainVBox = new VBox(15);
	private HBox allButtonHBox = new HBox(5);
	private HBox progressBarHBox = new HBox(5);
	private ProgressBar testProgressBar = new ProgressBar();
	private Label percentageLabel = new Label("0%");
    
	public String getRUN_CONFIG_ID() {
        return RUN_CONFIG_ID.get();
    }

    public void setRUN_CONFIG_ID(String rUN_CONFIG_ID) {
        RUN_CONFIG_ID.set(rUN_CONFIG_ID);
    }

    public StringProperty runConfigIdProperty() {
        return RUN_CONFIG_ID;
    }
    
    public LRUTestingController() {
    	 initializeTestStop() ;
    }
    
	public GridPane createlruTestMainContainerGridPane() {

		lruTestMainContainerGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LRUTest.css").toExternalForm());
		lruTestMainContainerGridPane.getStyleClass().add("lruTest-main-container");

		getLruCradData();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(7);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(53);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(40);

		lruTestMainContainerGridPane.setVgap(5);

		lruTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
		lruTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
		lruTestMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));

		lruTestMainContainerGridPane.add(lruheadingGridPane(), 0, 0);
		lruTestMainContainerGridPane.add(lruTestMidContainer(), 0, 1);
		lruTestMainContainerGridPane.add(lruTestBottomContainer(), 0, 2);

		return lruTestMainContainerGridPane;

	}

	private void getLruCradData() {
		List<StageObject> stageList = StateMachine.getStageDatalist();
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);

		observableStageList.stream()
				.filter(stage -> "LRU Test".equalsIgnoreCase(stage.getL1StageName()))
				.filter(stage -> stage.getL3StageId() != null)
		        .sorted((stage1, stage2) -> {
		             int id1 = Integer.parseInt(stage1.getL3StageId().split("_")[1]);
		             int id2 = Integer.parseInt(stage2.getL3StageId().split("_")[1]);
		             return Integer.compare(id1, id2);
		         })
				.forEach(stage -> {
					TestCardData newCard = new TestCardData(stage.getL3StageId(), stage.getL3StageName(), stage.getTestTypeId(), null);

					if ("Mandatory Test".equalsIgnoreCase(stage.getL2StageName())) {
						if (LRUTestStateObject.getLruMandatoryCardList().stream()
								.noneMatch(card -> card.getCardId().equals(newCard.getCardId()))) {
							LRUTestStateObject.addLruMandatoryCard(newCard);
						}
					} else if ("SRU Test".equalsIgnoreCase(stage.getL2StageName())) {
						if (LRUTestStateObject.getLruSruCardList().stream()
								.noneMatch(card -> card.getCardId().equals(newCard.getCardId()))) {
							LRUTestStateObject.addLruSruCard(newCard);
						}
					} else if ("GO & NOGO Test".equalsIgnoreCase(stage.getL2StageName())) {
						if (LRUTestStateObject.getLruGoAndNogoCardList().stream()
								.noneMatch(card -> card.getCardId().equals(newCard.getCardId()))) {
							LRUTestStateObject.addLruGoAndNogoCard(newCard);
						}
					}

				});
		if(LRUTestStateObject.getLruMandatoryCardList().size() > 4){
			LRUTestStateObject.getIsMandatoryFifthCardStatus().set(true);
		}
	}

	public GridPane lruheadingGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		headingGridPane.getStyleClass().add("lruTest-top-container");
		headingGridPane.getColumnConstraints().addAll(firstColumn);
		headingGridPane.getRowConstraints().addAll(firstRow);

		headingGridPane.add(headingHbox(), 0, 0);

		return headingGridPane;

	}

	private HBox headingHbox() {
		Label pageHeading = new Label("LRU TEST");
		pageHeading.getStyleClass().add("lrutest-top-header");
		headingHbox.setAlignment(Pos.CENTER_LEFT);
		headingHbox.getChildren().add(pageHeading);

		return headingHbox;
	}

	private GridPane lruTestMidContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(15);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(40);

		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(20);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(90);

		lrumidContainerGridPane.setHgap(5);
		lrumidContainerGridPane.setVgap(5);

		lrumidContainerGridPane.getStyleClass().add("lruTest-mid-container");
		lrumidContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		lrumidContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);

		lrumidContainerGridPane.add(midTopHbox1(), 0, 0, 3, 1);
		lrumidContainerGridPane.add(midTopHbox2(), 3, 0);
		lrumidContainerGridPane.add(sruIsolationGridPane(), 0, 1);
		lrumidContainerGridPane.add(sruSubTestGridPane(), 1, 1, 2, 1);
		lrumidContainerGridPane.add(goNOGOGridPane(), 3, 1);

		return lrumidContainerGridPane;

	}

	private HBox midTopHbox1() {
		lruLeftLabel.getStyleClass().add("midheader-label");
		midTopHbox1.getStyleClass().add("midheader-hbox");
		midTopHbox1.setAlignment(Pos.CENTER);
		midTopHbox1.getChildren().add(lruLeftLabel);
		return midTopHbox1;
	}

	private HBox midTopHbox2() {
		lruLeftLabe2.getStyleClass().add("midheader-label");
		midTopHbox2.getStyleClass().add("midheader-hbox");
		midTopHbox2.setAlignment(Pos.CENTER);
		midTopHbox2.getChildren().add(lruLeftLabe2);
		return midTopHbox2;
	}

	private GridPane sruIsolationGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(16);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(21);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(21);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(21);
		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(21);

		subTestGridPane.setPadding(new Insets(5, 10, 10, 10));

		subTestGridPane.getStyleClass().add("mid-Gridepane-content");
		subTestGridPane.getColumnConstraints().addAll(firstColumn);
		subTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow);

		subTestGridPane.add(mandatoryTest(), 0, 0);

		return subTestGridPane;

	}

	private Label mandatoryTest() {
		mandatoryTest.getStyleClass().add("midlabel-content");
		mandatoryTest.setMaxWidth(Double.MAX_VALUE);
		mandatoryTest.setAlignment(Pos.CENTER);

		
		subTestGridPane.add(createLruTestCardButton(), 0, 1, 1, 5);

		return mandatoryTest;

	}

	private VBox mandatoryTestVBox = new VBox(10);
	
	
	private void ofpDownWDMUp(String stageId, String stageName, String testTypeId) {
		Task<Response> task = new Task<Response>() {
			@Override
			protected Response call() throws Exception {
				String runConfigId = runConfigurationService
						.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
				currentSessionDetails.setRunConfigId(runConfigId);
				String ID = stageId;
				
				String testFilename1 = "download_verify.com";
				String testFilename2 = "pbit_test.tpf";
				
				List<String> testFileName = Arrays.asList(testFilename1, testFilename2);
				System.out.println("TEST File Name " + testFileName);

				TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
				System.out.println("testPlanFileManagement " + testFileResponse);

				Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
				System.out.println("testFileMap " + testFileMap);

				List<String> testFileList = new ArrayList<>();
				for (Map.Entry<String, String> entry : testFileMap.entrySet()) {

					String fullPath = entry.getValue();
					String extractedFileName = fullPath.substring(fullPath.lastIndexOf('/') + 1);

					// Check if the extracted filename matches any in testFileName
					if (testFileName.contains(extractedFileName)) {
						testFileList.add(entry.getKey());
						System.out.println("Matching File: " + extractedFileName);
					}
				}

				// Validate if all files in `testFileName` have corresponding IDs
				if (testFileList.size() != testFileName.size()) {
					Platform.runLater(() -> {
						Notifications
								.showWarningAlert("Some test files are missing IDs. Please check the configuration.");
					});
					startButton.setDisable(false);
					StateMachine.setTestState(TestState.COMPLETED);
					return null;
				}

				System.out.println("TEST FILE LIST " + testFileList);

				return testProcessManagement.testProcesControl(currentSessionDetails.getSessionId(), ID, 1,
						testFileList, true, stageName, testTypeId, ofpConfigId);
			}
		};

		new Thread(task).start();
	}

	private void ofpUpWDMUp(String stageId, String stageName, String testTypeId) {
		System.out.println("Entered ofpUpWDMUp ");
		Task<Response> task = new Task<Response>() {
			@Override
			protected Response call() throws Exception {
				String runConfigId = runConfigurationService
						.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
				currentSessionDetails.setRunConfigId(runConfigId);
				String ID = stageId;

				String testFilename1 = "pbit_test.tpf";
				List<String> testFileName = Arrays.asList(testFilename1);
				System.out.println("TEST File Name " + testFileName);

				TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
				System.out.println("testPlanFileManagement " + testFileResponse);

				Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
				System.out.println("testFileMap " + testFileMap);

				List<String> testFileList = new ArrayList<>();
				for (Map.Entry<String, String> entry : testFileMap.entrySet()) {

					String fullPath = entry.getValue();
					String extractedFileName = fullPath.substring(fullPath.lastIndexOf('/') + 1);

					// Check if the extracted filename matches any in testFileName
					if (testFileName.contains(extractedFileName)) {
						testFileList.add(entry.getKey());
						System.out.println("Matching File: " + extractedFileName);
					}
				}

				// Validate if all files in `testFileName` have corresponding IDs
				if (testFileList.size() != testFileName.size()) {
					Platform.runLater(() -> {
						Notifications
								.showWarningAlert("Some test files are missing IDs. Please check the configuration.");
					});
					startButton.setDisable(false);
					StateMachine.setTestState(TestState.COMPLETED);
					return null;
				}

				System.out.println("TEST FILE LIST " + testFileList);

				// Call the test process control
				return testProcessManagement.testProcesControl(currentSessionDetails.getSessionId(), ID, 1,
						testFileList, true, stageName, testTypeId, ofpConfigId);
			}
		};

		new Thread(task).start();
	}

	private void initializeOfpVersionComboBox() {
		ofpVersionList.clear();
		UUT_ID = StateMachine.currentSessionDetails.getUutId();

		ofpVersionDataList = FXCollections.observableArrayList(ofpConfig.getOfpConfig(UUT_ID));
		for (OfpConfigurationDto ofpVersion : ofpVersionDataList) {
			ofpVersionList.add(ofpVersion.getOfpVersion());
		}
		OFPVersion.setItems(ofpVersionList);
		OFPVersion.setOnAction((event) -> {
			ofpConfigId = fetchOFPVersion(OFPVersion.getValue());
			this.RUN_CONFIG_ID.set(ofpConfigId);

		});

	}

	private String fetchOFPVersion(String ofpVersionName) {
		for (OfpConfigurationDto ofpVersion : ofpVersionDataList) {
			if (ofpVersion.getOfpVersion().equals(ofpVersionName)) {
				return ofpVersion.getOfpVersion();
			}
		}
		return null;
	}

	ComboBox<String> OFPVersion = new ComboBox<>();

		
	
	
	@SuppressWarnings("unlikely-arg-type")
	private VBox createLruTestCardButton() {
		ObservableList<TestCardData> mandatoryCardList = LRUTestStateObject.getLruMandatoryCardList();
		boolean firstButton = true;
		for (TestCardData card : mandatoryCardList) {
			Button newButton = new Button();
			newButton.setText(card.getCardName().trim());
			newButton.setId(card.getCardId());
			newButton.setUserData(card.getTestTypeId());
			newButton.setMaxWidth(Double.MAX_VALUE);
			newButton.setAlignment(Pos.CENTER);
			newButton.setWrapText(true);
			if(!firstButton) {
				newButton.setDisable(true);	
			}
			firstButton =false;
		  
		    
			newButton.setOnAction(e ->{	
				
//				if(newButton.getText().toLowerCase().contains("spil")) {
//					LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.SPIL_LINK);
//					LRUTestStateObject.updateLruMandatoryCardstatus(newButton.getId(), "OK");
//					LRUTestStateObject.getSpilLinkStatus().set(false);
//					
//				}else if(newButton.getText().toLowerCase().contains("pbit")) {
//					LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
//					LRUTestStateObject.updateLruMandatoryCardstatus(newButton.getId(), "OK");
//					LRUTestStateObject.getPbitStatus().set(false);
//					
//				}else if(newButton.getText().toLowerCase().contains("initialize")) {
//			    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.INITIALIZE_LRU);
//			    	LRUTestStateObject.updateLruMandatoryCardstatus(newButton.getId(), "OK");
//			    	LRUTestStateObject.getInitializeLRUStatus().set(false);
//			    	
//			    }else if(newButton.getText().toLowerCase().contains("power")) {
//			    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.POWER_SUPPLY);
//			    	LRUTestStateObject.updateLruMandatoryCardstatus(newButton.getId(), "OK");
//			    	LRUTestStateObject.getPowerSupplyStatus().set(false);
//			    	
//			    }else if(newButton.getText().toLowerCase().contains("interface")) {
//			    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.AD_DA_INTERFACE);
//			    	LRUTestStateObject.updateLruMandatoryCardstatus(newButton.getId(), "OK");
//			    	LRUTestStateObject.getAd_daInterfaceStatus().set(false);
//			    	
//			    }
				
				 if (!checkAitessStatus.isBothAitessOn()) {
				        newButton.setDisable(true);
				        return;
				    } else {
				        newButton.setDisable(false);
				    }
			

				 TestState currentState = StateMachine.getTestState();            
				    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED || currentState ==  TestState.STOPPED) {
						startButton.setDisable(true);
				    	StateMachine.setTestState(TestState.RUNNING);
				    	StateMachine.setRunningTestName(RunningTestName.LRU_SRU_TEST);
				    	ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
					    ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on " + newButton.getText() + " button");
						appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				    } else if(currentState == TestState.RUNNING) {
				        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				        return;
				    }
				    
				    if(StateMachine.getTestState()!=PENDING ||COMPLETED ) {
				    	 Dialog<ButtonType> dialog = new Dialog<>();
				            dialog.setTitle("Confirmation Dialog");
				            dialog.setContentText("Please click OK to proceed the test (or) Click Cancel");
				            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL );
				            Optional<ButtonType> resultButton = dialog.showAndWait();

				            if (resultButton.isPresent() && resultButton.get() == ButtonType.CANCEL) {
//				                StateMachine.setTestState(TestState.STOPPED);
//				                startButton.setText("Start");
//				                pauseButton.setDisable(true);
//				                stopButton.setDisable(true);
//				                startButton.setDisable(false);
//				                runAllButton.setDisable(false);
//				                sruTestCheckBoxList.setDisable(false);
//				                System.out.println("TEST STATE @ :" + StateMachine.getTestState());
				                dialog.close();
				            }
				    
				            else {
				    if (newButton.getText().toLowerCase().contains("spil")) {
				    	
//			            Dialog<ButtonType> dialog1 = new Dialog<>();
//			            dialog1.setTitle("Confirmation Dialog");
//			            dialog1.setContentText("Please ensure the Rack Power is ON, the DFCC is powered ON, and the cooler switch is turned ON.");
//			            dialog1.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
//			            dialog1.showAndWait();
				    	
				    	
						LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.SPIL_LINK);
					} else if (newButton.getText().toLowerCase().contains("pbit")) {
						UUT_ID = StateMachine.currentSessionDetails.getUutId();

						if("UUT2".equals(StateMachine.currentSessionDetails.getUutId()) || "UUT3".equals(StateMachine.currentSessionDetails.getUutId())) {
							LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
							
						}
						else {
						if (aitessProcessControlManagement.pbitCheck().getResponseCode() == 300) {

							OFPVersion.setPromptText("select OFP Version");
							OFPVersion.setVisible(false);

							Dialog<String> dialog2 = new Dialog<>();
							dialog2.setWidth(500);
							dialog2.setTitle("Check Status");

							Button okButton = new Button("OK");
							Button cancelButton = new Button("Cancel");

							okButton.setOnAction(event -> {
								dialog2.setResult("Ok");
								LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
								dialog.close();
							});

							cancelButton.setOnAction(event -> {
								dialog2.setResult("Cancel");
								dialog.close();
							});

							HBox buttonBox = new HBox();
							buttonBox.setAlignment(Pos.CENTER);
							buttonBox.setSpacing(10);

							buttonBox.getChildren().addAll(okButton, cancelButton);

							RadioButton option1 = new RadioButton("Execute PBIT without loading OFP");
							RadioButton option2 = new RadioButton("Download OFP and Execute PBIT");

							ToggleGroup group = new ToggleGroup();

							if (option1.isPressed()) {
								ofpUpWDMUp(newButton.getId(), "MANDATORY", newButton.getUserData().toString());
							}
							if (option2.isPressed()) {
								ofpDownWDMUp(newButton.getId(), "MANDATORY", newButton.getUserData().toString());
							}

							option1.setToggleGroup(group);
							option2.setToggleGroup(group);

							HBox ofpSelection = new HBox();
							ofpSelection.setAlignment(Pos.CENTER_LEFT);
							ofpSelection.setSpacing(30);

							ofpSelection.getChildren().addAll(option2, OFPVersion);

							VBox vbox = new VBox(option1, ofpSelection, buttonBox);
							vbox.setSpacing(10);
							dialog.getDialogPane().setContent(vbox);

							option2.setOnAction(event -> {
								OFPVersion.setVisible(true);
								initializeOfpVersionComboBox();

							});

							option1.setOnAction(event -> OFPVersion.setVisible(false));

							dialog.showAndWait().ifPresent(result -> {
								Debug.printDebug("Dialog result: " + result);
							});

						}
						if (aitessProcessControlManagement.pbitCheck().getResponseCode() == 400) {

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Information");
							alert.setHeaderText(null);
							alert.setContentText(
									"OFP is not present, PBIT test will continue after downloading the Latest OFP");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.isPresent() && result.get() == ButtonType.OK) {
//								LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
								ofpDownWDMUp(newButton.getId(), "MANDATORY", newButton.getUserData().toString());
							}

						}

						if (aitessProcessControlManagement.pbitCheck().getResponseCode() == 200) {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Information");
							alert.setHeaderText(null);
							alert.setContentText("OFP is Present, and WDM is also Present PBIT test is going to execute");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.isPresent() && result.get() == ButtonType.OK) {
//								LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
								ofpUpWDMUp(newButton.getId(), "MANDATORY", newButton.getUserData().toString());

							}
						}}

						
					}else if(newButton.getText().toLowerCase().contains("initialize")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.INITIALIZE_LRU);
				    }else if(newButton.getText().toLowerCase().contains("power")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.POWER_SUPPLY);
				    }else if(newButton.getText().toLowerCase().contains("interface")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.AD_DA_INTERFACE);
				    }
				            }}    
				    callstartButton(newButton.getId(),"MANDATORY",newButton.getUserData().toString());
			});
					
				    
			mandatoryTestVBox.getChildren().add(newButton);
		}
		
		LRUTestStateObject.spilLinkStatusProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				Button pbitButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(1).getCardId());
				if(checkMandatoryStatus("spil")) {
					pbitButton.setDisable(false);
				}
				StateMachine.setTestState(TestState.COMPLETED);
				LRUTestStateObject.getSpilLinkStatus().set(true);
			}
		
		});
		LRUTestStateObject.pbitStatusProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				if(LRUTestStateObject.getIsMandatoryFifthCardStatus().get()) {
					Button initializeLRUButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(2).getCardId());
					initializeLRUButton.setDisable(false);
					StateMachine.setTestState(TestState.COMPLETED);
				}else {
					Button powerSupplyButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(2).getCardId());
					powerSupplyButton.setDisable(false);
					StateMachine.setTestState(TestState.COMPLETED);
				}
				LRUTestStateObject.getPbitStatus().set(true);
			}
		});
		LRUTestStateObject.initializeLRUStatusProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				Button powerSupplyButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(3).getCardId());
				if(checkMandatoryStatus("initialize")) {
					powerSupplyButton.setDisable(false);
				}
				StateMachine.setTestState(TestState.COMPLETED);
				LRUTestStateObject.getInitializeLRUStatus().set(true);
			}
		});
		LRUTestStateObject.powerSupplyStatusProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				int index = LRUTestStateObject.getIsMandatoryFifthCardStatus().get() ? 4 : 3;
				Button ad_daInterfaceButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(index).getCardId());
				if(checkMandatoryStatus("power")) {
					ad_daInterfaceButton.setDisable(false);
				}
				StateMachine.setTestState(TestState.COMPLETED);
				LRUTestStateObject.getPowerSupplyStatus().set(true);
			}
		});
		LRUTestStateObject.ad_daInterfaceStatusProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				Debug.printDebug("insid-----");
				LRUTestStateObject.getAd_daInterfaceStatus().set(true);
			}
			boolean allCardsStatusOk = true;
		
	        for (TestCardData card : mandatoryCardList) {
	        	System.out.println("----"+card.getCardName()+"   "+card.getStatus());
	            if (card.getStatus().equalsIgnoreCase("NOT OK")) {
	            	System.out.println("Inside 1st IF");
	            	System.out.println("PBIT    :"+!card.getCardName().trim().toLowerCase().contains("pbit"));
	                if (!card.getCardName().trim().toLowerCase().contains("pbit")) {
	                    allCardsStatusOk = false;
	                    break;
	                }
	            }
	        }
	        
	       Debug.printDebug("allCardsStatusOk--------"+allCardsStatusOk);

	        StateMachine.setTestState(TestState.COMPLETED);	
	        
	        if(allCardsStatusOk) {
	        	for(TestCardData card : LRUTestStateObject.getLruSruCardList()) {
		        	Button enableButton = (Button) sruCardVBox.lookup("#" + card.getCardId());
		        	enableButton.setDisable(false);
	        	}
	        	for(TestCardData card : LRUTestStateObject.getLruGoAndNogoCardList()) {
	        		if(card.getCardName().toLowerCase().contains("complete")) {
	        			Button enableButton = (Button) goNoGoVBox.lookup("#" + card.getCardId());
			        	enableButton.setDisable(false);
			        	break;
	        		}
	        	}
	        }
		});

		return mandatoryTestVBox;
	}
	
	

	
	
	private boolean checkMandatoryStatus(String cardName) {
	    List<TestCardData> matchingCards = LRUTestStateObject.getLruMandatoryCardList().stream()
	            .filter(card -> card.getCardName().trim().toLowerCase().contains(cardName))
	            .collect(Collectors.toList());

	    for (TestCardData card : matchingCards) {
	        if (card.getStatus().equalsIgnoreCase("OK")) {
	            return true;
	        }
	    }

	    return false;
	}




	private GridPane sruSubTestGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(40);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(60);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(8);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(17);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(17);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(17);
		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(17);
		RowConstraints SixthRow = new RowConstraints();
		SixthRow.setPercentHeight(24);

		sruSubTestGridPane.setHgap(20);
		sruSubTestGridPane.setPadding(new Insets(5, 10, 10, 10));

		sruSubTestGridPane.getStyleClass().add("mid-Gridepane-content");
		sruSubTestGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		sruSubTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow, SixthRow);

		sruSubTestGridPane.add(sruTest(), 0, 0, 2, 1);
		sruSubTestGridPane.add(createSruTestVBox(), 0, 1, 1, 4);

		sruSubTestGridPane.add(subTestGridPane(), 1, 1, 1, 4);
		sruSubTestGridPane.add(createButtonBox(), 0, 5, 2, 1);

		return sruSubTestGridPane;

	}

	private Label sruTest() {
		sruTest.getStyleClass().add("midlabel-content");
		sruTest.setMaxWidth(Double.MAX_VALUE);
		sruTest.setAlignment(Pos.CENTER);
		return sruTest;
	}


	private VBox createSruTestVBox() {
		
		ObservableList<TestCardData> sruCardList = LRUTestStateObject.getLruSruCardList();

		for (TestCardData card : sruCardList) {
			Button newButton = new Button();
			newButton.setText(card.getCardName());
			newButton.setId(card.getCardId());
			newButton.setUserData(card.getTestTypeId());
			newButton.setMaxWidth(Double.MAX_VALUE);
			newButton.setAlignment(Pos.CENTER);
			newButton.setWrapText(true);
			newButton.setDisable(true);
			
			newButton.setOnAction(e ->{
				TestState currentState = StateMachine.getTestState();            
			    if (currentState != TestState.RUNNING && currentState != TestState.PAUSED) {			    	
			    	getSRUSubStage(newButton.getId(), newButton.getText());
			    	startButton.setDisable(false);
			    	runAllButton.setDisable(false);
			    	ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			    	ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on "+ newButton.getText());
			    	appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			    }else {
			    	Notifications.showWarningAlert("SRU test is currently in progress. Please try again once the test is complete.");
			    }
			});
			sruCardVBox.getChildren().add(newButton);
		}

		return sruCardVBox;
	}

	
	private GridPane subTestGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);
		
		sruTestCheckBoxList.getColumnConstraints().addAll(firstColumn);
		sruTestCheckBoxList.getRowConstraints().addAll(firstRow);
		
		sruTestCheckBoxList.add(createListOfSubStage(),0,0);
		return sruTestCheckBoxList;
	}
	

    private List<CheckBox> checkBoxes = new ArrayList<>();
    private ListView<CheckBox> testListView = new ListView<>();
	

	private Node createListOfSubStage() {
	   	testListView.getStyleClass().add("session-testing-list-view");
	    selectedListVBox.getChildren().add(testListView);
	    selectedListVBox.getStyleClass().add("session-testing-right-container");
	    return selectedListVBox;
	}

	

	private HBox createButtonBox() {
		buttonHBox.setPadding(new Insets(10,0,0,0));
		
		Image playImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/play.png"));
		Image stopImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/stop.png"));
		Image pauseImage = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/pause.png"));

		ImageView playImageView = new ImageView(playImage);
		playImageView.getStyleClass().add("button-image");
		playImageView.setFitHeight(25);
		playImageView.setFitWidth(25);
		playImageView.setPreserveRatio(true);
		playImageView.setSmooth(true);

		ImageView stopImageView = new ImageView(stopImage);
		stopImageView.getStyleClass().add("button-image");
		stopImageView.setFitHeight(25);
		stopImageView.setFitWidth(25);
		stopImageView.setPreserveRatio(true);
		stopImageView.setSmooth(true);

		ImageView pauseImageView = new ImageView(pauseImage);
		pauseImageView.getStyleClass().add("button-image");
		pauseImageView.setFitHeight(25);
		pauseImageView.setFitWidth(25);
		pauseImageView.setPreserveRatio(true);
		pauseImageView.setSmooth(true);
		
		startButton.setGraphic(playImageView);
		startButton.setGraphicTextGap(10);
		stopButton.setGraphic(stopImageView);
		stopButton.setGraphicTextGap(10);
		pauseButton.setGraphic(pauseImageView);
		pauseButton.setGraphicTextGap(10);

		runAllButton.setDisable(true);
		startButton.setDisable(true);
		stopButton.setDisable(true);
		pauseButton.setDisable(true);
		
		initializeButtons();
		
		
		buttonHBox.setAlignment(Pos.CENTER);

		testProgressBar.setProgress(0);
		testProgressBar.getStyleClass().add("progress-bar");
		percentageLabel.getStyleClass().add("progress-label");

		progressBarHBox.setAlignment(Pos.CENTER);
		
		allButtonHBox.getChildren().addAll(runAllButton, startButton, pauseButton, stopButton);
		progressBarHBox.getChildren().addAll(testProgressBar, percentageLabel);
		buttonMainVBox.getChildren().addAll(allButtonHBox, progressBarHBox);

		buttonHBox.getChildren().addAll(buttonMainVBox);
		
		LRUTestStateObject.runnedLRUTestFileCountProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null ) {
				double percentage = (double) LRUTestStateObject.getRunnedLRUTestFileCount().get() / LRUTestStateObject.getTotalLRUSelectedTestFileCount();
				double roundedPercentage = Math.round(percentage * 100.0) / 100.0;
				Platform.runLater(() -> {
					testProgressBar.setProgress(roundedPercentage);
					percentageLabel.setText((int) (roundedPercentage * 100) + "%");
				});
			}
		});
		
		return buttonHBox;
	}


	private void initializeTestStop() {
		StateMachine.setAitess1Launched(true);
		StateMachine.setAitess2Launched(true);
		TestProcessDto testState = new TestProcessDto();
		TestState currentState = StateMachine.getTestState();  
		if (currentState == TestState.RUNNING)
		{
			if(testState.getTestState().equals("STOPPED"))
			{
				Notifications.showErrorAlert(testState.getOnlineStatus() + " Test is Stopped because Some Channel is Offline");
				StateMachine.setTestState(TestState.STOPPED);
				startButton.setText("Start");
				pauseButton.setDisable(true);
				stopButton.setDisable(true);
				startButton.setDisable(false);
				runAllButton.setDisable(false);
				sruTestCheckBoxList.setDisable(false);
			}
		}
		
		
	}

	private void initializeButtons() {
		runAllButton.setOnAction(e -> {
			
			if(!checkAitessStatus.isBothAitessOn()) {
				return ;
			}
			
			TestState currentState = StateMachine.getTestState();            
		    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED || currentState == TestState.STOPPED) {
		    	
		    	for (CheckBox checkBox : checkBoxes) {
			        checkBox.setSelected(true);
			     }
				
				
				List<String> fileIdList = getAllTestFilesForSelectedSubStages();
				
				if(fileIdList == null){	
					return ;
				}
				
				int totalTestFileCount = fileIdList.size() ;
				LRUTestStateObject.setTotalLRUSelectedTestFileCount(totalTestFileCount);
				Platform.runLater(()->{
					percentageLabel.setText("0%");
				});
				LRUTestStateObject.getRunnedLRUTestFileCount().set(0);
	
//				Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> generateData()));
//				timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
//				timeline.play();
				
				sendSelectedSubStageData();
			    startSruTest();
		    	
		    	startButton.setDisable(true);
		    	runAllButton.setDisable(true);
				stopButton.setDisable(false);
				pauseButton.setDisable(false);
				StateMachine.setTestState(TestState.RUNNING);
				sruTestCheckBoxList.setDisable(true);
				
		    } else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			}
		});
		
		startButton.setOnAction(e ->{
			if (startButton.getText().equalsIgnoreCase("Resume")) {
				StateMachine.setTestState(TestState.RUNNING);
				startButton.setText("Start");
				startButton.setDisable(true);
				pauseButton.setDisable(false);
				stopButton.setDisable(false);
				return;
			}
			if(!checkAitessStatus.isBothAitessOn()) {
				return ;
			}
			
			
			TestState currentState = StateMachine.getTestState();            
		    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED || currentState == TestState.STOPPED) {		
				
				boolean selected = false;
				for(CheckBox checkBox : checkBoxes) {
					if(checkBox.isSelected()) {
						selected = true;
					}
				}
				if(!selected) {
					Notifications.showWarningAlert("Please select any checkbox for start test.");
					return ;
				}
				
				List<String> fileIdList = getAllTestFilesForSelectedSubStages();
				
				if(fileIdList == null){	
					return ;
				}
				
				int totalTestFileCount = fileIdList.size() ;
				LRUTestStateObject.setTotalLRUSelectedTestFileCount(totalTestFileCount);
				Platform.runLater(()->{
					percentageLabel.setText("0%");
				});
				LRUTestStateObject.getRunnedLRUTestFileCount().set(0);
	
//				Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> generateData()));
//				timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
//				timeline.play();
				
				
				sendSelectedSubStageData();
		    	startSruTest();
				
				startButton.setDisable(true);
				runAllButton.setDisable(true);
				stopButton.setDisable(false);
				pauseButton.setDisable(false);
				StateMachine.setTestState(TestState.RUNNING);
				StateMachine.setRunningTestName(RunningTestName.LRU_SRU_TEST);
				sruTestCheckBoxList.setDisable(true);
		    	ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			    ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on SRU test START button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
		    } else if (currentState == TestState.RUNNING) {
				Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			} else if (currentState == TestState.PAUSED) {
				Notifications.showWarningAlert(
						StateMachine.getRunningTestName() + " Test is Paused. Please Resume or Stop...");
				startButton.setDisable(false);
				stopButton.setDisable(true);
				pauseButton.setDisable(true);
				return;
			}
		});
		
		pauseButton.setOnAction(e -> {
			StateMachine.setTestState(TestState.PAUSED);
			startButton.setText("Resume");
			pauseButton.setDisable(true);
			startButton.setDisable(false);
			stopButton.setDisable(false);
		});
		
		stopButton.setOnAction(e -> {
			StateMachine.setTestState(TestState.STOPPED);
			startButton.setText("Start");
			pauseButton.setDisable(true);
			stopButton.setDisable(true);
			startButton.setDisable(false);
			runAllButton.setDisable(false);
			sruTestCheckBoxList.setDisable(false);
		});
		
		
	}

	private void generateData() {
		if(LRUTestStateObject.getTotalLRUSelectedTestFileCount() != LRUTestStateObject.getRunnedLRUTestFileCount().get()) {
			LRUTestStateObject.getRunnedLRUTestFileCount().set(LRUTestStateObject.getRunnedLRUTestFileCount().get()+1);
		}
	}

	private List<String> getAllTestFilesForSelectedSubStages() {
		List<String> allSelectedStageTestFileIds = new ArrayList<String>();
		for(TestCardData subStage : LRUTestStateObject.getAllTestFiles()) {
			List<String> response = getTestFilesForStageId(subStage);
			if(response == null) {
				return null;
			}else {
				allSelectedStageTestFileIds.addAll(response);
			}
		}
		return allSelectedStageTestFileIds;
	}
	
	
	private List<String> getTestFilesForStageId(TestCardData subStage){
		List<String> subStageTestFileIds = new ArrayList<String>();TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(subStage.getCardId());
		if (testFileResponse.getTestFilesIdName() == null || testFileResponse.getTestFilesIdName().isEmpty() || testFileResponse.getTestFilesIdName().size() == 0) {
            Platform.runLater(() -> {
                Notifications.showWarningAlert("Please Add Test Files For "+subStage.getCardName() +" Stage... ");
            });
            return null;
        }
            		
        Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
        subStageTestFileIds = new ArrayList<>(testFileMap.keySet());
		return subStageTestFileIds;
	}
	
	private void sendSelectedSubStageData() {
		for(TestCardData subStage : LRUTestStateObject.getSelectedSubStagesList()) {
			subStage.statusProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null && newValue.equals("COMPLETED")) {
					if(LRUTestStateObject.getSelectedSubStagesList().size()-1 > currentIndex) {
						currentIndex++;
						StateMachine.setTestState(TestState.RUNNING);
						callstartButton(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardId(), "SRU", LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getTestTypeId());
						LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex-1).getCardId(), null);
					}else if(LRUTestStateObject.getSelectedSubStagesList().size()-1 == currentIndex) {
						LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardId(), null);
						StateMachine.setTestState(TestState.COMPLETED);
						currentIndex = 0;
						startButton.setDisable(false);
						runAllButton.setDisable(false);
						pauseButton.setDisable(true);
						stopButton.setDisable(true);
						sruTestCheckBoxList.setDisable(false);
					}
				}
			});
			
		}
	}
	
	private void startSruTest() {
		callstartButton(LRUTestStateObject.getSelectedSubStagesList().get(0).getCardId(), "SRU", LRUTestStateObject.getSelectedSubStagesList().get(0).getTestTypeId());
	}

	private GridPane goNOGOGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(18);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(18);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(18);
		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(18);
		RowConstraints SixthRow = new RowConstraints();
		SixthRow.setPercentHeight(18);

		goNOGOGridPane.setPadding(new Insets(5, 10, 10, 10));
		goNOGOGridPane.getStyleClass().add("mid-Gridepane-content");
		goNOGOGridPane.getColumnConstraints().addAll(firstColumn);
		goNOGOGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow, SixthRow);
		goNOGOGridPane.add(goNoGoVBox(), 0, 1, 1, 4);
		goNOGOGridPane.add(goNoGoLabel(), 0, 5);

		return goNOGOGridPane;
	}

	private VBox goNoGoVBox() {		
		ObservableList<TestCardData> goAndNoGoCardList = LRUTestStateObject.getLruGoAndNogoCardList();

		for (TestCardData card : goAndNoGoCardList) {
			Button newButton = new Button();
			newButton.setText(card.getCardName().trim());
			newButton.setId(card.getCardId());
			newButton.setUserData(card.getTestTypeId());
			newButton.setMaxWidth(Double.MAX_VALUE);
			newButton.setAlignment(Pos.CENTER);
			newButton.setWrapText(true);
			newButton.setDisable(true);
			newButton.setOnAction(e ->{
				if(!checkAitessStatus.isBothAitessOn()) {
					return ;
				}
				 TestState currentState = StateMachine.getTestState();            
				    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED || currentState == TestState.STOPPED) {
						
				    	startButton.setDisable(true);
				    	StateMachine.setTestState(TestState.RUNNING);
				    	StateMachine.setRunningTestName(RunningTestName.LRU_SRU_TEST);
				    	ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
					    ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on " + newButton.getText() + " button");
						appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				    } else if(currentState == TestState.RUNNING) {
				        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				        return;
				    }
				    
				    callstartButton(newButton.getId(),"GO NOGO",newButton.getUserData().toString());
				    
				    if(newButton.getText().toLowerCase().contains("complete")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.COMPLETE_TEST);
				    }else if(newButton.getText().toLowerCase().contains("ofp")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.OFP_LOADING);
				    }else if(newButton.getText().toLowerCase().contains("pi")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PI_CHECK);
				    }
				    			    
			});
			goNoGoVBox.getChildren().add(newButton);
		}
		
			LRUTestStateObject.completeTestStatusProperty().addListener((observable, oldValue, newValue) -> {
				 if(!newValue) {
						Button ofpButton = (Button) goNoGoVBox.lookup("#" + goAndNoGoCardList.get(1).getCardId());
						if(checkGOandNOGOStatus("complete")) {
							ofpButton.setDisable(false);
						}
						startButton.setDisable(false);
						StateMachine.setTestState(TestState.COMPLETED);
						LRUTestStateObject.getCompleteTestStatus().set(true);
				 }
			});
		    LRUTestStateObject.ofpLoadingStatusProperty().addListener((observable, oldValue, newValue) -> {
				if(!newValue) {
			    	Button piCheckButton = (Button) goNoGoVBox.lookup("#" + goAndNoGoCardList.get(2).getCardId());
					if(checkGOandNOGOStatus("ofp")) {
						piCheckButton.setDisable(false);					
					}
					startButton.setDisable(false);
					StateMachine.setTestState(TestState.COMPLETED);	
					LRUTestStateObject.getOfpLoadingStatus().set(true);
				}
			});
		    LRUTestStateObject.piCheckStatusProperty().addListener((observable, oldValue, newValue) -> {
		    	if(!newValue) {
					StateMachine.setTestState(TestState.COMPLETED);
					LRUTestStateObject.getPiCheckStatus().set(true);
			        startButton.setDisable(false);
				}
				boolean allCardsStatusOk = true;
				
		        for (TestCardData card : goAndNoGoCardList) {
		            if (card.getStatus().equalsIgnoreCase("NOT OK")) {
		                if (!card.getCardName().trim().toLowerCase().contains("pbit")) {
		                    allCardsStatusOk = false;
		                    break;
		                }
		            }
		        }
		        
		        if(allCardsStatusOk) {
			        goLabel.setStyle("-fx-background-color:green;-fx-text-fill:white;");
			        
		        }else {
		        	noGoLabel.setStyle("-fx-background-color:red;-fx-text-fill:white;");
		        }

		    });
		
		return goNoGoVBox;
	}
	
	private boolean checkGOandNOGOStatus(String cardName) {
		
	    List<TestCardData> matchingCards = LRUTestStateObject.getLruGoAndNogoCardList().stream()
	    		 .filter(card -> card.getCardName().trim().toLowerCase().contains(cardName))
	            .collect(Collectors.toList());

	    for (TestCardData card : matchingCards) {
	        if (card.getStatus().equalsIgnoreCase("OK")) {
	            return true;
	        }
	    }
	    return false;
	}
	

	
	private HBox goNoGoLabel() {
		goLabel.getStyleClass().add("label-gonogo");
		goLabel.setPadding(new Insets(5, 0, 5, 0));
		goLabel.setPrefWidth(100);
		goLabel.setAlignment(Pos.CENTER);
		
		noGoLabel.getStyleClass().add("label-gonogo");
		noGoLabel.setPadding(new Insets(5, 0, 5, 0));
		noGoLabel.setPrefWidth(100);
		noGoLabel.setAlignment(Pos.CENTER);
		
		goNoGoLabel.setAlignment(Pos.CENTER);
		goNoGoLabel.getChildren().addAll(goLabel,noGoLabel);
		return goNoGoLabel;
	}

	private GridPane lruTestBottomContainer() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);
		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomGridPane.getColumnConstraints().addAll(firstColumn);
		bottomGridPane.getRowConstraints().addAll(firstRow);

		bottomGridPane.add(createTableView(), 0, 0);

		return bottomGridPane;

	}

	private TableView<LRUTestResult> createTableView() {
		TableView<LRUTestResult> tableView = new TableView<>();
		tableView.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/SelfTest.css").toExternalForm());
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<LRUTestResult, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setMaxWidth(825);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		// Custom cell to show ellipsis for file path
		fileNameColumn
				.setCellFactory(new Callback<TableColumn<LRUTestResult, String>, TableCell<LRUTestResult, String>>() {
					@Override
					public TableCell<LRUTestResult, String> call(TableColumn<LRUTestResult, String> col) {
						return new TableCell<LRUTestResult, String>() {
							@Override
							protected void updateItem(String filePath, boolean empty) {
								super.updateItem(filePath, empty);
								if (empty || filePath == null) {
									setText(null);
								} else {
									File file = new File(filePath);
			                    	setText(file.getName());
								}
							}

						};
					}
				});

		TableColumn<LRUTestResult, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setMaxWidth(300);
		resultColumn.setMinWidth(300);
		resultColumn.setStyle("-fx-alignment: CENTER;");
		rewriteColumn(resultColumn);

		LRUTestStateObject.getTestFilesResultList().addListener((ListChangeListener<? super LRUTestResult>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					int lastIndex = LRUTestStateObject.getTestFilesResultList().size() - 1;
					Platform.runLater(() -> {
						tableView.scrollTo(lastIndex);
						tableView.getSelectionModel().select(lastIndex);
						tableView.getFocusModel().focus(lastIndex);
					});
				}
			}
		});


		// View Button Column
		TableColumn<LRUTestResult, Void> viewButtonColumn = new TableColumn<>();
		viewButtonColumn.setCellFactory(col -> new TableCell<LRUTestResult, Void>() {
			private final Button viewButton = new Button("View");

			{
				viewButton.setOnAction(e -> {
					LRUTestResult lruTestResult = getTableView().getItems().get(getIndex());
				    File file = new File(lruTestResult.getFileName());

				    // Check if the file exists before trying to open it
				    if (file.exists()) {
				        try {
				            String os = System.getProperty("os.name").toLowerCase();
				            if (os.contains("win")) {
				                // Windows-specific code
				                Desktop desktop = Desktop.getDesktop();
				                if (desktop.isSupported(Desktop.Action.OPEN)) {
				                    desktop.open(file);
				                } else {
				                   Debug.printDebug("Open action not supported on this platform.");
				                }
				            } else if (os.contains("nix") || os.contains("nux")) {
				                // Linux-specific code using xdg-open
				                // Ensure the file path is absolute
				                File absoluteFile = file.isAbsolute() ? file : file.getAbsoluteFile();
				                new ProcessBuilder("xdg-open", absoluteFile.getAbsolutePath()).start();
				            } else {
				               Debug.printDebug("Unsupported OS: " + os);
				            }
				        } catch (IOException ex) {
				           Debug.printDebug("Error opening file: " + ex.getMessage());
				        }
				    } else {
				       Debug.printDebug("File does not exist: " + file.getAbsolutePath());
				    }
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(viewButton);
				}
			}
		});
		viewButtonColumn.setReorderable(false);
		viewButtonColumn.setSortable(false);
		viewButtonColumn.setMaxWidth(100);
		
		tableView.getColumns().addAll(fileNameColumn, resultColumn, viewButtonColumn);

		tableView.setItems(LRUTestStateObject.getTestFilesResultList());

		return tableView;
	}
	private void rewriteColumn(TableColumn<LRUTestResult, String> resultColumn) {
		resultColumn.setReorderable(false);
		resultColumn.setSortable(false);
		resultColumn.setCellFactory(column -> new TableCell<LRUTestResult, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					if ("OK".equalsIgnoreCase(item)) {
						setText("Passed");
						setStyle("-fx-background-color: green;-fx-alignment: CENTER;-fx-text-fill:white");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;-fx-text-fill:white");
					}else {
						setText(item);
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;-fx-text-fill:white");
					}
				}
			}
		});
	}
	
	
	private void callstartButton(String stageId, String stageName, String testTypeId) {
		Task<Response> task = new Task<Response>() {
	        @Override
	        protected Response call() throws Exception {
//	        	LRUTestStateObject.updateSelectedSubStagesList(stageId, "COMPLETED");
	        	 	String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
	        		currentSessionDetails.setRunConfigId(runConfigId);
	        		String ID = stageId;
	        		 TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
	        		 	if (testFileResponse.getTestFilesIdName() == null || testFileResponse.getTestFilesIdName().isEmpty() || testFileResponse.getTestFilesIdName().size() == 0) {
		                    Platform.runLater(() -> {
		                        Notifications.showWarningAlert("Please Add Test Files For This Stage... ");
		                    });
		                    startButton.setDisable(false);
		                    StateMachine.setTestState(TestState.COMPLETED);
		                    return null;
		                }
		                    		
		        		
		                Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
		                List<String> testFileList = new ArrayList<>(testFileMap.keySet());
		                
		                return testProcessManagement.testProcesControl(
		                    currentSessionDetails.getSessionId(),
		                    ID, 1, testFileList, true,stageName , testTypeId , ofpConfigId 
		                );                   			               
	        }
	    };
	    
	    task.setOnSucceeded(event -> {
	        Response response = task.getValue(); // Get the response
	        if (response.getResponseCode() == 0) {
	            Debug.printDebug(stageName+" Task Response received: " + response.getResponseMessage());
	            
	            StateMachine.setTestState(TestState.PENDING);
	            
	            if(stageName.equals("SRU")) {
	            	runAllButton.setDisable(false);
	            	startButton.setDisable(false);
	            	pauseButton.setDisable(true);
	            	stopButton.setDisable(true);
	            	sruTestCheckBoxList.setDisable(false);
	            }
	            
	            Notifications.showErrorAlert(response.getResponseMessage());
	        }
	    });

	    task.setOnFailed(event -> {
	        Throwable exception = task.getException();
	        Debug.printDebug(stageName+" Task failed with exception: " + exception.getMessage());
	    });
	    
	    new Thread(task).start();
	}
	
	private void getSRUSubStage(String stageId, String stageName) {
		if(stageId != null) {
			LRUTestStateObject.clearSelectedSubStagesList();
			LRUTestStateObject.clearSRUSubCardList();
			checkBoxes.clear();
			testListView.getItems().clear();
		}
		
		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(StateMachine.getStageDatalist());
		
		 
		observableStageList.stream()
		.filter(stage -> "LRU Test".equalsIgnoreCase(stage.getL1StageName()))
		.filter(stage -> stage.getL3StageId() != null)
        .sorted((stage1, stage2) -> {
             int id1 = Integer.parseInt(stage1.getL3StageId().split("_")[1]);
             int id2 = Integer.parseInt(stage2.getL3StageId().split("_")[1]);
             return Integer.compare(id1, id2);
         })
		.forEach(stage -> {
			TestCardData newCard = new TestCardData(stage.getL4StageId(), stage.getL4StageName(), stage.getTestTypeId(), null);

			if ("SRU Test".equalsIgnoreCase(stage.getL2StageName())) {
				if (stageId.equalsIgnoreCase(stage.getL3StageId())) {
					LRUTestStateObject.addSRUSubCardList(newCard);
				}
			} 
		});
		setListOfSubStage(LRUTestStateObject.getSRUSubCardList());
	}

	private void setListOfSubStage(ObservableList<TestCardData> subStageList) {
	    for (TestCardData stage : LRUTestStateObject.getSRUSubCardList()) {
	        CheckBox newCheckBox = new CheckBox(stage.getCardName());
	        newCheckBox.getStyleClass().add("lru-testing-checkbox-inside-box");
	        newCheckBox.setId(stage.getCardId());
	        newCheckBox.setUserData(stage);
	        
	        newCheckBox.getStyleClass().add("session-testing-checkbox");
	        newCheckBox.setWrapText(true);
	        checkBoxes.add(newCheckBox);
	        
	        Platform.runLater(() -> {
		    	testListView.setFixedCellSize(30);
		        testListView.getItems().add(newCheckBox);
		        testListView.requestLayout();
		    });
	        
	        testListView.setCellFactory(lv -> new ListCell<CheckBox>() {
	            @Override
	            protected void updateItem(CheckBox item, boolean empty) {
	                super.updateItem(item, empty);
	                if (empty || item == null) {
	                    setGraphic(null);
	                } else {
	                    setGraphic(item);
	                }
	            }
	        });

	        
	        newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
	        	if(newValue) {
	        		LRUTestStateObject.addSelectedSubStagesList(stage);
	        	}else {
					LRUTestStateObject.removeSelectedSubStagesList(stage);
				}
	        });
	    }
	}

}	
