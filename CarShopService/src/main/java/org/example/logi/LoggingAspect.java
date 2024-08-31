//package org.example.logi;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class LoggingAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
//
//    /**
//     * Логирует начало выполнения метода в любом классе пакета controller.
//     *
//     * @param joinPoint содержит информацию о целевом методе.
//     */
//    @Before("execution(* org.example.controller..*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//        logger.info("Запуск метода: {}", joinPoint.getSignature().getName());
//    }
//
//    /**
//     * Логирует завершение выполнения метода в любом классе пакета controller.
//     *
//     * @param joinPoint содержит информацию о целевом методе.
//     */
//    @After("execution(* org.example.controller..*(..))")
//    public void logAfter(JoinPoint joinPoint) {
//        logger.info("Завершился метод: {}", joinPoint.getSignature().getName());
//    }
//
//    /**
//     * Логирует время выполнения метода в любом классе пакета controller.
//     *
//     * @param joinPoint содержит информацию о целевом методе и позволяет управлять его выполнением.
//     * @return результат выполнения целевого метода.
//     * @throws Throwable если целевой метод выбрасывает исключение.
//     */
//    @Around("execution(* org.example.controller..*(..))")
//    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        long elapsedTime = System.currentTimeMillis() - start;
//        logger.info("{} Выполненный в {}мс", joinPoint.getSignature(), elapsedTime);
//        return result;
//    }
//}
