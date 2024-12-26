package com.simsimbookstore.apiserver.books.book.exception;

public class BookOutOfStockException extends RuntimeException{
    public BookOutOfStockException(String message) {
        super(message);
    }
}
