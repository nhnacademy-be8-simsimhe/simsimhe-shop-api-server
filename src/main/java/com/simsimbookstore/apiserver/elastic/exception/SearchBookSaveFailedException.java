package com.simsimbookstore.apiserver.elastic.exception;

public class SearchBookSaveFailedException extends RuntimeException {
    public SearchBookSaveFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
