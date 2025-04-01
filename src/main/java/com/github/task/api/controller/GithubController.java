package com.github.task.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.task.application.dto.RepositoryDto;
import com.github.task.application.service.GithubService;
import com.github.task.domain.Constants;

import java.util.List;

@RestController
@RequestMapping(Constants.API_ROUTE)
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping(Constants.GET_USER_REPOSITORIES_ROUTE)
    public ResponseEntity<List<RepositoryDto>> getUserRepositories(@PathVariable String username) {
        List<RepositoryDto> repositories = githubService.getUserRepositories(username);
        return ResponseEntity.ok(repositories);
    }
}