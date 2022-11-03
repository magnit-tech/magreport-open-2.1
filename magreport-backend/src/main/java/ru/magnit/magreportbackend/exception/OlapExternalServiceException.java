package ru.magnit.magreportbackend.exception;

public class OlapExternalServiceException extends RuntimeException {

    public OlapExternalServiceException(String message) {
        super(message);
    }

    public OlapExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
