package database;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {
    public static Connection open() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:./database/traceforge", "sa", "");
    }

    public static void initialize() throws SQLException, IOException {
        try (Connection db = open();
             Statement statement = db.createStatement();
             Scanner sql = new Scanner(new File("database/schema.sql"), "UTF-8")) {
            sql.useDelimiter(";");
            while (sql.hasNext()) {
                String command = sql.next().trim();
                if (!command.isEmpty()) {
                    statement.executeUpdate(command);
                }
            }
            if (sql.ioException() != null) {
                throw sql.ioException();
            }
        }
    }
}
