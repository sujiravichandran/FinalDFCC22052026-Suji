package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import com.teclever.dfcc.datastore.dto.DbDriverCard;
import com.teclever.dfcc.datastore.dto.DriverCard;
import com.teclever.dfcc.datastore.dto.DriverCardDetailsResponse;
import com.teclever.dfcc.datastore.terminalmanagement.DriverManagement;
import com.teclever.utils.ProcessControl;

public class LoadDriverProcessControlManagement {

	public enum LoadMode {
		STARTUP, SWITCH
	}

	private BlockingQueue<String> loadDriverBQueue = new ArrayBlockingQueue<>(10000);
	private Thread loadDriverLaunchingThread;
	private Thread outputProcessingThread;
	boolean flag = true;
	private String currentLoadedDriver = null;

	private ProcessControl loadDriverProcessController;

	CompletableFuture<Void> launcherFuture = new CompletableFuture<>();

	public LoadDriverProcessControlManagement() {
		loadDriverProcessController = new ProcessControl(loadDriverBQueue);
	}

	public DriverCardDetailsResponse loadDriver(String command, String unloadCommand, int aitessId, LoadMode mode) {
		LoadDriverProcessControlManagement pcm = new LoadDriverProcessControlManagement();

		// DB obj
		DriverManagement dm = new DriverManagement();
		List<DbDriverCard> dbDriverCards = dm.getDriverCardDetailsBasedOnAitess(aitessId);

		// Response obj
		List<DriverCard> responseDriverCards = new ArrayList<DriverCard>();

		Map<String, String> dbMap = new HashMap<>();

		for (DbDriverCard d : dbDriverCards) {
			dbMap.put(d.getCardName(), d.getTotalNumberOfCards());
			System.out.println(" DB CardName :: " + d.getCardName());
			System.out.println(" DB Count :: " + d.getTotalNumberOfCards());
			System.out.println("CARD IDENTIFICATION TEXT" + d.getCardIdentificationText());
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
								System.out.println("loadDriver:: " +output);
								for (DbDriverCard d : dbDriverCards) {
									String cardIdentificationText = d.getCardIdentificationText();
									DriverCard parsedCards = dm.parseLine1(output, cardIdentificationText);

									if (parsedCards.getResponse().getResponseCode() == 1) {

										if (dbMap.get(parsedCards.getCardName()) != null) {
											System.out.println("DB CARD COUNT : " + dbMap.get(parsedCards.getCardName()) + " PARSED CARD COUNT  " + parsedCards.getFoundedNumberOfCards());
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
								if (output.contains("Starting AETS RT Scheduler") || output.contains("Staring AETS RT Scheduler") ) {
									System.out.println("LAST LINE :  "+ output.contains("Starting AETS RT Scheduler"));
									flag = false;
								}
//	                                System.out.println("after    IF CONTIDION ");
							}
//	                            System.out.println("WHILE LOOP COMPLETED ---- ");
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							Thread.currentThread().interrupt();
						}
						outputProcessingThread.interrupt();
					});
//	                    System.out.println("----------STOP--------");
					outputProcessingThread.start();

				});

				// Wait for launcherFuture to complete and ensure the processing thread has
				// finished
				launcherFuture.join();
//	                System.out.println("LAUNCHER JOINED");
				if (outputProcessingThread != null) {
					outputProcessingThread.join();
				}
//	                outputProcessingThread.wait();
				DriverCardDetailsResponse response = new DriverCardDetailsResponse();

//				while (true) {
//					if (!flag)
//						break;
//				}

	                while(flag) {
	                	System.out.print("- ");
	                }
//	                	System.out.println("-------- COMPLETED -----------");
//	                while(responseDriverCards.size()!=4) {
////	                	System.out.println("Waiting to complete Thread  ");
//	                }
				response.setDriverCardDetails(responseDriverCards);
				pcm.setCurrentLoadedDriver(command);
				System.out.println(
						"----response.getDriverCardDetails().size()----" + response.getDriverCardDetails().size());
				return response;

			case SWITCH:
				if (!pcm.isCurrentDriver(command)) {
					launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess("\u0003" + "\n"));

					if (unloadCommand != null && !unloadCommand.isEmpty()) {
						loadDriverProcessController.WritingProcess(unloadCommand);
					}

					launcherFuture.thenRun(() -> loadDriverProcessController.WritingProcess(command));
					pcm.setCurrentLoadedDriver(command);
				}
				break;
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

	private boolean isCurrentDriver(String driverCommand) {
		return driverCommand.equals(currentLoadedDriver);
	}

	private void setCurrentLoadedDriver(String driverCommand) {
		this.currentLoadedDriver = driverCommand;
	}

}