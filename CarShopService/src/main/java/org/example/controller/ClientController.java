package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.config.DatabaseConnectionManager;
import org.example.dto.ClientDTO;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


/**
 * Класс ClientController является сервлетом, который обрабатывает HTTP-запросы, связанные с клиентами.
 * Он предоставляет действия для фильтрации, сортировки и получения данных о клиентах.
 */
@WebServlet("/api/clients")
public class ClientController extends HttpServlet {
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Инициализирует сервлет и настраивает необходимые сервисы для обработки данных о клиентах.
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
     * Обрабатывает GET-запросы к эндпоинту /api/clients.
     * Поддерживает действия такие как получение всех клиентов, фильтрация клиентов по имени, контактной информации или количеству заказов,
     * а также сортировка клиентов по имени или количеству заказов.
     *
     * @param req  объект HttpServletRequest, содержащий запрос клиента к сервлету
     * @param resp объект HttpServletResponse, содержащий ответ сервлета клиенту
     * @throws ServletException если произошла ошибка при обработке запроса
     * @throws IOException      если произошла ошибка ввода-вывода при обработке запроса
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if (Objects.isNull(action)) {
                // Получить всех клиентов
                List<ClientDTO> clientDTOs = userService.getAllClients();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), clientDTOs);
            } else {
                // Используем switch-case для обработки действия
                switch (action) {
                    case "filterByName" -> {
                        String name = req.getParameter("name");
                        if (Objects.nonNull(name)) {
                            List<ClientDTO> filteredClients = userService.filterClientsByName(name);
                            resp.setContentType("application/json");
                            objectMapper.writeValue(resp.getWriter(), filteredClients);
                        } else {
                            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Имя не указано");
                        }
                    }
                    case "filterByContactInfo" -> {
                        String contactInfo = req.getParameter("contactInfo");
                        if (Objects.nonNull(contactInfo)) {
                            List<ClientDTO> filteredClients = userService.filterClientsByContactInfo(contactInfo);
                            resp.setContentType("application/json");
                            objectMapper.writeValue(resp.getWriter(), filteredClients);
                        } else {
                            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Контактная информация не указана");
                        }
                    }
                    case "sortByName" -> {
                        List<ClientDTO> sortedClients = userService.sortClientsByName();
                        resp.setContentType("application/json");
                        objectMapper.writeValue(resp.getWriter(), sortedClients);
                    }
                    case "filterByOrders" -> {
                        try {
                            int minOrders = Integer.parseInt(req.getParameter("minOrders"));
                            int maxOrders = Integer.parseInt(req.getParameter("maxOrders"));
                            List<ClientDTO> filteredClients = userService.filterClientsByOrders(minOrders, maxOrders);
                            resp.setContentType("application/json");
                            objectMapper.writeValue(resp.getWriter(), filteredClients);
                        } catch (NumberFormatException e) {
                            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный формат чисел для заказов");
                        }
                    }
                    case "sortByOrders" -> {
                        List<ClientDTO> sortedClients = userService.sortClientsByOrders();
                        resp.setContentType("application/json");
                        objectMapper.writeValue(resp.getWriter(), sortedClients);
                    }
                    default -> {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверное действие");
                    }
                }
            }
        } catch (SQLException e) {
            // Обработка исключений и возвращение статуса ошибки сервера
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка обработки запроса");
        }
    }
}