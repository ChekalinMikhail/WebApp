package org.example.app.exception;

public class NoAccessRightsToCardException extends RuntimeException {
    public NoAccessRightsToCardException() {
    }

    public NoAccessRightsToCardException(String message) {
        super(message);
    }

    public NoAccessRightsToCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAccessRightsToCardException(Throwable cause) {
        super(cause);
    }

    public NoAccessRightsToCardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
