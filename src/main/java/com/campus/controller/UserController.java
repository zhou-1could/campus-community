package com.campus.controller;

import com.campus.dto.LoginDTO;
import com.campus.dto.Result;
import com.campus.entity.User;
import com.campus.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        return Result.ok(user);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User created = userService.register(user);
        if (created == null) {
            return Result.fail("用户名已存在");
        }
        return Result.ok(created);
    }

    @GetMapping("/profile")
    public Result<User> profile(@RequestParam Long userId) {
        User user = userService.profile(userId);
        return user != null ? Result.ok(user) : Result.fail("用户不存在");
    }
}
