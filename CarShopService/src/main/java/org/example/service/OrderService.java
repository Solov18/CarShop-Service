package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервисный класс для управления заказами. Предоставляет методы для создания, получения,
 * обновления и отмены заказов, а также для поиска заказов по различным критериям.
 */
@AllArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CarRepository carRepository;

    /**
     * Создание нового заказа.
     *
     * @param orderDTO объект DTO заказа, который нужно создать.
     * @return объект DTO созданного заказа.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     * @throws RuntimeException если машина с указанным ID не найдена.
     */
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

    /**
     * Получение всех заказов.
     *
     * @return список DTO всех заказов.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getAllOrders() throws SQLException {
        List<Order> orders = orderRepository.getAllOrders();
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение заказа по его идентификатору.
     *
     * @param id идентификатор заказа.
     * @return объект DTO заказа.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public OrderDTO getOrderById(int id) throws SQLException {
        Order order = orderRepository.getOrderById(id);
        return Optional.ofNullable(order)
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .orElse(null);
    }

    /**
     * Обновление статуса заказа.
     *
     * @param id идентификатор заказа.
     * @param status новый статус заказа.
     * @return true, если статус успешно обновлен, иначе false.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public boolean updateOrderStatus(int id, String status) throws SQLException {
        return orderRepository.updateOrderStatus(id, status);
    }

    /**
     * Отмена заказа по его идентификатору.
     *
     * @param id идентификатор заказа.
     * @return true, если заказ успешно отменен, иначе false.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public boolean cancelOrder(int id) throws SQLException {
        return orderRepository.cancelOrder(id);
    }

    /**
     * Получение заказов по диапазону дат.
     *
     * @param startDateTime начальная дата и время.
     * @param endDateTime конечная дата и время.
     * @return список DTO заказов, находящихся в указанном диапазоне дат.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByDateRange(startDateTime, endDateTime);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение заказов клиента по его имени пользователя.
     *
     * @param clientUsername имя пользователя клиента.
     * @return список DTO заказов клиента.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByClient(String clientUsername) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByClient(clientUsername);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение заказов по их статусу.
     *
     * @param status статус заказа.
     * @return список DTO заказов с указанным статусом.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByStatus(String status) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByStatus(status);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение заказов по идентификатору автомобиля.
     *
     * @param carId идентификатор автомобиля.
     * @return список DTO заказов для указанного автомобиля.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByCar(int carId) throws SQLException {
        List<Order> orders = orderRepository.getOrdersByCar(carId);
        return orders.stream()
                .map(OrderMapper.INSTANCE::orderToOrderDTO)
                .collect(Collectors.toList());
    }
}
