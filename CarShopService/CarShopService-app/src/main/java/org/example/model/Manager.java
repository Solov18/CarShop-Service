package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Manager extends User {
    public Manager(String username, String password) {
        super(username, password, "Manager");
    }

    @Override
    public String getRole() {
        return "Manager";
    }
}