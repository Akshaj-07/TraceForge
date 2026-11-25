package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

// Every input file uses the same local clock and a 24-hour timestamp.
public class DataTimeUtil {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    public static LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, FORMAT);
    }

    public static String format(LocalDateTime time) {
        return time.format(FORMAT);
    }
}
