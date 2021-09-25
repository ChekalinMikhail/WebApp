package org.example.app.exception;

public class CarBlockingException extends RuntimeException {
    public CarBlockingException() {
    }

    public CarBlockingException(String message) {
        super(message);
    }

    public CarBlockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CarBlockingException(Throwable cause) {
        super(cause);
    }

    public CarBlockingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
