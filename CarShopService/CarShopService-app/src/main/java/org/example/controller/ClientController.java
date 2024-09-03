package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ClientDTO;
import org.example.exception.ClientNotFoundException;
import org.example.exception.InvalidParameterException;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Контроллер для управления клиентами.
 * Предоставляет API для получения списка клиентов с возможностью фильтрации и сортировки,
 * а также обработки ошибок, связанных с клиентами и параметрами запроса.
 */
@RestController
@RequestMapping("/api/clients")
@Slf4j
@Tag(name = "Clients", description = "API для управления клиентами")
public class ClientController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClientController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    /**
     * Получение списка клиентов с возможностью фильтрации и сортировки.
     *
     * Действия, которые могут быть выполнены:
     * - Фильтрация по имени клиента
     * - Фильтрация по контактной информации клиента
     * - Сортировка по имени
     * - Фильтрация по количеству заказов
     * - Сортировка по количеству заказов
     *
     * @param action Тип действия (фильтрация/сортировка)
     * @param name Имя клиента (требуется при фильтрации по имени)
     * @param contactInfo Контактная информация клиента (требуется при фильтрации по контактной информации)
     * @param minOrders Минимальное количество заказов (требуется при фильтрации по количеству заказов)
     * @param maxOrders Максимальное количество заказов (требуется при фильтрации по количеству заказов)
     * @return Список клиентов, соответствующих указанным параметрам, или пустой список при ошибках
     */
    @Operation(summary = "Получить список клиентов", description = "Получить список клиентов с возможностью фильтрации и сортировки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список клиентов успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Клиенты не найдены"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getClients(
            @Parameter(description = "Тип действия (фильтрация/сортировка)") @RequestParam(value = "action", required = false) String action,
            @Parameter(description = "Имя клиента") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "Контактная информация клиента") @RequestParam(value = "contactInfo", required = false) String contactInfo,
            @Parameter(description = "Минимальное количество заказов") @RequestParam(value = "minOrders", required = false) Integer minOrders,
            @Parameter(description = "Максимальное количество заказов") @RequestParam(value = "maxOrders", required = false) Integer maxOrders) {

        try {
            log.info("Получение клиентов с параметрами: action={}, name={}, contactInfo={}, minOrders={}, maxOrders={}",
                    action, name, contactInfo, minOrders, maxOrders);

            if (Objects.isNull(action)) {
                List<ClientDTO> clientDTOs = userService.getAllClients();
                return ResponseEntity.ok(clientDTOs);
            } else {
                switch (action) {
                    case "filterByName":
                        if (Objects.nonNull(name)) {
                            log.info("Фильтрация клиентов по имени: {}", name);
                            List<ClientDTO> filteredClientsByName = userService.filterClientsByName(name);
                            return ResponseEntity.ok(filteredClientsByName);
                        } else {
                            log.warn("Не указано имя для фильтрации");
                            throw new InvalidParameterException("Имя не указано");
                        }
                    case "filterByContactInfo":
                        if (Objects.nonNull(contactInfo)) {
                            log.info("Фильтрация клиентов по контактной информации: {}", contactInfo);
                            List<ClientDTO> filteredClientsByContactInfo = userService.filterClientsByContactInfo(contactInfo);
                            return ResponseEntity.ok(filteredClientsByContactInfo);
                        } else {
                            log.warn("Не указана контактная информация для фильтрации");
                            throw new InvalidParameterException("Контактная информация не указана");
                        }
                    case "sortByName":
                        log.info("Сортировка клиентов по имени");
                        List<ClientDTO> sortedClientsByName = userService.sortClientsByName();
                        return ResponseEntity.ok(sortedClientsByName);
                    case "filterByOrders":
                        if (minOrders != null && maxOrders != null) {
                            log.info("Фильтрация клиентов по количеству заказов: minOrders={}, maxOrders={}", minOrders, maxOrders);
                            List<ClientDTO> filteredClientsByOrders = userService.filterClientsByOrders(minOrders, maxOrders);
                            return ResponseEntity.ok(filteredClientsByOrders);
                        } else {
                            log.warn("Некорректные параметры для фильтрации по количеству заказов");
                            throw new InvalidParameterException("Неверный формат чисел для заказов");
                        }
                    case "sortByOrders":
                        log.info("Сортировка клиентов по количеству заказов");
                        List<ClientDTO> sortedClientsByOrders = userService.sortClientsByOrders();
                        return ResponseEntity.ok(sortedClientsByOrders);
                    default:
                        log.warn("Неверное действие: {}", action);
                        throw new InvalidParameterException("Неверное действие");
                }
            }
        } catch (ClientNotFoundException e) {
            log.error("Клиенты не найдены: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        } catch (InvalidParameterException e) {
            log.error("Некорректные параметры: {}", e.getMessage());
            return ResponseEntity.badRequest().body(List.of());
        } catch (SQLException e) {
            log.error("Ошибка работы с базой данных: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        } catch (Exception e) {
            log.error("Неизвестная ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
}
