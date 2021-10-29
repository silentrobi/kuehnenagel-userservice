package com.silentrobi.userservice.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UpdateUserDto {
    @NotEmpty(message = "name is required")
    private String name;

    @DecimalMin(value = "0")
    private int age;

    @Max(15)
    private String phoneNumber;
}
