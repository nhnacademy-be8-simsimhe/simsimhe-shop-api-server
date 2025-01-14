package com.simsimbookstore.apiserver.elastic.exception;

public class AlreadyExistSearchBook extends RuntimeException {
    public AlreadyExistSearchBook(String message) {
        super(message);
    }
}
