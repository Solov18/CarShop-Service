package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
//@NoArgsConstructor
public class ClientDTO extends UserDTO {
    private int id;  // Добавляем id
    private String contactInfo;
    private int orderCount;

    public ClientDTO(int id, String username, String role, String contactInfo, int orderCount) {
        super(username, role);
        this.id = id;
        this.contactInfo = contactInfo;
        this.orderCount = orderCount;
    }
    public int getId() {  // Добавляем геттер для id
        return id;
    }

    public void setId(int id) {  // Добавляем сеттер для id
        this.id = id;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}