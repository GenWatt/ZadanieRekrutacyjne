package com.github.task.domain.exception;

public class GithubApiErrorException extends RuntimeException {
    public GithubApiErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GithubApiErrorException(String message) {
        super(message);
    }
}