package org.example.repository;


import org.example.config.DatabaseConnectionManager;
import org.example.model.Car;
import org.example.model.Client;
import org.example.model.Order;
import org.example.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с заказами в базе данных.
 */
public class OrderRepository {
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final DatabaseConnectionManager dbConnectionManager;

    /**
     * Конструктор, принимающий зависимости.
     *
     * @param carRepository Репозиторий для работы с автомобилями.
     * @param userRepository Репозиторий для работы с пользователями.
     * @param dbConnectionManager Менеджер соединений с базой данных.
     */
    public OrderRepository(CarRepository carRepository, UserRepository userRepository, DatabaseConnectionManager dbConnectionManager) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.dbConnectionManager = dbConnectionManager;
    }

    /**
     * Получение соединения с базой данных.
     *
     * @return Соединение с базой данных.
     * @throws SQLException Если не удалось получить соединение.
     */
    private Connection getConnection() throws SQLException {
        return dbConnectionManager.getConnection();
    }

    /**
     * Добавление нового заказа в базу данных.
     *
     * @param order Заказ для добавления.
     */
    public void addOrder(Order order) {
        String sql = "INSERT INTO orders (car_id, client_username, status, date) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, order.getCar().getId());
            preparedStatement.setString(2, order.getClient().getUsername());
            preparedStatement.setString(3, order.getStatus());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(order.getDate()));
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);
                order.setId(orderId);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении заказа: " + e.getMessage());
            throw new RuntimeException("Не удалось добавить заказ в базу данных", e);
        }
    }

    /**
     * Получение заказа по его идентификатору.
     *
     * @param id Идентификатор заказа.
     * @return Заказ с указанным идентификатором.
     * @throws RuntimeException Если заказ с указанным идентификатором не найден или возникла ошибка.
     */
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int carId = resultSet.getInt("car_id");
                String clientUsername = resultSet.getString("client_username");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");

                Car car = carRepository.getCarById(carId)
                        .orElseThrow(() -> new RuntimeException("Автомобиль с ID " + carId + " не найден"));

                User user = userRepository.getUserByUsername(clientUsername);
                if (!(user instanceof Client)) {
                    throw new RuntimeException("Пользователь с username " + clientUsername + " не является клиентом");
                }
                Client client = (Client) user;

                return new Order(id, car, client, status, date);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении заказа по ID: " + e.getMessage());
            throw new RuntimeException("Не удалось получить заказ из базы данных", e);
        }
        return null;
    }

    /**
     * Получение всех заказов из базы данных.
     *
     * @return Список всех заказов.
     * @throws RuntimeException Если возникла ошибка при получении заказов.
     */
    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int carId = resultSet.getInt("car_id");
                String clientUsername = resultSet.getString("client_username");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");
                Car car = carRepository.getCarById(carId)
                        .orElseThrow(() -> new RuntimeException("Автомобиль с ID " + carId + " не найден"));
                User user = userRepository.getUserByUsername(clientUsername);
                if (!(user instanceof Client)) {
                    throw new RuntimeException("Пользователь с username " + clientUsername + " не является клиентом");
                }
                Client client = (Client) user;

                orders.add(new Order(id, car, client, status, date));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении всех заказов: " + e.getMessage());
            throw new RuntimeException("Не удалось получить заказы из базы данных", e);
        }
        return orders;
    }

    /**
     * Получение заказов по username клиента.
     *
     * @param clientUsername Username клиента.
     * @return Список заказов клиента.
     * @throws RuntimeException Если возникла ошибка при получении заказов.
     */
    public List<Order> getOrdersByClient(String clientUsername) {
        String sql = "SELECT * FROM orders WHERE client_username = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, clientUsername);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int carId = resultSet.getInt("car_id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");
                Car car = carRepository.getCarById(carId)
                        .orElseThrow(() -> new RuntimeException("Автомобиль с ID " + carId + " не найден"));
                User user = userRepository.getUserByUsername(clientUsername);
                if (!(user instanceof Client)) {
                    throw new RuntimeException("Пользователь с username " + clientUsername + " не является клиентом");
                }
                Client client = (Client) user;

                orders.add(new Order(id, car, client, status, date));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении заказов по клиенту: " + e.getMessage());
            throw new RuntimeException("Не удалось получить заказы по клиенту из базы данных", e);
        }
        return orders;
    }

    /**
     * Обновление статуса заказа.
     *
     * @param id Идентификатор заказа.
     * @param status Новый статус заказа.
     * @return true, если статус был обновлен успешно, иначе false.
     */
    public boolean updateOrderStatus(int id, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении статуса заказа: " + e.getMessage());
            throw new RuntimeException("Не удалось обновить статус заказа в базе данных", e);
        }
    }

    /**
     * Отмена заказа (установка статуса 'cancelled').
     *
     * @param id Идентификатор заказа.
     * @return true, если заказ был отменен успешно, иначе false.
     */
    public boolean cancelOrder(int id) {
        return updateOrderStatus(id, "cancelled");
    }

    /**
     * Получение заказов по диапазону дат.
     *
     * @param startDateTime Начальная дата и время.
     * @param endDateTime Конечная дата и время.
     * @return Список заказов, сделанных в указанный диапазон дат.
     * @throws RuntimeException Если возникла ошибка при получении заказов.
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        String sql = "SELECT * FROM orders WHERE date BETWEEN ? AND ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDateTime));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDateTime));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int carId = resultSet.getInt("car_id");
                String clientUsername = resultSet.getString("client_username");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");
                Car car = carRepository.getCarById(carId)
                        .orElseThrow(() -> new RuntimeException("Автомобиль с ID " + carId + " не найден"));
                User user = userRepository.getUserByUsername(clientUsername);
                if (!(user instanceof Client)) {
                    throw new RuntimeException("Пользователь с username " + clientUsername + " не является клиентом");
                }
                Client client = (Client) user;

                orders.add(new Order(id, car, client, status, date));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении заказов по диапазону дат: " + e.getMessage());
            throw new RuntimeException("Не удалось получить заказы по диапазону дат из базы данных", e);
        }
        return orders;
    }

    /**
     * Получение заказов по статусу.
     *
     * @param status Статус заказа.
     * @return Список заказов с указанным статусом.
     * @throws RuntimeException Если возникла ошибка при получении заказов.
     */
    public List<Order> getOrdersByStatus(String status) {
        String sql = "SELECT * FROM orders WHERE status = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int carId = resultSet.getInt("car_id");
                String clientUsername = resultSet.getString("client_username");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Car car = carRepository.getCarById(carId)
                        .orElseThrow(() -> new RuntimeException("Автомобиль с ID " + carId + " не найден"));
                User user = userRepository.getUserByUsername(clientUsername);
                if (!(user instanceof Client)) {
                    throw new RuntimeException("Пользователь с username " + clientUsername + " не является клиентом");
                }
                Client client = (Client) user;

                orders.add(new Order(id, car, client, status, date));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении заказов по статусу: " + e.getMessage());
            throw new RuntimeException("Не удалось получить заказы по статусу из базы данных", e);
        }
        return orders;
    }

    /**
     * Получение заказов по идентификатору автомобиля.
     *
     * @param carId Идентификатор автомобиля.
     * @return Список заказов для указанного автомобиля.
     * @throws RuntimeException Если возникла ошибка при получении заказов.
     */
    public List<Order> getOrdersByCar(int carId) {
        String sql = "SELECT * FROM orders WHERE car_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String clientUsername = resultSet.getString("client_username");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");
                Car car = carRepository.getCarById(carId)
                        .orElseThrow(() -> new RuntimeException("Автомобиль с ID " + carId + " не найден"));
                User user = userRepository.getUserByUsername(clientUsername);
                if (!(user instanceof Client)) {
                    throw new RuntimeException("Пользователь с username " + clientUsername + " не является клиентом");
                }
                Client client = (Client) user;

                orders.add(new Order(id, car, client, status, date));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении заказов по ID автомобиля: " + e.getMessage());
            throw new RuntimeException("Не удалось получить заказы по ID автомобиля из базы данных", e);
        }
        return orders;
    }
}