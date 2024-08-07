package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class User {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean hasRole(String role) {
        return getRole().equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return "Пользователь{" +
                "Имя : " + username + '\'' +
                ", Роль : " + getRole() + '\'' +
                '}';
    }
}