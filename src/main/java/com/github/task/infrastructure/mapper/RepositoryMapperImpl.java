package com.github.task.infrastructure.mapper;

import com.github.task.api.dto.RepositoryDto;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class RepositoryMapperImpl implements RepositoryMapper {

    @Override
    public RepositoryDto toDto(Repository repository, List<Branch> branches) {
        Objects.requireNonNull(repository, "Repository cannot be null");
        Objects.requireNonNull(repository.getOwner(), "Repository owner cannot be null");

        List<Branch> safeBranches = branches != null ? branches : Collections.emptyList();

        return new RepositoryDto(
                repository.getName(),
                repository.getOwner().getLogin(),
                safeBranches);
    }
}