package ru.magnit.magreportbackend.exception;

public class OlapMaxDataVolumeExceeded extends RuntimeException {

    public OlapMaxDataVolumeExceeded(String message) {
        super(message);
    }

    public OlapMaxDataVolumeExceeded(String message, Throwable cause) {
        super(message, cause);
    }
}
