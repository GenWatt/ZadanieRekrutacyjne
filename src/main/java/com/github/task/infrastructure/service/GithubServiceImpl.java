package com.github.task.infrastructure.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.task.api.dto.RepositoryDto;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;
import com.github.task.infrastructure.mapper.RepositoryMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class GithubServiceImpl implements GithubService {

    private static final Logger logger = LoggerFactory.getLogger(GithubServiceImpl.class);
    private final GithubApiClient githubApiClient;
    private final RepositoryMapper repositoryMapper;
    private final ExecutorService executorService;

    public GithubServiceImpl(
            GithubApiClient githubApiClient,
            RepositoryMapper repositoryMapper) {

        this.githubApiClient = githubApiClient;
        this.repositoryMapper = repositoryMapper;
        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
    }

    public List<RepositoryDto> getUserRepositories(String username) {
        Assert.hasText(username, "Username must not be empty");
        logger.debug("Fetching repositories for user: {}", username);

        List<Repository> repositories = githubApiClient.fetchRepositories(username);

        List<Repository> nonForkRepositories = repositories.stream()
                .filter(repository -> !repository.isFork())
                .collect(Collectors.toUnmodifiableList());

        List<CompletableFuture<RepositoryDto>> futures = nonForkRepositories.stream()
                .map(repository -> CompletableFuture.supplyAsync(() -> {
                    try {
                        logger.debug("Fetching branches for repository: {}", repository.getName());
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
    public void destroy() throws Exception {
        System.out.println("Shutting down executor service...");
        shutdown();
    }
}