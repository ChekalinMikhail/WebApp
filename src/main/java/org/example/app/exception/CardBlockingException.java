package org.example.app.exception;

public class CardBlockingException extends RuntimeException {
    public CardBlockingException() {
    }

    public CardBlockingException(String message) {
        super(message);
    }

    public CardBlockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardBlockingException(Throwable cause) {
        super(cause);
    }

    public CardBlockingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
