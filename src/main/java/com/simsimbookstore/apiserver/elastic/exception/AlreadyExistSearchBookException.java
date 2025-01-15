package com.simsimbookstore.apiserver.elastic.exception;

public class AlreadyExistSearchBookException extends RuntimeException {
    public AlreadyExistSearchBookException(String message) {
        super(message);
    }
}
