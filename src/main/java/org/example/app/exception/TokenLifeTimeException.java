package org.example.app.exception;

public class TokenLifeTimeException extends RuntimeException {
    public TokenLifeTimeException() {
    }

    public TokenLifeTimeException(String message) {
        super(message);
    }

    public TokenLifeTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenLifeTimeException(Throwable cause) {
        super(cause);
    }

    public TokenLifeTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
