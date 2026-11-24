package cli;

import java.io.EOFException;
import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);

    public String line(String prompt) throws EOFException {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new EOFException();
        }
        return scanner.nextLine().trim();
    }

    public int number(String prompt, int min, int max) throws EOFException {
        while (true) {
            try {
                int value = Integer.parseInt(line(prompt));
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // The next message also covers non-numeric input.
            }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
    }
}
