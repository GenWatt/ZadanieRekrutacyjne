package com.github.task.application.service;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;

import com.github.task.application.dto.RepositoryDto;

public interface GithubService extends DisposableBean {
    public List<RepositoryDto> getUserRepositories(String username);

    public void shutdown();
}
