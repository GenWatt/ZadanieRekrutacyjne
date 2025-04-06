package com.github.task.domain.exception;

public class UsernameException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UsernameException(String message) {
        super(message);
    }

    public UsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
