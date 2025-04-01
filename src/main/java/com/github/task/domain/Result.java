package com.github.task.domain;

import java.util.function.Function;

public class Result<T, E> {
    private final T value;
    private final E error;
    private final boolean isSuccess;

    private Result(T value, E error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getValue() {
        if (!isSuccess)
            throw new IllegalStateException("Cannot get value from failure result");
        return value;
    }

    public E getError() {
        if (isSuccess)
            throw new IllegalStateException("Cannot get error from success result");
        return error;
    }

    public <U> Result<U, E> map(Function<T, U> mapper) {
        return isSuccess ? success(mapper.apply(value)) : failure(error);
    }
}