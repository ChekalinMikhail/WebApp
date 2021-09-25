package org.example.app.exception;

public class WrongPasswordRecoveryKeyException extends RuntimeException {
    public WrongPasswordRecoveryKeyException() {
    }

    public WrongPasswordRecoveryKeyException(String message) {
        super(message);
    }

    public WrongPasswordRecoveryKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongPasswordRecoveryKeyException(Throwable cause) {
        super(cause);
    }

    public WrongPasswordRecoveryKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
