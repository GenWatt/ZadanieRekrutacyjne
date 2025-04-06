package com.github.task.api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.task.application.dto.RepositoryDto;
import com.github.task.application.handler.GetUserRepositoriesQueryHandler;
import com.github.task.application.query.GetUserRepositoriesQuery;
import com.github.task.domain.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping(Constants.API_ROUTE)
@Tag(name = "GitHub API", description = "Endpoints to retrieve GitHub repository information")
public class GithubController {

    private final GetUserRepositoriesQueryHandler getUserRepositoriesQueryHandler;

    public GithubController(GetUserRepositoriesQueryHandler getUserRepositoriesQueryHandler) {
        this.getUserRepositoriesQueryHandler = getUserRepositoriesQueryHandler;
    }

    @GetMapping(value = Constants.GET_USER_REPOSITORIES_ROUTE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user repositories", description = "Retrieves all non-fork repositories with their branches for the specified GitHub username")
    public ResponseEntity<List<RepositoryDto>> getUserRepositories(@PathVariable String username) {
        GetUserRepositoriesQuery query = new GetUserRepositoriesQuery(username);

        List<RepositoryDto> repositories = getUserRepositoriesQueryHandler.handle(query);
        return ResponseEntity.ok(repositories);
    }
}