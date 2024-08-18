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



@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", ignore = true)
    UserDTO userToUserDTO(User user);

    @Mapping(target = "role", ignore = true)
    User userDTOToUser(UserDTO userDTO);

    @Mapping(target = "orderCount", ignore = true)
    ClientDTO clientToClientDTO(Client client);

    @Mapping(target = "orderCount", ignore = true)
    Client clientDTOToClient(ClientDTO clientDTO);


    ManagerDTO managerToManagerDTO(Manager manager);

    Manager managerDTOToManager(ManagerDTO managerDTO);

    AdminDTO adminToAdminDTO(Admin admin);

    Admin adminDTOToAdmin(AdminDTO adminDTO);
}
