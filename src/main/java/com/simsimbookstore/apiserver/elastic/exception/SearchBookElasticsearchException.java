package com.simsimbookstore.apiserver.elastic.exception;

public class SearchBookElasticsearchException extends RuntimeException {
    public SearchBookElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
