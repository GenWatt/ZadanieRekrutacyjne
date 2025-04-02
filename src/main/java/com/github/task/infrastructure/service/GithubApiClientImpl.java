package com.github.task.infrastructure.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.github.task.domain.Constants;
import com.github.task.domain.exception.GithubApiErrorException;
import com.github.task.domain.exception.UserNotFoundException;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;

@Component
public class GithubApiClientImpl implements GithubApiClient {

    private static final Logger log = LoggerFactory.getLogger(GithubApiClientImpl.class);
    private final RestClient restClient;

    public GithubApiClientImpl(
            @Qualifier(Constants.GITHUB_CLIENT_QUALIFIER) RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Repository> fetchRepositories(String username) {
        log.debug("Fetching repositories for user: {}", username);

        try {
            Repository[] repositories = restClient.get()
                    .uri(Constants.GITHUB_API_REPOS, username)
                    .retrieve()
                    .body(Repository[].class);

            return repositories != null ? Arrays.asList(repositories) : Collections.emptyList();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("User not found: {}", username);
                throw new UserNotFoundException(username);
            }
            log.error("HTTP error while fetching repositories: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error fetching repositories for user {}: {}", username, ex.getMessage(), ex);
            throw new GithubApiErrorException("Error fetching repositories: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<List<Branch>> fetchBranches(String username, String repoName) {
        log.debug("Fetching branches for repository: {}/{}", username, repoName);

        try {
            Branch[] branches = restClient.get()
                    .uri(Constants.GITHUB_API_BRANCHES, username, repoName)
                    .retrieve()
                    .body(Branch[].class);

            return Optional.ofNullable(branches)
                    .map(Arrays::asList)
                    .or(Optional::empty);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Repository or branches not found: {}/{}", username, repoName);
                return Optional.empty();
            }
            log.error("HTTP error while fetching branches: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error fetching branches for {}/{}: {}", username, repoName, ex.getMessage(), ex);
            throw new GithubApiErrorException("Error fetching branches: " + ex.getMessage(), ex);
        }
    }
}