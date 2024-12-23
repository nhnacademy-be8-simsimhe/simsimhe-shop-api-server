package com.simsimbookstore.apiserver.orders.orderbook.exception;

public class OrderBookNotFoundException extends RuntimeException {
    public OrderBookNotFoundException(String message) {
        super(message);
    }
}
