package ru.magnit.magreportbackend.exception;

public class OlapException extends RuntimeException {

    public OlapException(String message) {
        super(message);
    }

    public OlapException(String message, Throwable cause) {
        super(message, cause);
    }
}
