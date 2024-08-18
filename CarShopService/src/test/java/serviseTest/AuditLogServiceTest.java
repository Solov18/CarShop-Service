package serviseTest;

import org.example.logi.AuditLog;
import org.example.logi.AuditLogService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuditLogServiceTest {

    @Test
    void testLogAction() {
        AuditLogService auditLogService = mock(AuditLogService.class);

        doNothing().when(auditLogService).logAction("user", "action");

        auditLogService.logAction("user", "action");

        verify(auditLogService).logAction("user", "action");
    }

    @Test
    void testGetAllLogs() {
        AuditLogService auditLogController = mock(AuditLogService.class);

        List<AuditLog> mockLogs = new ArrayList<>();
        mockLogs.add(new AuditLog("user", "action"));
        when(auditLogController.getAllLogs()).thenReturn(mockLogs);

        List<AuditLog> logs = auditLogController.getAllLogs();

        verify(auditLogController).getAllLogs();
        assertFalse(logs.isEmpty());
        assertEquals("user", logs.get(0).getUsername());
    }
}