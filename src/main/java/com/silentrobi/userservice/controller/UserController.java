package com.silentrobi.userservice.controller;

import com.silentrobi.userservice.exception.AlreadyExistException;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import com.silentrobi.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List> getAllUsers() throws NotFoundException {
        return new ResponseEntity<List>( userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody User user) throws AlreadyExistException {
        return new ResponseEntity(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping("users/{id}")
    public ResponseEntity getUser(@PathVariable(name = "id") UUID id) throws NotFoundException {
        return new ResponseEntity( userService.getUserById(id), HttpStatus.OK);
    }

    @PutMapping("users/{id}")
    public ResponseEntity updateUser(@PathVariable(name = "id") UUID id, @RequestBody User user) throws NotFoundException {
        return new ResponseEntity( userService.updateUser(id, user), HttpStatus.OK);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity deleteUser(@PathVariable(name = "id") UUID id) throws NotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
