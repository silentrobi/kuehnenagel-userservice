package com.silentrobi.userservice.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UpdateUserDto {
    @NotEmpty(message = "name is required")
    private String name;

    @Min(0)
    @NotNull(message = "age is required")
    private int age;

    @NotEmpty(message = "phoneNumber is required")
    private String phoneNumber;
}
