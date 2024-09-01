package org.example.repository;

import org.example.logi.AuditLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class JdbcAuditLogRepository implements AuditLogRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_AUDIT_LOG_SQL = "INSERT INTO audit_log (timestamp, action_type, username, details) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_AUDIT_LOGS_SQL = "SELECT * FROM audit_log ORDER BY timestamp DESC";

    @Autowired
    public JdbcAuditLogRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(AuditLog auditLog) {
        jdbcTemplate.update(INSERT_AUDIT_LOG_SQL,
                auditLog.getTimestamp(),
                auditLog.getActionType(),
                auditLog.getUsername(),
                auditLog.getDetails());
    }

    public List<AuditLog> getAllLogs() {
        return jdbcTemplate.query(SELECT_ALL_AUDIT_LOGS_SQL, this::mapRowToAuditLog);
    }

    private AuditLog mapRowToAuditLog(ResultSet rs, int rowNum) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getLong("id"));
        log.setTimestamp(rs.getObject("timestamp", LocalDateTime.class));
        log.setActionType(rs.getString("action_type"));
        log.setUsername(rs.getString("username"));
        log.setDetails(rs.getString("details"));
        return log;
    }
}
