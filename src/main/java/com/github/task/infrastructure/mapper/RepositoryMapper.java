package com.github.task.infrastructure.mapper;

import com.github.task.api.dto.RepositoryDto;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;

import java.util.List;

public interface RepositoryMapper {
    RepositoryDto toDto(Repository repository, List<Branch> branches);
}