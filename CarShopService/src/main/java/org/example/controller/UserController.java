package org.example.controller;

import org.example.model.Client;
import org.example.model.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserController {
    private List<User> users = new ArrayList<>();

    // Регистрация нового пользователя
    public void registerUser(User user) {
        users.add(user);
    }

    // Аутентификация пользователя
    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Получение списка всех клиентов
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Client) {
                clients.add((Client) user);
            }
        }
        return clients;
    }


    // Проверка, есть ли пользователь с таким именем
    public boolean userExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    // Фильтрация клиентов по имени
    public List<Client> filterClientsByName(String name) {
        return getAllClients().stream()
                .filter(client -> client.getUsername().contains(name))
                .collect(Collectors.toList());
    }

    // Фильтрация клиентов по контактной информации
    public List<Client> filterClientsByContactInfo(String contactInfo) {
        return getAllClients().stream()
                .filter(client -> client.getContactInfo().contains(contactInfo))
                .collect(Collectors.toList());
    }




    // Сортировка клиентов по имени
    public List<Client> sortClientsByName() {
        return getAllClients().stream()
                .sorted(Comparator.comparing(Client::getUsername))
                .collect(Collectors.toList());
    }


    // Фильтрация клиентов по количеству заказов
    public List<Client> filterClientsByOrders(int minOrders, int maxOrders) {
        return getAllClients().stream()
                .filter(client -> client.getOrderCount() >= minOrders && client.getOrderCount() <= maxOrders)
                .collect(Collectors.toList());
    }



    // Сортировка клиентов по количеству заказов
    public List<Client> sortClientsByOrders() {
        return getAllClients().stream()
                .sorted(Comparator.comparingInt(Client::getOrderCount))
                .collect(Collectors.toList());
    }

    // Увеличение количества заказов клиента
    public void increaseOrderCount(String username) {
        for (User user : users) {
            if (user instanceof Client && user.getUsername().equals(username)) {
                Client client = (Client) user;
                client.increaseOrderCount();
                System.out.println("Количество заказов для клиента " + username + " увеличено до " + client.getOrderCount());
                return;
            }
        }
        System.out.println("Клиент с именем " + username + " не найден.");
    }
}



