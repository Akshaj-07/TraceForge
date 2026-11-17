package detection;

import java.util.List;
import logs.LogEvent;

// Each rule receives one user's events on one host, in time order.
public abstract class DetectionRule {
    public abstract void apply(List<LogEvent> events);

    protected void flagAction(List<LogEvent> events, String action, String reason) {
        for (LogEvent event : events) {
            if (event.action.equals(action)) {
                event.flag(reason);
            }
        }
    }
}
