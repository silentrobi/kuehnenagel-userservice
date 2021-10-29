package com.silentrobi.userservice.exception;

import com.silentrobi.userservice.exception.errorModel.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity notFoundException(NotFoundException notFoundException) {
        return new ResponseEntity(ErrorStatus.NOT_FOUND.getError(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AlreadyExistException.class)
    public ResponseEntity alreadyExistException(AlreadyExistException alreadyExistException) {
        return new ResponseEntity(ErrorStatus.ALREADY_EXIST.getError(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> generalException(Exception exception) {
        return new ResponseEntity<>(ErrorStatus.GENERAL_ERROR.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}