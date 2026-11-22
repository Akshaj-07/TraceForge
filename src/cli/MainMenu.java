package cli;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import database.InvestigationDAO;
import exceptions.InvestigationException;
import investigation.*;
import logs.LogImporter;
import reports.*;

public class MainMenu {
    private final InputHandler input = new InputHandler();
    private final InvestigationDAO store = new InvestigationDAO();
    private final InvestigationManager manager = new InvestigationManager(store);
    private final ReportExporter exporter = new ReportExporter();

    public void run() {
        while (true) {
            System.out.println("\nTRACEFORGE");
            System.out.println("1 Sample investigation     2 Import my log files");
            System.out.println("3 List investigations      4 View investigation");
            System.out.println("5 Export report            6 Change case status");
            System.out.println("7 Search history by user   8 Delete investigation");
            System.out.println("0 Exit");
            try {
                int choice = input.number("Choice: ", 0, 8);
                switch (choice) {
                    case 0: return;
                    case 1:
                        create("Sample investigation", Arrays.asList(LogImporter.createSamples()));
                        break;
                    case 2:
                        importFiles();
                        break;
                    case 3:
                        printRows(store.list());
                        break;
                    case 4:
                        viewOrExport(false);
                        break;
                    case 5:
                        viewOrExport(true);
                        break;
                    case 6:
                        changeStatus();
                        break;
                    case 7:
                        printRows(store.history(input.line("Exact user name: ")));
                        break;
                    case 8:
                        delete();
                        break;
                    default: break;
                }
            } catch (EOFException e) {
                return;
            } catch (SQLException | IOException | InvestigationException e) {
                System.out.println("Operation failed: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Investigation interrupted. Exiting.");
                return;
            }
        }
    }

    private int caseId() throws EOFException {
        return input.number("Investigation ID: ", 1, Integer.MAX_VALUE);
    }

    private void importFiles() throws IOException, SQLException, InvestigationException, InterruptedException {
        String name = input.line("Case name: ");
        List<File> files = new ArrayList<>();
        System.out.println("Example path: data/sample_logs/auth.log");
        System.out.println("Paths start in: " + new File(".").getCanonicalPath());
        while (true) {
            String path = input.line("Enter a file path, or 0 to finish: ");
            if (path.equals("0") || path.isEmpty()) {
                break;
            }
            File file = new File(path);
            if (!file.isFile() || !file.canRead()) {
                System.out.println("Cannot read that file. Check the path and try again.");
                continue;
            }
            file = file.getCanonicalFile();
            if (files.contains(file)) {
                System.out.println("Already added.");
            } else {
                files.add(file);
                System.out.println("Added " + file.getName() + " (" + files.size() + " selected)");
            }
        }
        if (files.isEmpty()) {
            System.out.println("No files selected. Returning to menu.");
            return;
        }
        create(name, files);
    }

    private void create(String name, List<File> files)
            throws IOException, SQLException, InvestigationException, InterruptedException {
        Investigation item = manager.create(name, files);
        System.out.println("Saved " + item);
        System.out.println(new InvestigationSummary().describe(item));
        try {
            System.out.println("Report: " + exporter.export(item));
        } catch (IOException e) {
            System.out.println("Report export failed: " + e.getMessage() + ". Retry with option 5.");
        }
    }

    private void viewOrExport(boolean export) throws IOException, SQLException {
        Investigation item = store.find(caseId());
        if (item == null) {
            System.out.println("Investigation not found.");
        } else if (export) {
            System.out.println("Report: " + exporter.export(item));
        } else {
            System.out.print(new ReportGenerator().generate(item));
        }
    }

    private void changeStatus() throws EOFException, SQLException {
        int id = caseId();
        int choice = input.number("1 Open, 2 Closed: ", 1, 2);
        InvestigationStatus status = (choice == 1) ? InvestigationStatus.OPEN : InvestigationStatus.CLOSED;
        System.out.println(store.setStatus(id, status) ? "Status saved." : "Investigation not found.");
    }

    private void delete() throws EOFException, SQLException {
        int id = caseId();
        if (input.line("Type DELETE to remove case #" + id + " and its stored evidence: ").equals("DELETE")) {
            System.out.println(store.delete(id) ? "Case deleted. Exported files are kept." : "Investigation not found.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void printRows(List<?> rows) {
        if (rows.isEmpty()) {
            System.out.println("No investigations found.");
        }
        for (Object row : rows) {
            System.out.println(row);
        }
    }
}
