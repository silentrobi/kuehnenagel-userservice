package com.silentrobi.userservice.service;

import com.silentrobi.userservice.dto.UpsertUserDto;
import com.silentrobi.userservice.dto.UserDto;
import com.silentrobi.userservice.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(UUID id);
    User createUser(UpsertUserDto dto);
    User updateUser(UUID id, UpsertUserDto dto);
    void deleteUser(UUID id);
}
