package org.example.exception;
/**
 * Исключение, которое выбрасывается, когда автомобиль не найден.
 */
public class CarNotFoundException extends RuntimeException {

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение об ошибке.
     */
    public CarNotFoundException(String message) {
        super(message);
    }
}