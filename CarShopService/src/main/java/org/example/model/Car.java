package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor

public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String make;
    private String model;
    private int year;
    private double price;
    private String condition;
    private boolean isAvailable;

    public Car(int id, String make, String model, int year, double price, String condition, boolean isAvailable) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.condition = condition;
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }


    @Override
    public String toString() {
        return "Car {" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", condition='" + condition + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}