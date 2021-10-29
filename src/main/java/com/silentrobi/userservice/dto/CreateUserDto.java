package com.silentrobi.userservice.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreateUserDto {
    @NotEmpty(message = "name is required")
    private String name;

    @NotEmpty(message = "email is required")
    @Email(message = "invalid email")
    private String email;

    @DecimalMin(value = "0")
    private int age;

    @Max(15)
    private String phoneNumber;
}
