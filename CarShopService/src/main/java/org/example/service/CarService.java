package org.example.service;

import lombok.AllArgsConstructor;
import org.example.model.Car;
import org.example.repository.CarRepository;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
public class CarService {

    private CarRepository carRepository;

    // Метод для добавления нового автомобиля   /
    public void addCar(String make, String model, int year, double price, String condition)   {
        Car car = new Car(make, model, year, price, condition);
        carRepository.addCar(car);
    }

    // Получение всех доступных автомобилей /
    public List<Car> getAllAvailableCars() {
        return carRepository.getAllAvailableCars();
    }

    // Получение автомобиля по его ID   /
    public Car getCarById(int id) {
        Optional<Car> carOptional = carRepository.getCarById(id);
        return carOptional.orElseThrow(() -> new RuntimeException("Автомобиль с ID " + id + " не найден"));
    }


    // Удаление автомобиля по его ID /
    public boolean removeCar(int id)  {
        return carRepository.removeCar(id);
    }

    // Обновление информации об автомобиле /
    public boolean updateCar(Car car) {
        return carRepository.updateCar(car);
    }

    // Поиск автомобилей по заданным критериям /
    public List<Car> searchCars(String make, String model, Integer year, Double minPrice, Double maxPrice, String condition) {
        return carRepository.searchCars(make, model, year, minPrice, maxPrice, condition);
    }

    // Получение автомобилей в диапазоне цен
    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice)  {
        return carRepository.getCarsByPriceRange(minPrice, maxPrice);
    }

    // Получение автомобилей в диапазоне годов выпуска /
    public List<Car> getCarsByYearRange(int minYear, int maxYear)  {
        return carRepository.getCarsByYearRange(minYear, maxYear);
    }

}