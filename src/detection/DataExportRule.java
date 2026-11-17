package detection;

import java.util.List;
import logs.LogEvent;

public class DataExportRule extends DetectionRule {
    public void apply(List<LogEvent> events) {
        flagAction(events, "DATA_EXPORT", "Data export requires review");
    }
}
