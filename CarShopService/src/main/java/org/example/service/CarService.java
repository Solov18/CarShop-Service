package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.CarDTO;
import org.example.mapper.CarMapper;
import org.example.model.Car;
import org.example.repository.CarRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
public class CarService {

    private CarRepository carRepository;


    public void addCar(CarDTO carDTO) {

        Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
        carRepository.addCar(car);
    }

    // Получение всех доступных автомобилей
    public List<CarDTO> getAllAvailableCars() {

        List<Car> cars = carRepository.getAllAvailableCars();
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }


    public CarDTO getCarById(int id) {
        Optional<Car> carOptional = carRepository.getCarById(id);
        Car car = carOptional.orElseThrow(() -> new RuntimeException("Автомобиль с ID " + id + " не найден"));
        return CarMapper.INSTANCE.carToCarDTO(car);
    }


    public boolean removeCar(int id) {
        return carRepository.removeCar(id);
    }


    public boolean updateCar(CarDTO carDTO) {

        Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
        return carRepository.updateCar(car);
    }


    public List<CarDTO> searchCars(String make, String model, Integer year, Double minPrice, Double maxPrice, String condition) {
        List<Car> cars = carRepository.searchCars(make, model, year, minPrice, maxPrice, condition);
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }


    public List<CarDTO> getCarsByPriceRange(double minPrice, double maxPrice) {
        List<Car> cars = carRepository.getCarsByPriceRange(minPrice, maxPrice);
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }


    public List<CarDTO> getCarsByYearRange(int minYear, int maxYear) {
        List<Car> cars = carRepository.getCarsByYearRange(minYear, maxYear);
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }
}