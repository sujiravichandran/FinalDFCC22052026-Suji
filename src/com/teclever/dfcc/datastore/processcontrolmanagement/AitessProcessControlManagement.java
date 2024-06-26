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
import com.teclever.dfcc.Controller.ui.LoadDriverController;
import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.terminalmanagement.ChannelStatusParser;
import com.teclever.dfcc.datastore.terminalmanagement.TemperatureParser;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp.aitessRunning;
import com.teclever.dfcc.stateMachine.StateMachine.channelTemp;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;
import com.teclever.utils.ProcessControl;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class AitessProcessControlManagement {

	private static AitessProcessControlManagement instance;

	private Path homeLocation;
	private Path aitessDir;
	private Path aitess1Dir;
	private Path aitessConfigFile;
	private Path aitess1ConfigFile;
	private Path aitessStartupUserFile;
	private Path aitess1StartupUserFile;

	private Path startupUserFile;

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
	private Thread dfccCheckStatusThread;

	private boolean testStarted = false;
	private String currentCommand = "";

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
		try {
			Files.copy(Paths.get(configFileLocation), aitessConfigFile, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(startupUserFile, aitessStartupUserFile, StandardCopyOption.REPLACE_EXISTING);
			Files.deleteIfExists(aitessDir.resolve("config.cache"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void configureAitess1(String configFileLocation) {
		try {
			Files.copy(Paths.get(configFileLocation), aitess1ConfigFile, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(startupUserFile, aitess1StartupUserFile, StandardCopyOption.REPLACE_EXISTING);
			Files.deleteIfExists(aitess1Dir.resolve("config.cache"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void launchAitess(String testTypeId, TextArea textArea) {

		RunConfigurationService runConfigurationService = new RunConfigurationService();
		
		String uutId = StateMachine.currentSessionDetails.getUutId();
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);

		try {
			// Get the username from the system property or environment variable
			String username = System.getProperty("user.name");
			if (username == null || username.isEmpty() || "root".equals(username)) {
				username = System.getenv("SUDO_USER");
			}
			if (username == null || username.isEmpty()) {
				throw new IllegalStateException("Failed to retrieve the username.");
			}

			// Define the home location with the username
			homeLocation = Paths.get("/home", username);
			System.out.println("Home location: " + homeLocation);

			// Create aitess and aitess1 folders
			aitessDir = homeLocation.resolve("aitess");
			aitess1Dir = homeLocation.resolve("aitess1");

			// Create directories if they do not exist
			if (Files.notExists(aitessDir)) {
				Files.createDirectory(aitessDir);
			}
			if (Files.notExists(aitess1Dir)) {
				Files.createDirectory(aitess1Dir);
			}

			// Set aitessConfigFile and aitess1ConfigFile globally
			aitessConfigFile = aitessDir.resolve("config.dat");
			aitess1ConfigFile = aitess1Dir.resolve("config.dat");

			// Copy config.dat to the respective folders
			Path configFile = Paths.get(currentAitess.getConfigFile());

			Path configFileParentPath = Paths.get(currentAitess.getConfigFile()).getParent();

			if (Files.exists(configFile)) {
				Files.copy(configFile, aitessConfigFile, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(configFile, aitess1ConfigFile, StandardCopyOption.REPLACE_EXISTING);
			}

			startupUserFile = configFileParentPath.resolve("startup.user");

			// Set startupuserfile for both aitess globally
			aitessStartupUserFile = aitessDir.resolve("startup.user");
			aitess1StartupUserFile = aitess1Dir.resolve("startup.user");

			if (Files.exists(startupUserFile)) {
				Files.copy(startupUserFile, aitessStartupUserFile, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(startupUserFile, aitess1StartupUserFile, StandardCopyOption.REPLACE_EXISTING);
			}

			// delete config.cache file if it exists
			Files.deleteIfExists(aitessDir.resolve("config.cache"));
			Files.deleteIfExists(aitess1Dir.resolve("config.cache"));

			// launch aitess1 inside aitess folder
			if (!StateMachine.isAitess1Launched()) {
				launchAitess1("cd " + aitessDir.toString() + "\n", textArea);
				launcherFuture1.thenRun(
						() -> aitess1ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));
			}

			// launch aitess2 inside aitess1 folder
			if (!StateMachine.isAitess2Launched()) {
				launchAitess2("cd " + aitess1Dir.toString() + "\n");
				launcherFuture2.thenRun(
						() -> aitess2ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));
			}

			System.out.println("currentRunConfig-->>" + currentRunConfigId);
			currentSessionDetails.setRunConfigId(currentRunConfigId);
		} catch (IOException e) {
			e.printStackTrace();
			textArea.appendText("Failed to create directories or copy config.dat file.\n");
			System.out.println("1");
		} catch (IllegalStateException e) {
			e.printStackTrace();
			textArea.appendText("Failed to retrieve the username.\n");
			System.out.println("2");
		}
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
					String parserErrorResult = null;

					while (true) {
						s2 = s1 = aitess1ReadQ.take();
//						System.out.println("s1 :: " + s1);

						if (!aitessRunning.isAitess1Exited()) {
							aitessRunning.setAitess1Exited(true);
						}
						
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
							} else if (oldString[0] == null) {
//						    	System.out.println("-----firstTime----");
								textArea.appendText(newString);
								textArea.requestFocus();
								textArea.setScrollTop(Double.MAX_VALUE);
							}
							oldString[0] = newString;
						});

						String userActionLine = getUserActionLine(s1);
						if (userActionLine != null) {
							StateMachine.setUserAction(userActionLine);
						}

						// condition if test stared
						if (testStarted) {
//							System.out.println("s2 :: " + s2);
							cleanText = cleanOutput(s2);
							cleanText = cleanText.replaceAll("\\(B", "");
							cleanText = cleanText.replaceAll("]104", "");

							final String finalLine = cleanText;
							
							
							if (getParseErrorLine(finalLine) != null) {
								parserErrorResult = getParseErrorLine(finalLine);
							}
							
							if (parserErrorResult != null) {
								aitess1ResultQ.put(parserErrorResult);
							}

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
		ChannelStatusParser channelStatusParser = new ChannelStatusParser();
		TemperatureParser temperatureParser = new TemperatureParser();
		aitess2ProcessControl.LaunchingProcess(command, launcherFuture2);
		launcherFuture2.thenRun(() -> {
			aitess2ProcessControl.ReadingProcess();
			outputProcessingThread2 = new Thread(() -> {
				try {
					ChannelStatus channelStatus = new ChannelStatus();
					ChannelTemperature channelTemperature = new ChannelTemperature();

					String cleanText;

					while (true) {
						String output = aitess2ReadQ.take();
						System.out.println("aitess2 :: " + output);
						cleanText = cleanOutput(output);
						cleanText = cleanText.replaceAll("\\(B", "");
						cleanText = cleanText.replaceAll("]104", "");
						final String finalLine = cleanText;
//						aitess2ResultQ.put(finalLine);

						if (!aitessRunning.isAitess2Exited()) {
							aitessRunning.setAitess2Exited(true);
						}

						
						switch (currentCommand) {
						case "OnlineStatusCommand":
							channelStatus = channelStatusParser.getChannelStatus(finalLine);

							if (channelStatus != null) {
								OnlineStatus.setChannel1Status(channelStatus.getChannel1());
								OnlineStatus.setChannel2Status(channelStatus.getChannel2());
								OnlineStatus.setChannel3Status(channelStatus.getChannel3());
								OnlineStatus.setChannel4Status(channelStatus.getChannel4());
							}
							break;

						case "DfccPowerOnCommand":

							break;
						case "DfccPowerOffCommand":

							break;
						case "Mk1ScTemperatureCommand":
							channelTemperature = temperatureParser.getChannelTemperature(finalLine);

							if (channelTemperature != null) {
								channelTemp.setChannel1Temperature(channelTemperature.getChannel1Temp());
								channelTemp.setChannel2Temperature(channelTemperature.getChannel2Temp());
								channelTemp.setChannel3Temperature(channelTemperature.getChannel3Temp());
								channelTemp.setChannel4Temperature(channelTemperature.getChannel4Temp());
							}
							break;

						case "Mk1AecTemperatureCommand":
							channelTemperature = temperatureParser.getChannelTemperature(finalLine);

							if (channelTemperature != null) {
								channelTemp.setChannel1Temperature(channelTemperature.getChannel1Temp());
								channelTemp.setChannel2Temperature(channelTemperature.getChannel2Temp());
								channelTemp.setChannel3Temperature(channelTemperature.getChannel3Temp());
								channelTemp.setChannel4Temperature(channelTemperature.getChannel4Temp());
							}
							break;

						default:
							break;
						}

						// directly here we can store into STATE MACHINE why need of Blocking Queue
						// ..??????????
						// any one needed
//							aitess2ResultQ.put(channel1Status);
//							aitess2ResultQ.put(channel2Status);
//							aitess2ResultQ.put(channel3Status);
//							aitess2ResultQ.put(channel4Status);

//							OnlineStatus.setChannel1Status(channel1Status);
//							OnlineStatus.setChannel2Status(channel2Status);
//							OnlineStatus.setChannel3Status(channel3Status);
//							OnlineStatus.setChannel4Status(channel4Status);

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
		String parseErrorResponse = null;
		try {
			testStarted = true;
//			long startTime = System.currentTimeMillis();
//	        long timeout = 10 * 60 * 1000; 
			launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess("@ " + tpfFileName + "\n"));
			boolean flag = true;
			while (flag) {
				if (aitess1ResultQ != null && aitess1ResultQ.peek() != null) {
					rdfFileName = aitess1ResultQ.take();
					if (rdfFileName.equals("PARSE ERROR")) {
						rdfFileName = null;
					}
					flag = false;
				}

//				if(aitess1ErrorQ !=null && aitess1ErrorQ.peek() != null) {
//					parseErrorResponse = aitess1ErrorQ.take();
//					flag = false;
//				}
//				 if (System.currentTimeMillis() - startTime > timeout) {
//		                System.out.println("Timeout reached. Exiting the loop.");
//		                flag = false;
//		            }
//				  Thread.sleep(1000);
			}

			testStarted = false;
			System.out.println("Perform Test() Return : "+rdfFileName );

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rdfFileName;

	}

	public void WriteAitess1Command(String command) {
		launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess(command));
	}

	public void WriteDfccPowerOnCommandToAitess2() {
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getDfccPowerOnCommand() + "\n"));
		currentCommand = "DfccPowerOnCommand";
		dfccCheckStatus.setDfccPowerStatus(true);
	}
	
	public void WriteDfccPowerOffCommandToAitess2() {
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getDfccPowerOffCommand() + "\n"));
		currentCommand = "DfccPowerOffCommand";
		dfccCheckStatus.setDfccPowerStatus(false);
	}
	
	
	
	public void WriteAitess2Command(String command) {

		// pending
		dfccCheckStatusThread = new Thread(() -> {

			//once or multiple ..????????
			launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOnlineStatusCommand() + "\n"));
			currentCommand = "OnlineStatusCommand";

			launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getMk1ScTemperatureCommand() + "\n"));
			currentCommand = "Mk1ScTemperatureCommand";

			launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getMk1AecTemperatureCommand() + "\n"));
			currentCommand = "Mk1AecTemperatureCommand";

			launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(command + "\n"));

		});
		dfccCheckStatusThread.start();

	}

	public void check(String testTypeId) {
		RunConfigurationService runConfigurationService = new RunConfigurationService();
		LoadDriverProcessControlManagement pcm = LoadDriverProcessControlManagement.getInstance();

		String smRunConfigId = currentSessionDetails.getRunConfigId();
		AitessConfigurationDetails smAitess = runConfigurationService.getAitessDetailsByRunConfigId(smRunConfigId);

		String uutId = currentSessionDetails.getUutId();
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);

		// CHECKING DRIVER
		if (!smAitess.getDriverName().equals(currentAitess.getDriverName())) {
			// NOT MATCHED
			System.out.println(
					"Switching Load Driver::---- " + smAitess.getDriverName() + " to ::---- " + currentAitess.getDriverName());
			pcm.loadDriver(currentAitess.getLoadDriverCommand(), smAitess.getUnloadDriverCommand(), 0,
					LoadDriverProcessControlManagement.LoadMode.SWITCH);
			if(aitessRunning.isAitess1Exited()==true && aitessRunning.isAitess2Exited()==true) {
			switchAitess(testTypeId);
			}
		} else {
			// MATCHED
			System.out.println(
					"Load Driver Matches::---- " + smAitess.getDriverName() + " == " + currentAitess.getDriverName());
			
			// CHECKING AITESS
			if (!smAitess.getAitessName().equals(currentAitess.getAitessName())) {
				// NOT MATCHED
				System.out.println("Aitess Not Matched:: OLD AITESS:---- " + smAitess.getAitessName() + " NEW AITESS:---- "
						+ currentAitess.getAitessName());

				aitessRunning.setAitess1Exited(false);
				exitAitess1Command();
				aitessRunning.setAitess2Exited(false);
				exitAitess2Command();
				
				if(aitessRunning.isAitess1Exited()==true && aitessRunning.isAitess2Exited()==true) {
				switchAitess(testTypeId);
				}

			} else {
				System.out.println("Aitess Matches::---- " + smAitess.getAitessName() + " == " + currentAitess.getAitessName());
				if(!smAitess.getConfigFile().equals(currentAitess.getConfigFile())) {
					try {
						Files.copy(Paths.get(currentAitess.getConfigFile()), aitessConfigFile, StandardCopyOption.REPLACE_EXISTING);
						Files.copy(Paths.get(currentAitess.getConfigFile()), aitess1ConfigFile, StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}	
		}

		
		System.out.println("FUNCTION ENDED----------------------->>>>>>>>>>>>>>>>>>");
	}

	public void switchAitess(String testTypeId) {
		System.out.println("Entering into Switching AITESS");
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		String uutId = currentSessionDetails.getUutId();
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);

		configureAitess(currentAitess.getConfigFile());
		configureAitess1(currentAitess.getConfigFile());

		launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess("sudo "+currentAitess.getAitessCommand() + "\n"));
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess("sudo "+currentAitess.getAitessCommand() + "\n"));
		
		currentSessionDetails.setRunConfigId(currentRunConfigId);
		System.out.println("AFTER SWITCHING RUN CONFIG GETS UPDATED:: ------>>> "+currentSessionDetails.getRunConfigId());


	}

	public void exitAitess1Command() {
		launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess("exit" + "\n"));
	}
	public void exitAitess2Command() {
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess("exit" + "\n"));
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

	private String getParseErrorLine(String line) {
		Pattern parseErrorPattern = Pattern.compile("Parse Error \\(file '([^']+)', line (\\d+)\\): (.*)");
		Matcher parseErrorMatcher = parseErrorPattern.matcher(line);

		if (parseErrorMatcher.find()) {
			System.out.println("<<-----PARSE ERROR print statement----->>");
			return "PARSE ERROR";
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
