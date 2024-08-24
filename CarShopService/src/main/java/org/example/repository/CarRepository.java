package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.model.Car;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления данными об автомобилях.
 */
@Repository
@RequiredArgsConstructor
public class CarRepository {

    private final JdbcTemplate jdbcTemplate;

    // Константы для SQL-запросов
    private static final String INSERT_CAR_SQL = "INSERT INTO cars (make, model, year, price, condition, is_available) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_CAR_BY_ID_SQL = "SELECT * FROM cars WHERE id = ?";
    private static final String SELECT_ALL_AVAILABLE_CARS_SQL = "SELECT * FROM cars WHERE is_available = true";
    private static final String UPDATE_CAR_SQL = "UPDATE cars SET make = ?, model = ?, year = ?, price = ?, condition = ?, is_available = ? WHERE id = ?";
    private static final String DELETE_CAR_BY_ID_SQL = "DELETE FROM cars WHERE id = ?";
    private static final String SEARCH_CARS_SQL_BASE = "SELECT * FROM cars WHERE 1=1";
    private static final String SELECT_CARS_BY_PRICE_RANGE_SQL = "SELECT * FROM cars WHERE price BETWEEN ? AND ?";
    private static final String SELECT_CARS_BY_YEAR_RANGE_SQL = "SELECT * FROM cars WHERE year BETWEEN ? AND ?";

    private final RowMapper<Car> carRowMapper = (rs, rowNum) -> new Car(
            rs.getInt("id"),
            rs.getString("make"),
            rs.getString("model"),
            rs.getInt("year"),
            rs.getDouble("price"),
            rs.getString("condition"),
            rs.getBoolean("is_available")
    );

    /**
     * Добавление нового автомобиля.
     *
     * @param car объект автомобиля для добавления.
     * @return объект автомобиля с присвоенным идентификатором.
     */
    public Car addCar(Car car) {
        return jdbcTemplate.queryForObject(
                INSERT_CAR_SQL,
                new Object[]{
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        car.getPrice(),
                        car.getCondition(),
                        car.isAvailable()
                },
                carRowMapper
        );
    }


    /**
     * Получение автомобиля по идентификатору.
     *
     * @param id идентификатор автомобиля.
     * @return объект автомобиля, если он найден.
     */
    public Optional<Car> getCarById(int id) {
        try {
            Car car = jdbcTemplate.queryForObject(SELECT_CAR_BY_ID_SQL, new Object[]{id}, carRowMapper);
            return Optional.ofNullable(car);
        } catch (Exception e) {
            // Логирование ошибки
            // Можно заменить System.err на ваш логер
            System.err.println("Ошибка при получении автомобиля по ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Получение всех доступных автомобилей.
     *
     * @return список доступных автомобилей.
     */
    public List<Car> getAllAvailableCars() {
        return jdbcTemplate.query(SELECT_ALL_AVAILABLE_CARS_SQL, carRowMapper);
    }

    /**
     * Обновление информации об автомобиле.
     *
     * @param car объект автомобиля с обновленными данными.
     * @return true, если автомобиль успешно обновлен, иначе false.
     */
    public boolean updateCar(Car car) {
        return jdbcTemplate.update(
                UPDATE_CAR_SQL,
                car.getMake(),
                car.getModel(),
                car.getYear(),
                car.getPrice(),
                car.getCondition(),
                car.isAvailable(),
                car.getId()
        ) > 0;
    }

    /**
     * Удаление автомобиля по его идентификатору.
     *
     * @param id идентификатор автомобиля.
     * @return true, если автомобиль успешно удален, иначе false.
     */
    public boolean removeCar(int id) {
        return jdbcTemplate.update(DELETE_CAR_BY_ID_SQL, id) > 0;
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
     * @return список автомобилей, соответствующих критериям поиска.
     */
    public List<Car> searchCars(String make, String model, Integer year, Double minPrice, Double maxPrice, String condition) {
        StringBuilder sql = new StringBuilder(SEARCH_CARS_SQL_BASE);
        List<Object> parameters = new ArrayList<>();

        if (make != null) {
            sql.append(" AND make = ?");
            parameters.add(make);
        }
        if (model != null) {
            sql.append(" AND model = ?");
            parameters.add(model);
        }
        if (year != null) {
            sql.append(" AND year = ?");
            parameters.add(year);
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            parameters.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            parameters.add(maxPrice);
        }
        if (condition != null) {
            sql.append(" AND condition = ?");
            parameters.add(condition);
        }

        return jdbcTemplate.query(sql.toString(), parameters.toArray(), carRowMapper);
    }

    /**
     * Поиск автомобилей по диапазону цен.
     *
     * @param minPrice минимальная цена.
     * @param maxPrice максимальная цена.
     * @return список автомобилей, находящихся в заданном диапазоне цен.
     */
    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice) {
        return jdbcTemplate.query(SELECT_CARS_BY_PRICE_RANGE_SQL, new Object[]{minPrice, maxPrice}, carRowMapper);
    }

    /**
     * Поиск автомобилей по диапазону годов.
     *
     * @param minYear минимальный год выпуска.
     * @param maxYear максимальный год выпуска.
     * @return список автомобилей, находящихся в заданном диапазоне годов выпуска.
     */
    public List<Car> getCarsByYearRange(int minYear, int maxYear) {
        return jdbcTemplate.query(SELECT_CARS_BY_YEAR_RANGE_SQL, new Object[]{minYear, maxYear}, carRowMapper);
    }
}
