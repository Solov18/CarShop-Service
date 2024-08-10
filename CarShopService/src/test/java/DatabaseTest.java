import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseTest {

    private static PostgreSQLContainer<?> postgresContainer;

    @BeforeAll
    public static void setUp() {
        postgresContainer = new PostgreSQLContainer<>("postgres:14")
                .withDatabaseName("testdb")
                .withUsername("user")
                .withPassword("password");
        postgresContainer.start();
    }

    @Test
    public void testDatabaseConnection() {
        assertNotNull(postgresContainer.getJdbcUrl());
        // Ваш код для проверки подключения к базе данных
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }
}