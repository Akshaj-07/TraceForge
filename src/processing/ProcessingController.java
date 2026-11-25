package processing;

import java.io.File;
import java.util.*;
import exceptions.InvestigationException;
import logs.LogEvent;

public class ProcessingController {
    public List<LogEvent> read(List<File> files, List<String> warnings)
            throws InterruptedException, InvestigationException {
        ProcessingQueue queue = new ProcessingQueue();
        LogWorker first = new LogWorker(queue);
        LogWorker second = new LogWorker(queue);
        Thread workerA = new Thread(first);
        Thread workerB = new Thread(second);
        Thread producer = new Thread(new LogProducer(queue, files));
        workerA.start();
        workerB.start();
        producer.start();
        try {
            producer.join();
            workerA.join();
            workerB.join();
        } catch (InterruptedException e) {
            workerA.interrupt();
            workerB.interrupt();
            throw e;
        }

        List<LogEvent> events = new ArrayList<>();
        for (LogWorker worker : Arrays.asList(first, second)) {
            if (!worker.error.isEmpty()) {
                throw new InvestigationException(worker.error);
            }
            events.addAll(worker.events);
            warnings.addAll(worker.warnings);
        }
        Collections.sort(warnings);
        return events;
    }
}
