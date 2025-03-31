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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.task.domain.Constants;
import com.github.task.domain.exception.GithubApiErrorException;
import com.github.task.domain.exception.UserNotFoundException;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;

@Component
public class RestTemplateGithubApiClient implements GithubApiClient {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateGithubApiClient.class);

    private final RestTemplate restTemplate;

    public RestTemplateGithubApiClient(
            @Qualifier(Constants.GITHUB_TEMPLATE_QUALIFIER) RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public List<Repository> fetchRepositories(String username) {
        log.debug("Fetching repositories for user: {}", username);
        try {
            Repository[] repositories = restTemplate.getForObject(
                    Constants.GITHUB_API_REPOS,
                    Repository[].class,
                    username);

            return repositories != null ? Arrays.asList(repositories) : Collections.emptyList();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("GitHub user not found: {}", username, ex);
                throw new UserNotFoundException(": " + username);
            }

            log.error("Error fetching repositories for user {}: {}", username, ex.getMessage(), ex);
            throw ex;
        } catch (RestClientException ex) {
            log.error("RestClientException while fetching repositories for user {}: {}", username, ex.getMessage(), ex);
            throw new GithubApiErrorException(
                    "Network or client error while fetching repositories for user " + username, ex);
        }
    }

    @Override
    public Optional<List<Branch>> fetchBranches(String username, String repoName) {
        log.debug("Fetching branches for repository: {}/{}", username, repoName);
        try {
            Branch[] branches = restTemplate.getForObject(
                    Constants.GITHUB_API_BRANCHES, Branch[].class,
                    username,
                    repoName);

            return Optional.ofNullable(branches)
                    .map(Arrays::asList)
                    .or(Optional::empty);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Branches not found (or repo not found) for {}/{}: {}", username, repoName, ex.getMessage());
                return Optional.empty();
            }

            log.error("Error fetching branches for repository {}/{}: {}", username, repoName, ex.getMessage(), ex);

            throw ex;
        } catch (RestClientException ex) {
            log.error("RestClientException while fetching branches for {}/{}: {}", username, repoName, ex.getMessage(),
                    ex);

            return Optional.empty();
        }
    }
}