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
import com.teclever.dfcc.utils.Debug;
import com.teclever.utils.ProcessControl;

public class LoadDriverProcessControlManagement {

	private static LoadDriverProcessControlManagement instance;

	public enum LoadMode {
		STARTUP, SWITCH, LOGOUT, CARD
	}

	private BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> aimMilBQueue = new ArrayBlockingQueue<>(10000);

	private Thread loadDriverLaunchingThread;

	private Thread outputProcessingThread;
	private Thread aimMilOutputProcessingThread;

	boolean flag = true;
	boolean aimFlag = true;
	private String currentLoadedDriver = null;
	private boolean closeCommandExecuted = false;
	private boolean endOfLoadDriverCommand = false;

	private ProcessControl loadDriverProcessController;
	private ProcessControl aimMil;

	CompletableFuture<Void> launcherFuture = new CompletableFuture<>();
	CompletableFuture<Void> launcherFuture1 = new CompletableFuture<>();

	private LoadDriverProcessControlManagement() {
		loadDriverProcessController = new ProcessControl(loadDriverBQueue);
		aimMil = new ProcessControl(aimMilBQueue);
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
			Debug.printDebug(" DB CardName :: " + d.getCardName());
			Debug.printDebug(" DB Count :: " + d.getTotalNumberOfCards());
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
			                // Initialize a map to track card match status
			                Map<String, Boolean> cardStatusMap = new HashMap<>();
			                for (DbDriverCard d : dbDriverCards) {
			                    cardStatusMap.put(d.getCardName(), false); // Default to false (not OK)
			                }
			                
			                while (flag) {
			                    String output = loadDriverBQueue.take();
			                    Debug.printDebug("loadDriver:: " + output);
			                    
			                    for (DbDriverCard d : dbDriverCards) {
			                        String cardIdentificationText = d.getCardIdentificationText();
			                        DriverCard parsedCards = dm.parseLineNEWtrim(output, cardIdentificationText);
			                        
			                        if (parsedCards.getResponse().getResponseCode() == 1) {
			                            if (dbMap.get(parsedCards.getCardName()) != null) {
			                                cardStatusMap.put(parsedCards.getCardName(), true); // Mark as OK
			                                parsedCards.setMsg("OK");
			                                responseDriverCards.add(parsedCards);
			                            }else {
				                            parsedCards.setMsg("NOT OK");
				                            responseDriverCards.add(parsedCards);
				                        }
			                        } 
			                    }
			                    
			                    if (output.contains("Starting AETS RT Scheduler") || output.contains("Staring AETS RT Scheduler")) {
			                        Debug.printDebug("LAST LINE :  " + output);
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
			    Debug.printDebug("---- RESPONSE LIST SIZE----" + response.getDriverCardDetails().size());
			    return response;


			case CARD:
				aimMil.LaunchingProcess(command, launcherFuture1);

				launcherFuture1.thenRun(() -> {
					aimMil.ReadingProcess();
					aimMilOutputProcessingThread = new Thread(() -> {
						try {
							aimFlag = true;
							while (aimFlag) {
								String output = aimMilBQueue.take();
								Debug.printDebug("aimMil :: " + output);

								DriverCard aimMil = dm.parseLineAIM(output);
								if (aimMil.getResponse().getResponseCode() == 1) {
									aimMil.setCardName("aim_mil");
									aimMil.setMsg("OK");
									responseDriverCards.add(aimMil);
								}

								Debug.printDebug(output);
								if (output.contains("aim_mil")) {
									aimFlag = false;
								}
							}
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							Thread.currentThread().interrupt();
						}
						aimMilOutputProcessingThread.interrupt();
					});
					aimMilOutputProcessingThread.start();

				});

				launcherFuture1.join();
				if (aimMilOutputProcessingThread != null) {
					aimMilOutputProcessingThread.join();
				}
				DriverCardDetailsResponse response1 = new DriverCardDetailsResponse();

				while (aimFlag) {
					System.out.print("- ");
				}

				response1.setDriverCardDetails(responseDriverCards);
				Debug.printDebug("---- RESPONSE LIST SIZE AIM_MIL ----" + response1.getDriverCardDetails().size());
				return response1;

			case SWITCH:
				launcherFuture.thenRun(() -> {
					loadDriverProcessController.ReadingProcess();
					outputProcessingThread = new Thread(() -> {
						try {
							flag = true;
							while (flag) {
								String output = loadDriverBQueue.take();
								Debug.printDebug("loadDriver:: " + output);

//								if (getCloseLoadDriverEndMatchingLine(output) != null) {
//									closeCommandExecuted = true;
//								}

								if (output.contains("Starting AETS RT Scheduler")
										|| output.contains("Staring AETS RT Scheduler")) {
									Debug.printDebug("LAST LINE :  " + output.contains("Starting AETS RT Scheduler")
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

				Thread.sleep(2000);
				
				launcherFuture.thenRun(
						() -> loadDriverProcessController.WritingProcess("sudo " + unloadCommand + "\n"));
				
//				if (closeCommandExecuted == true) {
//					if (unloadCommand != null && !unloadCommand.isEmpty()) {
//						launcherFuture.thenRun(
//								() -> loadDriverProcessController.WritingProcess("sudo " + unloadCommand + "\n"));
//					}
//					closeCommandExecuted = false;
//				}

				launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("sudo " + command + "\n"));

				launcherFuture.join();
				if (outputProcessingThread != null) {
					outputProcessingThread.join();
				}
				if (endOfLoadDriverCommand == true) {
					endOfLoadDriverCommand = false;
					break;
				}

			case LOGOUT:
				aitessProcessControlManagement.exitAitess1Command();
				aitessProcessControlManagement.exitAitess2Command();
				launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("\u0003" + "\n"));
				launcherFuture
						.thenRun(() -> loadDriverProcessController.WritingProcess("sudo " + unloadCommand + "\n"));

				stopLoadDriverLaunchingThread();

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
			Debug.printDebug("END LINE:: " + line);
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