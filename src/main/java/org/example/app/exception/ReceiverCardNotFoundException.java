package org.example.app.exception;

public class ReceiverCardNotFoundException extends RuntimeException {
    public ReceiverCardNotFoundException() {
    }

    public ReceiverCardNotFoundException(String message) {
        super(message);
    }

    public ReceiverCardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiverCardNotFoundException(Throwable cause) {
        super(cause);
    }

    public ReceiverCardNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
