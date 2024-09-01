package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CarDTO;
import org.example.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car API", description = "API для управления автомобилями")
public class CarController {

    private final CarService carService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CarController(CarService carService, ObjectMapper objectMapper) {
        this.carService = carService;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Получить список автомобилей", description = "Получение списка всех доступных автомобилей или поиск по параметрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список автомобилей успешно возвращен"),
            @ApiResponse(responseCode = "500", description = "Ошибка при получении списка автомобилей")
    })
    @GetMapping
    public ResponseEntity<?> getCars(
            @Parameter(description = "ID автомобиля", example = "1") @RequestParam(value = "id", required = false) Integer id,
            @Parameter(description = "Марка автомобиля") @RequestParam(value = "make", required = false) String make,
            @Parameter(description = "Модель автомобиля") @RequestParam(value = "model", required = false) String model,
            @Parameter(description = "Год выпуска автомобиля") @RequestParam(value = "year", required = false) Integer year,
            @Parameter(description = "Минимальная цена") @RequestParam(value = "minPrice", required = false) Double minPrice,
            @Parameter(description = "Максимальная цена") @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @Parameter(description = "Состояние автомобиля") @RequestParam(value = "condition", required = false) String condition) {

        try {
            log.info("Fetching cars with parameters: id={}, make={}, model={}, year={}, minPrice={}, maxPrice={}, condition={}",
                    id, make, model, year, minPrice, maxPrice, condition);

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
            log.error("Error fetching cars", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Получить список автомобилей по диапазону цен", description = "Получение списка автомобилей, у которых цена в заданном диапазоне")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список автомобилей успешно возвращен"),
            @ApiResponse(responseCode = "500", description = "Ошибка при получении автомобилей по диапазону цен")
    })
    @GetMapping("/price-range")
    public ResponseEntity<?> getCarsByPriceRange(
            @Parameter(description = "Минимальная цена", example = "5000.0") @RequestParam("minPrice") double minPrice,
            @Parameter(description = "Максимальная цена", example = "30000.0") @RequestParam("maxPrice") double maxPrice) {
        try {
            log.info("Fetching cars in price range: {} - {}", minPrice, maxPrice);
            List<CarDTO> carsDTO = carService.getCarsByPriceRange(minPrice, maxPrice);
            return carsDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(carsDTO);
        } catch (Exception e) {
            log.error("Error fetching cars by price range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Получить список автомобилей по диапазону годов", description = "Получение списка автомобилей, у которых год выпуска в заданном диапазоне")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список автомобилей успешно возвращен"),
            @ApiResponse(responseCode = "500", description = "Ошибка при получении автомобилей по диапазону годов")
    })
    @GetMapping("/year-range")
    public ResponseEntity<?> getCarsByYearRange(
            @Parameter(description = "Минимальный год выпуска", example = "2000") @RequestParam("minYear") int minYear,
            @Parameter(description = "Максимальный год выпуска", example = "2022") @RequestParam("maxYear") int maxYear) {
        try {
            log.info("Fetching cars in year range: {} - {}", minYear, maxYear);
            List<CarDTO> carsDTO = carService.getCarsByYearRange(minYear, maxYear);
            return carsDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(carsDTO);
        } catch (Exception e) {
            log.error("Error fetching cars by year range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Добавить новый автомобиль", description = "Добавление новой записи об автомобиле")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Автомобиль успешно добавлен"),
            @ApiResponse(responseCode = "500", description = "Ошибка при добавлении нового автомобиля")
    })
    @PostMapping
    public ResponseEntity<CarDTO> addCar(
            @Parameter(description = "Данные нового автомобиля", required = true) @RequestBody CarDTO carDTO) {
        try {
            log.info("Adding new car: {}", carDTO);
            CarDTO addedCar = carService.addCar(carDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedCar);
        } catch (Exception e) {
            log.error("Error adding new car", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Обновить информацию об автомобиле", description = "Обновление существующей записи об автомобиле")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Автомобиль успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Автомобиль не найден для обновления"),
            @ApiResponse(responseCode = "500", description = "Ошибка при обновлении автомобиля")
    })
    @PutMapping
    public ResponseEntity<String> updateCar(
            @Parameter(description = "Данные автомобиля для обновления", required = true) @RequestBody CarDTO carDTO) {
        try {
            log.info("Updating car: {}", carDTO);
            boolean updated = carService.updateCar(carDTO);
            if (updated) {
                return ResponseEntity.ok("Автомобиль успешно обновлён");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Автомобиль не найден для обновления");
            }
        } catch (Exception e) {
            log.error("Error updating car", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении автомобиля");
        }
    }

    @Operation(summary = "Удалить автомобиль", description = "Удаление записи об автомобиле по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Автомобиль успешно удалён"),
            @ApiResponse(responseCode = "400", description = "ID автомобиля не указан"),
            @ApiResponse(responseCode = "404", description = "Автомобиль не найден для удаления"),
            @ApiResponse(responseCode = "500", description = "Ошибка при удалении автомобиля")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteCar(
            @Parameter(description = "ID автомобиля для удаления", example = "1") @RequestParam("id") Integer id) {
        try {
            log.info("Deleting car with id: {}", id);
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
        } catch (Exception e) {
            log.error("Error deleting car", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении автомобиля");
        }
    }
}
