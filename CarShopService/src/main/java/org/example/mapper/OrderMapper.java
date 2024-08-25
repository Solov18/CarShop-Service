package org.example.mapper;

import org.example.dto.OrderDTO;
import org.example.model.Car;
import org.example.model.Client;
import org.example.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "client.username", target = "clientUsername")
    })
    OrderDTO orderToOrderDTO(Order order);

    @Mappings({
            @Mapping(source = "carId", target = "car.id"),
            @Mapping(source = "clientUsername", target = "client.username")
    })
    Order orderDTOToOrder(OrderDTO orderDTO);


}
