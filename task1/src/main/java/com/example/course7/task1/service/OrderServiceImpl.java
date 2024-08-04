package com.example.course7.task1.service;

import com.example.course7.task1.aspect.MethodArgument;
import com.example.course7.task1.dto.CreateOrderRequest;
import com.example.course7.task1.dto.UpdateOrderRequest;
import com.example.course7.task1.exception.OrderNotFoundException;
import com.example.course7.task1.model.Order;
import com.example.course7.task1.model.User;
import com.example.course7.task1.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Order createOrder(@MethodArgument(name = "userId") int userId, @MethodArgument(name = "createOrderRequest") CreateOrderRequest createOrderRequest) {
        User user = userService.getUser(userId);
        Order order = new Order(createOrderRequest.description(), createOrderRequest.status(), user);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(@MethodArgument(name = "orderId") int orderId, @MethodArgument(name = "updateOrderRequest") UpdateOrderRequest updateOrderRequest) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setDescription(updateOrderRequest.description());
        order.setStatus(updateOrderRequest.status());
        return order;
    }

    @Override
    @Transactional
    public void deleteOrder(@MethodArgument(name = "orderId") int orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(@MethodArgument(name = "orderId") int orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(@MethodArgument(name = "userId") int userId) {
        User user = userService.getUser(userId);
        Hibernate.initialize(user.getOrders());
        return user.getOrders();
    }
}
