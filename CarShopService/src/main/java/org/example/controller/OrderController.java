package org.example.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
import java.util.Objects;


/**
 * Контроллер для управления CRUD операциями с заказами.
 * Обрабатывает HTTP-запросы на URL "/api/orders".
 */
@WebServlet("/api/orders")
public class OrderController extends HttpServlet {
    private final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Конструктор контроллера заказов.
     * Инициализирует все необходимые сервисы и репозитории.
     */
    public OrderController() {
        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager();
        CarRepository carRepository = new CarRepository(dbConnectionManager);
        UserRepository userRepository = new UserRepository(dbConnectionManager);
        OrderRepository orderRepository = new OrderRepository(carRepository, userRepository, dbConnectionManager);
        UserService userService = new UserService(userRepository);
        this.orderService = new OrderService(orderRepository, userService, carRepository);
    }

    /**
     * Обрабатывает POST-запрос для создания нового заказа.
     *
     * @param req  запрос от клиента, содержащий данные заказа.
     * @param resp ответ клиенту с созданным заказом или ошибкой.
     * @throws ServletException если произошла ошибка сервлета.
     * @throws IOException если произошла ошибка ввода/вывода.
     */
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

    /**
     * Обрабатывает GET-запрос для получения списка заказов или определенного заказа.
     *
     * @param req  запрос от клиента, содержащий параметры действия.
     * @param resp ответ клиенту с информацией о заказе или списке заказов.
     * @throws ServletException если произошла ошибка сервлета.
     * @throws IOException если произошла ошибка ввода/вывода.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if (Objects.isNull(action)) {
                List<OrderDTO> orders = orderService.getAllOrders();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), orders);
            } else {
                handleAction(action, req, resp);
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обработке запроса");
        }
    }

    /**
     * Обрабатывает действия, переданные через параметр "action" в GET-запросе.
     *
     * @param action тип действия.
     * @param req запрос от клиента.
     * @param resp ответ клиенту.
     * @throws IOException если произошла ошибка ввода/вывода.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    private void handleAction(String action, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        switch (action) {
            case "byId":
                handleGetById(req, resp);
                break;
            case "byDateRange":
                handleGetByDateRange(req, resp);
                break;
            case "byClient":
                handleGetByClient(req, resp);
                break;
            case "byStatus":
                handleGetByStatus(req, resp);
                break;
            case "byCar":
                handleGetByCar(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный параметр действия");
        }
    }

    /**
     * Получает заказ по ID.
     *
     * @param req запрос от клиента, содержащий параметр ID заказа.
     * @param resp ответ клиенту с информацией о заказе.
     * @throws IOException если произошла ошибка ввода/вывода.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    private void handleGetById(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        int id = Integer.parseInt(req.getParameter("id"));
        OrderDTO order = orderService.getOrderById(id);
        if (order != null) {
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), order);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Заказ не найден");
        }
    }

    /**
     * Обрабатывает запросы на получение заказов в определенном временном диапазоне.
     *
     * @param req  запрос от клиента, содержащий параметры startDateTime и endDateTime.
     * @param resp ответ клиенту с заказами в указанный временной период в формате JSON.
     * @throws IOException если произошла ошибка ввода/вывода.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    private void handleGetByDateRange(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        LocalDateTime startDateTime = LocalDateTime.parse(req.getParameter("startDateTime"));
        LocalDateTime endDateTime = LocalDateTime.parse(req.getParameter("endDateTime"));
        List<OrderDTO> orders = orderService.getOrdersByDateRange(startDateTime, endDateTime);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), orders);
    }

    /**
     * Обрабатывает запросы на получение заказов клиента по его имени пользователя.
     *
     * @param req  запрос от клиента, содержащий параметр clientUsername.
     * @param resp ответ клиенту с заказами, связанными с указанным клиентом, в формате JSON.
     * @throws IOException если произошла ошибка ввода/вывода.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    private void handleGetByClient(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String clientUsername = req.getParameter("clientUsername");
        List<OrderDTO> orders = orderService.getOrdersByClient(clientUsername);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), orders);
    }

    /**
     * Обрабатывает запросы на получение заказов по их статусу.
     *
     * @param req  запрос от клиента, содержащий параметр status.
     * @param resp ответ клиенту с заказами, имеющими указанный статус, в формате JSON.
     * @throws IOException если произошла ошибка ввода/вывода.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    private void handleGetByStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String status = req.getParameter("status");
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), orders);
    }

    /**
     * Обрабатывает запросы на получение заказов по идентификатору автомобиля.
     *
     * @param req  запрос от клиента, содержащий параметр carId.
     * @param resp ответ клиенту с заказами, связанными с указанным автомобилем, в формате JSON.
     * @throws IOException если произошла ошибка ввода/вывода.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    private void handleGetByCar(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        int carId = Integer.parseInt(req.getParameter("carId"));
        List<OrderDTO> orders = orderService.getOrdersByCar(carId);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), orders);
    }

    /**
     * Обрабатывает PUT-запрос для обновления статуса заказа.
     *
     * @param req  запрос от клиента, содержащий параметры id заказа и новый статус.
     * @param resp ответ клиенту с кодом состояния, подтверждающим успешное обновление или сообщение об ошибке.
     * @throws ServletException если произошла ошибка сервлета.
     * @throws IOException если произошла ошибка ввода/вывода.
     */
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

    /**
     * Обрабатывает DELETE-запрос для отмены заказа.
     *
     * @param req  запрос от клиента, содержащий параметр id заказа.
     * @param resp ответ клиенту с кодом состояния, подтверждающим успешную отмену или сообщение об ошибке.
     * @throws ServletException если произошла ошибка сервлета.
     * @throws IOException если произошла ошибка ввода/вывода.
     */
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