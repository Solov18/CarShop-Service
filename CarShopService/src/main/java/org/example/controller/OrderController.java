package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDTO;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Api(value = "Order API", tags = {"Orders"})
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderController(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @ApiOperation(value = "Создать новый заказ", notes = "Создает новый заказ и возвращает его данные")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@ApiParam(value = "Данные нового заказа", required = true) @RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (SQLException e) {
            log.error("Ошибка при создании заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            log.error("Ошибка при создании заказа", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @ApiOperation(value = "Получить список заказов", notes = "Возвращает список заказов с возможностью фильтрации")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(
            @ApiParam(value = "Тип действия (фильтрация по различным критериям)") @RequestParam(value = "action", required = false) String action,
            @ApiParam(value = "ID заказа") @RequestParam(value = "id", required = false) Integer id,
            @ApiParam(value = "Начальная дата и время (ISO 8601)") @RequestParam(value = "startDateTime", required = false) String startDateTimeStr,
            @ApiParam(value = "Конечная дата и время (ISO 8601)") @RequestParam(value = "endDateTime", required = false) String endDateTimeStr,
            @ApiParam(value = "Имя клиента") @RequestParam(value = "clientUsername", required = false) String clientUsername,
            @ApiParam(value = "Статус заказа") @RequestParam(value = "status", required = false) String status,
            @ApiParam(value = "ID автомобиля") @RequestParam(value = "carId", required = false) Integer carId) {

        try {
            if (action == null) {
                return ResponseEntity.ok(orderService.getAllOrders());
            } else {
                switch (action) {
                    case "byId":
                        return handleGetById(id);
                    case "byDateRange":
                        return handleGetByDateRange(startDateTimeStr, endDateTimeStr);
                    case "byClient":
                        return ResponseEntity.ok(orderService.getOrdersByClient(clientUsername));
                    case "byStatus":
                        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
                    case "byCar":
                        return ResponseEntity.ok(orderService.getOrdersByCar(carId));
                    default:
                        return ResponseEntity.badRequest().body(null);
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении заказов", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private ResponseEntity<List<OrderDTO>> handleGetById(Integer id) throws SQLException {
        OrderDTO order = orderService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(List.of(order));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private ResponseEntity<List<OrderDTO>> handleGetByDateRange(String startDateTimeStr, String endDateTimeStr) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr);
            LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr);
            return ResponseEntity.ok(orderService.getOrdersByDateRange(startDateTime, endDateTime));
        } catch (DateTimeParseException e) {
            log.error("Ошибка при преобразовании даты", e);
            return ResponseEntity.badRequest().body(null);
        } catch (SQLException e) {
            log.error("Ошибка при получении заказов по диапазону дат", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ApiOperation(value = "Обновить статус заказа", notes = "Обновляет статус заказа по его ID")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrderStatus(@ApiParam(value = "ID заказа", required = true) @PathVariable int id,
                                                  @ApiParam(value = "Новый статус заказа", required = true) @RequestParam String status) {
        try {
            boolean updated = orderService.updateOrderStatus(id, status);
            return updated ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SQLException e) {
            log.error("Ошибка при обновлении статуса заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Отменить заказ", notes = "Отменяет заказ по его ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@ApiParam(value = "ID заказа", required = true) @PathVariable int id) {
        try {
            boolean canceled = orderService.cancelOrder(id);
            return canceled ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SQLException e) {
            log.error("Ошибка при отмене заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
