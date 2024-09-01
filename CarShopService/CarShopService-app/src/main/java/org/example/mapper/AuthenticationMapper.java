package org.example.mapper;

import org.example.dto.AuthenticationDTO;
import org.example.model.Admin;
import org.example.model.Client;
import org.example.model.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Admin authenticationDTOToAdmin(AuthenticationDTO authenticationDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Client authenticationDTOToClient(AuthenticationDTO authenticationDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Manager authenticationDTOToManager(AuthenticationDTO authenticationDTO);
}
