package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client extends User {

    private String contactInfo;
    private int orderCount;

    public Client(String username, String password, String contactInfo) {
        super(username, password, "client");
        this.orderCount = 0;
        this.contactInfo = contactInfo;
    }

    @Override
    public String getRole() {
        return "Client";
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public void increaseOrderCount() {
        this.orderCount++;
    }

    @Override
    public String toString() {
        return super.toString() + ", email: " + contactInfo + ", Заказы: " + orderCount;
    }
}