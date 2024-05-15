package com.teclever.dfcc;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.teclever.datastore.configuration.DataStoreConfiguration;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	private static final String LOCK_FILE_PATH = ".app_lock";
	private static FileChannel fileChannel;
	private static FileLock lock;
	private static boolean lockAcquired = true; // Indicate if the lock was acquired successfully.

	public static void main(String[] args) {
		System.out.println("Hello World!");
		String driverClass = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/dfcc";
		String username = "root";
		String password = "root";
		String dialect = "org.hibernate.dialect.MySQLDialect";
		String hbm2ddlAuto = "update";
		String showSql = "true";
		DataStoreConfiguration dataStore = new DataStoreConfiguration(driverClass, url, username, password, dialect,
				hbm2ddlAuto, showSql);


//    	// Get the location of the class file (inside the JAR)
//        String classFilePath = JarFilePathExample.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//        // Convert the URL to a File object
//        File classFile = new File(classFilePath);
//        // Get the absolute path of the class file
//        String absolutePath = classFile.getAbsolutePath();
//        // Get the parent directory of the JAR file
//        String jarParentDirectory = classFile.getParent();
//        // Get the name of the JAR file
//        String jarFileName = classFile.getName();
//        // Display the results
//        System.out.println("Absolute Path: " + absolutePath);
//        System.out.println("arent Directory:" + jarParentDirectory);
//        System.out.println("JAR File Name:" + jarFileName);
		launch(args);

	}

	@SuppressWarnings("resource")
	@Override
	public void init() {
		try {
			File lockFile = new File(LOCK_FILE_PATH);
			if (!lockFile.exists()) {
				lockFile.createNewFile();
			}
			fileChannel = new RandomAccessFile(lockFile, "rw").getChannel();
			lock = fileChannel.tryLock();

			if (lock == null) {
				lockAcquired = false; // set to false if the lock couldn't be acquired
				showErrorAndExit("Application is already running.");
			}
		} catch (Exception e) {
			lockAcquired = false; // set to false in case of any exception
			showErrorAndExit("Error encountered while checking application instance.");
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
//		try {
//            Font font = Font.loadFont(new FileInputStream("fonts/NotoSans-Regular.ttf"), 12);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
		if (!lockAcquired) {
			return; // Prevent the start method from setting up the scene and stage.
		}
//		Parent root = FXMLLoader.load(getClass().getResource("/com/teclever/dfcc/ui/fxml/Login5.fxml"));
		Parent root = FXMLLoader.load(getClass().getResource("/com/teclever/dfcc/ui/fxml/MainWindow.fxml"));
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		double width = resolution.getWidth();
		double height = resolution.getHeight();
		double w = width / 1920; // your window width
		double h = height / 1080; // your window height
		Scale scale = new Scale(w, h, 0, 0);
		root.getTransforms().add(scale);
		

//root.setStyle("-fx-font: 14px 'Noto Sans', Bold;");

		Scene scene = new Scene(root);
		primaryStage.setMaximized(true);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
		primaryStage.show();
		scene.getStylesheets().add(getClass().getResource("/com/teclever/dfcc/ui/css/MainWindow.css").toExternalForm());
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


//package com.teclever.dfcc;
//
//
//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.stage.Stage;
//
//public class Main extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        // Create a StackPane to hold all elements
//        StackPane root = new StackPane();
//
//        // Set background color to grey
////        Rectangle bg = new Rectangle(400,400);
////        bg.setFill(Color.GREY);
////        root.getChildren().add(bg);
////
////        // Create a white box
////        Rectangle whiteBox = new Rectangle(200,200);
////        whiteBox.setFill(Color.WHITE);
////        whiteBox.setWidth(0.75 * primaryStage.getWidth());
////        whiteBox.setHeight(0.75 * primaryStage.getHeight());
////        root.getChildren().add(whiteBox);
//
//        // Center the white box
////        StackPane.setAlignment(whiteBox, Pos.CENTER);
//
//               
//    	ImageView imageView = new ImageView();
//        Image image = new Image("/Resources/Images/TejasBGImage.png"); 
//        imageView.setImage(image);
//
//        // Make the image cover the entire screen
//        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
//        imageView.fitHeightProperty().bind(primaryStage.heightProperty());
//
//        // Add the image view to the stack pane
//        root.getChildren().add(imageView);
//
//        
//        
//        GridPane gridPane = new GridPane();
//        gridPane.setHgap(10);
//        gridPane.setVgap(10);
//        gridPane.setAlignment(Pos.CENTER);
//        gridPane.setStyle("-fx-background-color: transparent;");
//
//        // Add content to the grid view
//        // For example:
//         gridPane.add(new Label("Item 1"), 0, 0);
//         gridPane.add(new Label("Item 2"), 1, 0);
//
//        // Add the grid view to the stack pane
//        root.getChildren().add(gridPane);
//
//        // Create the scene and set it on the stage
//        Scene scene = new Scene(root, Color.BLACK);
//        primaryStage.setScene(scene);
//
//        // Set stage to full screen
//        primaryStage.setFullScreen(true);
//
//        // Show the stage
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}

