package com.example.course7.task1.dto;


import com.example.course7.task1.model.enums.OrderStatus;

public record CreateOrderRequest(String description, OrderStatus status) {
}
