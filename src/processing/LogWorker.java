package processing;

import java.io.*;
import java.util.*;
import logs.*;

public class LogWorker implements Runnable {
    private final ProcessingQueue queue;
    // Only this worker writes to these lists. The controller reads them after join().
    final List<LogEvent> events = new ArrayList<>();
    final List<String> warnings = new ArrayList<>();
    String error = "";

    public LogWorker(ProcessingQueue queue) {
        this.queue = queue;
    }

    public void run() {
        LogImporter importer = new LogImporter();
        try {
            File file;
            while ((file = queue.take()) != null) {
                importer.read(file, events, warnings);
            }
        } catch (IOException e) {
            error = "Cannot read log: " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            error = "Log import interrupted";
        }
    }
}
