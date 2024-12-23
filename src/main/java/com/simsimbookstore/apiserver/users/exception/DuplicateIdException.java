package com.simsimbookstore.apiserver.users.exception;

public class DuplicateIdException extends RuntimeException {
    public DuplicateIdException(String s) {
        super(s);
    }
}
