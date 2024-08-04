package com.example.course7.task1.model;

import com.example.course7.task1.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@ToString(exclude = "user")
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")

    private Integer id;
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    User user;

    public Order(String description, OrderStatus status, User user) {
        this.description = description;
        this.status = status;
        this.user = user;
    }
}
