package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.model.Client;
import org.example.model.User;
import org.example.repository.UserRepository;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserController {
    private UserRepository userRepository;

    // Регистрация нового пользователя
    public void registerUser(User user) throws SQLException {
        userRepository.addUser(user);
    }

    // Аутентификация пользователя
    public User authenticate(String username, String password) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    // Получение всех пользователей
    public List<User> getAllUsers() throws SQLException {
        return userRepository.getAllUsers();
    }

    // Получение списка всех клиентов
    public List<Client> getAllClients() throws SQLException {
        return userRepository.getAllUsers().stream()
                .filter(user -> user instanceof Client)
                .map(user -> (Client) user)
                .collect(Collectors.toList());
    }

    // Проверка, есть ли пользователь с таким именем
    public boolean userExists(String username) throws SQLException {
        return userRepository.userExists(username);
    }

    // Фильтрация клиентов по имени
    public List<Client> filterClientsByName(String name) throws SQLException {
        return getAllClients().stream()
                .filter(client -> client.getUsername().contains(name))
                .collect(Collectors.toList());
    }

    // Фильтрация клиентов по контактной информации
    public List<Client> filterClientsByContactInfo(String contactInfo) throws SQLException {
        return getAllClients().stream()
                .filter(client -> client.getContactInfo().contains(contactInfo))
                .collect(Collectors.toList());
    }

    // Сортировка клиентов по имени
    public List<Client> sortClientsByName() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparing(Client::getUsername))
                .collect(Collectors.toList());
    }

    // Фильтрация клиентов по количеству заказов
    public List<Client> filterClientsByOrders(int minOrders, int maxOrders) throws SQLException {
        return getAllClients().stream()
                .filter(client -> client.getOrderCount() >= minOrders && client.getOrderCount() <= maxOrders)
                .collect(Collectors.toList());
    }

    // Сортировка клиентов по количеству заказов
    public List<Client> sortClientsByOrders() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparingInt(Client::getOrderCount))
                .collect(Collectors.toList());
    }

    // Увеличение количества заказов клиента
    public void increaseOrderCount(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user instanceof Client) {
            Client client = (Client) user;
            client.increaseOrderCount();
            userRepository.addUser(client); // Update the client in the repository
            System.out.println("Количество заказов для клиента " + username + " увеличено до " + client.getOrderCount());
        } else {
            System.out.println("Клиент с именем " + username + " не найден.");
        }
    }
}

