package database;

import java.sql.*;
import correlation.*;
import investigation.Investigation;
import utils.DataTimeUtil;

public class IncidentDAO {
    public void save(Connection db, Investigation item) throws SQLException {
        String sql = "INSERT INTO incidents (case_id, incident_no, user_name, host_name, "
                + "start_time, end_time, priority) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insert = db.prepareStatement(sql)) {
            for (Incident incident : item.incidents) {
                insert.setInt(1, item.id);
                insert.setInt(2, incident.number);
                insert.setString(3, incident.user);
                insert.setString(4, incident.host);
                insert.setString(5, DataTimeUtil.format(incident.start));
                insert.setString(6, DataTimeUtil.format(incident.getEnd()));
                insert.setString(7, incident.getPriority().name());
                insert.executeUpdate();
            }
        }
    }

    public void load(Connection db, Investigation item) throws SQLException {
        String sql = "SELECT * FROM incidents WHERE case_id = ? ORDER BY incident_no";
        try (PreparedStatement query = db.prepareStatement(sql)) {
            query.setInt(1, item.id);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    item.incidents.add(new Incident(rows.getInt("incident_no"), rows.getString("user_name"),
                            rows.getString("host_name"), DataTimeUtil.parse(rows.getString("start_time")),
                            DataTimeUtil.parse(rows.getString("end_time")),
                            IncidentPriority.valueOf(rows.getString("priority"))));
                }
            }
        }
    }
}
