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
/**
 * Аспект для аудита действий контроллеров, который перехватывает вызовы методов контроллеров
 * и записывает информацию об их выполнении в журнал аудита.
 *
 * Аспект используется для автоматической регистрации успешных вызовов методов контроллеров
 * и записи данных о действии, времени выполнения и пользователе, который вызвал метод.
 */
@Aspect
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    /**
     * Конструктор для инициализации класса аспекта с репозиторием аудита.
     *
     * @param auditLogRepository репозиторий для сохранения записей аудита.
     */
    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Определение точки среза для методов всех контроллеров в пакете com.example.controller.
     * Эта точка среза используется для перехвата всех методов контроллеров.
     */
    @Pointcut("execution(* com.example.controller.*.*(..))")
    public void controllerMethods() {}

    /**
     * Метод, выполняемый после успешного завершения вызова метода контроллера.
     * Логирует информацию о выполнении метода и сохраняет её в журнал аудита.
     *
     * @param joinPoint точка присоединения, содержащая информацию о методе, который был вызван.
     * @param result результат выполнения метода контроллера (не используется в логике, но может быть полезен для дальнейшей доработки).
     */
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
    /**
     * Получение имени текущего пользователя из сессии запроса.
     * Если пользователь не найден в сессии, возвращает "unknown".
     *
     * @return имя текущего пользователя или "unknown", если пользователь не найден.
     */
    private String getCurrentUsername() {
        HttpServletRequest request = RequestContext.getRequest();
        if (request != null) {
            Object user = request.getSession().getAttribute("currentUser");
            return user != null ? user.toString() : "unknown";
        }
        return "unknown";
    }
}