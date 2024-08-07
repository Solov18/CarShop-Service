package org.example.logi;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime timestamp;
    private String username;
    private String action;

    public AuditLog(String username, String action) {
        this.timestamp = LocalDateTime.now();
        this.username = username;
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "timestamp=" + timestamp +
                ", username='" + username + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}