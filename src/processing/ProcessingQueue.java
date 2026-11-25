package processing;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

// Workers wait on this queue until a file arrives or the producer finishes.
public class ProcessingQueue {
    private final Queue<File> files = new ArrayDeque<>();
    private boolean finished;

    public synchronized void add(File file) {
        files.add(file);
        notifyAll();
    }

    public synchronized File take() throws InterruptedException {
        while (files.isEmpty() && !finished) {
            wait();
        }
        return files.poll(); // null means all files have been taken.
    }

    public synchronized void finish() {
        finished = true;
        notifyAll();
    }
}
