package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teclever.dfcc.datastore.dto.DbDriverCard;
import com.teclever.dfcc.datastore.dto.DriverCard;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.terminalmanagement.DriverManagement;
import com.teclever.dfcc.stateMachine.StateMachine.aitessRunning;
import com.teclever.utils.ProcessControl;

public class LoadDriverProcessControlManagement {

	private static LoadDriverProcessControlManagement instance;

	public enum LoadMode {
		STARTUP, SWITCH, LOGOUT
	}

	private BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(10000);
	private Thread loadDriverLaunchingThread;
	private Thread outputProcessingThread;
	boolean flag = true;
	private String currentLoadedDriver = null;
	private boolean closeCommandExecuted = false;
	private boolean endOfLoadDriverCommand = false;

	private ProcessControl loadDriverProcessController;

	CompletableFuture<Void> launcherFuture = new CompletableFuture<>();

	private LoadDriverProcessControlManagement() {
		loadDriverProcessController = new ProcessControl(loadDriverBQueue);
	}

	public static synchronized LoadDriverProcessControlManagement getInstance() {
		if (instance == null) {
			instance = new LoadDriverProcessControlManagement();
		}
		return instance;
	}

	public DriverCardDetailsResponse loadDriver(String command, String unloadCommand, int aitessId, LoadMode mode) {

		AitessProcessControlManagement aitessProcessControlManagement = AitessProcessControlManagement.getInstance();
		DriverManagement dm = new DriverManagement();
		List<DbDriverCard> dbDriverCards = dm.getDriverCardDetailsBasedOnAitess(aitessId);

		List<DriverCard> responseDriverCards = new ArrayList<DriverCard>();

		Map<String, String> dbMap = new HashMap<>();

		for (DbDriverCard d : dbDriverCards) {
			dbMap.put(d.getCardName(), d.getTotalNumberOfCards());
			System.out.println(" DB CardName :: " + d.getCardName());
			System.out.println(" DB Count :: " + d.getTotalNumberOfCards());
		}

		try {
			switch (mode) {
			case STARTUP:
				loadDriverProcessController.LaunchingProcess(command, launcherFuture);

				launcherFuture.thenRun(() -> {
					loadDriverProcessController.ReadingProcess();
					outputProcessingThread = new Thread(() -> {
						try {
							flag = true;
							while (flag) {
								String output = loadDriverBQueue.take();
								System.out.println("loadDriver:: " + output);
								for (DbDriverCard d : dbDriverCards) {
									String cardIdentificationText = d.getCardIdentificationText();
									DriverCard parsedCards = dm.parseLine1(output, cardIdentificationText);

									if (parsedCards.getResponse().getResponseCode() == 1) {

										if (dbMap.get(parsedCards.getCardName()) != null) {
											System.out.println("DB CARD COUNT : " + dbMap.get(parsedCards.getCardName())
													+ " PARSED CARD COUNT  " + parsedCards.getFoundedNumberOfCards());
											if (dbMap.get(parsedCards.getCardName())
													.equals(parsedCards.getFoundedNumberOfCards())) {
												parsedCards.setExpectedCountOfCards(d.getTotalNumberOfCards());
												parsedCards.setMsg("OK");
												responseDriverCards.add(parsedCards);
											} else {
												parsedCards.setExpectedCountOfCards(d.getTotalNumberOfCards());
												parsedCards.setMsg("NOT OK");
												responseDriverCards.add(parsedCards);
											}
										}
									}
								}
								System.out.println(output);
								if (output.contains("Starting AETS RT Scheduler")
										|| output.contains("Staring AETS RT Scheduler")) {
									System.out.println("LAST LINE :  " + output.contains("Starting AETS RT Scheduler"));
									flag = false;
								}
							}
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							Thread.currentThread().interrupt();
						}
						outputProcessingThread.interrupt();
					});
					outputProcessingThread.start();

				});

				launcherFuture.join();
				if (outputProcessingThread != null) {
					outputProcessingThread.join();
				}
				DriverCardDetailsResponse response = new DriverCardDetailsResponse();

				while (flag) {
					System.out.print("- ");
				}

				response.setDriverCardDetails(responseDriverCards);
				System.out.println("---- RESPONSE LIST SIZE----" + response.getDriverCardDetails().size());
				return response;

			case SWITCH:
				launcherFuture.thenRun(() -> {
					loadDriverProcessController.ReadingProcess();
					outputProcessingThread = new Thread(() -> {
						try {
							flag = true;
							while (flag) {
								String output = loadDriverBQueue.take();
								System.out.println("loadDriver:: " + output);

								if (getCloseLoadDriverEndMatchingLine(output) != null) {
									closeCommandExecuted = true;
								}

								if (output.contains("Starting AETS RT Scheduler")
										|| output.contains("Staring AETS RT Scheduler")) {
									System.out.println("LAST LINE :  " + output.contains("Starting AETS RT Scheduler")
											+ output.contains("Staring AETS RT Scheduler"));
									endOfLoadDriverCommand = true;
								}

								if (endOfLoadDriverCommand) {
									flag = false;
								}
							}
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							Thread.currentThread().interrupt();
						}
						outputProcessingThread.interrupt();
					});
					outputProcessingThread.start();
				});

				aitessRunning.setAitess1Exited(false);
				aitessProcessControlManagement.exitAitess1Command();
				aitessRunning.setAitess2Exited(false);
				aitessProcessControlManagement.exitAitess2Command();

				launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("\u0003" + "\n"));

				if (closeCommandExecuted == true) {
					if (unloadCommand != null && !unloadCommand.isEmpty()) {
						launcherFuture.thenRun(
								() -> loadDriverProcessController.WritingProcess("sudo " + unloadCommand + "\n"));
					}
					closeCommandExecuted = false;
				}

				launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("sudo " + command + "\n"));

				launcherFuture.join();
				if (outputProcessingThread != null) {
					outputProcessingThread.join();
				}
				if (endOfLoadDriverCommand == true) {
					endOfLoadDriverCommand = false;
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("An error occurred while loading the driver: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private String getCloseLoadDriverEndMatchingLine(String line) {
		Pattern exitLinePattern = Pattern.compile("\\*{71}");
		Matcher exitLineMatcher = exitLinePattern.matcher(line);

		if (exitLineMatcher.find()) {
			System.out.println("END LINE:: " + line);
			return line;
		}
		return null;
	}

	private void stopLoadDriverLaunchingThread() {
		if (loadDriverLaunchingThread != null) {
			loadDriverLaunchingThread.interrupt();
		}
	}

	private boolean isCurrentDriver(String driverCommand) {
		return driverCommand.equals(currentLoadedDriver);
	}

	private void setCurrentLoadedDriver(String driverCommand) {
		this.currentLoadedDriver = driverCommand;
	}

}