package org.example.model;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int idCounter = 0;
    private int id;
    private Car car;
    private Client client;
    private LocalDateTime date;
    private String status; // statuses: created, confirmed, completed, cancelled

    public Order(Car car, Client client) {
        this.id = idCounter++;
        this.car = car;
        this.client = client;
        this.status = "created";
        this.date = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public Client getClient() {
        return client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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