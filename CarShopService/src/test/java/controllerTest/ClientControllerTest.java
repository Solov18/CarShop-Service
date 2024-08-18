package controllerTest;


import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controller.ClientController;
import org.example.dto.ClientDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;


public class ClientControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ClientController clientController;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter writer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGetAllClients() throws Exception {
        List<ClientDTO> clientDTOs = Collections.singletonList(new ClientDTO());
        when(request.getParameter("action")).thenReturn(null);
        when(userService.getAllClients()).thenReturn(clientDTOs);


        clientController.doGet(request, response);


        verify(response).setContentType("application/json");


        verify(objectMapper).writeValue(writer, clientDTOs);
    }

    @Test
    public void testDoGetFilterByName() throws Exception {

        List<ClientDTO> filteredClients = Collections.singletonList(new ClientDTO());
        when(request.getParameter("action")).thenReturn("filterByName");
        when(request.getParameter("name")).thenReturn("John");
        when(userService.filterClientsByName("John")).thenReturn(filteredClients);


        clientController.doGet(request, response);


        verify(response).setContentType("application/json");
        verify(userService).filterClientsByName("John");
    }

    @Test
    public void testDoGetFilterByNameBadRequest() throws Exception {

        when(request.getParameter("action")).thenReturn("filterByName");
        when(request.getParameter("name")).thenReturn(null);


        clientController.doGet(request, response);


        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя не указано");
    }

    @Test
    public void testDoGetFilterByContactInfo() throws Exception {

        List<ClientDTO> filteredClients = Collections.singletonList(new ClientDTO());
        when(request.getParameter("action")).thenReturn("filterByContactInfo");
        when(request.getParameter("contactInfo")).thenReturn("test@example.com");
        when(userService.filterClientsByContactInfo("test@example.com")).thenReturn(filteredClients);


        clientController.doGet(request, response);


        verify(response).setContentType("application/json");
        verify(userService).filterClientsByContactInfo("test@example.com");
    }

    @Test
    public void testDoGetFilterByContactInfoBadRequest() throws Exception {

        when(request.getParameter("action")).thenReturn("filterByContactInfo");
        when(request.getParameter("contactInfo")).thenReturn(null);


        clientController.doGet(request, response);


        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Контактная информация не указана");
    }

    @Test
    public void testDoGetSortByName() throws Exception {

        List<ClientDTO> sortedClients = Collections.singletonList(new ClientDTO());
        when(request.getParameter("action")).thenReturn("sortByName");
        when(userService.sortClientsByName()).thenReturn(sortedClients);


        clientController.doGet(request, response);


        verify(response).setContentType("application/json");
        verify(userService).sortClientsByName();
    }

    @Test
    public void testDoGetFilterByOrders() throws Exception {

        List<ClientDTO> filteredClients = Collections.singletonList(new ClientDTO());
        when(request.getParameter("action")).thenReturn("filterByOrders");
        when(request.getParameter("minOrders")).thenReturn("5");
        when(request.getParameter("maxOrders")).thenReturn("15");
        when(userService.filterClientsByOrders(5, 15)).thenReturn(filteredClients);


        clientController.doGet(request, response);


        verify(response).setContentType("application/json");
        verify(userService).filterClientsByOrders(5, 15);
    }

    @Test
    public void testDoGetFilterByOrdersNumberFormatException() throws Exception {

        when(request.getParameter("action")).thenReturn("filterByOrders");
        when(request.getParameter("minOrders")).thenReturn("invalid");
        when(request.getParameter("maxOrders")).thenReturn("15");


        clientController.doGet(request, response);


        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка обработки запроса");
    }

    @Test
    public void testDoGetSortByOrders() throws Exception {

        List<ClientDTO> sortedClients = Collections.singletonList(new ClientDTO());
        when(request.getParameter("action")).thenReturn("sortByOrders");
        when(userService.sortClientsByOrders()).thenReturn(sortedClients);


        clientController.doGet(request, response);


        verify(response).setContentType("application/json");
        verify(userService).sortClientsByOrders();
    }
}