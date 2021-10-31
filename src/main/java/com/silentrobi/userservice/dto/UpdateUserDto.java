package com.silentrobi.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    @NotEmpty(message = "name is required")
    private String name;

    @Min(0)
    @NotNull(message = "age is required")
    private Integer age;

    @NotEmpty(message = "phoneNumber is required")
    private String phoneNumber;
}
