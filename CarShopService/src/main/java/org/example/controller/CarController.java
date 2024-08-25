package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CarDTO;
import org.example.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Контроллер для управления автомобилями.
 * Предоставляет API для выполнения CRUD операций с автомобилями, включая получение списка,
 * добавление, обновление и удаление автомобилей, а также поиск по параметрам.
 */
@Slf4j
@RestController
@RequestMapping("/api/cars")
@Api(value = "Car API", tags = {"Автомобили"})
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Получение списка автомобилей. Возможность поиска по параметрам или получение всего списка.
     *
     * @param id ID автомобиля (необязательный параметр)
     * @param make Производитель автомобиля (необязательный параметр)
     * @param model Модель автомобиля (необязательный параметр)
     * @param year Год выпуска автомобиля (необязательный параметр)
     * @param minPrice Минимальная цена автомобиля (необязательный параметр)
     * @param maxPrice Максимальная цена автомобиля (необязательный параметр)
     * @param condition Состояние автомобиля (необязательный параметр)
     * @return Список автомобилей, соответствующих критериям поиска, или весь список доступных автомобилей
     */
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

    /**
     * Получение списка автомобилей по диапазону цен.
     *
     * @param minPrice Минимальная цена
     * @param maxPrice Максимальная цена
     * @return Список автомобилей в указанном диапазоне цен
     */
    @ApiOperation(value = "Получить список автомобилей по диапазону цен", notes = "Получение списка автомобилей, у которых цена в заданном диапазоне")
    @GetMapping("/price-range")
    public ResponseEntity<?> getCarsByPriceRange(
            @RequestParam("minPrice") double minPrice,
            @RequestParam("maxPrice") double maxPrice) {
        try {
            log.info("Fetching cars in price range: {} - {}", minPrice, maxPrice);
            List<CarDTO> carsDTO = carService.getCarsByPriceRange(minPrice, maxPrice);
            return carsDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(carsDTO);
        } catch (Exception e) {
            log.error("Error fetching cars by price range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Получение списка автомобилей по диапазону годов выпуска.
     *
     * @param minYear Минимальный год выпуска
     * @param maxYear Максимальный год выпуска
     * @return Список автомобилей, выпущенных в указанном диапазоне годов
     */
    @ApiOperation(value = "Получить список автомобилей по диапазону годов", notes = "Получение списка автомобилей, у которых год выпуска в заданном диапазоне")
    @GetMapping("/year-range")
    public ResponseEntity<?> getCarsByYearRange(
            @RequestParam("minYear") int minYear,
            @RequestParam("maxYear") int maxYear) {
        try {
            log.info("Fetching cars in year range: {} - {}", minYear, maxYear);
            List<CarDTO> carsDTO = carService.getCarsByYearRange(minYear, maxYear);
            return carsDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(carsDTO);
        } catch (Exception e) {
            log.error("Error fetching cars by year range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Добавление нового автомобиля.
     *
     * @param carDTO DTO объекта автомобиля для добавления
     * @return Добавленный автомобиль
     */
    @ApiOperation(value = "Добавить новый автомобиль", notes = "Добавление новой записи об автомобиле")
    @PostMapping
    public ResponseEntity<CarDTO> addCar(@RequestBody CarDTO carDTO) {
        try {
            log.info("Adding new car: {}", carDTO);
            CarDTO addedCar = carService.addCar(carDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedCar);
        } catch (Exception e) {
            log.error("Error adding new car", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Обновление информации об автомобиле.
     *
     * @param carDTO DTO объекта автомобиля с обновленной информацией
     * @return Сообщение о результате обновления
     */
    @ApiOperation(value = "Обновить информацию об автомобиле", notes = "Обновление существующей записи об автомобиле")
    @PutMapping
    public ResponseEntity<String> updateCar(@RequestBody CarDTO carDTO) {
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

    /**
     * Удаление автомобиля по его ID.
     *
     * @param id ID автомобиля для удаления
     * @return Сообщение о результате удаления
     */
    @ApiOperation(value = "Удалить автомобиль", notes = "Удаление записи об автомобиле по ID")
    @DeleteMapping
    public ResponseEntity<String> deleteCar(@RequestParam("id") Integer id) {
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
