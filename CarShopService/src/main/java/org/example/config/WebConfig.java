package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "org.example.controller",
        "org.example.service",
        "org.example.repository",
        "org.example.mapper"
})
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/swagger-ui/")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui/");


   //    адрес страницы    http://localhost:8080/swagger-ui/index.html



        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}