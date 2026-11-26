package logs;

import exceptions.InvalidLogException;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import utils.DataTimeUtil;

public class FileLogParse implements LogParse {
    public LogEvent parse(String row, String source, int lineNumber) throws InvalidLogException {
        // Split only four times so the detail can include a pipe.
        String[] fields = row.split("\\|", 5);
        LogValidator.check(fields);
        try {
            return new LogEvent(DataTimeUtil.parse(fields[0].trim()), fields[1].trim(),
                    fields[2].trim(), fields[3].trim().toUpperCase(Locale.ROOT),
                    fields[4].trim(), source, lineNumber);
        } catch (DateTimeParseException e) {
            throw new InvalidLogException("Expected a valid date and time, yyyy-MM-dd HH:mm:ss");
        }
    }
}
