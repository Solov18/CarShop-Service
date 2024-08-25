package org.example;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.example.config.AppConfig;
import org.example.config.WebConfig;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class EmbeddedTomcat {
    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);


        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(AppConfig.class, WebConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

        Context context = tomcat.addContext("", null);

        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet).addMapping("/");

        tomcat.getConnector().setURIEncoding("UTF-8");

        tomcat.start();
        tomcat.getServer().await();
    }
}
