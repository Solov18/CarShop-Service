package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDTO;
import org.example.exception.OrderNotFoundException;
import org.example.exception.InvalidOrderDataException;
import org.example.exception.DatabaseException;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Api(value = "Order API", tags = {"Orders"})
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ApiOperation(value = "Создать новый заказ", notes = "Создает новый заказ и возвращает его данные")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@ApiParam(value = "Данные нового заказа", required = true) @RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (DatabaseException e) {
            log.error("Ошибка при создании заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidOrderDataException e) {
            log.error("Ошибка в данных заказа", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
                        return ResponseEntity.badRequest().build();
                }
            }
        } catch (DatabaseException e) {
            log.error("Ошибка при получении заказов", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<List<OrderDTO>> handleGetById(Integer id) {
        try {

            OrderDTO orderDTO = orderService.getOrderById(id);
            return ResponseEntity.ok(List.of(orderDTO));
        } catch (OrderNotFoundException e) {
            log.error("Заказ с ID {} не найден", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении заказа с ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<List<OrderDTO>> handleGetByDateRange(String startDateTimeStr, String endDateTimeStr) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr);
            LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr);
            return ResponseEntity.ok(orderService.getOrdersByDateRange(startDateTime, endDateTime));
        } catch (DateTimeParseException e) {
            log.error("Ошибка при преобразовании даты", e);
            return ResponseEntity.badRequest().build();
        } catch (DatabaseException e) {
            log.error("Ошибка при получении заказов по диапазону дат", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Обновить статус заказа", notes = "Обновляет статус заказа по его ID")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrderStatus(@ApiParam(value = "ID заказа", required = true) @PathVariable int id,
                                                  @ApiParam(value = "Новый статус заказа", required = true) @RequestParam String status) {
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

    @ApiOperation(value = "Отменить заказ", notes = "Отменяет заказ по его ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@ApiParam(value = "ID заказа", required = true) @PathVariable int id) {
        try {
            boolean canceled = orderService.cancelOrder(id);
            return canceled ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (OrderNotFoundException e) {
            log.error("Заказ не найден", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DatabaseException e) {
            log.error("Ошибка при отмене заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
