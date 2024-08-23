package org.example;

import org.apache.catalina.startup.Tomcat;
import org.example.config.AppConfig;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class EmbeddedTomcat {
    public static void main(String[] args) throws Exception {
        // Создаем экземпляр Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080); // Устанавливаем порт для Tomcat

        // Создаем Spring ApplicationContext
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(AppConfig.class);

        // Создаем и регистрируем DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

        // Добавляем контекст и сервлет для обработки запросов
        tomcat.addContext("", System.getProperty("java.io.tmpdir")); // Создаем корневой контекст
        tomcat.addServlet("", "dispatcherServlet", dispatcherServlet).setLoadOnStartup(1);
        tomcat.getConnector().setURIEncoding("UTF-8"); // Устанавливаем кодировку

        // Запускаем сервер
        tomcat.start();
        tomcat.getServer().await();
    }
}