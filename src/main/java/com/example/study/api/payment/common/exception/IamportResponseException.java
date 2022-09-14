package com.example.study.api.payment.common.exception;

public class IamportResponseException extends RuntimeException {
    public IamportResponseException() {
    }

    public IamportResponseException(String message) {
        super(message);
    }

    public IamportResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public IamportResponseException(Throwable cause) {
        super(cause);
    }

    public IamportResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
