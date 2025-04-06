package com.github.task.testData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.task.application.dto.RepositoryDto;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Commit;
import com.github.task.domain.model.Owner;
import com.github.task.domain.model.Repository;

public class TestDataFactory {

    public static Repository createRepository(String name, boolean fork) {
        Repository repo = new Repository();
        repo.setName(name);
        repo.setFork(fork);
        repo.setOwner(createOwner("testuser"));
        return repo;
    }

    public static Owner createOwner(String login) {
        Owner owner = new Owner();
        owner.setLogin(login);

        return owner;
    }

    public static Branch createBranch(String name, String sha) {
        Branch branch = new Branch();
        branch.setName(name);

        Commit commit = new Commit();
        commit.setSha(sha);
        branch.setCommit(commit);

        return branch;
    }

    public static RepositoryDto createRepositoryDto(String name, String owner, List<Branch> branches) {
        return new RepositoryDto(name, owner, branches);
    }

    public static Branch createBranchDto(String name, String sha) {
        return new Branch(name, new Commit(sha));
    }

    public static TestData createStandardTestData() {
        Repository repo1 = createRepository("repo1", false);
        Repository repo2 = createRepository("repo2", false);
        Repository forkedRepo = createRepository("forked-repo", true);

        List<Branch> branchesRepo1 = Arrays.asList(
                createBranch("main", "abc123"),
                createBranch("develop", "def456"));

        List<Branch> branchesRepo2 = Collections.singletonList(
                createBranch("master", "789xyz"));

        List<Branch> branchDtosRepo1 = Arrays.asList(
                createBranchDto("main", "abc123"),
                createBranchDto("develop", "def456"));

        List<Branch> branchDtosRepo2 = Collections.singletonList(
                createBranchDto("master", "789xyz"));

        RepositoryDto dto1 = createRepositoryDto("repo1", "testuser", branchDtosRepo1);
        RepositoryDto dto2 = createRepositoryDto("repo2", "testuser", branchDtosRepo2);

        return new TestData(
                Arrays.asList(repo1, repo2, forkedRepo),
                branchesRepo1,
                branchesRepo2,
                dto1,
                dto2);
    }

    public static class TestData {
        private final List<Repository> repositories;
        private final List<Branch> branchesRepo1;
        private final List<Branch> branchesRepo2;
        private final RepositoryDto dto1;
        private final RepositoryDto dto2;

        public TestData(List<Repository> repositories, List<Branch> branchesRepo1,
                List<Branch> branchesRepo2, RepositoryDto dto1, RepositoryDto dto2) {
            this.repositories = repositories;
            this.branchesRepo1 = branchesRepo1;
            this.branchesRepo2 = branchesRepo2;
            this.dto1 = dto1;
            this.dto2 = dto2;
        }

        public List<Repository> getRepositories() {
            return repositories;
        }

        public List<Branch> getBranchesRepo1() {
            return branchesRepo1;
        }

        public List<Branch> getBranchesRepo2() {
            return branchesRepo2;
        }

        public RepositoryDto getDto1() {
            return dto1;
        }

        public RepositoryDto getDto2() {
            return dto2;
        }

        public Repository getRepo1() {
            return repositories.get(0);
        }

        public Repository getRepo2() {
            return repositories.get(1);
        }

        public Repository getForkedRepo() {
            return repositories.get(2);
        }
    }
}