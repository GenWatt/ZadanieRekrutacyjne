package com.github.task.application.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.task.application.dto.RepositoryDto;
import com.github.task.application.query.GetUserRepositoriesQuery;
import com.github.task.domain.exception.UsernameException;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;
import com.github.task.domain.validator.GithubUsernameValidator;
import com.github.task.infrastructure.mapper.RepositoryMapper;
import com.github.task.infrastructure.service.GithubApiClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class GetUserRepositoriesQueryHandler
        implements QueryHandler<GetUserRepositoriesQuery, List<RepositoryDto>>, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(GetUserRepositoriesQueryHandler.class);
    private final GithubApiClient githubApiClient;
    private final RepositoryMapper repositoryMapper;
    private final ExecutorService executorService;
    private final GithubUsernameValidator githubUsernameValidator;

    public GetUserRepositoriesQueryHandler(
            GithubApiClient githubApiClient,
            RepositoryMapper repositoryMapper,
            GithubUsernameValidator githubUsernameValidator) {

        this.githubApiClient = githubApiClient;
        this.repositoryMapper = repositoryMapper;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.githubUsernameValidator = githubUsernameValidator;
    }

    @Override
    public List<RepositoryDto> handle(GetUserRepositoriesQuery query) {
        String username = query.getUsername();

        if (!githubUsernameValidator.isValid(username)) {
            throw new UsernameException("Username cannot be null or empty");
        }

        List<Repository> repositories = githubApiClient.fetchRepositories(username);

        List<Repository> nonForkRepositories = repositories.stream()
                .filter(repository -> !repository.isFork())
                .collect(Collectors.toUnmodifiableList());

        List<CompletableFuture<RepositoryDto>> futures = nonForkRepositories.stream()
                .map(repository -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Optional<List<Branch>> branchesOpt = githubApiClient.fetchBranches(username,
                                repository.getName());
                        List<Branch> branches = branchesOpt.orElse(Collections.emptyList());

                        return repositoryMapper.toDto(repository, branches);
                    } catch (Exception e) {
                        logger.error("Error fetching branches for repository: {}", repository.getName(), e);
                        return repositoryMapper.toDto(repository, Collections.emptyList());
                    }
                }, executorService))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toUnmodifiableList());
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}