package testBase;

import org.example.config.DatabaseConnectionManager;
import org.example.model.Admin;
import org.example.model.Client;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends DatabaseTestBase {

    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager(
                System.getProperty("db.url"),
                System.getProperty("db.username"),
                System.getProperty("db.password")
        );
        userRepository = new UserRepository(dbConnectionManager);

        // Добавление тестовых данных
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR PRIMARY KEY, " +
                    "password VARCHAR, " +
                    "role VARCHAR, " +
                    "contact_info VARCHAR)");
            statement.execute("INSERT INTO users (username, password, role, contact_info) " +
                    "VALUES ('john_doe', 'password123', 'Admin', 'john.doe@example.com')");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при вставке данных в базу данных", e);
        }
    }

    @Test
    public void testGetUserByUsername() {
        User user = userRepository.getUserByUsername("john_doe");
        assertNotNull(user);
        assertEquals("john_doe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertTrue(user instanceof Admin);

    }

    @Test
    public void testUserExists() {
        boolean exists = userRepository.userExists("john_doe");
        assertTrue(exists);
    }

    @Test
    public void testAddUser() {
        User newUser = new Client("jane_smith", "password456", "jane.smith@example.com");
        userRepository.addUser(newUser);
        User fetchedUser = userRepository.getUserByUsername("jane_smith");
        assertNotNull(fetchedUser);
        assertTrue(fetchedUser instanceof Client);
        assertEquals("jane_smith", fetchedUser.getUsername());
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userRepository.getAllUsers();
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(user -> "john_doe".equals(user.getUsername())));
    }
}