package com.example.course7.task1.aspect;

import com.example.course7.task1.dto.CreateOrderRequest;
import com.example.course7.task1.dto.UpdateOrderRequest;
import com.example.course7.task1.exception.OrderNotFoundException;
import com.example.course7.task1.exception.UserNotFoundException;
import com.example.course7.task1.model.Order;
import com.example.course7.task1.model.User;
import com.example.course7.task1.model.enums.OrderStatus;
import com.example.course7.task1.service.OrderServiceImpl;
import jakarta.persistence.EntityManager;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"app.logging.enable=true"})
class OrderServiceLoggingEnabledTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setOut(originalOut);
        truncateAll();
    }

    public void truncateAll() {
        Session session = entityManager.unwrap(Session.class);
        session.getSessionFactory().getSchemaManager().truncateMappedObjects();
    }

    @Test
    void getOrder_WhenOrderNotExists_ThrowsException() {
        int orderId = 2;
        assertAll(
                () -> assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.getOrder() with argument[s] = {orderId=2}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.OrderServiceImpl.getOrder() with cause = Order not found by id: 2"))
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru');
            INSERT INTO orders(order_id,user_id,description,status) VALUES (2,1,'Description1','NEW');
            """)
    void getOrder_WhenOrderExists_Ok() {
        int orderId = 2;
        User user = new User(1, "Alex", "al@mail.ru");
        Order expected = new Order(2, "Description1", OrderStatus.NEW, user);
        Order actual = orderService.getOrder(orderId);
        assertAll(
                () -> assertEquals(expected, actual),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.getOrder() with argument[s] = {orderId=2}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.OrderServiceImpl.getOrder() with result = Order(id=2, description=Description1, status=NEW)"))
        );
    }

    @Test
    void deleteOrder_WhenOrderNotExists_ThrowsException() {
        int orderId = 2;
        assertAll(
                () -> assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(orderId)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.deleteOrder() with argument[s] = {orderId=2}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.OrderServiceImpl.deleteOrder() with cause = Order not found by id: 2"))
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru');
            INSERT INTO orders(order_id,user_id,description,status) VALUES (2,1,'Description1','NEW');
            """)
    void deleteOrder_WhenOrderExists_Ok() {
        int orderId = 2;
        orderService.deleteOrder(orderId);
        assertAll(
                () -> assertEquals(0, entityManager.createQuery("select o from Order o where o.id=:orderId", Order.class).setParameter("orderId", orderId).getResultList().size()),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.deleteOrder() with argument[s] = {orderId=2}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.OrderServiceImpl.deleteOrder() with result = "))
        );
    }

    @Test
    void updateOrder_WhenOrderNotExists_ThrowsException() {
        int orderId = 2;
        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest("NewDescription", OrderStatus.IN_PROGRESS);
        assertAll(
                () -> assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(orderId, updateOrderRequest)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.updateOrder() with argument[s] = {orderId=2, updateOrderRequest=UpdateOrderRequest[description=NewDescription, status=IN_PROGRESS]}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.OrderServiceImpl.updateOrder() with cause = Order not found by id: 2"))
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru');
            INSERT INTO orders(order_id,user_id,description,status) VALUES (2,1,'Description1','NEW');
            """)
    void updateOrder_WhenOrderExists_Ok() {
        int orderId = 2;
        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest("NewDescription", OrderStatus.IN_PROGRESS);
        User user = new User(1, "Alex", "al@mail.ru");
        Order updatedOrder = new Order(2, "NewDescription", OrderStatus.IN_PROGRESS, user);
        assertAll(
                () -> assertEquals(updatedOrder, orderService.updateOrder(orderId, updateOrderRequest)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.updateOrder() with argument[s] = {orderId=2, updateOrderRequest=UpdateOrderRequest[description=NewDescription, status=IN_PROGRESS]}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.OrderServiceImpl.updateOrder() with result = Order(id=2, description=NewDescription, status=IN_PROGRESS)"))
        );
    }

    @Test
    void createOrder_WhenUserNotExists_ThrowException() {
        int userId = 1;
        CreateOrderRequest createOrderRequest = new CreateOrderRequest("NewDescription", OrderStatus.NEW);
        assertAll(
                () -> assertThrows(UserNotFoundException.class, () -> orderService.createOrder(userId, createOrderRequest)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.createOrder() with argument[s] = {createOrderRequest=CreateOrderRequest[description=NewDescription, status=NEW], userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.OrderServiceImpl.createOrder() with cause = User not found by id: 1"))
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru');
            """)
    void createOrder_WhenUserExists_Ok() {
        int userId = 1;
        CreateOrderRequest createOrderRequest = new CreateOrderRequest("NewDescription", OrderStatus.NEW);
        User user = new User(1, "Alex", "al@mail.ru");
        Order createdOrder = new Order(1, "NewDescription", OrderStatus.NEW, user);
        assertAll(
                () -> assertEquals(createdOrder, orderService.createOrder(userId, createOrderRequest)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.createOrder() with argument[s] = {createOrderRequest=CreateOrderRequest[description=NewDescription, status=NEW], userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.OrderServiceImpl.createOrder() with result = Order(id=1, description=NewDescription, status=NEW)"))
        );
    }

    @Test
    void getAllOrdersByUserId_WhenUserNotExists_ThrowException() {
        int userId = 1;
        assertAll(
                () -> assertThrows(UserNotFoundException.class, () -> orderService.getOrdersByUserId(userId)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.getOrdersByUserId() with argument[s] = {userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.OrderServiceImpl.getOrdersByUserId() with cause = User not found by id: 1"))
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru');
            INSERT INTO orders(order_id,user_id,description,status) VALUES (1,1,'Description1','NEW');
            INSERT INTO orders(order_id,user_id,description,status) VALUES (2,1,'Description2','NEW');
            INSERT INTO orders(order_id,user_id,description,status) VALUES (3,1,'Description3','NEW');
            """)
    void getAllOrdersByUserId_WhenUserExists_Ok() {
        int userId = 1;

        User user = new User(1, "Alex", "al@mail.ru");
        List<Order> orders = List.of(
                new Order(1, "Description1", OrderStatus.NEW, user),
                new Order(2, "Description2", OrderStatus.NEW, user),
                new Order(3, "Description3", OrderStatus.NEW, user)
        );
        assertAll(
                () -> assertIterableEquals(orders, orderService.getOrdersByUserId(userId)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.getOrdersByUserId() with argument[s] = {userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.OrderServiceImpl.getOrdersByUserId() with result = [Order(id=1, description=Description1, status=NEW), Order(id=2, description=Description2, status=NEW), Order(id=3, description=Description3, status=NEW)]"))
        );
    }
}
