package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.example.logi.EnableAuditLog;
import org.example.logi.EnableLogging;

@SpringBootApplication
@EnableLogging
@EnableAuditLog
public class CarShopServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarShopServiceApplication.class, args);
    }
}