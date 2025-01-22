package com.simsimbookstore.apiserver.payment.exception;

public class JsonFailedParsingException extends RuntimeException {
    public JsonFailedParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
