package org.example.controller;

import org.example.model.Car;
import org.example.model.Client;
import org.example.model.Order;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class OrderController {
    private List<Order> orders = new ArrayList<>();
    private UserController userController;

    // Конструктор для инициализации UserController
    public OrderController(UserController userController) {
        this.userController = userController;
    }

    // Создание нового заказа
    public Order createOrder(Car car, Client client) {

        Order order = new Order(car, client);
        car.setAvailable(false);
        orders.add(order);
        userController.increaseOrderCount(client.getUsername());
        return order;
    }

    // Получение всех заказов
    public List<Order> getAllOrders() {
        return orders;
    }

    // Получение заказа по ID
    public Order getOrderById(int id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    // Изменение статуса заказа
    public boolean updateOrderStatus(int id, String status) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
            return true;
        }
        return false;
    }

    // Отмена заказа
    public boolean cancelOrder(int id) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus("cancelled");
            order.getCar().setAvailable(true);
            return true;
        }
        return false;
    }

    // Получение заказов по диапазону дат
    public List<Order> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orders.stream()
                .filter(order -> order.getDate() != null &&
                        (order.getDate().isAfter(startDateTime) || order.getDate().isEqual(startDateTime)) &&
                        (order.getDate().isBefore(endDateTime) || order.getDate().isEqual(endDateTime)))
                .collect(Collectors.toList());
    }

    // Получение заказов по клиенту
    public List<Order> getOrdersByClient(String clientUsername) {
        return orders.stream()
                .filter(order -> order.getClient().getUsername().equalsIgnoreCase(clientUsername))
                .collect(Collectors.toList());
    }

    // Получение заказов по статусу
    public List<Order> getOrdersByStatus(String status) {
        return orders.stream()
                .filter(order -> order.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    // Получение заказов по машине
    public List<Order> getOrdersByCar(int carId) {
        return orders.stream()
                .filter(order -> order.getCar().getId() == carId)
                .collect(Collectors.toList());
    }
}