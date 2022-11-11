package ru.magnit.magreportbackend.exception;

public class InvalidExpression extends RuntimeException {
    public InvalidExpression(String message) {
        super(message);
    }

    public InvalidExpression(String message, Throwable cause) {
        super(message, cause);
    }
}
