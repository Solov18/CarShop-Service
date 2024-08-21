package org.example.logi;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.example.repository.AuditLogRepository;

import java.time.LocalDateTime;


/**
 * Аспект для аудита, который перехватывает вызовы методов контроллеров
 * и записывает информацию об их выполнении в журнал аудита.
 */
@Aspect
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Pointcut("execution(* com.example.controller.*.*(..))")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterControllerCall(JoinPoint joinPoint, Object result) {
        String actionType = joinPoint.getSignature().toShortString();
        String username = getCurrentUsername();
        String details = "Request completed successfully";

        AuditLog log = new AuditLog();
        log.setTimestamp(LocalDateTime.now());
        log.setActionType(actionType);
        log.setUsername(username);
        log.setDetails(details);

        auditLogRepository.save(log);
    }

    private String getCurrentUsername() {
        HttpServletRequest request = RequestContext.getRequest();
        if (request != null) {
            Object user = request.getSession().getAttribute("currentUser");
            return user != null ? user.toString() : "unknown";
        }
        return "unknown";
    }
}