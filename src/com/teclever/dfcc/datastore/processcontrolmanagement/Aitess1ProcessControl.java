package com.teclever.dfcc.datastore.processcontrolmanagement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import com.teclever.utils.ProcessControl;
public class Aitess1ProcessControl {
    private static Aitess1ProcessControl instance;
    private ProcessControl aitess1ProcessControl;
    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
    private Thread outputProcessingThread;
    private boolean isTerminalLaunched = false;
    private boolean isAitessLoaded = false;

    private Aitess1ProcessControl() {
        //aitess1ProcessControl = new ProcessControl(queue);
    }
    public static synchronized Aitess1ProcessControl getInstance() {
        if (instance == null) {
            instance = new Aitess1ProcessControl();
        }
        return instance;
    }

    public void terminateAitess(String command) {
        aitess1ProcessControl.WritingProcess(command + "\n");
        aitess1ProcessControl.stopProcesses();
        if (outputProcessingThread != null) {
            outputProcessingThread.interrupt();
        }
        isAitessLoaded = false;
    }

    public String getAitessTerminalOutputFromQueue() throws InterruptedException
    {
    	return queue.take();
    }

    
    
}
