package com.example.course7.task1.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int userId) {
        super("User not found by id: %s".formatted(userId));
    }
}
