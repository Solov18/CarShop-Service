package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDTO;
import org.example.exception.DatabaseException;
import org.example.exception.InvalidOrderDataException;
import org.example.exception.OrderNotFoundException;
import org.example.mapper.OrderMapper;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@Tag(name = "Orders", description = "API для управления заказами")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    /**
     * Создает новый заказ.
     *
     * @param orderDTO объект передачи данных заказа, содержащий информацию о заказе.
     * @return ResponseEntity с созданным объектом заказа и HTTP-статусом 201 Created, если заказ успешно создан.
     *         В случае ошибки данных возвращает HTTP-статус 400 Bad Request.
     *         В случае ошибки базы данных возвращает HTTP-статус 500 Internal Server Error.
     */
    @Operation(summary = "Создает новый заказ", description = "Создает новый заказ и возвращает созданный объект заказа.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных заказа"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Parameter(description = "Объект передачи данных заказа, содержащий информацию о заказе.")
            @RequestBody OrderDTO orderDTO) {
        try {
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

    /**
     * Получает информацию о заказе по его идентификатору.
     *
     * @param id идентификатор заказа.
     * @return ResponseEntity с объектом заказа и HTTP-статусом 200 OK, если заказ найден.
     *         В случае, если заказ не найден, возвращает HTTP-статус 404 Not Found.
     *         В случае ошибки базы данных возвращает HTTP-статус 500 Internal Server Error.
     */
    @Operation(summary = "Получает информацию о заказе по его идентификатору",
            description = "Получает информацию о заказе по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о заказе успешно возвращена"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Идентификатор заказа.") @PathVariable int id) {
        try {
            OrderDTO orderDTO = orderService.getOrderById(id);
            return ResponseEntity.ok(orderDTO);
        } catch (OrderNotFoundException e) {
            log.error("Заказ не найден", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении заказа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Обновляет статус заказа по его идентификатору.
     *
     * @param id идентификатор заказа.
     * @param status новый статус заказа.
     * @return ResponseEntity с HTTP-статусом 204 No Content, если статус успешно обновлен.
     *         В случае, если заказ не найден, возвращает HTTP-статус 404 Not Found.
     *         В случае ошибки базы данных возвращает HTTP-статус 500 Internal Server Error.
     */
    @Operation(summary = "Обновляет статус заказа по его идентификатору",
            description = "Обновляет статус заказа по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Статус заказа успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "Идентификатор заказа.") @PathVariable int id,
            @Parameter(description = "Новый статус заказа.") @RequestParam String status) {
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
