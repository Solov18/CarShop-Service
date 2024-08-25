package org.example.mapper;

import org.example.dto.OrderDTO;
import org.example.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "client.username", target = "clientUsername")
    OrderDTO orderToOrderDTO(Order order);

    @Mapping(source = "carId", target = "car.id")
    @Mapping(source = "clientUsername", target = "client.username")
    Order orderDTOToOrder(OrderDTO orderDTO);
}
