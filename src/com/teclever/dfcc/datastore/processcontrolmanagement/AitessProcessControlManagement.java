package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ChannelStatus;
import com.teclever.dfcc.datastore.dto.ChannelTemperature;
import com.teclever.dfcc.datastore.terminalmanagement.ChannelStatusParser;
import com.teclever.dfcc.datastore.terminalmanagement.TemperatureParser;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.OFPversionStatus;
import com.teclever.dfcc.stateMachine.StateMachine.OnlineStatus;
import com.teclever.dfcc.stateMachine.StateMachine.TestState;
import com.teclever.dfcc.stateMachine.StateMachine.WDMStatus;
import com.teclever.dfcc.stateMachine.StateMachine.aitessRunning;
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
	private final AtomicBoolean dfccCheckStstusStarted = new AtomicBoolean(false);
    private final AtomicReference<String> currentCommand = new AtomicReference<>("Empty");
	private boolean switchAitessMethod = false;
	private boolean switchAitess1Method = false;
	private boolean checkMethod = false;

	boolean flag;

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

		RunConfigurationService runConfigurationService = new RunConfigurationService();

		String uutId = StateMachine.currentSessionDetails.getUutId();
		System.out.println("UUT ID inside launch() --------" + uutId);
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);
		System.out.println("WHILE CALLING launchAitess() runconfigId-------------" + currentRunConfigId);
		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);
		System.out.println("WHILE CALLING launchAitess() AITESS-------------" + currentAitess.getAitessName());

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
				System.out.println("AITESS 1 LAUNCHED COMMAND executed");
			}

			System.out.println("moving to aitess 2........................" + !StateMachine.isAitess2Launched());
			// launch aitess2 inside aitess1 folder
			if (!StateMachine.isAitess2Launched()) {
				launchAitess2("cd " + aitess1Dir.toString() + "\n");
				launcherFuture2.thenRun(
						() -> aitess2ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));
				System.out.println("AITESS 2 LAUNCHED COMMAND executed");

			}

			StateMachine.setPreviousRunConfigId(currentRunConfigId);
			System.out.println(
					"BOTH AITESS LAUNCHED launch() after that currentRunConfig set to SM-->>" + currentRunConfigId);

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
					String result = "";
					String userExitErrorResult = null;

					while (true) {
						s2 = s1 = aitess1ReadQ.take();
//						System.out.println("aitess1 output:: " + s1);

						if (!aitessRunning.isAitess1Exited()) {
							aitessRunning.setAitess1Exited(true);
						}

						if (switchAitessMethod == true) {
							if (s1.contains(">>>")) {
								System.out.println("END FOR AITESS FOUNDED ---------**********-----------");
								aitessRunning.setAitess1Switched(true);
								switchAitessMethod = false;
							} else if (s1.contains("ValueError")) {
								System.out.println(
										"END FOR AITESS FAILED  FOUNDED --XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX-");
								aitessRunning.setAitess1SwitchedFailed(true);
								switchAitessMethod = false;
							}
						}
						if (StateMachine.isRunCommand() == true) {
							if (s1.contains(">>>")) {
								System.out.println("command end line founded . . . . . . . . . ");
								StateMachine.setAitess1CommandFinished(true);
								StateMachine.setRunCommand(false);
							}
						}

						if (checkMethod == true) {
							if (s1.contains(">>>")) {
								System.out.println("END LINE FOR RELOAD CONFIG FOUNDED -------777777777777777777777");
								aitessRunning.setAitess1ReloadConfigured(true);
								checkMethod = false;
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
						if (userActionLine != null || s1.contains("Do you wish to continue")) {
							StateMachine.setUserAction(userActionLine);
							StateMachine.getUserActionFlag().set(true);
						}

						// condition if test stared
						if (testStarted) {
//							System.out.println("s2 :: " + s2);
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

//							System.out.println("aitess1 finalLine:: " + finalLine);

							if (finalLine.contains("Parse Error")) {

//								System.out.println("aitess1 result1:: " + result);
								if (result.equals("")) {

//									System.out.println("aitess1 result2:: " + result);
									aitess1ResultQ.put("PARSE ERROR");
									testStarted = false;
								}
							}
							// rdf file name
							if (getRdfFileName(finalLine) != null) {
								result = getRdfFileName(finalLine);
							}

							String endLine = getEndMatchingLine(finalLine);
							if (endLine != null) {
								if (result != null) {
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

						if (switchAitess1Method == true) {
							if (output.contains(">>>")) {
								System.out.println("END FOR AITESS 2 FOUNDED ---------**********-----------");

								aitessRunning.setAitess2Switched(true);
								switchAitess1Method = false;
							} else if (output.contains("ValueError")) {
								System.out.println(
										"END FOR AITESS 2  FAILED  FOUNDED --XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX-");

								aitessRunning.setAitess2SwitchedFailed(true);
								switchAitess1Method = false;
							}
						}

						cleanText = cleanOutput(output);
						cleanText = cleanText.replaceAll("\\(B", "");
						cleanText = cleanText.replaceAll("]104", "");
						final String finalLine = cleanText;
//						aitess2ResultQ.put(finalLine);

						if (!aitessRunning.isAitess2Exited()) {
							aitessRunning.setAitess2Exited(true);
						}
						if (dfccCheckStstusStarted.get()==true) {
							channelStatusParser.getChannelStatus(finalLine);
							channelStatusParser.getOFPversionStatus(finalLine);
				            String command1 = currentCommand.get();

							switch (command1) {
							case "OnlineStatusCommand":
							channelStatus = channelStatusParser.getChannelStatus(finalLine);
								break;

							case "Mk1ScTemperatureCommand":
								channelTemperature = temperatureParser.getChannelTemperature(finalLine);						
								break;

							case "Mk1AecTemperatureCommand":
								channelTemperature = temperatureParser.getChannelTemperature(finalLine);						
								break;

							case "OFPversion":
								channelStatus = channelStatusParser.getOFPversionStatus(finalLine);					
								break;

							case "WDMversion":
								channelStatus = channelStatusParser.getWDMStatus(finalLine);
								break;

							default:
								System.out.println(" --> AETS 2 SWITCH   -" + currentCommand);
								break;
							}
						}

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

		checkChannelStatus();
		String aets1QResponse = null;
		try {
			testStarted = true;

			launcherFuture1.thenRun(() -> aitess1ProcessControl.WritingProcess("@ " + tpfFileName + "\n"));
			boolean flag = true;
			while (flag) {
				if (aitess1ResultQ != null && aitess1ResultQ.peek() != null) {
					aets1QResponse = aitess1ResultQ.take();
					System.out.println(" --> AETS 1 Q Data : " + aets1QResponse);
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

			testStarted = false;
			System.out.println("Perform Test() Return : " + aets1QResponse);

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

	public void WriteDfccPowerOnCommandToAitess2() {

		try {
			dfccCheckStstusStarted.set(true);;
			launcherFuture2.thenRun(
					() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getDfccPowerOnCommand() + "\n"));
			Thread.sleep(500);
			launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess("gse_conn=1" + "\n"));
			System.out.println("EXECUTED gse_conn=1");

			launcherFuture2.thenRun(
					() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOnlineStatusCommand() + "\n"));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		currentCommand.set("DfccPowerOnCommand");
		dfccCheckStatus.getDfccPowerStatus().set(true);
		dfccCheckStstusStarted.set(false);;

	}

	// check before any test is get started
	public void checkChannelStatus() {
		dfccCheckStstusStarted.set(true);
		launcherFuture2
				.thenRun(() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOnlineStatusCommand() + "\n"));
		dfccCheckStstusStarted.set(false);

		if (OnlineStatus.getChannel1Status() == "offline" || OnlineStatus.getChannel2Status() == "offline"
				|| OnlineStatus.getChannel3Status() == "offline" || OnlineStatus.getChannel4Status() == "offline") {

			WriteDfccPowerOnCommandToAitess2();
		} else {

		}
	}

	public void WriteDfccPowerOffCommandToAitess2() {
		try {
			dfccCheckStstusStarted.set(true);
			launcherFuture2.thenRun(
					() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getDfccPowerOffCommand() + "\n"));
			Thread.sleep(500);
			launcherFuture2.thenRun(
					() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOnlineStatusCommand() + "\n"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentCommand.set("DfccPowerOffCommand");
		dfccCheckStatus.getDfccPowerStatus().set(false);
		dfccCheckStstusStarted.set(false);;

	}

	public void WriteMacroCommandToAitess2(String macroCommand) {
		launcherFuture2.thenRun(() -> aitess2ProcessControl.WritingProcess(macroCommand + "\n"));
	}

	public void WriteAitess2Command1() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			if (StateMachine.getTestState() != TestState.RUNNING && StateMachine.aitessRunning.isAitess2Switched()) {
				executeCommands();
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	public void executeCommands() {

		dfccCheckStatusThread = new Thread(() -> {
			if(StateMachine.getTestState() == TestState.RUNNING) {
				return;
			}
			try {
				StateMachine.setTextArea(false);
				dfccCheckStstusStarted.set(true);

				// ONLINE STATUS
				launcherFuture2.thenRun(
						() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOnlineStatusCommand() + "\n"));
				currentCommand.set("OnlineStatusCommand");
				Thread.sleep(1000);

				// SC TEMPERATURE
				launcherFuture2.thenRun(() -> aitess2ProcessControl
						.WritingProcess(dfccCheckStatus.getMk1ScTemperatureCommand() + "\n"));
				currentCommand.set("Mk1ScTemperatureCommand");
				Thread.sleep(1000);

				// AEC TEMPERATURE
				launcherFuture2.thenRun(() -> aitess2ProcessControl
						.WritingProcess(dfccCheckStatus.getMk1AecTemperatureCommand() + "\n"));
				currentCommand.set("Mk1AecTemperatureCommand");
				Thread.sleep(1000);

				// OFP VERSION
				launcherFuture2.thenRun(() -> aitess2ProcessControl
						.WritingProcess(dfccCheckStatus.getOfpVersionStatusCommand() + "\n"));
				currentCommand.set("OFPversion");
				Thread.sleep(1000);

				// WDM STATUS
				launcherFuture2.thenRun(
						() -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getWdmStatusCommand() + "\n"));
				currentCommand.set("WDMversion");
				Thread.sleep(1000);
				dfccCheckStstusStarted.set(false);
				StateMachine.setTextArea(true);
			} catch (Exception e) {
				// TODO: handle exception
			}
		});
		dfccCheckStatusThread.start();

	}

	public void check(String testTypeId) {
		System.out.println("---ENTERING check() passed testTypeIdl---------------" + testTypeId);
		RunConfigurationService runConfigurationService = new RunConfigurationService();
		LoadDriverProcessControlManagement pcm = LoadDriverProcessControlManagement.getInstance();

		String smRunConfigId = StateMachine.getPreviousRunConfigId();
		System.out.println("PREVIOUS RUN CONFIG check() ::--------" + smRunConfigId);
		AitessConfigurationDetails smAitess = runConfigurationService.getAitessDetailsByRunConfigId(smRunConfigId);
		System.out.println("PREVIOUS  AITESS check() ::" + smAitess.getAitessName());

		String uutId = currentSessionDetails.getUutId();
		String currentRunConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutId, testTypeId);

		System.out.println("CURRENT RUN CONFIG based on testType check()::----------" + currentRunConfigId);

		AitessConfigurationDetails currentAitess = runConfigurationService
				.getAitessDetailsByRunConfigId(currentRunConfigId);
		System.out.println("CURRENT AITESS check() :: " + currentAitess.getAitessName());

		System.out.println("STATE MACHINE AITESS driverName:: -----" + smAitess.getDriverName());
		System.out.println("CURRENT AITESS driverName:: -----" + currentAitess.getDriverName());

		// CHECKING DRIVER
		if (!smAitess.getDriverName().equals(currentAitess.getDriverName())) {
			// NOT MATCHED
			System.out.println("Switching Load Driver::---- " + smAitess.getDriverName() + " to ::---- "
					+ currentAitess.getDriverName());
			pcm.loadDriver(currentAitess.getLoadDriverCommand(), smAitess.getUnloadDriverCommand(), 0,
					LoadDriverProcessControlManagement.LoadMode.SWITCH);
			if (aitessRunning.isAitess1Exited() == true && aitessRunning.isAitess2Exited() == true) {
				switchAitess(testTypeId);
			}
		} else {
			// MATCHED
			System.out.println(
					"Load Driver Matches::---- " + smAitess.getDriverName() + " == " + currentAitess.getDriverName());

			// CHECKING AITESS
			if (!smAitess.getAitessName().equals(currentAitess.getAitessName())) {
				// NOT MATCHED
				System.out.println("Aitess Not Matched:: OLD AITESS:---- " + smAitess.getAitessName()
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
				System.out.println(
						"Aitess Matches::---- " + smAitess.getAitessName() + " == " + currentAitess.getAitessName());
				if (!smAitess.getConfigFile().equals(currentAitess.getConfigFile())) {
					try {
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
							System.out.println("AFTER 1 SWITCHING RUN CONFIG GETS UPDATED:: ------>>> "
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
		System.out.println("UPDATED previous runConfig Id ::------" + StateMachine.getPreviousRunConfigId());
		System.out.println("FUNCTION ENDED----------------------->>>>>>>>>>>>>>>>>>");
	}

	public void switchAitess(String testTypeId) {
		switchAitessMethod = true;
		switchAitess1Method = true;
		boolean aets1SwitchFlag = true;
		boolean aets2SwitchFlag = true;
		System.out.println("Entering into Switching AITESS");
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
				System.out.println("AFTER 1 SWITCHING RUN CONFIG GETS UPDATED:: ------>>> "
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
				System.out.println("AFTER  2 SWITCHING RUN CONFIG GETS UPDATED:: ------>>> "
						+ currentSessionDetails.getRunConfigId());
			} else if (aitessRunning.isAitess2SwitchedFailed()) {
				System.out.println("AFTER  2 SWITCHING Failed:: ------>>> " + currentSessionDetails.getRunConfigId());

				aets2SwitchFlag = false;
			}

		}
//			currentSessionDetails.setRunConfigId(currentRunConfigId);
		System.out.println("< ======   BOTH AETS SWITCH DONE  ===== >");
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

	// POWER ON
	public String getPowerOnLine(String line) {
		Pattern pscFccPattern = Pattern.compile("psc_fcc_pwr_on\\(\\d+\\) \\+ \\d+ = 0x1");
		Matcher pscFccMatcher = pscFccPattern.matcher(line);

		if (pscFccMatcher.find()) {
			System.out.println("END LINE:: " + line);
			return line;
		}

		return null;
	}

	// POWER OFF
	public String getPowerOffLine(String line) {
		Pattern pscFccPattern = Pattern.compile("psc_fcc_pwr_on\\(\\d+\\) \\+ \\d+ = 0x0");
		Matcher pscFccMatcher = pscFccPattern.matcher(line);

		if (pscFccMatcher.find()) {
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
			AitessConfigurationDetails currentAitess = runConfigurationService
					.getAitessDetailsByRunConfigId(currentRunConfigId);

			LoadDriverProcessControlManagement pcm = LoadDriverProcessControlManagement.getInstance();
			pcm.loadDriver(null, currentAitess.getUnloadDriverCommand(), 0,
					LoadDriverProcessControlManagement.LoadMode.LOGOUT);
			System.out.println("DRIVER <<< " + currentAitess.getDriverName() + " >>> UNLOADED");

			// kill pty process
			exitAitess1Command();
			exitAitess2Command();
			
			//aitess2thread stop
			shutdownScheduler();

		}

	}

	public void shutdownScheduler() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}
	
	
	
	public Response pbitCheck() {
	    Response response = new Response();
	    
	    boolean ofpMatch = false;
	    boolean wdmMatch = false;

	    try {
	        dfccCheckStstusStarted.set(true);

	        // Write OFP version command
	        launcherFuture2.thenRun(
	                () -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getOfpVersionStatusCommand() + "\n"));
	        currentCommand.set("OFPversion");
	        Thread.sleep(500);

	        // Write WDM version command
	        launcherFuture2.thenRun(
	                () -> aitess2ProcessControl.WritingProcess(dfccCheckStatus.getWdmStatusCommand() + "\n"));
	        currentCommand.set("WDMversion");

	    } catch (InterruptedException e) {
	        e.printStackTrace();
	        response.setResponseCode(500);
	        response.setResponseMessage("Error occurred: " + e.getMessage());
	        dfccCheckStstusStarted.set(false);
	        return response;
	    }
	    
	    dfccCheckStstusStarted.set(false);

	    // Check if all OFP versions are equal
	    if (OFPversionStatus.getChannel1Status().equals(OFPversionStatus.getChannel2Status()) 
	            && OFPversionStatus.getChannel2Status().equals(OFPversionStatus.getChannel3Status())
	            && OFPversionStatus.getChannel3Status().equals(OFPversionStatus.getChannel4Status())) {
	        
	        // OFP Present OK
	        ofpMatch = true;
	        System.out.println("All channels have the same OFP version.");
	        
	    } else {
	        // NOT OK
	        ofpMatch = false;
	        System.out.println("Channels have different OFP versions.");
	    }
	    
	    // Check if all WDM statuses are "UP"
	    if ("UP".equals(WDMStatus.getChannel1Status()) && "UP".equals(WDMStatus.getChannel2Status())
	            && "UP".equals(WDMStatus.getChannel3Status()) && "UP".equals(WDMStatus.getChannel4Status())) {
	        
	        // WDM Status OK
	        wdmMatch = true;
	        System.out.println("All channels WDM status are UP.");
	        
	    } else {
	        // NOT OK
	        wdmMatch = false;
	        System.out.println("All channels WDM status are not UP.");
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

	    return response;
	}
	
	public void  check1(String testTypeId, String ofpConfigPath) {
		checkMethod=true;
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

				AitessConfigurationDetails currentAitess = r
						.getAitessDetailsByRunConfigId(currentRunConfigId);

				launcherFuture1
				.thenRun(() -> aitess1ProcessControl.WritingProcess("sudo " + currentAitess.getAitessCommand() + "\n"));			

				while (aets1SwitchFlaggg) {
					// System.out.print(" 1 ");
					if (aitessRunning.isAitess1ReloadConfigured()) {
						aets1SwitchFlaggg = false;
						// currentSessionDetails.setRunConfigId(currentRunConfigId);
						System.out.println("AFTER 1 SWITCHING RUN CONFIG GETS pbit UPDATED:: ------>>> "
								+ currentSessionDetails.getRunConfigId());

					} 
				}
				aitessRunning.setAitess1ReloadConfigured(false);
				aitessRunning.setAitess1Switched(false);
				StateMachine.setPreviousRunConfigId(currentRunConfigId);
				System.out.println("UPDATED previous runConfig before pbit ::------" + StateMachine.getPreviousRunConfigId());


	}

}
