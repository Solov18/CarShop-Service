import org.example.config.DatabaseConnectionManager;
import org.example.model.Car;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

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

        // Создание CarController с зависимостью от CarRepository
        carService = new CarService(carRepository);

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
        carService.addCar("Toyota", "Corolla", 2020, 20000, "New");

        List<Car> cars = carService.getAllAvailableCars();
        assertThat(cars).isNotEmpty();
        assertThat(cars.get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    public void testRemoveCar() {
        carService.addCar("Toyota", "Corolla", 2020, 20000, "New");
        List<Car> cars = carService.getAllAvailableCars();
        Car car = cars.get(0);

        boolean removed = carService.removeCar(car.getId());
        assertThat(removed).isTrue();

        List<Car> updatedCars = carService.getAllAvailableCars();
        assertThat(updatedCars).isEmpty();
    }


    @Test
    public void testSearchCars() {
        carService.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carService.addCar("Honda", "Civic", 2019, 18000, "Used");

        List<Car> results = carService.searchCars("Toyota", null, null, null, null, null);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getModel()).isEqualTo("Corolla");

        results = carService.searchCars(null, "Civic", 2019, null, null, "Used");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMake()).isEqualTo("Honda");
    }

    @Test
    public void testGetCarsByPriceRange() {
        carService.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carService.addCar("Honda", "Civic", 2019, 18000, "Used");

        List<Car> cars = carService.getCarsByPriceRange(15000, 20000);
        assertThat(cars).hasSize(2);

        cars = carService.getCarsByPriceRange(19000, 20000);
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Corolla");
    }

    @Test
    public void testGetCarsByYearRange() {
        carService.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carService.addCar("Honda", "Civic", 2019, 18000, "Used");

        List<Car> cars = carService.getCarsByYearRange(2018, 2020);
        assertThat(cars).hasSize(2);

        cars = carService.getCarsByYearRange(2020, 2020);
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Corolla");
    }
}