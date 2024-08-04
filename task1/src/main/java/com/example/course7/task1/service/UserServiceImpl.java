package com.example.course7.task1.service;

import com.example.course7.task1.aspect.MethodArgument;
import com.example.course7.task1.dto.CreateUserRequest;
import com.example.course7.task1.dto.UpdateUserRequest;
import com.example.course7.task1.exception.UserNotFoundException;
import com.example.course7.task1.model.User;
import com.example.course7.task1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(@MethodArgument(name = "createUserRequest") CreateUserRequest createUserRequest) {
        User user = new User(createUserRequest.name(), createUserRequest.email());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(@MethodArgument(name = "userId") int userId, @MethodArgument(name = "updateUserRequest") UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setName(updateUserRequest.name());
        user.setEmail(updateUserRequest.email());
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(@MethodArgument(name = "userId") int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(@MethodArgument(name = "userId") int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
