package com.silentrobi.userservice.dto;

import lombok.Data;

@Data
public class UpsertUserDto {
    private String name;
    private String email;
    private int age;
    private String phoneNumber;
}
