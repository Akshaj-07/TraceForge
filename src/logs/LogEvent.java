package logs;

import java.time.LocalDateTime;

// One accepted row. Source and line number identify the original evidence.
public class LogEvent {
    public final LocalDateTime time;
    public final String user;
    public final String host;
    public final String action;
    public final String detail;
    public final String source;
    public final int lineNumber;

    private String finding = "";
    private int incidentNumber;

    public LogEvent(LocalDateTime time, String user, String host, String action,
                    String detail, String source, int lineNumber) {
        this.time = time;
        this.user = user;
        this.host = host;
        this.action = action;
        this.detail = detail;
        this.source = source;
        this.lineNumber = lineNumber;
    }

    public void flag(String reason) {
        finding = reason;
    }

    public String getFinding() {
        return finding;
    }

    public void linkTo(int number) {
        incidentNumber = number;
    }

    public int getIncidentNumber() {
        return incidentNumber;
    }
}
