package serviseTest;

import org.example.config.DatabaseConnectionManager;
import org.example.repository.CarRepository;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.example.service.CarService;
import org.example.service.OrderService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.PostgreSQLContainer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OrderServiceTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresContainer.start();
    }

    private OrderService orderService;
    private CarService carService;
    private UserService userService;
    private OrderRepository orderRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws SQLException {

        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());


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


        carService = new CarService(carRepository);
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService, carRepository);


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
            connection.createStatement().execute("DELETE FROM orders");
            connection.createStatement().execute("DELETE FROM cars");
            connection.createStatement().execute("DELETE FROM users");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось очистить базу данных", e);
        }
    }






}


