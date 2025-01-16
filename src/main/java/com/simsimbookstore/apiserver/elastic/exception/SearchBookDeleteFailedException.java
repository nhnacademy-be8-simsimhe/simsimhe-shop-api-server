package com.simsimbookstore.apiserver.elastic.exception;

public class SearchBookDeleteFailedException extends RuntimeException {
    public SearchBookDeleteFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
