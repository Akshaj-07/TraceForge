package exceptions;

// One bad row can be skipped while the rest of its file is read.
public class InvalidLogException extends Exception {
    public InvalidLogException(String message) {
        super(message);
    }
}
