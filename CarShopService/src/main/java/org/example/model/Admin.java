package org.example.model;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}