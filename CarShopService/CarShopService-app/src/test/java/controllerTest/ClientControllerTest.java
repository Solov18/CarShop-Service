package controllerTest;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    public void testGetAllClients() throws Exception {
        // Prepare data
        ClientDTO client1 = new ClientDTO(1, "John Doe","Клиент", "john.doe@example.com", 5);
        ClientDTO client2 = new ClientDTO(2, "Jane Smith","Клиент", "jane.smith@example.com", 10);
        List<ClientDTO> clientList = Arrays.asList(client1, client2);

        // Mocking service response
        given(userService.getAllClients()).willReturn(clientList);


        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(client1.getId()))
                .andExpect(jsonPath("$[0].username").value(client1.getUsername()))
                .andExpect(jsonPath("$[1].id").value(client2.getId()))
                .andExpect(jsonPath("$[1].username").value(client2.getUsername()));
    }

    @Test
    public void testFilterClientsByName() throws Exception {
        // Prepare data
        ClientDTO client = new ClientDTO(1, "John Doe","Клиент", "john.doe@example.com", 5);

        // Mocking service response
        given(userService.filterClientsByName("John")).willReturn(List.of(client));

        // Perform request and verify response
        mockMvc.perform(get("/api/clients")
                        .param("action", "filterByName")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(client.getId()))
                .andExpect(jsonPath("$[0].name").value(client.getUsername()));
    }

    @Test
    public void testFilterClientsByContactInfo() throws Exception {
        // Prepare data
        ClientDTO client = new ClientDTO(1, "John Doe","Клиент", "john.doe@example.com", 5);

        // Mocking service response
        given(userService.filterClientsByContactInfo("john.doe@example.com")).willReturn(List.of(client));

        // Perform request and verify response
        mockMvc.perform(get("/api/clients")
                        .param("action", "filterByContactInfo")
                        .param("contactInfo", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(client.getId()))
                .andExpect(jsonPath("$[0].contactInfo").value(client.getContactInfo()));
    }

    @Test
    public void testFilterClientsByOrders() throws Exception {
        // Prepare data
        ClientDTO client1 = new ClientDTO(1, "John Doe","Клиент", "john.doe@example.com", 5);
        ClientDTO client2 = new ClientDTO(2, "Jane Smith","Клиент", "jane.smith@example.com", 7);
        List<ClientDTO> clientList = Arrays.asList(client1, client2);

        // Mocking service response
        given(userService.filterClientsByOrders(5, 10)).willReturn(clientList);

        // Perform request and verify response
        mockMvc.perform(get("/api/clients")
                        .param("action", "filterByOrders")
                        .param("minOrders", "5")
                        .param("maxOrders", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(client1.getId()))
                .andExpect(jsonPath("$[0].ordersCount").value(client1.getOrderCount()))
                .andExpect(jsonPath("$[1].id").value(client2.getId()))
                .andExpect(jsonPath("$[1].ordersCount").value(client2.getOrderCount()));
    }

    @Test
    public void testSortClientsByName() throws Exception {
        // Prepare data
        ClientDTO client1 = new ClientDTO(1, "John Doe", "Клиент", "john.doe@example.com", 5);
        ClientDTO client2 = new ClientDTO(2, "Jane Smith","Клиент", "jane.smith@example.com", 7);
        List<ClientDTO> clientList = Arrays.asList(client1, client2);


        given(userService.sortClientsByName()).willReturn(clientList);


        mockMvc.perform(get("/api/clients")
                        .param("action", "sortByName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].name").value("John Doe"));
    }

    @Test
    public void testHandleClientNotFoundException() throws Exception {

        given(userService.getAllClients()).willThrow(new ClientNotFoundException("Clients not found"));


        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testHandleInvalidParameterException() throws Exception {
        // Mocking service to throw exception
        given(userService.filterClientsByName("John")).willThrow(new InvalidParameterException("Invalid parameter"));

        // Perform request and verify response
        mockMvc.perform(get("/api/clients")
                        .param("action", "filterByName")
                        .param("name", "John"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHandleSQLException() throws Exception {
        // Mocking service to throw exception
        given(userService.getAllClients()).willThrow(new SQLException("Database error"));

        // Perform request and verify response
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isInternalServerError());
    }
}