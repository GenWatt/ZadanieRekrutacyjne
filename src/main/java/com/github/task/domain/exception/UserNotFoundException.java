package com.github.task.domain.exception;

import com.github.task.domain.Constants;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(Constants.USER_NOT_FOUND_MESSAGE + message);
    }
}