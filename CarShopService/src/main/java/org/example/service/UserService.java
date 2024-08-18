package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.ClientDTO;
import org.example.dto.UserDTO;
import org.example.mapper.UserMapper;
import org.example.model.Client;
import org.example.model.User;
import org.example.repository.UserRepository;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;


    public void registerUser(UserDTO userDTO) throws SQLException {
        User user = UserMapper.INSTANCE.userDTOToUser(userDTO);
        if (userExists(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        userRepository.addUser(user);
    }


    public UserDTO authenticate(String username, String password) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // Преобразование сущности в DTO
            return UserMapper.INSTANCE.userToUserDTO(user);
        }
        return null;
    }


    public List<UserDTO> getAllUsers() throws SQLException {
        return userRepository.getAllUsers().stream()
                .map(UserMapper.INSTANCE::userToUserDTO)
                .collect(Collectors.toList());
    }


    public List<ClientDTO> getAllClients() throws SQLException {
        return userRepository.getAllUsers().stream()
                .filter(user -> user instanceof Client)
                .map(user -> (Client) user)
                .map(UserMapper.INSTANCE::clientToClientDTO)
                .collect(Collectors.toList());
    }


    public boolean userExists(String username) throws SQLException {
        return userRepository.userExists(username);
    }


    public List<ClientDTO> filterClientsByName(String name) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getUsername().contains(name))
                .collect(Collectors.toList());
    }


    public List<ClientDTO> filterClientsByContactInfo(String contactInfo) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getContactInfo().contains(contactInfo))
                .collect(Collectors.toList());
    }


    public List<ClientDTO> sortClientsByName() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparing(ClientDTO::getUsername))
                .collect(Collectors.toList());
    }


    public List<ClientDTO> filterClientsByOrders(int minOrders, int maxOrders) throws SQLException {
        return getAllClients().stream()
                .filter(clientDTO -> clientDTO.getOrderCount() >= minOrders && clientDTO.getOrderCount() <= maxOrders)
                .collect(Collectors.toList());
    }


    public List<ClientDTO> sortClientsByOrders() throws SQLException {
        return getAllClients().stream()
                .sorted(Comparator.comparingInt(ClientDTO::getOrderCount))
                .collect(Collectors.toList());
    }


    public void increaseOrderCount(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user instanceof Client) {
            Client client = (Client) user;
            client.increaseOrderCount();
            userRepository.addUser(client);
        } else {
            System.out.println("Клиент с именем " + username + " не найден.");
        }
    }


    public void removeUser(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user != null) {
            userRepository.removeUser(user);
        } else {
            System.out.println("Пользователь с именем " + username + " не найден.");
        }
    }

    public ClientDTO getClientByUsername(String username) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user instanceof Client) {
            return UserMapper.INSTANCE.clientToClientDTO((Client) user);
        }
        throw new RuntimeException("Клиент с таким именем не найден");
    }
}

