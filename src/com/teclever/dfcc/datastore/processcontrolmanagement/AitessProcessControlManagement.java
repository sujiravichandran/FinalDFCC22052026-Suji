package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.datastore.dto.AitessConfigurationDetails;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.datastore.dto.ChannelStatusBeforeTestResponse;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.dto.PbitResponse;
import com.teclever.dfcc.datastore.terminalmanagement.ChannelStatusParser;
import com.teclever.dfcc.datastore.terminalmanagement.TemperatureParser;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OFPversionStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.WDMStatus;
import com.teclever.dfcc.stateMachine.StateMachine.aitessRunning;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTemp;
import com.teclever.dfcc.stateMachine.StateMachine.boardChannelTempAEC;
import com.teclever.dfcc.stateMachine.StateMachine.channelAECTemp;
import com.teclever.dfcc.stateMachine.StateMachine.channelSCTemp;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.stateMachine.StateMachine.dfccCheckStatus;
import com.teclever.dfcc.utils.Debug;
import com.teclever.utils.ProcessControl;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

public class AitessProcessControlManagement {

	private StringBuilder textBuffer = new StringBuilder();
	private static int MAX_TOTAL_LINES = 5000;
	private static int MAX_LINES = 10;
	private int LINES_COUNT;

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
	private final AtomicBoolean dfccCheckStstusStarted = new AtomicBoolean(false);
	private final AtomicBoolean powerOnStatus = new AtomicBoolean(false);
	private final AtomicBoolean SCtemperatureMonitoring = new AtomicBoolean(false);
	private final AtomicBoolean AECtemperatureMonitoring = new AtomicBoolean(false);

	private final AtomicReference<String> currentCommand = new AtomicReference<>("Empty");
	private boolean switchAitessMethod = false;
	private boolean switchAitess1Method = false;
	private boolean checkMethod = false;
	private boolean launchAitess = false;
	private boolean launchAitess1 = false;
	boolean bothLaunched = false;
	public boolean runCommands = false;

	boolean flag;
	static boolean allChannelsOnline = false;

	private long lastExecutedTime = 0;

	private ScheduledExecutorService scheduler;

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

	public void configureAitess(String configFileLocation) {
		try {
			WriteAitess1Command("sudo rm -r config.cache" + "\n");
			Files.copy(Paths.get(configFileLocation), aitessConfigFile, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(startupUserFile, aitessStartupUserFile, StandardCopyOption.REPLACE_EXISTING);
			// Files.deleteIfExists(aitessDir.resolve("config.cache"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void configureAitess1(String configFileLocation) {
		try {
			WriteAitess2Command("sudo rm -r config.cache" + "\n");
			Files.copy(Paths.get(configFileLocation), aitess1ConfigFile, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(startupUserFile, aitess1StartupUserFile, StandardCopyOption.REPLACE_EXISTING);
			// Files.deleteIfExists(aitess1Dir.resolve("config.cache"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void launchAitess(String testTypeId, TextArea textArea) {
		launchAitess = true;
		launchAitess1 = true;
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		String uutId = StateMachine.currentSessionDetails.getUutId();
		Debug.printDebug("UUT ID inside launch() --------" + uutId);
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);
		Debug.printDebug("WHILE CALLING launchAitess() runconfigId-------------" + currentRunConfigId);
		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);
		Debug.printDebug("WHILE CALLING launchAitess() AITESS-------------" + currentAitess.getAitessName());

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
			Debug.printDebug("Home location: " + homeLocation);

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
			Path configFile1 = Paths.get(currentAitess.getAitess2ConfigFile());

			Path configFileParentPath = Paths.get(currentAitess.getConfigFile()).getParent();

			if (Files.exists(configFile) && Files.exists(configFile1)) {
				Files.copy(configFile, aitessConfigFile, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(configFile1, aitess1ConfigFile, StandardCopyOption.REPLACE_EXISTING);
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
				Debug.printDebug("AITESS 1 LAUNCHED COMMAND executed");
			}

			Debug.printDebug("moving to aitess 2........................" + !StateMachine.isAitess2Launched());
			// launch aitess2 inside aitess1 folder
			if (!StateMachine.isAitess2Launched()) {
				launchAitess2("cd " + aitess1Dir.toString() + "\n");
				launcherFuture2.thenRun(() -> {
					aitess2ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n");
					Debug.printDebug("AITESS 2 LAUNCHED COMMAND executed");

				});

			}
			StateMachine.setPreviousRunConfigId(currentRunConfigId);
			sortOutputFolder();
			Debug.printDebug(
					"BOTH AITESS LAUNCHED launch() after that currentRunConfig set to SM-->>" + currentRunConfigId);

		} catch (IOException e) {
			e.printStackTrace();
//			textArea.appendText("Failed to create directories or copy config.dat file.\n");
			appendText(textArea, "Failed to create directories or copy config.dat file.\n");
			Debug.printDebug("1");
		} catch (IllegalStateException e) {
			e.printStackTrace();
//			textArea.appendText("Failed to retrieve the username.\n");
			appendText(textArea, "Failed to retrieve the username.\\n");
			Debug.printDebug("2");
		}
	}

	private void launchAitess1(String command, TextArea textArea) {
		final String oldString[] = new String[1];
		Debug.printDebug("Entering Launch Aitess 1");
		aitess1ProcessControl.LaunchingProcess(command, launcherFuture1);
		launcherFuture1.thenRun(() -> {
			aitess1ProcessControl.ReadingProcess();
			outputProcessingThread1 = new Thread(() -> {
				try {
					String s1;
					String s2 = null;
					String cleanText;
					String result = "";
					String userExitErrorResult = null;

					while (true) {
						s2 = s1 = aitess1ReadQ.take();
//						Debug.printDebug("aitess1 output:: " + s1);

						if (!aitessRunning.isAitess1Exited()) {
							aitessRunning.setAitess1Exited(true);
						}

						if (launchAitess == true) {
							if (s1.contains(">>>")) {
								Debug.printDebug("Launch time aitess 1 end founded");
								StateMachine.setAitess1Launched(true);
								launchAitess = false;
							}
						}

						if (switchAitessMethod == true) {
							if (s1.contains(">>>")) {
								Debug.printDebug("END FOR AITESS FOUNDED ---------**********-----------");
								aitessRunning.setAitess1Switched(true);
								switchAitessMethod = false;
							} else if (s1.contains("ValueError")) {
								Debug.printDebug(
										"END FOR AITESS FAILED  FOUNDED --XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX-");
								aitessRunning.setAitess1SwitchedFailed(true);
								switchAitessMethod = false;
							}
						}
						if (StateMachine.isRunCommand() == true) {
							if (s1.contains(">>>")) {
								Debug.printDebug("command end line founded . . . . . . . . . ");
								StateMachine.setAitess1CommandFinished(true);
								StateMachine.setRunCommand(false);
								AdvancedTestStateObject.getCustomTest1Status().set(false);
							}
						}

						if (checkMethod == true) {
							if (s1.contains(">>>")) {
								Debug.printDebug("END LINE FOR RELOAD CONFIG FOUNDED -------777777777777777777777");
								aitessRunning.setAitess1ReloadConfigured(true);
								checkMethod = false;
							}
						}
						if (runCommands == true) {
							if (s1.contains(">>>")) {
								System.out.println("END LINE FOR PASSED COMMAND FOUNDED - ");
								runCommands = false;
								System.out.println(
										" runCommands : " + AitessProcessControlManagement.getInstance().runCommands);

							}
						}

						cleanText = cleanOutput(s1);
						cleanText = cleanText.replaceAll("\\(B", "");
						cleanText = cleanText.replaceAll("]104", "");

						final String cleanContent = cleanText;
						final String newString = cleanContent;

						Platform.runLater(() -> {
							if (oldString[0] != null) {
								if (!newString.equals(oldString[0])) {
//						    		Debug.printDebug("----new Text---"+cleanContent);
//									Debug.printDebug("-------------------------------------------------");
//									Debug.printDebug();
//									Debug.printDebug("--Before Appending TextArea--"+textArea.getText());
//									textArea.appendText(cleanContent);
//									textArea.requestFocus();
//									textArea.setScrollTop(Double.MAX_VALUE);
									appendText(textArea, cleanContent);
//									Debug.printDebug("--After Appending TextArea--"+textArea.getText());
//									Debug.printDebug("--After Appending TextArea--"+textArea.getText());
//									Debug.printDebug();
//									Debug.printDebug("-------------------------------------------------");
								}
							} else if (oldString[0] == null) {
//						    	Debug.printDebug("-----firstTime----");
//								textArea.appendText(newString);
//								textArea.requestFocus();
//								textArea.setScrollTop(Double.MAX_VALUE);
								appendText(textArea, newString);
							}
							oldString[0] = newString;
						});

						String userActionLine = getUserActionLine(s1);
						if (userActionLine != null || s1.contains("Do you wish to continue")) {
							StateMachine.setUserAction(userActionLine);
							StateMachine.getUserActionFlag().set(true);
						}

						// condition if test stared
						if (testStarted) {
//							Debug.printDebug("s2 :: " + s2);
							cleanText = cleanOutput(s2);
							cleanText = cleanText.replaceAll("\\(B", "");
							cleanText = cleanText.replaceAll("]104", "");

							final String finalLine = cleanText;

							// user exit line
//							if(getUserExitErrorLine(finalLine) != null) {
//								userExitErrorResult = getUserExitErrorLine(finalLine);
//							}
//							if(userExitErrorResult !=null) {
//								aitess1ResultQ.put(userExitErrorResult);
//							}

							if (finalLine.contains("Runtime Error")) {
								aitess1ResultQ.put("RUN TIME ERROR");
								testStarted = false;
								result = "";

							}

							if (finalLine.contains("User Exit Error")) {
								aitess1ResultQ.put("USER EXIT");
								testStarted = false;
								result = "";

							}

//							Debug.printDebug("aitess1 finalLine:: " + finalLine);

							if (finalLine.contains("Parse Error")) {

//								Debug.printDebug("aitess1 result1:: " + result);
								if (result.equals("")) {

//									Debug.printDebug("aitess1 result2:: " + result);
									aitess1ResultQ.put("PARSE ERROR");
									testStarted = false;
								}
							}
							// rdf file name
							String tmp = getRdfFileName(finalLine);
							if (tmp != null) {
								result = tmp;
							}

							String endLine = getEndMatchingLine(finalLine);
							if (endLine != null) {
								if (result != null) {
									aitess1ResultQ.put(result);
									result = "";
								}
							} else if (finalLine.contains("Execution of TPF") && finalLine.endsWith("completed.")) {
								Debug.printDebug("END LINE:: " + finalLine);
								if (result != null) {
									aitess1ResultQ.put(result);
									result = "";
								}
							}

							if (finalLine.contains(">>>")) {
								if (result != null && !result.isEmpty()) {
									aitess1ResultQ.put(result);
									result = "";
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

	}

	private void launchAitess2(String command) {
		Debug.printDebug("Entering Launch Aitess 2");
		ChannelStatusParser channelStatusParser = new ChannelStatusParser();
		TemperatureParser temperatureParser = new TemperatureParser();
		aitess2ProcessControl.LaunchingProcess(command, launcherFuture2);
		launcherFuture2.thenRun(() -> {
			aitess2ProcessControl.ReadingProcess();
			outputProcessingThread2 = new Thread(() -> {
				try {
					ChannelStatus channelStatus = new ChannelStatus();
					ChannelTemperature channelTemperature = new ChannelTemperature();

					boolean minMaxExtracted = false;

					boolean channel1Online = false;
					boolean channel2Online = false;
					boolean channel3Online = false;
					boolean channel4Online = false;

					double minValue = 0.0;
					double maxValue = 0.0;

					String bName = null;
					String boardAECName = null;

					String cleanText;

					while (true) {
						String output = aitess2ReadQ.take();
						Debug.printDebug("aitess2 :: " + output);

						if (launchAitess1 == true) {
							if (output.contains(">>>")) {
								Debug.printDebug("Launch time aitess 2 end founded");
								WriteAitess2Command1();
								StateMachine.setAitess2Launched(true);
								launchAitess1 = false;
							}
						}

						if (switchAitess1Method == true) {
							if (output.contains(">>>")) {
								Debug.printDebug("END FOR AITESS 2 FOUNDED ---------**********-----------");

								aitessRunning.setAitess2Switched(true);
								switchAitess1Method = false;
							} else if (output.contains("ValueError")) {
								Debug.printDebug(
										"END FOR AITESS 2  FAILED  FOUNDED --XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX-");

								aitessRunning.setAitess2SwitchedFailed(true);
								switchAitess1Method = false;
							}
						}

						cleanText = cleanOutput(output);
						cleanText = cleanText.replaceAll("\\(B", "");
						cleanText = cleanText.replaceAll("]104", "");
						final String finalLine = cleanText;

						if (!aitessRunning.isAitess2Exited()) {
							aitessRunning.setAitess2Exited(true);
						}
						if (dfccCheckStstusStarted.get() == true) {
							String command1 = currentCommand.get();

							switch (command1) {

							case "Mk1ScTemperatureCommand":
								ChannelTemperature scTemp = new ChannelTemperature();
								scTemp = temperatureParser.getChannelTemperature(finalLine);
								if (scTemp != null) {
									channelSCTemp.setChannel1Temperature(scTemp.getChannel1Temp());
									channelSCTemp.setChannel2Temperature(scTemp.getChannel2Temp());
									channelSCTemp.setChannel3Temperature(scTemp.getChannel3Temp());
									channelSCTemp.setChannel4Temperature(scTemp.getChannel4Temp());

									Debug.printDebug("State Machine CH1 SC Temp:: " + channelSCTemp.getChannel1Temperature());
									Debug.printDebug("State Machine CH2 SC Temp:: " + channelSCTemp.getChannel2Temperature());
									Debug.printDebug("State Machine CH3 SC Temp:: " + channelSCTemp.getChannel3Temperature());
									Debug.printDebug("State Machine CH4 SC Temp:: " + channelSCTemp.getChannel4Temperature());

								}

								break;

							case "Mk1AecTemperatureCommand":
								ChannelTemperature aecTemp = new ChannelTemperature();
								aecTemp = temperatureParser.getChannelTemperature(finalLine);
								if (aecTemp != null) {
									channelAECTemp.setChannel1Temperature(aecTemp.getChannel1Temp());
									channelAECTemp.setChannel2Temperature(aecTemp.getChannel2Temp());
									channelAECTemp.setChannel3Temperature(aecTemp.getChannel3Temp());
									channelAECTemp.setChannel4Temperature(aecTemp.getChannel4Temp());

									Debug.printDebug("State Machine AEC CH1 Temp:: " + channelAECTemp.getChannel1Temperature());
									Debug.printDebug("State Machine AEC CH2 Temp:: " + channelAECTemp.getChannel2Temperature());
									Debug.printDebug("State Machine AEC CH3 Temp:: " + channelAECTemp.getChannel3Temperature());
									Debug.printDebug("State Machine AEC CH4 Temp:: " + channelAECTemp.getChannel4Temperature());

								}
								break;

							case "OFPversion":
								ChannelStatus ofp = new ChannelStatus();
								ofp = channelStatusParser.getOFPversionStatus(finalLine);

								if (ofp != null) {
									Debug.printDebug("State Machine OFPversion CH1:: " + OFPversionStatus.getChannel1Status());
									Debug.printDebug("State Machine OFPversion CH2:: " + OFPversionStatus.getChannel2Status());
									Debug.printDebug("State Machine OFPversion CH3:: " + OFPversionStatus.getChannel3Status());
									Debug.printDebug("State Machine OFPversion CH4:: " + OFPversionStatus.getChannel4Status());
								}

								break;

							case "WDMversion":
								ChannelStatus wdm = new ChannelStatus();
								wdm = channelStatusParser.getWDMStatus(finalLine);

								if (wdm != null) {
									Debug.printDebug("State Machine WDMstatus CH1:: " + WDMStatus.getChannel1Status());
									Debug.printDebug("State Machine WDMstatus CH2:: " + WDMStatus.getChannel2Status());
									Debug.printDebug("State Machine WDMstatus CH3:: " + WDMStatus.getChannel3Status());
									Debug.printDebug("State Machine WDMstatus CH4:: " + WDMStatus.getChannel4Status());
									Debug.printDebug("*****---------------------------*****");
									Debug.printDebug("State Machine OnlineStatus CH1:: " + OnlineStatus.getChannel1Status());
									Debug.printDebug("State Machine OnlineStatus CH2:: " + OnlineStatus.getChannel2Status());
									Debug.printDebug("State Machine OnlineStatus CH3:: " + OnlineStatus.getChannel3Status());
									Debug.printDebug("State Machine OnlineStatus CH4:: " + OnlineStatus.getChannel4Status());
								}
								break;

							case "scTemp":
								break;

							case "aecTemp":
								break;

							default:
								Debug.printDebug(" --> AETS 2 SWITCH   -" + currentCommand);
								break;
							}
						}

						// TESTING POWER ON STATUS
						if (powerOnStatus.get()) {

							if (cleanText.contains("pwronstsend")) {
								Debug.printDebug(" -- -- -- END OF MACRO -- -- -- ");
								powerOnStatus.set(false);
							}

							// minMaxExatraction
							if (getMinMaxLine(cleanText) != null) {
								Pattern minMaxPattern = Pattern
										.compile(".*\\(\\s*(-?\\d+\\.\\d+)\\s*,\\s*(-?\\d+\\.\\d+)\\s*\\)\\?");
								Matcher matcher = minMaxPattern.matcher(cleanText);
								if (matcher.find()) {
									minValue = Double.parseDouble(matcher.group(1).trim());
									maxValue = Double.parseDouble(matcher.group(2).trim());
									minMaxExtracted = true;
								}
							}

							if (cleanText.contains("pwrsts")) {
								minMaxExtracted = false; // Reset for the next min/max extraction

							}

							if (getPchannelValues(cleanText) != null) {
								Pattern channelPattern = Pattern.compile(
										"<\\s*.*\\s*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)\\s*(\\w+)");
								Matcher channelMatcher = channelPattern.matcher(cleanText);
								if (channelMatcher.find()) {
									double channel1 = Double.parseDouble(channelMatcher.group(1).trim());
									double channel2 = Double.parseDouble(channelMatcher.group(2).trim());
									double channel3 = Double.parseDouble(channelMatcher.group(3).trim());
									double channel4 = Double.parseDouble(channelMatcher.group(4).trim());

									// Compare and set channel statuses based on min and max values
									if (!channel1Online) {
										channel1Online = (channel1 >= minValue && channel1 <= maxValue);
									}
									if (!channel2Online) {
										channel2Online = (channel2 >= minValue && channel2 <= maxValue);
									}
									if (!channel3Online) {
										channel3Online = (channel3 >= minValue && channel3 <= maxValue);
									}
									if (!channel4Online) {
										channel4Online = (channel4 >= minValue && channel4 <= maxValue);
									}

									// Display current channel statuses
//									System.out.println("Channel 1: " + (channel1Online ? "online" : "offline"));
//									System.out.println("Channel 2: " + (channel2Online ? "online" : "offline"));
//									System.out.println("Channel 3: " + (channel3Online ? "online" : "offline"));
//									System.out.println("Channel 4: " + (channel4Online ? "online" : "offline"));

									// Check if all channels are online and exit if true
									if (channel1Online && channel2Online && channel3Online && channel4Online) {
										Debug.printDebug("All channels are online. Exiting loop.");
										com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
												.setChannel1Status("online");
										com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
												.setChannel2Status("online");
										com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
												.setChannel3Status("online");
										com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
												.setChannel4Status("online");

										Debug.printDebug("STATE MACHINE powerOnStatus : "
												+ com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
														.getChannel1Status());
										Debug.printDebug("STATE MACHINE powerOnStatus : "
												+ com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
														.getChannel2Status());
										Debug.printDebug("STATE MACHINE powerOnStatus : "
												+ com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
														.getChannel3Status());
										Debug.printDebug("STATE MACHINE powerOnStatus : "
												+ com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
														.getChannel4Status());

										// Check if any channel is offline
										if (com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
												.getChannel1Status().equals("online")
												&& com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
														.getChannel2Status().equals("online")
												&& com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
														.getChannel3Status().equals("online")
												&& com.teclever.dfcc.stateMachine.StateMachine.powerOnStatus
														.getChannel4Status().equals("online")) {

											allChannelsOnline = true; // Set the flag to true if all channels are online
										}

										powerOnStatus.set(false); // Set powerOnStatus to false
									}
								}

							}
						}

						// SC TEMPERATURE MONITORING
						if (SCtemperatureMonitoring.get() == true) {
							if (cleanText.contains("sctempend")) {
								Debug.printDebug("End of SC Temprature Monitoring found.");
								AECtemperatureMonitoring.set(true);
								SCtemperatureMonitoring.set(false);
							}

							// Check for specific board names
							if (cleanText.contains("DFCC_TEMP_AN2")) {
								bName = "DFCC_TEMP_AN2";
							} else if (cleanText.contains("AN1L_BRD_TEMP")) {
								bName = "AN1L_BRD_TEMP";
							} else if (cleanText.contains("AN1R_BRD_TEMP")) {
								bName = "AN1R_BRD_TEMP";
							} else if (cleanText.contains("DM_BRD_TEMP")) {
								bName = "DM_BRD_TEMP";
							}

							if (getDEGCValues(cleanText) != null) {
								// Extract temperature values
								Pattern tempPattern = Pattern.compile(
										"<\\s*.*\\s*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)\\s*DEGC");
								Matcher tempMatcher = tempPattern.matcher(cleanText);

								if (tempMatcher.find()) {
									String temp1 = tempMatcher.group(1).trim();
									String temp2 = tempMatcher.group(2).trim();
									String temp3 = tempMatcher.group(3).trim();
									String temp4 = tempMatcher.group(4).trim();

									ChannelTemperature c = new ChannelTemperature();
									c.setChannel1Temp(temp1);
									c.setChannel2Temp(temp2);
									c.setChannel3Temp(temp3);
									c.setChannel4Temp(temp4);

									// Update to STATE MACHINE
									boardChannelTemp.addBoardTemperatureMap(bName, c);

									Debug.printDebug("STATE MACHINE SC TEMP::    " + bName + ": " + "CH 1: " + temp1
											+ ", CH 2: " + temp2 + ", CH 3: " + temp3 + ", CH 4: " + temp4);
								}
								bName = null;

							}
						}

						// AEC monitoring
						if (AECtemperatureMonitoring.get() == true) {
							if (cleanText.contains("aectempend")) {
								Debug.printDebug("End of AEC Temprature Monitoring found.");
								AECtemperatureMonitoring.set(false);
							}

							// Check for specific board names
							if (cleanText.contains("DFCC_TEMP_AN2")) {
								boardAECName = "DFCC_TEMP_AN2";
							} else if (cleanText.contains("AN1L_BRD_TEMP")) {
								boardAECName = "AN1L_BRD_TEMP";
							} else if (cleanText.contains("AN1R_BRD_TEMP")) {
								boardAECName = "AN1R_BRD_TEMP";
							} else if (cleanText.contains("DM_BRD_TEMP")) {
								boardAECName = "DM_BRD_TEMP";
							}

							if (getDEGCValues(cleanText) != null) {
								// Extract temperature values
								Pattern tempPattern = Pattern.compile(
										"<\\s*.*\\s*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)\\s*DEGC");
								Matcher tempMatcher = tempPattern.matcher(cleanText);

								if (tempMatcher.find()) {
									String temp1 = tempMatcher.group(1).trim();
									String temp2 = tempMatcher.group(2).trim();
									String temp3 = tempMatcher.group(3).trim();
									String temp4 = tempMatcher.group(4).trim();

									ChannelTemperature cc = new ChannelTemperature();
									cc.setChannel1Temp(temp1);
									cc.setChannel2Temp(temp2);
									cc.setChannel3Temp(temp3);
									cc.setChannel4Temp(temp4);

									// Update to STATE MACHINE
									boardChannelTempAEC.addBoardTemperatureMap(boardAECName, cc);

									Debug.printDebug("STATE MACHINE AEC TEMP::    " + boardAECName + ": " + "CH 1: "
											+ temp1 + ", CH 2: " + temp2 + ", CH 3: " + temp3 + ", CH 4: " + temp4);
								}
								boardAECName = null;

							}
						}

					} // while

				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			});
			outputProcessingThread2.start();
		});

	}

	public String performTest(String tpfFileName) {

		checkChannelStatus();
		String aets1QResponse = null;
		try {
			testStarted = true;
			final String fileName;
			Debug.printDebug("Perform test() Start -- " + tpfFileName);
			if (tpfFileName.contains("\\") || tpfFileName.contains("/")) {
				File file = new File(tpfFileName);
				fileName = file.getName();
			} else {
				fileName = tpfFileName;
			}

			launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess("@ " + fileName + "\n"));
			boolean flag = true;
			while (flag) {

				if (aitess1ResultQ != null && aitess1ResultQ.peek() != null) {
					aets1QResponse = aitess1ResultQ.take();
					Debug.printDebug(" --> AETS 1 Q Data : " + aets1QResponse);

					if (aets1QResponse != null && !aets1QResponse.isEmpty()) {

						if (aets1QResponse.equals("PARSE ERROR")) {
							aets1QResponse = null;
						} else if (aets1QResponse.equals("USER EXIT")) {
							aets1QResponse = "USER EXIT";
						} else if (aets1QResponse.equals("RUN TIME ERROR")) {
							aets1QResponse = "RUN TIME ERROR";
						}
						flag = false;

					}
				}
			}

			testStarted = false;
			Debug.printDebug("Perform Test() Return : " + aets1QResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return aets1QResponse;

	}

	public void WriteAitess1Command(String command) {
		launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess(command));
	}

	public void WriteAitess2Command(String command) {
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(command));
	}

	// NEW LOGIC**
	public void WriteDfccPowerOnCommandToAitess2() {

		try {
			dfccCheckStstusStarted.set(true);
			// psc_fcc_pwr_on=1
			currentCommand.set("");
			launcherFuture2.thenRun(
					() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getDfccPowerOnCommand() + "\n"));
			Thread.sleep(300);
			// gse_conn=1
			currentCommand.set("");
			launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess("gse_conn=1" + "\n"));
			Thread.sleep(300);
			// ltm_syntax on
			currentCommand.set("");
			launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess("ltm_syntax on" + "\n"));
			Thread.sleep(300);

			currentCommand.set("WDMversion");
			launcherFuture2
					.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getWdmStatusCommand() + "\n"));
			Thread.sleep(300);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dfccCheckStatus.getDfccPowerStatus().set(true);
		dfccCheckStstusStarted.set(false);
		;

	}

	// check before any test is get started
	public void checkChannelStatus() {
		dfccCheckStstusStarted.set(true);
		launcherFuture2
				.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOnlineStatusCommand() + "\n"));
		dfccCheckStstusStarted.set(false);

		// -------------- here for mk1a and mk2 what to do?
		if (OnlineStatus.getChannel1Status() == "offline" || OnlineStatus.getChannel2Status() == "offline"
				|| OnlineStatus.getChannel3Status() == "offline" || OnlineStatus.getChannel4Status() == "offline") {

			WriteDfccPowerOnCommandToAitess2();
		} else {

		}
	}

	// NEW LOGIC**
	public ChannelStatusBeforeTestResponse checkChannelStatusBeforeAnyTest() {
		try {
			dfccCheckStstusStarted.set(true);
			currentCommand.set("WDMversion");
			launcherFuture2
					.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getWdmStatusCommand() + "\n"));

			Thread.sleep(300);

			// Check if any channel is offline, return 0 if any are offline
			if (WDMStatus.getChannel1Status().equals("offline") || WDMStatus.getChannel2Status().equals("offline")
					|| WDMStatus.getChannel3Status().equals("offline")
					|| WDMStatus.getChannel4Status().equals("offline")) {
				return new ChannelStatusBeforeTestResponse(0, "WDM Status is offline. Please Check"); // Stop
			} else {
				return new ChannelStatusBeforeTestResponse(1, "Continue"); // Continue only if all are not offline
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ChannelStatusBeforeTestResponse(0, "Error"); // Stop in case of an exception
		} finally {
			dfccCheckStstusStarted.set(false);
		}
	}

	public void WriteDfccPowerOffCommandToAitess2() {
		try {
			dfccCheckStstusStarted.set(true);
			currentCommand.set("");
			launcherFuture2.thenRun(
					() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getDfccPowerOffCommand() + "\n"));
			Thread.sleep(300);
			currentCommand.set("WDMversion");
			launcherFuture2
					.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getWdmStatusCommand() + "\n"));
			Thread.sleep(300);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dfccCheckStatus.getDfccPowerStatus().set(false);
		dfccCheckStstusStarted.set(false);

	}

	public void WriteMacroCommandToAitess2(String macroCommand) {
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(macroCommand + "\n"));
	}

	public void WriteAitess2Command1() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			updateUIdfccStatus(true);
		}, 0, DFCCConstant.tempDelayTime, TimeUnit.MILLISECONDS);
	}

	public void updateUIdfccStatus(boolean fromThread) {
		long currentTime = System.currentTimeMillis();
		if (fromThread && StateMachine.getTestState() == TestState.RUNNING) {
			return;
		}
		else if(!fromThread) {
			if (lastExecutedTime == 0 || currentTime - lastExecutedTime < DFCCConstant.tempDelayTime) {
			return;
			}
		}
		executeDfccStatusCommandsToAitess2();
		lastExecutedTime = currentTime;
	}

	public void executeDfccStatusCommandsToAitess2() {

		dfccCheckStatusThread = new Thread(() -> {

			StateMachine.setTextArea(false);
			dfccCheckStstusStarted.set(true);
			powerOnStatus.set(true);

			// POWER ON STATUS
			launcherFuture2.thenRun(() -> {

				aitess2ProcessControl.WritingProcess("ltm_syntax on" + "\n");

				currentCommand.set("dfccPowerOnStatus");
				aitess2ProcessControl.WritingProcess(dfccCheckStatus.getDfccPowerOnStatus() + "\n");

				// if all power on channels are online then only
				if (allChannelsOnline) {
					System.out.println("After PowerOnStatus Command ALL CHANNELS are ONLINE: -> " + allChannelsOnline);
					try {
						currentCommand.set("");
						aitess2ProcessControl.WritingProcess("gse_conn=1" + "\n");
						Thread.sleep(100);
						currentCommand.set("WDMversion");
						aitess2ProcessControl.WritingProcess(dfccCheckStatus.getWdmStatusCommand() + "\n");
						Thread.sleep(100);
						// Check if any channel is offline, return 0 if any are offline
						if (WDMStatus.getChannel1Status().equals("offline")
								|| WDMStatus.getChannel2Status().equals("offline")
								|| WDMStatus.getChannel3Status().equals("offline")
								|| WDMStatus.getChannel4Status().equals("offline")) {
							// Stop
						} else {
							// Continue only if all are not offline
							if ("UUT1".equals(currentSessionDetails.getUutId())) {

								// MK1
								// SC
								currentCommand.set("Mk1ScTemperatureCommand");
								aitess2ProcessControl
										.WritingProcess(dfccCheckStatus.getMk1ScTemperatureCommand() + "\n");
								Thread.sleep(100);

								// AEC
								currentCommand.set("Mk1AecTemperatureCommand");
								aitess2ProcessControl
										.WritingProcess(dfccCheckStatus.getMk1AecTemperatureCommand() + "\n");
								Thread.sleep(100);
							} else {
								// MK1A MK2
								// SC
								SCtemperatureMonitoring.set(true);
								currentCommand.set("scTemp");
								aitess2ProcessControl.WritingProcess(dfccCheckStatus.getScTemperatureCommand() + "\n");
								Thread.sleep(300);

								// AEC
								currentCommand.set("aecTemp");
								aitess2ProcessControl.WritingProcess(dfccCheckStatus.getAecTemperatureCommand() + "\n");
								Thread.sleep(100);

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} // end of if

			});

			dfccCheckStstusStarted.set(false);
			StateMachine.setTextArea(true);
		});
		dfccCheckStatusThread.start();
	}

	public void check(String testTypeId) {
		Debug.printDebug("---ENTERING check() passed testTypeIdl---------------" + testTypeId);
		RunConfigurationService runConfigurationService = new RunConfigurationService();
		LoadDriverProcessControlManagement pcm = LoadDriverProcessControlManagement.getInstance();

		String smRunConfigId = StateMachine.getPreviousRunConfigId();
		Debug.printDebug("PREVIOUS RUN CONFIG check() ::--------" + smRunConfigId);
		AitessConfigurationDetails smAitess = runConfigurationService.getAitessDetailsByRunConfigId(smRunConfigId);
		Debug.printDebug("PREVIOUS  AITESS check() ::" + smAitess.getAitessName());

		String uutId = currentSessionDetails.getUutId();
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		Debug.printDebug("CURRENT RUN CONFIG based on testType check()::----------" + currentRunConfigId);

		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);
		Debug.printDebug("CURRENT AITESS check() :: " + currentAitess.getAitessName());

		Debug.printDebug("STATE MACHINE AITESS driverName:: -----" + smAitess.getDriverName());
		Debug.printDebug("CURRENT AITESS driverName:: -----" + currentAitess.getDriverName());

		// CHECKING DRIVER
		if (!smAitess.getDriverName().equals(currentAitess.getDriverName())) {
			// NOT MATCHED
			Debug.printDebug("Switching Load Driver::---- " + smAitess.getDriverName() + " to ::---- "
					+ currentAitess.getDriverName());
			pcm.loadDriver(currentAitess.getLoadDriverCommand(), smAitess.getUnloadDriverCommand(), 0,
					LoadDriverProcessControlManagement.LoadMode.SWITCH);
			if (aitessRunning.isAitess1Exited() == true && aitessRunning.isAitess2Exited() == true) {
				switchAitess(testTypeId);
			}
		} else {
			// MATCHED
			Debug.printDebug(
					"Load Driver Matches::---- " + smAitess.getDriverName() + " == " + currentAitess.getDriverName());

			// CHECKING AITESS
			if (!smAitess.getAitessName().equals(currentAitess.getAitessName())) {
				// NOT MATCHED
				Debug.printDebug("Aitess Not Matched:: OLD AITESS:---- " + smAitess.getAitessName()
						+ " NEW AITESS:---- " + currentAitess.getAitessName());

				aitessRunning.setAitess1Exited(false);
				exitAitess1Command();
				aitessRunning.setAitess2Exited(false);
				exitAitess2Command();

				if (aitessRunning.isAitess1Exited() == true && aitessRunning.isAitess2Exited() == true) {
					switchAitess(testTypeId);
				}

			} else {
				checkMethod = true;
				boolean aets1SwitchFlagg = true;
				Debug.printDebug(
						"Aitess Matches::---- " + smAitess.getAitessName() + " == " + currentAitess.getAitessName());
				if (!smAitess.getConfigFile().equals(currentAitess.getConfigFile())) {
					try {
						WriteAitess1Command("sudo rm -r config.cache" + "\n");

						Files.copy(Paths.get(currentAitess.getConfigFile()), aitessConfigFile,
								StandardCopyOption.REPLACE_EXISTING);

						// exit from aitess
						exitAitess1Command();
						Thread.sleep(200);

						// load aitess
						launcherFuture1.thenRun(() -> aitess1ProcessControl
								.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));

//						launcherFuture1
//								.thenRun(() -> aitess1ProcessControl.WritingProcess("reload_configuration" + "\n"));
						// launcherFuture2.thenRun(() ->
						// aitess2ProcessControl.WritingProcess("reload_configuration" + "\n"));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					while (aets1SwitchFlagg) {
						// System.out.print(" 1 ");
						if (aitessRunning.isAitess1ReloadConfigured()) {
							aets1SwitchFlagg = false;
							// currentSessionDetails.setRunConfigId(currentRunConfigId);
							Debug.printDebug("AFTER 1 SWITCHING RUN CONFIG GETS UPDATED:: ------>>> "
									+ currentSessionDetails.getRunConfigId());

						}
					}

				}
				aitessRunning.setAitess1ReloadConfigured(false);

			}

		}

		aitessRunning.setAitess1Switched(false);
//		aitessRunning.setAitess1SwitchedFailed(false);
		aitessRunning.setAitess2Switched(false);
//		aitessRunning.setAitess2SwitchedFailed(false);
		StateMachine.setPreviousRunConfigId(currentRunConfigId);
		Debug.printDebug("UPDATED previous runConfig Id ::------" + StateMachine.getPreviousRunConfigId());
		Debug.printDebug("FUNCTION ENDED----------------------->>>>>>>>>>>>>>>>>>");
	}

	public void switchAitess(String testTypeId) {
		switchAitessMethod = true;
		switchAitess1Method = true;
		boolean aets1SwitchFlag = true;
		boolean aets2SwitchFlag = true;
		Debug.printDebug("Entering into Switching AITESS");
		RunConfigurationService runConfigurationService = new RunConfigurationService();

		String uutId = currentSessionDetails.getUutId();
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);

		configureAitess(currentAitess.getConfigFile());
		configureAitess1(currentAitess.getAitess2ConfigFile());

		launcherFuture1
				.thenRun(() -> aitess1ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));
		launcherFuture2
				.thenRun(() -> aitess2ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));

		while (aets1SwitchFlag) {
			// System.out.print(" 1 ");
			if (aitessRunning.isAitess1Switched()) {
				aets1SwitchFlag = false;
				// currentSessionDetails.setRunConfigId(currentRunConfigId);
				Debug.printDebug("AFTER 1 SWITCHING RUN CONFIG GETS UPDATED:: ------>>> "
						+ currentSessionDetails.getRunConfigId());

			} else if (aitessRunning.isAitess1SwitchedFailed()) {
				System.out
						.println("AFTER 1 SWITCHING RUN FAILED :: ------>>> " + currentSessionDetails.getRunConfigId());

				aets1SwitchFlag = false;
			}
		}
		while (aets2SwitchFlag) {
			// System.out.print(" * ");
			if (aitessRunning.isAitess2Switched()) {

				aets2SwitchFlag = false;
				// currentSessionDetails.setRunConfigId(currentRunConfigId);
				Debug.printDebug("AFTER  2 SWITCHING RUN CONFIG GETS UPDATED:: ------>>> "
						+ currentSessionDetails.getRunConfigId());
			} else if (aitessRunning.isAitess2SwitchedFailed()) {
				Debug.printDebug("AFTER  2 SWITCHING Failed:: ------>>> " + currentSessionDetails.getRunConfigId());

				aets2SwitchFlag = false;
			}

		}
//			currentSessionDetails.setRunConfigId(currentRunConfigId);
		Debug.printDebug("< ======   BOTH AETS SWITCH DONE  ===== >");
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
			// System.out.println("END LINE*:: " + line);
			return line;
		}
		return null;
	}

	// POWER ON
	public String getPowerOnLine(String line) {
		Pattern pscFccPattern = Pattern.compile("psc_fcc_pwr_on\\(\\d+\\) \\+ \\d+ = 0x1");
		Matcher pscFccMatcher = pscFccPattern.matcher(line);

		if (pscFccMatcher.find()) {
			Debug.printDebug("END LINE:: " + line);
			return line;
		}

		return null;
	}

	// POWER OFF
	public String getPowerOffLine(String line) {
		Pattern pscFccPattern = Pattern.compile("psc_fcc_pwr_on\\(\\d+\\) \\+ \\d+ = 0x0");
		Matcher pscFccMatcher = pscFccPattern.matcher(line);

		if (pscFccMatcher.find()) {
			Debug.printDebug("END LINE:: " + line);
			return line;
		}

		return null;
	}

	// MIN MAX LINE
	private String getMinMaxLine(String line) {
		Pattern channelPattern = Pattern.compile(".*\\(\\s*(-?\\d+\\.\\d+)\\s*,\\s*(-?\\d+\\.\\d+)\\s*\\)\\?");
		Matcher channelMatcher = channelPattern.matcher(line);

		if (channelMatcher.find()) {
			return channelMatcher.group(0);
		}
		return null;
	}

	// CHANNEL VALUES
	private String getPchannelValues(String line) {
		Pattern channelPattern = Pattern
				.compile("<\\s*.*\\s*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)\\s*(\\w+)");
		Matcher channelMatcher = channelPattern.matcher(line);

		if (channelMatcher.find()) {
			return line;
		}
		return null;
	}

	// DEGC LINE
	private String getDEGCValues(String line) {
		Pattern channelPattern = Pattern
				.compile("<\\s*.*\\s*>\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^,]+)\\)\\s*DEGC");
		Matcher channelMatcher = channelPattern.matcher(line);

		if (channelMatcher.find()) {
			return line;
		}
		return null;
	}

	private String getRdfFileName(String line) {
		Pattern rdfFileNamePattern = Pattern.compile("Running TPF '.*?' and generating RDF '([^']+)'.");
		Matcher rdfFileNameMatcher = rdfFileNamePattern.matcher(line);

		if (rdfFileNameMatcher.find()) {
			String rdfFileName = rdfFileNameMatcher.group(1);
			Debug.printDebug("RDF NAME FROM TERMINAL :: " + rdfFileName);
			return rdfFileName;
		}
		return null;
	}

	private String getUserActionLine(String line) {
		Pattern userActionPattern = Pattern.compile("User Action .* \\(Y/N\\):");
		Matcher userActionMatcher = userActionPattern.matcher(line);

		if (userActionMatcher.find()) {
			Debug.printDebug("USER ACTION LINE :: " + line);
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

	public void endAllProcessOnLogout() {
		if (DFCCConstant.isJarBuild) {
			// unloadDriver
			RunConfigurationService runConfigurationService = new RunConfigurationService();
			String currentRunConfigId = currentSessionDetails.getRunConfigId();
			Debug.printDebug("AT LOGOUT runConfigId: " + currentRunConfigId);
			AitessConfigurationDetails currentAitess = runConfigurationService
					.getAitessDetailsByRunConfigId(currentRunConfigId);
			Debug.printDebug("AT LOGOUT aitess: " + currentAitess.getAitessName());
			LoadDriverProcessControlManagement pcm = LoadDriverProcessControlManagement.getInstance();
			Debug.printDebug("Unload Driver Command : " + currentAitess.getUnloadDriverCommand());
			pcm.loadDriver(null, currentAitess.getUnloadDriverCommand(), 0,
					LoadDriverProcessControlManagement.LoadMode.LOGOUT);
			Debug.printDebug("AT LOGOUT driver -> " + currentAitess.getDriverName() + " >>> UNLOADED");
			// kill pty process
			exitAitess1Command();
			exitAitess2Command();
			// aitess2thread stop
			shutdownScheduler();
		}
	}

	public void shutdownScheduler() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}

	public PbitResponse pbitCheck() {
		PbitResponse response = new PbitResponse();

		boolean ofpMatch = false;
		boolean wdmMatch = false;

		try {
			dfccCheckStstusStarted.set(true);

			// Write OFP version command
			currentCommand.set("OFPversion");
			launcherFuture2.thenRun(
					() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOfpVersionStatusCommand() + "\n"));
			Thread.sleep(500);
			currentCommand.set("");
			// Write WDM version command
			currentCommand.set("WDMversion");
			launcherFuture2
					.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getWdmStatusCommand() + "\n"));

		} catch (InterruptedException e) {
			e.printStackTrace();
			response.setResponseCode(500);
			response.setResponseMessage("Error occurred: " + e.getMessage());
			dfccCheckStstusStarted.set(false);
			return response;
		}

		dfccCheckStstusStarted.set(false);

	    List<String> ofpStatusList = new ArrayList<>();
	    List<String> wdmStatusList = new ArrayList<>();
	    
	    ofpStatusList.add(OFPversionStatus.getChannel1Status());
	    ofpStatusList.add(OFPversionStatus.getChannel2Status());
	    ofpStatusList.add(OFPversionStatus.getChannel3Status());
	    ofpStatusList.add(OFPversionStatus.getChannel4Status());

	    wdmStatusList.add(WDMStatus.getChannel1Status());
	    wdmStatusList.add(WDMStatus.getChannel2Status());
	    wdmStatusList.add(WDMStatus.getChannel3Status());
	    wdmStatusList.add(WDMStatus.getChannel4Status());

	    
		// Check if all OFP versions are equal
		if (OFPversionStatus.getChannel1Status().equals(OFPversionStatus.getChannel2Status())
				&& OFPversionStatus.getChannel2Status().equals(OFPversionStatus.getChannel3Status())
				&& OFPversionStatus.getChannel3Status().equals(OFPversionStatus.getChannel4Status())) {

			// OFP Present OK
			ofpMatch = true;
			Debug.printDebug("All channels have the same OFP version.");

		} else {
			// NOT OK
			ofpMatch = false;
			Debug.printDebug("Channels have different OFP versions.");
		}

		String expectedWDMStatus = "0xfffe6020";

		if ("UUT1".equals(currentSessionDetails.getUutId())) {
			expectedWDMStatus = "0xffff6000";
		}

		if ("UUT1".equals(currentSessionDetails.getUutId())) {
			// Check if all WDM statuses are "online"
			if (expectedWDMStatus.equals(WDMStatus.getChannel1Status())
					&& expectedWDMStatus.equals(WDMStatus.getChannel2Status())
					&& expectedWDMStatus.equals(WDMStatus.getChannel3Status())
					&& expectedWDMStatus.equals(WDMStatus.getChannel4Status())) {

				// WDM Status OK
				wdmMatch = true;
				Debug.printDebug("All channels WDM status are UP.");

			} else {
				// NOT OK
				wdmMatch = false;
				Debug.printDebug("All channels WDM status are not UP.");
			}
		} else {
			// mk1 and mk2
			if (!WDMStatus.getChannel1Status().equals("offline") && !WDMStatus.getChannel2Status().equals("offline")
					&& !WDMStatus.getChannel3Status().equals("offline")
					&& !WDMStatus.getChannel4Status().equals("offline")) {

				// WDM Status OK
				wdmMatch = true;
				Debug.printDebug("All channels WDM status are UP.");

			} else {
				// NOT OK
				wdmMatch = false;
				Debug.printDebug("All channels WDM status are not UP.");
			}

		}

		if (ofpMatch && wdmMatch) {
			response.setResponseCode(200);
			response.setResponseMessage("All channels have the same OFP version and all WDM channels are UP.");
		} else if (ofpMatch && !wdmMatch) {
			response.setResponseCode(300);
			response.setResponseMessage("All channels have the same OFP version but not all WDM channels are UP.");
		} else if (!ofpMatch && wdmMatch) {
			response.setResponseCode(400);
			response.setResponseMessage("Channels have different OFP versions but all WDM channels are UP.");
		}
		
		response.setOfpStatus(ofpStatusList);
		response.setWdmStatus(wdmStatusList);
		Debug.printDebug("responseId while pbit Test:-> " + response.getResponseCode());
		return response;
	}

	public void check1(String testTypeId, String ofpConfigPath) {
		checkMethod = true;
		boolean aets1SwitchFlaggg = true;

		// Check and update AETS process status
		exitAitess1Command();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		configureAitess(ofpConfigPath);

		String uutId = currentSessionDetails.getUutId();
		RunConfigurationService r = new RunConfigurationService();
		String currentRunConfigId = r.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		AitessConfigurationDetails currentAitess = r.getAitessDetailsByRunConfigId(currentRunConfigId);

		launcherFuture1
				.thenRun(() -> aitess1ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));

		while (aets1SwitchFlaggg) {
			// System.out.print(" 1 ");
			if (aitessRunning.isAitess1ReloadConfigured()) {
				aets1SwitchFlaggg = false;
				// currentSessionDetails.setRunConfigId(currentRunConfigId);
				Debug.printDebug("AFTER 1 SWITCHING RUN CONFIG GETS pbit UPDATED:: ------>>> "
						+ currentSessionDetails.getRunConfigId());

			}
		}
		aitessRunning.setAitess1ReloadConfigured(false);
		aitessRunning.setAitess1Switched(false);
		StateMachine.setPreviousRunConfigId(currentRunConfigId);
		Debug.printDebug("UPDATED previous runConfig before pbit ::------" + StateMachine.getPreviousRunConfigId());

	}

	// ORDER OUTPUT FOLDER COPYING AS PER LAST DATE MODIFIED
	public void sortOutputFolder() {
		String outputFolderPath = aitessDir + File.separator + "output";
		Debug.printDebug("------ OUTPUT FOLDER CHECK : " + outputFolderPath);
		File directory = new File(outputFolderPath);
		// Check if the path is a directory
		if (!directory.isDirectory()) {
			Debug.printDebug("The specified path is not a directory.");
			return;
		}
		File[] files = directory.listFiles();
		if (files == null || files.length == 0) {
			Debug.printDebug("No files found in the directory.");
			return;
		}
		// Get today's date
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");
		String todayDate = todayFormat.format(calendar.getTime());
		// Move files into date-specific folders
		for (File file : files) {
			if (file.isFile()) {
				long lastModifiedTime = file.lastModified();
				Date lastModifiedDate = new Date(lastModifiedTime);
				String dateFolderName = todayFormat.format(lastModifiedDate);
				// Only create a folder and move files if the last modified date is not today
				if (!todayDate.equals(dateFolderName)) {
					File dateFolder = new File(directory, dateFolderName);
					if (!dateFolder.exists()) {
						if (dateFolder.mkdir()) {
							// Debug.printDebug("Folder created: " + dateFolder.getAbsolutePath());
						} else {
							// Debug.printDebug("Failed to create folder: " +
							// dateFolder.getAbsolutePath());
							continue;
						}
					}
					try {
						Path targetPath = dateFolder.toPath().resolve(file.getName());
						Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
						// Debug.printDebug("Moved: " + file.getName() + " to " +
						// dateFolder.getAbsolutePath());
					} catch (IOException e) {
						Debug.printDebug("Failed to move file: " + file.getName() + " - " + e.getMessage());
					}
				}
			}
		}
	}

	private void appendText(TextArea textArea, String content) {
		Task<Void> appendTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> {

					textBuffer.append(content);
					LINES_COUNT++;

					if ((LINES_COUNT == MAX_LINES) || content.contains(">>>") || content.contains("@")
							|| content.contains("macname") || content.contains("wait") || content.contains("Y/N")) {
//	                	    textArea.setText(textBuffer.toString()); // Replace entire content with text buffer
						textArea.appendText(textBuffer.toString()); // append content with text buffer

						textArea.setScrollTop(Double.MAX_VALUE); // Scroll to bottom

						textBuffer.setLength(0); // Clear buffer
						LINES_COUNT = 0;

						int maxTextLine = textArea.getParagraphs().size();

						if (maxTextLine >= MAX_TOTAL_LINES) {
							textArea.deleteText(0, maxTextLine - MAX_TOTAL_LINES);
						}

					}
//	                	 if (textArea.getParagraphs().size() >= 2500) {
////	                         int firstLineEndIndex = textArea.getText().indexOf("\n") + 1;
//	                         int maxTextLine = textArea.getParagraphs().size();
//	                         int DeleteLinendex = maxTextLine - 2500;
//	                         textArea.deleteText(0, DeleteLinendex);
//	                     }
//	                	 textArea.appendText(content);
//	                	 textArea.requestFocus();
//	                	 textArea.setScrollTop(Double.MAX_VALUE);
				});
				Thread.sleep(10);
				return null;
			}
		};
		new Thread(appendTask).start();
	}

}