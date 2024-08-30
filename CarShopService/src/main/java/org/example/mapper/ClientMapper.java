package org.example.mapper;

import org.example.dto.AuthenticationDTO;
import org.example.dto.ClientDTO;
import org.example.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO clientToClientDTO(Client client);

    Client clientDTOToClient(ClientDTO clientDTO);


    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Client authenticationDTOToClient(AuthenticationDTO authenticationDTO);
}
