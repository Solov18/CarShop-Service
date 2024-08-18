package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.config.DatabaseConnectionManager;
import org.example.dto.AuthenticationDTO;
import org.example.dto.ClientDTO;
import org.example.dto.UserDTO;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import java.io.IOException;
import java.util.List;
import java.sql.SQLException;


@WebServlet("/api/users")
public class UserController extends HttpServlet {
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager();
        UserRepository userRepository = new UserRepository(dbConnectionManager);
        this.userService = new UserService(userRepository);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if (action == null) {

                List<UserDTO> userDTOs = userService.getAllUsers();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), userDTOs);
            } else if ("clients".equals(action)) {

                List<ClientDTO> clientDTOs = userService.getAllClients();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), clientDTOs);
            } else if ("client".equals(action)) {

                String username = req.getParameter("username");
                if (username != null) {
                    try {
                        ClientDTO clientDTO = userService.getClientByUsername(username);
                        resp.setContentType("application/json");
                        objectMapper.writeValue(resp.getWriter(), clientDTO);
                    } catch (RuntimeException e) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                    }
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя пользователя не указано");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверное действие");
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка получения данных");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("register".equals(action)) {

                UserDTO userDTO = objectMapper.readValue(req.getReader(), UserDTO.class);
                try {
                    userService.registerUser(userDTO);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } catch (RuntimeException e) {
                    resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
                }
            } else if ("authenticate".equals(action)) {

                AuthenticationDTO authDTO = objectMapper.readValue(req.getReader(), AuthenticationDTO.class);
                UserDTO userDTO = userService.authenticate(authDTO.getUsername(), authDTO.getPassword());
                if (userDTO != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    objectMapper.writeValue(resp.getWriter(), userDTO);
                } else {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Неверное имя пользователя или пароль");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверное действие");
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка обработки запроса");
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("increaseOrders".equals(action)) {

                String username = req.getParameter("username");
                if (username != null) {
                    userService.increaseOrderCount(username);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя пользователя не указано");
                }
            } else if ("filterByName".equals(action)) {

                String name = req.getParameter("name");
                if (name != null) {
                    List<ClientDTO> filteredClients = userService.filterClientsByName(name);
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getWriter(), filteredClients);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя не указано");
                }
            } else if ("filterByContactInfo".equals(action)) {

                String contactInfo = req.getParameter("contactInfo");
                if (contactInfo != null) {
                    List<ClientDTO> filteredClients = userService.filterClientsByContactInfo(contactInfo);
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getWriter(), filteredClients);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Контактная информация не указана");
                }
            } else if ("sortByName".equals(action)) {

                List<ClientDTO> sortedClients = userService.sortClientsByName();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), sortedClients);
            } else if ("filterByOrders".equals(action)) {

                int minOrders = Integer.parseInt(req.getParameter("minOrders"));
                int maxOrders = Integer.parseInt(req.getParameter("maxOrders"));
                List<ClientDTO> filteredClients = userService.filterClientsByOrders(minOrders, maxOrders);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), filteredClients);
            } else if ("sortByOrders".equals(action)) {

                List<ClientDTO> sortedClients = userService.sortClientsByOrders();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), sortedClients);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверное действие");
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка обработки запроса");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            String username = req.getParameter("username");
            if (username != null) {
                if (userService.userExists(username)) {
                    userService.removeUser(username);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Пользователь не найден");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя пользователя не указано");
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка удаления пользователя");
        }
    }
}