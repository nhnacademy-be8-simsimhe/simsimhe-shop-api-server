package com.simsimbookstore.apiserver.elastic.exception;

public class SearchBookExistenceCheckFailedException extends RuntimeException {
    public SearchBookExistenceCheckFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
