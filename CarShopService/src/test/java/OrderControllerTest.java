import org.example.config.DatabaseConnectionManager;
import org.example.controller.CarController;
import org.example.controller.OrderController;
import org.example.controller.UserController;
import org.example.model.Car;
import org.example.model.Client;
import org.example.model.User;
import org.example.repository.CarRepository;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.model.Order;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OrderControllerTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresContainer.start();
    }

    private OrderController orderController;
    private CarController carController;
    private UserController userController;
    private OrderRepository orderRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        // Устанавливаем параметры подключения к базе данных
        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());

        // Создаем и настраиваем репозитории
        orderRepository = new OrderRepository(
                new CarRepository(new DatabaseConnectionManager(
                        System.getProperty("db.url"),
                        System.getProperty("db.username"),
                        System.getProperty("db.password"))),
                new UserRepository(new DatabaseConnectionManager(
                        System.getProperty("db.url"),
                        System.getProperty("db.username"),
                        System.getProperty("db.password"))),
                new DatabaseConnectionManager(
                        System.getProperty("db.url"),
                        System.getProperty("db.username"),
                        System.getProperty("db.password"))
        );

        carRepository = new CarRepository(new DatabaseConnectionManager(
                System.getProperty("db.url"),
                System.getProperty("db.username"),
                System.getProperty("db.password"))
        );
        userRepository = new UserRepository(new DatabaseConnectionManager(
                System.getProperty("db.url"),
                System.getProperty("db.username"),
                System.getProperty("db.password"))
        );

        // Создаем контроллеры с зависимостями
        carController = new CarController(carRepository);
        userController = new UserController(userRepository);
        orderController = new OrderController(orderRepository, userController, carRepository);

        // Очистка данных перед каждым тестом
        clearDatabase();
    }

    @AfterEach
    public void tearDown() {
        // Очистка данных после каждого теста
        clearDatabase();
    }

    private void clearDatabase() {
        try (Connection connection = DriverManager.getConnection(
                System.getProperty("db.url"),
                System.getProperty("db.username"),
                System.getProperty("db.password")
        )) {
            connection.createStatement().execute("DELETE FROM orders");
            connection.createStatement().execute("DELETE FROM cars");
            connection.createStatement().execute("DELETE FROM users");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось очистить базу данных", e);
        }
    }






}


