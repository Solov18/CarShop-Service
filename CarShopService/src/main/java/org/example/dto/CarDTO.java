package org.example.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {

    private int id;

    @NotBlank(message = "Поле Make не может быть пустым")
    private String make;

    @NotBlank(message = "Model не может быть пустой")
    private String model;

    @NotNull(message = "Год не может быть пустым")
    @Min(value = 1800, message = "Не верно введен год")
    private int year;

    @Positive(message = "Цена должна быть положительной")
    private double price;

    @NotBlank(message = "Condition не может быть пустой")
    private String condition;

    private boolean isAvailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
