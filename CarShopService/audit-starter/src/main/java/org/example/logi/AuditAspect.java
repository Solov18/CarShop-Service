package org.example.logi;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Pointcut("execution(* org.example.controller..*(..))")
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

        logger.info("Saving audit log: {}", log);

        auditLogRepository.save(log);
    }

    private String getCurrentUsername() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object user = request.getSession().getAttribute("currentUser");
            return user != null ? user.toString() : "unknown";
        }
        return "unknown";
    }
}