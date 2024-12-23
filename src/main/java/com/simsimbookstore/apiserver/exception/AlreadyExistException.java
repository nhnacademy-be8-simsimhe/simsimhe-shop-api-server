package com.simsimbookstore.apiserver.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistException extends CustomException {

    public AlreadyExistException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
