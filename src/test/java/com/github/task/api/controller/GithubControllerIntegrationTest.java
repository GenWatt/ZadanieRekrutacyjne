package com.github.task.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.github.task.api.dto.ErrorDto;
import com.github.task.api.dto.RepositoryDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GithubControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String buildUrl(String username) {
        return "http://localhost:" + port + "/api/users/" + username + "/repositories";
    }

    private ParameterizedTypeReference<List<RepositoryDto>> getParameterizedTypeReference = new ParameterizedTypeReference<>() {
    };

    @Test
    @DisplayName("When valid username, then repositories should be returned")
    public void whenValidUsername_thenRepositoriesShouldBeReturned() {
        // Given
        String username = "octocat";

        // When
        ResponseEntity<List<RepositoryDto>> response = restTemplate.exchange(
                buildUrl(username),
                HttpMethod.GET,
                null,
                getParameterizedTypeReference);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected HTTP status 200 OK");
        List<RepositoryDto> repositories = response.getBody();
        assertNotNull(repositories, "Repositories list should not be null");
        assertFalse(repositories.isEmpty(), "Repositories list should not be empty");

        repositories.forEach(repo -> {
            assertNotNull(repo.getRepositoryName(), "Repository name should not be null");
            assertNotNull(repo.getOwnerLogin(), "Owner login should not be null");
            assertEquals(username, repo.getOwnerLogin(), "Owner login should match the provided username");

            repo.getBranches().forEach(branch -> {
                assertNotNull(branch.getName(), "Branch name should not be null");
                assertNotNull(branch.getCommit(), "Commit object should not be null");
                assertNotNull(branch.getCommit().getSha(), "Commit SHA should not be null");
            });
        });
    }

    @Test
    @DisplayName("When invalid username, then NotFound error should be returned")
    public void whenInvalidUsername_thenNotFoundErrorShouldBeReturned() {
        // Given
        String invalidUsername = "this-user-does-not-exist-12345-abcdef";

        // When
        ResponseEntity<ErrorDto> response = restTemplate.getForEntity(
                buildUrl(invalidUsername),
                ErrorDto.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected HTTP status 404 NOT FOUND");
        ErrorDto errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response should not be null");
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus(), "Error status should be 404");
        assertTrue(errorResponse.getMessage().contains(invalidUsername),
                "Error message should contain the invalid username");
    }
}
