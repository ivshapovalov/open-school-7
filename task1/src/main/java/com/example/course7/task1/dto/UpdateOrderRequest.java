package com.example.course7.task1.dto;

import com.example.course7.task1.model.enums.OrderStatus;

public record UpdateOrderRequest(String description, OrderStatus status) {
}
