package org.example.repository;

import lombok.AllArgsConstructor;
import org.example.config.DatabaseConnectionManager;
import org.example.model.Car;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
public class CarRepository {

    private DatabaseConnectionManager dbConnectionManager;

    private Connection getConnection() throws SQLException {
        return dbConnectionManager.getConnection();
    }

    // Добавление нового автомобиля в базу данных
    public void addCar(Car car) {
        String sql = "INSERT INTO cars (make, model, year, price, condition, is_available) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, car.getMake());
            preparedStatement.setString(2, car.getModel());
            preparedStatement.setInt(3, car.getYear());
            preparedStatement.setDouble(4, car.getPrice());
            preparedStatement.setString(5, car.getCondition());
            preparedStatement.setBoolean(6, car.isAvailable());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении автомобиля в базу данных: " + e.getMessage());
            throw new RuntimeException("Не удалось добавить автомобиль в базу данных", e);
        }
    }

    // Получение автомобиля по ID
    public Optional<Car> getCarById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Car(
                            resultSet.getInt("id"),
                            resultSet.getString("make"),
                            resultSet.getString("model"),
                            resultSet.getInt("year"),
                            resultSet.getDouble("price"),
                            resultSet.getString("condition"),
                            resultSet.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {

            System.err.println("Ошибка при выполнении запроса на получение автомобиля: " + e.getMessage());
            throw new RuntimeException("Не удалось выполнить запрос на получение автомобиля", e);
        }
        return Optional.empty();
    }

    // Получение всех доступных автомобилей
    public List<Car> getAllAvailableCars() {
        String sql = "SELECT * FROM cars WHERE is_available = true";
        List<Car> cars = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                cars.add(new Car(
                        resultSet.getInt("id"),
                        resultSet.getString("make"),
                        resultSet.getString("model"),
                        resultSet.getInt("year"),
                        resultSet.getDouble("price"),
                        resultSet.getString("condition"),
                        resultSet.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении доступных автомобилей: " + e.getMessage());
            throw new RuntimeException("Не удалось получить доступные автомобили", e);
        }

        return cars;
    }

    // Обновление информации об автомобиле
    public boolean updateCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Объект Car не может быть null");
        }

        String sql = "UPDATE cars SET make = ?, model = ?, year = ?, price = ?, condition = ?, is_available = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, car.getMake());
            preparedStatement.setString(2, car.getModel());
            preparedStatement.setInt(3, car.getYear());
            preparedStatement.setDouble(4, car.getPrice());
            preparedStatement.setString(5, car.getCondition());
            preparedStatement.setBoolean(6, car.isAvailable());
            preparedStatement.setInt(7, car.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении автомобиля с ID " + car.getId() + ": " + e.getMessage());
            throw new RuntimeException("Не удалось обновить информацию об автомобиле", e);
        }
    }

    // Удаление автомобиля по ID
    public boolean removeCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {

            System.err.println("Ошибка при удалении автомобиля с ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Не удалось удалить автомобиль", e);
        }
    }

    // Поиск автомобилей по критериям
    public List<Car> searchCars(String make, String model, Integer year, Double minPrice, Double maxPrice, String condition) {

        StringBuilder sql = new StringBuilder("SELECT * FROM cars WHERE 1=1");
        if (make != null) sql.append(" AND make = ?");
        if (model != null) sql.append(" AND model = ?");
        if (year != null) sql.append(" AND year = ?");
        if (minPrice != null) sql.append(" AND price >= ?");
        if (maxPrice != null) sql.append(" AND price <= ?");
        if (condition != null) sql.append(" AND condition = ?");

        List<Car> cars = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {


            int index = 1;
            if (make != null) preparedStatement.setString(index++, make);
            if (model != null) preparedStatement.setString(index++, model);
            if (year != null) preparedStatement.setInt(index++, year);
            if (minPrice != null) preparedStatement.setDouble(index++, minPrice);
            if (maxPrice != null) preparedStatement.setDouble(index++, maxPrice);
            if (condition != null) preparedStatement.setString(index++, condition);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(new Car(
                            resultSet.getInt("id"),
                            resultSet.getString("make"),
                            resultSet.getString("model"),
                            resultSet.getInt("year"),
                            resultSet.getDouble("price"),
                            resultSet.getString("condition"),
                            resultSet.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {

            System.err.println("Ошибка при выполнении запроса поиска автомобилей: " + e.getMessage());
            throw new RuntimeException("Не удалось выполнить поиск автомобилей", e);
        }
        return cars;
    }

    // Получение автомобилей по диапазону цен
    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice) {
        String sql = "SELECT * FROM cars WHERE price BETWEEN ? AND ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, minPrice);
            preparedStatement.setDouble(2, maxPrice);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(new Car(
                            resultSet.getInt("id"),
                            resultSet.getString("make"),
                            resultSet.getString("model"),
                            resultSet.getInt("year"),
                            resultSet.getDouble("price"),
                            resultSet.getString("condition"),
                            resultSet.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {

            System.err.println("Ошибка при выполнении запроса по диапазону цен: " + e.getMessage());
            throw new RuntimeException("Не удалось получить автомобили по диапазону цен", e);
        }
        return cars;
    }

    // Получение автомобилей по диапазону годов
    public List<Car> getCarsByYearRange(int minYear, int maxYear) {
        String sql = "SELECT * FROM cars WHERE year BETWEEN ? AND ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Устанавливаем параметры запроса
            preparedStatement.setInt(1, minYear);
            preparedStatement.setInt(2, maxYear);

            // Выполняем запрос и обрабатываем результаты
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(new Car(
                            resultSet.getInt("id"),
                            resultSet.getString("make"),
                            resultSet.getString("model"),
                            resultSet.getInt("year"),
                            resultSet.getDouble("price"),
                            resultSet.getString("condition"),
                            resultSet.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при выполнении запроса по диапазону годов: " + e.getMessage());
            throw new RuntimeException("Не удалось получить автомобили по диапазону годов", e);
        }
        return cars;
    }
}