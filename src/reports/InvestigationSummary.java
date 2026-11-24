package reports;

import correlation.IncidentPriority;
import investigation.Investigation;

public class InvestigationSummary {
    public String describe(Investigation item) {
        long flagged = item.events.stream().filter(e -> !e.getFinding().isEmpty()).count();
        long high = item.incidents.stream().filter(i -> i.getPriority() == IncidentPriority.HIGH).count();
        return "Valid events: " + item.events.size() + " | Flagged events: " + flagged
                + " | Incidents: " + item.incidents.size() + " | High priority: " + high
                + " | Skipped rows: " + item.warnings.size();
    }
}
