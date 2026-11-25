package logs;

import exceptions.InvalidLogException;
import utils.ValidationUtil;

public class LogValidator {
    public static void check(String[] fields) throws InvalidLogException {
        if (fields.length != 5) {
            throw new InvalidLogException("Expected timestamp|user|host|action|detail");
        }
        for (int i = 0; i < fields.length; i++) {
            if (ValidationUtil.isBlank(fields[i])) {
                throw new InvalidLogException("Field " + (i + 1) + " is empty");
            }
        }
    }
}
