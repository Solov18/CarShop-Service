package org.example.mapper;

import org.example.dto.AuthenticationDTO;
import org.example.dto.ManagerDTO;
import org.example.model.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ManagerMapper {

    ManagerDTO managerToManagerDTO(Manager manager);
    Manager managerDTOToManager(ManagerDTO managerDTO);


    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Manager authenticationDTOToManager(AuthenticationDTO authenticationDTO);
}
