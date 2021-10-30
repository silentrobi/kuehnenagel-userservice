package com.silentrobi.userservice.exception.errorModel;

import com.silentrobi.userservice.exception.errorModel.Error;

public enum ErrorStatus {
    EMAIL_ALREADY_EXIST(new Error(1000, "email already exist")),
    NOT_FOUND(new Error(1001, "record not found")),
    GENERAL_ERROR(new Error(1002, "unexpected error occurred"));
    private final Error err;

    private ErrorStatus(Error err) {
       this.err = err;
    }

    public Error getError() { return err; }
}
