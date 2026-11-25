package logs;

import exceptions.InvalidLogException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LogImporter {
    private final LogParse parser = new FileLogParse();

    public void read(File file, List<LogEvent> events, List<String> warnings) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String row;
            int lineNumber = 0;
            while ((row = reader.readLine()) != null) {
                lineNumber++;
                if (row.trim().isEmpty() || row.trim().startsWith("#")) {
                    continue;
                }
                try {
                    events.add(parser.parse(row, file.getPath(), lineNumber));
                } catch (InvalidLogException e) {
                    warnings.add(file.getPath() + ":" + lineNumber + " - " + e.getMessage());
                }
            }
        }
    }

    public static File[] createSamples() throws IOException {
        File folder = new File("data/sample_logs");
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw new IOException("Cannot create " + folder);
        }
        File auth = new File(folder, "auth.log");
        File activity = new File(folder, "activity.log");
        writeSample(auth, new String[] {
            "2026-01-15 09:04:00|asha|lab-01|LOGIN_FAILED|Invalid password",
            "2026-01-15 09:00:00|asha|lab-01|LOGIN_FAILED|Invalid password",
            "2026-01-15 09:03:00|ravi|lab-02|LOGIN_SUCCESS|Signed in",
            "2026-01-15 09:06:00|asha|lab-01|LOGIN_SUCCESS|Signed in"
        });
        writeSample(activity, new String[] {
            "2026-01-15 09:02:00|asha|lab-01|LOGIN_FAILED|Invalid password",
            "2026-01-15 09:07:00|asha|lab-01|PRIVILEGE_GRANTED|Granted editor access",
            "2026-01-15 09:09:00|asha|lab-01|DATA_EXPORT|Exported a project list",
            "2026-01-15 09:08:00|ravi|lab-02|FILE_ACCESS_DENIED|Access denied on marks.txt",
            "2026-01-15 10:00:00|asha|lab-01|ACCOUNT_CREATED|Created a trainee account",
            "bad row for the validation demonstration"
        });
        return new File[] {auth, activity};
    }

    private static void writeSample(File file, String[] lines) throws IOException {
        if (file.exists()) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
