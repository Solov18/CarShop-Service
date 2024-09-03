package controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.UserController;
import org.example.dto.AuthenticationDTO;
import org.example.dto.ClientDTO;
import org.example.dto.UserDTO;
import org.example.exception.ClientNotFoundException;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllUsersSuccess() throws Exception {
        List<UserDTO> userDTOs = Collections.singletonList(new UserDTO("user1", "role"));

        Mockito.when(userService.getAllUsers()).thenReturn(userDTOs);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].role").value("role"));
    }

    @Test
    public void testGetAllUsersDatabaseError() throws Exception {
        Mockito.when(userService.getAllUsers()).thenThrow(new SQLException("Database error"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Database error"));
    }

    @Test
    public void testGetAllClientsSuccess() throws Exception {
        List<ClientDTO> clientDTOs = Collections.singletonList(new ClientDTO(1, "user1", "Клиент", "contact", 5));

        Mockito.when(userService.getAllClients()).thenReturn(clientDTOs);

        mockMvc.perform(get("/api/users/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].contactInfo").value("contact"))
                .andExpect(jsonPath("$[0].orderCount").value(5));
    }

    @Test
    public void testGetAllClientsDatabaseError() throws Exception {
        Mockito.when(userService.getAllClients()).thenThrow(new SQLException("Database error"));

        mockMvc.perform(get("/api/users/clients"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Database error"));
    }

    @Test
    public void testGetClientByUsernameSuccess() throws Exception {
        ClientDTO clientDTO = new ClientDTO(1, "user1", "Клиент", "contact", 5);

        Mockito.when(userService.getClientByUsername("user1")).thenReturn(clientDTO);

        mockMvc.perform(get("/api/users/client")
                        .param("username", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.contactInfo").value("contact"))
                .andExpect(jsonPath("$.orderCount").value(5));
    }

    @Test
    public void testGetClientByUsernameNotFound() throws Exception {
        Mockito.when(userService.getClientByUsername("user1")).thenThrow(new ClientNotFoundException("Client not found"));

        mockMvc.perform(get("/api/users/client")
                        .param("username", "user1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Client not found"));
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("user1", "password", "Клиент");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Пользователь зарегистрирован"));
    }

    @Test
    public void testRegisterUserConflict() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("user1", "password", "Клиент");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists"));
    }

    @Test
    public void testRegisterUserDatabaseError() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("user1", "password", "Клиент");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ошибка регистрации пользователя"));
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("user1", "password", "Клиент");
        UserDTO userDTO = new UserDTO("user1", "role");

        Mockito.when(userService.authenticate("user1", "password")).thenReturn(userDTO);

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.role").value("role"));
    }

    @Test
    public void testAuthenticateUnauthorized() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("user1", "password", "Клиент");

        Mockito.when(userService.authenticate("user1", "password")).thenReturn(null);

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Неверное имя пользователя или пароль"));
    }

    @Test
    public void testAuthenticateDatabaseError() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("user1", "password", "Клиент");

        Mockito.when(userService.authenticate("user1", "password")).thenThrow(new SQLException("Database error"));

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ошибка аутентификации"));
    }

    @Test
    public void testIncreaseOrdersSuccess() throws Exception {
        mockMvc.perform(put("/api/users/increaseOrders")
                        .param("username", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Количество заказов увеличено"));
    }

    @Test
    public void testIncreaseOrdersDatabaseError() throws Exception {
        Mockito.doThrow(new SQLException("Database error")).when(userService).increaseOrderCount("user1");

        mockMvc.perform(put("/api/users/increaseOrders")
                        .param("username", "user1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ошибка обновления количества заказов"));
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        Mockito.when(userService.userExists("user1")).thenReturn(true);

        mockMvc.perform(delete("/api/users")
                        .param("username", "user1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        Mockito.when(userService.userExists("user1")).thenReturn(false);

        mockMvc.perform(delete("/api/users")
                        .param("username", "user1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь не найден"));
    }

    @Test
    public void testDeleteUserDatabaseError() throws Exception {
        Mockito.when(userService.userExists("user1")).thenReturn(true);
        Mockito.doThrow(new SQLException("Database error")).when(userService).removeUser("user1");

        mockMvc.perform(delete("/api/users")
                        .param("username", "user1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ошибка удаления пользователя"));
    }
}
