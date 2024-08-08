import org.example.controller.UserController;
import org.example.logi.AuditLogController;
import org.example.model.Admin;
import org.example.model.Client;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController;
    private AuditLogController auditLogController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        auditLogController = mock(AuditLogController.class);
    }

    @Test
    public void testRegisterUser() {
        User admin = new Admin("admin", "password");
        userController.registerUser(admin);

        List<User> users = userController.getAllUsers();
        assertThat(users).contains(admin);
    }

    @Test
    public void testAuthenticateUser() {
        User admin = new Admin("admin", "password");
        userController.registerUser(admin);

        User authenticatedUser = userController.authenticate("admin", "password");
        assertThat(authenticatedUser).isEqualTo(admin);
    }

    @Test
    public void testAuthenticateUserWithWrongPassword() {
        User admin = new Admin("admin", "password");
        userController.registerUser(admin);

        User authenticatedUser = userController.authenticate("admin", "wrongpassword");
        assertThat(authenticatedUser).isNull();
    }

    @Test
    public void testFilterClientsByName() {
        Client client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);

        List<Client> filteredClients = userController.filterClientsByName("client");
        assertThat(filteredClients).contains(client);
    }


    @Test
    public void testFilterClientsByContactInfo() {
        Client client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);

        List<Client> filteredClients = userController.filterClientsByContactInfo("contact@example.com");
        assertThat(filteredClients).contains(client);
    }

    @Test
    public void testSortClientsByName() {
        Client client1 = new Client("zebra", "password", "contact1@example.com");
        Client client2 = new Client("apple", "password", "contact2@example.com");
        userController.registerUser(client1);
        userController.registerUser(client2);

        List<Client> sortedClients = userController.sortClientsByName();
        assertThat(sortedClients).isSortedAccordingTo((c1, c2) -> c1.getUsername().compareTo(c2.getUsername()));
    }

    @Test
    public void testFilterClientsByOrders() {
        Client client1 = new Client("client1", "password", "contact1@example.com");
        client1.setOrderCount(5);
        Client client2 = new Client("client2", "password", "contact2@example.com");
        client2.setOrderCount(15);
        userController.registerUser(client1);
        userController.registerUser(client2);

        List<Client> filteredClients = userController.filterClientsByOrders(5, 10);
        assertThat(filteredClients).contains(client1);
        assertThat(filteredClients).doesNotContain(client2);
    }

    @Test
    public void testSortClientsByOrders() {
        Client client1 = new Client("client1", "password", "contact1@example.com");
        client1.setOrderCount(10);
        Client client2 = new Client("client2", "password", "contact2@example.com");
        client2.setOrderCount(5);
        userController.registerUser(client1);
        userController.registerUser(client2);

        List<Client> sortedClients = userController.sortClientsByOrders();
        assertThat(sortedClients).isSortedAccordingTo((c1, c2) -> Integer.compare(c1.getOrderCount(), c2.getOrderCount()));
    }

    @Test
    public void testIncreaseOrderCount() {
        Client client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);

        userController.increaseOrderCount("client");
        assertThat(client.getOrderCount()).isEqualTo(1);
    }

    @Test
    public void testUserExists() {
        Client client = new Client("client", "password", "contact@example.com");
        userController.registerUser(client);

        boolean exists = userController.userExists("client");
        assertThat(exists).isTrue();

        exists = userController.userExists("nonexistent");
        assertThat(exists).isFalse();
    }
}

