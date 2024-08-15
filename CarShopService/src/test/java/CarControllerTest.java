import org.example.config.DatabaseConnectionManager;
import org.example.controller.CarController;
import org.example.model.Car;
import org.example.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CarControllerTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresContainer.start();
    }

    private CarController carController;
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

        // Создание CarController с зависимостью от CarRepository
        carController = new CarController(carRepository);

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
            connection.createStatement().execute("DELETE FROM cars");
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось очистить базу данных", e);
        }
    }

    @Test
    public void testAddCar() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");

        List<Car> cars = carController.getAllAvailableCars();
        assertThat(cars).isNotEmpty();
        assertThat(cars.get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    public void testRemoveCar() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        List<Car> cars = carController.getAllAvailableCars();
        Car car = cars.get(0);

        boolean removed = carController.removeCar(car.getId());
        assertThat(removed).isTrue();

        List<Car> updatedCars = carController.getAllAvailableCars();
        assertThat(updatedCars).isEmpty();
    }


    @Test
    public void testSearchCars() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carController.addCar("Honda", "Civic", 2019, 18000, "Used");

        List<Car> results = carController.searchCars("Toyota", null, null, null, null, null);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getModel()).isEqualTo("Corolla");

        results = carController.searchCars(null, "Civic", 2019, null, null, "Used");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMake()).isEqualTo("Honda");
    }

    @Test
    public void testGetCarsByPriceRange() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carController.addCar("Honda", "Civic", 2019, 18000, "Used");

        List<Car> cars = carController.getCarsByPriceRange(15000, 20000);
        assertThat(cars).hasSize(2);

        cars = carController.getCarsByPriceRange(19000, 20000);
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Corolla");
    }

    @Test
    public void testGetCarsByYearRange() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carController.addCar("Honda", "Civic", 2019, 18000, "Used");

        List<Car> cars = carController.getCarsByYearRange(2018, 2020);
        assertThat(cars).hasSize(2);

        cars = carController.getCarsByYearRange(2020, 2020);
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Corolla");
    }
}