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

    // Метод для создания Car из ID (заглушка, в реальном приложении будет использоваться сервис)
    default Car mapCarIdToCar(int carId) {
        Car car = new Car();
        car.setId(carId);
        return car;
    }

    // Метод для создания Client из username (заглушка, в реальном приложении будет использоваться сервис)
    default Client mapClientUsernameToClient(String clientUsername) {
        Client client = new Client(clientUsername, "", ""); // Здесь лучше использовать сервис для поиска по username
        return client;
    }
}
