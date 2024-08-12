package com.example.services.implementations;

import static com.example.mappers.ProjectMapper.convertProjectToProjectDto;

import com.example.api.dto.ProjectDto;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectTypeNotFoundException;
import com.example.mappers.ProjectMapper;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.ProjectType;
import com.example.persistence.repositories.ProjectRepository;
import com.example.persistence.repositories.ProjectTypeRepository;
import com.example.services.interfaces.ProjectService;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectTypeRepository projectTypeRepository;

  public ProjectServiceImpl(
      ProjectRepository projectRepository,
      ProjectTypeRepository projectTypeRepository
  ) {
    this.projectRepository = projectRepository;
    this.projectTypeRepository = projectTypeRepository;
  }

  @Override
  public ProjectDto saveProject(final ProjectDto projectDto)
      throws ProjectAlreadyExistsException, ProjectTypeNotFoundException {

    checkIfProjectExists(projectDto.projectName());
    val projectType = checkIfProjectTypeIsPresentAndReturn(projectDto.projectType());
    Project project = ProjectMapper.convertProjectDtoAndProjectTypeToProject(projectDto, projectType);
    val savedProject = projectRepository.save(project);

    log.info("Saved project with id '%s' and name '%s'"
        .formatted(savedProject.getProjectId(), savedProject.getProjectName()));
    return convertProjectToProjectDto(savedProject);
  }

  @Override
  public List<ProjectDto> findAllProjects() {
    val projectList = projectRepository.findAll();

    if (!projectList.isEmpty()) {
      log.info("Returned %s projects".formatted(projectList.size()));
      return projectList.stream().map(ProjectMapper::convertProjectToProjectDto).toList();
    }
    log.warn("No projects found");
    return Collections.emptyList();
  }

  private void checkIfProjectExists(final String projectName) {
    if (projectRepository.findByProjectName(projectName).isPresent()) {
      log.warn("Project with project name '%s' already exists in database.".formatted(projectName));
      throw new ProjectAlreadyExistsException(
          "Project with project name '%s' already exists in database.".formatted(projectName));
    }
  }

  private ProjectType checkIfProjectTypeIsPresentAndReturn(final String projectType) {
    val optionalProjectType = projectTypeRepository.findByProjectTypeValue(projectType);
    if (optionalProjectType.isPresent()) {
      return optionalProjectType.get();
    }
    log.error("Project type '%s' not found in database.".formatted(projectType));
    throw new ProjectTypeNotFoundException("Project type '%s' not found in database.".formatted(projectType));
  }
}
