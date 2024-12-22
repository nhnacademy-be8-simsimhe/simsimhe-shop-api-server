package com.simsimbookstore.apiserver.books.contributor.error;

public class ContributorNotFoundException extends RuntimeException{
    public ContributorNotFoundException() {
    }

    public ContributorNotFoundException(Throwable cause) {
        super(cause);
    }

    public ContributorNotFoundException(String message) {
        super(message);
    }

    public ContributorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContributorNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
