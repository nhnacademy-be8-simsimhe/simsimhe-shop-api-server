package com.simsimbookstore.apiserver.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final String message;

    public CustomException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
