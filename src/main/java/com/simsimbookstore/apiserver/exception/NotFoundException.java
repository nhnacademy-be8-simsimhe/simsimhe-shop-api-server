package com.simsimbookstore.apiserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
