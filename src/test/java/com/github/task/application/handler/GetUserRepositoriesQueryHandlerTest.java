package com.github.task.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.task.application.dto.RepositoryDto;
import com.github.task.application.query.GetUserRepositoriesQuery;
import com.github.task.domain.exception.UsernameException;
import com.github.task.domain.model.Repository;
import com.github.task.domain.validator.GithubUsernameValidator;
import com.github.task.infrastructure.mapper.RepositoryMapper;
import com.github.task.infrastructure.service.GithubApiClient;
import com.github.task.testData.TestDataFactory;
import com.github.task.testData.TestDataFactory.TestData;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GetUserRepositoriesQueryHandlerTest {

    @Mock
    private GithubApiClient githubApiClient;

    @Mock
    private RepositoryMapper repositoryMapper;

    @Mock
    private GithubUsernameValidator githubUsernameValidator;

    private GetUserRepositoriesQueryHandler queryHandler;

    @BeforeEach
    public void setUp() {
        queryHandler = new GetUserRepositoriesQueryHandler(
                githubApiClient,
                repositoryMapper,
                githubUsernameValidator);
    }

    @AfterEach
    public void tearDown() {
        queryHandler.shutdown();
    }

    @Test
    public void testHandleWithValidInput() {
        // Arrange
        String username = "testuser";
        GetUserRepositoriesQuery query = new GetUserRepositoriesQuery(username);

        TestData testData = TestDataFactory.createStandardTestData();

        when(githubUsernameValidator.isValid(username)).thenReturn(true);
        when(githubApiClient.fetchRepositories(username)).thenReturn(testData.getRepositories());
        when(githubApiClient.fetchBranches(username, "repo1")).thenReturn(Optional.of(testData.getBranchesRepo1()));
        when(githubApiClient.fetchBranches(username, "repo2")).thenReturn(Optional.of(testData.getBranchesRepo2()));
        when(repositoryMapper.toDto(testData.getRepo1(), testData.getBranchesRepo1())).thenReturn(testData.getDto1());
        when(repositoryMapper.toDto(testData.getRepo2(), testData.getBranchesRepo2())).thenReturn(testData.getDto2());

        // Act
        List<RepositoryDto> result = queryHandler.handle(query);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testData.getDto1()));
        assertTrue(result.contains(testData.getDto2()));

        verify(githubApiClient, never()).fetchBranches(username, "forked-repo");
    }

    @Test
    public void testHandleWithInvalidUsername() {
        // Arrange
        String username = "invalid";
        GetUserRepositoriesQuery query = new GetUserRepositoriesQuery(username);
        when(githubUsernameValidator.isValid(username)).thenReturn(false);

        // Act & Assert
        assertThrows(UsernameException.class, () -> queryHandler.handle(query));
        verify(githubApiClient, never()).fetchRepositories(anyString());
    }

    @Test
    public void testHandleWithEmptyRepositories() {
        // Arrange
        String username = "empty-user";
        GetUserRepositoriesQuery query = new GetUserRepositoriesQuery(username);
        when(githubUsernameValidator.isValid(username)).thenReturn(true);
        when(githubApiClient.fetchRepositories(username)).thenReturn(Collections.emptyList());

        // Act
        List<RepositoryDto> result = queryHandler.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testHandleWithMissingBranches() {
        // Arrange
        String username = "testuser";
        GetUserRepositoriesQuery query = new GetUserRepositoriesQuery(username);

        Repository repo = TestDataFactory.createRepository("repo-no-branches", false);
        List<Repository> repos = Collections.singletonList(repo);

        RepositoryDto dto = TestDataFactory.createRepositoryDto("repo-no-branches", "testuser",
                Collections.emptyList());

        when(githubUsernameValidator.isValid(username)).thenReturn(true);
        when(githubApiClient.fetchRepositories(username)).thenReturn(repos);
        when(githubApiClient.fetchBranches(username, "repo-no-branches")).thenReturn(Optional.empty());
        when(repositoryMapper.toDto(eq(repo), anyList())).thenReturn(dto);

        // Act
        List<RepositoryDto> result = queryHandler.handle(query);

        // Assert
        assertEquals(1, result.size());
        assertEquals("repo-no-branches", result.get(0).getRepositoryName());
        assertTrue(result.get(0).getBranches().isEmpty());
    }

    @Test
    public void testHandleWithExceptionFetchingBranches() {
        // Arrange
        String username = "testuser";
        GetUserRepositoriesQuery query = new GetUserRepositoriesQuery(username);

        Repository repo = TestDataFactory.createRepository("problem-repo", false);
        List<Repository> repos = Collections.singletonList(repo);

        RepositoryDto dto = TestDataFactory.createRepositoryDto("problem-repo", "testuser", Collections.emptyList());

        when(githubUsernameValidator.isValid(username)).thenReturn(true);
        when(githubApiClient.fetchRepositories(username)).thenReturn(repos);
        when(githubApiClient.fetchBranches(username, "problem-repo")).thenThrow(new RuntimeException("API error"));
        when(repositoryMapper.toDto(eq(repo), eq(Collections.emptyList()))).thenReturn(dto);

        // Act
        List<RepositoryDto> result = queryHandler.handle(query);

        // Assert
        assertEquals(1, result.size());
        assertEquals("problem-repo", result.get(0).getRepositoryName());
        assertTrue(result.get(0).getBranches().isEmpty());
    }
}