package org.example;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.example.controller.CarController;
import org.example.controller.ClientController;
import org.example.controller.OrderController;
import org.example.controller.UserController;


public class EmbeddedTomcat {
    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context ctx = tomcat.addContext("", null);

        Tomcat.addServlet(ctx, "carController", new CarController());
        ctx.addServletMappingDecoded("/api/cars", "carController");

        Tomcat.addServlet(ctx, "clientController", new ClientController());
        ctx.addServletMappingDecoded("/api/clients", "clientController");

        Tomcat.addServlet(ctx, "orderController", new OrderController());
        ctx.addServletMappingDecoded("/api/orders", "orderController");

        Tomcat.addServlet(ctx, "userController", new UserController());
        ctx.addServletMappingDecoded("/api/users", "userController");

        tomcat.start();
        tomcat.getServer().await();
    }
}