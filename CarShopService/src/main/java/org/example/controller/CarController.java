package org.example.controller;

import org.example.model.Car;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CarController {
    private List<Car> cars = new ArrayList<>();
    private int nextId = 1;

    // Добавление нового автомобиля
    public void addCar(String make, String model, int year, double price, String condition) {
        Car car = new Car(make, model, year, price, condition);
        cars.add(car);
        nextId = Math.max(nextId, car.getId() + 1);
    }

    // Просмотр списка всех доступных автомобилей
    public List<Car> getAllAvailableCars() {
        return cars.stream().filter(Car::isAvailable).collect(Collectors.toList());
    }

    // Получение автомобиля по ID
    public Car getCarById(int id) {
        return cars.stream().filter(car -> car.getId() == id).findFirst().orElse(null);
    }

    // Удаление автомобиля
    public boolean removeCar(int id) {
        Car car = getCarById(id);
        if (car != null) {
            cars.remove(car);
            return true;
        }
        return false;
    }

    // Редактирование информации об автомобиле
    public boolean updateCar(int id, String make, String model, int year, double price, String condition) {
        Car car = getCarById(id);
        if (car != null) {
            car.setMake(make);
            car.setModel(model);
            car.setYear(year);
            car.setPrice(price);
            car.setCondition(condition);
            return true;
        }
        return false;
    }

    // Сохранение списка автомобилей в файл
    public void saveCarsToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(cars);
        }
    }

    // Загрузка списка автомобилей из файла
    @SuppressWarnings("unchecked")
    public void loadCarsFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            cars = (List<Car>) ois.readObject();
            nextId = cars.stream().mapToInt(Car::getId).max().orElse(1) + 1;
        }
    }

    // Поиск автомобилей по критериям
    public List<Car> searchCars(String make, String model, Integer year, Double minPrice, Double maxPrice, String condition) {
        return cars.stream()
                .filter(car -> (make == null || car.getMake().equalsIgnoreCase(make)) &&
                        (model == null || car.getModel().equalsIgnoreCase(model)) &&
                        (year == null || car.getYear() == year) &&
                        (minPrice == null || car.getPrice() >= minPrice) &&
                        (maxPrice == null || car.getPrice() <= maxPrice) &&
                        (condition == null || car.getCondition().equalsIgnoreCase(condition)))
                .collect(Collectors.toList());
    }

    // Получение автомобилей по диапазону цен
    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice) {
        return cars.stream()
                .filter(car -> car.getPrice() >= minPrice && car.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    // Получение автомобилей по диапазону годов
    public List<Car> getCarsByYearRange(int minYear, int maxYear) {
        return cars.stream()
                .filter(car -> car.getYear() >= minYear && car.getYear() <= maxYear)
                .collect(Collectors.toList());
    }
}