package com.silentrobi.userservice.exception;

import com.silentrobi.userservice.exception.errorModel.ErrorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity notFoundException() {
        return new ResponseEntity(ErrorStatus.NOT_FOUND.getError(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EmailAlreadyExistException.class)
    public ResponseEntity alreadyExistException() {
        return new ResponseEntity(ErrorStatus.EMAIL_ALREADY_EXIST.getError(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> generalException() {
        return new ResponseEntity(ErrorStatus.GENERAL_ERROR.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }
}