package com.github.task.domain.validator;

import org.springframework.stereotype.Component;

@Component
public class GithubUsernameValidator {

    public boolean isValid(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        return true;
    }
}
