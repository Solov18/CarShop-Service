package org.example;

import org.apache.catalina.Context;
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

        // Добавляем корневой контекст и сервлет для обработки запросов
        Context context = tomcat.addContext("", null); // Используйте null или пустую строку для временного каталога
        tomcat.addServlet("", "dispatcherServlet", dispatcherServlet).addMapping("/*");
        tomcat.getConnector().setURIEncoding("UTF-8"); // Устанавливаем кодировку

        // Запускаем сервер
        tomcat.start();
        tomcat.getServer().await();
    }
}
