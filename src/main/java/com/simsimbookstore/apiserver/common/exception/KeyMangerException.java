package com.simsimbookstore.apiserver.common.exception;

public class KeyMangerException extends RuntimeException {
    public KeyMangerException(String message, Exception e) {
        super(message, e);
    }
}