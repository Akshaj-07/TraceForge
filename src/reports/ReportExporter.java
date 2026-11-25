package reports;

import java.io.*;
import java.nio.charset.StandardCharsets;
import investigation.Investigation;

public class ReportExporter {
    public File export(Investigation item) throws IOException {
        File folder = new File("data/exported_reports");
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw new IOException("Cannot create report directory " + folder);
        }
        String base = "case-" + item.id;
        File output = new File(folder, base + ".txt");
        int version = 2;
        while (!output.createNewFile()) {
            output = new File(folder, base + "-" + version + ".txt");
            version++;
        }
        try (FileWriter writer = new FileWriter(output, StandardCharsets.UTF_8)) {
            writer.write(new ReportGenerator().generate(item));
        } catch (IOException e) {
            output.delete();
            throw e;
        }
        return output;
    }
}
