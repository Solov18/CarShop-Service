package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDTO;
import org.example.exception.DatabaseException;
import org.example.exception.InvalidOrderDataException;
import org.example.exception.OrderNotFoundException;
import org.example.mapper.OrderMapper;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Предположим, что OrderService теперь использует OrderMapper
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;  // Добавляем маппер

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            // Логика создания заказа через сервис (сервис работает с DTO)
            OrderDTO createdOrderDTO = orderService.createOrder(orderDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderDTO);
        } catch (DatabaseException e) {
            log.error("Ошибка при создании заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidOrderDataException e) {
            log.error("Ошибка в данных заказа", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable int id) {
        try {
            // Сразу получаем OrderDTO из сервиса
            OrderDTO orderDTO = orderService.getOrderById(id);

            // Возвращаем OrderDTO
            return ResponseEntity.ok(orderDTO);
        } catch (OrderNotFoundException e) {
            log.error("Заказ не найден", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable int id, @RequestParam String status) {
        try {
            boolean updated = orderService.updateOrderStatus(id, status);
            return updated ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (OrderNotFoundException e) {
            log.error("Заказ не найден", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DatabaseException e) {
            log.error("Ошибка при обновлении статуса заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
