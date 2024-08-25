package org.example.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Order {
    private int id;
    private Car car;
    private Client client;
    private String status;
    private LocalDateTime date;

    public Order(Car car, Client client) {
        this.car = car;
        this.client = client;
        this.status = "created";
        this.date = LocalDateTime.now();
    }


    public Order(int id, Car car, Client client, String status, LocalDateTime date) {
        this.id = id;
        this.car = car;
        this.client = client;
        this.status = status;
        this.date = date;
    }


    @Override
    public String toString() {
        return "Order{" +
                "id " + id +
                ", Автомобиль " + car +
                ", Клиент " + client +
                ", Дата " + date +
                ", Статус  " + status + '\'' +
                '}';
    }
}