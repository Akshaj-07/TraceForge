package database;

import java.sql.*;
import java.util.*;
import investigation.*;

public class InvestigationDAO {
    private final LogEventDAO events = new LogEventDAO();
    private final IncidentDAO incidents = new IncidentDAO();

    public void save(Investigation item) throws SQLException {
        String sql = "INSERT INTO investigations (name, status, warnings) VALUES (?, ?, ?)";
        try (Connection db = DatabaseConnection.open()) {
            // Save the case and all its evidence together.
            db.setAutoCommit(false);
            try (PreparedStatement insert = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, item.name);
                insert.setString(2, item.status.name());
                insert.setString(3, String.join("\n", item.warnings));
                insert.executeUpdate();
                try (ResultSet keys = insert.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Missing investigation ID");
                    }
                    item.id = keys.getInt(1);
                }
                incidents.save(db, item);
                events.save(db, item);
                db.commit();
            } catch (SQLException e) {
                db.rollback();
                throw e;
            }
        }
    }

    private Investigation readCase(ResultSet rows) throws SQLException {
        Investigation item = new Investigation(rows.getString("name"));
        item.id = rows.getInt("id");
        item.status = InvestigationStatus.valueOf(rows.getString("status"));
        return item;
    }

    public List<Investigation> list() throws SQLException {
        List<Investigation> cases = new ArrayList<>();
        String sql = "SELECT id, name, status FROM investigations ORDER BY id DESC";
        try (Connection db = DatabaseConnection.open();
             Statement query = db.createStatement();
             ResultSet rows = query.executeQuery(sql)) {
            while (rows.next()) {
                cases.add(readCase(rows));
            }
        }
        return cases;
    }

    public Investigation find(int id) throws SQLException {
        try (Connection db = DatabaseConnection.open();
             PreparedStatement query = db.prepareStatement("SELECT * FROM investigations WHERE id = ?")) {
            query.setInt(1, id);
            Investigation item;
            try (ResultSet rows = query.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }
                item = readCase(rows);
                String warnings = rows.getString("warnings");
                if (!warnings.isEmpty()) {
                    item.warnings.addAll(Arrays.asList(warnings.split("\n")));
                }
            }
            incidents.load(db, item);
            events.load(db, item);
            return item;
        }
    }

    public boolean setStatus(int id, InvestigationStatus status) throws SQLException {
        try (Connection db = DatabaseConnection.open();
             PreparedStatement update = db.prepareStatement("UPDATE investigations SET status = ? WHERE id = ?")) {
            update.setString(1, status.name());
            update.setInt(2, id);
            return update.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection db = DatabaseConnection.open();
             PreparedStatement delete = db.prepareStatement("DELETE FROM investigations WHERE id = ?")) {
            delete.setInt(1, id);
            return delete.executeUpdate() == 1;
        }
    }

    public List<String> history(String user) throws SQLException {
        String sql = "SELECT i.id, i.name, COUNT(*) AS total_events, "
                + "COUNT(DISTINCT e.incident_no) AS total_incidents "
                + "FROM investigations i JOIN log_events e ON i.id = e.case_id "
                + "WHERE e.user_name = ? GROUP BY i.id, i.name ORDER BY i.id DESC";
        List<String> result = new ArrayList<>();
        try (Connection db = DatabaseConnection.open(); PreparedStatement query = db.prepareStatement(sql)) {
            query.setString(1, user);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    result.add("#" + rows.getInt("id") + "  " + rows.getString("name")
                            + " | events: " + rows.getInt("total_events")
                            + " | incidents: " + rows.getInt("total_incidents"));
                }
            }
        }
        return result;
    }
}
