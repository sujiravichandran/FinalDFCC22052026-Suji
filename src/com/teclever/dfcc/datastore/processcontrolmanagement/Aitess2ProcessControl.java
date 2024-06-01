package com.teclever.dfcc.datastore.processcontrolmanagement;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

import com.teclever.utils.ProcessControl;

public class Aitess2ProcessControl {
    private static Aitess2ProcessControl instance;
    private ProcessControl aitess2ProcessControl;
    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
    private Thread outputProcessingThread;

    private Aitess2ProcessControl() {
        //aitess2ProcessControl = new ProcessControl(queue);
    }

    public static synchronized Aitess2ProcessControl getInstance() {
        if (instance == null) {
            instance = new Aitess2ProcessControl();
        }
        return instance;
    }

    public void launchAitess(String command) {
//        CompletableFuture<Void> launcherFuture = new CompletableFuture<>();
//        aitess2ProcessControl.LaunchingProcess(command, launcherFuture);
//        launcherFuture.thenRun(() -> {
//            aitess2ProcessControl.ReadingProcess();
//            outputProcessingThread = new Thread(() -> {
//                try {
//                    while (true) {
//                        String output = queue.take();
//                        System.out.println(output);
//                    }
//                } catch (InterruptedException e1) {
//                    // Thread interrupted, stopping gracefully
//                }
//            });
//            outputProcessingThread.start();
//        });
    }

    public void executeTestFiles(String command) {
        aitess2ProcessControl.WritingProcess(command + "\n");
    }

    public void terminateAitess(String command) {
        aitess2ProcessControl.WritingProcess(command + "\n");
        aitess2ProcessControl.stopProcesses();
        if (outputProcessingThread != null) {
            outputProcessingThread.interrupt();
        }
    }
}