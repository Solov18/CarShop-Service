package controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.ClientController;
import org.example.dto.ClientDTO;
import org.example.exception.ClientNotFoundException;
import org.example.exception.InvalidParameterException;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест для проверки получения всех клиентов без фильтрации.
     */
    @Test
    void testGetAllClients() throws Exception {
        // Данные для теста
        List<ClientDTO> clients = Arrays.asList(
                new ClientDTO(1, "John" , "john@example.com", 5 ),
                new ClientDTO(2, "Jane", "jane@example.com", 10)
        );

        // Мокируем поведение сервиса
        when(userService.getAllClients()).thenReturn(clients);

        // Выполняем запрос без действия
        ResponseEntity<List<ClientDTO>> response = clientController.getClients(null, null, null, null, null);

        // Проверяем ответ
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clients, response.getBody());
    }

    /**
     * Тест для фильтрации клиентов по имени.
     */
    @Test
    void testFilterClientsByName() throws Exception {
        // Данные для теста
        List<ClientDTO> filteredClients = Arrays.asList(
                new ClientDTO(1, "John", "john@example.com", 5)
        );

        // Мокируем поведение сервиса
        when(userService.filterClientsByName("John")).thenReturn(filteredClients);

        // Выполняем запрос с фильтрацией по имени
        ResponseEntity<List<ClientDTO>> response = clientController.getClients("filterByName", "John", null, null, null);

        // Проверяем ответ
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(filteredClients, response.getBody());
    }

    /**
     * Тест для обработки ситуации, когда имя для фильтрации не указано.
     */
    @Test
    void testFilterClientsByNameWithoutName() throws Exception {
        // Ожидаем выброс исключения InvalidParameterException
        assertThrows(InvalidParameterException.class, () -> {
            clientController.getClients("filterByName", null, null, null, null);
        });
    }

    /**
     * Тест для фильтрации клиентов по количеству заказов.
     */
    @Test
    void testFilterClientsByOrders() throws Exception {
        // Данные для теста
        List<ClientDTO> filteredClients = Arrays.asList(
                new ClientDTO(2, "Jane", "jane@example.com", 10)
        );

        // Мокируем поведение сервиса
        when(userService.filterClientsByOrders(5, 10)).thenReturn(filteredClients);

        // Выполняем запрос с фильтрацией по количеству заказов
        ResponseEntity<List<ClientDTO>> response = clientController.getClients("filterByOrders", null, null, 5, 10);

        // Проверяем ответ
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(filteredClients, response.getBody());
    }

    /**
     * Тест для обработки некорректных параметров фильтрации по количеству заказов.
     */
    @Test
    void testFilterClientsByOrdersWithInvalidParameters() throws Exception {
        // Ожидаем выброс исключения InvalidParameterException
        assertThrows(InvalidParameterException.class, () -> {
            clientController.getClients("filterByOrders", null, null, null, null);
        });
    }

    /**
     * Тест для обработки ошибки "Клиенты не найдены".
     */
    @Test
    void testGetClientsNotFound() throws Exception {
        // Мокируем выброс исключения ClientNotFoundException
        when(userService.getAllClients()).thenThrow(new ClientNotFoundException("Clients not found"));

        // Выполняем запрос
        ResponseEntity<List<ClientDTO>> response = clientController.getClients(null, null, null, null, null);

        // Проверяем ответ
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Тест для обработки ошибки с базой данных.
     */
    @Test
    void testGetClientsDatabaseError() throws Exception {
        // Мокируем выброс SQLException
        when(userService.getAllClients()).thenThrow(new SQLException("Database error"));

        // Выполняем запрос
        ResponseEntity<List<ClientDTO>> response = clientController.getClients(null, null, null, null, null);

        // Проверяем ответ
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}
