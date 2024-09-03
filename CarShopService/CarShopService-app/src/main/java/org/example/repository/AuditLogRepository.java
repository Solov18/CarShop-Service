package org.example.repository;

import org.example.logi.AuditLog;

public interface AuditLogRepository {
    void save(AuditLog auditLog);
}