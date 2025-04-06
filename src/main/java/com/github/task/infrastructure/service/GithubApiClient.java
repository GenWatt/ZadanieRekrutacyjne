package com.github.task.infrastructure.service;

import java.util.List;
import java.util.Optional;

import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;

public interface GithubApiClient {
    /**
     * Fetches repositories for a given GitHub username.
     *
     * @param username The GitHub username.
     * @return A List of Repositories.
     * @throws GithubUserNotFoundException if the user is not found.
     */
    List<Repository> fetchRepositories(String username);

    /**
     * Fetches branches for a given repository.
     *
     * @param username The owner's GitHub username.
     * @param repoName The name of the repository.
     * @return An Optional containing a List of Branches if found, otherwise empty
     *         Optional.
     * @throws GithubApiErrorException for API related errors during branch
     *                                 fetching.
     */
    Optional<List<Branch>> fetchBranches(String username, String repoName);
}