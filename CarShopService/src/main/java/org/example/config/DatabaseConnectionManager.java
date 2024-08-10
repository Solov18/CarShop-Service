package org.example.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseConnectionManager {

    private String url;
    private String username;
    private String password;

    public DatabaseConnectionManager() {
        Yaml yaml = new Yaml();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.yaml")) {
            if (input == null) {
                throw new RuntimeException("Не удалось найти application.yaml");
            }
            Map<String, Object> properties = yaml.load(input);
            Map<String, String> dbConfig = (Map<String, String>) properties.get("database");
            url = dbConfig.get("url");
            username = dbConfig.get("username");
            password = dbConfig.get("password");
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка при загрузке конфигурации базы данных", ex);
        }
    }

    public Connection getConnection() throws SQLException {
        if (url == null || username == null || password == null) {
            throw new RuntimeException("Не установлены параметры подключения к базе данных");
        }
        return DriverManager.getConnection(url, username, password);
    }
}