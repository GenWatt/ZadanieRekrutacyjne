package com.github.task.application.query;

public class GetUserRepositoriesQuery {
    private final String username;

    public GetUserRepositoriesQuery(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}