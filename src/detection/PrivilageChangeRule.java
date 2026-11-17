package detection;

import java.util.List;
import logs.LogEvent;

public class PrivilageChangeRule extends DetectionRule {
    public void apply(List<LogEvent> events) {
        flagAction(events, "PRIVILEGE_GRANTED", "Account received a new privilege");
    }
}
