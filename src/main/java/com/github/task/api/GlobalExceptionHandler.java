package com.github.task.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.task.application.dto.ErrorDto;
import com.github.task.domain.exception.UserNotFoundException;
import com.github.task.domain.exception.UsernameException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        ErrorDto errorResponse = new ErrorDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDto> handleHttpClientErrorException(HttpClientErrorException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String message = ex.getMessage();

        if (status == HttpStatus.FORBIDDEN && message.contains("rate limit")) {
            logger.warn("Rate limit exceeded: {}", message);
            ErrorDto errorResponse = new ErrorDto(
                    status.value(),
                    "Rate limit exceeded. Please try again later.");
            return new ResponseEntity<>(errorResponse, status);
        }

        logger.error("HTTP client error: {}", message);
        ErrorDto errorResponse = new ErrorDto(
                status.value(),
                message);

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(UsernameException.class)
    public ResponseEntity<ErrorDto> handleUsernameException(UsernameException ex) {
        logger.warn("Invalid username: {}", ex.getMessage());
        ErrorDto errorResponse = new ErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred", ex);

        ErrorDto errorResponse = new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error.");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}