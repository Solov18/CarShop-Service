package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ClientDTO;
import org.example.dto.OrderDTO;
import org.example.exception.OrderNotFoundException;
import org.example.exception.InvalidOrderDataException;
import org.example.exception.DatabaseException;
import org.example.mapper.OrderMapper;
import org.example.mapper.UserMapper;
import org.example.model.Car;
import org.example.model.Client;
import org.example.model.Order;
import org.example.repository.CarRepository;
import org.example.repository.OrderRepository;

import java.time.LocalDateTime;
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
     * @throws InvalidOrderDataException если машина с указанным ID не найдена.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            Optional<Car> optionalCar = carRepository.getCarById(orderDTO.getCarId());
            if (optionalCar.isEmpty()) {
                throw new InvalidOrderDataException("Машина с ID " + orderDTO.getCarId() + " не найдена");
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
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при создании заказа", e);
            throw new DatabaseException("Ошибка при создании заказа", e);
        }
    }

    /**
     * Получение всех заказов.
     *
     * @return список DTO всех заказов.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getAllOrders() {
        try {
            List<Order> orders = orderRepository.getAllOrders();
            return orders.stream()
                    .map(OrderMapper.INSTANCE::orderToOrderDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при получении всех заказов", e);
            throw new DatabaseException("Ошибка при получении всех заказов", e);
        }
    }

    /**
     * Получение заказа по его идентификатору.
     *
     * @param id идентификатор заказа.
     * @return объект DTO заказа.
     * @throws OrderNotFoundException если заказ не найден.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public OrderDTO getOrderById(int id) {
        try {
            Order order = orderRepository.getOrderById(id);
            return Optional.ofNullable(order)
                    .map(OrderMapper.INSTANCE::orderToOrderDTO)
                    .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + id + " не найден"));
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при получении заказа с ID " + id, e);
            throw new DatabaseException("Ошибка при получении заказа с ID " + id, e);
        }
    }

    /**
     * Обновление статуса заказа.
     *
     * @param id идентификатор заказа.
     * @param status новый статус заказа.
     * @return true, если статус успешно обновлен, иначе false.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public boolean updateOrderStatus(int id, String status) {
        try {
            return orderRepository.updateOrderStatus(id, status);
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при обновлении статуса заказа с ID " + id, e);
            throw new DatabaseException("Ошибка при обновлении статуса заказа с ID " + id, e);
        }
    }

    /**
     * Отмена заказа по его идентификатору.
     *
     * @param id идентификатор заказа.
     * @return true, если заказ успешно отменен, иначе false.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public boolean cancelOrder(int id) {
        try {
            return orderRepository.cancelOrder(id);
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при отмене заказа с ID " + id, e);
            throw new DatabaseException("Ошибка при отмене заказа с ID " + id, e);
        }
    }

    /**
     * Получение заказов по диапазону дат.
     *
     * @param startDateTime начальная дата и время.
     * @param endDateTime конечная дата и время.
     * @return список DTO заказов, находящихся в указанном диапазоне дат.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            List<Order> orders = orderRepository.getOrdersByDateRange(startDateTime, endDateTime);
            return orders.stream()
                    .map(OrderMapper.INSTANCE::orderToOrderDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при получении заказов по диапазону дат", e);
            throw new DatabaseException("Ошибка при получении заказов по диапазону дат", e);
        }
    }

    /**
     * Получение заказов клиента по его имени пользователя.
     *
     * @param clientUsername имя пользователя клиента.
     * @return список DTO заказов клиента.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByClient(String clientUsername) {
        try {
            List<Order> orders = orderRepository.getOrdersByClient(clientUsername);
            return orders.stream()
                    .map(OrderMapper.INSTANCE::orderToOrderDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при получении заказов клиента", e);
            throw new DatabaseException("Ошибка при получении заказов клиента", e);
        }
    }

    /**
     * Получение заказов по их статусу.
     *
     * @param status статус заказа.
     * @return список DTO заказов с указанным статусом.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByStatus(String status) {
        try {
            List<Order> orders = orderRepository.getOrdersByStatus(status);
            return orders.stream()
                    .map(OrderMapper.INSTANCE::orderToOrderDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при получении заказов по статусу", e);
            throw new DatabaseException("Ошибка при получении заказов по статусу", e);
        }
    }

    /**
     * Получение заказов по идентификатору автомобиля.
     *
     * @param carId идентификатор автомобиля.
     * @return список DTO заказов для указанного автомобиля.
     * @throws DatabaseException если произошла ошибка при работе с базой данных.
     */
    public List<OrderDTO> getOrdersByCar(int carId) {
        try {
            List<Order> orders = orderRepository.getOrdersByCar(carId);
            return orders.stream()
                    .map(OrderMapper.INSTANCE::orderToOrderDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {  // Обрабатываем любые ошибки
            log.error("Ошибка при получении заказов по автомобилю", e);
            throw new DatabaseException("Ошибка при получении заказов по автомобилю", e);
        }
    }
}
