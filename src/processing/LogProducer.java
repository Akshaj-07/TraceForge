package processing;

import java.io.File;
import java.util.List;

public class LogProducer implements Runnable {
    private final ProcessingQueue queue;
    private final List<File> files;

    public LogProducer(ProcessingQueue queue, List<File> files) {
        this.queue = queue;
        this.files = files;
    }

    public void run() {
        try {
            for (File file : files) {
                queue.add(file);
            }
        } finally {
            queue.finish();
        }
    }
}
