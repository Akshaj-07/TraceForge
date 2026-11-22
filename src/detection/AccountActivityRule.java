package detection;

import java.util.List;
import logs.LogEvent;

public class AccountActivityRule extends DetectionRule {
    public void apply(List<LogEvent> events) {
        flagAction(events, "ACCOUNT_CREATED", "Account creation requires review");
    }
}
