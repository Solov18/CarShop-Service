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

    private final String jdbcUrl = "jdbc:postgresql://localhost:5433/mydatabase";
    private final String jdbcUser = "user";
    private final String jdbcPassword = "password";

    /**
     * Сохраняет запись журнала аудита в базе данных.
     *
     * @param auditLog Запись журнала аудита для сохранения.
     */
    @Override
    public void save(AuditLog auditLog) {
        String sql = "INSERT INTO audit_log (timestamp, action_type, username, details) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {

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
        String query = "SELECT * FROM audit_log ORDER BY timestamp DESC";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
             PreparedStatement statement = connection.prepareStatement(query);
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