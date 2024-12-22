package com.simsimbookstore.apiserver.books.category.exception;

public class ChildCategoryExistException extends RuntimeException{

    public ChildCategoryExistException() {
    }

    public ChildCategoryExistException(Throwable cause) {
        super(cause);
    }

    public ChildCategoryExistException(String message) {
        super(message);
    }

    public ChildCategoryExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChildCategoryExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
