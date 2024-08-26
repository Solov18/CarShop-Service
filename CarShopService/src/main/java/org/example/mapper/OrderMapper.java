package org.example.mapper;

import org.example.dto.OrderDTO;
import org.example.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CarMapper.class, ClientMapper.class})
public interface OrderMapper {


    OrderDTO orderToOrderDTO(Order order);


}
