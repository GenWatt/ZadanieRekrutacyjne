package com.github.task.application.handler;

public interface QueryHandler<T, R> {
    R handle(T query);
}