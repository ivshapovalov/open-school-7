package com.example.course7.task1.service;

import com.example.course7.task1.dto.CreateUserRequest;
import com.example.course7.task1.dto.UpdateUserRequest;
import com.example.course7.task1.model.User;

public interface UserService {

    User createUser(CreateUserRequest createUserRequest);

    User updateUser(int userId, UpdateUserRequest updateUserRequest);

    void deleteUser(int userId);

    User getUser(int userId);

}
