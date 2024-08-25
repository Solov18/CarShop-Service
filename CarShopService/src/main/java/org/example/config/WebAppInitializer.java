//package org.example.config;
//import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
//
//public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
//
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return new Class<?>[]{AppConfig.class}; // Основная конфигурация
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        return new Class<?>[]{WebConfig.class}; // Конфигурация для DispatcherServlet
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[]{"/"}; // Все запросы будут обрабатываться DispatcherServlet
//    }
//}