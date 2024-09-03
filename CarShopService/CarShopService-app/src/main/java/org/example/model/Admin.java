package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}