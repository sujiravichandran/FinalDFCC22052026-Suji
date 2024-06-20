package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.utils.ProcessControl;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class AitessProcessControlManagement {
	
	private static AitessProcessControlManagement instance;

	private static String homeLocation = "home/bel/desktop/"; // user.home
	private static String configHomeLocation = "home/bel/desktop/config.dat"; // user.home+/config.dat
	private static String startupUserFileLocation = "home/bel/downloads/startup.user";
	private static String cacheFilePath = "home/bel/desktop/.cache"; // home location + .cache

	private BlockingQueue<String> aitess1ReadQ = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> aitess1ResultQ = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> aitess2ReadQ = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> aitess2ResultQ = new ArrayBlockingQueue<>(10000);

	private ProcessControl aitess1ProcessControl;
	private ProcessControl aitess2ProcessControl;

	CompletableFuture<Void> launcherFuture1 = new CompletableFuture<>();
	CompletableFuture<Void> launcherFuture2 = new CompletableFuture<>();

	private Thread outputProcessingThread1;
	private Thread outputProcessingThread2;
	private Thread performTestThread;

	private boolean testStarted = false;

	boolean flag;
	

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

	private AitessProcessControlManagement() {
		aitess1ProcessControl = new ProcessControl(aitess1ReadQ);
		aitess2ProcessControl = new ProcessControl(aitess2ReadQ);
	}

	public static synchronized AitessProcessControlManagement getInstance() {
		if (instance == null) {
			instance = new AitessProcessControlManagement();
		}
		return instance;
	}
	
	
	private void configureAitess(String configFileLocation) {
		// COPY CONFIG FILE
		copyFile(configFileLocation, configHomeLocation);

		// COPY STARTUP.USER FILE
		copyFile(startupUserFileLocation, homeLocation);

		// DELETE .CACHE FILE
		deleteCacheFile(cacheFilePath);

	}

	public void launchAitess(String testTypeId, TextArea textArea) {
			
		
		// FIND RUN CONFIG FROM TEST TYPE ID AND UUT ID
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		// get uutId from STATE MACHINE
		String uutId = StateMachine.currentSessionDetails.getUutId();

		// get currentRunConfigId based on uutId and testTypeId
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		// get current Aitess Details from DB in object
		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);

		// calling configureAitess()
		configureAitess(currentAitess.getConfigFile());

		if(!StateMachine.isAitess1Launched())
		launchAitess1("sudo "+currentAitess.getAitessCommand()+"\n", textArea);

		if(!StateMachine.isAitess2Launched())
		launchAitess2("sudo "+currentAitess.getAitessCommand()+"\n");

	}

	private void launchAitess1(String command, TextArea textArea) {
		final String oldString[] = new String[1];
		System.out.println("Entering Launch Aitess 1");
		aitess1ProcessControl.LaunchingProcess(command, launcherFuture1);
		launcherFuture1.thenRun(() -> {
			aitess1ProcessControl.ReadingProcess();
			outputProcessingThread1 = new Thread(() -> {
				try {
					String s1;
					String s2 = null;
					String cleanText;
					String result = null;
					
					while (true) {
						s2 = s1 = aitess1ReadQ.take();
//						s1= aitess1ReadQ.take();
//						System.out.println("s1 :: " + s1);
						
						cleanText = cleanOutput(s1);
						cleanText = cleanText.replaceAll("\\(B", "");
						cleanText = cleanText.replaceAll("]104", "");
						
						final String cleanContent = cleanText;
						final String newString = cleanContent;

						Platform.runLater(() -> {
						    if (oldString[0] != null) {
						    	if (!newString.equals(oldString[0])) {
//						    		System.out.println("----new Text---"+cleanContent);
//									System.out.println("-------------------------------------------------");
//									System.out.println();
//									System.out.println("--Before Appending TextArea--"+textArea.getText());
									textArea.appendText(cleanContent);
									textArea.requestFocus();
									textArea.setScrollTop(Double.MAX_VALUE);
//									System.out.println("--After Appending TextArea--"+textArea.getText());
//									System.out.println();
//									System.out.println("-------------------------------------------------");
						        }
						    } else if(oldString[0] == null) {
//						    	System.out.println("-----firstTime----");
						        textArea.appendText(newString);
						        textArea.requestFocus();
						        textArea.setScrollTop(Double.MAX_VALUE);
						    }
							oldString[0] = newString;
						});

						


						String userActionLine = getUserActionLine(s1);
						if(userActionLine!=null) {
							StateMachine.setUserAction(userActionLine);
						}
						
						// condition if test stared
						if (testStarted) {
//							System.out.println("s2 :: " + s2);
							cleanText = cleanOutput(s2);
							cleanText = cleanText.replaceAll("\\(B", "");
							cleanText = cleanText.replaceAll("]104", "");

							final String finalLine = cleanText;

							// getting rdf File Name
							if (getRdfFileName(finalLine) != null) {
								result = getRdfFileName(finalLine);
							}

							String endLine = getEndMatchingLine(finalLine);
							if (endLine != null) {
								if (result != null) {
									aitess1ResultQ.put(result);
								}
							}

						}
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			});
			outputProcessingThread1.start();
		});
		StateMachine.setAitess1Launched(true);

	}

	private void launchAitess2(String command) {
		System.out.println("Entering Launch Aitess 2");
		aitess2ProcessControl.LaunchingProcess(command, launcherFuture2);
		launcherFuture2.thenRun(() -> {
			aitess2ProcessControl.ReadingProcess();
			outputProcessingThread2 = new Thread(() -> {
				try {
					String cleanText;
					while (true) {
						String output = aitess2ReadQ.take();
						System.out.println("aitess2 :: " + output);
						cleanText = cleanOutput(output);
						cleanText = cleanText.replaceAll("\\(B", "");
						cleanText = cleanText.replaceAll("]104", "");
						final String finalLine = cleanText;
						aitess2ResultQ.put(finalLine);

					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			});
			outputProcessingThread2.start();
		});
		StateMachine.setAitess2Launched(true);

	}

	public String performTest(String tpfFileName) {

		String rdfFileName = null;
		try {
			testStarted = true;
//			long startTime = System.currentTimeMillis();
//	        long timeout = 10 * 60 * 1000; 
			launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess("@ " + tpfFileName + "\n"));
			boolean flag = true;
			while (flag) {
				if (aitess1ResultQ != null&& aitess1ResultQ.peek() != null) {

					rdfFileName = aitess1ResultQ.take();
					flag = false;
				}
//				 if (System.currentTimeMillis() - startTime > timeout) {
//		                System.out.println("Timeout reached. Exiting the loop.");
//		                flag = false;
//		            }
//				  Thread.sleep(1000);
			}

			testStarted = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rdfFileName;

	}
	
	public void WriteAitess1Command(String command) {
		launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess(command));
	}

	public void WriteAitess2Command(String command) {
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(command));
		// pending

	}

	public void switchAitess(String testTypeId) {
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		// get uutId from STATE MACHINE
		String uutId = "UUT1";

		// get currentRunConfigId based on uutId and testTypeId
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		// get current Aitess Details from DB in object
		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);

		// calling configureAitess()
		configureAitess(currentAitess.getConfigFile());

		// switch Aitess1
		launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess("exit" + "\n"));
		launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess(currentAitess.getAitessCommand() + "\n"));

		// switch Aitess2
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess("exit" + "\n"));
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(currentAitess.getAitessCommand() + "\n"));

	}

	private String getEndMatchingLine(String line) {
		Pattern tpfLinePattern = Pattern.compile("Execution of TPF '([^']+)' completed\\.");
		Matcher tpfLineMatcher = tpfLinePattern.matcher(line);

		if (tpfLineMatcher.find()) {
			System.out.println("END LINE:: " + line);
			return line;
		}
		return null; 
	}

	private String getRdfFileName(String line) {
		Pattern rdfFileNamePattern = Pattern.compile("Running TPF '.*?' and generating RDF '([^']+)'.");
		Matcher rdfFileNameMatcher = rdfFileNamePattern.matcher(line);

		if (rdfFileNameMatcher.find()) {
			String rdfFileName = rdfFileNameMatcher.group(1);
			System.out.println("RDF NAME FROM TERMINAL :: " + rdfFileName);
			return rdfFileName;
		}
		return null;
	}
	
	private String getUserActionLine(String line) {
	    Pattern userActionPattern = Pattern.compile("User Action .* \\(Y/N\\):");
	    Matcher userActionMatcher = userActionPattern.matcher(line);

	    if (userActionMatcher.find()) {
	    	System.out.println("USER ACTION LINE :: " + line);
	        return line; 
	    }
	    return null; 
	}


	private String cleanOutput(String output) {
		String regex1 = "\u001B\\[[;\\d]*[A-Za-z]|\\[\\??\\d*[A-Za-z]|\\u0007|\\u0008|"
				+ "\\u0009|\\u000B|\\u000C|\\u000D|\\u000E|\\u000F | \\p{Cntrl}|\\u001B\\(B | \\p{Cntrl}";
		Pattern pattern1 = Pattern.compile(regex1);
		Matcher matcher1 = pattern1.matcher(output);
		return matcher1.replaceAll("");
	}

	private void copyFile(String sourcePath, String destinationPath) {
		try {
			Files.copy(Paths.get(sourcePath), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Config file copied from " + sourcePath + " to " + destinationPath);
		} catch (IOException e) {
			System.err.println("An error occurred while copying the config file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void deleteCacheFile(String cacheFilePath) {
		try {
			Path path = Paths.get(cacheFilePath);
			if (Files.exists(path)) {
				Files.delete(path);
				System.out.println("Cache file deleted from " + cacheFilePath);
			} else {
				System.out.println("Cache file does not exist at " + cacheFilePath);
			}
		} catch (IOException e) {
			System.err.println("An error occurred while deleting the cache file: " + e.getMessage());
			e.printStackTrace();
		}
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

}
