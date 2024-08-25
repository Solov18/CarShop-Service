package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private int id;

    @NotNull(message = "Car ID cannot be null")
    private int carId;

    @NotNull(message = "Client username cannot be null")
    private String clientUsername;

    private String status;
    private LocalDateTime date;
}
