package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.CarDTO;
import org.example.mapper.CarMapper;
import org.example.model.Car;
import org.example.repository.CarRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Сервисный класс для управления автомобилями. Предоставляет методы для добавления,
 * получения, обновления и удаления автомобилей, а также для поиска автомобилей по различным критериям.
 */
@AllArgsConstructor
public class CarService {

    private CarRepository carRepository;


    /**
     * Добавление нового автомобиля.
     *
     * @param carDTO объект DTO автомобиля, который нужно добавить.
     */
    public void addCar(CarDTO carDTO) {

        Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
        carRepository.addCar(car);
    }

    /**
     * Получение всех доступных автомобилей.
     *
     * @return список DTO всех доступных автомобилей.
     */
    public List<CarDTO> getAllAvailableCars() {

        List<Car> cars = carRepository.getAllAvailableCars();
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }


    /**
     * Получение автомобиля по его идентификатору.
     *
     * @param id идентификатор автомобиля.
     * @return DTO автомобиля, если он найден.
     * @throws RuntimeException если автомобиль с данным идентификатором не найден.
     */
    public CarDTO getCarById(int id) {
        Optional<Car> carOptional = carRepository.getCarById(id);
        Car car = carOptional.orElseThrow(() -> new RuntimeException("Автомобиль с ID " + id + " не найден"));
        return CarMapper.INSTANCE.carToCarDTO(car);
    }


    /**
     * Удаление автомобиля по его идентификатору.
     *
     * @param id идентификатор автомобиля.
     * @return true, если автомобиль успешно удален, иначе false.
     */
    public boolean removeCar(int id) {
        return carRepository.removeCar(id);
    }


    /**
     * Обновление информации об автомобиле.
     *
     * @param carDTO объект DTO автомобиля с обновленными данными.
     * @return true, если автомобиль успешно обновлен, иначе false.
     */
    public boolean updateCar(CarDTO carDTO) {

        Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
        return carRepository.updateCar(car);
    }


    /**
     * Поиск автомобилей по заданным критериям.
     *
     * @param make марка автомобиля.
     * @param model модель автомобиля.
     * @param year год выпуска автомобиля.
     * @param minPrice минимальная цена автомобиля.
     * @param maxPrice максимальная цена автомобиля.
     * @param condition состояние автомобиля (новый или подержанный).
     * @return список DTO автомобилей, соответствующих критериям поиска.
     */
    public List<CarDTO> searchCars(String make, String model, Integer year, Double minPrice, Double maxPrice, String condition) {
        List<Car> cars = carRepository.searchCars(make, model, year, minPrice, maxPrice, condition);
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }


    /**
     * Получение автомобилей по диапазону цен.
     *
     * @param minPrice минимальная цена.
     * @param maxPrice максимальная цена.
     * @return список DTO автомобилей, находящихся в заданном диапазоне цен.
     */
    public List<CarDTO> getCarsByPriceRange(double minPrice, double maxPrice) {
        List<Car> cars = carRepository.getCarsByPriceRange(minPrice, maxPrice);
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }


    /**
     * Получение автомобилей по диапазону годов выпуска.
     *
     * @param minYear минимальный год выпуска.
     * @param maxYear максимальный год выпуска.
     * @return список DTO автомобилей, находящихся в заданном диапазоне годов выпуска.
     */
    public List<CarDTO> getCarsByYearRange(int minYear, int maxYear) {
        List<Car> cars = carRepository.getCarsByYearRange(minYear, maxYear);
        return cars.stream()
                .map(CarMapper.INSTANCE::carToCarDTO)
                .collect(Collectors.toList());
    }
}