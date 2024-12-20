package com.simsimbookstore.apiserver.users.exception;

public class DuplicateId extends RuntimeException {
    public DuplicateId(String s) {
        super(s);
    }
}
