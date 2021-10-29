package com.silentrobi.userservice.service;

import com.silentrobi.userservice.dto.UpdateUserDto;
import com.silentrobi.userservice.dto.CreateUserDto;
import com.silentrobi.userservice.dto.UserDto;
import com.silentrobi.userservice.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(UUID id);
    User createUser(CreateUserDto dto);
    User updateUser(UUID id, UpdateUserDto dto);
    void deleteUser(UUID id);
}
