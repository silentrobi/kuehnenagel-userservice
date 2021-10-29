package com.silentrobi.userservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private int age;
    private String phoneNumber;
}
