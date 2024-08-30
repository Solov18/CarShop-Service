package org.example.repository;

import org.example.model.Car;
import org.example.model.Client;
import org.example.model.Order;
import org.example.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CarRepository carRepository;
    private final UserRepository userRepository;


    private static final String INSERT_ORDER_SQL = "INSERT INTO orders (car_id, client_username, status, date) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ORDER_BY_ID_SQL = "SELECT * FROM orders WHERE id = ?";
    private static final String SELECT_ALL_ORDERS_SQL = "SELECT * FROM orders";
    private static final String SELECT_ORDERS_BY_CLIENT_SQL = "SELECT * FROM orders WHERE client_username = ?";
    private static final String UPDATE_ORDER_STATUS_SQL = "UPDATE orders SET status = ? WHERE id = ?";
    private static final String SELECT_ORDERS_BY_DATE_RANGE_SQL = "SELECT * FROM orders WHERE date BETWEEN ? AND ?";
    private static final String SELECT_ORDERS_BY_STATUS_SQL = "SELECT * FROM orders WHERE status = ?";
    private static final String SELECT_ORDERS_BY_CAR_SQL = "SELECT * FROM orders WHERE car_id = ?";

    public OrderRepository(JdbcTemplate jdbcTemplate, CarRepository carRepository, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addOrder(Order order) {
        jdbcTemplate.update(INSERT_ORDER_SQL, order.getCar().getId(), order.getClient().getUsername(), order.getStatus(), order.getDate());
    }

    public Order getOrderById(int id) {
        return jdbcTemplate.queryForObject(SELECT_ORDER_BY_ID_SQL, new Object[]{id}, new OrderRowMapper());
    }

    public List<Order> getAllOrders() {
        return jdbcTemplate.query(SELECT_ALL_ORDERS_SQL, new OrderRowMapper());
    }

    public List<Order> getOrdersByClient(String clientUsername) {
        return jdbcTemplate.query(SELECT_ORDERS_BY_CLIENT_SQL, new Object[]{clientUsername}, new OrderRowMapper());
    }

    public boolean updateOrderStatus(int id, String status) {
        return jdbcTemplate.update(UPDATE_ORDER_STATUS_SQL, status, id) > 0;
    }

    public boolean cancelOrder(int id) {
        return updateOrderStatus(id, "cancelled");
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jdbcTemplate.query(SELECT_ORDERS_BY_DATE_RANGE_SQL, new Object[]{startDateTime, endDateTime}, new OrderRowMapper());
    }

    public List<Order> getOrdersByStatus(String status) {
        return jdbcTemplate.query(SELECT_ORDERS_BY_STATUS_SQL, new Object[]{status}, new OrderRowMapper());
    }

    public List<Order> getOrdersByCar(int carId) {
        return jdbcTemplate.query(SELECT_ORDERS_BY_CAR_SQL, new Object[]{carId}, new OrderRowMapper());
    }

    private class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            int carId = rs.getInt("car_id");
            String clientUsername = rs.getString("client_username");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            String status = rs.getString("status");

            Car car = carRepository.getCarById(carId)
                    .orElseThrow(() -> new RuntimeException("Car with ID " + carId + " not found"));

            User user = userRepository.getUserByUsername(clientUsername);
            if (!(user instanceof Client)) {
                throw new RuntimeException("User with username " + clientUsername + " is not a client");
            }
            Client client = (Client) user;

            return new Order(id, car, client, status, date);
        }
    }
}
