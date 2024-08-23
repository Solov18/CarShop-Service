package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Autowired
    public CarController(CarService carService, ObjectMapper objectMapper) {
        this.carService = carService;
        this.objectMapper = objectMapper;
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
                return ResponseEntity.ok(carsDTO);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Добавить новый автомобиль", notes = "Добавление новой записи об автомобиле")
    @PostMapping
    public ResponseEntity<Void> addCar(@RequestBody CarDTO carDTO) {
        carService.addCar(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "Обновить информацию об автомобиле", notes = "Обновление существующей записи об автомобиле")
    @PutMapping
    public ResponseEntity<String> updateCar(@RequestBody CarDTO carDTO) {
        boolean updated = carService.updateCar(carDTO);
        if (updated) {
            return ResponseEntity.ok().build();
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
