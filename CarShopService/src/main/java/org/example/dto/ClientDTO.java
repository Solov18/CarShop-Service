package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDTO extends UserDTO {
    private String contactInfo;
    private int orderCount;
}