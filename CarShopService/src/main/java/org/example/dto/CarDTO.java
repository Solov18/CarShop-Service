package org.example.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {

    private int id;

    @NotBlank(message = "Поле Make не может быть пустым")
    private String make;

    @NotBlank(message = "Model не может быть пустой")
    private String model;

    @NotNull(message = "Год не может быть пустым")
    @Min(value = 1800, message = "Не верно введен год")
    private int year;

    @Positive(message = "Цена должна быть положительной")
    private double price;

    @NotBlank(message = "Condition не может быть пустой")
    private String condition;

    private boolean isAvailable;
}
