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
import com.teclever.dfcc.datastore.filemanagement.FaultCodeConfiguration;
import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
import com.teclever.dfcc.model.LRUTest;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestResult;
import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestRunningCard;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.RunningTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.TestCardDataObject.TestCardData;
import com.teclever.dfcc.utils.CheckAitessStatus;
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
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class LRUTestingController {

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
	private HBox startTestHBox = new HBox();

	private VBox goNoGoVBox = new VBox(25);
	private Label goLabel= new Label("GO");
	private Label noGoLabel = new Label("NOGO");

	private Button startTest = new Button("Start Test");
	
	private GridPane sruTestCheckBoxList = new GridPane();
	private VBox selectAllVBox = new VBox();
	private CheckBox selectAllCheckBox = new CheckBox("Select All");
	private VBox selectedListVBox = new VBox(5);

	private GridPane bottomGridPane = new GridPane();
	
	private ObservableList<LRUTest> lruTestTableData = FXCollections.observableArrayList();
	
	private String selectedSRUCard;
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
	
	public String getRUN_CONFIG_ID() {
        return RUN_CONFIG_ID.get();
    }

    public void setRUN_CONFIG_ID(String rUN_CONFIG_ID) {
        RUN_CONFIG_ID.set(rUN_CONFIG_ID);
    }

    public StringProperty runConfigIdProperty() {
        return RUN_CONFIG_ID;
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
		secondRow.setPercentHeight(50);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(43);

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
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					String runConfigId = runConfigurationService
							.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
					currentSessionDetails.setRunConfigId(runConfigId);
					String ID = stageId;

					String testFile1 = "download_versions.com";
					String testFile2 = "PBIT_Test.com";

					List<String> testFileName = Arrays.asList(testFile1, testFile2);

					for (String testFile : testFileName) {
						TestFileResponse testFileResponse = testPlanFileManagement
								.getSelectedTestFilesFromStage(testFile);
						Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
						List<String> testFileList = new ArrayList<>(testFileMap.keySet());

						Response response = testProcessManagement.testProcesControl(
								currentSessionDetails.getSessionId(), ID, 1, testFileList, true, stageName, testTypeId,
								ofpConfigId);

						System.out.println("File Name of test file" + testFileList);
					}
				} catch (Exception e) {
					e.printStackTrace(); // Optionally handle/log the exception
				}

				return null;
			}
		};

		new Thread(task).start();
	}

	private void ofpUpWDMUp(String stageId, String stageName, String testTypeId) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					String runConfigId = runConfigurationService
							.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
					currentSessionDetails.setRunConfigId(runConfigId);
					String ID = stageId;

					String testFile2 = "PBIT_Test.com";

					List<String> testFileName = Arrays.asList(testFile2);

					for (String testFile : testFileName) {
						TestFileResponse testFileResponse = testPlanFileManagement
								.getSelectedTestFilesFromStage(testFile);
						Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
						List<String> testFileList = new ArrayList<>(testFileMap.keySet());

						Response response = testProcessManagement.testProcesControl(
								currentSessionDetails.getSessionId(), ID, 1, testFileList, true, stageName, testTypeId,
								ofpConfigId);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
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

	private void dialogBox() {
		String ofpConfig;
		OFPVersion.setPromptText("select OFP Version");
		OFPVersion.setVisible(false);

		Dialog<String> dialog = new Dialog<>();
		dialog.setWidth(500);
		dialog.setTitle("Check Status");

		Button okButton = new Button("OK");
		Button cancelButton = new Button("Cancel");

		okButton.setOnAction(event -> {
			dialog.setResult("Ok");

			dialog.close();
		});

		cancelButton.setOnAction(event -> {
			dialog.setResult("Cancel");
			dialog.close();
		});

		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(10);

		buttonBox.getChildren().addAll(okButton, cancelButton);

		RadioButton option1 = new RadioButton("Execute PBIT without loading OFP");
		RadioButton option2 = new RadioButton("Download OFP and Execute the PBIT");

		ToggleGroup group = new ToggleGroup();

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
			System.out.println("Dialog result: " + result);
		});
	}
	
	
	
	
	
	
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
			System.out.println();
			if(!firstButton) {
				newButton.setDisable(true);	
			}
			firstButton =false;

			newButton.setOnAction(e ->{
				if(!checkAitessStatus.isBothAitessOn()) {
					return ;
				}
				 TestState currentState = StateMachine.getTestState();            
				    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED || currentState ==  TestState.STOPPED) {
				    	startTest.setDisable(true);
				    	StateMachine.setTestState(TestState.RUNNING);
				    	StateMachine.setRunningTestName(RunningTestName.LRU_SRU_TEST);
				    	ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
					    ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on " + newButton.getText() + " button");
						appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				    } else if(currentState == TestState.RUNNING) {
				        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				        startTest.setDisable(false);
				        return;
				    }
//				    callStartTest(newButton.getId(),"MANDATORY",newButton.getUserData().toString());
				    

//				    if(newButton.getText().trim().equalsIgnoreCase("SPIL LINK")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.SPIL_LINK);
//				    }else if(newButton.getText().trim().equalsIgnoreCase("POWER SUPPLY")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.POWER_SUPPLY);
//				    }else if(newButton.getText().trim().equalsIgnoreCase("PBIT TEST")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
//				    }else if(newButton.getText().trim().equalsIgnoreCase("A/D-D/A INTERFACE TEST")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.AD_DA_INTERFACE);
//				    }else if(newButton.getText().trim().equalsIgnoreCase("INITIALIZE LRU")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.INITIALIZE_LRU);
//				    }
				    
				    
				    callStartTest(newButton.getId(),"MANDATORY",newButton.getUserData().toString());
				    if (newButton.getText().toLowerCase().contains("spil")) {

						LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.SPIL_LINK);
					} else if (newButton.getText().toLowerCase().contains("pbit")) {

						if (aitessProcessControlManagement.pbitCheck().getResponseCode() == 300) {

							String ofpConfig;
							OFPVersion.setPromptText("select OFP Version");
							OFPVersion.setVisible(false);

							Dialog<String> dialog = new Dialog<>();
							dialog.setWidth(500);
							dialog.setTitle("Check Status");

							Button okButton = new Button("OK");
							Button cancelButton = new Button("Cancel");

							okButton.setOnAction(event -> {
								dialog.setResult("Ok");
								LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
								dialog.close();
							});

							cancelButton.setOnAction(event -> {
								dialog.setResult("Cancel");
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
								System.out.println("Dialog result: " + result);
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
								LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
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
								LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
								ofpUpWDMUp(newButton.getId(), "MANDATORY", newButton.getUserData().toString());

							}
						}

						
					}else if(newButton.getText().toLowerCase().contains("initialize")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.INITIALIZE_LRU);
				    }else if(newButton.getText().toLowerCase().contains("power")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.POWER_SUPPLY);
				    }else if(newButton.getText().toLowerCase().contains("interface")) {
				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.AD_DA_INTERFACE);
				    }
				    
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
				System.out.println("insid-----");
				LRUTestStateObject.getAd_daInterfaceStatus().set(true);
			}
			boolean allCardsStatusOk = true;
		
	        for (TestCardData card : mandatoryCardList) {
	            if (card.getStatus().equalsIgnoreCase("NOT OK")) {
	                if (!card.getCardName().trim().toLowerCase().contains("pbit")) {
	                    allCardsStatusOk = false;
	                    break;
	                }
	            }
	        }
	        
	        System.out.println("allCardsStatusOk--------"+allCardsStatusOk);

	        StateMachine.setTestState(TestState.COMPLETED);
	        startTest.setDisable(false);
	        
	        if(allCardsStatusOk) {
	        	for(TestCardData card : LRUTestStateObject.getLruSruCardList()) {
		        	Button enableButton = (Button) sruCardVBox.lookup("#" + card.getCardId());
		        	enableButton.setDisable(false);
	        	}
//	        	for(TestCardData card : LRUTestStateObject.getLruGoAndNogoCardList()) {
//	        		if(card.getCardName().trim().equalsIgnoreCase("COMPLETE TEST")) {
//	        			Button enableButton = (Button) goNoGoVBox.lookup("#" + card.getCardId());
//			        	enableButton.setDisable(false);
//			        	break;
//	        		}
//	        	}
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
//	            .filter(card -> cardName.equalsIgnoreCase(card.getCardName().trim()))
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

		sruSubTestGridPane.setHgap(20);
		sruSubTestGridPane.setPadding(new Insets(5, 10, 10, 10));

		sruSubTestGridPane.getStyleClass().add("mid-Gridepane-content");
		sruSubTestGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		sruSubTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow, SixthRow);

		sruSubTestGridPane.add(sruTest(), 0, 0, 2, 1);
		sruSubTestGridPane.add(createSruTestVBox(), 0, 1, 1, 4);

//		sruSubTestGridPane.add(subTestVBox(), 1, 1, 1, 4);
		sruSubTestGridPane.add(subTestGridPane(), 1, 1, 1, 4);
		sruSubTestGridPane.add(startTestHBox(), 0, 5, 2, 1);

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
				getSRUSubStage(newButton.getId(), newButton.getText());
				selectedSRUCard = newButton.getText();
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			    ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on "+ newButton.getText());
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			});
			sruCardVBox.getChildren().add(newButton);
		}

		return sruCardVBox;
	}

	
	private GridPane subTestGridPane() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(12);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(88);
		
		sruTestCheckBoxList.getColumnConstraints().addAll(firstColumn);
		sruTestCheckBoxList.getRowConstraints().addAll(firstRow, secondRow);
		
		sruTestCheckBoxList.add(createSelectAllCheckbox(),0,0);
		sruTestCheckBoxList.add(createListOfSubStage1(),0,1);
		return sruTestCheckBoxList;
	}
	

    private List<CheckBox> checkBoxes = new ArrayList<>();
    private ListView<CheckBox> testListView = new ListView<>();
	

	private Node createListOfSubStage1() {
	   	testListView.getStyleClass().add("session-testing-list-view");
	    selectedListVBox.getChildren().add(testListView);
	    selectedListVBox.getStyleClass().add("session-testing-right-container");
	    return selectedListVBox;
	}

	private VBox createSelectAllCheckbox() {
		 selectAllCheckBox.getStyleClass().addAll("lru-testing-checkbox","lru-select-all-checkbox");
		 	   
		 selectAllCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
		   for (CheckBox checkBox : checkBoxes) {
		        checkBox.setSelected(newValue);
		     }
		  });
		selectAllVBox.getChildren().add(selectAllCheckBox);	
		return selectAllVBox;
	}
	

	private HBox startTestHBox() {
		
		startTest.setPrefWidth(200);
		startTest.setDisable(true);
		
		startTest.setOnAction(e ->{
			if(!checkAitessStatus.isBothAitessOn()) {
				return ;
			}
			TestState currentState = StateMachine.getTestState();            
		    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED) {
		    	startTest.setDisable(true);
		    	StateMachine.setTestState(TestState.RUNNING);
		    	StateMachine.setRunningTestName(RunningTestName.LRU_SRU_TEST);
		    	ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			    ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on SRU test START button");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
		    } else if(currentState == TestState.RUNNING) {
		        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
		        startTest.setDisable(false);
		        return;
		    }
		    sendSelectedSubStageData1();
		    startTest();
		});
		
		startTestHBox.setAlignment(Pos.CENTER);
		startTestHBox.getChildren().add(startTest);

		return startTestHBox;
	}



	private void startTest() {
//		LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(0).getCardId(), "COMPLETED");
		callStartTest(LRUTestStateObject.getSelectedSubStagesList().get(0).getCardId(), "SRU", LRUTestStateObject.getSelectedSubStagesList().get(0).getTestTypeId());
	}

	private void sendSelectedSubStageData() {
		for(TestCardData subStage : LRUTestStateObject.getSelectedSubStagesList()) {
//			System.err.println(subStage.getCardName());
			subStage.statusProperty().addListener((observable, oldValue, newValue) -> {
				if(newValue.equals("COMPLETED")) {
//					System.out.println(LRUTestStateObject.getSelectedSubStagesList().size());
//					System.out.println(currentIndex);
					if(LRUTestStateObject.getSelectedSubStagesList().size()-1 > currentIndex) {
						try {
							Thread.sleep(5000);
							currentIndex++;
//							System.out.println(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardName());
							LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardId(), "COMPLETED");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else if(LRUTestStateObject.getSelectedSubStagesList().size()-1 == currentIndex) {
//						System.out.println("COMPLETED");
						StateMachine.setTestState(TestState.COMPLETED);
						currentIndex = 0;
						startTest.setDisable(false);
					}
				}
			});
			
		}
	}
	
	private void sendSelectedSubStageData1() {
		for(TestCardData subStage : LRUTestStateObject.getSelectedSubStagesList()) {
			subStage.statusProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null && newValue.equals("COMPLETED")) {
//					System.out.println("LRUTestStateObject.getSelectedSubStagesList().size()    "+LRUTestStateObject.getSelectedSubStagesList().size());
//					System.out.println("currentIndex    "+currentIndex);
					if(LRUTestStateObject.getSelectedSubStagesList().size()-1 > currentIndex) {
						currentIndex++;
						callStartTest(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardId(), "SRU", LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getTestTypeId());
						LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex-1).getCardId(), null);
					}else if(LRUTestStateObject.getSelectedSubStagesList().size()-1 == currentIndex) {
//						System.out.println("COMPLETED");
						LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardId(), null);
						StateMachine.setTestState(TestState.COMPLETED);
						currentIndex = 0;
						startTest.setDisable(false);
					}

				}
			});
			
		}
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
				    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED) {
				    	startTest.setDisable(true);
				    	StateMachine.setTestState(TestState.RUNNING);
				    	StateMachine.setRunningTestName(RunningTestName.LRU_SRU_TEST);
				    	ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
					    ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),currentSessionDetails.getDfccSerialNumber(),currentSessionDetails.getSessionId(),StateMachine.getCurrentUserLogin(),new Date(),"clicked on " + newButton.getText() + " button");
						appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				    } else if(currentState == TestState.RUNNING) {
				        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
				        startTest.setDisable(false);
				        return;
				    }
//				    callStartTest(newButton.getId(),"GO NOGO",newButton.getUserData().toString());
				    
				    
//				    if(newButton.getText().equalsIgnoreCase("COMPLETE TEST")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.COMPLETE_TEST);
//				    }else if(newButton.getText().equalsIgnoreCase("OFP LOADING")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.OFP_LOADING);
//				    }else if(newButton.getText().equalsIgnoreCase("PI-CHECK")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PI_CHECK);
//				    }
				    
				    callStartTest(newButton.getId(),"GO NOGO",newButton.getUserData().toString());
				    
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
						startTest.setDisable(false);
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
					startTest.setDisable(false);
					StateMachine.setTestState(TestState.COMPLETED);	
					LRUTestStateObject.getOfpLoadingStatus().set(true);
				}
			});
		    LRUTestStateObject.piCheckStatusProperty().addListener((observable, oldValue, newValue) -> {
		    	if(!newValue) {
					StateMachine.setTestState(TestState.COMPLETED);
					LRUTestStateObject.getPiCheckStatus().set(true);
			        startTest.setDisable(false);
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
			        goLabel.setStyle("-fx-background-color:green;");	
		        }else {
		        	noGoLabel.setStyle("-fx-background-color:red;");
		        }

		    });
		
		return goNoGoVBox;
	}
	
	private boolean checkGOandNOGOStatus(String cardName) {
	    List<TestCardData> matchingCards = LRUTestStateObject.getLruGoAndNogoCardList().stream()
//	            .filter(card -> cardName.equalsIgnoreCase(card.getCardName().trim()))
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
									String fileName = filePath.substring(
											Math.max(filePath.lastIndexOf("/"), filePath.lastIndexOf("\\")) + 1); // Extract
																													// the
																													// file
																													// name
									double availableWidth = getTableColumn().getWidth();
									String displayText = getEllipsizedText(filePath, fileName, availableWidth);
									setText(displayText);
								}
							}

							private String getEllipsizedText(String filePath, String fileName, double columnWidth) {

								double padding = 15; // Adjust based on styling, padding, and alignment
								double approxCharWidth = 7; // Estimated average width of a character

								int totalAvailableChars = (int) ((columnWidth - padding) / approxCharWidth);
								System.out.println("totalAvailableChars" + totalAvailableChars);

								if (filePath.length() <= totalAvailableChars) {
									return filePath;
								}

								int fileNameLength = fileName.length();
								int availableForPath = totalAvailableChars - fileNameLength - 3;

								if (availableForPath > 0) {
									return "..." + filePath.substring(filePath.length() - availableForPath);
								}
								return fileName;
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

//		TableColumn<LRUTest, String> faultPinSuggestionColumn = new TableColumn<>("Fault Pin Suggestion");
//		faultPinSuggestionColumn.setCellValueFactory(new PropertyValueFactory<>("faultPinSuggestion"));
//		faultPinSuggestionColumn.setReorderable(false);
//		faultPinSuggestionColumn.setSortable(false);
//		faultPinSuggestionColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> interfaceSignalColumn = new TableColumn<>("Interface Signal");
//		interfaceSignalColumn.setCellValueFactory(new PropertyValueFactory<>("interfaceSignal"));
//		interfaceSignalColumn.setReorderable(false);
//		interfaceSignalColumn.setSortable(false);
//		interfaceSignalColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> channelColumn = new TableColumn<>("Channel");
//		channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
//		channelColumn.setReorderable(false);
//		channelColumn.setSortable(false);
//		channelColumn.setStyle("-fx-alignment: CENTER;");

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
				                    System.out.println("Open action not supported on this platform.");
				                }
				            } else if (os.contains("nix") || os.contains("nux")) {
				                // Linux-specific code using xdg-open
				                // Ensure the file path is absolute
				                File absoluteFile = file.isAbsolute() ? file : file.getAbsoluteFile();
				                new ProcessBuilder("xdg-open", absoluteFile.getAbsolutePath()).start();
				            } else {
				                System.out.println("Unsupported OS: " + os);
				            }
				        } catch (IOException ex) {
				            System.out.println("Error opening file: " + ex.getMessage());
				        }
				    } else {
				        System.out.println("File does not exist: " + file.getAbsolutePath());
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
						setStyle("-fx-background-color: green;-fx-alignment: CENTER;");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;");
					}else {
						setText(item);
						setStyle("-fx-background-color: red;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}
	
	private void callStartTest(String stageId, String stageName, String testTypeId) {
		Task<Void> task = new Task<Void>() {
	        @Override
	        protected Void call() throws Exception {
	        	 	String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
	        		currentSessionDetails.setRunConfigId(runConfigId);
	        		String ID = stageId;
	        		 TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
	        		 	if (testFileResponse.getTestFilesIdName() == null || testFileResponse.getTestFilesIdName().isEmpty() || testFileResponse.getTestFilesIdName().size() == 0) {
		                    Platform.runLater(() -> {
		                        Notifications.showWarningAlert("Please Add Test Files For This Stage... ");
		                    });
		                    startTest.setDisable(false);
		                    StateMachine.setTestState(TestState.COMPLETED);
		                    return null;
		                }
		                    		
		        		
		                Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
		                List<String> testFileList = new ArrayList<>(testFileMap.keySet());
		                
		                Response response = testProcessManagement.testProcesControl(
		                    currentSessionDetails.getSessionId(),
		                    ID, 1, testFileList, true,stageName , testTypeId , ofpConfigId 
		                );                   			               
	          
	            return null;
	        }
	    };
	    
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
		selectAllCheckBox.setSelected(false);
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
	        testListView.getItems().add(newCheckBox);
	        
	        newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
	        	if(newValue) {
	        		LRUTestStateObject.addSelectedSubStagesList(stage);
	        	}else {
					LRUTestStateObject.removeSelectedSubStagesList(stage);
					selectAllCheckBox.setIndeterminate(true);
				}
	        });
	        
	        
//	        newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//	        	boolean anySelected = false;
//	        	for(CheckBox checkBox : checkBoxes) {
//	        		if(checkBox.isSelected()) {
//	        			anySelected = true;
//	        		}
//	        	}
//	        });
	    }
	}
		
}







//package com.teclever.dfcc.Controller.ui;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import com.teclever.datastore.dto.Response;
//import com.teclever.datastore.service.RunConfigurationService;
//import com.teclever.dfcc.DFCCConstant;
//import com.teclever.dfcc.datastore.dto.StageObject;
//import com.teclever.dfcc.datastore.dto.TestFileResponse;
//import com.teclever.dfcc.datastore.filemanagement.TestPlanFileManagement;
//import com.teclever.dfcc.datastore.testmanagement.TestProcessManagement;
//import com.teclever.dfcc.model.LRUTest;
//import com.teclever.dfcc.stateMachine.LRUTestStateObject;
//import com.teclever.dfcc.stateMachine.LRUTestStateObject.LRUTestRunningCard;
//import com.teclever.dfcc.stateMachine.SelfTestStateObject.TestCardData;
//import com.teclever.dfcc.stateMachine.StateMachine;
//import com.teclever.dfcc.stateMachine.StateMachine.TestState;
//import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
//import com.teclever.dfcc.utils.Notifications;
//
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.concurrent.Task;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//import javafx.scene.layout.VBox;
//
//public class LRUTestingController {
//
//	private GridPane lruTestMainContainerGridPane = new GridPane();
//
//	private GridPane headingGridPane = new GridPane();
//
//	private HBox headingHbox = new HBox();
//
//	private GridPane lrumidContainerGridPane = new GridPane();
//	private HBox midTopHbox1 = new HBox();
//	private Label lruLeftLabel = new Label("SRU Identification and Isolation");
//	private HBox midTopHbox2 = new HBox();
//	private Label lruLeftLabe2 = new Label("GO and NOGO TEST");
//
//	private Label mandatoryTest = new Label("Mandatory Test");
//
//
//	private GridPane sruSubTestGridPane = new GridPane();
//	private Label sruTest = new Label("SRU Test");
//	private VBox sruCardVBox = new VBox(10);
//
//	private GridPane goNOGOGridPane = new GridPane();
//
//	private GridPane subTestGridPane = new GridPane();
//
//	private HBox goNoGoLabel = new HBox();
//	private HBox startTestHBox = new HBox();
//
//	private VBox goNoGoVBox = new VBox(25);
//	private Label goNoGo = new Label("GO/NOGO");
//
//	private Button startTest = new Button("Start Test");
//	
//	private GridPane sruTestCheckBoxList = new GridPane();
//	private VBox selectAllVBox = new VBox();
//	private CheckBox selectAllCheckBox = new CheckBox("Select All");
//	private VBox selectedListVBox = new VBox(5);
//
//	private GridPane bottomGridPane = new GridPane();
//	
//	private ObservableList<LRUTest> lruTestTableData = FXCollections.observableArrayList();
//	
//	private String selectedSRUCard;
//	private int currentIndex = 0;
//
//	TestPlanFileManagement testPlanFileManagement = new TestPlanFileManagement();
//	TestProcessManagement testProcessManagement = new TestProcessManagement();
//	RunConfigurationService runConfigurationService = new RunConfigurationService();
//
//	public GridPane createlruTestMainContainerGridPane() {
//
//		lruTestMainContainerGridPane.getStylesheets().add(getClass()
//				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LRUTest.css").toExternalForm());
//		lruTestMainContainerGridPane.getStyleClass().add("lruTest-main-container");
//
//		getLruCradData();
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(50);
//
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(43);
//
//		lruTestMainContainerGridPane.setVgap(5);
//
//		lruTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
//		lruTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
//		lruTestMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
//
//		lruTestMainContainerGridPane.add(lruheadingGridPane(), 0, 0);
//		lruTestMainContainerGridPane.add(lruTestMidContainer(), 0, 1);
//		lruTestMainContainerGridPane.add(lruTestBottomContainer(), 0, 2);
//
//		return lruTestMainContainerGridPane;
//
//	}
//
//	private void getLruCradData() {
//		List<StageObject> stageList = StateMachine.getStageDatalist();
//		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(stageList);
//
//		observableStageList.stream()
//				.filter(stage -> "LRU Test".equalsIgnoreCase(stage.getL1StageName()))
//				.filter(stage -> stage.getL3StageId() != null)
//		        .sorted((stage1, stage2) -> {
//		             int id1 = Integer.parseInt(stage1.getL3StageId().split("_")[1]);
//		             int id2 = Integer.parseInt(stage2.getL3StageId().split("_")[1]);
//		             return Integer.compare(id1, id2);
//		         })
//				.forEach(stage -> {
//					TestCardData newCard = new TestCardData(stage.getL3StageId(), stage.getL3StageName(), stage.getTestTypeId(), null);
//
//					if ("Mandatory Test".equalsIgnoreCase(stage.getL2StageName())) {
//						if (LRUTestStateObject.getLruMandatoryCardList().stream()
//								.noneMatch(card -> card.getCardId().equals(newCard.getCardId()))) {
//							LRUTestStateObject.addLruMandatoryCard(newCard);
//						}
//					} else if ("SRU Test".equalsIgnoreCase(stage.getL2StageName())) {
//						if (LRUTestStateObject.getLruSruCardList().stream()
//								.noneMatch(card -> card.getCardId().equals(newCard.getCardId()))) {
//							LRUTestStateObject.addLruSruCard(newCard);
//						}
//					} else if ("GO & NOGO Test".equalsIgnoreCase(stage.getL2StageName())) {
//						if (LRUTestStateObject.getLruGoAndNogoCardList().stream()
//								.noneMatch(card -> card.getCardId().equals(newCard.getCardId()))) {
//							LRUTestStateObject.addLruGoAndNogoCard(newCard);
//						}
//					}
//
//				});
//
//	}
//
//	public GridPane lruheadingGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		headingGridPane.getStyleClass().add("lruTest-top-container");
//		headingGridPane.getColumnConstraints().addAll(firstColumn);
//		headingGridPane.getRowConstraints().addAll(firstRow);
//
//		headingGridPane.add(headingHbox(), 0, 0);
//
//		return headingGridPane;
//
//	}
//
//	private HBox headingHbox() {
//		Label pageHeading = new Label("LRU TEST");
//		pageHeading.getStyleClass().add("lrutest-top-header");
//		headingHbox.setAlignment(Pos.CENTER_LEFT);
//		headingHbox.getChildren().add(pageHeading);
//
//		return headingHbox;
//	}
//
//	private GridPane lruTestMidContainer() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(20);
//
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(20);
//
//		ColumnConstraints thirdColumn = new ColumnConstraints();
//		thirdColumn.setPercentWidth(40);
//
//		ColumnConstraints fourthColumn = new ColumnConstraints();
//		fourthColumn.setPercentWidth(20);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(90);
//
//		lrumidContainerGridPane.setHgap(5);
//		lrumidContainerGridPane.setVgap(5);
//
//		lrumidContainerGridPane.getStyleClass().add("lruTest-mid-container");
//		lrumidContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
//		lrumidContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		lrumidContainerGridPane.add(midTopHbox1(), 0, 0, 3, 1);
//		lrumidContainerGridPane.add(midTopHbox2(), 3, 0);
//		lrumidContainerGridPane.add(sruIsolationGridPane(), 0, 1);
//		lrumidContainerGridPane.add(sruSubTestGridPane(), 1, 1, 2, 1);
//		lrumidContainerGridPane.add(goNOGOGridPane(), 3, 1);
//
//		return lrumidContainerGridPane;
//
//	}
//
//	private HBox midTopHbox1() {
//		lruLeftLabel.getStyleClass().add("midheader-label");
//		midTopHbox1.getStyleClass().add("midheader-hbox");
//		midTopHbox1.setAlignment(Pos.CENTER);
//		midTopHbox1.getChildren().add(lruLeftLabel);
//		return midTopHbox1;
//	}
//
//	private HBox midTopHbox2() {
//		lruLeftLabe2.getStyleClass().add("midheader-label");
//		midTopHbox2.getStyleClass().add("midheader-hbox");
//		midTopHbox2.setAlignment(Pos.CENTER);
//		midTopHbox2.getChildren().add(lruLeftLabe2);
//		return midTopHbox2;
//	}
//
//	private GridPane sruIsolationGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(16);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(21);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(21);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(21);
//		RowConstraints fivthRow = new RowConstraints();
//		fivthRow.setPercentHeight(21);
//
//		subTestGridPane.setPadding(new Insets(5, 10, 10, 10));
//
//		subTestGridPane.getStyleClass().add("mid-Gridepane-content");
//		subTestGridPane.getColumnConstraints().addAll(firstColumn);
//		subTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow);
//
//		subTestGridPane.add(mandatoryTest(), 0, 0);
//
//		return subTestGridPane;
//
//	}
//
//	private Label mandatoryTest() {
//		mandatoryTest.getStyleClass().add("midlabel-content");
//		mandatoryTest.setMaxWidth(Double.MAX_VALUE);
//		mandatoryTest.setAlignment(Pos.CENTER);
//
//		
//		subTestGridPane.add(createLruTestCardButton(), 0, 1, 1, 5);
//
//		return mandatoryTest;
//
//	}
//
//	private VBox mandatoryTestVBox = new VBox(10);
//	
//	private VBox createLruTestCardButton() {
//		ObservableList<TestCardData> mandatoryCardList = LRUTestStateObject.getLruMandatoryCardList();
//		boolean firstButton = true;
//		for (TestCardData card : mandatoryCardList) {
//			Button newButton = new Button();
//			newButton.setText(card.getCardName());
//			newButton.setId(card.getCardId());
//			newButton.setUserData(card.getTestTypeId());
//			newButton.setMaxWidth(Double.MAX_VALUE);
//			newButton.setAlignment(Pos.CENTER);
//			newButton.setWrapText(true);
//			if(!firstButton) {
//				newButton.setDisable(true);	
//			}
//			firstButton =false;
//
//			newButton.setOnAction(e ->{
//				 TestState currentState = StateMachine.getTestState();            
//				    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED) {
//				    	startTest.setDisable(true);
//				    	StateMachine.setTestState(TestState.RUNNING);
//				    } else if(currentState == TestState.RUNNING) {
//				        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
//				        startTest.setDisable(false);
//				        return;
//				    }
//				    callStartTest(newButton.getId(),"LRU",newButton.getUserData().toString());
//				    
//				   
//				    if(newButton.getText().equalsIgnoreCase("SPIL LINK")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.SPIL_LINK);
//				    }else if(newButton.getText().equalsIgnoreCase("POWER_SUPPLY")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.POWER_SUPPLY);
//				    }else if(newButton.getText().equalsIgnoreCase("PBIT")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PBIT);
//				    }else if(newButton.getText().equalsIgnoreCase("AD_DA_INTERFACE")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.AD_DA_INTERFACE);
//				    }else if(newButton.getText().equalsIgnoreCase("INITIALIZE_LRU")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.INITIALIZE_LRU);
//				    }
//				    
//			});
//					
//	
//			mandatoryTestVBox.getChildren().add(newButton);
//		}
//		
//		LRUTestStateObject.spilLinkStatusProperty().addListener((observable, oldValue, newValue) -> {
//			Button pbitButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(1).getCardId());
//			pbitButton.setDisable(false);
//		});
//		LRUTestStateObject.pbitStatusProperty().addListener((observable, oldValue, newValue) -> {
//			System.out.println(LRUTestStateObject.getIsMandatoryFifthCardStatus().get());
//			if(LRUTestStateObject.getIsMandatoryFifthCardStatus().get()) {
//				Button initializeLRUButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(2).getCardId());
//				initializeLRUButton.setDisable(false);
//			}else {
//				Button powerSupplyButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(2).getCardId());
//				powerSupplyButton.setDisable(false);
//			}
//		});
//		LRUTestStateObject.initializeLRUStatusProperty().addListener((observable, oldValue, newValue) -> {
//				Button powerSupplyButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(3).getCardId());
//				powerSupplyButton.setDisable(false);
//		});
//		LRUTestStateObject.powerSupplyStatusProperty().addListener((observable, oldValue, newValue) -> {
//			int index = LRUTestStateObject.getIsMandatoryFifthCardStatus().get() ? 4 : 3;
//			Button ad_daInterfaceButton = (Button) mandatoryTestVBox.lookup("#" + mandatoryCardList.get(index).getCardId());
//			ad_daInterfaceButton.setDisable(false);
//		});
//		LRUTestStateObject.ad_daInterfaceStatusProperty().addListener((observable, oldValue, newValue) -> {
//			boolean allCardsStatusOk = true;
//			
//	        for (TestCardData card : mandatoryCardList) {
//	            if (card.getStatus().equalsIgnoreCase("NOT OK")) {
//	                if (!card.getCardName().equalsIgnoreCase("PBIT TEST")) {
//	                    allCardsStatusOk = false;
//	                    break;
//	                }
//	            }
//	        }
//	        System.err.println("allCardsStatusOk-----"+allCardsStatusOk);
//		});
//
//		return mandatoryTestVBox;
//	}
//
//
//	private GridPane sruSubTestGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(18);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(18);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(18);
//		RowConstraints fivthRow = new RowConstraints();
//		fivthRow.setPercentHeight(18);
//		RowConstraints SixthRow = new RowConstraints();
//		SixthRow.setPercentHeight(18);
//
//		sruSubTestGridPane.setHgap(20);
//		sruSubTestGridPane.setPadding(new Insets(5, 20, 10, 20));
//
//		sruSubTestGridPane.getStyleClass().add("mid-Gridepane-content");
//		sruSubTestGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		sruSubTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow, SixthRow);
//
//		sruSubTestGridPane.add(sruTest(), 0, 0, 2, 1);
//		sruSubTestGridPane.add(createSruTestVBox(), 0, 1, 1, 4);
//
////		sruSubTestGridPane.add(subTestVBox(), 1, 1, 1, 4);
//		sruSubTestGridPane.add(subTestGridPane(), 1, 1, 1, 4);
//		sruSubTestGridPane.add(startTestHBox(), 0, 5, 2, 1);
//
//		return sruSubTestGridPane;
//
//	}
//
//	private Label sruTest() {
//		sruTest.getStyleClass().add("midlabel-content");
//		sruTest.setMaxWidth(Double.MAX_VALUE);
//		sruTest.setAlignment(Pos.CENTER);
//		return sruTest;
//	}
//
//
//	private VBox createSruTestVBox() {
//		
//		ObservableList<TestCardData> sruCardList = LRUTestStateObject.getLruSruCardList();
//
//		for (TestCardData card : sruCardList) {
//			Button newButton = new Button();
//			newButton.setText(card.getCardName());
//			newButton.setId(card.getCardId());
//			newButton.setUserData(card.getTestTypeId());
//			newButton.setMaxWidth(Double.MAX_VALUE);
//			newButton.setAlignment(Pos.CENTER);
//			newButton.setWrapText(true);
////			newButton.setDisable(true);
//			
//			newButton.setOnAction(e ->{
//				getSRUSubStage(newButton.getId(), newButton.getText());
//				selectedSRUCard = newButton.getText();
//			});
//			sruCardVBox.getChildren().add(newButton);
//		}
//
//		return sruCardVBox;
//	}
//
//	
//	private GridPane subTestGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(90);
//		
//		sruTestCheckBoxList.getColumnConstraints().addAll(firstColumn);
//		sruTestCheckBoxList.getRowConstraints().addAll(firstRow, secondRow);
//		
//		sruTestCheckBoxList.add(createSelectAllCheckbox(),0,0);
//		sruTestCheckBoxList.add(createListOfSubStage(),0,1);
//		return sruTestCheckBoxList;
//	}
//	
//
//
//	
//
//	private VBox createSelectAllCheckbox() {
//		selectAllVBox.getChildren().add(selectAllCheckBox);
//		
//		selectAllCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->{
//			if(newValue) {
//				selectAllSubStageCheckBox();
//			}else {
//				removeAllSubStageCheckBox();
//			}
//		});
//		
//		return selectAllVBox;
//	}
//	
//
//	private VBox createListOfSubStage() {
//		  selectedListVBox.getStyleClass().add("stage-list-box");
//		    
//		    ScrollPane scrollPane = new ScrollPane();
//		    scrollPane.setContent(selectedListVBox);
//		    scrollPane.setFitToWidth(true); 
//		    scrollPane.setFitToHeight(true); 
//		    
//		    scrollPane.setPrefHeight(400);
//
//		    VBox wrapperVBox = new VBox(scrollPane);
//		    return wrapperVBox;
//	}
//
//
//	private HBox startTestHBox() {
//		
//		startTest.setPrefWidth(200);
//		
//		startTest.setOnAction(e ->{
//			TestState currentState = StateMachine.getTestState();            
//		    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED) {
//		    	startTest.setDisable(true);
//		    	StateMachine.setTestState(TestState.RUNNING);
//		    	System.out.println("TEST STARTING....");
//		    } else if(currentState == TestState.RUNNING) {
//		        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
//		        startTest.setDisable(false);
//		        return;
//		    }
//		    sendSelectedSubStageData();
//		    startTest();
//		});
//		startTestHBox.setAlignment(Pos.CENTER);
//		startTestHBox.getChildren().add(startTest);
//
//		return startTestHBox;
//	}
//
//
//
//	private void startTest() {
//		LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(0).getCardId(), "COMPLETED");
//	}
//
//	private void sendSelectedSubStageData() {
//		for(TestCardData subStage : LRUTestStateObject.getSelectedSubStagesList()) {
//			subStage.statusProperty().addListener((observable, oldValue, newValue) -> {
//				if(newValue.equals("COMPLETED")) {
//					System.out.println(LRUTestStateObject.getSelectedSubStagesList().size());
//					System.out.println(currentIndex);
//					if(LRUTestStateObject.getSelectedSubStagesList().size()-1 > currentIndex) {
//						try {
//							Thread.sleep(5000);
//							currentIndex++;
//							System.out.println(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardName());
//							LRUTestStateObject.updateSelectedSubStagesList(LRUTestStateObject.getSelectedSubStagesList().get(currentIndex).getCardId(), "COMPLETED");
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}else if(LRUTestStateObject.getSelectedSubStagesList().size()-1 == currentIndex) {
//						System.out.println("COMPLETED");
//						StateMachine.setTestState(TestState.COMPLETED);
//						currentIndex = 0;
//						startTest.setDisable(false);
//					}
//				}
//			});
//			
//		}
//	}
//
//	private GridPane goNOGOGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(18);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(18);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(18);
//		RowConstraints fivthRow = new RowConstraints();
//		fivthRow.setPercentHeight(18);
//		RowConstraints SixthRow = new RowConstraints();
//		SixthRow.setPercentHeight(18);
//
//		goNOGOGridPane.setPadding(new Insets(5, 10, 10, 10));
//		goNOGOGridPane.getStyleClass().add("mid-Gridepane-content");
//		goNOGOGridPane.getColumnConstraints().addAll(firstColumn);
//		goNOGOGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow, SixthRow);
//		goNOGOGridPane.add(goNoGoVBox(), 0, 1, 1, 4);
//		goNOGOGridPane.add(goNoGoLabel(), 0, 5);
//
//		return goNOGOGridPane;
//	}
//
//	private VBox goNoGoVBox() {		
//		ObservableList<TestCardData> goAndNoGoCardList = LRUTestStateObject.getLruGoAndNogoCardList();
//
//		for (TestCardData card : goAndNoGoCardList) {
//			Button newButton = new Button();
//			newButton.setText(card.getCardName());
//			newButton.setId(card.getCardId());
//			newButton.setUserData(card.getTestTypeId());
//			newButton.setMaxWidth(Double.MAX_VALUE);
//			newButton.setAlignment(Pos.CENTER);
//			newButton.setWrapText(true);
//			newButton.setDisable(true);
//			
//			newButton.setOnAction(e ->{
//				 TestState currentState = StateMachine.getTestState();            
//				    if (currentState == TestState.PENDING || currentState == TestState.COMPLETED) {
//				    	startTest.setDisable(true);
//				    	StateMachine.setTestState(TestState.RUNNING);
//				    } else if(currentState == TestState.RUNNING) {
//				        Notifications.showWarningAlert(StateMachine.getRunningTestName() + " Test is Already Running...");
//				        startTest.setDisable(false);
//				        return;
//				    }
//				    callStartTest(newButton.getId(),"GO NOGO",newButton.getUserData().toString());
//				    
//				    
//				    if(newButton.getText().equalsIgnoreCase("COMPLETE_TEST")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.COMPLETE_TEST);
//				    }else if(newButton.getText().equalsIgnoreCase("OFP_LOADING")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.OFP_LOADING);
//				    }else if(newButton.getText().equalsIgnoreCase("PI_CHECK")) {
//				    	LRUTestStateObject.setLRUTestRunningCard(LRUTestRunningCard.PI_CHECK);
//				    }
//				    
//			});
//			
//			goNoGoVBox.getChildren().add(newButton);
//		}
//		
//		return goNoGoVBox;
//	}
//
//	
//	private HBox goNoGoLabel() {
//		goNoGo.getStyleClass().add("label-gonogo");
//		goNoGo.setPadding(new Insets(5, 0, 5, 0));
//		goNoGo.setPrefWidth(200);
//		goNoGo.setAlignment(Pos.CENTER);
//		goNoGoLabel.setAlignment(Pos.CENTER);
//		goNoGoLabel.getChildren().add(goNoGo);
//		return goNoGoLabel;
//	}
//
//	private GridPane lruTestBottomContainer() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		bottomGridPane.getColumnConstraints().addAll(firstColumn);
//		bottomGridPane.getRowConstraints().addAll(firstRow);
//
//		bottomGridPane.add(createTableView(), 0, 0);
//
//		return bottomGridPane;
//
//	}
//
//	private TableView<LRUTest> createTableView() {
//		TableView<LRUTest> tableView = new TableView<>();
//		tableView.getStylesheets().add(getClass()
//				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());
//		tableView.getStyleClass().add("check-sum-table");
//		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//		TableColumn<LRUTest, String> fileNameColumn = new TableColumn<>("File Name");
//		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
//		fileNameColumn.setReorderable(false);
//		fileNameColumn.setSortable(false);
//		fileNameColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> resultColumn = new TableColumn<>("Result");
//		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
//		resultColumn.setReorderable(false);
//		resultColumn.setSortable(false);
//		resultColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> faultPinSuggestionColumn = new TableColumn<>("Fault Pin Suggestion");
//		faultPinSuggestionColumn.setCellValueFactory(new PropertyValueFactory<>("faultPinSuggestion"));
//		faultPinSuggestionColumn.setReorderable(false);
//		faultPinSuggestionColumn.setSortable(false);
//		faultPinSuggestionColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> interfaceSignalColumn = new TableColumn<>("Interface Signal");
//		interfaceSignalColumn.setCellValueFactory(new PropertyValueFactory<>("interfaceSignal"));
//		interfaceSignalColumn.setReorderable(false);
//		interfaceSignalColumn.setSortable(false);
//		interfaceSignalColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> channelColumn = new TableColumn<>("Channel");
//		channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
//		channelColumn.setReorderable(false);
//		channelColumn.setSortable(false);
//		channelColumn.setStyle("-fx-alignment: CENTER;");
//
//		tableView.getColumns().addAll(fileNameColumn, resultColumn, faultPinSuggestionColumn, interfaceSignalColumn,
//				channelColumn);
//
//		tableView.setItems(lruTestTableData);
//
//		return tableView;
//	}
//	
//	private void callStartTest(String stageId, String stageName, String testTypeId) {
//		Task<Void> task = new Task<Void>() {
//	        @Override
//	        protected Void call() throws Exception {
//	        	 	String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(currentSessionDetails.getUutId(), testTypeId);
//	        		currentSessionDetails.setRunConfigId(runConfigId);
//	        		String ID = stageId;
//	        		 TestFileResponse testFileResponse = testPlanFileManagement.getSelectedTestFilesFromStage(ID);
//		                if (testFileResponse.getTestFilesIdName() == null) {
//		                    Platform.runLater(() -> {
//		                        Notifications.showWarningAlert("Please Add Test Files For This Stage... ");
//		                    });
//		                    return null;
//		                }
//		                    		
//		        		
//		                Map<String, String> testFileMap = testFileResponse.getTestFilesIdName();
//		                List<String> testFileList = new ArrayList<>(testFileMap.keySet());
//		                
//		                Response response = testProcessManagement.testProcesControl(
//		                    currentSessionDetails.getSessionId(),
//		                    ID, 1, testFileList, true,stageName
//		                );                   			               
//	          
//	            return null;
//	        }
//	    };
//	    
//	    new Thread(task).start();
//	}
//	
//	private void getSRUSubStage(String stageId, String stageName) {
//		if(stageId != null) {
//			LRUTestStateObject.clearSRUSubCardList();
//		}
//		
//		ObservableList<StageObject> observableStageList = FXCollections.observableArrayList(StateMachine.getStageDatalist());
//		
//		 
//		observableStageList.stream()
//		.filter(stage -> "LRU Test".equalsIgnoreCase(stage.getL1StageName()))
//		.filter(stage -> stage.getL3StageId() != null)
//        .sorted((stage1, stage2) -> {
//             int id1 = Integer.parseInt(stage1.getL3StageId().split("_")[1]);
//             int id2 = Integer.parseInt(stage2.getL3StageId().split("_")[1]);
//             return Integer.compare(id1, id2);
//         })
//		.forEach(stage -> {
//			TestCardData newCard = new TestCardData(stage.getL4StageId(), stage.getL4StageName(), stage.getTestTypeId(), null);
//
//			if ("SRU Test".equalsIgnoreCase(stage.getL2StageName())) {
//				if (stageId.equalsIgnoreCase(stage.getL3StageId())) {
//					LRUTestStateObject.addSRUSubCardList(newCard);
//				}
//			} 
//		});
//		selectAllCheckBox.setSelected(false);
//		setListOfSubStage(LRUTestStateObject.getSRUSubCardList());
//	}
//
//	private void setListOfSubStage(ObservableList<TestCardData> subStageList) {
//		selectedListVBox.getChildren().clear();
//		subStageList.stream()
//			.forEach(subStage ->{
//				CheckBox subStageCheckBox = new CheckBox(subStage.getCardName());
//				subStageCheckBox.setId(subStage.getCardId());
//				subStageCheckBox.setUserData(subStage);
//											
//				 subStageCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//			            if (newValue) {
//			            	subStage.setStatus("PENDING");
//			            	LRUTestStateObject.addSelectedSubStagesList(subStage);
//			            } else {
//			            	LRUTestStateObject.removeSelectedSubStagesList(subStage);
//			            }
//			        });
//				
//				selectedListVBox.getChildren().add(subStageCheckBox);
//			});
//	}
//
//	private void selectAllSubStageCheckBox() {
//		selectedListVBox.getChildren().forEach(node -> {
//	        if (node instanceof CheckBox) {
//	            CheckBox checkBox = (CheckBox) node;
//	            checkBox.setSelected(true);
//	        }
//	    });
//	}
//	
//	private void removeAllSubStageCheckBox() {
//		LRUTestStateObject.clearSelectedSubStagesList();
//		selectedListVBox.getChildren().forEach(node -> {
//	        if (node instanceof CheckBox) {
//	            CheckBox checkBox = (CheckBox) node;
//	            checkBox.setSelected(false);
//	        }
//	    });
//	}		
//}




















//package com.teclever.dfcc.Controller.ui;
//
//import com.teclever.dfcc.DFCCConstant;
//import com.teclever.dfcc.model.LRUTest;
//import com.teclever.dfcc.stateMachine.StateMachine;
//import com.teclever.dfcc.stateMachine.StateMachine.TestState;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.RowConstraints;
//import javafx.scene.layout.VBox;
//
//public class LRUTestingController {
//
//	private GridPane lruTestMainContainerGridPane = new GridPane();
//
//	private GridPane lruContainerGridPane = new GridPane();
//
//	private GridPane headingGridPane = new GridPane();
//
//	private HBox headingHbox = new HBox();
//
//	private GridPane lrumidContainerGridPane = new GridPane();
//	private HBox midTopHbox1 = new HBox();
//	private Label lruLeftLabel = new Label("SRU Identification and Isolation");
//	private HBox midTopHbox2 = new HBox();
//	private Label lruLeftLabe2 = new Label("GO and NOGO TEST");
//	private HBox midTopHbox3 = new HBox();
//	private Label lruLeftLabe3 = new Label("Sub Test / Interface:");
//	
//	private GridPane sruIsolationGridPane = new GridPane();
//	
//	private Label mandatoryTest = new Label("Mandatory Test");
//	private Button spilLink = new Button("Spil Link");
//	private Button pBit = new Button("PBIT");
//	private Button powerSupply = new Button("Power Supply");
//	private Button adInterface = new Button("A/D-D/A Interface");
//
//	private GridPane sruSubTestGridPane = new GridPane();
//	private Label sruTest = new Label("SRU Test");
//	private Button digital = new Button("Digital");
//	private Button analog1R = new Button("Analog-1 Right");
//	private Button analog1L = new Button("Analog-1 Left");
//	private Button analog2 = new Button("Analog 2");
//
//	private GridPane goNOGOGridPane = new GridPane();
//
//	private GridPane subTestGridPane = new GridPane();
//	private CheckBox subTest = new CheckBox("Select All Sub-Test:");
//	private TextArea subTestTextArea = new TextArea();
//
//	private HBox hboxLabel = new HBox();
//	private HBox goNoGoLabel = new HBox();
//	private HBox nogoButton = new HBox();
//	private HBox startTestHBox = new HBox();
//	
//	private HBox sruTestHBox = new HBox();
//	private HBox digitalHBox = new HBox();
//	private HBox analog1RHBox = new HBox();
//	private HBox analog1LHBox = new HBox();
//	private HBox analog2HBox = new HBox();
//	
//	private VBox subTestVBox= new VBox();
//	private VBox goNoGoVBox= new VBox(20);
//	
//	private HBox textAreaHBox = new HBox();
//
//	private GridPane bottomGridPane = new GridPane();
//	private ObservableList<LRUTest> lruTestTableData = FXCollections.observableArrayList();
//
//	public GridPane createlruTestMainContainerGridPane() {
//
//		lruTestMainContainerGridPane.getStylesheets()
//				.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LRUTest.css").toExternalForm());
//		lruTestMainContainerGridPane.getStyleClass().add("lruTest-main-container");
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//		
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(7);
//		
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(50);
//		
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(43);
//		
//		lruTestMainContainerGridPane.setVgap(5);
//		
//		lruTestMainContainerGridPane.getColumnConstraints().addAll(firstColumn);
//		lruTestMainContainerGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
//		lruTestMainContainerGridPane.setPadding(new Insets(5, 5, 5, 5));
//
//		lruTestMainContainerGridPane.add(lruheadingGridPane(), 0, 0);
//		lruTestMainContainerGridPane.add(lruTestMidContainer(), 0, 1);
//		lruTestMainContainerGridPane.add(lruTestBottomContainer(), 0, 2);
//		
//		
//		return lruTestMainContainerGridPane;
//
//	}
//	
//
//	public GridPane lruheadingGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		headingGridPane.getStyleClass().add("lruTest-top-container");
//		headingGridPane.getColumnConstraints().addAll(firstColumn);
//		headingGridPane.getRowConstraints().addAll(firstRow);
//
//		headingGridPane.add(headingHbox(), 0, 0);
//
//		return headingGridPane;
//
//	}
//	private HBox headingHbox() {
//		Label pageHeading = new Label("LRU TEST");
//		pageHeading.getStyleClass().add("lrutest-top-header");
//		headingHbox.setAlignment(Pos.CENTER_LEFT);
//		headingHbox.getChildren().add(pageHeading);
//
//		return headingHbox;
//	}
//	
//	private GridPane lruTestMidContainer() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(20);
//
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(20);
//
//		ColumnConstraints thirdColumn = new ColumnConstraints();
//		thirdColumn.setPercentWidth(40);
//
//		ColumnConstraints fourthColumn = new ColumnConstraints();
//		fourthColumn.setPercentWidth(20);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(90);
//
//		lrumidContainerGridPane.setHgap(5);
//		lrumidContainerGridPane.setVgap(5);
////		lrumidContainerGridPane.setPadding(new Insets(20, 20, 20, 20));
//
//		lrumidContainerGridPane.getStyleClass().add("lruTest-mid-container");
//		lrumidContainerGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
//		lrumidContainerGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		lrumidContainerGridPane.add(midTopHbox1(), 0, 0,3,1);
//		lrumidContainerGridPane.add(midTopHbox2(), 3, 0); 
//		lrumidContainerGridPane.add(sruIsolationGridPane(), 0, 1);
//		lrumidContainerGridPane.add(sruSubTestGridPane(),1, 1,2,1);
//		lrumidContainerGridPane.add(goNOGOGridPane(), 3, 1);
//	
////		lrumidContainerGridPane.add(sruSubTestGridPane(), 3, 1);
////		lrumidContainerGridPane.add(hboxButton(), 0, 2, 3, 1);
////		lrumidContainerGridPane.add(startTestHBox(), 3,2);
////		
//		
//
//		return lrumidContainerGridPane;
//
//	}
//	
//	private HBox midTopHbox1() {
//		lruLeftLabel.getStyleClass().add("midheader-label");
//		midTopHbox1.getStyleClass().add("midheader-hbox");
//		midTopHbox1.setAlignment(Pos.CENTER);
//		midTopHbox1.getChildren().add(lruLeftLabel);
//		return midTopHbox1;
//	}
//	private HBox midTopHbox2() {
//		lruLeftLabe2.getStyleClass().add("midheader-label");
//		midTopHbox2.getStyleClass().add("midheader-hbox");
//		midTopHbox2.setAlignment(Pos.CENTER);
//		midTopHbox2.getChildren().add(lruLeftLabe2);
//		return midTopHbox2;
//	}
//
////	private HBox midTopHbox3() {
////		lruLeftLabe3.getStyleClass().add("midheader-label");
////		midTopHbox3.getStyleClass().add("midheader-hbox");
////		midTopHbox3.setAlignment(Pos.CENTER);
////		midTopHbox3.getChildren().add(lruLeftLabe3);
////		return midTopHbox3;
////	}
//	
//	private GridPane sruIsolationGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(16);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(21);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(21);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(21);
//		RowConstraints fivthRow = new RowConstraints();
//		fivthRow.setPercentHeight(21);
//		
////		subTestGridPane.setHgap(20);
//		subTestGridPane.setPadding(new Insets(5, 20, 10, 20));
//
//		subTestGridPane.getStyleClass().add("mid-Gridepane-content");
//		subTestGridPane.getColumnConstraints().addAll(firstColumn);
//		subTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow);
//		
//		subTestGridPane.add(mandatoryTest(), 0, 0);
//		subTestGridPane.add(spilLink(), 0, 1);
//		subTestGridPane.add(pBit(), 0, 2);
//		subTestGridPane.add(powerSupply(), 0, 3);
//		subTestGridPane.add(adInterface(), 0, 4);
//		
//		return subTestGridPane;
//
//	}
//	
//	private Label mandatoryTest() {
//		mandatoryTest.getStyleClass().add("midlabel-content");
//		mandatoryTest.setMaxWidth(Double.MAX_VALUE);
//		mandatoryTest.setAlignment(Pos.CENTER);
//		return mandatoryTest;
//
//	}
//
//	private Button spilLink() {
////	spilLink.getStyleClass().add("test-Button");
//		spilLink.setMaxWidth(Double.MAX_VALUE);
////	spilLink.setPrefHeight(35);
//		spilLink.setAlignment(Pos.CENTER);
//		return spilLink;
//
//	}
//
//	private Button pBit() {
////	pBit.getStyleClass().add("test-Button");
//		pBit.setMaxWidth(Double.MAX_VALUE);
////	pBit.setPrefHeight(35);
//		pBit.setAlignment(Pos.CENTER);
//		return pBit;
//
//	}
//
//	private Button powerSupply() {
////	powerSupply.getStyleClass().add("test-Button");
//		powerSupply.setMaxWidth(Double.MAX_VALUE);
////	powerSupply.setPrefHeight(35);
//		powerSupply.setAlignment(Pos.CENTER);
//		return powerSupply;
//	}
//
//	private Button adInterface() {
////	adInterface.getStyleClass().add("test-Button");
//		adInterface.setMaxWidth(Double.MAX_VALUE);
////	adInterface.setPrefHeight(35);
//		adInterface.setAlignment(Pos.CENTER);
//		return adInterface;
//	}
//
//	
//	private GridPane sruSubTestGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(50);
//		ColumnConstraints secondColumn = new ColumnConstraints();
//		secondColumn.setPercentWidth(50);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(18);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(18);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(18);
//		RowConstraints fivthRow = new RowConstraints();
//		fivthRow.setPercentHeight(18);
//		RowConstraints SixthRow = new RowConstraints();
//		SixthRow.setPercentHeight(18);
//		
//		sruSubTestGridPane.setHgap(20);
//		sruSubTestGridPane.setPadding(new Insets(5, 20, 10, 20));
//
//		sruSubTestGridPane.getStyleClass().add("mid-Gridepane-content");
//		sruSubTestGridPane.getColumnConstraints().addAll(firstColumn,secondColumn);
//		sruSubTestGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow,SixthRow);
//		
//		sruSubTestGridPane.add(sruTest(), 0, 0,2,1);
//		sruSubTestGridPane.add(digital(), 0, 1);
//		sruSubTestGridPane.add(analog1R(), 0, 2);
//		sruSubTestGridPane.add(analog1L(), 0, 3);
//		sruSubTestGridPane.add(analog2(), 0, 4);
//		
//		sruSubTestGridPane.add(subTestVBox(), 1, 1,1,4);
//	    sruSubTestGridPane.add(startTestHBox(), 0, 5, 2, 1);
//	  
//		return sruSubTestGridPane;
//
//	}
//	
//	private Label sruTest() {
//		sruTest.getStyleClass().add("midlabel-content");
//		sruTest.setMaxWidth(Double.MAX_VALUE);
//		sruTest.setAlignment(Pos.CENTER);
//		return sruTest;
//	}
//
//	private Button digital() {
////	digital.getStyleClass().add("test-Button");
//		digital.setMaxWidth(Double.MAX_VALUE);
////	digital.setPrefHeight(35);
//		digital.setAlignment(Pos.CENTER);
//		return digital;
//	}
//
//	private Button analog1R() {
////	analog1R.getStyleClass().add("test-Button");
//		analog1R.setMaxWidth(Double.MAX_VALUE);
////	analog1R.setPrefHeight(35);
//		analog1R.setAlignment(Pos.CENTER);
//		return analog1R;
//	}
//
//	private Button analog1L() {
////	analog1L.getStyleClass().add("test-Button");
//		analog1L.setMaxWidth(Double.MAX_VALUE);
////	analog1L.setPrefHeight(35);
//		analog1L.setAlignment(Pos.CENTER);
//		return analog1L;
//	}
//
//	private Button analog2() {
////	analog2.getStyleClass().add("test-Button");
//		analog2.setMaxWidth(Double.MAX_VALUE);
////	analog2.setPrefHeight(35);
//		analog2.setAlignment(Pos.CENTER);
//		return analog2;
//	}
//	
//private VBox subTestVBox() {
//	
//	subTestVBox.getChildren().addAll(subTest(),textAreaHBox());
//	
//	return subTestVBox;
//}
//
//	private CheckBox subTest() {
//
//		subTest.getStyleClass().add("checkBox-midcontainer");
//		subTest.setPadding(new Insets(5, 0, 0, 0));
//		subTest.setMaxWidth(Double.MAX_VALUE);
//		subTest.setPrefHeight(35);
//		subTest.setAlignment(Pos.CENTER_LEFT);
//		return subTest;
//
//	}
//	
//	private HBox textAreaHBox() {
//		
//		textAreaHBox.setAlignment(Pos.CENTER);
//		textAreaHBox.setPadding(new Insets(17, 0, 0, 0));
////		textAreaHBox.getStyleClass().add("lruTest-top-container");
//		subTestTextArea.setMaxWidth(Double.MAX_VALUE);
//		textAreaHBox.getChildren().add(subTestTextArea());
//		return textAreaHBox;
//	}
//
//	private TextArea subTestTextArea() {
//
//		subTestTextArea.getStyleClass().add("textarea-midcontainer");
////		subTestTextArea.setPrefHeight(100);
////		subTestTextArea.setMaxWidth(Double.MAX_VALUE);
////		subTestTextArea.setAlignment(Pos.CENTER);
////		subTestTextArea.setPadding(new Insets(20, 20, 20, 20));
//		return subTestTextArea;
//	}
//
//
//	private HBox startTestHBox() {
//		Button startTest = new Button("Start Test");
//		TestState currentState = StateMachine.getTestState();            
//	    if (currentState == TestState.RUNNING) {
//	    	startTest.setDisable(true);
//	    }
//		startTest.setPrefWidth(200);
////		startTestHBox.getStyleClass().add("midheader-hbox");
////		startTestHBox.setPadding(new Insets(5,0,5,0));
//		startTestHBox.setAlignment(Pos.CENTER);
//		startTestHBox.getChildren().add(startTest);
//
//		return startTestHBox;
//	}
//
//	private GridPane goNOGOGridPane() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(10);
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(18);
//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(18);
//		RowConstraints fourthRow = new RowConstraints();
//		fourthRow.setPercentHeight(18);
//		RowConstraints fivthRow = new RowConstraints();
//		fivthRow.setPercentHeight(18);
//		RowConstraints SixthRow = new RowConstraints();
//		SixthRow.setPercentHeight(18);
//
//		goNOGOGridPane.setPadding(new Insets(5, 20, 10, 20));
//		goNOGOGridPane.getStyleClass().add("mid-Gridepane-content");
//		goNOGOGridPane.getColumnConstraints().addAll(firstColumn);
//		goNOGOGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow, SixthRow);
////		goNOGOGridPane.add(completeTest(), 0, 1);
////		goNOGOGridPane.add(piCheck(), 0, 2);
////		goNOGOGridPane.add(ofpLoading(), 0, 3);
//		goNOGOGridPane.add(goNoGoVBox(), 0, 1, 1, 4);
//		goNOGOGridPane.add(goNoGoLabel(), 0, 5);
//		
//
//		return goNOGOGridPane;
//	}
//	
//	private VBox goNoGoVBox() {
////		goNoGoVBox.setPadding(new Insets(5));
//		goNoGoVBox.getChildren().addAll( completeTest() ,piCheck(), ofpLoading());
//		goNoGoVBox.setAlignment(Pos.CENTER);
//		return goNoGoVBox;
//	}
//
//	private Button completeTest() {
//		Button completeTest = new Button("Complete Test");
//		completeTest.getStyleClass().add("test-Button");
//		completeTest.setMaxWidth(Double.MAX_VALUE);
//		return completeTest;
//	}
//
//	private Button piCheck() {
//		Button piCheck = new Button("PI Check");
//		piCheck.getStyleClass().add("test-Button");
//		piCheck.setMaxWidth(Double.MAX_VALUE);
//		return piCheck;
//	}
//
//	private Button ofpLoading() {
//		Button ofpLoading = new Button("OFP Loading");
//		ofpLoading.getStyleClass().add("test-Button");
//		ofpLoading.setMaxWidth(Double.MAX_VALUE);
//		return ofpLoading;
//	}
//
//	
//	private HBox goNoGoLabel() {
//		Label goNoGo = new Label("GO/NOGO");
//		goNoGo.getStyleClass().add("label-gonogo");
//		goNoGo.setPadding(new Insets(5, 0, 5, 0));
//		goNoGo.setPrefWidth(200);
//		goNoGo.setAlignment(Pos.CENTER);
//		goNoGoLabel.setAlignment(Pos.CENTER);
//		goNoGoLabel.getChildren().add(goNoGo);
//		return goNoGoLabel;
//	}
//	
//	private GridPane lruTestBottomContainer() {
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(100);
//
//		bottomGridPane.getColumnConstraints().addAll(firstColumn);
//		bottomGridPane.getRowConstraints().addAll(firstRow);
//		
//		bottomGridPane.add(createTableView(), 0, 0);
//
//		return bottomGridPane;
//
//	}
//	
//
//	private TableView<LRUTest> createTableView() {
//		TableView<LRUTest> tableView = new TableView<>();
//		tableView.getStylesheets()
//		.add(getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());
//		tableView.getStyleClass().add("check-sum-table");
//		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
////		tableView.setPrefWidth(1250);
////		tableView.setPrefHeight(900);
//
//		TableColumn<LRUTest, String> fileNameColumn = new TableColumn<>("File Name");
//		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
//		fileNameColumn.setReorderable(false);
//		fileNameColumn.setSortable(false);
//		fileNameColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> resultColumn = new TableColumn<>("Result");
//		resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
//		resultColumn.setReorderable(false);
//		resultColumn.setSortable(false);
//		resultColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> faultPinSuggestionColumn = new TableColumn<>("Fault Pin Suggestion");
//		faultPinSuggestionColumn.setCellValueFactory(new PropertyValueFactory<>("faultPinSuggestion"));
//		faultPinSuggestionColumn.setReorderable(false);
//		faultPinSuggestionColumn.setSortable(false);
//		faultPinSuggestionColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> interfaceSignalColumn = new TableColumn<>("Interface Signal");
//		interfaceSignalColumn.setCellValueFactory(new PropertyValueFactory<>("interfaceSignal"));
//		interfaceSignalColumn.setReorderable(false);
//		interfaceSignalColumn.setSortable(false);
//		interfaceSignalColumn.setStyle("-fx-alignment: CENTER;");
//
//		TableColumn<LRUTest, String> channelColumn = new TableColumn<>("Channel");
//		channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
//		channelColumn.setReorderable(false);
//		channelColumn.setSortable(false);
//		channelColumn.setStyle("-fx-alignment: CENTER;");
//
//		tableView.getColumns().addAll(fileNameColumn, resultColumn, faultPinSuggestionColumn, interfaceSignalColumn,
//				channelColumn);
//
//		tableView.setItems(lruTestTableData);
//
//		return tableView;
//	}
//
//
//
//
//	
//	
//	}
