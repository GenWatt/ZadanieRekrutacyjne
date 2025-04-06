package com.github.task.infrastructure.mapper;

import com.github.task.application.dto.RepositoryDto;
import com.github.task.domain.model.Branch;
import com.github.task.domain.model.Repository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RepositoryMapperImpl implements RepositoryMapper {

    @Override
    public RepositoryDto toDto(Repository repository, List<Branch> branches) {
        List<Branch> safeBranches = branches != null ? branches : Collections.emptyList();

        return new RepositoryDto(
                repository.getName(),
                repository.getOwner().getLogin(),
                safeBranches);
    }
}