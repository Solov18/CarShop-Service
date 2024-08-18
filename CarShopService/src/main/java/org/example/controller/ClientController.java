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


@WebServlet("/api/clients")
public class ClientController extends HttpServlet {
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

                List<ClientDTO> clientDTOs = userService.getAllClients();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), clientDTOs);
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
}