package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@Api(value = "User API", tags = {"Users"})
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

    @ApiOperation(value = "Получить всех пользователей", notes = "Возвращает список всех пользователей")
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

    @ApiOperation(value = "Получить всех клиентов", notes = "Возвращает список всех клиентов")
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

    @ApiOperation(value = "Получить клиента по имени пользователя", notes = "Возвращает данные клиента по его имени пользователя")
    @GetMapping("/client")
    public ResponseEntity<?> getClientByUsername(
            @ApiParam(value = "Имя пользователя", required = true) @RequestParam String username) {
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

    @ApiOperation(value = "Регистрация пользователя", notes = "Регистрация нового пользователя в системе")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @ApiParam(value = "Данные нового пользователя", required = true) @RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь зарегистрирован");
        } catch (UserAlreadyExistsException e) {
            log.error("Ошибка при регистрации пользователя", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (SQLException e) {
            log.error("Ошибка при регистрации пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка регистрации пользователя");
        }
    }

    @ApiOperation(value = "Аутентификация пользователя", notes = "Проверка учетных данных пользователя для аутентификации")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @ApiParam(value = "Данные для аутентификации", required = true) @RequestBody AuthenticationDTO authDTO) {
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

    @ApiOperation(value = "Увеличение количества заказов пользователя", notes = "Увеличивает количество заказов пользователя по его имени пользователя")
    @PutMapping("/increaseOrders")
    public ResponseEntity<String> increaseOrders(
            @ApiParam(value = "Имя пользователя", required = true) @RequestParam String username) {
        try {
            userService.increaseOrderCount(username);
            return ResponseEntity.ok("Количество заказов увеличено");
        } catch (SQLException e) {
            log.error("Ошибка при увеличении количества заказов", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка обновления количества заказов");
        }
    }

    @ApiOperation(value = "Удаление пользователя", notes = "Удаляет пользователя по его имени пользователя")
    @DeleteMapping
    public ResponseEntity<String> deleteUser(
            @ApiParam(value = "Имя пользователя", required = true) @RequestParam String username) {
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
