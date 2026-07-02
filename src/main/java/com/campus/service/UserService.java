package com.campus.service;

import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User register(User user) {
        User exist = userMapper.findByUsername(user.getUsername());
        if (exist != null) {
            return null;
        }
        if (user.getRole() == null) {
            user.setRole("user");
        }
        userMapper.insert(user);
        return user;
    }

    public User profile(Long userId) {
        return userMapper.findById(userId);
    }
}
