package serviseTest;

import org.example.config.DatabaseConnectionManager;
import org.example.model.Car;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.PostgreSQLContainer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class CarServiceTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresContainer.start();
    }

    private CarService carService;
    private CarRepository carRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        // Установите параметры подключения к базе данных
        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());

        // Создание и настройка CarRepository
        carRepository = new CarRepository(new DatabaseConnectionManager(
                System.getProperty("db.url"),
                System.getProperty("db.username"),
                System.getProperty("db.password")
        ));

        carService = new CarService(carRepository);
        clearDatabase();
    }

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }


    private void clearDatabase() {
        try (Connection connection = DriverManager.getConnection(
                System.getProperty("db.url"),
                System.getProperty("db.username"),
                System.getProperty("db.password")
        )) {
            connection.createStatement().execute("DELETE FROM cars");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось очистить базу данных", e);
        }
    }
}

