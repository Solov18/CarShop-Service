package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.ClientDTO;
import org.example.dto.OrderDTO;
import org.example.mapper.OrderMapper;
import org.example.mapper.UserMapper;
import org.example.model.Car;
import org.example.model.Client;
import org.example.model.Order;
import org.example.repository.CarRepository;
import org.example.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CarRepository carRepository;


    public OrderDTO createOrder(OrderDTO orderDTO) throws SQLException {
        Optional<Car> optionalCar = carRepository.getCarById(orderDTO.getCarId());
        if (optionalCar.isEmpty()) {
            throw new RuntimeException("Машина с ID " + orderDTO.getCarId() + " не найдена");
        }
        Car car = optionalCar.get();


        ClientDTO clientDTO = userService.getClientByUsername(orderDTO.getClientUsername());


        Client client = UserMapper.INSTANCE.clientDTOToClient(clientDTO);


        Order order = new Order(car, client);
        orderRepository.addOrder(order);
        car.setAvailable(false);
        carRepository.updateCar(car);
        userService.increaseOrderCount(client.getUsername());

        return OrderMapper.INSTANCE.orderToOrderDTO(order);
    }


    public List<OrderDTO> getAllOrders() throws SQLException {
        List<Order> orders = orderRepository.getAllOrders();
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }


    public OrderDTO getOrderById(int id) throws SQLException {
        Order order = orderRepository.getOrderById(id);
        return OrderMapper.INSTANCE.orderToOrderDTO(order);
    }


    public boolean updateOrderStatus(int id, String status) throws SQLException {
        return orderRepository.updateOrderStatus(id, status);
    }


    public boolean cancelOrder(int id) throws SQLException {
        return orderRepository.cancelOrder(id);
    }


    public List<OrderDTO> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByDateRange(startDateTime, endDateTime);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }


    public List<OrderDTO> getOrdersByClient(String clientUsername) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByClient(clientUsername);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }


    public List<OrderDTO> getOrdersByStatus(String status) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByStatus(status);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }


    public List<OrderDTO> getOrdersByCar(int carId) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByCar(carId);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }
}