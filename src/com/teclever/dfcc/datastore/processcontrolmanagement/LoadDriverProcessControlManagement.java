package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
		STARTUP, UNLOADMODE, LOGOUT, CARD, AUX, KILL, SWITCH
	}

	private BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> aimMilBQueue = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> auxBQueue = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> killBQueue = new ArrayBlockingQueue<>(10000);
	private List<String> cardName = new ArrayList<String>();
	private Thread loadDriverLaunchingThread;

	private Thread outputProcessingThread;
	private Thread aimMilOutputProcessingThread;
	private Thread killProcessingThread;
	private Thread auxProcessingThread;

	boolean flag = true;
	boolean aimFlag = true;
	boolean auxflag = true;
	boolean killflag = true;
	private boolean endOfLoadDriverCommand = false;

	private ProcessControl loadDriverProcessController;
	private ProcessControl aimMil;
	private ProcessControl kill;
	private ProcessControl aux;
	
	

	CompletableFuture<Void> launcherFuture = new CompletableFuture<>();
	CompletableFuture<Void> launcherFuture1 = new CompletableFuture<>();
	CompletableFuture<Void> killFuture = new CompletableFuture<>();
	CompletableFuture<Void> auxFuture = new CompletableFuture<>();

	private LoadDriverProcessControlManagement() {
		loadDriverProcessController = new ProcessControl(loadDriverBQueue);
		aimMil = new ProcessControl(aimMilBQueue);
		kill = new ProcessControl(killBQueue);
		aux = new ProcessControl(auxBQueue);
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
		Map<String, Boolean> cardStatusMap = new HashMap<>();//chnage on 13022026
		List<DriverCard> responseDriverCards = new ArrayList<DriverCard>();

		Map<String, String> dbMap = new HashMap<>();

		for (DbDriverCard d : dbDriverCards) {
			dbMap.put(d.getCardName(), d.getTotalNumberOfCards());
			cardStatusMap.put(d.getCardName(), false);
			Debug.printDebug("DB CardName :: " + d.getCardName());
		}
	

		try {
			Thread.sleep(1000);
			switch (mode) {
			case STARTUP:
				loadDriverProcessController.LaunchingProcess(command, launcherFuture);
				Debug.printDebug("Entering into START UP");
				launcherFuture.thenRun(() -> {
					loadDriverProcessController.ReadingProcess();
					outputProcessingThread = new Thread(() -> {
						try {
							flag = true;
//							Map<String, Boolean> cardStatusMap = new HashMap<>();
						

							while (flag) {
								String output = loadDriverBQueue.take();
								Debug.printDebug("loadDriver:: " + output);

								for (DbDriverCard d : dbDriverCards) {
									String cardIdentificationText = d.getCardIdentificationText();
									//DriverCard parsedCards = dm.parseLineNEWtrim(output, cardIdentificationText);
									
									//Driver CarD Issue Mani Changed
									DriverCard parsedCards = dm.parseLineNEWtrim(output, cardIdentificationText,aitessId);
									////System.out.println("SUSCEPT MANI DRIVER CARD ISSUE"+parsedCards.getCardName());
									if (parsedCards.getResponse().getResponseCode() == 1) {
										if (dbMap.get(parsedCards.getCardName()) != null) {
											////System.out.println("PASSED CARDS :::");
											if(!cardName.contains(parsedCards.getCardName()))
											{
											cardStatusMap.put(parsedCards.getCardName(), true);
											parsedCards.setMsg("OK");
											responseDriverCards.add(parsedCards);
											}
											////System.out.println("PASSESD CARD"+parsedCards.getCardName());
										} else {
											if (!cardName.contains(parsedCards.getCardName())) {
												// //System.out.println("FAILED CARDS :::");
												parsedCards.setMsg("NOT OK");
												responseDriverCards.add(parsedCards);
											}
											// //System.out.println("FAILED CARD"+parsedCards.getCardName());
										}
									}
								}

								if (output.contains("Starting AETS RT Scheduler")
										|| output.contains("Staring AETS RT Scheduler")) {
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
				
				// ADD THIS HERE  added for driver issue on 09-02-2026 sai
			

				DriverCardDetailsResponse response = new DriverCardDetailsResponse();
				while (flag) {
					System.out.print("- ");
				}
				
 				for (DbDriverCard d : dbDriverCards) {
				    if (!cardStatusMap.get(d.getCardName())) {
				        DriverCard notFound = new DriverCard();
				        notFound.setCardName(d.getCardName());
				        notFound.setMsg("NOT OK");
				        responseDriverCards.add(notFound);
				    }
				}
				

				response.setDriverCardDetails(responseDriverCards);
				return response;

			case CARD:
				Debug.printDebug("Enter into CARD ");
				aimMil.LaunchingProcess(command, launcherFuture1);

				launcherFuture1.thenRun(() -> {
					aimMil.ReadingProcess();
					aimMilOutputProcessingThread = new Thread(() -> {
					try {
							aimFlag = true;
							int emptyCount = 0;
							//boolean flag_c = false;
							//Edited By Mani And K1 For Intial Driver loading...For AIML Issue
							while (aimFlag) {
								//String output = aimMilBQueue.take();
								String output = aimMilBQueue.poll(3, TimeUnit.SECONDS);
	
								if (output == null) {
								    emptyCount++;
								    ////System.out.println("No response received... count: " + emptyCount);

								    if (emptyCount >= 3) {  // Wait max 9 seconds (3 x 3)
								        aimFlag = false;
								        ////System.out.println("No response received... count:  GO TO ====> If Loop" + emptyCount);
								    }
								    continue;
								}

								
//								////System.out.println("aimMil :: " + output);

								DriverCard aimMil = dm.parseLineAIM(output, unloadCommand);
								
								
								if (aimMil.getResponse().getResponseCode() == 1) {

									aimMil.setCardName("1553B MODULE");
									aimMil.setMsg("OK");
									//flag_c = true;
									aimFlag = false;
									responseDriverCards.add(aimMil);
								}
//								} else if (aimMil.getResponse().getResponseCode() == 0 && !flag_c)  {
//									aimMil.setCardName("1553B MODULE");
//									aimMil.setMsg("NOT OK");
//									responseDriverCards.add(aimMil);
//								}
								
								if (output.contains(unloadCommand)) {
									aimFlag = false;
								}
								
								

//								////System.out.println(output);
								//if (output.contains(unloadCommand)) {
//								if (output.contains("root#")) {
//									//aimFlag = false;
//									emptyCount++;
//								}
								
//								if (output.equals(null) || (output.equals(""))) {
//									emptyCount++;
//									////System.out.println("aimMil IF CONT :: " + output);
//									////System.out.println("aimMil IF CONT emptyCount:: " + emptyCount);
//								} else {
//									emptyCount = 0;
//									////System.out.println("else IF CONT :: " + output);
//								}
//
//								if (emptyCount > 2) {
//									aimFlag = false;
//								}
								
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
				Debug.printDebug("End of CARD ");
				response1.setDriverCardDetails(responseDriverCards);
				Debug.printDebug("---- RESPONSE LIST SIZE AIM_MIL ----" + response1.getDriverCardDetails().size());
				return response1;
			case SWITCH:
				Debug.printDebug("Start of SWITCH ");
				launcherFuture.thenRun(() -> {
					loadDriverProcessController.ReadingProcess();
					outputProcessingThread = new Thread(() -> {
						Debug.printDebug("Start of SWITCH Thread");
						try {
							flag = true;
							while (flag) {
								String output = loadDriverBQueue.take();
								Debug.printDebug("loadDriver:: " + output);

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
						Debug.printDebug("End of SWITCH Thread");
					});
					outputProcessingThread.start();
				});

				aitessRunning.setAitess1Exited(false);
				aitessProcessControlManagement.exitAitess1Command();
				aitessRunning.setAitess2Exited(false);
				aitessProcessControlManagement.exitAitess2Command();

				launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("\u0003" + "\n"));

				Thread.sleep(2000);
				Debug.printDebug("Strting of unloadCommand");

				launcherFuture
						.thenRun(() -> loadDriverProcessController.WritingProcess("sudo " + unloadCommand + "\n"));
				
				Debug.printDebug("Strting of loadCommand");

				launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("sudo " + command + "\n"));

				launcherFuture.join();
				if (outputProcessingThread != null) {
					outputProcessingThread.join();
				}
				if (endOfLoadDriverCommand == true) {
					endOfLoadDriverCommand = false;

					break;
				}

			case UNLOADMODE:
				Debug.printDebug("Start of SWITCH ");
				launcherFuture.thenRun(() -> {
					loadDriverProcessController.ReadingProcess();
					outputProcessingThread = new Thread(() -> {
						Debug.printDebug("Start of SWITCH Thread");
						try {
							flag = true;
							while (flag) {
								String output = loadDriverBQueue.take();
								Debug.printDebug("loadDriver:: " + output);

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
						Debug.printDebug("Switch End of SWITCH Thread");
					});
					outputProcessingThread.start();
				});

				aitessRunning.setAitess1Exited(false);
				aitessProcessControlManagement.exitAitess1Command();
				aitessRunning.setAitess2Exited(false);
				aitessProcessControlManagement.exitAitess2Command();

				launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("\u0003" + "\n"));

				Thread.sleep(2000);
				Debug.printDebug("Strting of unloadCommand");

				launcherFuture
						.thenRun(() -> loadDriverProcessController.WritingProcess("sudo " + unloadCommand + "\n"));
			
				Debug.printDebug("Strting of loadCommand");

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
				Debug.printDebug("End of LOGOUT ");
				break;

			case AUX:
				aux.LaunchingProcess(command, auxFuture);
				Debug.printDebug("Entering into AUX");

				// Use a list to hold extracted PIDs
				List<String> pidList = new ArrayList<>();
				auxFuture.thenRun(() -> {
					aux.ReadingProcess();
					auxProcessingThread = new Thread(() -> {
						try {
							auxflag = true;
							boolean isFirstCommand = true;
							int count = 0;

							while (auxflag) {
								String output = auxBQueue.take();
								Debug.printDebug("aux:: " + output);

								if (output.contains("rt_integrated") || output.endsWith("rt_integrated")) {
									Debug.printDebug("rt_integrated FOUND-------: " + output.contains("rt_integrated"));
									String[] parts = output.split("\\s+");

									for (int i = 0; i < parts.length; i++) {
										if (parts[i].equals("root") && i + 1 < parts.length) {
											String pid = parts[i + 1];
											pidList.add(pid);
											Debug.printDebug("Pid extracted ---: " + pid);
											auxflag = false;
										}
									}
								} else if (output.contains(command.trim())) {
									Debug.printDebug(" PID --- : " + pidList);
									if (count > 0) {
										isFirstCommand = false;
									}
									count++;
								}

								if (auxBQueue.size() == 0) {
									if (!isFirstCommand) {
										auxflag = false;
									}
								}
							}
							Debug.printDebug("Exit from AUX while Loop ");
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							Thread.currentThread().interrupt();
						} finally {
							auxProcessingThread.interrupt();
						}
						Debug.printDebug("End of Aux Process");
					});
					auxProcessingThread.start();
				});

				auxFuture.join();
				if (auxProcessingThread != null) {
					auxProcessingThread.join();
				}

				DriverCardDetailsResponse response2 = new DriverCardDetailsResponse();
				while (auxflag) {
					System.out.print("- ");
				}

				response2.setProcessIds(pidList);
				Debug.printDebug("---- PROCESS IDs LIST SIZE----" + response2.getProcessIds());
				return response2;

			case KILL:
				kill.LaunchingProcess(command, killFuture);
				Debug.printDebug("Entering into KILL");

				killFuture.thenRun(() -> {
					kill.ReadingProcess();
					killProcessingThread = new Thread(() -> {
						try {
							killflag = true;
							while (killflag) {
								String output = killBQueue.take();
								Debug.printDebug("kill:: " + output);

								killflag = false;
							}
							Debug.printDebug("Exit from KILL while Loop ");
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							Thread.currentThread().interrupt();
						} finally {
							killProcessingThread.interrupt();
						}
						Debug.printDebug("End of Kill Process");
					});
					killProcessingThread.start();
				});

				killFuture.thenRun(() -> kill.WritingProcess("sudo " + unloadCommand + "\n"));

				killFuture.join();
				if (killProcessingThread != null) {
					killProcessingThread.join();
				}

			}
		} catch (Exception e) {
			System.err.println("An error occurred while loading the driver: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	

	private void stopLoadDriverLaunchingThread() {
		if (loadDriverLaunchingThread != null) {
			loadDriverLaunchingThread.interrupt();
		}
	}

}