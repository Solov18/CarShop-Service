import org.example.controller.CarController;
import org.example.controller.OrderController;
import org.example.controller.UserController;
import org.example.model.Car;
import org.example.model.Client;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.model.Order;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderControllerTest {

    private OrderController orderController;
    private UserController userController;
    private CarController carController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        carController = new CarController();
        orderController = new OrderController(userController);
    }

    @Test
    public void testCreateOrder() {
        User client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);

        orderController.createOrder(car, (Client) client);
        List<Order> orders = orderController.getAllOrders();
        assertThat(orders).isNotEmpty();
    }

    @Test
    public void testUpdateOrderStatus() {
        User client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);

        Order order = orderController.createOrder(car, (Client) client);
        orderController.updateOrderStatus(order.getId(), "Shipped");

        Order updatedOrder = orderController.getOrderById(order.getId());
        assertThat(updatedOrder.getStatus()).isEqualTo("Shipped");
    }


    @Test
    public void testCancelOrder() {
        User client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);

        Order order = orderController.createOrder(car, (Client) client);
        boolean cancelled = orderController.cancelOrder(order.getId());

        Order cancelledOrder = orderController.getOrderById(order.getId());
        assertThat(cancelled).isTrue();
        assertThat(cancelledOrder.getStatus()).isEqualTo("cancelled");
        assertThat(carController.getAllAvailableCars().contains(car)).isTrue();
    }

    @Test
    public void testGetOrdersByDateRange() {
        User client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);

        Order order1 = orderController.createOrder(car, (Client) client);
        order1.setDate(LocalDateTime.of(2024, 1, 1, 10, 0));

        Order order2 = orderController.createOrder(car, (Client) client);
        order2.setDate(LocalDateTime.of(2024, 6, 15, 15, 0));

        List<Order> orders = orderController.getOrdersByDateRange(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59)
        );

        assertThat(orders).hasSize(2);
        assertThat(orders).containsExactlyInAnyOrder(order1, order2);
    }

    @Test
    public void testGetOrdersByClient() {
        User client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);

        Order order1 = orderController.createOrder(car, (Client) client);
        Order order2 = orderController.createOrder(car, (Client) client);

        List<Order> orders = orderController.getOrdersByClient(client.getUsername());

        assertThat(orders).hasSize(2);
        assertThat(orders).containsExactlyInAnyOrder(order1, order2);
    }


    @Test
    public void testGetOrdersByCar() {
        User client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);
        carController.addCar("Toyota", "Corolla", 2020, 20000, "New");
        Car car = carController.getAllAvailableCars().get(0);

        Order order1 = orderController.createOrder(car, (Client) client);
        Order order2 = orderController.createOrder(car, (Client) client);

        List<Order> orders = orderController.getOrdersByCar(car.getId());

        assertThat(orders).hasSize(2);
        assertThat(orders).containsExactlyInAnyOrder(order1, order2);
    }
}


