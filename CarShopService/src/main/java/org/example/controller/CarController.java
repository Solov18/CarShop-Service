package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.CarDTO;
import org.example.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/cars")
@Api(value = "Car API", tags = {"Автомобили"})
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @ApiOperation(value = "Получить список автомобилей", notes = "Получение списка всех доступных автомобилей или поиск по параметрам")
    @GetMapping
    public ResponseEntity<?> getCars(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "make", required = false) String make,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "condition", required = false) String condition) {

        try {
            if (Objects.nonNull(id)) {
                CarDTO carDTO = carService.getCarById(id);
                return ResponseEntity.ok(carDTO);
            } else if (Objects.nonNull(make) || Objects.nonNull(model) || Objects.nonNull(year) ||
                    Objects.nonNull(minPrice) || Objects.nonNull(maxPrice) || Objects.nonNull(condition)) {
                List<CarDTO> carsDTO = carService.searchCars(make, model, year, minPrice, maxPrice, condition);
                return ResponseEntity.ok(carsDTO);
            } else {
                List<CarDTO> carsDTO = carService.getAllAvailableCars();
                return carsDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(carsDTO);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Получить список автомобилей по диапазону цен", notes = "Получение списка автомобилей, у которых цена в заданном диапазоне")
    @GetMapping("/price-range")
    public ResponseEntity<?> getCarsByPriceRange(
            @RequestParam("minPrice") double minPrice,
            @RequestParam("maxPrice") double maxPrice) {
        try {
            List<CarDTO> carsDTO = carService.getCarsByPriceRange(minPrice, maxPrice);
            return carsDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(carsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Получить список автомобилей по диапазону годов", notes = "Получение списка автомобилей, у которых год выпуска в заданном диапазоне")
    @GetMapping("/year-range")
    public ResponseEntity<?> getCarsByYearRange(
            @RequestParam("minYear") int minYear,
            @RequestParam("maxYear") int maxYear) {
        try {
            List<CarDTO> carsDTO = carService.getCarsByYearRange(minYear, maxYear);
            return carsDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(carsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Добавить новый автомобиль", notes = "Добавление новой записи об автомобиле")
    @PostMapping
    public ResponseEntity<CarDTO> addCar(@RequestBody CarDTO carDTO) {
        CarDTO addedCar = carService.addCar(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedCar);
    }

    @ApiOperation(value = "Обновить информацию об автомобиле", notes = "Обновление существующей записи об автомобиле")
    @PutMapping
    public ResponseEntity<String> updateCar(@RequestBody CarDTO carDTO) {
        boolean updated = carService.updateCar(carDTO);
        if (updated) {
            return ResponseEntity.ok("Автомобиль успешно обновлён");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Автомобиль не найден для обновления");
        }
    }

    @ApiOperation(value = "Удалить автомобиль", notes = "Удаление записи об автомобиле по ID")
    @DeleteMapping
    public ResponseEntity<String> deleteCar(@RequestParam("id") Integer id) {
        if (Objects.nonNull(id)) {
            boolean removed = carService.removeCar(id);
            if (removed) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Автомобиль не найден для удаления");
            }
        } else {
            return ResponseEntity.badRequest().body("ID автомобиля не указан");
        }
    }
}
