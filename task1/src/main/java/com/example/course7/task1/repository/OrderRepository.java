package com.example.course7.task1.repository;

import com.example.course7.task1.model.Order;
import com.example.course7.task1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByUser(User user);
}
