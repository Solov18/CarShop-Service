//package controllerTest;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.example.controller.UserController;
//import org.example.dto.AuthenticationDTO;
//import org.example.dto.ClientDTO;
//import org.example.dto.UserDTO;
//import org.example.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.io.BufferedReader;
//import java.io.StringReader;
//import java.util.Collections;
//import java.util.List;
//
//
//public class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private HttpServletRequest request;
//    private HttpServletResponse response;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        request = mock(HttpServletRequest.class);
//        response = mock(HttpServletResponse.class);
//    }
//
//    @Test
//    public void testDoGetAllUsers() throws Exception {
//
//        List<UserDTO> userDTOs = Collections.singletonList(new UserDTO());
//        when(request.getParameter("action")).thenReturn(null);
//        when(userService.getAllUsers()).thenReturn(userDTOs);
//
//
//        userController.doGet(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(userService).getAllUsers();
//    }
//
//    @Test
//    public void testDoGetAllClients() throws Exception {
//
//        List<ClientDTO> clientDTOs = Collections.singletonList(new ClientDTO());
//        when(request.getParameter("action")).thenReturn("clients");
//        when(userService.getAllClients()).thenReturn(clientDTOs);
//
//
//        userController.doGet(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(userService).getAllClients();
//    }
//
//    @Test
//    public void testDoGetClientByUsername() throws Exception {
//
//        ClientDTO clientDTO = new ClientDTO();
//        when(request.getParameter("action")).thenReturn("client");
//        when(request.getParameter("username")).thenReturn("testuser");
//        when(userService.getClientByUsername("testuser")).thenReturn(clientDTO);
//
//
//        userController.doGet(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(userService).getClientByUsername("testuser");
//    }
//
//    @Test
//    public void testDoGetClientByUsernameNotFound() throws Exception {
//
//        when(request.getParameter("action")).thenReturn("client");
//        when(request.getParameter("username")).thenReturn("testuser");
//        when(userService.getClientByUsername("testuser")).thenThrow(new RuntimeException("Client not found"));
//
//
//        userController.doGet(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
//    }
//
//    @Test
//    public void testDoPostRegisterUser() throws Exception {
//
//        UserDTO userDTO = new UserDTO();
//        when(request.getParameter("action")).thenReturn("register");
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\":\"testuser\",\"password\":\"password\"}")));
//
//
//        userController.doPost(request, response);
//
//
//        verify(userService).registerUser(any(UserDTO.class));
//        verify(response).setStatus(HttpServletResponse.SC_CREATED);
//    }
//
//    @Test
//    public void testDoPostAuthenticate() throws Exception {
//
//        AuthenticationDTO authDTO = new AuthenticationDTO("testuser", "password");
//        UserDTO userDTO = new UserDTO();
//        when(request.getParameter("action")).thenReturn("authenticate");
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\":\"testuser\",\"password\":\"password\"}")));
//        when(userService.authenticate("testuser", "password")).thenReturn(userDTO);
//
//
//        userController.doPost(request, response);
//
//
//        verify(response).setStatus(HttpServletResponse.SC_OK);
//        verify(response).setContentType("application/json");
//        verify(userService).authenticate("testuser", "password");
//    }
//
//    @Test
//    public void testDoPostAuthenticateUnauthorized() throws Exception {
//
//        when(request.getParameter("action")).thenReturn("authenticate");
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}")));
//        when(userService.authenticate("testuser", "wrongpassword")).thenReturn(null);
//
//
//        userController.doPost(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Неверное имя пользователя или пароль");
//    }
//
//    @Test
//    public void testDoPutIncreaseOrders() throws Exception {
//
//        when(request.getParameter("action")).thenReturn("increaseOrders");
//        when(request.getParameter("username")).thenReturn("testuser");
//
//
//        userController.doPut(request, response);
//
//
//        verify(userService).increaseOrderCount("testuser");
//        verify(response).setStatus(HttpServletResponse.SC_OK);
//    }
//
//    @Test
//    public void testDoPutIncreaseOrdersBadRequest() throws Exception {
//
//        when(request.getParameter("action")).thenReturn("increaseOrders");
//        when(request.getParameter("username")).thenReturn(null);
//
//
//        userController.doPut(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя пользователя не указано");
//    }
//
//    @Test
//    public void testDoPutFilterByName() throws Exception {
//
//        List<ClientDTO> filteredClients = Collections.singletonList(new ClientDTO());
//        when(request.getParameter("action")).thenReturn("filterByName");
//        when(request.getParameter("name")).thenReturn("John");
//        when(userService.filterClientsByName("John")).thenReturn(filteredClients);
//
//
//        userController.doPut(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(userService).filterClientsByName("John");
//    }
//
//    @Test
//    public void testDoDeleteUser() throws Exception {
//
//        when(request.getParameter("username")).thenReturn("testuser");
//        when(userService.userExists("testuser")).thenReturn(true);
//
//
//        userController.doDelete(request, response);
//
//
//        verify(userService).removeUser("testuser");
//        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
//    }
//
//    @Test
//    public void testDoDeleteUserNotFound() throws Exception {
//
//        when(request.getParameter("username")).thenReturn("testuser");
//        when(userService.userExists("testuser")).thenReturn(false);
//
//
//        userController.doDelete(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Пользователь не найден");
//    }
//
//    @Test
//    public void testDoDeleteBadRequest() throws Exception {
//
//        when(request.getParameter("username")).thenReturn(null);
//
//
//        userController.doDelete(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя пользователя не указано");
//    }
//}