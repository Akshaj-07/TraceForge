package reports;

import correlation.Incident;
import investigation.Investigation;
import logs.LogEvent;
import utils.DataTimeUtil;

public class ReportGenerator {
    public String generate(Investigation item) {
        StringBuilder text = new StringBuilder();
        text.append("TRACEFORGE - INVESTIGATION ").append(item).append('\n');
        text.append(new InvestigationSummary().describe(item)).append("\n\nINCIDENTS\n");
        if (item.incidents.isEmpty()) {
            text.append("No rule matched.\n");
        }
        for (Incident incident : item.incidents) {
            text.append(String.format("#%d  %s  %s @ %s  %s to %s%n", incident.number,
                    incident.getPriority(), incident.user, incident.host,
                    DataTimeUtil.format(incident.start), DataTimeUtil.format(incident.getEnd())));
        }
        text.append("\nEVENT TIMELINE\n");
        for (LogEvent event : item.events) {
            text.append(String.format("%s | %s @ %s | %s | %s | %s:%d",
                    DataTimeUtil.format(event.time), event.user, event.host, event.action,
                    event.detail, event.source, event.lineNumber));
            if (event.getIncidentNumber() > 0) {
                text.append(" | Incident #").append(event.getIncidentNumber())
                        .append(" - ").append(event.getFinding());
            }
            text.append('\n');
        }
        if (!item.warnings.isEmpty()) {
            text.append("\nSKIPPED ROWS\n");
            for (String warning : item.warnings) {
                text.append(warning).append('\n');
            }
        }
        return text.toString();
    }
}
