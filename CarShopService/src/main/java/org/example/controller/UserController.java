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
import java.util.Objects;


/**
 * Сервлет для обработки HTTP-запросов, связанных с пользователями.
 * Обрабатывает различные действия, такие как регистрация, аутентификация, фильтрация и сортировка пользователей.
 */
@WebServlet("/api/users")
public class UserController extends HttpServlet {
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Инициализирует UserService при создании сервлета.
     *
     * @throws ServletException если произошла ошибка при инициализации.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager();
        UserRepository userRepository = new UserRepository(dbConnectionManager);
        this.userService = new UserService(userRepository);
    }

    /**
     * Обрабатывает GET-запросы для получения списка пользователей или информации о клиентах.
     *
     * @param req  запрос от клиента с параметрами действия.
     * @param resp ответ клиенту с информацией в формате JSON.
     * @throws ServletException если произошла ошибка в процессе обработки запроса.
     * @throws IOException      если произошла ошибка ввода/вывода.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if (Objects.isNull(action)) {

                List<UserDTO> userDTOs = userService.getAllUsers();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), userDTOs);
            } else if ("clients".equals(action)) {

                List<ClientDTO> clientDTOs = userService.getAllClients();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), clientDTOs);
            } else if ("client".equals(action)) {

                String username = req.getParameter("username");
                if (Objects.nonNull(username)) {
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

    /**
     * Обрабатывает POST-запросы для регистрации пользователей или аутентификации.
     *
     * @param req  запрос от клиента с параметрами действия.
     * @param resp ответ клиенту с подтверждением регистрации или аутентификации.
     * @throws ServletException если произошла ошибка в процессе обработки запроса.
     * @throws IOException      если произошла ошибка ввода/вывода.
     */
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
                if (Objects.nonNull(userDTO)) {
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

    /**
     * Обрабатывает PUT-запросы для увеличения количества заказов, фильтрации и сортировки клиентов.
     *
     * @param req  запрос от клиента с параметрами действия.
     * @param resp ответ клиенту с результатами обработки.
     * @throws ServletException если произошла ошибка в процессе обработки запроса.
     * @throws IOException      если произошла ошибка ввода/вывода.
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if (Objects.isNull(action)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверное действие");
                return;
            }

            switch (action) {
                case "increaseOrders":
                    String username = req.getParameter("username");
                    if (Objects.nonNull(username)) {
                        userService.increaseOrderCount(username);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя пользователя не указано");
                    }
                    break;

                case "filterByName":
                    String name = req.getParameter("name");
                    if (Objects.nonNull(name)) {
                        List<ClientDTO> filteredClients = userService.filterClientsByName(name);
                        resp.setContentType("application/json");
                        objectMapper.writeValue(resp.getWriter(), filteredClients);
                    } else {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя не указано");
                    }
                    break;

                case "filterByContactInfo":
                    String contactInfo = req.getParameter("contactInfo");
                    if (Objects.nonNull(contactInfo)) {
                        List<ClientDTO> filteredClients = userService.filterClientsByContactInfo(contactInfo);
                        resp.setContentType("application/json");
                        objectMapper.writeValue(resp.getWriter(), filteredClients);
                    } else {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Контактная информация не указана");
                    }
                    break;

                case "sortByName":
                    List<ClientDTO> sortedClients = userService.sortClientsByName();
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getWriter(), sortedClients);
                    break;

                case "filterByOrders":
                    int minOrders = Integer.parseInt(req.getParameter("minOrders"));
                    int maxOrders = Integer.parseInt(req.getParameter("maxOrders"));
                    List<ClientDTO> filteredClientsByOrders = userService.filterClientsByOrders(minOrders, maxOrders);
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getWriter(), filteredClientsByOrders);
                    break;

                case "sortByOrders":
                    List<ClientDTO> sortedClientsByOrders = userService.sortClientsByOrders();
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getWriter(), sortedClientsByOrders);
                    break;

                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверное действие");
                    break;
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка обработки запроса");
        }
    }

    /**
     * Обрабатывает DELETE-запросы для удаления пользователя по имени пользователя.
     *
     * @param req  запрос от клиента с параметром username.
     * @param resp ответ клиенту с результатом операции удаления.
     * @throws ServletException если произошла ошибка в процессе обработки запроса.
     * @throws IOException      если произошла ошибка ввода/вывода.
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String username = req.getParameter("username");
            if (Objects.nonNull(username)) {
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