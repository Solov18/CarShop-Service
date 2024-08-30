package org.example.mapper;

import org.example.dto.CarDTO;
import org.example.model.Car;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CarMapper {
    CarDTO carToCarDTO(Car car);
    Car carDTOToCar(CarDTO carDTO);
}