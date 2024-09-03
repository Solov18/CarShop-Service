package org.example.logi;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AuditLog {
    private Long id;
    private LocalDateTime timestamp;
    private String actionType;
    private String username;
    private String details;
}