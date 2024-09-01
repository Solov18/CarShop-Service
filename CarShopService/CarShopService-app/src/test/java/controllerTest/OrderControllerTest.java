package controllerTest;

import org.example.controller.OrderController;
import org.example.dto.OrderDTO;
import org.example.exception.DatabaseException;
import org.example.exception.InvalidOrderDataException;
import org.example.exception.OrderNotFoundException;
import org.example.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест для успешного создания заказа.
     * Ожидается, что контроллер вернет статус 201 Created и созданный объект заказа.
     */
    @Test
    void testCreateOrder_Success() throws Exception {
        // Мокируем создание заказа
        OrderDTO orderDTO = new OrderDTO(1, "Item 1", "New", 100);
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        // Выполняем запрос создания заказа
        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        // Проверяем ответ
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    /**
     * Тест для обработки ошибки базы данных при создании заказа.
     * Ожидается, что контроллер вернет статус 500 Internal Server Error.
     */
    @Test
    void testCreateOrder_DatabaseError() throws Exception {
        // Мокируем выброс DatabaseException
        when(orderService.createOrder(any(OrderDTO.class))).thenThrow(new Exception());

        // Выполняем запрос создания заказа
        ResponseEntity<OrderDTO> response = orderController.createOrder(new OrderDTO());

        // Проверяем ответ
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    /**
     * Тест для обработки ошибки данных при создании заказа.
     * Ожидается, что контроллер вернет статус 400 Bad Request.
     */
    @Test
    void testCreateOrder_InvalidDataError() throws Exception {
        // Мокируем выброс InvalidOrderDataException
        when(orderService.createOrder(any(OrderDTO.class))).thenThrow(new InvalidOrderDataException("Invalid order data"));

        // Выполняем запрос создания заказа
        ResponseEntity<OrderDTO> response = orderController.createOrder(new OrderDTO());

        // Проверяем ответ
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    /**
     * Тест для успешного получения заказа по идентификатору.
     * Ожидается, что контроллер вернет статус 200 OK и объект заказа.
     */
    @Test
    void testGetOrderById_Success() throws Exception {
        // Мокируем получение заказа
        OrderDTO orderDTO = new OrderDTO(1, "Item 1", "New", 100);
        when(orderService.getOrderById(1)).thenReturn(orderDTO);

        // Выполняем запрос получения заказа
        ResponseEntity<OrderDTO> response = orderController.getOrderById(1);

        // Проверяем ответ
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    /**
     * Тест для обработки случая, когда заказ не найден.
     * Ожидается, что контроллер вернет статус 404 Not Found.
     */
    @Test
    void testGetOrderById_NotFound() throws Exception {
        // Мокируем выброс OrderNotFoundException
        when(orderService.getOrderById(1)).thenThrow(new OrderNotFoundException("Order not found"));

        // Выполняем запрос получения заказа
        ResponseEntity<OrderDTO> response = orderController.getOrderById(1);

        // Проверяем ответ
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    /**
     * Тест для обработки ошибки базы данных при получении заказа.
     * Ожидается, что контроллер вернет статус 500 Internal Server Error.
     */
    @Test
    void testGetOrderById_DatabaseError() throws Exception {
        // Мокируем выброс DatabaseException
        when(orderService.getOrderById(1)).thenThrow(new Exception("Database error"));

        // Выполняем запрос получения заказа
        ResponseEntity<OrderDTO> response = orderController.getOrderById(1);

        // Проверяем ответ
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    /**
     * Тест для успешного обновления статуса заказа.
     * Ожидается, что контроллер вернет статус 204 No Content.
     */
    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        // Мокируем успешное обновление
        when(orderService.updateOrderStatus(1, "Shipped")).thenReturn(true);

        // Выполняем запрос обновления статуса заказа
        ResponseEntity<Void> response = orderController.updateOrderStatus(1, "Shipped");

        // Проверяем ответ
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    /**
     * Тест для обработки случая, когда заказ не найден при обновлении статуса.
     * Ожидается, что контроллер вернет статус 404 Not Found.
     */
    @Test
    void testUpdateOrderStatus_NotFound() throws Exception {
        // Мокируем выброс OrderNotFoundException
        when(orderService.updateOrderStatus(1, "Shipped")).thenThrow(new OrderNotFoundException("Order not found"));

        // Выполняем запрос обновления статуса заказа
        ResponseEntity<Void> response = orderController.updateOrderStatus(1, "Shipped");

        // Проверяем ответ
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    /**
     * Тест для обработки ошибки базы данных при обновлении статуса заказа.
     * Ожидается, что контроллер вернет статус 500 Internal Server Error.
     */
    @Test
    void testUpdateOrderStatus_DatabaseError() throws Exception {
        // Мокируем выброс DatabaseException
        when(orderService.updateOrderStatus(1, "Shipped")).thenThrow(new Exception("Database error"));

        // Выполняем запрос обновления статуса заказа
        ResponseEntity<Void> response = orderController.updateOrderStatus(1, "Shipped");

        // Проверяем ответ
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
