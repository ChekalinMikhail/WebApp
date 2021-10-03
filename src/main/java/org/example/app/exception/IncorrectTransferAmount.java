package org.example.app.exception;

public class IncorrectTransferAmount extends RuntimeException {
    public IncorrectTransferAmount() {
    }

    public IncorrectTransferAmount(String message) {
        super(message);
    }

    public IncorrectTransferAmount(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTransferAmount(Throwable cause) {
        super(cause);
    }

    public IncorrectTransferAmount(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
