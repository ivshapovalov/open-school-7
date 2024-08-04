package com.example.course7.task1.aspect;

import com.example.course7.task1.dto.CreateUserRequest;
import com.example.course7.task1.dto.UpdateUserRequest;
import com.example.course7.task1.exception.UserNotFoundException;
import com.example.course7.task1.model.User;
import com.example.course7.task1.service.UserServiceImpl;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"app.logging.enable=true"})
class UserServiceLoggingEnabledTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserServiceImpl userService;

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
    void getUser_WhenUserNotExists_ThrowsException() {
        int userId = 1;
        assertAll(
                () -> assertThrows(UserNotFoundException.class, () -> userService.getUser(userId)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.UserServiceImpl.getUser() with argument[s] = {userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.UserServiceImpl.getUser() with cause = User not found by id: 1"))
        );
    }

    @Test
    @Sql(statements = "INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru')")
    void getUser_WhenUserExists_Ok() {
        int userId = 1;
        User expected = new User(1, "Alex", "al@mail.ru");
        User actual = userService.getUser(userId);
        assertAll(
                () -> assertEquals(expected, actual),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.UserServiceImpl.getUser() with argument[s] = {userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.UserServiceImpl.getUser() with result = User(id=1, name=Alex, email=al@mail.ru)"))
        );
    }

    @Test
    void deleteUser_WhenUserNotExists_ThrowsException() {
        int userId = 1;
        assertAll(
                () -> assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.UserServiceImpl.deleteUser() with argument[s] = {userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.UserServiceImpl.deleteUser() with cause = User not found by id: 1"))
        );
    }

    @Test
    @Sql(statements = "INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru')")
    void deleteUser_WhenUserExists_Ok() {
        int userId = 1;
        userService.deleteUser(userId);
        assertAll(
                () -> assertEquals(0, entityManager.createQuery("select u from User u where u.id=:userId", User.class).setParameter("userId", userId).getResultList().size()),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.UserServiceImpl.deleteUser() with argument[s] = {userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.UserServiceImpl.deleteUser() with result = "))
        );
    }

    @Test
    void updateUser_WhenUserNotExists_ThrowsException() {
        int userId = 1;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("NewName", "NewEmail@mail.com");
        assertAll(
                () -> assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updateUserRequest)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.UserServiceImpl.updateUser() with argument[s] = {updateUserRequest=UpdateUserRequest[name=NewName, email=NewEmail@mail.com], userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("ERROR Exception in com.example.course7.task1.service.UserServiceImpl.updateUser() with cause = User not found by id: 1"))
        );
    }

    @Test
    @Sql(statements = "INSERT INTO users(user_id,name,email) VALUES (1,'Alex','al@mail.ru')")
    void updateUser_WhenUserExists_Ok() {
        int userId = 1;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("NewName", "NewEmail@mail.com");
        User updatedUser = new User(1, "NewName", "NewEmail@mail.com");
        assertAll(
                () -> assertEquals(updatedUser, userService.updateUser(userId, updateUserRequest)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.UserServiceImpl.updateUser() with argument[s] = {updateUserRequest=UpdateUserRequest[name=NewName, email=NewEmail@mail.com], userId=1}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.UserServiceImpl.updateUser() with result = User(id=1, name=NewName, email=NewEmail@mail.com)"))
        );
    }

    @Test
    void createUser_Ok() {
        CreateUserRequest createUserRequest = new CreateUserRequest("NewName", "NewEmail@mail.com");
        User createdUser = new User(1, "NewName", "NewEmail@mail.com");
        assertAll(
                () -> assertEquals(createdUser, userService.createUser(createUserRequest)),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Enter: com.example.course7.task1.service.UserServiceImpl.createUser() with argument[s] = {createUserRequest=CreateUserRequest[name=NewName, email=NewEmail@mail.com]}")),
                () -> assertThat(outContent.toString(),
                        CoreMatchers.containsStringIgnoringCase("DEBUG Exit: com.example.course7.task1.service.UserServiceImpl.createUser() with result = User(id=1, name=NewName, email=NewEmail@mail.com)"))
        );
    }
}
