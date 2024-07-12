package com.teclever.dfcc;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;

import com.itextpdf.text.DocumentException;
import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.reportgeneration.ReportGeneration;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main
extends Application {
    private static final String LOCK_FILE_PATH = ".app_lock";
    private static FileChannel fileChannel;
    private static FileLock lock;
    private static boolean lockAcquired;

    static {
        lockAcquired = true;
    }

    public static void main(String[] args) throws MalformedURLException, DocumentException, IOException {
        System.out.println("Hello World!");
        String driverClass = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/dfcc";
        String username = "root";
        String password = "root";
        String dialect = "org.hibernate.dialect.MySQLDialect";
        String hbm2ddlAuto = "update";
        String showSql = "false";
        DataStoreConfiguration dataStore = new DataStoreConfiguration(driverClass, url, username, password, dialect, hbm2ddlAuto, showSql);
      //  ReportGeneration reportGeneration = new ReportGeneration();
      //  reportGeneration.generateDetailedReport(null, null);
        System.out.println("Before the Result Management Execution--");
        ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
        resultExecutionManagement.getResultExecutionDetailedListForStages("SASN00003");
        resultExecutionManagement.getResultExecutionListBriefListForStages("SASN00003");
		if (DFCCConstant.isJarBuild) {
			DFCCConstant.JARSTRING = "/src";
		}
//      // Define the file path
//      String filePath = "C:\\Users\\anujk\\Downloads\\load";
//
//      // Create a BlockingQueue and load data from the file
//      BlockingQueue<String> queue = new ArrayBlockingQueue<>(10000);
//
//      try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//          String line;
//          while ((line = br.readLine()) != null) {
//              queue.add(line);
//          }
//          // Adding a termination condition for the while loop in parseQueue method
//          queue.add("END");
//      } catch (IOException e) {
//          e.printStackTrace();
//      }
//
//      // Create an instance of DriverManagement
//      DriverManagement driverManagement = new DriverManagement();
//
//      // Call the validateDriverCard method with the data from the queue and a sample aitessId
//      int aitessId = 1; // Replace with a valid aitessId
//      DriverCardDetailsResponse response = driverManagement.validateDriverCard(queue, aitessId);
//
//      // Print the results
//      for (DriverCard driverCard : response.getDriverCardDetails()) {
//          System.out.println("***********************");
//          System.out.println("Card Name: " + driverCard.getCardName());
//          System.out.println("Expected Count of Cards: " + driverCard.getExpectedCountOfCards());
//          System.out.println("Found Number of Cards: " + driverCard.getFoundedNumberOfCards());
//          System.out.println("Message: " + driverCard.getMsg());
//      }
//
//      // Print the overall response message
//      System.out.println("Response Message: " + response.getResponse().getResponseMessage());
  
      
      
//      DriverManagement dm = new DriverManagement();
//      DriverCardDetailsResponse d = dm.validateDriverCard( "C:\\Users\\anujk\\Downloads\\load", 1);
      
//		CardDetailsService cd = new CardDetailsService();
//		String t = " ##NUM## CCDL Cards found";
// String c = cd.getCardNameByIdentificationText(t);
// System.out.println(c+"---");
      
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

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (!lockAcquired) {
            return;
        }
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/fxml/MainWindow.fxml"));
        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        double width = resolution.getWidth();
        double height = resolution.getHeight();
        double w = width / 1920.0;
        double h = height / 1080.0;
        Scale scale = new Scale(w, h, 0.0, 0.0);
        root.getTransforms().add(scale);
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(DFCCConstant.JARSTRING+"/Resources/Images/DFCC-Logo.png")));
        primaryStage.setMaximized(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getStylesheets().add(this.getClass().getResource(DFCCConstant.JARSTRING+"/com/teclever/dfcc/ui/css/MainWindow.css").toExternalForm());
        AitessConfigurationManagement configManager = new AitessConfigurationManagement();
        UUTMasterDetailsDto[] uutDataList = configManager.getAllUUT();
        HashMap<String, String> idNameMap = new HashMap<String, String>();
        HashMap<String, String> nameIdMap = new HashMap<String, String>();
        UUTMasterDetailsDto[] uUTMasterDetailsDtoArray = uutDataList;
        int n = uutDataList.length;
        int n2 = 0;
        while (n2 < n) {
            UUTMasterDetailsDto uutType = uUTMasterDetailsDtoArray[n2];
//            System.out.println("UUT Type--------   " + uutType.getUutType());
            nameIdMap.put(uutType.getUutType(), uutType.getUutId());
            idNameMap.put(uutType.getUutId(), uutType.getUutType());
            ++n2;
        }
        DFCCConstant.setUutIdNameMap(idNameMap);
        DFCCConstant.setUutNameIdMap(nameIdMap);
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
}