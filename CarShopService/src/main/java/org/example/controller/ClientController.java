package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@Api(value = "Client API", tags = {"Clients"})
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final UserService userService;

    @Autowired
    public ClientController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Получить список клиентов", notes = "Получить список клиентов с возможностью фильтрации и сортировки")
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getClients(
            @ApiParam(value = "Тип действия (фильтрация/сортировка)") @RequestParam(value = "action", required = false) String action,
            @ApiParam(value = "Имя клиента") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "Контактная информация клиента") @RequestParam(value = "contactInfo", required = false) String contactInfo,
            @ApiParam(value = "Минимальное количество заказов") @RequestParam(value = "minOrders", required = false) Integer minOrders,
            @ApiParam(value = "Максимальное количество заказов") @RequestParam(value = "maxOrders", required = false) Integer maxOrders) {

        try {
            if (Objects.isNull(action)) {
                List<ClientDTO> clientDTOs = userService.getAllClients();
                return ResponseEntity.ok(clientDTOs);
            } else {
                switch (action) {
                    case "filterByName":
                        if (Objects.nonNull(name)) {
                            List<ClientDTO> filteredClientsByName = userService.filterClientsByName(name);
                            return ResponseEntity.ok(filteredClientsByName);
                        } else {
                            throw new InvalidParameterException("Имя не указано");
                        }
                    case "filterByContactInfo":
                        if (Objects.nonNull(contactInfo)) {
                            List<ClientDTO> filteredClientsByContactInfo = userService.filterClientsByContactInfo(contactInfo);
                            return ResponseEntity.ok(filteredClientsByContactInfo);
                        } else {
                            throw new InvalidParameterException("Контактная информация не указана");
                        }
                    case "sortByName":
                        List<ClientDTO> sortedClientsByName = userService.sortClientsByName();
                        return ResponseEntity.ok(sortedClientsByName);
                    case "filterByOrders":
                        if (minOrders != null && maxOrders != null) {
                            List<ClientDTO> filteredClientsByOrders = userService.filterClientsByOrders(minOrders, maxOrders);
                            return ResponseEntity.ok(filteredClientsByOrders);
                        } else {
                            throw new InvalidParameterException("Неверный формат чисел для заказов");
                        }
                    case "sortByOrders":
                        List<ClientDTO> sortedClientsByOrders = userService.sortClientsByOrders();
                        return ResponseEntity.ok(sortedClientsByOrders);
                    default:
                        throw new InvalidParameterException("Неверное действие");
                }
            }
        } catch (ClientNotFoundException e) {
            // Возвращаем пустой список с соответствующим статусом, так как клиент не найден
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        } catch (InvalidParameterException e) {
            // Возвращаем пустой список с соответствующим статусом, так как параметры некорректны
            return ResponseEntity.badRequest().body(List.of());
        } catch (SQLException e) {
            // Возвращаем пустой список с ошибкой сервера
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        } catch (Exception e) {
            // Возвращаем пустой список с ошибкой сервера
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
}
