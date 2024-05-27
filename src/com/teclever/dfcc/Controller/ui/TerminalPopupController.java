package com.teclever.dfcc.Controller.ui;

import java.util.concurrent.CompletableFuture;

import com.teclever.utils.ProcessControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class TerminalPopupController {

	@FXML
	private Button enterButton;

	@FXML
	private Label headerLabel;

	@FXML
	private Button minimizeButton;

	@FXML
	private GridPane terminalOperationsGridPane;

	@FXML
	private AnchorPane terminalPopupMainContainer;

	@FXML
	private TextField terminalTextField;

	@FXML
	private VBox terminalVBox;
	@FXML
	private WebView terminalWebView;
	private WebEngine webEngine;
	private boolean processLaunched = false;
	private ProcessControl handler1;

	@FXML
	void initialize() {
		ColumnConstraints firstColumn = new ColumnConstraints();
		firstColumn.setPercentWidth(82);
		ColumnConstraints secondColumn = new ColumnConstraints();
		secondColumn.setPercentWidth(9);
		ColumnConstraints thirdColumn = new ColumnConstraints();
		thirdColumn.setPercentWidth(9);

		RowConstraints firstRow = new RowConstraints();
		firstRow.setPercentHeight(100);

		terminalOperationsGridPane.getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);
		terminalOperationsGridPane.getRowConstraints().addAll(firstRow);
		webEngine = terminalWebView.getEngine();
		webEngine.loadContent("<html><body style='background-color:black; color:white; font-family:monospace;'></body></html>");

		handler1 = new ProcessControl();
		terminalTextField.requestFocus();

	}

//    public void startBackgroundThread() {
//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                for (int i = 0; i < 500; i++) {
//                    final String text = "Appended Text " + i + "\n";
//                    System.out.println("Thread: "+i);
//                    Platform.runLater(() -> appendTextToWebView(text));
//                    Thread.sleep(500); 
//                }
//                return null;
//            }
//        };
//        Thread backgroundThread = new Thread(task);
//        backgroundThread.setDaemon(true); 
//        backgroundThread.start();
//    }

	private void appendTextToWebView(String text) {

		String currentContent = (String) webEngine.executeScript("document.body.innerText");
		String updatedContent = currentContent + text;
		webEngine.loadContent(
				"<html><body contenteditable='true' style='background-color:black; color:white; font-family:monospace;'>"
						+ updatedContent + "</body></html>");
		webEngine.executeScript("window.scrollTo(0, document.body.scrollHeight);");
	}

	@FXML
	void onClickEnter(ActionEvent event) {
//    	 String inputText = terminalTextField.getText().trim();
//         if (!inputText.isEmpty()) {
//             appendTextToWebView(inputText + "\n");
//             terminalTextField.clear(); 
//         }

		String inputCommand = terminalTextField.getText() + "\n";
		if (!processLaunched) {
			CompletableFuture<Void> launcherFuture1 = new CompletableFuture<>();
			handler1.LaunchingProcess(inputCommand, launcherFuture1);
			launcherFuture1.thenRun(() -> {
				handler1.ReadingProcess(webEngine);
			});
			processLaunched = true;
		} else {
			handler1.WritingProcess(inputCommand);
		}
		terminalTextField.clear();
	}

	@FXML
	void onClickMinimize(ActionEvent event) {
		handler1.WritingProcess("exit\n");
		handler1.WritingProcess("exit\n");
		handler1.stopProcesses();
		Stage stage = (Stage) terminalPopupMainContainer.getScene().getWindow();
		stage.hide();

	}

}
