package com.simsimbookstore.apiserver.storage.exception;

public class ObjectStorageException extends RuntimeException {
    public ObjectStorageException(String message) {
        super(message);
    }
}