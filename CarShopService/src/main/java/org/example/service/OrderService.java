package org.example.service;

import lombok.AllArgsConstructor;
import org.example.model.Car;
import org.example.model.Client;
import org.example.model.Order;
import org.example.repository.CarRepository;
import org.example.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.SQLException;


@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CarRepository carRepository;


    // Создание нового заказа
    public Order createOrder(Car car, Client client) throws SQLException {
        Order order = new Order(car, client);
        orderRepository.addOrder(order);
        car.setAvailable(false);
        carRepository.updateCar(car);
        userService.increaseOrderCount(client.getUsername());
        return order;
    }

    // Получение всех заказов /
    public List<Order> getAllOrders() throws SQLException {
        return orderRepository.getAllOrders();
    }

    // Получение заказа по ID
    public Order getOrderById(int id) throws SQLException {
        return orderRepository.getOrderById(id);
    }

    // Изменение статуса заказа
    public boolean updateOrderStatus(int id, String status) throws SQLException {
        return orderRepository.updateOrderStatus(id, status);
    }

    // Отмена заказа
    public boolean cancelOrder(int id) throws SQLException {
        return orderRepository.cancelOrder(id);
    }

    // Получение заказов по диапазону дат
    public List<Order> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        return orderRepository.getOrdersByDateRange(startDateTime, endDateTime);
    }

    // Получение заказов по клиенту
    public List<Order> getOrdersByClient(String clientUsername) throws SQLException {
        return orderRepository.getOrdersByClient(clientUsername);
    }

    // Получение заказов по статусу
    public List<Order> getOrdersByStatus(String status) throws SQLException {
        return orderRepository.getOrdersByStatus(status);
    }

    // Получение заказов по машине
    public List<Order> getOrdersByCar(int carId) throws SQLException {
        return orderRepository.getOrdersByCar(carId);
    }
}