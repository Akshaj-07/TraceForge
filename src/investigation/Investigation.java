package investigation;

import java.util.*;
import logs.LogEvent;
import correlation.Incident;

public class Investigation {
    public int id;
    public final String name;
    public InvestigationStatus status = InvestigationStatus.OPEN;
    public final List<LogEvent> events = new ArrayList<>();
    public final List<Incident> incidents = new ArrayList<>();
    public final List<String> warnings = new ArrayList<>();

    public Investigation(String name) {
        this.name = name;
    }

    public String toString() {
        return "#" + id + "  " + name + "  [" + status + "]";
    }
}
