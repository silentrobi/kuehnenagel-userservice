package com.silentrobi.userservice.exception.errorModel;

import lombok.Data;

@Data
public class Error {
    private int status;
    private String description;

    public Error(int status, String description) {
        this.status = status;
        this.description = description;
    }
}
