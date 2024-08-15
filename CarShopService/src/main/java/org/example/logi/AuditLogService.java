package org.example.logi;

import java.util.ArrayList;
import java.util.List;

public class AuditLogService {
    private List<AuditLog> logs = new ArrayList<>();

    public void logAction(String username, String action) {
        logs.add(new AuditLog(username, action));
    }

    public List<AuditLog> getAllLogs() {
        return logs;
    }

}