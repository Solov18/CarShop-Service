package org.example.mapper;

import org.example.dto.AdminDTO;
import org.example.dto.AuthenticationDTO;
import org.example.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminDTO adminToAdminDTO(Admin admin);
    Admin adminDTOToAdmin(AdminDTO adminDTO);




    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Admin authenticationDTOToAdmin(AuthenticationDTO authenticationDTO);
}
