package com.teclever.dfcc.Controller.ui;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.testmanagement.TestManagerManagement;
import com.teclever.utils.ProcessControl;

import javafx.application.Platform;
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
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
	private static BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(1000000);

	ProcessControl loadDriverProcessController;

	private static final String[][] ANSI_TO_HTML_COLOR_MAP = { { "30", "black" }, { "31", "red" }, { "32", "green" },
			{ "33", "yellow" }, { "34", "blue" }, { "35", "magenta" }, { "36", "cyan" }, { "37", "white" },
			{ "90", "gray" }, { "91", "lightred" }, { "92", "lightgreen" }, { "93", "lightyellow" },
			{ "94", "lightblue" }, { "95", "lightmagenta" }, { "96", "lightcyan" }, { "97", "lightwhite" } };
	private static final Pattern UNNECESSARY_ANSI_PATTERN = Pattern
			.compile("\u001B\\[\\?1049[hl]|" + "\u001B\\[22;0;0t|" + "\u001B\\[1;24r|" + "\u001B\\[\\?12l|"
					+ "\u001B\\[\\?25h|" + "\u001B\\]104|" + "\u001B\\(B|" + "\u001B\\[4l|" + "\u001B\\[H|"
					+ "\u001B\\[2J|" + "\u001B\\[8;38H|" + "\u001B\\[11;28H|" + "\u001B\\[16d|" + "\u001B\\[15;41H|"
					+ "\u001B\\[13;33H|" + "\u001B\\[24d|" + "\u001B\\[K|" + "\u001B\\[\\?1049l|" + "\u001B\\[23;0;0t|"
					+ "\u001B\\[\\?1l|" + "\u001B>" + "\u001B\\[?7h|" + "\u001B\\[\\?25l|" + "\u001B\\[\\d+;\\d+[Hh]");

	private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[([;\\d]*)m");

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
		webEngine.loadContent(
				"<html><body style='background-color:black; color:white; font-family:monospace;'></body></html>");
		webEngine.documentProperty().addListener((observable, oldDoc, newDoc) -> {

			if (newDoc != null) {
				webEngine.executeScript("window.scrollBy({ top: 1000, left: 0, behavior: 'smooth' });");
				webEngine.executeScript("setInterval(function() { window.scrollBy(0, 20); }, 5);");
			}
		});
		System.out.println("HANDLER 1 anuj");
		handler1 = new ProcessControl(queue);
		loadDriverProcessController = new ProcessControl(loadDriverBQueue);


	}

	@FXML
	void onClickEnter(ActionEvent event) {

		String inputCommand = terminalTextField.getText() + "\n";
		if (!processLaunched) {
			CompletableFuture<Void> launcherFuture1 = new CompletableFuture<>();
			handler1.LaunchingProcess(inputCommand, launcherFuture1);
			System.out.println("On Click Enter");
			launcherFuture1.thenRun(() -> {
				// JUN-03
				// handler1.ReadingProcess(null,true,webEngine);
				handler1.ReadingProcess();


//    	 String inputText = terminalTextField.getText().trim();
//         if (!inputText.isEmpty()) {
//             appendTextToWebView(inputText + "\n");
//             terminalTextField.clear(); 
//         }
		
				new Thread(() -> {
					try {
						while (true) {
							String output = queue.take();
							System.out.println(output);
							final String htmlContent = ansiToHtml(output);
							Platform.runLater(() -> {
								String safeOutput = htmlContent.replace("\\", "\\\\").replace("'", "\\'")
										.replace("\n", "\\n").replace("\r", "\\r");
								webEngine.executeScript("document.body.innerHTML += '" + safeOutput + "';");
							});
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}).start();
				//handler1.ReadingProcess(webEngine);

			});

//			new Thread(() -> {
//					while (true) {

			CompletableFuture<Void> launcherFuture2 = new CompletableFuture<>();
			System.out.println("OK1 Before Launching Process");
			loadDriverProcessController.LaunchingProcess("cd /home/teclever/Documents/load_data" + "\n" + "ll" + "\n",
					launcherFuture2);

			launcherFuture2.thenRun(() -> {
				// JUN-03
				// handler1.ReadingProcess(null,true,webEngine);
				loadDriverProcessController.ReadingProcess();
				new Thread(() -> {
					try {
						while (true) {
//									String cleanText;
							String output = loadDriverBQueue.take();
							System.out.println("MANI:::");
							System.out.println(output);
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}).start();
			});
			processLaunched = true;

		} else {
			handler1.WritingProcess(inputCommand);
			System.out.println("Calling preLoad Drive KMAN:::");

//			handler1.WritingProcess("cd /home/teclever/Documents/load_data");
//			System.out.println("Calling Path Command:::");

			/* ____________________________________________________ */

//					}
			System.out.println("OK1 after Launching Process");
			loadDriverProcessController.WritingProcess("cat loadDriverOutput.txt" + "\n");

//			}).start();

			/*
			 * ______________________________________________________________________________
			 */
//			TestManagerManagement tm = new TestManagerManagement();
//			tm.preLoadDriver(webEngine);
		}
		terminalTextField.clear();
	}

	private String ansiToHtml(String text) {
		text = text.replaceAll("\u001B\\[\\?7h", "");
		text = UNNECESSARY_ANSI_PATTERN.matcher(text).replaceAll("");
		StringBuilder htmlText = new StringBuilder();
		int lastEnd = 0;
		Matcher matcher = ANSI_PATTERN.matcher(text);
		while (matcher.find()) {
			String codes = matcher.group(1);
			String[] codeArray = codes.split(";");

			htmlText.append(text, lastEnd, matcher.start());

			StringBuilder style = new StringBuilder();
			for (String code : codeArray) {
				for (String[] colorMap : ANSI_TO_HTML_COLOR_MAP) {
					if (code.equals(colorMap[0])) {
						style.append("color:").append(colorMap[1]).append(";");
					}
				}
				if (code.equals("1")) {
					style.append("font-weight:bold;");
				} else if (code.equals("4")) {
					style.append("text-decoration:none;");
				} else if (code.equals("0")) {
					style.append("</span>");
				}
			}

			if (style.length() > 0 && !style.toString().equals("</span>")) {
				htmlText.append("<span style=\"").append(style).append("\">");
			} else if (style.toString().equals("</span>")) {
				htmlText.append(style);
			}

			lastEnd = matcher.end();
		}

		htmlText.append(text.substring(lastEnd));
		if (htmlText.indexOf("<span") != -1 && htmlText.lastIndexOf("</span>") < htmlText.lastIndexOf("<span")) {
			htmlText.append("</span>");
		}

		String finalHtmlText = htmlText.toString().replaceAll("\n", "<br>");
		return finalHtmlText;
	}

	@FXML
	void onClickMinimize(ActionEvent event) {
		Stage stage = (Stage) terminalPopupMainContainer.getScene().getWindow();
		stage.hide();

	}

}
