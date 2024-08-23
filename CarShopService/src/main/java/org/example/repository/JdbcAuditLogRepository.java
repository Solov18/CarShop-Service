package org.example.repository;

import org.example.logi.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация репозитория для работы с журналом аудита с использованием JDBC.
 */
public class JdbcAuditLogRepository implements AuditLogRepository {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5433/mydatabase";
    private static final String JDBC_USER = "user";
    private static final String JDBC_PASSWORD = "password";

    private static final String INSERT_AUDIT_LOG_SQL = "INSERT INTO audit_log (timestamp, action_type, username, details) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_AUDIT_LOGS_SQL = "SELECT * FROM audit_log ORDER BY timestamp DESC";

    /**
     * Сохраняет запись журнала аудита в базе данных.
     *
     * @param auditLog Запись журнала аудита для сохранения.
     */
    @Override
    public void save(AuditLog auditLog) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT_AUDIT_LOG_SQL)) {

            statement.setObject(1, auditLog.getTimestamp());
            statement.setString(2, auditLog.getActionType());
            statement.setString(3, auditLog.getUsername());
            statement.setString(4, auditLog.getDetails());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает все записи журнала аудита из базы данных, отсортированные по времени в порядке убывания.
     *
     * @return Список записей журнала аудита.
     */
    public List<AuditLog> getAllLogs() {
        List<AuditLog> logs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_AUDIT_LOGS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                AuditLog log = new AuditLog();
                log.setId(resultSet.getLong("id"));
                log.setTimestamp(resultSet.getObject("timestamp", LocalDateTime.class));
                log.setActionType(resultSet.getString("action_type"));
                log.setUsername(resultSet.getString("username"));
                log.setDetails(resultSet.getString("details"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
