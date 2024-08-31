package org.example.logi;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* org.example.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Запуск метода: {}", joinPoint.getSignature().getName());
    }

    @After("execution(* org.example.controller..*(..))")
    public void logAfter(JoinPoint joinPoint) {
        logger.info("Завершился метод: {}", joinPoint.getSignature().getName());
    }

    @Around("execution(* org.example.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info("{} Выполненный в {}мс", joinPoint.getSignature(), elapsedTime);
        return result;
    }
}