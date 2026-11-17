package detection;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import logs.LogEvent;

public class LoginFailureRule extends DetectionRule {
    public void apply(List<LogEvent> events) {
        Deque<LogEvent> recent = new ArrayDeque<>();
        for (LogEvent event : events) {
            if (!event.action.equals("LOGIN_FAILED")) {
                continue;
            }
            while (!recent.isEmpty() && recent.peekFirst().time.isBefore(event.time.minusMinutes(5))) {
                recent.removeFirst();
            }
            recent.addLast(event);
            if (recent.size() >= 3) {
                for (LogEvent failure : recent) {
                    failure.flag("Three or more failed logins within five minutes");
                }
                // The latest two are enough to test the next failure.
                recent.removeFirst();
            }
        }
    }
}
