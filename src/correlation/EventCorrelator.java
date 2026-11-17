package correlation;

import java.util.*;
import detection.*;
import logs.LogEvent;

public class EventCorrelator {
    private final List<DetectionRule> rules = Arrays.asList(new LoginFailureRule(),
            new AccountActivityRule(), new FileAccessRule(), new PrivilageChangeRule(),
            new DataExportRule());

    public List<Incident> findIncidents(List<LogEvent> timeline) {
        flagEvents(timeline);
        return linkFlaggedEvents(timeline);
    }

    private void flagEvents(List<LogEvent> timeline) {
        Map<String, List<LogEvent>> groups = new HashMap<>();
        for (LogEvent event : timeline) {
            String key = event.user + "|" + event.host;
            if (!groups.containsKey(key)) {
                groups.put(key, new ArrayList<>());
            }
            groups.get(key).add(event);
        }
        for (List<LogEvent> group : groups.values()) {
            for (DetectionRule rule : rules) {
                rule.apply(group);
            }
        }
    }

    private List<Incident> linkFlaggedEvents(List<LogEvent> timeline) {
        List<Incident> incidents = new ArrayList<>();
        Map<String, Incident> latest = new HashMap<>();
        for (LogEvent event : timeline) {
            if (event.getFinding().isEmpty()) {
                continue;
            }
            String key = event.user + "|" + event.host;
            Incident incident = latest.get(key);
            if (incident == null || event.time.isAfter(incident.getEnd().plusMinutes(10))) {
                incident = new Incident(incidents.size() + 1, event.user, event.host,
                        event.time, event.time, IncidentPriority.LOW);
                latest.put(key, incident);
                incidents.add(incident);
            }
            incident.add(event);
        }
        return incidents;
    }
}
