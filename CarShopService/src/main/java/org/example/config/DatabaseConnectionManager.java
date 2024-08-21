package org.example.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * Класс для управления подключением к базе данных.
 * Загружает параметры подключения из файла конфигурации `application.yaml`
 * или принимает их через конструктор.
 */
public class DatabaseConnectionManager {

    private String url;
    private String username;
    private String password;

    /**
     * Конструктор по умолчанию.
     * Загружает конфигурацию базы данных из файла `application.yaml`.
     *
     * @throws RuntimeException если файл `application.yaml` не найден или при возникновении ошибки загрузки конфигурации.
     */
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

    /**
     * Конструктор с параметрами подключения к базе данных.
     *
     * @param url      URL для подключения к базе данных.
     * @param username Имя пользователя базы данных.
     * @param password Пароль пользователя базы данных.
     */
    public DatabaseConnectionManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Получает подключение к базе данных.
     *
     * @return объект Connection для работы с базой данных.
     * @throws SQLException если не удается установить подключение к базе данных.
     * @throws RuntimeException если параметры подключения к базе данных не установлены.
     */
    public Connection getConnection() throws SQLException {
        if (url == null || username == null || password == null) {
            throw new RuntimeException("Не установлены параметры подключения к базе данных");
        }
        return DriverManager.getConnection(url, username, password);
    }
}