package org.example.repository;

import org.example.model.Admin;
import org.example.model.Client;
import org.example.model.Manager;
import org.example.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;


    private static final String INSERT_USER_SQL = "INSERT INTO users (username, password, role, contact_info) VALUES (?, ?, ?, ?)";
    private static final String SELECT_USER_BY_USERNAME_SQL = "SELECT * FROM users WHERE username = ?";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM users";
    private static final String USER_EXISTS_SQL = "SELECT COUNT(*) FROM users WHERE username = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE username = ?";
    private static final String SELECT_CLIENTS_BY_CONTACT_INFO_SQL = "SELECT * FROM users WHERE role = 'client' AND contact_info LIKE ?";

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void addUser(User user) {
        jdbcTemplate.update(INSERT_USER_SQL, user.getUsername(), user.getPassword(), user.getRole(),
                user instanceof Client ? ((Client) user).getContactInfo() : null);
    }

    public User getUserByUsername(String username) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_USERNAME_SQL, new Object[]{username}, new UserRowMapper());
    }

    public List<User> getAllUsers() {
        return jdbcTemplate.query(SELECT_ALL_USERS_SQL, new UserRowMapper());
    }

    public boolean userExists(String username) {
        return jdbcTemplate.queryForObject(USER_EXISTS_SQL, new Object[]{username}, Integer.class) > 0;
    }

    @Transactional
    public void removeUser(User user) {
        jdbcTemplate.update(DELETE_USER_SQL, user.getUsername());
    }

    public List<Client> getClientsByContactInfo(String contactInfo) {
        return jdbcTemplate.query(SELECT_CLIENTS_BY_CONTACT_INFO_SQL, new Object[]{"%" + contactInfo + "%"}, new ClientRowMapper());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            String username = rs.getString("username");
            String password = rs.getString("password");
            String role = rs.getString("role");
            String contactInfo = rs.getString("contact_info");

            switch (role.toLowerCase()) {
                case "admin":
                    return new Admin(username, password);
                case "client":
                    return new Client(username, password, contactInfo);
                case "manager":
                    return new Manager(username, password);
                default:
                    throw new SQLException("Unknown role: " + role);
            }
        }
    }

    private static class ClientRowMapper implements RowMapper<Client> {
        @Override
        public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
            String username = rs.getString("username");
            String password = rs.getString("password");
            String contactInfo = rs.getString("contact_info");
            return new Client(username, password, contactInfo);
        }
    }
}
