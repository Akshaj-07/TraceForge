package detection;

import java.util.List;
import logs.LogEvent;

public class FileAccessRule extends DetectionRule {
    public void apply(List<LogEvent> events) {
        flagAction(events, "FILE_ACCESS_DENIED", "File access was denied");
    }
}
