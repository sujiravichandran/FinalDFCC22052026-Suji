package com.teclever.dfcc;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.itextpdf.text.DocumentException;
import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;
import com.teclever.dfcc.datastore.dto.LogOutFileCopyResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SymbolFileManagement;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;
import com.teclever.dfcc.datastore.logbookmanagement.ApplicationLogbookManagement;
import com.teclever.dfcc.datastore.processcontrolmanagement.AitessProcessControlManagement;
import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.resultstore.resultmanagement.StepParser;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	private static final String LOCK_FILE_PATH = ".app_lock";
	private static FileChannel fileChannel;
	private static FileLock lock;
	private static boolean lockAcquired;

	static {
		lockAcquired = true;
	}
	static String currentDirectory = new File(
			SystemConfigManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	public static void main(String[] args) throws MalformedURLException, DocumentException, IOException, ParseException, InterruptedException {
		String driverClass = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/dfcc";
		String username = "root";
		String password = "root";
		String dialect = "org.hibernate.dialect.MySQLDialect";
		String hbm2ddlAuto = "update";
		String showSql = "false";
		new DataStoreConfiguration(driverClass, url, username, password, dialect, hbm2ddlAuto, showSql);

		if (DFCCConstant.isJarBuild) {
			DFCCConstant.JARSTRING = "/src";
			SessionFileManagement.deleteAitesslogFiles();
		}
		
		
		
//		List<StepDto> lst = new ArrayList<>();
//		StepParser.parseStepContextNEW("C:\\Users\\anujk\\Downloads\\New folder (4)\\pwrauto_mk1a_GP2.rdf;01");
//		for(StepDto s:lst)
//		{
//			System.out.println( s.getFaultySRU());
//		}
		
//		 Response response = new Response();
//		 RunPathMasterService runPathMasterService = new RunPathMasterService();
//		 response = runPathMasterService. getPathLocationByRunConfigId( "RUN101", "rdf");
//		System.out.println(response.getResponseMessage());
//		List<StepDto> lst = new ArrayList<>();
//		lst = StepParser.parseStepContextNEW("C:\\Users\\anujk\\Downloads\\New folder (4)\\pwrauto_mk1a_GP2.rdf;01");
//		for(StepDto s:lst)
//		{
//			
//			System.out.println( s.getFaultySRU());
//		}
		
	
//		SessionManagement sessionManagement = new SessionManagement();
//		Map<String,String> stageIdFullPath = new HashMap<String,String>();
//		stageIdFullPath =	sessionManagement.getStageIdFullPath();
//		//System.out.println(stageIdFullPath);
//		ResultExecutionManagement res = new ResultExecutionManagement();
//		
//		ResultDetailedResponse re = new ResultDetailedResponse();
//		re =res.getResultExecutionDetailedListForStagesFromMysql("UUT2",null,null,null);
//		//System.out.println(re.getResultDetailedList().size());
//		
//		SummaryResult smr = new SummaryResult();
//		smr.generateHistoryResultForSession("SASN00010");
//		ReportGeneration rg = new ReportGeneration();
//		try {
//			rg.generateDetailedReportESSPQTSession("SASN00018");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	

//		SessionTestingController sessionTestingController = new SessionTestingController();
//		sessionTestingController.createTreeView();

		// COM FILES
//		String sessionId = "SASN00014";
		
		// COM FILES
//		String sessionId = "SASN00014";
//		List<StepDto> lst = new ArrayList<StepDto>();
//		 currentSessionDetails.setUutId("UUT2");
//		try {
//			lst = StepParser.parseStepContextNEW("C:\\Users\\anujk\\OneDrive\\Desktop\\pwrauto_ess_mod.rdf;00");
//			for(StepDto x:lst)
//			{
//				if(x.getStep().equals("1091"))
//				{
//					System.out.println("Faulty SRU ::"+x.getFaultySRU());
//				}
//			}
//			//System.out.println("List Size"+lst.size());
//			
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//
//		try {
//			RdfFileDetailsParser.saveProjectDetailsToMongoDB("SASN00137","C:\\\\Users\\\\sharn\\\\Downloads\\\\1553_brcst_ess.rdf;00" );
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		List<CopyFileDTO> lst = new ArrayList<CopyFileDTO>();
//		
//		CopyFileDTO copy1  = new CopyFileDTO();
//		copy1.setStageId("L1_001");
//		copy1.setStagePath("C:\\LatestFX_anuj\\BEL-DFCC-new\\BEL-DFCC-new\\target\\MK-1\\1160 000 395 75\\MK-1_2334_Mod-A_TESTEND_25-10-2024 11 19 16\\LRU Test\\Mandatory Test\\PBIT Test");
//		lst.add(copy1);
//		
//		CopyFileDTO copy2 = new CopyFileDTO();
//		copy1.setStageId("L1_002");
//		copy1.setStagePath("C:\\LatestFX_anuj\\BEL-DFCC-new\\BEL-DFCC-new\\target\\MK-1\\1160 000 395 75\\MK-1_2334_Mod-A_TESTEND_25-10-2024 11 19 16\\LRU Test\\Mandatory Test\\SPIL LINK");
//		lst.add(copy2);
//		
//		
//		SessionFileManagement session = new SessionFileManagement();
//		session.copyFilesToOutputFolderWhilePlayButton(lst);


		SymbolFileManagement symbolFileManagement = new SymbolFileManagement();
//
//		MacroFileManagement macroFileManagement = new MacroFileManagement();
//		String path1 = "C:\\\\Users\\\\sharn\\\\Desktop\\\\SymbolsCombine\\";
//		
//		String path = "C:\\Users\\anujk\\OneDrive\\Desktop\\Symbols\\1553_ttr.sym";
//
//		List<String> fileName = Arrays.asList(path);
//
//		List<String> paths = Arrays.asList(path);

//		List<SymbolDto> result2 = symbolFileManagement.saveSymbolsForCustomFiles(paths, "Path315");

//		Macro checking
//		String path1 = "C:\\Users\\sharn\\Downloads\\macros-20251229T064050Z-1-001\\macros\\combined.mac";
//		String path = "C:\\Users\\sharn\\Downloads\\macros-20251229T064050Z-1-001\\macros\\1553bbrdcst.mac";
//		List<String> fileName = Arrays.asList(path, path1);
//
//		List<String> paths = Arrays.asList(path);
//
//		MacroFileManagement macroFileManagement = new MacroFileManagement();
//		List<MacroDto> result3 = macroFileManagement.saveMacroNamesForCustomFiles(fileName, "RUN066");
//		////System.out.println("Result2 = " + result2.size());

		Main.launch(args);

	}

	@Override
	public void init() {
		try {
			File lockFile = new File(LOCK_FILE_PATH);
			if (!lockFile.exists()) {
				lockFile.createNewFile();
			}
			if ((lock = (fileChannel = new RandomAccessFile(lockFile, "rw").getChannel()).tryLock()) == null) {
				lockAcquired = false;
				this.showErrorAndExit("Application is already running.");
			}
		} catch (Exception e) {
			lockAcquired = false;
			this.showErrorAndExit("Error encountered while checking application instance.");
		}
	}

	private void showErrorAndExit(String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.setOnCloseRequest(evt -> Platform.exit());
			alert.show();
		});
	}
//Before Changing on :29-04-2025
//	@Override
//	public void start(Stage primaryStage) throws IOException {
//		if (!lockAcquired) {
//			return;
//		}
//		Parent root = (Parent) FXMLLoader.load(
//				this.getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/MainWindow.fxml"));
//		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
//		double width = resolution.getWidth();
//		double height = resolution.getHeight();
//		double w = width / 1920.0;
//		double h = height / 1080.0;
//		Scale scale = new Scale(w, h, 0.0, 0.0);
//		root.getTransforms().add(scale);
//		Scene scene = new Scene(root);
//		primaryStage.getIcons().add(
//				new Image(getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/DFCC-Logo.png")));
//		primaryStage.setMaximized(true);
//		primaryStage.initStyle(StageStyle.UNDECORATED);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//		scene.getStylesheets().add(this.getClass()
//				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/MainWindow.css").toExternalForm());
//		AitessConfigurationManagement configManager = new AitessConfigurationManagement();
//		UUTMasterDetailsDto[] uutDataList = configManager.getAllUUT();
//		HashMap<String, String> idNameMap = new HashMap<String, String>();
//		HashMap<String, String> nameIdMap = new HashMap<String, String>();
//		UUTMasterDetailsDto[] uUTMasterDetailsDtoArray = uutDataList;
//		int n = uutDataList.length;
//		int n2 = 0;
//		while (n2 < n) {
//			UUTMasterDetailsDto uutType = uUTMasterDetailsDtoArray[n2];
////            Debug.printDebug("UUT Type--------   " + uutType.getUutType());
//			nameIdMap.put(uutType.getUutType(), uutType.getUutId());
//			idNameMap.put(uutType.getUutId(), uutType.getUutType());
//			++n2;
//		}
//		DFCCConstant.setUutIdNameMap(idNameMap);
//		DFCCConstant.setUutNameIdMap(nameIdMap);
//
//		 String isDebugValue = getIsDebugValueFromFile();
//	        
//	        if (isDebugValue != null) {
//
//	        	if (isDebugValue.equalsIgnoreCase("true")) {
//	            	DFCCConstant.setDebug(true);
//	            }
//	        } else {
//	            ////System.out.println(" dfcc.set File Not Present ");
//	        }
//		
//			////System.out.println("DEBUG Mode :: " +DFCCConstant.isDebug +"  :   "+ (DFCCConstant.isDebug ? "Active" : "Inactive"));
//
//		// Report Temp Files
//		if (DFCCConstant.isJarBuild) {
//			SessionFileManagement sessionFileManagement = new SessionFileManagement();
//			String reportDirectory = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
//					.getParent() + File.separator + "Reports";
//			sessionFileManagement.deleteAllFilesInDirectory(reportDirectory);
//		}
//
//	}


	// After Changing on :29-04-2025

	@Override
	public void start(Stage primaryStage) throws IOException {
		if (!lockAcquired) {
			return;
		}
		Parent root = (Parent) FXMLLoader.load(
				this.getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/MainWindow.fxml"));
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		double width = resolution.getWidth();
		double height = resolution.getHeight();
		double w = width / 1920.0;
		double h = height / 1080.0;
		Scale scale = new Scale(w, h, 0.0, 0.0);
		root.getTransforms().add(scale);
		Scene scene = new Scene(root);
		primaryStage.getIcons().add(
				new Image(getClass().getResourceAsStream(DFCCConstant.JARSTRING + "/Resources/Images/LOGONEW.png")));
		primaryStage.setMaximized(true);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setTitle("DFCC TESTING AND DATA HANDLING SOFTWARE");
		primaryStage.setScene(scene);
		primaryStage.show();
		// Add your CSS
		scene.getStylesheets().add(this.getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/MainWindow.css").toExternalForm());
		// ==== NEW: Handle close button action ====
		primaryStage.setOnCloseRequest(event -> {
			// chnage06112025
			event.consume();
//	        ////System.out.println("User attempted to close the application.");
			Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
			alert1.setTitle("Exit Confirmation");
			alert1.setHeaderText("Are you sure you want to exit?");
			alert1.setContentText("Unsaved changes will be lost.");
			ButtonType result = alert1.showAndWait().orElse(ButtonType.CANCEL);
			if (result != ButtonType.OK) {
				return;
			} else {

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
								SessionFileManagement session = new SessionFileManagement();
								LogOutFileCopyResponse response = session
										.copyingFileWhileLogOut(StateMachine.currentSessionDetails.getSessionId());
//								//System.out.println("SUji Log oUt raesponse code check:::" + response.getCode());
								if (response.getCode() == 1) {
//									aitessProcessControlManagement.endAllProcessOnLogout();
									Platform.exit();
									System.exit(0);
								} else if (response.getCode() == 0) {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Error Dialog");
									alert.setHeaderText(null);
									alert.setContentText("Something went wrong! The application will now close.");

									alert.setOnCloseRequest(event1 -> {
										aitessProcessControlManagement.endAllProcessOnLogout();
										Platform.exit();
										System.exit(0);
									});

									alert.showAndWait();
								} else if (response.getCode() == 100) {
									SessionTestStateObject.isLogoutFileCopyPopupOpenedProperty()
											.addListener((observable, oldValue, newValue) -> {
												if (!newValue) {
													aitessProcessControlManagement.endAllProcessOnLogout();
													Platform.exit();
													System.exit(0);
												}
											});
									SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(true);
									SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);
								}else {
									Platform.runLater(() -> {
										Platform.exit();
										System.exit(0);
									});
								}
						
							});
				} else if (StateMachine.getTestState() == TestState.PAUSED
						|| StateMachine.getTestState() == TestState.RUNNING) {
					Notifications.showWarningAlert("Please stop " + StateMachine.getRunningTestName()
							+ " test before log out and close the application");
				} else {
					Platform.runLater(() -> {
						Platform.exit();
						System.exit(0);
					});
				}
				// User confirmed exit - perform any cleanup if needed
//	            ////System.out.println("Application is closing...");
				// (Optional) Save data, close connections, etc.
			}
		});
		// =========================================
		// Your existing UUT and configuration code
		AitessConfigurationManagement configManager = new AitessConfigurationManagement();
		UUTMasterDetailsDto[] uutDataList = configManager.getAllUUT();
		HashMap<String, String> idNameMap = new HashMap<>();
		HashMap<String, String> nameIdMap = new HashMap<>();
		UUTMasterDetailsDto[] uUTMasterDetailsDtoArray = uutDataList;
		int n = uutDataList.length;
		int n2 = 0;
		while (n2 < n) {
			UUTMasterDetailsDto uutType = uUTMasterDetailsDtoArray[n2];
			nameIdMap.put(uutType.getUutType(), uutType.getUutId());
			idNameMap.put(uutType.getUutId(), uutType.getUutType());
			++n2;
		}
		DFCCConstant.setUutIdNameMap(idNameMap);
		DFCCConstant.setUutNameIdMap(nameIdMap);
		String isDebugValue = getIsDebugValueFromFile();
		if (isDebugValue != null) {
			if (isDebugValue.equalsIgnoreCase("true")) {
				DFCCConstant.setDebug(true);
			}
		} else {
			////System.out.println("dfcc.set File Not Present");
		}
//	    ////System.out.println("DEBUG Mode :: " + DFCCConstant.isDebug + "  :   " + (DFCCConstant.isDebug ? "Active" : "Inactive"));
		// Report Temp Files
		if (DFCCConstant.isJarBuild) {
			SessionFileManagement sessionFileManagement = new SessionFileManagement();
			String reportDirectory = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
					.getParent() + File.separator + "Reports";
			sessionFileManagement.deleteAllFilesInDirectory(reportDirectory);
		}
	}

	@Override
	public void stop() {
		try {
			if (lock != null && fileChannel != null) {
				lock.release();
				fileChannel.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getIsDebugValueFromFile() {
		String isDebugValue = null;
		Properties properties = new Properties();
		// Get the current parent directory path
		File file = new File(currentDirectory + File.separator + "dfcc.set");

		// Check if the file exists
		if (!file.exists()) {
//			////System.out.println("dfcc.set File Not Exist "+(currentDirectory + File.separator + "dfcc.set"));
			return isDebugValue;
		}
		try (FileInputStream input = new FileInputStream(file)) {
			properties.load(input);
			isDebugValue = properties.getProperty("isdebug");
//            ////System.out.println("Is Debug: " + isDebugValue);
		} catch (IOException ex) {
			////System.out.println("Error: Could not load configuration from " + file.getAbsolutePath());
		}
//		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//			String line;
//            while ((line = reader.readLine()) != null) {
//                // Find line containing "isdebug" and extract its value
//                if (line.contains("isdebug")) {
//                    // Assuming the line is in the format: isdebug="true" or isdebug=true
//                    String[] parts = line.split("=");
//                    if (parts.length == 2) {
//                        isDebugValue = parts[1].replaceAll("\"", "").trim(); // Remove quotes and trim
//                    }
//                    break;
//                }
//            }
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		// Return false if "isdebug = true" is not found or any other condition is met
		return isDebugValue;
	}
}