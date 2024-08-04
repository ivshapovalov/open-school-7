package com.example.course7.task1.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(int orderId) {
        super("Order not found by id: %s".formatted(orderId));
    }
}
