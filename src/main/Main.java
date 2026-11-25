package main;

import java.io.IOException;
import java.sql.SQLException;
import cli.MainMenu;
import database.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection.initialize();
            new MainMenu().run();
        } catch (IOException | SQLException e) {
            System.err.println("Cannot open TRACEFORGE: " + e.getMessage());
            System.err.println("Run from the TraceForge folder with lib/h2.jar on the classpath.");
            System.exit(1);
        }
    }
}
