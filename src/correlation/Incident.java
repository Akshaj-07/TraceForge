package correlation;

import java.time.LocalDateTime;
import logs.LogEvent;

// One numbered group of related findings in an investigation.
public class Incident {
    public final int number;
    public final String user;
    public final String host;
    public final LocalDateTime start;
    private LocalDateTime end;
    private IncidentPriority priority;
    private String firstAction;

    public Incident(int number, String user, String host, LocalDateTime start,
                    LocalDateTime end, IncidentPriority priority) {
        this.number = number;
        this.user = user;
        this.host = host;
        this.start = start;
        this.end = end;
        this.priority = priority;
    }

    public void add(LogEvent event) {
        if (firstAction == null) {
            firstAction = event.action;
        }
        if (event.action.equals("PRIVILEGE_GRANTED") || !firstAction.equals(event.action)) {
            priority = IncidentPriority.HIGH;
        } else if (priority == IncidentPriority.LOW && !event.action.equals("ACCOUNT_CREATED")) {
            priority = IncidentPriority.MEDIUM;
        }
        end = event.time;
        event.linkTo(number);
    }

    public IncidentPriority getPriority() {
        return priority;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
