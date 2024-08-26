package controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.UserController;
import org.example.dto.AuthenticationDTO;
import org.example.dto.ClientDTO;
import org.example.dto.UserDTO;
import org.example.exception.ClientNotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Тесты для контроллера пользователей {@link UserController}.
 * Проверяют функциональность методов контроллера, включая обработку исключений.
 */
@SpringJUnitConfig
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тестирование получения всех пользователей.
     * Проверяет успешный сценарий, когда метод {@code getAllUsers()} возвращает список пользователей.
     */
    @Test
    void testGetAllUsers_Success() throws SQLException {
        List<UserDTO> userDTOs = new ArrayList<>();
        userDTOs.add(new UserDTO("user1", "pass1"));
        userDTOs.add(new UserDTO("user2", "pass2"));

        when(userService.getAllUsers()).thenReturn(userDTOs);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTOs, response.getBody());
    }

    /**
     * Тестирование получения всех клиентов.
     * Проверяет успешный сценарий, когда метод {@code getAllClients()} возвращает список клиентов.
     */
    @Test
    void testGetAllClients_Success() throws SQLException {
        List<ClientDTO> clientDTOs = new ArrayList<>();
        clientDTOs.add(new ClientDTO("client1", 1));
        clientDTOs.add(new ClientDTO("client2", 1));

        when(userService.getAllClients()).thenReturn(clientDTOs);

        ResponseEntity<List<ClientDTO>> response = userController.getAllClients();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clientDTOs, response.getBody());
    }

    /**
     * Тестирование получения клиента по имени пользователя.
     * Проверяет успешный сценарий, когда клиент найден.
     */
    @Test
    void testGetClientByUsername_Success() throws SQLException, ClientNotFoundException {
        ClientDTO clientDTO = new ClientDTO("client1", 1);

        when(userService.getClientByUsername(anyString())).thenReturn(clientDTO);

        ResponseEntity<?> response = userController.getClientByUsername("client1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clientDTO, response.getBody());
    }

    /**
     * Тестирование получения клиента по имени пользователя, когда клиент не найден.
     * Проверяет обработку исключения {@code ClientNotFoundException}.
     */
    @Test
    void testGetClientByUsername_ClientNotFound() throws SQLException, ClientNotFoundException {
        when(userService.getClientByUsername(anyString())).thenThrow(new ClientNotFoundException("Client not found"));

        ResponseEntity<?> response = userController.getClientByUsername("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Client not found", response.getBody());
    }

    /**
     * Тестирование регистрации нового пользователя.
     * Проверяет успешный сценарий регистрации пользователя.
     */
    @Test
    void testRegisterUser_Success() throws SQLException, UserAlreadyExistsException {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO();

        ResponseEntity<String> response = userController.registerUser(authenticationDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered", response.getBody());
    }

    /**
     * Тестирование регистрации нового пользователя, когда пользователь уже существует.
     * Проверяет обработку исключения {@code UserAlreadyExistsException}.
     */
    @Test
    void testRegisterUser_UserAlreadyExists() throws SQLException, UserAlreadyExistsException {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        ResponseEntity<String> response = userController.registerUser(authenticationDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    /**
     * Тестирование аутентификации пользователя.
     * Проверяет успешный сценарий, когда аутентификация проходит успешно.
     */
    @Test
    void testAuthenticate_Success() throws SQLException {
        AuthenticationDTO authDTO = new AuthenticationDTO();
        UserDTO userDTO = new UserDTO("user1", "pass1");

        when(userService.authenticate(anyString(), anyString())).thenReturn(userDTO);

        ResponseEntity<?> response = userController.authenticate(authDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    /**
     * Тестирование аутентификации пользователя, когда учетные данные неверные.
     * Проверяет обработку случая, когда аутентификация не удается.
     */
    @Test
    void testAuthenticate_Unauthorized() throws SQLException {
        AuthenticationDTO authDTO = new AuthenticationDTO();

        when(userService.authenticate(anyString(), anyString())).thenReturn(null);

        ResponseEntity<?> response = userController.authenticate(authDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Неверное имя пользователя или пароль", response.getBody());
    }

    /**
     * Тестирование увеличения количества заказов пользователя.
     * Проверяет успешный сценарий увеличения количества заказов.
     */
    @Test
    void testIncreaseOrders_Success() throws SQLException {
        ResponseEntity<String> response = userController.increaseOrders("user1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Количество заказов увеличено", response.getBody());
    }

    /**
     * Тестирование удаления пользователя.
     * Проверяет успешный сценарий удаления пользователя.
     */
    @Test
    void testDeleteUser_Success() throws SQLException {
        when(userService.userExists(anyString())).thenReturn(true);

        ResponseEntity<String> response = userController.deleteUser("user1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Тестирование удаления пользователя, когда пользователь не найден.
     * Проверяет обработку случая, когда пользователь не существует.
     */
    @Test
    void testDeleteUser_NotFound() throws SQLException {
        when(userService.userExists(anyString())).thenReturn(false);

        ResponseEntity<String> response = userController.deleteUser("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Пользователь не найден", response.getBody());
    }

    /**
     * Тестирование обработки SQL исключения при получении всех пользователей.
     * Проверяет правильную обработку исключений при ошибке базы данных.
     */
    @Test
    void testGetAllUsers_SqlException() throws SQLException {
        when(userService.getAllUsers()).thenThrow(new SQLException("Database error"));

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}
