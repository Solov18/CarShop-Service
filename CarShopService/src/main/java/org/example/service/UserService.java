package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthenticationDTO;
import org.example.dto.ClientDTO;
import org.example.dto.UserDTO;
import org.example.exception.ClientNotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.mapper.AdminMapper;
import org.example.mapper.ClientMapper;
import org.example.mapper.ManagerMapper;
import org.example.mapper.UserMapper;
import org.example.model.Client;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями и клиентами.
 *
 * Этот сервис предоставляет методы для регистрации пользователей, аутентификации, получения списка пользователей,
 * фильтрации и сортировки клиентов, а также для увеличения количества заказов и удаления пользователей.
 *
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final ClientMapper clientMapper;
    private final ManagerMapper managerMapper;

    /**
     * Регистрация нового пользователя.
     *
     * Преобразует данные аутентификации в объект {@link User}. Если пользователь с таким именем уже существует,
     * генерируется исключение {@link UserAlreadyExistsException}. Иначе, новый пользователь сохраняется в репозитории.
     *
     *
     * @param authenticationDTO данные для аутентификации пользователя.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     * @throws UserAlreadyExistsException если пользователь с таким именем уже существует.
     */
    public void registerUser(AuthenticationDTO authenticationDTO) throws SQLException {
        User user = convertToUser(authenticationDTO);
        if (userExists(user.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        userRepository.addUser(user);
    }

    /**
     * Аутентификация пользователя по имени и паролю.
     *
     * Проверяет, существует ли пользователь с указанным именем и совпадает ли пароль. Если проверка успешна,
     * возвращает объект {@link UserDTO}; в противном случае возвращает {@code null}.
     *
     *
     * @param username имя пользователя.
     * @param password пароль пользователя.
     * @return объект {@link UserDTO} при успешной аутентификации, {@code null} в противном случае.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public UserDTO authenticate(String username, String password) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return userMapper.userToUserDTO(user);
        }
        return null;
    }

    /**
     * Получение списка всех пользователей.
     *
     * Получает список всех пользователей из репозитория и преобразует их в объекты {@link UserDTO}.
     *
     *
     * @return список {@link UserDTO} всех пользователей.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public List<UserDTO> getAllUsers() throws SQLException {
        return userRepository.getAllUsers().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение списка всех клиентов.
     *
     * Фильтрует пользователей по типу {@link Client} и преобразует их в объекты {@link ClientDTO}.
     *
     *
     * @return список {@link ClientDTO} всех клиентов.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> getAllClients() throws SQLException {
        return userRepository.getAllUsers().stream()
                .filter(Client.class::isInstance)
                .map(Client.class::cast)
                .map(clientMapper::clientToClientDTO)
                .collect(Collectors.toList());
    }

    /**
     * Проверка существования пользователя.
     *
     * Проверяет, существует ли пользователь с указанным именем в репозитории.
     *
     *
     * @param username имя пользователя.
     * @return {@code true}, если пользователь существует; {@code false} в противном случае.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public boolean userExists(String username) throws SQLException {
        return userRepository.userExists(username);
    }

    /**
     * Фильтрация клиентов по имени.
     *
     * Возвращает список клиентов, у которых имя содержит указанную подстроку.
     *
     *
     * @param name подстрока для фильтрации имени клиента.
     * @return список {@link ClientDTO} клиентов, удовлетворяющих условию.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> filterClientsByName(String name) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getUsername().contains(name))
                .collect(Collectors.toList());
    }

    /**
     * Фильтрация клиентов по контактной информации.
     *
     * Возвращает список клиентов, у которых контактная информация содержит указанную подстроку.
     *
     *
     * @param contactInfo подстрока для фильтрации контактной информации клиента.
     * @return список {@link ClientDTO} клиентов, удовлетворяющих условию.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> filterClientsByContactInfo(String contactInfo) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getContactInfo().contains(contactInfo))
                .collect(Collectors.toList());
    }

    /**
     * Сортировка клиентов по имени.
     *
     * Возвращает список клиентов, отсортированный по имени в алфавитном порядке.
     *
     *
     * @return отсортированный список {@link ClientDTO} клиентов.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> sortClientsByName() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparing(ClientDTO::getUsername))
                .collect(Collectors.toList());
    }

    /**
     * Фильтрация клиентов по количеству заказов.
     *
     * Возвращает список клиентов, у которых количество заказов находится в указанном диапазоне.
     *
     *
     * @param minOrders минимальное количество заказов.
     * @param maxOrders максимальное количество заказов.
     * @return список {@link ClientDTO} клиентов, удовлетворяющих условию.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> filterClientsByOrders(int minOrders, int maxOrders) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getOrderCount() >= minOrders && clientDTO.getOrderCount() <= maxOrders)
                .collect(Collectors.toList());
    }

    /**
     * Сортировка клиентов по количеству заказов.
     *
     * Возвращает список клиентов, отсортированный по количеству заказов в порядке возрастания.
     *
     *
     * @return отсортированный список {@link ClientDTO} клиентов.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public List<ClientDTO> sortClientsByOrders() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparingInt(ClientDTO::getOrderCount))
                .collect(Collectors.toList());
    }


    /**
     * Увеличение количества заказов клиента.
     * <p>
     * Увеличивает количество заказов клиента с указанным именем. Если пользователь не является клиентом,
     * генерируется предупреждающее сообщение в лог.
     * <p>
     *
     * @param username имя пользователя.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public void increaseOrderCount(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user instanceof Client) {
            Client client = (Client) user;
            client.increaseOrderCount();
            userRepository.addUser(client);
        } else {
            log.warn("Клиент с именем {} не найден.", username);
        }
    }

    /**
     * Удаление пользователя.
     * <p>
     * Удаляет пользователя с указанным именем. Если пользователь не найден, генерируется предупреждающее сообщение в лог.
     * <p>
     *
     * @param username имя пользователя.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     */
    public void removeUser(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user != null) {
            userRepository.removeUser(user);
        } else {
            log.warn("Пользователь с именем {} не найден.", username);
        }
    }

    /**
     * Получение клиента по имени пользователя.
     * <p>
     * Возвращает клиента с указанным именем пользователя. Если клиент не найден, генерируется исключение {@link ClientNotFoundException}.
     * </p>
     *
     * @param username имя пользователя клиента.
     * @return объект {@link ClientDTO} клиента.
     * @throws SQLException если возникает ошибка при работе с базой данных.
     * @throws ClientNotFoundException если клиент с таким именем не найден.
     */
    public ClientDTO getClientByUsername(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user instanceof Client) {
            return clientMapper.clientToClientDTO((Client) user);
        }
        throw new ClientNotFoundException("Клиент с таким именем не найден");
    }

    /**
     * Преобразование данных аутентификации в объект {@link User}.
     * <p>
     * Преобразует объект {@link AuthenticationDTO} в соответствующий объект {@link User} в зависимости от типа пользователя.
     * </p>
     *
     * @param authenticationDTO данные аутентификации.
     * @return объект {@link User}.
     * @throws IllegalArgumentException если неизвестный тип пользователя.
     */
    private User convertToUser(AuthenticationDTO authenticationDTO) {
        switch (authenticationDTO.getUserType()) {
            case "admin":
                return adminMapper.authenticationDTOToAdmin(authenticationDTO);
            case "client":
                return clientMapper.authenticationDTOToClient(authenticationDTO);
            case "manager":
                return managerMapper.authenticationDTOToManager(authenticationDTO);
            default:
                throw new IllegalArgumentException("Unknown user type: " + authenticationDTO.getUserType());
        }
    }
}
