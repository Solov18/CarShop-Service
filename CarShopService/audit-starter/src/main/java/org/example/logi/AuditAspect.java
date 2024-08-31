package org.example.logi;




import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditLogService auditLogService;

    @Autowired
    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Pointcut("execution(* org.example.controller..*(..))")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterControllerCall(JoinPoint joinPoint, Object result) {
        String actionType = joinPoint.getSignature().toShortString();
        String username = getCurrentUsername();
        String details = "Request completed successfully";

        // Используем сервис для записи лога
        auditLogService.logAction(actionType, username, details);

        logger.info("Audit log saved: actionType={}, username={}, details={}", actionType, username, details);
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
