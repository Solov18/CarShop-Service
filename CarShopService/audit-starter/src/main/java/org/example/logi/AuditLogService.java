package org.example.logi;

public interface AuditLogService {
    void logAction(String actionType, String username, String details);
}