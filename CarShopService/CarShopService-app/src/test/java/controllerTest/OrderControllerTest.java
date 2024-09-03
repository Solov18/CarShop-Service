package controllerTest;

import org.example.controller.OrderController;
import org.example.dto.OrderDTO;
import org.example.exception.InvalidOrderDataException;
import org.example.exception.OrderNotFoundException;
import org.example.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreateOrderSuccess() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, "New Order", "details", 1);

        Mockito.when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"New Order\",\"details\":\"details\",\"status\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.name").value(orderDTO.getClientUsername()))
                .andExpect(jsonPath("$.details").value(orderDTO.getClientUsername()))
                .andExpect(jsonPath("$.status").value(orderDTO.getStatus()));
    }

    @Test
    public void testCreateOrderInvalidData() throws Exception {
        Mockito.when(orderService.createOrder(any(OrderDTO.class))).thenThrow(new InvalidOrderDataException("Invalid data"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"\",\"details\":\"\",\"status\":1}")) // Добавлено поле "status"
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOrderByIdSuccess() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, "Order", "details", 1);

        Mockito.when(orderService.getOrderById(1)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.name").value(orderDTO.getClientUsername()))
                .andExpect(jsonPath("$.details").value(orderDTO.getClientUsername()))
                .andExpect(jsonPath("$.status").value(orderDTO.getStatus()));
    }

    @Test
    public void testGetOrderByIdNotFound() throws Exception {
        Mockito.when(orderService.getOrderById(1)).thenThrow(new OrderNotFoundException("Order not found"));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateOrderStatusSuccess() throws Exception {
        Mockito.when(orderService.updateOrderStatus(eq(1), eq("shipped"))).thenReturn(true);

        mockMvc.perform(put("/api/orders/1")
                        .param("status", "shipped"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateOrderStatusNotFound() throws Exception {
        Mockito.when(orderService.updateOrderStatus(eq(1), eq("shipped"))).thenThrow(new OrderNotFoundException("Order not found"));

        mockMvc.perform(put("/api/orders/1")
                        .param("status", "shipped"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateOrderStatusDatabaseError() throws Exception {
        Mockito.when(orderService.updateOrderStatus(eq(1), eq("shipped"))).thenThrow(new SQLException("Database error"));

        mockMvc.perform(put("/api/orders/1")
                        .param("status", "shipped"))
                .andExpect(status().isInternalServerError());
    }
}
