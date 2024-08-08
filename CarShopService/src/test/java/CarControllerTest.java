import org.example.controller.CarController;
import org.example.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CarControllerTest {

    private CarController carController;

    @BeforeEach
    public void setUp() {
        carController = new CarController();
    }

    @Test
    public void testAddCar() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");

        List<Car> cars = carController.getAllAvailableCars();
        assertThat(cars).isNotEmpty();
    }

    @Test
    public void testRemoveCar() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);
        boolean removed = carController.removeCar(car.getId());

        assertThat(removed).isTrue();
    }

    @Test
    public void testUpdateCar() {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);

        carController.updateCar(car.getId(), "Toyota", "Camry", 2021, 22000, "Used");
        Car updatedCar = carController.getCarById(car.getId());

        assertThat(updatedCar.getModel()).isEqualTo("Camry");
    }


    @Test
    public void testSaveCarsToFile() throws IOException, ClassNotFoundException {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carController.saveCarsToFile("cars.dat");

        // Create a new CarController instance and load cars from the file
        CarController newCarController = new CarController();
        newCarController.loadCarsFromFile("cars.dat");

        List<Car> cars = newCarController.getAllAvailableCars();
        assertThat(cars).isNotEmpty();
        assertThat(cars.get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    public void testLoadCarsFromFile() throws IOException, ClassNotFoundException {
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        carController.saveCarsToFile("cars.dat");

        CarController newCarController = new CarController();
        newCarController.loadCarsFromFile("cars.dat");

        List<Car> cars = newCarController.getAllAvailableCars();
        assertThat(cars).isNotEmpty();
        assertThat(cars.get(0).getMake()).isEqualTo("Toyota");
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

