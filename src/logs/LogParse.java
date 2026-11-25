package logs;

import exceptions.InvalidLogException;

public interface LogParse {
    LogEvent parse(String row, String source, int lineNumber) throws InvalidLogException;
}
