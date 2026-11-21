package investigation;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import correlation.*;
import database.InvestigationDAO;
import exceptions.InvestigationException;
import processing.ProcessingController;
import utils.ValidationUtil;

public class InvestigationManager {
    private final InvestigationDAO store;

    public InvestigationManager(InvestigationDAO store) {
        this.store = store;
    }

    public Investigation create(String name, List<File> files)
            throws IOException, InvestigationException, InterruptedException, SQLException {
        if (ValidationUtil.isBlank(name) || files.isEmpty()) {
            throw new InvestigationException("Enter a case name and at least one log file.");
        }
        Investigation item = new Investigation(name.trim());
        ProcessingController controller = new ProcessingController();
        item.events.addAll(controller.read(readableFiles(files), item.warnings));
        if (item.events.isEmpty()) {
            String message = "No valid events. Investigation was not saved.";
            if (!item.warnings.isEmpty()) {
                message += System.lineSeparator() + String.join(System.lineSeparator(), item.warnings);
            }
            throw new InvestigationException(message);
        }
        new TimeLineBuilder().sort(item.events);
        item.incidents.addAll(new EventCorrelator().findIncidents(item.events));
        store.save(item);
        return item;
    }

    private List<File> readableFiles(List<File> files) throws IOException, InvestigationException {
        // A path entered twice must not count as two pieces of evidence.
        List<File> readable = new ArrayList<>();
        Set<File> seen = new HashSet<>();
        for (File file : files) {
            if (!file.isFile() || !file.canRead()) {
                throw new InvestigationException("Cannot read file: " + file);
            }
            if (seen.add(file.getCanonicalFile())) {
                readable.add(file);
            }
        }
        return readable;
    }
}
