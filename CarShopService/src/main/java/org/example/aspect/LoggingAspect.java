package org.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;




@Aspect
public class LoggingAspect {

    @Before("execution(* org.example.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Запуск метода:  " + joinPoint.getSignature().getName());
    }

    @After("execution(* org.example.controller..*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("Завершился метод:  " + joinPoint.getSignature().getName());
    }

    @Around("execution(* org.example.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " Выполненный в " + elapsedTime + "мс");
        return proceed;
    }
}