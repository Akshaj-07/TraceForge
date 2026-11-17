package utils;

public class ValidationUtil {
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
