package org.example.repository;

import lombok.AllArgsConstructor;
import org.example.config.DatabaseConnectionManager;
import org.example.model.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class UserRepository {
    private final DatabaseConnectionManager dbConnectionManager;

    // Получение соединения с базой данных
    private Connection getConnection() throws SQLException {
        return dbConnectionManager.getConnection();
    }

    // Добавление нового пользователя
    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, contact_info) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole());
            if (user instanceof Client) {
                preparedStatement.setString(4, ((Client) user).getContactInfo());
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении пользователя: " + e.getMessage());
            throw new RuntimeException("Не удалось добавить пользователя в базу данных", e);
        }
    }

    // Получение пользователя по username
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                String contactInfo = resultSet.getString("contact_info");

                // Сопоставление роли
                switch (role.toLowerCase()) {
                    case "admin":
                        return new Admin(username, password);
                    case "client":
                        return new Client(username, password, contactInfo);
                    case "manager":
                        return new Manager(username, password);
                    default:
                        System.out.println("Unknown role: " + role);
                }
            } else {
                System.out.println("User not found: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении пользователя по username: " + e.getMessage());
            throw new RuntimeException("Не удалось получить пользователя из базы данных", e);
        }
        return null;
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                if (role.equalsIgnoreCase("admin")) {
                    users.add(new Admin(username, password));
                } else if (role.equalsIgnoreCase("client")) {
                    users.add(new Client(username, password, resultSet.getString("contact_info")));
                } else if (role.equalsIgnoreCase("manager")) {
                    users.add(new Manager(username, password));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении всех пользователей: " + e.getMessage());
            throw new RuntimeException("Не удалось получить пользователей из базы данных", e);
        }
        return users;
    }

    // Проверка существования пользователя
    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке существования пользователя: " + e.getMessage());
            throw new RuntimeException("Не удалось проверить существование пользователя в базе данных", e);
        }
    }

    // Удаление пользователя
    public void removeUser(User user) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            throw new RuntimeException("Не удалось удалить пользователя из базы данных", e);
        }
    }

    // Фильтрация клиентов по контактной информации
    public List<Client> getClientsByContactInfo(String contactInfo) {
        String sql = "SELECT * FROM users WHERE role = 'Client' AND contact_info LIKE ?";
        List<Client> clients = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + contactInfo + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                clients.add(new Client(username, password, resultSet.getString("contact_info")));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при фильтрации клиентов по контактной информации: " + e.getMessage());
            throw new RuntimeException("Не удалось получить клиентов из базы данных", e);
        }
        return clients;
    }
}