package com.tanmay.makemytrip_backend.user.exception;

import com.tanmay.makemytrip_backend.common.exception.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.tanmay.makemytrip_backend.user.controller.UserController;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ApiErrorResponse handleUserNotFound(
            UserNotFoundException exception) {

        return new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now(),
                null
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ApiErrorResponse handleUserAlreadyExists(
            UserAlreadyExistsException exception) {

        return new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                LocalDateTime.now(),
                null
        );
    }
}