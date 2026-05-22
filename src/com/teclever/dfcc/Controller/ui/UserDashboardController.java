package com.teclever.dfcc.Controller.ui;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.teclever.datastore.service.SessionTimingService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.MacroConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.dto.LogOutFileCopyResponse;
import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;
import com.teclever.dfcc.datastore.dto.SessionDTOResponse;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;
import com.teclever.dfcc.datastore.dto.ValidateResponse;
import com.teclever.dfcc.datastore.filemanagement.Aitess2ConfigManagement;
import com.teclever.dfcc.datastore.filemanagement.ChecksumManagement;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.logbookmanagement.UUTLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.model.CheckSumList;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.StatusBarTestName;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.TestStateNew;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTempAEC;
import com.teclever.dfcc.stateMachine.StateMachine.channelAECTemp;
import com.teclever.dfcc.stateMachine.StateMachine.channelSCTemp;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;
import com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus;
import com.teclever.dfcc.utils.CheckAitessStatus;
import com.teclever.dfcc.utils.Notifications;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class UserDashboardController {

	Boolean checkSumFinalResult = true;

	protected static final TestState RUNNING = null;
	private static final TestState PENDING = null;

	private static final Background Blue = null;
	private GridPane bottomMainGridPane = new GridPane();
	private GridPane bottomGridPane = new GridPane();
	private GridPane bottomMidTopGridPane = new GridPane();

	private GridPane lastUpdateTime = new GridPane();

	private ObservableMap<String, ChannelTemperature> scBoardTemperatureMap;
	private ObservableMap<String, ChannelTemperature> aecBoardTemperatureMap;

	private ObservableMap<String, channelSCTemp> scBoardTemperatureMapMk1;
	private ObservableMap<String, channelSCTemp> aecBoardTemperatureMapMk1;

	public static StringProperty lastupdatedTime2 = new SimpleStringProperty();

	private ComboBox<String> temperatureComboBox = new ComboBox<String>();

	private List<MacroButtonMapDto> macroButtonList;
	ChecksumManagement checksumManagement = new ChecksumManagement();

	UserCenterContentController centerContentController = UserCenterContentController.getInstance();
	SessionManagement sessionManagement = new SessionManagement();
	MacroConfigurationManagement macroConfigurationManagement = new MacroConfigurationManagement();
	AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
	Aitess2ConfigManagement aitess2ConfigManagement = new Aitess2ConfigManagement();
	CheckAitessStatus checkAitessStatus = new CheckAitessStatus();
	private final SystemConfigManagement systemConfigManagement = new SystemConfigManagement();

	private GridPane terminalGridPane = new GridPane();
	private TextField terminaTextField = new TextField();
	private Button enterButton = new Button("ENTER");
	private Button yesButton = new Button("YES");
	private Button noButton = new Button("NO");
	private StackPane terminalStackPane;
	private Rectangle background = new Rectangle(80, 30, Color.RED);
	private Circle toggleButton = new Circle(12, Color.WHITE);
	private Label toggleLabel = new Label("OFF");
	private StackPane stack = new StackPane();
	private HBox toggleSwitch = new HBox();
	TerminalController terminalController1 = new TerminalController();
	private Button showTerminalButton = new Button("SHOW TERMINAL");
	private ImageView minImageView;

	private MapChangeListener<String, ChannelTemperature> scListener;
	private MapChangeListener<String, ChannelTemperature> aecListener;

	private MapChangeListener<String, channelSCTemp> scListenerMk1;
	private MapChangeListener<String, channelAECTemp> aecListenerMk1;

	private Label statusBar = new Label("");

	private VBox statusBarVbox = new VBox();

	private Label serialNo1 = new Label();
	private Label sessionType1 = new Label();
	private Label startTime1 = new Label();
	private Label sessionName1 = new Label();
	
	private Label label1 = new Label("N/A");
	private Label label2 = new Label("N/A");
	private Label label3 = new Label("N/A");
	private Label label4 = new Label("N/A");
	
	double channel1;
	double channel2;
	double channel3;
	double channel4 ;
	
	private BLSTempData bLSTempData = new BLSTempData();
	private BlsTemperatureController blsTemperatureController = new BlsTemperatureController();

	private final BooleanProperty startTimeAutoUpdateFlag = new SimpleBooleanProperty(false);

	String commonStyle = "-fx-font-size: 15px; -fx-background-radius: 5px;"
			+ "-fx-border-radius: 5px; -fx-font-weight: bold; -fx-text-fill: black;";

	public GridPane rightMidSecondGridPane = new GridPane();

	public UserDashboardController() {
		
		terminalStackPane = terminalController1.createTerminalStackPane();
//		CheckToggleStatus();

		Platform.runLater(() -> {
//		if(StateMachine.isMacroPassing() == false) {
//			rightMidSecondGridPane.setDisable(false);
//		}

			initialize();
		});

	}

	public void initialize() {
//		Suji Added For Toggle,Macro,and Status Bar updating::(11-08-2025)
//		////System.out.println("currentSessionDetails.getSessionId()" + currentSessionDetails.getSessionId());
		

		
//		StateMachine.testStateProperty().addListener((obs, oldState, newState) -> {
//
//		    Platform.runLater(() -> {
//
//		        if (newState == TestState.STOPPED 
//		                && StateMachine.getTestStateNew() == TestStateNew.STOPPING) {
//
//		            statusBar.textProperty().unbind();
//		            StateMachine.setDissableEnable(false);
//
//		            statusBar.setText("Ready");
//		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");
//		            StateMachine.setTestStateNew(TestStateNew.NONE);
//		            //System.out.println("Suji Stopping in User Dashboard");
//
//
//		        } else if (newState == TestState.STOPPED) {
//
//		            statusBar.textProperty().unbind();
//		            StateMachine.setDissableEnable(false);
//
//		            statusBar.setText("Stopping test execution.");
//		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #e5350e");
//
//
//		        } else if (newState == TestState.PAUSED 
//		                && StateMachine.getTestStateNew() == TestStateNew.PAUSING) {
//
//		            statusBar.textProperty().unbind();
//		            statusBar.setText("Test is pause.");
//		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #66b7ed");
//		            StateMachine.setTestStateNew(TestStateNew.NONE);
//		            //System.out.println("Suji pausing in User Dashboard");
//
//
//		        } else if (newState == TestState.PAUSED) {
//
//		            statusBar.textProperty().unbind();
//		            statusBar.setText("Test execution is under pause.");
//		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #66b7ed");
//
//		        }
//
//		    });
//
//		});
		
		StateMachine.testStatusStateProperty().addListener((obs, oldState, newState) -> {

		    //System.out.println("Listener triggered: " + newState);

		    

		        statusBar.textProperty().unbind();

		        if (newState == TestStateNew.PAUSING) {

		            
		            Platform.runLater(() -> {
		            DFCCConstant.testPauseStopping = true;
				    StateMachine.setTestStatusState(TestStateNew.NONE);
		            statusBar.setText("Test is paused.");
		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #66b7ed");

		           
		            //System.out.println("Check Pausing user dashboard  entered::" + StateMachine.getTestStatusState());
		            });
		        }
		    
		});
		
		
		StateMachine.testStateProperty().addListener((obs, oldState, newState) -> {

		    Platform.runLater(() -> {

		        statusBar.textProperty().unbind(); // unbind once at start

		        // 1️⃣ STOPPED while STOPPING (TestStateNew flag)
		        if (newState == TestState.STOPPED && StateMachine.getTestStateNew() == TestStateNew.STOPPING) {
		            StateMachine.setDissableEnable(false);
		            statusBar.setText("Ready");
		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");
		            StateMachine.setTestStateNew(TestStateNew.NONE);
		            Platform.runLater(() -> {
			            toggleButton.setDisable(false);
						rightMidSecondGridPane.setDisable(false);
						DFCCConstant.pauseStopping = true;
			            });
//		            //System.out.println("Suji Stopping in User Dashboard (STOPPING)");

		        // 2️⃣ Transition from PAUSED → STOPPED
		        } else if (oldState == TestState.PAUSED && newState == TestState.STOPPED) {
		        	 StateMachine.setDissableEnable(false);
			            statusBar.setText("Ready");
			            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");
			            StateMachine.setTestStateNew(TestStateNew.NONE);
			            Platform.runLater(() -> {
				            toggleButton.setDisable(false);
							rightMidSecondGridPane.setDisable(false);
							DFCCConstant.pauseStopping = true;
				            });
//		            //System.out.println("Suji Pause -> Stop in User Dashboard");

		        // 3️⃣ Transition from RUNNING → STOPPED (normal stop)
		        } else if (oldState == TestState.RUNNING && newState == TestState.STOPPED) {
		            StateMachine.setDissableEnable(false);
		           
		            statusBar.setText("Stopping test execution.");
		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #e5350e");
		          
//		            //System.out.println("Suji Running -> Stop in User Dashboard");

		        // 4️⃣ PAUSED while PAUSING (TestStateNew flag)
		        } 
//		        else if (newState == TestState.PAUSED && StateMachine.getTestStateNew() == TestStateNew.PAUSING) {
//		            statusBar.setText("Test is paused.");
//		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #66b7ed");
//		            StateMachine.setTestStateNew(TestStateNew.NONE);
//		            DFCCConstant.testPauseStopping = true;
////		            //System.out.println("Suji pausing in User Dashboard (PAUSING)");
//
//		        // 5️⃣ Transition to PAUSED from RUNNING (normal pause)
//		        } 
		        else if (oldState == TestState.RUNNING && newState == TestState.PAUSED) {
		            statusBar.setText("Pausing test execution.");
		            statusBarVbox.setStyle(commonStyle + "-fx-background-color: #66b7ed");
//		            //System.out.println("Suji Running -> Pause in User Dashboard");
		        }
		    });

		});
		
		
		
		StateMachine.macroCommandProperty().addListener((obs, wasSet, isNowSet) -> {

	        if (isNowSet) {
	            Notifications.showSuccessAlert("Macro Executed..\n"+ DFCCConstant.macroCommandName);
	        }

	        toggleButton.setDisable(false);
	        rightMidSecondGridPane.setDisable(false);

	        Platform.runLater(() -> {
	        StateMachine.setMacroCommand(false);
	        });
	    });

		

		StateMachine.yesEntredProperty().addListener((obs, oldState, newState) -> {

			if (StateMachine.getTestState().equals(TestState.RUNNING)|| StateMachine.getTestState().equals(TestState.PAUSED)) {
//				////System.out.println("B IN USR DASH " + StateMachine.isDissableEnable());
				StateMachine.setDissableEnable(false);
//				////System.out.println("A IN USR DASH " + StateMachine.isDissableEnable());
				StateMachine.setYesEntred(false);
				
			}
			
		});

		
		StateMachine.checkAitesSwitchProperty().addListener((obs, oldVal, newVal) ->{
			if(newVal) {
			toggleButton.setDisable(true);
			rightMidSecondGridPane.setDisable(true);
			}
		});
		
		StateMachine.confirmTestFileCompletedProperty().addListener((obs, oldVal, newVal) -> {
			startTimeAutoUpdateFlag.set(true);
			////System.out.println("Start Time Flag:: SUJI CUSTOM" + startTimeAutoUpdateFlag);
			applyUiState(StateMachine.getTestState(), newVal);

		});
		
		StateMachine.cancelTestProperty().addListener((obs, oldVal, newVal) -> {
//			  //System.out.println("SUJI Cancel test for All channels entred Befroe " + newVal);
			  if (newVal) {
				  Platform.runLater(() -> {
//					  //System.out.println("SUJI Cancel test for All channels entred After" +newVal);
						statusBar.setText("Ready");
						statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");
						toggleButton.setDisable(false);
						rightMidSecondGridPane.setDisable(false);
						StateMachine.setConfirmTestFileCompleted(false);
						
						});
			  }
			  StateMachine.setCancelTest(false);
		});

//		Exit:::
		updateUI();
		Platform.runLater(() -> {
			SessionManagement sessionManagment = new SessionManagement();
			SessionDTOResponse response = sessionManagment.getSessionDetailById(currentSessionDetails.getSessionId());
//			////System.out.println("Start Time" + response.getStartDateTime());
			if (response.getStartDateTime() != null) {
				startTime1.setText(response.getStartDateTime());
			} else {
				startTime1.setText("Test not Started");
			}

			startTime1.setWrapText(true);
		});

//		For updateing Session details Start Time After Test::
		startTimeAutoUpdateFlag.addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				Platform.runLater(() -> {
					SessionManagement sessionManagment = new SessionManagement();
					SessionDTOResponse response = sessionManagment
							.getSessionDetailById(currentSessionDetails.getSessionId());
//					////System.out.println("Start Time" + response.getStartDateTime());
					if (response.getStartDateTime() != null) {
//						startTime1.setText(response.getStartDateTime());
						startTime1.textProperty().bind(response.startDateTimeProperty());
//						////System.out.println("Star Time check1" + response.startDateTimeProperty());
//						////System.out.println("Star Time check2" + response.getStartDateTime());
					} else {
						startTime1.setText("Test not Started");
					}

					startTime1.setWrapText(true);
					startTimeAutoUpdateFlag.set(false);
				});
			}
		});

//		StateMachine.testStateProperty().addListener((obs, oldState, newState) -> {
//			
//			updateUI(newState);
//		});

		StateMachine.aitess1LaunchedProperty().addListener((obs, oldVal, newVal) -> {
//			////System.out.println(" User Dashborad AITESS1 changed to: " + newVal);
			aitess1Updated = true;
			checkBothAitessLaunched();
		});

		StateMachine.aitess2LaunchedProperty().addListener((obs, oldVal, newVal) -> {
//			////System.out.println("User Dashborad List AITESS2 changed to: " + newVal);
			aitess2Updated = true;
			checkBothAitessLaunched();
		});

	}

	private void updateUI() {
		Platform.runLater(() -> {
			// Updated By ON BEL 31-07-2025
//			Suji Added: isConfirmTestFileCompleted for checking the test gets completed:(08-08-2025)

			if (StateMachine.isMacroPassing()) {
				toggleButton.setDisable(true);
			} else {
				toggleButton.setDisable(false);
			}
		});
	}

//	Suji Added For Toggle,Macro,and Status Bar updating::(11-08-2025)
	private void applyUiState(TestState currentState, boolean isTestFileCompleted) {
		Platform.runLater(() -> {
			
			
			
			if (!StateMachine.getStatusBarRunningTestName().equals(StatusBarTestName.ADVANCED_TEST_CUSTOM1_TEST.toString()) && isTestFileCompleted || currentState == TestState.RUNNING || currentState == TestState.PAUSED || !StateMachine.isConfirmTestStop()) {
				if(!DFCCConstant.pauseStopping) {
				toggleButton.setDisable(true);
				rightMidSecondGridPane.setDisable(true);
				StateMachine.setDissableEnable(false);
				}
				DFCCConstant.pauseStopping = false;
				////System.out.println("CHeck TEst State 1" +StateMachine.getTestState() );
			}else if(StateMachine.getStatusBarRunningTestName().equals(StatusBarTestName.ADVANCED_TEST_CUSTOM1_TEST.toString())&& StateMachine.getTestState().equals(TestState.COMPLETED)){
				statusBar.textProperty().unbind();
//				////System.out.println("Suji Entred User Dash Ready.....");
				//System.out.println("CHeck TEst State 1bbbbb" +StateMachine.getTestState() );
				Platform.runLater(() -> {
				statusBar.setText("Ready");
				statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");
				toggleButton.setDisable(false);
				rightMidSecondGridPane.setDisable(false);
				StateMachine.setConfirmTestFileCompleted(false);
				
				//System.out.println("CHeck TEst State 1AAAAAA" +StateMachine.getTestState() );
				});}
				else if(StateMachine.getStatusBarRunningTestName().equals(StatusBarTestName.ADVANCED_TEST_CUSTOM2_TEST.toString())&& StateMachine.getTestState().equals(TestState.COMPLETED)){
					statusBar.textProperty().unbind();
//					////System.out.println("Suji Entred User Dash Ready.....");
//					////System.out.println("CHeck TEst State 1bbbbb" +StateMachine.getTestState() );
					Platform.runLater(() -> {
					statusBar.setText("Ready");
					statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");
					toggleButton.setDisable(false);
					rightMidSecondGridPane.setDisable(false);
					StateMachine.setConfirmTestFileCompleted(false);
					});
			}else {
				Platform.runLater(() -> {
				statusBar.textProperty().unbind();
				statusBar.setText("Ready");
				statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");
				toggleButton.setDisable(false);
				rightMidSecondGridPane.setDisable(false);
				});
//	            minImageView.setDisable(false);
			}
		});
	}

	private void applyUiStatus(TestState currentState) {
		Platform.runLater(() -> {
			statusBar.textProperty().unbind();
			StateMachine.setDissableEnable(false);
//			////System.out.println("StateMachineStateMachineStateMachine" + StateMachine.isDissableEnable());
//			statusBar.setText("Ready");
			statusBar.setText("Stopping test execution.");
			statusBarVbox.setStyle(commonStyle + "-fx-background-color: #e5350e");
//			toggleButton.setDisable(false);
//			rightMidSecondGridPane.setDisable(false);
		});
	}

//EXIT::(11-08-2025)
	// Method to check both
	private boolean aitess1Updated = false;
	private boolean aitess2Updated = false;

	private void checkBothAitessLaunched() {
		if (aitess1Updated && aitess2Updated) {
			boolean bothLaunched = StateMachine.aitess1LaunchedProperty().get()
					&& StateMachine.aitess2LaunchedProperty().get();
			if (bothLaunched) {
//				////System.out.println("Both AITESS1 and AITESS2 have launched and updated. INItialize");

				// ✅ This ensures all UI updates are done on the JavaFX Application Thread
				Platform.runLater(() -> {
					statusBar.setText("Ready");
					statusBarVbox.setStyle(commonStyle + "-fx-background-color: #037ce6");

				});
			}
		}
	}

	public GridPane createUserDashboard() {

		aitess2ConfigManagement.getAllDfccStatusCommand();
		bottomMainGridPane.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/UserDashboard.css").toExternalForm());
		bottomMainGridPane.setHgap(10);
		bottomMainGridPane.setId("bottomMainGridPane");
		bottomMidTopGridPane.setId("bottomMidTopGridPane");

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(17);
		ColumnConstraints bottomMidColumn = new ColumnConstraints();
		bottomMidColumn.setPercentWidth(66);
		ColumnConstraints bottomRightColumn = new ColumnConstraints();
		bottomRightColumn.setPercentWidth(17);

		RowConstraints bottomRow = new RowConstraints();
		bottomRow.setPercentHeight(100);

		bottomMainGridPane.getColumnConstraints().addAll(bottomLeftColumn, bottomMidColumn, bottomRightColumn);
		bottomMainGridPane.getRowConstraints().add(bottomRow);

		bottomMainGridPane.add(createBottomleftGridPane(), 0, 0);
		bottomMainGridPane.add(createBottomMidGridPane(), 1, 0);
		bottomMainGridPane.add(createBottomRightGridPane(), 2, 0);

		return bottomMainGridPane;

	}

	private GridPane createBottomleftGridPane() {
		GridPane bottomLeftGridPane = new GridPane();
		bottomLeftGridPane.setVgap(10);

		ColumnConstraints bottomLeftColumn = new ColumnConstraints();
		bottomLeftColumn.setPercentWidth(100);

		RowConstraints bottomLeftTopRow = new RowConstraints();
		bottomLeftTopRow.setPercentHeight(69);

		RowConstraints bottomLeftMidRow = new RowConstraints();
		bottomLeftMidRow.setPercentHeight(24);

		RowConstraints bottomLeftBottomRow = new RowConstraints();
		bottomLeftBottomRow.setPercentHeight(7);

		bottomLeftGridPane.getColumnConstraints().add(bottomLeftColumn);
		bottomLeftGridPane.getRowConstraints().addAll(bottomLeftTopRow, bottomLeftMidRow, bottomLeftBottomRow);

		bottomLeftGridPane.add(createMenuBox(), 0, 0);
		bottomLeftGridPane.add(createSubMenuBox(), 0, 1);
		bottomLeftGridPane.add(createButtonBox(), 0, 2);

		return bottomLeftGridPane;
	}

	private TreeView<Label> createMenuBox() {
		TreeItem<Label> rootItem = new TreeItem<>();
		rootItem.setExpanded(true);
		TreeView<Label> menuTreeView = new TreeView<>(rootItem);
		menuTreeView.getStyleClass().add("menu-container");
		menuTreeView.setShowRoot(false);

		menuTreeView.setOnMouseClicked(event -> {
			TreeItem<Label> selectedItem = menuTreeView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				Label selectedLabel = selectedItem.getValue();

				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on " + selectedLabel.getText() + " menu");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

				if (selectedItem.getChildren().isEmpty()) {
					centerContentController.createUserCenterContent(bottomMidTopGridPane, selectedLabel.getText(), null,
							null);
				}

				if (!selectedItem.getChildren().isEmpty()) {
					selectedItem.getChildren().forEach(subMenuItem -> {
					});
				}
			}
		});

		if (UserData.getRoleId().equals("RL_ID_3")) {
			addTreeItemWithChildren(rootItem, "Dashboard",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/dashboard.png", null);
			
			addTreeItemWithChildren(rootItem, "LRU Config",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png", null);

			if (currentSessionDetails.getSessionTypeID().equals("ST4")) {
				addTreeItemWithChildren(rootItem, "Testing",
						DFCCConstant.JARSTRING + "/Resources/Images/menuImages/testing.png", new String[] { "Self Test",
								"SRU/LRU Test", "Trials Config", "Trials Testing", "Advanced Testing" });
			} else {
				addTreeItemWithChildren(rootItem, "Testing",
						DFCCConstant.JARSTRING + "/Resources/Images/menuImages/testing.png",
						new String[] { "Self Test", "SRU/LRU Test", "Session Testing", "Advanced Testing" });
			}

			addTreeItemWithChildren(rootItem, "Results",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png",
					new String[] { "Current Execution", "Unit Results", "Session Results", "Stage Results" });
			addTreeItemWithChildren(rootItem, "Data Analysis",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/advance_testing.png", null);
			addTreeItemWithChildren(rootItem, "Reports",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png",
					new String[] { "PQT Report", "ESS Report", "Datapack Report", "Upload", "Data Backup" });

		} else if (UserData.getRoleId().equals("RL_ID_4")) {

			addTreeItemWithChildren(rootItem, "Dashboard",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/dashboard.png", null);
			
			addTreeItemWithChildren(rootItem, "Testing",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/testing.png",
					new String[] { "Self Test", "SRU/LRU Test", "OFP-Loading" });
			addTreeItemWithChildren(rootItem, "Results",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/results.png",
					new String[] { "Current Execution", "Unit Results", "Session Results", "Stage Results" });
//			addTreeItemWithChildren(rootItem, "Test Summary",
//					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/advance_testing.png", null);
			addTreeItemWithChildren(rootItem, "Reports",
					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png",
					new String[] { "Data Backup","History Report" });
//			addTreeItemWithChildren(rootItem, "History Reports",
//					DFCCConstant.JARSTRING + "/Resources/Images/menuImages/reports.png", null);
		}

		menuTreeView.setPadding(new Insets(5, 10, 5, 10));

		return menuTreeView;
	}

	private void addTreeItemWithChildren(TreeItem<Label> parent, String text, String imagePath, String[] children) {
		Image menuImage = new Image(imagePath);
		Label newMenuItem = new Label(text);
		ImageView menuImageView = new ImageView(menuImage);
		menuImageView.setFitWidth(newMenuItem.getFont().getSize() + 30);
		menuImageView.setFitHeight(newMenuItem.getFont().getSize() + 30);
		menuImageView.getStyleClass().add("menu-image");

		newMenuItem.setGraphic(menuImageView);
		newMenuItem.getStyleClass().add("menu-item");

		TreeItem<Label> menuItem = new TreeItem<>(newMenuItem);

		if (children != null) {
			for (String child : children) {
				Label newSubMenuItem = new Label(child);
				TreeItem<Label> childItem = new TreeItem<>(newSubMenuItem);
				menuItem.getChildren().add(childItem);
				newSubMenuItem.getStyleClass().add("sub-menu-item");
			}
		}
		parent.getChildren().add(menuItem);
	}

	private VBox createSubMenuBox() {
		VBox middleMenuBox = new VBox(3);
		middleMenuBox.setAlignment(Pos.CENTER);
		middleMenuBox.getStyleClass().add("middle-menu");

		String[] labelsText = { "Configuration", "End Session", "Log Book", "Check Sum Info" };

		for (String labelText : labelsText) {
			Label label = new Label(labelText);
			label.prefWidthProperty().bind(middleMenuBox.widthProperty());
			label.getStyleClass().add("middle-menu-item");

			label.setOnMouseClicked(event -> {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on " + labelText);
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

				if (labelText == "End Session") {
					UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
					UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
							currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
							StateMachine.getCurrentUserLogin(), new Date(),
							"session " + currentSessionDetails.getSessionName() + " Clicked End Session");
					uutLogbookManagement.addUUTLogBook(uutLogBookDto);
				}

				if (labelText == "Close Session") {
					UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
					UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
							currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
							StateMachine.getCurrentUserLogin(), new Date(),
							"session " + currentSessionDetails.getSessionName() + " closed");
					uutLogbookManagement.addUUTLogBook(uutLogBookDto);
				}

//				Changed by Vignesh 31-07-25 for showing stage name in result page
				centerContentController.createUserCenterContent(bottomMidTopGridPane, labelText, null, null);
				for (Node node : middleMenuBox.getChildren()) {
					if (node instanceof Label) {
						((Label) node).getStyleClass().remove("selected");
					}
				}
				label.getStyleClass().add("selected");
			});

			middleMenuBox.getChildren().add(label);
		}
		return middleMenuBox;
	}

	private GridPane createButtonBox() {
		GridPane bottomButtonGridPane = new GridPane();
		bottomButtonGridPane.setHgap(10);
		bottomButtonGridPane.setVgap(10);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomButtonGridPane.getColumnConstraints().addAll(firstColumn);
		bottomButtonGridPane.getRowConstraints().addAll(firstRow);

//		VBox logoutBox = new VBox();
		HBox logoutBox = new HBox(5);
		logoutBox.setAlignment(Pos.CENTER);
		Label logoutLabel = new Label("Logout");
		Image icon = new Image(
				getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/menuImages/logout.png"));
		ImageView iconView = new ImageView(icon);

		iconView.setFitWidth(20);
		iconView.setFitHeight(20);

		logoutBox.getChildren().addAll(iconView, logoutLabel);

		// Attach CSS class
		logoutBox.getStyleClass().add("glossy-button");
		logoutLabel.getStyleClass().add("logout-text");

		logoutBox.setOnMouseClicked(e -> {
			if (StateMachine.isConfirmTestFileCompleted()) {

				Notifications
						.showWarningAlert("Please Wait until" + StateMachine.getRunningTestName() + " test Completes");
				return;
			}
			if (StateMachine.getTestState() == TestState.PENDING || StateMachine.getTestState() == TestState.STOPPED
					|| StateMachine.getTestState() == TestState.COMPLETED) {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						StateMachine.getCurrentUserLogin() + " logged out");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement
						.getInstance();

				Notifications.showConfirmationDialog("Logout Confirmation",
						"Are you sure you want to log out and close the application?", () -> {
							// Anuj : 05/08/2025 -- Changed Pattern for Date and Time
							LocalDateTime currentDateTime = LocalDateTime.now();
//							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
//							String formattedDate = currentDateTime.format(formatter);

							SessionFileManagement session = new SessionFileManagement();
							LogOutFileCopyResponse response = session
									.copyingFileWhileLogOut(StateMachine.currentSessionDetails.getSessionId());
//							////System.out.println("response.getCode()" + response.getCode());

							if (response.getCode() == 1) {

								SessionTimingService s = new SessionTimingService();

								int failedFiles = 0;
								for (CopyFileDTO copy : DFCCConstant.FailedStagesRdfPaths) {
									if (!copy.getStatus().equals("SUCCESS")) {
										failedFiles++;
									}
								}
//								////System.out.println("Total Files" + DFCCConstant.FailedStagesRdfPaths.size());
//								////System.out.println("Failed Files" + failedFiles);
								// ADD TIME NEW
//								s.addSessionTime(currentSessionDetails.getSessionId(),
//										StateMachine.getCurrentlySelectedStageId(), "", currentDateTime.toString(),
//										DFCCConstant.FailedStagesRdfPaths.size(), failedFiles);
//								
//								aitessProcessControlManagement.WriteDfccPowerOffCommandToAitess2();
								aitessProcessControlManagement.endAllProcessOnLogout();
								Platform.exit();
								System.exit(0);
							} else if (response.getCode() == 0) {
								Alert alert = new Alert(AlertType.ERROR);
								alert.setTitle("Error Dialog");
								alert.setHeaderText(null);
								alert.setContentText("Something went wrong! The application will now close.");
								SessionTimingService s = new SessionTimingService();
								int failedFiles = 0;
								for (CopyFileDTO copy : DFCCConstant.FailedStagesRdfPaths) {
									if (!copy.getStatus().equals("SUCCESS")) {
										failedFiles++;
									}
								}
								// ADD TIME NEW
//								s.addSessionTime(currentSessionDetails.getSessionId(),
//										StateMachine.getCurrentlySelectedStageId(), "", currentDateTime.toString(),
//										DFCCConstant.FailedStagesRdfPaths.size(), failedFiles);
								alert.setOnCloseRequest(event -> {
									aitessProcessControlManagement.endAllProcessOnLogout();
									Platform.exit();
									System.exit(0);
								});

								alert.showAndWait();
							} else if (response.getCode() == 100) {

//								SessionTestStateObject.isLogoutFileCopyPopupOpenedProperty()
//										.addListener((observable, oldValue, newValue) -> {
//											if (!newValue && !StateMachine.isRdfCopy()) {
//												////System.out.println("Entred !!!");
//											}
//										});
								StateMachine.setRdfMoveLogout(true);
								SessionTimingService s = new SessionTimingService();

								int failedFiles = 0;
								for (CopyFileDTO copy : DFCCConstant.FailedStagesRdfPaths) {
									if (!copy.getStatus().equals("SUCCESS")) {
										failedFiles++;
									}
								}

//								////System.out.println("Failed Files" + failedFiles);
//								////System.out.println("Total Files" + DFCCConstant.FailedStagesRdfPaths.size());
//ADD TIME NEW
//								s.addSessionTime(currentSessionDetails.getSessionId(),
//										StateMachine.getCurrentlySelectedStageId(), "", currentDateTime.toString(),
//										DFCCConstant.FailedStagesRdfPaths.size(), failedFiles);
//								////System.out.println("Entred out");
								SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(true);
								SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);

								StateMachine.setRdfCopy(false);

							}else {
								Platform.exit();
								System.exit(0);
							}
						});
			} else if (StateMachine.getTestState() == TestState.PAUSED
					|| StateMachine.getTestState() == TestState.RUNNING) {
				Notifications.showWarningAlert("Please stop " + StateMachine.getRunningTestName()
						+ " test before log out and close the application");
			}
		});

		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		Label label1 = new Label("Check sum Info");
		box.setCursor(Cursor.HAND);
		box.getChildren().add(label1);
		box.getStyleClass().add("logout-button");
		box.getStyleClass().add("logout-text");

		box.setOnMouseClicked(event -> {
			CheckSumController checkSumController = new CheckSumController();
			checkSumController.createCheckSumDataPopup();

		});

		bottomButtonGridPane.add(logoutBox, 0, 0);

		return bottomButtonGridPane;

	}

	private GridPane createBottomRightGridPane() {
		GridPane bottomRightGridPane = new GridPane();
		bottomRightGridPane.setVgap(10);

		ColumnConstraints bottomRightColumn = new ColumnConstraints();
		bottomRightColumn.setPercentWidth(100);

		RowConstraints bottomRightTopRow = new RowConstraints();
		bottomRightTopRow.setPercentHeight(25);

		RowConstraints bottomRightMidRow = new RowConstraints();
		bottomRightMidRow.setPercentHeight(50);

		RowConstraints bottomRightBottomRow = new RowConstraints();
		bottomRightBottomRow.setPercentHeight(18);

		RowConstraints bottomRightBottomLastRow = new RowConstraints();
		bottomRightBottomLastRow.setPercentHeight(7);

		bottomRightGridPane.getColumnConstraints().add(bottomRightColumn);
		bottomRightGridPane.getRowConstraints().addAll(bottomRightTopRow, bottomRightMidRow, bottomRightBottomRow,
				bottomRightBottomLastRow);

		bottomRightGridPane.add(createRightTop(), 0, 0);
		bottomRightGridPane.add(createRightMidFirst(), 0, 1);
		bottomRightGridPane.add(createRigthMidSecond(), 0, 2);
		bottomRightGridPane.add(createRightBottom(), 0, 3);

		return bottomRightGridPane;
	}

	private GridPane createRightTop() {
		VBox bottomRightTopBox = new VBox();
		bottomRightTopBox.setAlignment(Pos.CENTER);

		Label titleLabel = new Label("Session Details");
		titleLabel.getStyleClass().add("right-top-title");
		bottomRightTopBox.getChildren().add(titleLabel);

		Label serialNo = new Label("Serial No: ");
		Label sessionType = new Label("Session Type: ");
		sessionType.setWrapText(true);
		Label startTime = new Label("Start Time: ");
		Label sessionName = new Label("Session Name: ");
		sessionName.setWrapText(true);

		serialNo.setAlignment(Pos.CENTER_LEFT);
		sessionType.setAlignment(Pos.CENTER_LEFT);
		startTime.setAlignment(Pos.CENTER_LEFT);
		sessionName.setAlignment(Pos.CENTER_LEFT);

		serialNo.getStyleClass().add("session-name-label");
		sessionType.getStyleClass().add("session-name-label");
		startTime.getStyleClass().add("session-name-label");
		sessionName.getStyleClass().add("session-name-label");

		serialNo1.setAlignment(Pos.CENTER_LEFT);
		sessionType1.setAlignment(Pos.CENTER_LEFT);
		startTime1.setAlignment(Pos.CENTER_LEFT);
		sessionName1.setAlignment(Pos.CENTER_LEFT);

		serialNo1.getStyleClass().add("session-name-label1");
		sessionType1.getStyleClass().add("session-name-label1");
		startTime1.getStyleClass().add("session-name-label1");
		sessionName1.getStyleClass().add("session-name-label1");

		serialNo1.setText(currentSessionDetails.getDfccSerialNumber());
		serialNo1.setWrapText(true);
		sessionType1.setText(currentSessionDetails.getSessionTypeName());
		sessionType1.setWrapText(true);

		sessionName1.setText(currentSessionDetails.getSessionName());
		sessionName1.setWrapText(true);

		GridPane bottomRightTopGridPane = new GridPane();
		bottomRightTopGridPane.setVgap(5);
		bottomRightTopGridPane.setHgap(5);
		bottomRightTopGridPane.getStyleClass().add("right-first-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(40);

		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(60);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(20);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(20);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(20);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(20);
		RowConstraints fivthRow = new RowConstraints();
		fivthRow.setPercentHeight(20);

		bottomRightTopGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
//		bottomRightTopGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow, fivthRow );

		bottomRightTopGridPane.add(bottomRightTopBox, 0, 0, 2, 1);
		bottomRightTopGridPane.add(serialNo, 0, 1);
		bottomRightTopGridPane.add(sessionType, 0, 2);
		bottomRightTopGridPane.add(startTime, 0, 3);
		bottomRightTopGridPane.add(sessionName, 0, 4);

		bottomRightTopGridPane.add(serialNo1, 1, 1);
		bottomRightTopGridPane.add(sessionType1, 1, 2);
		bottomRightTopGridPane.add(startTime1, 1, 3);
		bottomRightTopGridPane.add(sessionName1, 1, 4);

		Label newLabel = new Label();

		newLabel.getStyleClass().add("session-name-label");

		Tooltip tooltip = new Tooltip(currentSessionDetails.getSessionName());
		Tooltip.install(newLabel, tooltip);
		tooltip.setShowDelay(Duration.ZERO);
		tooltip.setHideDelay(Duration.ZERO);

//		newLabel.setText(currentSessionDetails.getSessionName());
//		newLabel.setPrefWidth(290);
//		newLabel.setWrapText(true);
//		newLabel.setPadding(new Insets(0, 0, 0, 5));
//		newLabel.getStyleClass().add("session-name-label");

//		bottomRightTopBox.getChildren().addAll(titleLabel, newLabel);

		return bottomRightTopGridPane;
	}

	private Pane createRightMidFirst() {
		Pane bottomRightMidPane = new Pane();

		GridPane bottomRightMidGridPane = new GridPane();

		bottomRightMidGridPane.getStyleClass().add("right-second-container");

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(10);

		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(30);

		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(26);

		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(26);

		RowConstraints fifthRow = new RowConstraints();
		fifthRow.setPercentHeight(8);

		bottomRightMidGridPane.getColumnConstraints().add(firstColumn);
		bottomRightMidGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);

		bottomRightMidGridPane.add(createBottomRightMidFirst(), 0, 0);
		bottomRightMidGridPane.add(createBottomRightMidSecond(), 0, 1);
		bottomRightMidGridPane.add(createBottomRightMidThird(), 0, 2);
		bottomRightMidGridPane.add(createBottomRightMidFourth(), 0, 3);
		bottomRightMidGridPane.add(createBottomRightLastUpdateTime(), 0, 4);

		bottomRightMidPane.getChildren().add(bottomRightMidGridPane);
		return bottomRightMidGridPane;
	}

	private HBox createBottomRightLastUpdateTime() {
		HBox labelbox = new HBox();
		labelbox.getStyleClass().add("label-box");

		Label labelnew = new Label("Last update time :");

		HBox labelnewbox = new HBox(5);
		Label labelnew1 = new Label();
		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
		DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
         // Changed by Vignesh 31-07-25 update temp last checked time and 
		//changed by sai 16/12/2025
		StateMachine.tempLastCheckedTimeProperty().addListener((obs, oldVal, newVal) -> {
					Platform.runLater(()->{
				LocalDateTime dateTime = LocalDateTime.parse(newVal, inputFormat);
		        labelnew1.setText(dateTime.format(outputFormat));
		        	});
			
			
			
		});

		labelnew1.setAlignment(Pos.CENTER);
		labelnewbox.setAlignment(Pos.CENTER);
		labelnewbox.getChildren().add(labelnew1);
		labelnewbox.getStyleClass().add("label-new-box");

		labelbox.getChildren().addAll(labelnew, labelnewbox);
		labelbox.setAlignment(Pos.CENTER);
		lastUpdateTime.getChildren().add(labelbox);

		return labelbox;
	}

	private GridPane createBottomRightMidFirst() {
//		GridPane bottomRightMidFirstGridPane = new GridPane();
//
//		ColumnConstraints firstColumn = new ColumnConstraints();
//		firstColumn.setPercentWidth(100);
//
//		RowConstraints firstRow = new RowConstraints();
//		firstRow.setPercentHeight(40);
//
//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(60);
//
//		bottomRightMidFirstGridPane.getColumnConstraints().add(firstColumn);
//		bottomRightMidFirstGridPane.getRowConstraints().addAll(firstRow, secondRow);
//
//		VBox firstRowBox = new VBox(10);
//		firstRowBox.setAlignment(Pos.CENTER_LEFT);
//		firstRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
//		firstRowBox.prefHeightProperty().bind(firstRow.prefHeightProperty());
//		Label titleLabel = new Label("DFCC");
//		titleLabel.getStyleClass().add("dfcc-title");
//		firstRowBox.getChildren().add(titleLabel);
//
//		VBox secondRowBox = new VBox(10);
//		secondRowBox.setAlignment(Pos.CENTER_LEFT);
//		secondRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
//		secondRowBox.prefHeightProperty().bind(secondRow.prefHeightProperty());
//		Label statusLabel = new Label("DFCC Power OFF");
//
//		secondRowBox.setOnMouseClicked(e -> {
//			if (!checkAitessStatus.isBothAitessOn()) {
//				return;
//			}
//			if (statusLabel.getText().toLowerCase().contains("on")) {
//				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
//				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
//						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
//						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
//						"clicked on DFCC power ON button");
//				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
//
//				UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
//				UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
//						currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
//						StateMachine.getCurrentUserLogin(), new Date(), "DFCC gets powered OFF");
//				uutLogbookManagement.addUUTLogBook(uutLogBookDto);
//				aitessProcessControlManagement.WriteDfccPowerOffCommandToAitess2();
//
//				Platform.runLater(() -> {
//					statusLabel.setText("DFCC Power OFF");
//					dfccCheckStatus.getDfccPowerStatus().set(false);
//				});
//
//			} else if (statusLabel.getText().toLowerCase().contains("off")) {
//				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
//				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
//						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
//						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
//						"clicked on DFCC power OFF button");
//				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
//
//				UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
//				UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
//						currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
//						StateMachine.getCurrentUserLogin(), new Date(), "DFCC gets powered ON");
//				uutLogbookManagement.addUUTLogBook(uutLogBookDto);
//				aitessProcessControlManagement.WriteDfccPowerOnCommandToAitess2();
//
//				Platform.runLater(() -> {
//					statusLabel.setText("DFCC Power ON");
//					dfccCheckStatus.getDfccPowerStatus().set(true);
//				});
//
//			}
//		});
//
//		dfccCheckStatus.dfccPowerStatusProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue) {
//				secondRowBox.getStyleClass().remove("dfcc-status-box-off");
//				secondRowBox.getStyleClass().add("dfcc-status-box-on");
//			} else {
//				secondRowBox.getStyleClass().remove("dfcc-status-box-on");
//				secondRowBox.getStyleClass().add("dfcc-status-box-off");
//			}
//		});
//		secondRowBox.getStyleClass().addAll("dfcc-status-box", "dfcc-status-box-off");
//		statusLabel.getStyleClass().add("dfcc-status");
//		secondRowBox.getChildren().add(statusLabel);
//
//		bottomRightMidFirstGridPane.add(firstRowBox, 0, 0);
//		bottomRightMidFirstGridPane.add(secondRowBox, 0, 1);
//
//		return bottomRightMidFirstGridPane;

		GridPane bottomRightMidFirstGridPane = new GridPane();

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomRightMidFirstGridPane.getColumnConstraints().add(firstColumn);
		bottomRightMidFirstGridPane.getRowConstraints().addAll(firstRow);

		HBox firstRowBox = new HBox(10);
		firstRowBox.setAlignment(Pos.CENTER);

		firstRowBox.prefWidthProperty().bind(firstColumn.prefWidthProperty());
		firstRowBox.prefHeightProperty().bind(firstRow.prefHeightProperty());

		Label titleLabel = new Label("DFCC Power");
		titleLabel.getStyleClass().add("dfcc-title");

		HBox toggleSwitch = createToggleSwitch();

		// 31/7/25 srini
//		StateMachine.testStateProperty().addListener((obs, oldState, newState) -> {
//			if (newState == TestState.RUNNING || newState == TestState.PAUSED) {
//				toggleSwitch.setDisable(true);
//			} else {
//				toggleSwitch.setDisable(false);
//			}
//		});

		firstRowBox.getChildren().addAll(titleLabel, toggleSwitch);
		bottomRightMidFirstGridPane.add(firstRowBox, 0, 0);

		return bottomRightMidFirstGridPane;
	}

	private HBox createToggleSwitch() {
		background.setArcWidth(30);
		background.setArcHeight(30);

		toggleButton.setTranslateX(-26);

		toggleLabel.setTextFill(Color.WHITE);
		toggleLabel.setStyle("-fx-font-size:16px; -fx-font-weight: bold; -fx-padding:0px 5px");

		stack.getChildren().addAll(background, toggleLabel, toggleButton);

		toggleSwitch.setSpacing(0);
		toggleSwitch.getChildren().add(stack);

		// Set the initial alignment of the label
		StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);

		toggleButton.setOnMouseClicked(event -> {

			onClickToggle(background, toggleButton, toggleLabel);

		});
		toggleButton.setCursor(Cursor.HAND);

		TranslateTransition transition = new TranslateTransition(Duration.millis(200), toggleButton);

//		Before Suji CHange on:(05-08-2025)

//		dfccCheckStatus.dfccPowerStatusProperty().addListener((observable, oldValue, newValue) -> {
//
//			TranslateTransition transition = new TranslateTransition(Duration.millis(200), toggleButton);
//
//			if (dfccCheckStatus.getDfccPowerStatus().get()) {
//				Platform.runLater(() -> {
//					transition.setToX(26);
//					background.setFill(Color.GREEN);
//					toggleLabel.setText("ON");
//					StackPane.setAlignment(toggleLabel, Pos.CENTER_LEFT);
//					transition.play();
//				});
//			}
//
//			else {
//				Platform.runLater(() -> {
//					transition.setToX(-26);
//					background.setFill(Color.RED);
//					toggleLabel.setText("OFF");
//					StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
//					transition.play();
//				});
//			}
//
//		});

//		After Suji Changing for Updating toggle status based on Channel status(05-08-2025)::

		dfccCheckStatus.dfccPowerStatusProperty().addListener((observable, oldValue, newValue) -> {
//			////System.out.println("Entred Power on Power oN Stats Before");
//			//System.out.println("Entred Power on Power oN Stats after");
//			suji Added for BLS Issue::(07-08-2025)
			
			
			
			String sessionType = currentSessionDetails.getSessionTypeID();
			
				if (dfccCheckStatus.getDfccPowerStatus().get() && (powerOnStatus.getChannel1Status().equals("online")
						&& powerOnStatus.getChannel2Status().equals("online")
						&& powerOnStatus.getChannel3Status().equals("online")
						&& powerOnStatus.getChannel4Status().equals("online"))) {
					Platform.runLater(() -> {
//						//System.out.println("Suji check toggle :Power On New Value" );
						
						toggleButton.setDisable(false);
						transition.setToX(26);
						background.setFill(Color.GREEN);
						toggleLabel.setText("ON");
						StackPane.setAlignment(toggleLabel, Pos.CENTER_LEFT);
						transition.play();
//						Sai for multiple clicks
//						Platform.runLater(() -> {
//						toggleButton.setDisable(false);
//						rightMidSecondGridPane.setDisable(false);
//					});
					});
				} else {
					Platform.runLater(() -> {
//						//System.out.println("Suji check toggle :Power On Else New Value" );
						toggleButton.setDisable(false);
						transition.setToX(-26);
						background.setFill(Color.RED);
						toggleLabel.setText("OFF");
						StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
						transition.play();
//						Sai for multiple clicks
//						Platform.runLater(() -> {
//						toggleButton.setDisable(false);
//						rightMidSecondGridPane.setDisable(false);
//					});
					});
				}
				
			
			
		});

//		Exit::

//		Suji Added for BLS Toggel issue::(06-08-2025)
		dfccCheckStatus.dfccOnlineStatusProperty().addListener((observable, oldValue, newValue) -> {
//			////System.out.println("Entred Online Listener in Ui" + newValue);
//			//System.out.println("Entred Power on Online Stats after");
			if (newValue) {

				Platform.runLater(() -> {
//					////System.out.println("Entred New Value " + newValue);
//					//System.out.println("Suji check toggle :Online New Value" );
					toggleButton.setDisable(false);
					transition.setToX(-26);
					background.setFill(Color.RED);
					toggleLabel.setText("OFF");
					StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
					transition.play();
//					//System.out.println("Entred Power on -26");
				});
			} else {
				Platform.runLater(() -> {
//					//System.out.println("Suji check toggle :Online Old Value" );
					toggleButton.setDisable(false);
					transition.setToX(26);
					background.setFill(Color.GREEN);
					toggleLabel.setText("ON");
					StackPane.setAlignment(toggleLabel, Pos.CENTER_LEFT);
					transition.play();
//					//System.out.println("Entred Power on 26");
				});
			}
			

		});

//		EXIT::(06-08-2025)

		return toggleSwitch;
	}

	
	
	private void onClickToggle(Rectangle background2, Circle toggleButton, Label toggleLabel) {

//		Suji added to avoid the toggle if self test is runned
		if (StateMachine.getTestTypeId() != null && StateMachine.getTestTypeId().equalsIgnoreCase("TT1")) {
			Notifications.showErrorAlert(
					"'SELF-TEST' mode is active.\nDFCC unit must not be switched on. You may start any other test type directly.");

			return;
		}

		if (!checkAitessStatus.isBothAitessOn()) {
			return;
		}
		
		

		TranslateTransition transition = new TranslateTransition(Duration.millis(200), toggleButton);

		if (StateMachine.isAllowToggle()) {
			StateMachine.setAllowToggle(false);
			
			if (dfccCheckStatus.getDfccPowerStatus().get()) {
				toggleButton.setDisable(true);

				aitessProcessControlManagement.WriteDfccPowerOffCommandToAitess2();
			} else {
				Dialog<ButtonType> dialog = new Dialog<>();
				dialog.setTitle("Confirmation Dialog");
				dialog.setContentText("Please ensure the cooler switch is turned ON.");
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
				dialog.showAndWait();
				toggleButton.setDisable(true);
				aitessProcessControlManagement.WriteDfccPowerOnCommandToAitess2();
//				Sai for multiple clicks
//				Platform.runLater(() -> {
//				toggleButton.setDisable(true);
//				rightMidSecondGridPane.setDisable(true);
//			});
			}
		}
		
		
		
//		EXIT:::(07-08-2025)

//		if(OnlineStatus.getChannel1Status().equalsIgnoreCase(null) &&
//				OnlineStatus.getChannel2Status().equalsIgnoreCase(null) &&
//				OnlineStatus.getChannel3Status().equalsIgnoreCase(null) &&
//				OnlineStatus.getChannel4Status().equalsIgnoreCase(null)) {
//			
//			Platform.runLater(() -> {
//
//				transition.setToX(-26);
//				background.setFill(Color.RED);
//				toggleLabel.setText("OFF");
//				StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
//				transition.play();
//
//				// Show popup alert
//				Alert alert = new Alert(Alert.AlertType.WARNING);
//				alert.setTitle("DFCC Power Alert");
//				alert.setHeaderText(null);
//				alert.setContentText("All channels are offline. Please check that DFCC is turned ON.");
//				alert.showAndWait();
//
//			});
//			
//		}

//		//System.out.println("Onlime Statu Check ::"+OnlineStatus.getChannel1Status());
//		//System.out.println("Power On Statu Check ::"+powerOnStatus.getChannel1Status());
		if (!OnlineStatus.getChannel1Status().equalsIgnoreCase(null)
				&& !OnlineStatus.getChannel2Status().equalsIgnoreCase(null)
				&& !OnlineStatus.getChannel3Status().equalsIgnoreCase(null)
				&& !OnlineStatus.getChannel4Status().equalsIgnoreCase(null)) {
			if (OnlineStatus.getChannel1Status().equalsIgnoreCase("offline")
					&& OnlineStatus.getChannel2Status().equalsIgnoreCase("offline")
					&& OnlineStatus.getChannel3Status().equalsIgnoreCase("offline")
					&& OnlineStatus.getChannel4Status().equalsIgnoreCase("offline")) {
				Platform.runLater(() -> {

					transition.setToX(-26);
					background.setFill(Color.RED);
					toggleLabel.setText("OFF");
					StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
					transition.play();

					// Show popup alert
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("DFCC Power Alert");
					alert.setHeaderText(null);
					alert.setContentText("Some channels are offline. Please check that DFCC is turned ON.");
					alert.showAndWait();

				});
			}else if (OnlineStatus.getChannel1Status().equalsIgnoreCase("offline")
					|| OnlineStatus.getChannel2Status().equalsIgnoreCase("offline")
					|| OnlineStatus.getChannel3Status().equalsIgnoreCase("offline")
					|| OnlineStatus.getChannel4Status().equalsIgnoreCase("offline")) {
				Platform.runLater(() -> {
					transition.setToX(-26);
					background.setFill(Color.RED);
					toggleLabel.setText("OFF");
					StackPane.setAlignment(toggleLabel, Pos.CENTER_RIGHT);
					transition.play();
				});
				
			}
		}
		
	}

	private final Random random = new Random();

	private void generateData() {
		int n = random.nextInt(100);
		ChannelTemperature a = new ChannelTemperature();
		a.setChannel1Temp("a" + n);
		a.setChannel2Temp("a" + n);
		a.setChannel3Temp("a" + n);
		a.setChannel4Temp("a" + n);
		ChannelTemperature b = new ChannelTemperature();
		b.setChannel1Temp("b" + n);
		b.setChannel2Temp("b" + n);
		b.setChannel3Temp("b" + n);
		b.setChannel4Temp("b" + n);
		ChannelTemperature c = new ChannelTemperature();
		c.setChannel1Temp("c" + n);
		c.setChannel2Temp("c" + n);
		c.setChannel3Temp("c" + n);
		c.setChannel4Temp("c" + n);
		ChannelTemperature d = new ChannelTemperature();
		d.setChannel1Temp("d" + n);
		d.setChannel2Temp("d" + n);
		d.setChannel3Temp("d" + n);
		d.setChannel4Temp("d" + n);

		boardChannelTemp.addBoardTemperatureMap("DFCC_TEMP_AN2", a);
		boardChannelTemp.addBoardTemperatureMap("AN1L_BRD_TEMP", b);
		boardChannelTemp.addBoardTemperatureMap("AN1R_BRD_TEMP", c);
		boardChannelTemp.addBoardTemperatureMap("DM_BRD_TEMP", d);

		ChannelTemperature a1 = new ChannelTemperature();
		a1.setChannel1Temp("aa" + n);
		a1.setChannel2Temp("aa" + n);
		a1.setChannel3Temp("aa" + n);
		a1.setChannel4Temp("aa" + n);
		ChannelTemperature b1 = new ChannelTemperature();
		b1.setChannel1Temp("bb" + n);
		b1.setChannel2Temp("bb" + n);
		b1.setChannel3Temp("bb" + n);
		b1.setChannel4Temp("bb" + n);
		ChannelTemperature c1 = new ChannelTemperature();
		c1.setChannel1Temp("cc" + n);
		c1.setChannel2Temp("cc" + n);
		c1.setChannel3Temp("cc" + n);
		c1.setChannel4Temp("cc" + n);
		ChannelTemperature d1 = new ChannelTemperature();
		d1.setChannel1Temp("dd" + n);
		d1.setChannel2Temp("dd" + n);
		d1.setChannel3Temp("dd" + n);
		d1.setChannel4Temp("dd" + n);

		boardChannelTempAEC.addBoardTemperatureMap("DFCC_TEMP_AN2", a1);
		boardChannelTempAEC.addBoardTemperatureMap("AN1L_BRD_TEMP", b1);
		boardChannelTempAEC.addBoardTemperatureMap("AN1R_BRD_TEMP", c1);
		boardChannelTempAEC.addBoardTemperatureMap("DM_BRD_TEMP", d1);

		channelSCTemp.setChannel1Temperature("1S" + n);
		channelSCTemp.setChannel2Temperature("2S" + n);
		channelSCTemp.setChannel3Temperature("3S" + n);
		channelSCTemp.setChannel4Temperature("4S" + n);

		channelAECTemp.setChannel1Temperature("1A" + n);
		channelAECTemp.setChannel2Temperature("2A" + n);
		channelAECTemp.setChannel3Temperature("3A" + n);
		channelAECTemp.setChannel4Temperature("4A" + n);
	}
	private VBox createBottomRightMidSecond() {

//		Timeline timeline = new Timeline(
//	            new KeyFrame(Duration.seconds(5), event -> generateData())
//	        );
//		timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
//        timeline.play();

		VBox bottomRightMidSecondBox = new VBox(10);
		bottomRightMidSecondBox.setAlignment(Pos.CENTER_LEFT);

//		Temperature Combo Box
		Label titleLabel = new Label("Temperature");
		titleLabel.getStyleClass().add("right-common-title");

		temperatureComboBox.getItems().addAll("SC", "AEC");

		HBox temperatureTitleHBox = new HBox(5);
		HBox.setHgrow(temperatureComboBox, Priority.ALWAYS);
		temperatureComboBox.setMaxWidth(Double.MAX_VALUE);
		temperatureTitleHBox.getChildren().addAll(titleLabel, temperatureComboBox);

//		Board Combo Box
		Label boardLabel = new Label("Board Name");
		boardLabel.getStyleClass().add("right-common-title");

		ComboBox<String> boardComboBox = new ComboBox<String>();
		boardComboBox.getItems().addAll("DFCC_TEMP_AN2", "AN1L_BRD_TEMP", "AN1R_BRD_TEMP", "DM_BRD_TEMP");

		HBox boardTitleHBox = new HBox(5);
		HBox.setHgrow(boardComboBox, Priority.ALWAYS);
		boardComboBox.setMaxWidth(Double.MAX_VALUE);
		boardTitleHBox.getChildren().addAll(boardLabel, boardComboBox);

		GridPane bottomRightMidSecondGridPane = new GridPane();
		bottomRightMidSecondGridPane.setVgap(5);
		bottomRightMidSecondGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(25);

		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(60);

		bottomRightMidSecondGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn,
				fourthColumn);
		bottomRightMidSecondGridPane.getRowConstraints().addAll(firstRow);

		VBox box1 = new VBox();
		box1.setAlignment(Pos.CENTER);
		
		box1.getChildren().add(label1);
		box1.getStyleClass().add("temp-box");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		
		box2.getChildren().add(label2);
		box2.getStyleClass().add("temp-box");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		
		box3.getChildren().add(label3);
		box3.getStyleClass().add("temp-box");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		
		box4.getChildren().add(label4);
		box4.getStyleClass().add("temp-box");

		bottomRightMidSecondGridPane.add(box1, 0, 0);
		bottomRightMidSecondGridPane.add(box2, 1, 0);
		bottomRightMidSecondGridPane.add(box3, 2, 0);
		bottomRightMidSecondGridPane.add(box4, 3, 0);

		temperatureComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			if (!checkAitessStatus.isBothAitessOn()) {
//				return;
//			}
			
			StateMachine.setTemDataSelection(newValue);
			DFCCConstant.temDataSelection = newValue;
			
			Platform.runLater(() -> {
				ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
				ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
						currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
						currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
						"clicked on " + newValue + " temperature");
				appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
				if (currentSessionDetails.getUutId().equals("UUT1")) {
					Platform.runLater(() -> {
//						////System.out.println("Suji Entred 1st" + newValue);
						setMK1Temp(newValue, box1, box2, box3, box4);
						
					});
				} else {
					Platform.runLater(() -> {
						getMK1AandMk2TempData(temperatureComboBox.getValue(), boardComboBox.getValue(), box1, box2,
								box3, box4);
						////System.out.println("Check Suji Board Name:: "+  boardComboBox.getValue());
					});
				}

			});

		});

		boardComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (!currentSessionDetails.getUutId().equals("UUT1")) {
//				if (!checkAitessStatus.isBothAitessOn()) {
//					return;
//				}
//				Suji doubt for Low &high Temp value in dashboard:
				StateMachine.setTemDataSelection(newValue);
				DFCCConstant.temDataSelection = newValue;
//				End
				Platform.runLater(() -> {
					ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
					ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
							currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
							currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(), new Date(),
							"clicked on " + newValue + " temperature board");
					appLogbookManagement.addApplicationLogBook(applicationLogBookDto);

					getMK1AandMk2TempData(temperatureComboBox.getValue(), newValue, box1, box2, box3, box4);
					////System.out.println("Check Suji Board Name::Board Down "+  newValue);
				});
			}
		});

		temperatureComboBox.setValue("SC");
		boardComboBox.setValue("DFCC_TEMP_AN2");

		if (currentSessionDetails.getUutId().equals("UUT1")) {
			bottomRightMidSecondBox.getChildren().addAll(temperatureTitleHBox, bottomRightMidSecondGridPane);
//			Changed by Vignesh 31-07-25 - calling for displaying temp initially
//			////System.out.println("Suji Entred 2nd");
//			setMK1Temp("sc", box1, box2, box3, box4);
		} else {
			bottomRightMidSecondBox.getChildren().addAll(temperatureTitleHBox, boardTitleHBox,
					bottomRightMidSecondGridPane);
//			Changed by Vignesh 31-07-25 - calling for displaying temp initially
//			getMK1AandMk2TempData(temperatureComboBox.getValue(), boardComboBox.getValue(), box1, box2, box3, box4);
		}

		return bottomRightMidSecondBox;

	}

	private void getMK1AandMk2TempData(String temp, String board, VBox box1, VBox box2, VBox box3, VBox box4) {

		Label label1 = (Label) box1.getChildren().get(0);
		Label label2 = (Label) box2.getChildren().get(0);
		Label label3 = (Label) box3.getChildren().get(0);
		Label label4 = (Label) box4.getChildren().get(0);
		
		

		label1.textProperty().unbind();
		label2.textProperty().unbind();
		label3.textProperty().unbind();
		label4.textProperty().unbind();
		
		

		if (temp.equalsIgnoreCase("sc") && boardChannelTemp.getBoardTemperatureMap().get(board) != null) {
			double maxValue = boardChannelTemp.getMaxValue();
			double minValue = boardChannelTemp.getMinValue();
			
			label1.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel1Temp());
			label2.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel2Temp());
			label3.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel3Temp());
			label4.setText(boardChannelTemp.getBoardTemperatureMap().get(board).getChannel4Temp());
//			DFCC_TEMP_AN2
			
			
		
			
			
			
			try {
				double value1 = Double.parseDouble(label1.getText());
				double value2 = Double.parseDouble(label2.getText());
				double value3 = Double.parseDouble(label3.getText());
				double value4 = Double.parseDouble(label4.getText());
				
				

				if ((value1 <= 0 && value1 < 1) || (value2 <= 0 && value2 < 1) || (value3 <= 0 && value3 < 1)
						|| (value4 <= 0 && value4 < 1)) {

					box1.getStyleClass().add("temp-box-off");
					box2.getStyleClass().add("temp-box-off");
					box3.getStyleClass().add("temp-box-off");
					box4.getStyleClass().add("temp-box-off");

				}

				if (value1 < minValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value1 > maxValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value2 < minValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value2 > maxValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value3 < minValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value3 > maxValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value4 < minValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value4 > maxValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

			} catch (NumberFormatException | NullPointerException e) {
				System.err.println("Invalid numeric value in labels: " + label1.getText() + ", " + "" + label2.getText()
						+ ", " + "" + label3.getText() + ", " + "" + label4.getText());
			}
			
			

		} else if (temp.equalsIgnoreCase("aec") && boardChannelTempAEC.getBoardTemperatureMap().get(board) != null) {
			double maxValue = boardChannelTempAEC.getMaxValue();
			double minValue = boardChannelTempAEC.getMinValue();
			
			label1.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel1Temp());
			label2.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel2Temp());
			label3.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel3Temp());
			label4.setText(boardChannelTempAEC.getBoardTemperatureMap().get(board).getChannel4Temp());
			
			
			try {
				double value1 = Double.parseDouble(label1.getText());
				double value2 = Double.parseDouble(label2.getText());
				double value3 = Double.parseDouble(label3.getText());
				double value4 = Double.parseDouble(label4.getText());

				if (value1 < minValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value1 > maxValue) {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box1.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value2 < minValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value2 > maxValue) {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box2.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value3 < minValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value3 > maxValue) {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box3.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}

				if (value4 < minValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
									StateMachine.LessBackgroundColor()));
				} else if (value4 > maxValue) {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
									StateMachine.GreaterBackgroundColor()));
				} else {
					box4.styleProperty()
							.bind(Bindings.createStringBinding(
									() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
									StateMachine.NormalBackgroundColor()));
				}
			} catch (NumberFormatException | NullPointerException e) {
				System.err.println("Invalid numeric value in labels: " + label1.getText() + ", " + "" + label2.getText()
						+ ", " + "" + label3.getText() + ", " + "" + label4.getText());
			}

		}
		scBoardTemperatureMap = boardChannelTemp.getBoardTemperatureMap();
		aecBoardTemperatureMap = boardChannelTempAEC.getBoardTemperatureMap();

		if (scListener != null) {
			scBoardTemperatureMap.removeListener(scListener);
		}
		if (aecListener != null) {
			aecBoardTemperatureMap.removeListener(aecListener);
		}

		if (temp.equalsIgnoreCase("sc")) {
			scListener = change -> {
				if (board.equals(change.getKey())) {
					Platform.runLater(() -> {
						setMk1AandMk2Temp(change.getValueAdded(), box1, box2, box3, box4);
					});
				}
				
				
			};
			scBoardTemperatureMap.addListener(scListener);
		} else if (temp.equalsIgnoreCase("aec")) {
			aecListener = change -> {
				if (board.equals(change.getKey())) {
					Platform.runLater(() -> {
						setMk1AandMk2Temp(change.getValueAdded(), box1, box2, box3, box4);
						////System.out.println("Entred into 111 AEC selection::" + temp);
					});
				}
				
				
			};
			aecBoardTemperatureMap.addListener(aecListener);
		}
//		 StateMachine.tempLastCheckedTimeProperty().addListener((obs, oldVal, newVal) -> {
//			 Platform.runLater(() -> {
//					
//					
//					UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
//					UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
//							currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
//							StateMachine.getCurrentUserLogin(), new Date(),
//							temp + " temperature fetched [" + "CH1: " + label1.getText() + ", CH2: " + label2.getText()
//									+ ", CH3: " + label3.getText() + ", CH4: " + label4.getText() + "]");
//					uutLogbookManagement.addUUTLogBook(uutLogBookDto);
//					
//					});
//			});
		 StateMachine.tempLastCheckedTimeProperty().addListener((obs, oldVal, newVal) -> {
			 Platform.runLater(() -> {
					if(!StateMachine.isTempUpdate()) {
						channel1 = Double.parseDouble(boardChannelTemp.getBoardTemperatureMap().get("DFCC_TEMP_AN2").getChannel1Temp());
						channel2 = Double.parseDouble(boardChannelTemp.getBoardTemperatureMap().get("DFCC_TEMP_AN2").getChannel2Temp());
						channel3 = Double.parseDouble(boardChannelTemp.getBoardTemperatureMap().get("DFCC_TEMP_AN2").getChannel3Temp());
						channel4 = Double.parseDouble(boardChannelTemp.getBoardTemperatureMap().get("DFCC_TEMP_AN2").getChannel4Temp());
					UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
					UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
							currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
							StateMachine.getCurrentUserLogin(), new Date(),
							temp + " temperature fetched [" + "CH1: " + channel1 + ", CH2: " + channel2
									+ ", CH3: " + channel3 + ", CH4: " + channel4 + "]");
					uutLogbookManagement.addUUTLogBook(uutLogBookDto);
					StateMachine.setTempUpdate(true);
					////System.out.println("Check Bord Name:" + board + "Value :" + boardChannelTemp.getBoardTemperatureMap().get("DFCC_TEMP_AN2").getChannel1Temp());
					////System.out.println("Entred 3mins Temp Update:" + "CH!:" +channel1+"Ch2: "+channel2 + "Ch3: " + channel3+"Ch4: " + channel4 );
					}
					});
			});
		
		
		
	}

	private void setMk1AandMk2Temp(ChannelTemperature valueAdded, VBox box1, VBox box2, VBox box3, VBox box4) {
		Label label1 = (Label) box1.getChildren().get(0);
		Label label2 = (Label) box2.getChildren().get(0);
		Label label3 = (Label) box3.getChildren().get(0);
		Label label4 = (Label) box4.getChildren().get(0);
		String selectiponValue = temperatureComboBox.getValue();

		double maxValue = 0;
		double minValue = 0;
//		SUJI CHANGED FOR MK1A Color issue::
		if (selectiponValue.equalsIgnoreCase("sc")) {
			maxValue = boardChannelTemp.getMaxValue();
			minValue = boardChannelTemp.getMinValue();
			
		} else if (selectiponValue.equalsIgnoreCase("aec")) {
			maxValue = boardChannelTempAEC.getMaxValue();
			minValue = boardChannelTempAEC.getMinValue();
			
		}
//		EXIT::

		label1.textProperty().bind(valueAdded.channel1TempProperty());
		label2.textProperty().bind(valueAdded.channel2TempProperty());
		label3.textProperty().bind(valueAdded.channel3TempProperty());
		label4.textProperty().bind(valueAdded.channel4TempProperty());

		try {
			double value1 = Double.parseDouble(label1.getText());
			double value2 = Double.parseDouble(label2.getText());
			double value3 = Double.parseDouble(label3.getText());
			double value4 = Double.parseDouble(label4.getText());

//			////System.out.println("MK1A/2 TEMP1::" + value1);
//			////System.out.println("MK1A/2 TEMP2::" + value2);
//			////System.out.println("MK1A/2 TEMP3::" + value3);
//			////System.out.println("MK1A/2 TEMP4::" + value4);

			if ((value1 <= 0 && value1 < 1) || (value2 <= 0 && value2 < 1) || (value3 <= 0 && value3 < 1)
					|| (value4 <= 0 && value4 < 1)) {

				box1.getStyleClass().add("temp-box-off");
				box2.getStyleClass().add("temp-box-off");
				box3.getStyleClass().add("temp-box-off");
				box4.getStyleClass().add("temp-box-off");

			}

			if (value1 < minValue) {
				box1.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));

			} else if (value1 > maxValue) {
				box1.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box1.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

			if (value2 < minValue) {
				box2.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));
			} else if (value2 > maxValue) {
				box2.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box2.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

			if (value3 < minValue) {
				box3.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));
			} else if (value3 > maxValue) {
				box3.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box3.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

			if (value4 < minValue) {
				box4.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.LessBackgroundColor().get(),
								StateMachine.LessBackgroundColor()));
			} else if (value4 > maxValue) {
				box4.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.GreaterBackgroundColor().get(),
								StateMachine.GreaterBackgroundColor()));
			} else {
				box4.styleProperty()
						.bind(Bindings.createStringBinding(
								() -> "-fx-background-color: " + StateMachine.NormalBackgroundColor().get(),
								StateMachine.NormalBackgroundColor()));
			}

		} catch (NumberFormatException | NullPointerException e) {
			System.err.println("Invalid numeric value in labels: " + label1.getText() + ", " + "" + label2.getText()
					+ ", " + "" + label3.getText() + ", " + "" + label4.getText());
		}
		
		

	}

//Before Suji Change
//	private void setMK1Temp(String selectedItem, VBox box1, VBox box2, VBox box3, VBox box4) {
//		Label label1 = (Label) box1.getChildren().get(0);
//		Label label2 = (Label) box2.getChildren().get(0);
//		Label label3 = (Label) box3.getChildren().get(0);
//		Label label4 = (Label) box4.getChildren().get(0);
//		
//
//		if (selectedItem.equalsIgnoreCase("sc")) {
//			label1.textProperty().bind(channelSCTemp.channel1TemperatureProperty());
//			label2.textProperty().bind(channelSCTemp.channel2TemperatureProperty());
//			label3.textProperty().bind(channelSCTemp.channel3TemperatureProperty());
//			label4.textProperty().bind(channelSCTemp.channel4TemperatureProperty());
//		} else {
//			label1.textProperty().bind(channelAECTemp.channel1TemperatureProperty());
//			label2.textProperty().bind(channelAECTemp.channel2TemperatureProperty());
//			label3.textProperty().bind(channelAECTemp.channel3TemperatureProperty());
//			label4.textProperty().bind(channelAECTemp.channel4TemperatureProperty());
//		}
//		
//
//		
//
//		UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
//		UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
//				currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
//				StateMachine.getCurrentUserLogin(), new Date(),
//				selectedItem + " temperature fetched [" + "CH1: " + label1.getText() + ", CH2: " + label2.getText()
//						+ ", CH3: " + label3.getText() + ", CH4: " + label4.getText() + "]");
//		uutLogbookManagement.addUUTLogBook(uutLogBookDto);
//	}

//	Edited By: SUJI
//	Change Made for Point: 42Mail:7 July status || Observations_in_testing_Teclever_Date_Updated_18Jun.xlsx)
//	Change Made on: Temperature fields are not visible for MK1
	private void setMK1Temp(String selectedItem, VBox box1, VBox box2, VBox box3, VBox box4) {
		Label label1 = (Label) box1.getChildren().get(0);
		Label label2 = (Label) box2.getChildren().get(0);
		Label label3 = (Label) box3.getChildren().get(0);
		Label label4 = (Label) box4.getChildren().get(0);

//		////System.out.println("Entred in SETMK1");

	    if (selectedItem.equalsIgnoreCase("sc")) {
	        Platform.runLater(() -> {
	            label1.textProperty().bind(Bindings.when(channelSCTemp.channel1TemperatureProperty().isNull())
	                    .then("N/A")
	                    .otherwise(channelSCTemp.channel1TemperatureProperty()));
	            box1.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelSCTemp.channel1BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelSCTemp.channel1BackgroundColorProperty()));
	        });

	        Platform.runLater(() -> {
//	        	////System.out.println("----> SC "+channelSCTemp.channel2TemperatureProperty());
	            label2.textProperty().bind(Bindings.when(channelSCTemp.channel2TemperatureProperty().isNull())
	                    .then("N/A")
	                    .otherwise(channelSCTemp.channel2TemperatureProperty()));
	            box2.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelSCTemp.channel2BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelSCTemp.channel2BackgroundColorProperty()));
	        });

	        Platform.runLater(() -> {
	            label3.textProperty().bind(Bindings.when(channelSCTemp.channel3TemperatureProperty().isNull())
	                    .then("N/A")
	                    .otherwise(channelSCTemp.channel3TemperatureProperty()));
	            box3.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelSCTemp.channel3BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelSCTemp.channel3BackgroundColorProperty()));
	        });

	        Platform.runLater(() -> {
	            label4.textProperty().bind(Bindings.when(channelSCTemp.channel4TemperatureProperty().isNull())
	                    .then("N/A")
	                    .otherwise(channelSCTemp.channel4TemperatureProperty()));
	            box4.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelSCTemp.channel4BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelSCTemp.channel4BackgroundColorProperty()));
	        });
		}  else if (selectedItem.equalsIgnoreCase("aec")) {
	        Platform.runLater(() -> {
//	        	////System.out.println("---->AEC"+channelAECTemp.channel1TemperatureProperty());
	        	 label1.textProperty().bind(Bindings.when(channelAECTemp.channel1TemperatureProperty().isNull())
                .then("N/A")
                .otherwise(channelAECTemp.channel1TemperatureProperty()));
	            //label1.textProperty().bind(channelAECTemp.channel1TemperatureProperty());
	            box1.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelAECTemp.channel1BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelAECTemp.channel1BackgroundColorProperty()));
	        });

	        Platform.runLater(() -> {
//	            label2.textProperty().bind(channelAECTemp.channel2TemperatureProperty());
	        	label2.textProperty().bind(Bindings.when(channelSCTemp.channel2TemperatureProperty().isNull())
	                    .then("N/A")
	                    .otherwise(channelAECTemp.channel2TemperatureProperty()));
	            box2.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelAECTemp.channel2BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelAECTemp.channel2BackgroundColorProperty()));
	        });

	        Platform.runLater(() -> {
//	            label3.textProperty().bind(channelAECTemp.channel3TemperatureProperty());
	        	label3.textProperty().bind(Bindings.when(channelSCTemp.channel3TemperatureProperty().isNull())
	                    .then("N/A")
	                    .otherwise(channelAECTemp.channel3TemperatureProperty()));
	            box3.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelAECTemp.channel3BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelAECTemp.channel3BackgroundColorProperty()));
	        });

	        Platform.runLater(() -> {
//	            label4.textProperty().bind(channelAECTemp.channel4TemperatureProperty());
//	        	label4.textProperty().bind(Bindings.when(channelSCTemp.channel4TemperatureProperty().isEmpty())
//	                    .then("N/A")
//	                    .otherwise(channelSCTemp.channel4TemperatureProperty()));
	        	label4.textProperty().bind(
	        		    Bindings.when(channelAECTemp.channel4TemperatureProperty().isNull())
	        		        .then("N/A")
	        		        .otherwise(channelAECTemp.channel4TemperatureProperty()));
	            box4.styleProperty().bind(Bindings.createStringBinding(() -> {
	                String color = channelAECTemp.channel4BackgroundColorProperty().get();
	                return "-fx-background-color: " + (color == null || color.isEmpty() ? "yellow" : color);
	            }, channelAECTemp.channel4BackgroundColorProperty()));
	        });
	    }
	    	
	    StateMachine.tempLastCheckedTimeProperty().addListener((obs, oldVal, newVal) -> {
			Platform.runLater(()->{
				UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
				UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
						currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
						StateMachine.getCurrentUserLogin(), new Date(),
						selectedItem + " temperature fetched [" + "CH1: " + label1.getText() + ", CH2: " + label2.getText()
								+ ", CH3: " + label3.getText() + ", CH4: " + label4.getText() + "]");
				uutLogbookManagement.addUUTLogBook(uutLogBookDto);
        	});		
		});
	}
//	Exit;
//	Point: 42

	private VBox createBottomRightMidThird() {
		VBox bottomRightMidThirdBox = new VBox();
		bottomRightMidThirdBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("DFCC power on status");
		titleLabel.getStyleClass().add("right-common-title");

		GridPane bottomRightMidThirdGridPane = new GridPane();
		bottomRightMidThirdGridPane.setVgap(5);
		bottomRightMidThirdGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(25);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

//		RowConstraints thirdRow = new RowConstraints();
//		thirdRow.setPercentHeight(50);

		bottomRightMidThirdGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn);
		bottomRightMidThirdGridPane.getRowConstraints().addAll(firstRow);

		VBox box1 = new VBox();
		box1.setAlignment(Pos.CENTER);
		Label label1 = new Label("CH-1");
		box1.getChildren().add(label1);
		box1.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("CH-2");
		box2.getChildren().add(label2);
		box2.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("CH-3");
		box3.getChildren().add(label3);
		box3.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("CH-4");
		box4.getChildren().add(label4);
		box4.getStyleClass().addAll("power-status-box", "power-status-box-off");

		powerOnStatus.channel1StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box1.getStyleClass().remove("power-status-box-off");
				box1.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box1.getStyleClass().remove("power-status-box-on");
				box1.getStyleClass().add("power-status-box-off");
			}
		});

		powerOnStatus.channel2StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box2.getStyleClass().remove("power-status-box-off");
				box2.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box2.getStyleClass().remove("power-status-box-on");
				box2.getStyleClass().add("power-status-box-off");
			}
		});

		powerOnStatus.channel3StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box3.getStyleClass().remove("power-status-box-off");
				box3.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box3.getStyleClass().remove("power-status-box-on");
				box3.getStyleClass().add("power-status-box-off");
			}
		});

		powerOnStatus.channel4StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box4.getStyleClass().remove("power-status-box-off");
				box4.getStyleClass().add("power-status-box-on");
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box4.getStyleClass().remove("power-status-box-on");
				box4.getStyleClass().add("power-status-box-off");
			}
		});

		bottomRightMidThirdGridPane.add(box1, 0, 0);
		bottomRightMidThirdGridPane.add(box2, 1, 0);
		bottomRightMidThirdGridPane.add(box3, 2, 0);
		bottomRightMidThirdGridPane.add(box4, 3, 0);

		bottomRightMidThirdBox.getChildren().addAll(titleLabel, bottomRightMidThirdGridPane);

		return bottomRightMidThirdBox;
	}

	private VBox createBottomRightMidFourth() {
		VBox bottomRightMidFourthBox = new VBox();
		bottomRightMidFourthBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("Online status");
		titleLabel.getStyleClass().add("right-common-title");

		GridPane bottomRightMidFourthGridPane = new GridPane();
		bottomRightMidFourthGridPane.setVgap(5);
		bottomRightMidFourthGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(25);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(25);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(25);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(25);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

//		RowConstraints secondRow = new RowConstraints();
//		secondRow.setPercentHeight(50);

		bottomRightMidFourthGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn,
				fourthColumn);
		bottomRightMidFourthGridPane.getRowConstraints().addAll(firstRow);

		VBox box1 = new VBox();
		box1.setAlignment(Pos.CENTER);
		Label label1 = new Label("CH-1");
		box1.getChildren().add(label1);
		box1.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box2 = new VBox();
		box2.setAlignment(Pos.CENTER);
		Label label2 = new Label("CH-2");
		box2.getChildren().add(label2);
		box2.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box3 = new VBox();
		box3.setAlignment(Pos.CENTER);
		Label label3 = new Label("CH-3");
		box3.getChildren().add(label3);
		box3.getStyleClass().addAll("power-status-box", "power-status-box-off");

		VBox box4 = new VBox();
		box4.setAlignment(Pos.CENTER);
		Label label4 = new Label("CH-4");
		box4.getChildren().add(label4);
		box4.getStyleClass().addAll("power-status-box", "power-status-box-off");

		OnlineStatus.channel1StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box1.getStyleClass().remove("power-status-box-off");
				box1.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box1.getStyleClass().remove("power-status-box-on");
				box1.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel2StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box2.getStyleClass().remove("power-status-box-off");
				box2.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box2.getStyleClass().remove("power-status-box-on");
				box2.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel3StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box3.getStyleClass().remove("power-status-box-off");
				box3.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box3.getStyleClass().remove("power-status-box-on");
				box3.getStyleClass().add("power-status-box-off");
			}
		});

		OnlineStatus.channel4StatusProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.equalsIgnoreCase("online")) {
				box4.getStyleClass().remove("power-status-box-off");
				box4.getStyleClass().add("power-status-box-on");
//				checkChannelOnlineStatus();
			} else if (newValue != null && newValue.equalsIgnoreCase("offline")) {
				box4.getStyleClass().remove("power-status-box-on");
				box4.getStyleClass().add("power-status-box-off");
			}
		});

		bottomRightMidFourthGridPane.add(box1, 0, 0);
		bottomRightMidFourthGridPane.add(box2, 1, 0);
		bottomRightMidFourthGridPane.add(box3, 2, 0);
		bottomRightMidFourthGridPane.add(box4, 3, 0);

		bottomRightMidFourthBox.getChildren().addAll(titleLabel, bottomRightMidFourthGridPane);

		return bottomRightMidFourthBox;
	}

	private void CheckToggleStatus() {

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				while (true) {
					Platform.runLater(() -> {
						OnlineStatus.setChannel1Status("online");
						OnlineStatus.setChannel2Status("online");
						OnlineStatus.setChannel3Status("online");
						OnlineStatus.setChannel4Status("online");
						// checkChannelOnlineStatus();

					});

					Thread.sleep(5000);

					Platform.runLater(() -> {
						OnlineStatus.setChannel1Status("offline");
						OnlineStatus.setChannel2Status("offline");
						OnlineStatus.setChannel3Status("offline");
						OnlineStatus.setChannel4Status("offline");
						// checkChannelOnlineStatus();
					});
					Thread.sleep(5000);
				}
			}
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();

	}

	public GridPane createRigthMidSecond() {

		rightMidSecondGridPane.setVgap(5);
		rightMidSecondGridPane.setHgap(5);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(50);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(50);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(25);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(25);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(25);
		RowConstraints fourthRow = new RowConstraints();
		fourthRow.setPercentHeight(25);

		rightMidSecondGridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
		rightMidSecondGridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow, fourthRow);

		int i = 0;
		String[] labels = { "INIT DFCC", "Power Reset", "Erase EEPROM", "Cycle Power", "Check OFP", "Check WDM",
				"Check OFP 2", "Check WDM 2" };

		macroButtonList = macroConfigurationManagement.getAllMacroButtonsByUutId(currentSessionDetails.getUutId());

//		31/07/25 Srini changes
//		StateMachine.testStateProperty().addListener((obs, oldVal, newVal) -> {
//			boolean shouldDisable = newVal == TestState.RUNNING || newVal == TestState.PAUSED;
//			for (Node node : rightMidSecondGridPane.getChildren()) {
//				if (node instanceof VBox) {
//					node.setDisable(shouldDisable);
//				}
//			}
//		});

		// Edited By: SUJI
//		Change Made for Point: 52,72,74(Mail:7 July status || Observations_in_testing_Teclever_Date_Updated_18Jun.xlsx)
//		Change Made on:During initial loading of testing window,Update Status Bar
//		BooleanBinding bothAitessLaunched = StateMachine.aitess1LaunchedProperty()
//			    .and(StateMachine.aitess2LaunchedProperty());
//
//			bothAitessLaunched.addListener((obs, oldVal, newVal) -> {
//			    ////System.out.println("Triggered: both launched? " + newVal);
//			    if (newVal) {
//			        ////System.out.println("Both AITESS1 and AITESS2 are launched.");
//			        rightMidSecondGridPane.setDisable(false);
//			    }
//			});

//		Exit;
//		Point: 52,72,74

		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 2; col++) {
				VBox box = new VBox();
				box.setAlignment(Pos.CENTER);

				Tooltip tooltip = new Tooltip(macroButtonList.get(i).getButtonName());
				Tooltip.install(box, tooltip);
				tooltip.setShowDelay(Duration.ZERO);
				tooltip.setHideDelay(Duration.ZERO);

				final int x = i;
				Label label = new Label(macroButtonList.get(i).getButtonName());
				label.setUserData(macroButtonList.get(i).getCommand());

				box.setOnMouseClicked(e -> {
					
					if (!checkAitessStatus.isBothAitessOn()) {
						
						return;
					}
					if (StateMachine.getTestState() == StateMachine.TestState.RUNNING) {
						Notifications.showWarningAlert("Please try after Current Test once completes...");
					} else {
						if (!label.getUserData().toString().equals("<NOT SET>")) {
//							if (!checkAitessStatus.isBothAitessOn()) {
//								return;
//							}
							ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
							ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
									currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
									currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(),
									new Date(),
									"clicked on " + macroButtonList.get(x).getButtonName() + " macro button");
							appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
							UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
							UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
									currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
									StateMachine.getCurrentUserLogin(), new Date(),
									"macro command " + label.getUserData().toString() + " executed");
							uutLogbookManagement.addUUTLogBook(uutLogBookDto);

							// Suji load cursor
							
							
//							aitessProcessControlManagement.WriteMacroCommandToAitess2(label.getUserData().toString());
//							StateMachine.setMacroPassing(true);
//							rightMidSecondGridPane.setDisable(true);
//							

//							StateMachine.setMacroCommand(true);

							// Edited By: SUJI
//							Change Made for Point:20,95(Mail:7-Jul-Observations_in_testing_Teclever_Date_SAT))
//							Change Made on successful macro execution pop-up message
							Task<Void> macroTask = new Task<Void>() {
								@Override
								protected Void call() throws Exception {
									// Perform the macro command in the background
									////System.out.println("Entred after Clicking");
									aitessProcessControlManagement
											.WriteMacroCommandToAitess2(label.getUserData().toString());
									StateMachine.setMacroPassing(true);
									toggleButton.setDisable(true);
									rightMidSecondGridPane.setDisable(true);
									// Optional: Sleep or simulate processing if needed
									// Thread.sleep(500);

									return null;
								}

								@Override
								protected void succeeded() {
									// This runs on the JavaFX Application Thread

								}

								@Override
								protected void failed() {
//									for point 15 not linked macro properly
								    Throwable error = getException();

								    if (error != null) {
								        // Log or print the error for debugging
								        error.printStackTrace();

								        // Example: Take decision based on exception type
								        if (error instanceof IOException) {
								        	  Notifications.showErrorAlert("Macro execution failed");
								        } else if (error instanceof TimeoutException) {
								        	  Notifications.showErrorAlert("Macro execution failed");
								        } else {
								        	  Notifications.showErrorAlert("Macro execution failed");
								        }
								    } else {
								        Notifications.showErrorAlert("Macro execution failed");
								    }

								    toggleButton.setDisable(false);
								    rightMidSecondGridPane.setDisable(false);
								}

							};

							// Run the task in a background thread
							new Thread(macroTask).start();

//							Exit;
//							Point:  20,95

						} else {
							ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
							ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(
									currentSessionDetails.getUutId(), currentSessionDetails.getDfccSerialNumber(),
									currentSessionDetails.getSessionId(), StateMachine.getCurrentUserLogin(),
									new Date(),
									"clicked on " + macroButtonList.get(x).getButtonName() + " macro button");
							appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
							Notifications.showErrorAlert("Macro Button Not Configured");
							UUTLogbookManagement uutLogbookManagement = new UUTLogbookManagement();
							UUTLogBookDto uutLogBookDto = new UUTLogBookDto(currentSessionDetails.getUutId(),
									currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
									StateMachine.getCurrentUserLogin(), new Date(),
									"no macro command found for " + macroButtonList.get(x).getButtonName());
							uutLogbookManagement.addUUTLogBook(uutLogBookDto);
						}
					}

				});

				box.getChildren().add(label);
				box.getStyleClass().add("third-conatiner-button");
				rightMidSecondGridPane.add(box, col, row);
				i++;

			}

		}

		return rightMidSecondGridPane;
	}

	private GridPane createRightBottom() {
		GridPane rightBottomGridPane = new GridPane();

		rightBottomGridPane.setVgap(5);
		rightBottomGridPane.setHgap(5);
		rightBottomGridPane
				.setStyle("-fx-background-color: white; -fx-background-radius: 5px; -fx-border-radius: 5px;");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		rightBottomGridPane.getColumnConstraints().addAll(firstColumn);
		rightBottomGridPane.getRowConstraints().addAll(firstRow);

//		statusBarVbox.setAlignment(Pos.CENTER_LEFT);
		
		statusBarVbox.setAlignment(Pos.CENTER);

		// Edited By: SUJI
//		Change Made for Point: 52&72(Mail:7 July status || Observations_in_testing_Teclever_Date_Updated_18Jun.xlsx)
//		Change Made on During initial loading of testing window,Update Status Bar
		statusBar.setWrapText(true);
		statusBar.setPadding(new Insets(10));
		
		Platform.runLater(() -> {

			StateMachine.testStateProperty().addListener((obs, oldState, newState) -> {
				if (newState == TestState.RUNNING) {
					statusBar.textProperty().bind(Bindings.createStringBinding(() -> {
						String name = StateMachine.getStatusBarRunningTestName();
						
						return (name != null && !name.isEmpty()) ? "Running Test : " + name.replace("_", " ")
								: "Status Bar";
					}, StateMachine.statusBarRunningTestNameProperty()));

					statusBarVbox.setStyle(commonStyle + "-fx-background-color: #0fbd03");
				}

			});
			statusBar.setText("Aitess is Loading...");
			statusBarVbox.setStyle(commonStyle + "-fx-background-color: #e5350e");

		});
//		Exit;
//		Point:  52&72

		statusBarVbox.setCursor(Cursor.HAND);
		statusBarVbox.getChildren().add(statusBar);

		rightBottomGridPane.add(statusBarVbox, 0, 0);

		return rightBottomGridPane;
	}

	private GridPane createBottomMidGridPane() {
		bottomGridPane.setVgap(10);

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(93);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(7);

		bottomGridPane.getColumnConstraints().addAll(firstColumn);
		bottomGridPane.getRowConstraints().addAll(firstRow, secondRow);

		showTerminalButton.setMaxWidth(Double.MAX_VALUE);
		showTerminalButton.setMaxHeight(Double.MAX_VALUE);
		showTerminalButton.getStyleClass().add("show-terminal-button");

		showTerminalButton.setOnAction(e -> {

			ApplicationLogbookManagement appLogbookManagement = new ApplicationLogbookManagement();
			ApplicationLogBookDto applicationLogBookDto = new ApplicationLogBookDto(currentSessionDetails.getUutId(),
					currentSessionDetails.getDfccSerialNumber(), currentSessionDetails.getSessionId(),
					StateMachine.getCurrentUserLogin(), new Date(), "clicked on SHOW TERMINAL button");
			appLogbookManagement.addApplicationLogBook(applicationLogBookDto);
			centerContentController.createUserCenterContent(bottomMidTopGridPane, "Show Terminal", null, null);
		});

		bottomGridPane.add(createBottomMidContentArea(), 0, 0);
		bottomGridPane.add(createTerminalContainer(), 0, 1);

		return bottomGridPane;
	}

	private GridPane createTerminalContainer() {
		terminalGridPane.getStyleClass().add("terminal-bottom-container");
		terminaTextField.getStyleClass().add("terminal-input");
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(71);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(8);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(8);
		ColumnConstraints fourthColumn = new ColumnConstraints();
		fourthColumn.setPercentWidth(8);
		ColumnConstraints fifthColumn = new ColumnConstraints();
		fifthColumn.setPercentWidth(5);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		terminalGridPane.setHgap(20);
		terminalGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn, fourthColumn,
				fifthColumn);
		terminalGridPane.getRowConstraints().addAll(firstRow);

		terminaTextField.setDisable(true);
		enterButton.setDisable(true);
		yesButton.setDisable(true);
		noButton.setDisable(true);

		Image minImage = new Image(DFCCConstant.JARSTRING + "/Resources/Images/up-arrow1.png");
		minImageView = new ImageView(minImage);
		minImageView.getStyleClass().add("terminal-image");

		minImageView.setFitWidth(50);
		minImageView.setFitHeight(50);

		minImageView.setOnMouseClicked((MouseEvent event) -> {

			handleOpenTerminal();
		});

		StateMachine.userActionFlagProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				Platform.runLater(() -> {
					handleOpenTerminal();
					StateMachine.setDissableEnable(true);
					StateMachine.getUserActionFlag().set(false);
				});
			}
		});

		terminalGridPane.add(terminaTextField, 0, 0);
		terminalGridPane.add(enterButton, 1, 0);
		terminalGridPane.add(yesButton, 2, 0);
		terminalGridPane.add(noButton, 3, 0);
		terminalGridPane.add(minImageView, 4, 0);

		return terminalGridPane;
	}

//	private void handleOpenTerminal() {
//		bottomMainGridPane.setOpacity(0.5);
//
//		StackPane parentStackPane = (StackPane) bottomMainGridPane.getParent();
//		if (!parentStackPane.getChildren().contains(terminalStackPane)) {
//			parentStackPane.getChildren().add(terminalStackPane);
//		}
//
//		Platform.runLater(() -> {
//			animateNewStackPaneToFront();
//		});
//	}

	private void handleOpenTerminal() {
		Platform.runLater(() -> {
			bottomMainGridPane.setOpacity(0.5);

			StackPane parentStackPane = (StackPane) bottomMainGridPane.getParent();
			if (!parentStackPane.getChildren().contains(terminalStackPane)) {
				parentStackPane.getChildren().add(terminalStackPane);
			}

			animateNewStackPaneToFront();
		});
	}

//	public void animateNewStackPaneToFront() {
//	    terminalStackPane.toFront();
//	    
//	    FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), terminalStackPane);
//	    fadeTransition.setFromValue(0); 
//	    fadeTransition.setToValue(1); 
//	    
//	    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500), terminalStackPane);
//	    translateTransition.setFromY(terminalStackPane.getHeight());
//	    translateTransition.setToY(0);
//	    
//	    ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, translateTransition);
//	    
//	    parallelTransition.play();
//	}

//	public void animateNewStackPaneToFront() {
//	    terminalStackPane.toFront();
//	    GridPane newGridPane = (GridPane) terminalStackPane.getChildren().get(0);
//	    GridPane newGridPane1 = (GridPane) newGridPane.getChildren().get(0);
//	    GridPane newGridPane2 = (GridPane) newGridPane1.getChildren().get(0);
//	    
////	    FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), newGridPane2);
////	    fadeTransition.setFromValue(0); 
////	    fadeTransition.setToValue(1); 
////	    fadeTransition.setInterpolator(Interpolator.EASE_IN);
////	    
////	    fadeTransition.play();
//	    
////	    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), newGridPane2);
////	    translateTransition.setFromY(newGridPane2.getHeight());
////	    translateTransition.setToY(0);
////	    
////	    translateTransition.play();
//	    	    
//	}

	public void animateNewStackPaneToFront() {
		terminalStackPane.toFront();
		GridPane newGridPane = (GridPane) terminalStackPane.getChildren().get(0);
		GridPane newGridPane1 = (GridPane) newGridPane.getChildren().get(0);
		GridPane newGridPane2 = (GridPane) newGridPane1.getChildren().get(0);

		TextArea newTextArea = (TextArea) newGridPane2.getChildren().get(0);
		if (newTextArea instanceof TextArea) {
			newTextArea.setScrollTop(Double.MAX_VALUE);
		}

		newGridPane2.setScaleY(0.0);

		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), newGridPane2);
		scaleTransition.setFromY(0.0);
		scaleTransition.setToY(1.0);

		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), newGridPane2);
		translateTransition.setFromY(newGridPane2.getHeight() / 2);
		translateTransition.setToY(0);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), newGridPane2);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);

		ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, translateTransition,
				fadeTransition);
		parallelTransition.play();
	}

	private GridPane createBottomMidContentArea() {

		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(100);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		bottomMidTopGridPane.getColumnConstraints().addAll(firstColumn);
		bottomMidTopGridPane.getRowConstraints().addAll(firstRow);

		bottomMidTopGridPane.getStyleClass().add("center-container");
		Platform.runLater(() -> {
			
			 DashboardController dashboardController = new DashboardController();
			    
			    // Clear previous content
			    
			
			
			if (!UserData.getRoleId().equals("RL_ID_4")) {
			bottomMidTopGridPane.getChildren().add(dashboardController.createDashboardMainContainerGridPane());
			}else {
				// Clear previous children
				bottomMidTopGridPane.getChildren().clear();
				Platform.runLater(() -> {
				// Load image from classpath
				InputStream stream = getClass().getResourceAsStream(DFCCConstant.JARSTRING+"/Resources/Images/Reggie2.png");
			

				if (stream == null) {
				    //System.out.println("❌ Image not found. Check path and Resources folder!");
				    return;
				}
				
				
				Image image = new Image(stream);
				ImageView imageView = new ImageView(image);

				imageView.setPreserveRatio(true);

				// Bind image size to GridPane size
				imageView.fitWidthProperty().bind(bottomMidTopGridPane.widthProperty());
				imageView.fitHeightProperty().bind(bottomMidTopGridPane.heightProperty());

				
				    bottomMidTopGridPane.getChildren().add(imageView);
				    GridPane.setValignment(imageView, VPos.BOTTOM);  // Move to bottom
				    GridPane.setHalignment(imageView, HPos.CENTER);  // Center horizontally
				});
			}
			});

		return bottomMidTopGridPane;

	}

	private void setupStatusColumn(TableColumn<CheckSumList, String> statusColumn) {
		statusColumn.setReorderable(false);
		statusColumn.setSortable(false);
		statusColumn.setCellFactory(column -> new TableCell<CheckSumList, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					if ("OK".equalsIgnoreCase(item)) {
						setText("Passed");
						setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
					} else if ("NOT OK".equalsIgnoreCase(item)) {
						setText("Failed");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					} else if ("No File".equalsIgnoreCase(item)) {
						setText("No File");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					} else if ("Extra File".equalsIgnoreCase(item)) {
						setText("Extra File");
						checkSumFinalResult = false;
						setStyle("-fx-background-color: #fa9898;-fx-alignment: CENTER;");
					}
				}
			}
		});
	}

	private TableView<CheckSumList> createTableView(ObservableList<CheckSumList> checkSumTableData) {
		TableView<CheckSumList> tableView = new TableView<>();
		tableView.getStyleClass().add("check-sum-table");
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tableView.setPrefHeight(900);

		TableColumn<CheckSumList, String> fileNameColumn = new TableColumn<>("File Name");
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setReorderable(false);
		fileNameColumn.setSortable(false);
		fileNameColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> checkSumValueColumn = new TableColumn<>("CheckSum Value");
		checkSumValueColumn.setCellValueFactory(new PropertyValueFactory<>("checkSumValue"));
		checkSumValueColumn.setReorderable(false);
		checkSumValueColumn.setSortable(false);
		checkSumValueColumn.setStyle("-fx-alignment: CENTER;");

		TableColumn<CheckSumList, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		setupStatusColumn(statusColumn);

		tableView.getColumns().addAll(fileNameColumn, checkSumValueColumn, statusColumn);
		tableView.setItems(checkSumTableData);

		return tableView;
	}

	private VBox createCheckSumDataBox(ObservableList<CheckSumList> checkSumTableData) {
		VBox checkSumDataBox = new VBox(10);
		checkSumDataBox.setAlignment(Pos.CENTER);
		checkSumDataBox.getStyleClass().add("check-sum-data-box");
		checkSumDataBox.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/LoginForm.css").toExternalForm());

		HBox checkSumTitleBox = new HBox();
		checkSumTitleBox.setAlignment(Pos.CENTER);
		Label checkSumLabel = new Label("CheckSum Details");
		checkSumLabel.getStyleClass().add("check-sum-title");
		checkSumTitleBox.getChildren().add(checkSumLabel);

		TableView<CheckSumList> tableView = createTableView(checkSumTableData);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		Button okButton = new Button("OK");
		okButton.getStyleClass().add("check-sum-ok-btn");
		buttonBox.getChildren().add(okButton);

		okButton.setOnAction(e -> {
			for (CheckSumList checksum : checkSumTableData) {
				if (!"ok".equalsIgnoreCase(checksum.getStatus())) {
					checkSumFinalResult = false;
				}
			}
			if (checkSumFinalResult) {
				Parent parent = checkSumDataBox.getParent();
				if (parent instanceof GridPane) {
					GridPane parent1 = (GridPane) parent.getParent();
					parent1.getChildren().remove(parent);
				}
			} else {
				Platform.exit();
			}
		});

		checkSumDataBox.getChildren().addAll(checkSumTitleBox, tableView, buttonBox);

		return checkSumDataBox;
	}

	public GridPane getCheckSumDataUserLogin() {

		if (DFCCConstant.isJarBuild) {

			ValidateResponse response = checksumManagement.compare();

			ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

			if (response.getResponse().getResponseCode() == 0) {
				Notifications.showErrorAlert(response.getResponse().getResponseMessage());
				Platform.exit();
			} else if (response.getResponse().getResponseCode() == 1) {
				for (CheckSum data : response.getCheckSumList()) {
					CheckSumList checkSumUiDto = new CheckSumList();
					checkSumUiDto.setFileName(data.getFile());
					checkSumUiDto.setCheckSumValue(data.getChecksumValue());
					checkSumUiDto.setStatus(data.getMsg());
					checkSumTableData.add(checkSumUiDto);
				}
			}

			GridPane newGridPane = new GridPane();
			newGridPane.setStyle(
					"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
			setupGridPane(newGridPane);

			VBox checkSumDataBox = createCheckSumDataBox(checkSumTableData);

			newGridPane.add(checkSumDataBox, 1, 1);

			return newGridPane;

		} else {
			ValidateResponse checkSumData = systemConfigManagement.validateConfig();
			List<CheckSum> checkSumDataList = checkSumData.getCheckSumList();
			ObservableList<CheckSumList> checkSumTableData = FXCollections.observableArrayList();

			if (checkSumData.getResponse().getResponseCode() == 0) {
				Notifications.showErrorAlert(checkSumData.getResponse().getResponseMessage());
				Platform.exit();
			} else if (checkSumData.getResponse().getResponseCode() == 1) {
				for (CheckSum data : checkSumDataList) {
					CheckSumList checkSumUiDto = new CheckSumList();
					checkSumUiDto.setFileName(data.getFile());
					checkSumUiDto.setCheckSumValue(data.getChecksumValue());
					checkSumUiDto.setStatus(data.getMsg());
					checkSumTableData.add(checkSumUiDto);
				}
			}

			GridPane newGridPane = new GridPane();
			newGridPane.setStyle(
					"-fx-background-color: rgba(0, 0, 0, 0.7);-fx-border-radius: 10px;-fx-background-radius: 10px;");
			setupGridPane(newGridPane);

			VBox checkSumDataBox = createCheckSumDataBox(checkSumTableData);

			newGridPane.add(checkSumDataBox, 1, 1);

			return newGridPane;

		}

	}

	private void setupGridPane(GridPane gridPane) {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(2);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(96);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(2);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(5);
		RowConstraints secondRow = new RowConstraints();
		secondRow.setPercentHeight(90);
		RowConstraints thirdRow = new RowConstraints();
		thirdRow.setPercentHeight(5);

		gridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		gridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
	}

	class ToggleSwitch extends HBox {

		private final Label label = new Label();
		private final Button button = new Button();

		private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);

		public SimpleBooleanProperty switchOnProperty() {
			return switchedOn;
		}

		private void init() {

			label.setText("OFF");

			getChildren().addAll(label, button);
			button.setOnAction((e) -> {
				switchedOn.set(!switchedOn.get());
			});
			label.setOnMouseClicked((e) -> {
				switchedOn.set(!switchedOn.get());
			});
			setStyle();
			bindProperties();
		}

		private void setStyle() {
			// Default Width
			setWidth(50);
			label.setAlignment(Pos.CENTER);
			label.setStyle("-fx-background-color: red; -fx-text-fill:black; -fx-background-radius: 10px;");
			setAlignment(Pos.CENTER_LEFT);
		}

		private void bindProperties() {
			label.prefWidthProperty().bind(widthProperty().divide(2));
			label.prefHeightProperty().bind(heightProperty());
			button.prefWidthProperty().bind(widthProperty().divide(2));
			button.prefHeightProperty().bind(heightProperty());
		}

		public ToggleSwitch() {
			init();
			switchedOn.addListener((a, b, c) -> {
				if (c) {
					label.setText("ON");
					setStyle("-fx-background-color: green;");
					label.toFront();
				} else {
					label.setText("OFF");
					setStyle("-fx-background-color: red;");
					button.toFront();
				}
			});
		}
	}
}