import static org.assertj.core.api.Assertions.assertThat;

import org.example.logi.AuditLogController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.example.controller.CarController;
import org.example.controller.OrderController;
import org.example.controller.UserController;
import org.example.model.User;
import org.example.CarShopApp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class CarShopAppTest {

    private UserController userController;
    private CarController carController;
    private OrderController orderController;
    private AuditLogController auditLogController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        carController = new CarController();
        orderController = new OrderController(userController);
        auditLogController = Mockito.mock(AuditLogController.class);
    }

    @Test
    public void testRegisterNewUser() {

        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream("Client\nnewuser\npassword\ncontact@example.com\n".getBytes()));
        CarShopApp.registerNewUser(userController, auditLogController, new Scanner(System.in), null);
        List<User> users = userController.getAllUsers();
        assertThat(users).anyMatch(user -> user.getUsername().equals("newuser"));
        System.setIn(stdin);
    }
}