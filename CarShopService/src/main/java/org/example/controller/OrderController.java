package org.example.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.example.config.DatabaseConnectionManager;
import org.example.repository.CarRepository;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.example.service.OrderService;
import org.example.dto.OrderDTO;
import org.example.service.UserService;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;



public class OrderController extends HttpServlet {
    private final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderController() {

        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager();


        CarRepository carRepository = new CarRepository(dbConnectionManager);
        UserRepository userRepository = new UserRepository(dbConnectionManager);
        OrderRepository orderRepository = new OrderRepository(carRepository, userRepository, dbConnectionManager);


        UserService userService = new UserService(userRepository);
        this.orderService = new OrderService(orderRepository, userService, carRepository);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            OrderDTO orderDTO = objectMapper.readValue(req.getReader(), OrderDTO.class);
            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), createdOrder);
        } catch (ValidationException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при создании заказа");
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if (action == null) {

                List<OrderDTO> orders = orderService.getAllOrders();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), orders);
            } else if ("byId".equals(action)) {

                int id = Integer.parseInt(req.getParameter("id"));
                OrderDTO order = orderService.getOrderById(id);
                if (order != null) {
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getWriter(), order);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Заказ не найден");
                }
            } else if ("byDateRange".equals(action)) {

                LocalDateTime startDateTime = LocalDateTime.parse(req.getParameter("startDateTime"));
                LocalDateTime endDateTime = LocalDateTime.parse(req.getParameter("endDateTime"));
                List<OrderDTO> orders = orderService.getOrdersByDateRange(startDateTime, endDateTime);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), orders);
            } else if ("byClient".equals(action)) {

                String clientUsername = req.getParameter("clientUsername");
                List<OrderDTO> orders = orderService.getOrdersByClient(clientUsername);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), orders);
            } else if ("byStatus".equals(action)) {

                String status = req.getParameter("status");
                List<OrderDTO> orders = orderService.getOrdersByStatus(status);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), orders);
            } else if ("byCar".equals(action)) {

                int carId = Integer.parseInt(req.getParameter("carId"));
                List<OrderDTO> orders = orderService.getOrdersByCar(carId);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), orders);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный параметр действия");
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обработке запроса");
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            int id = Integer.parseInt(req.getParameter("id"));
            String status = req.getParameter("status");
            boolean updated = orderService.updateOrderStatus(id, status);
            if (updated) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Заказ не найден");
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обновлении статуса заказа");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            int id = Integer.parseInt(req.getParameter("id"));
            boolean canceled = orderService.cancelOrder(id);
            if (canceled) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Заказ не найден");
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при отмене заказа");
        }
    }
}