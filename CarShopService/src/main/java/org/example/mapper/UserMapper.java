package org.example.mapper;

import org.example.dto.AdminDTO;
import org.example.dto.ClientDTO;
import org.example.dto.ManagerDTO;
import org.example.dto.UserDTO;
import org.example.model.Admin;
import org.example.model.Client;
import org.example.model.Manager;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { AdminMapper.class, ManagerMapper.class, ClientMapper.class })
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", source = "role")
    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);

    // Маппинг для конкретных типов
    ClientDTO clientToClientDTO(Client client);

    Client clientDTOToClient(ClientDTO clientDTO);

    ManagerDTO managerToManagerDTO(Manager manager);

    Manager managerDTOToManager(ManagerDTO managerDTO);

    AdminDTO adminToAdminDTO(Admin admin);

    Admin adminDTOToAdmin(AdminDTO adminDTO);
}
