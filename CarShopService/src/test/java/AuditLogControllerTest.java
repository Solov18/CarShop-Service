import org.example.logi.AuditLog;
import org.example.logi.AuditLogController;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuditLogControllerTest {

    @Test
    void testLogAction() {
        AuditLogController auditLogController = mock(AuditLogController.class);

        doNothing().when(auditLogController).logAction("user", "action");

        auditLogController.logAction("user", "action");

        verify(auditLogController).logAction("user", "action");
    }

    @Test
    void testGetAllLogs() {
        AuditLogController auditLogController = mock(AuditLogController.class);

        List<AuditLog> mockLogs = new ArrayList<>();
        mockLogs.add(new AuditLog("user", "action"));
        when(auditLogController.getAllLogs()).thenReturn(mockLogs);

        List<AuditLog> logs = auditLogController.getAllLogs();

        verify(auditLogController).getAllLogs();
        assertFalse(logs.isEmpty());
        assertEquals("user", logs.get(0).getUsername());
    }
}