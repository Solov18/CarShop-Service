package org.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;




/**
 * Аспект для логирования выполнения методов в контроллерах.
 * Содержит методы для логирования перед, после и во время выполнения методов.
 */
@Aspect
public class LoggingAspect {

    /**
     * Логирует начало выполнения метода в любом классе пакета controller.
     *
     * @param joinPoint содержит информацию о целевом методе.
     */
    @Before("execution(* org.example.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Запуск метода:  " + joinPoint.getSignature().getName());
    }

    /**
     * Логирует завершение выполнения метода в любом классе пакета controller.
     *
     * @param joinPoint содержит информацию о целевом методе.
     */
    @After("execution(* org.example.controller..*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("Завершился метод:  " + joinPoint.getSignature().getName());
    }

    /**
     * Логирует время выполнения метода в любом классе пакета controller.
     *
     * @param joinPoint содержит информацию о целевом методе и позволяет управлять его выполнением.
     * @return результат выполнения целевого метода.
     * @throws Throwable если целевой метод выбрасывает исключение.
     */
    @Around("execution(* org.example.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " Выполненный в " + elapsedTime + "мс");
        return proceed;
    }
}