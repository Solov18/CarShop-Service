package org.example.config;

import org.example.logi.RequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import javax.sql.DataSource;


@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

    // Фильтр RequestContextFilter
    @Bean
    public RequestContextFilter requestContextFilter() {
        return new RequestContextFilter();
    }

    // Настройка DataSource и JdbcTemplate
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5433/mydatabase");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // Настройка Swagger UI ресурсов
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Обслуживание HTML-файла Swagger UI
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/dist/");

        // Настройка для обслуживания ресурсов, таких как JS и CSS
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webpack");
    }
}