package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthenticationDTO;
import org.example.dto.ClientDTO;
import org.example.dto.UserDTO;
import org.example.exception.ClientNotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Tag(name = "Users", description = "API для управления пользователями и клиентами")
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно возвращен"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> userDTOs = userService.getAllUsers();
            return ResponseEntity.ok(userDTOs);
        } catch (SQLException e) {
            log.error("Ошибка при получении всех пользователей", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Получить всех клиентов", description = "Возвращает список всех клиентов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список клиентов успешно возвращен"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @GetMapping("/clients")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        try {
            List<ClientDTO> clientDTOs = userService.getAllClients();
            return ResponseEntity.ok(clientDTOs);
        } catch (SQLException e) {
            log.error("Ошибка при получении всех клиентов", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Получить клиента по имени пользователя", description = "Возвращает данные клиента по его имени пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные клиента успешно возвращены"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @GetMapping("/client")
    public ResponseEntity<?> getClientByUsername(
            @Parameter(description = "Имя пользователя", required = true) @RequestParam String username) {
        try {
            ClientDTO clientDTO = userService.getClientByUsername(username);
            return ResponseEntity.ok(clientDTO);
        } catch (ClientNotFoundException e) {
            log.error("Клиент не найден", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SQLException e) {
            log.error("Ошибка при получении клиента по имени пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении клиента");
        }
    }

    @Operation(summary = "Регистрация пользователя", description = "Регистрация нового пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных регистрации пользователя"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Parameter(description = "Данные нового пользователя", required = true) @RequestBody AuthenticationDTO authenticationDTO) {
        try {
            userService.registerUser(authenticationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь зарегистрирован");
        } catch (UserAlreadyExistsException e) {
            log.error("Ошибка при регистрации пользователя", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (SQLException e) {
            log.error("Ошибка при регистрации пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка регистрации пользователя");
        }
    }

    @Operation(summary = "Аутентификация пользователя", description = "Проверка учетных данных пользователя для аутентификации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно аутентифицирован"),
            @ApiResponse(responseCode = "401", description = "Неверное имя пользователя или пароль"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @Parameter(description = "Данные для аутентификации", required = true) @RequestBody AuthenticationDTO authDTO) {
        try {
            UserDTO userDTO = userService.authenticate(authDTO.getUsername(), authDTO.getPassword());
            if (userDTO != null) {
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверное имя пользователя или пароль");
            }
        } catch (SQLException e) {
            log.error("Ошибка при аутентификации", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка аутентификации");
        }
    }

    @Operation(summary = "Увеличение количества заказов пользователя", description = "Увеличивает количество заказов пользователя по его имени пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество заказов успешно увеличено"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @PutMapping("/increaseOrders")
    public ResponseEntity<String> increaseOrders(
            @Parameter(description = "Имя пользователя", required = true) @RequestParam String username) {
        try {
            userService.increaseOrderCount(username);
            return ResponseEntity.ok("Количество заказов увеличено");
        } catch (SQLException e) {
            log.error("Ошибка при увеличении количества заказов", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка обновления количества заказов");
        }
    }

    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя по его имени пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы с базой данных")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "Имя пользователя", required = true) @RequestParam String username) {
        try {
            if (userService.userExists(username)) {
                userService.removeUser(username);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка удаления пользователя");
        }
    }
}
