package org.example;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.example.config.AppConfig;
import org.example.config.WebConfig;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class EmbeddedTomcat {
    public static void main(String[] args) throws Exception {
        // Создаем и настраиваем Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        // Создаем Spring ApplicationContext
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(AppConfig.class, WebConfig.class); // Регистрируем оба класса конфигурации

        // Создаем и регистрируем DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

        // Создаем контекст Tomcat
        Context context = tomcat.addContext("", null);

        // Добавляем DispatcherServlet в контекст Tomcat
        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet).addMapping("/");

        // Устанавливаем кодировку для запросов
        tomcat.getConnector().setURIEncoding("UTF-8");

        // Запускаем сервер Tomcat
        tomcat.start();
        tomcat.getServer().await();
    }
}
