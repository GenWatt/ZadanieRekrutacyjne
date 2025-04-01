package com.github.task.application.dto;

import java.util.List;

import com.github.task.domain.model.Branch;

public class RepositoryDto {
    private String repositoryName;
    private String ownerLogin;
    private List<Branch> branches;

    public RepositoryDto(String repositoryName, String ownerLogin, List<Branch> branches) {
        this.repositoryName = repositoryName;
        this.ownerLogin = ownerLogin;
        this.branches = branches;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}