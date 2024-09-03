package org.example.exception;

/**
 * Исключение, которое выбрасывается, когда пытаются зарегистрировать пользователя с уже существующим именем.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
