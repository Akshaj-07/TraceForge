package database;

import java.sql.*;
import investigation.Investigation;
import logs.LogEvent;
import utils.DataTimeUtil;

// DAO means Data Access Object. This class stores and reads log rows.
public class LogEventDAO {
    public void save(Connection db, Investigation item) throws SQLException {
        String sql = "INSERT INTO log_events (case_id, position_no, event_time, user_name, host_name, "
                + "action_name, detail, source_file, source_line, finding, incident_no) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insert = db.prepareStatement(sql)) {
            int position = 0;
            for (LogEvent e : item.events) {
                insert.setInt(1, item.id);
                insert.setInt(2, ++position);
                insert.setString(3, DataTimeUtil.format(e.time));
                insert.setString(4, e.user);
                insert.setString(5, e.host);
                insert.setString(6, e.action);
                insert.setString(7, e.detail);
                insert.setString(8, e.source);
                insert.setInt(9, e.lineNumber);
                insert.setString(10, e.getFinding());
                if (e.getIncidentNumber() == 0) {
                    insert.setNull(11, Types.INTEGER);
                } else {
                    insert.setInt(11, e.getIncidentNumber());
                }
                insert.executeUpdate();
            }
        }
    }

    public void load(Connection db, Investigation item) throws SQLException {
        String sql = "SELECT * FROM log_events WHERE case_id = ? ORDER BY position_no";
        try (PreparedStatement query = db.prepareStatement(sql)) {
            query.setInt(1, item.id);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    LogEvent e = new LogEvent(DataTimeUtil.parse(rows.getString("event_time")),
                            rows.getString("user_name"), rows.getString("host_name"),
                            rows.getString("action_name"), rows.getString("detail"),
                            rows.getString("source_file"), rows.getInt("source_line"));
                    e.flag(rows.getString("finding"));
                    e.linkTo(rows.getInt("incident_no")); // JDBC returns 0 for SQL NULL here.
                    item.events.add(e);
                }
            }
        }
    }
}
