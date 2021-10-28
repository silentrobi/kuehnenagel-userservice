package com.silentrobi.userservice.controller;

import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("users/{id}")
    public User getUserById(@PathVariable(name = "id") UUID id) {
        return userRepository.getById(id);
    }
}
