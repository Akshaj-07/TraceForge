package correlation;

import java.util.Comparator;
import java.util.List;
import logs.LogEvent;

public class TimeLineBuilder {
    public void sort(List<LogEvent> events) {
        // The source path and row give tied timestamps a repeatable order.
        Comparator<LogEvent> order = Comparator.comparing((LogEvent e) -> e.time);
        events.sort(order.thenComparing(e -> e.source).thenComparingInt(e -> e.lineNumber));
    }
}
