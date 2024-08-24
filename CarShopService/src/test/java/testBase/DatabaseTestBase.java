//package testBase;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseTestBase {
//
//    protected static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
//            .withDatabaseName("testdb")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    static {
//        postgresContainer.start();
//    }
//
//    @BeforeEach
//    public void setUp() {
//
//        System.setProperty("db.url", postgresContainer.getJdbcUrl());
//        System.setProperty("db.username", postgresContainer.getUsername());
//        System.setProperty("db.password", postgresContainer.getPassword());
//    }
//
//    @AfterEach
//    public void tearDown() {
//
//    }
//
//    protected Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(
//                System.getProperty("db.url"),
//                System.getProperty("db.username"),
//                System.getProperty("db.password")
//        );
//    }
//}