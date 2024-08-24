//package controllerTest;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.ValidationException;
//import org.example.controller.OrderController;
//import org.example.dto.OrderDTO;
//import org.example.service.OrderService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.io.BufferedReader;
//import java.io.StringReader;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//
//public class OrderControllerTest {
//
//    @Mock
//    private OrderService orderService;
//
//    @InjectMocks
//    private OrderController orderController;
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
//    public void testDoPostSuccess() throws Exception {
//
//        OrderDTO orderDTO = new OrderDTO();
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"orderDetails\":\"details\"}")));
//        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);
//
//
//        orderController.doPost(request, response);
//
//
//        verify(response).setStatus(HttpServletResponse.SC_CREATED);
//        verify(orderService).createOrder(any(OrderDTO.class));
//    }
//
//    @Test
//    public void testDoPostValidationException() throws Exception {
//
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"orderDetails\":\"details\"}")));
//        doThrow(new ValidationException("Invalid data")).when(orderService).createOrder(any(OrderDTO.class));
//
//
//        orderController.doPost(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
//    }
//
//    @Test
//    public void testDoGetAllOrders() throws Exception {
//
//        List<OrderDTO> orders = Collections.singletonList(new OrderDTO());
//        when(request.getParameter("action")).thenReturn(null);
//        when(orderService.getAllOrders()).thenReturn(orders);
//
//
//        orderController.doGet(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(orderService).getAllOrders();
//    }
//
//    @Test
//    public void testDoGetOrderById() throws Exception {
//
//        OrderDTO orderDTO = new OrderDTO();
//        when(request.getParameter("action")).thenReturn("byId");
//        when(request.getParameter("id")).thenReturn("1");
//        when(orderService.getOrderById(1)).thenReturn(orderDTO);
//
//
//        orderController.doGet(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(orderService).getOrderById(1);
//    }
//
//    @Test
//    public void testDoGetOrderByIdNotFound() throws Exception {
//
//        when(request.getParameter("action")).thenReturn("byId");
//        when(request.getParameter("id")).thenReturn("1");
//        when(orderService.getOrderById(1)).thenReturn(null);
//
//
//        orderController.doGet(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Заказ не найден");
//    }
//
//    @Test
//    public void testDoPutSuccess() throws Exception {
//
//        when(request.getParameter("id")).thenReturn("1");
//        when(request.getParameter("status")).thenReturn("shipped");
//        when(orderService.updateOrderStatus(1, "shipped")).thenReturn(true);
//
//
//        orderController.doPut(request, response);
//
//
//        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
//    }
//
//    @Test
//    public void testDoPutOrderNotFound() throws Exception {
//
//        when(request.getParameter("id")).thenReturn("1");
//        when(request.getParameter("status")).thenReturn("shipped");
//        when(orderService.updateOrderStatus(1, "shipped")).thenReturn(false);
//
//
//        orderController.doPut(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Заказ не найден");
//    }
//
//    @Test
//    public void testDoDeleteSuccess() throws Exception {
//
//        when(request.getParameter("id")).thenReturn("1");
//        when(orderService.cancelOrder(1)).thenReturn(true);
//
//
//        orderController.doDelete(request, response);
//
//
//        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
//    }
//
//    @Test
//    public void testDoDeleteOrderNotFound() throws Exception {
//
//        when(request.getParameter("id")).thenReturn("1");
//        when(orderService.cancelOrder(1)).thenReturn(false);
//
//
//        orderController.doDelete(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Заказ не найден");
//    }
//
//    @Test
//    public void testDoGetOrdersByDateRange() throws Exception {
//
//        List<OrderDTO> orders = Collections.singletonList(new OrderDTO());
//        when(request.getParameter("action")).thenReturn("byDateRange");
//        when(request.getParameter("startDateTime")).thenReturn("2024-01-01T00:00:00");
//        when(request.getParameter("endDateTime")).thenReturn("2024-01-31T23:59:59");
//        when(orderService.getOrdersByDateRange(LocalDateTime.parse("2024-01-01T00:00:00"), LocalDateTime.parse("2024-01-31T23:59:59")))
//                .thenReturn(orders);
//
//
//        orderController.doGet(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(orderService).getOrdersByDateRange(LocalDateTime.parse("2024-01-01T00:00:00"), LocalDateTime.parse("2024-01-31T23:59:59"));
//    }
//}