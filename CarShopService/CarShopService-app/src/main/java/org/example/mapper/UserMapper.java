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


    UserDTO userToUserDTO(User user);




    @Mapping(target = "role", ignore = true)
    ClientDTO clientToClientDTO(Client client);

    @Mapping(target = "role", ignore = true)
    Client clientDTOToClient(ClientDTO clientDTO);

    @Mapping(target = "role", ignore = true)
    ManagerDTO managerToManagerDTO(Manager manager);

    @Mapping(target = "role", ignore = true)
    Manager managerDTOToManager(ManagerDTO managerDTO);

    @Mapping(target = "role", ignore = true)
    AdminDTO adminToAdminDTO(Admin admin);

    @Mapping(target = "role", ignore = true)
    Admin adminDTOToAdmin(AdminDTO adminDTO);
}
