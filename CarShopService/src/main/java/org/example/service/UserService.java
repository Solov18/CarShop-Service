package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ClientDTO;
import org.example.dto.UserDTO;
import org.example.mapper.UserMapper;
import org.example.model.Client;
import org.example.model.User;
import org.example.repository.UserRepository;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервисный класс для управления пользователями. Предоставляет методы для регистрации,
 * аутентификации, фильтрации и сортировки пользователей и клиентов.
 */
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    /**
     * Регистрация нового пользователя.
     *
     * @param userDTO объект DTO пользователя, который нужно зарегистрировать.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     * @throws RuntimeException если пользователь с таким именем уже существует.
     */
    public void registerUser(UserDTO userDTO) throws SQLException {
        User user = UserMapper.INSTANCE.userDTOToUser(userDTO);
        if (userExists(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        userRepository.addUser(user);
    }

    /**
     * Аутентификация пользователя по имени пользователя и паролю.
     *
     * @param username имя пользователя.
     * @param password пароль пользователя.
     * @return объект DTO пользователя, если аутентификация успешна, иначе null.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public UserDTO authenticate(String username, String password) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return UserMapper.INSTANCE.userToUserDTO(user);
        }
        return null;
    }

    /**
     * Получение всех пользователей.
     *
     * @return список DTO всех пользователей.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public List<UserDTO> getAllUsers() throws SQLException {
        return userRepository.getAllUsers().stream()
                .map(UserMapper.INSTANCE::userToUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение всех клиентов.
     *
     * @return список DTO всех клиентов.
     * @throws SQLException если произошла ошибка при работе с базой данных.
     */
    public List<ClientDTO> getAllClients() throws SQLException {
        return userRepository.getAllUsers().stream()
                .filter(Client.class::isInstance)
                .map(Client.class::cast)
                .map(UserMapper.INSTANCE::clientToClientDTO)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, существует ли пользователь с заданным именем.
     *
     * @param username Имя пользователя, которого нужно проверить.
     * @return {@code true}, если пользователь существует, {@code false} в противном случае.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public boolean userExists(String username) throws SQLException {
        return userRepository.userExists(username);
    }

    /**
     * Фильтрует список клиентов по имени. Возвращает список клиентов, чье имя содержит заданную подстроку.
     *
     * @param name Подстрока для фильтрации по имени клиента.
     * @return Список клиентов, чье имя содержит заданную подстроку.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> filterClientsByName(String name) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getUsername().contains(name))
                .collect(Collectors.toList());
    }

    /**
     * Фильтрует список клиентов по контактной информации. Возвращает список клиентов, чья контактная информация содержит заданную подстроку.
     *
     * @param contactInfo Подстрока для фильтрации по контактной информации клиента.
     * @return Список клиентов, чья контактная информация содержит заданную подстроку.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> filterClientsByContactInfo(String contactInfo) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getContactInfo().contains(contactInfo))
                .collect(Collectors.toList());
    }

    /**
     * Сортирует список клиентов по имени в алфавитном порядке.
     *
     * @return Список клиентов, отсортированный по имени.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> sortClientsByName() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparing(ClientDTO::getUsername))
                .collect(Collectors.toList());
    }

    /**
     * Фильтрует список клиентов по количеству заказов. Возвращает список клиентов, количество заказов которых находится в заданном диапазоне.
     *
     * @param minOrders Минимальное количество заказов.
     * @param maxOrders Максимальное количество заказов.
     * @return Список клиентов, количество заказов которых находится в заданном диапазоне.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> filterClientsByOrders(int minOrders, int maxOrders) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getOrderCount() >= minOrders && clientDTO.getOrderCount() <= maxOrders)
                .collect(Collectors.toList());
    }

    /**
     * Сортирует список клиентов по количеству заказов в порядке возрастания.
     *
     * @return Список клиентов, отсортированный по количеству заказов.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> sortClientsByOrders() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparingInt(ClientDTO::getOrderCount))
                .collect(Collectors.toList());
    }

    /**
     * Увеличивает количество заказов для клиента с заданным именем.
     *
     * @param username Имя клиента, для которого нужно увеличить количество заказов.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public void increaseOrderCount(String username) throws SQLException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getUserByUsername(username));
        if (optionalUser.isPresent() && optionalUser.get() instanceof Client) {
            Client client = (Client) optionalUser.get();
            client.increaseOrderCount();
            userRepository.addUser(client);
        } else {
            log.warn("Клиент с именем {} не найден.", username);
        }
    }

    /**
     * Удаляет пользователя с заданным именем.
     *
     * @param username Имя пользователя, которого нужно удалить.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     */
    public void removeUser(String username) throws SQLException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getUserByUsername(username));
        if (optionalUser.isPresent()) {
            userRepository.removeUser(optionalUser.get());
        } else {
            log.warn("Пользователь с именем {} не найден.", username);
        }
    }

    /**
     * Возвращает клиента в виде {@link ClientDTO} по заданному имени.
     *
     * @param username Имя клиента, которого нужно найти.
     * @return {@link ClientDTO} клиента с заданным именем.
     * @throws SQLException Если возникает ошибка при работе с базой данных.
     * @throws RuntimeException Если клиент с таким именем не найден.
     */
    public ClientDTO getClientByUsername(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user instanceof Client) {
            return UserMapper.INSTANCE.clientToClientDTO((Client) user);
        }
        throw new RuntimeException("Клиент с таким именем не найден");
    }
}
