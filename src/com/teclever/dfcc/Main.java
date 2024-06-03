package com.teclever.dfcc;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.configurationmanagement.StageConfiguration;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;

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

    public static void main(String[] args) {
        System.out.println("Hello World!");
        String driverClass = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/dfcc";
        String username = "root";
        String password = "root";
        String dialect = "org.hibernate.dialect.MySQLDialect";
        String hbm2ddlAuto = "update";
        String showSql = "true";
        DataStoreConfiguration dataStore = new DataStoreConfiguration(driverClass, url, username, password, dialect, hbm2ddlAuto, showSql);
        
      
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