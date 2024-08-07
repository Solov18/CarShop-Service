package org.example.model;

public class Manager extends User {
    public Manager(String username, String password) {
        super(username, password, "Manager");
    }

    @Override
    public String getRole() {
        return "Manager";
    }
}