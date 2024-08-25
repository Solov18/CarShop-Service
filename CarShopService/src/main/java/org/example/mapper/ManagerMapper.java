package org.example.mapper;

import org.example.dto.ManagerDTO;
import org.example.model.Manager;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManagerMapper {
    ManagerDTO managerToManagerDTO(Manager manager);
    Manager managerDTOToManager(ManagerDTO managerDTO);
}
