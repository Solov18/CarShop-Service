//package org.example.logi;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.example.repository.AuditLogRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.time.LocalDateTime;
//
///**
// * Класс, отвечающий за аспект аудита, который записывает информацию о выполнении методов контроллеров.
// *
// * Этот аспект использует Spring AOP для перехвата выполнения методов контроллеров и записи информации
// * о выполнении запросов в репозиторий аудита.
// *
// */
//@Aspect
//@Component
//public class AuditAspect {
//
//    // Добавляем логгер
//    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
//
//    private final AuditLogRepository auditLogRepository;
//
//    @Autowired
//    public AuditAspect(AuditLogRepository auditLogRepository) {
//        this.auditLogRepository = auditLogRepository;
//    }
//
//    /**
//     * Определяет точку среза для методов контроллеров.
//     *
//     * Этот метод используется для указания на все методы в пакетах контроллеров.
//     *
//     */
//    @Pointcut("execution(* org.example.controller..*(..))")
//    public void controllerMethods() {}
//
//
//    /**
//     * Логирует выполнение метода контроллера после его успешного завершения.
//     *
//     * Этот метод срабатывает после выполнения любого метода, определенного в точке среза {@link #controllerMethods()},
//     * и сохраняет запись о выполнении в репозитории аудита.
//     *
//     *
//     * @param joinPoint объект, предоставляющий информацию о выполнении метода.
//     * @param result результат выполнения метода.
//     */
//    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
//    public void logAfterControllerCall(JoinPoint joinPoint, Object result) {
//        String actionType = joinPoint.getSignature().toShortString();
//        String username = getCurrentUsername();
//        String details = "Request completed successfully";
//
//        AuditLog log = new AuditLog();
//        log.setTimestamp(LocalDateTime.now());
//        log.setActionType(actionType);
//        log.setUsername(username);
//        log.setDetails(details);
//
//
//        logger.info("Saving audit log: {}", log);
//
//        auditLogRepository.save(log);
//    }
//
//    /**
//     * Получает имя текущего пользователя из текущего HTTP-запроса.
//     *
//     * Этот метод извлекает имя пользователя из сессии HTTP-запроса.
//     * Если имя пользователя не найдено, возвращается "unknown".
//     *
//     *
//     * @return имя текущего пользователя или "unknown", если имя пользователя не найдено.
//     */
//    private String getCurrentUsername() {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (attributes != null) {
//            HttpServletRequest request = attributes.getRequest();
//            Object user = request.getSession().getAttribute("currentUser");
//            return user != null ? user.toString() : "unknown";
//        }
//        return "unknown";
//    }
//}
