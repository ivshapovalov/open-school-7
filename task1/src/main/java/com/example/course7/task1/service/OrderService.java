package com.example.course7.task1.service;

import com.example.course7.task1.dto.CreateOrderRequest;
import com.example.course7.task1.dto.UpdateOrderRequest;
import com.example.course7.task1.model.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(int userId, CreateOrderRequest createOrderRequest);

    Order updateOrder(int orderId, UpdateOrderRequest updateOrderRequest);

    void deleteOrder(int orderId);

    Order getOrder(int orderId);

    List<Order> getOrdersByUserId(int userId);
}
