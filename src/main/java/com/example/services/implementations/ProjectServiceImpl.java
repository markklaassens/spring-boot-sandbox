package com.example.services.implementations;

import static com.example.utilities.AuthenticationUtil.getUsername;
import static com.example.utilities.ProjectMapper.convertProjectToProjectDto;

import com.example.api.dto.ProjectDto;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectTypeNotFoundException;
import com.example.exceptions.UserNotFoundException;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.ProjectType;
import com.example.persistence.entities.User;
import com.example.persistence.repositories.ProjectRepository;
import com.example.persistence.repositories.ProjectTypeRepository;
import com.example.persistence.repositories.UserRepository;
import com.example.services.interfaces.ProjectService;
import com.example.utilities.ProjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectTypeRepository projectTypeRepository;
  private final UserRepository userRepository;

  /**
   * Constructs a ProjectServiceImpl with the given repositories.
   *
   * @param projectRepository the repository for managing projects
   * @param projectTypeRepository the repository for managing project types
   * @param userRepository the repository for managing users
   */
  public ProjectServiceImpl(ProjectRepository projectRepository, ProjectTypeRepository projectTypeRepository,
      UserRepository userRepository
  ) {
    this.projectRepository = projectRepository;
    this.projectTypeRepository = projectTypeRepository;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public ProjectDto saveProject(final ProjectDto projectDto)
      throws ProjectAlreadyExistsException, ProjectTypeNotFoundException, UserNotFoundException {

    checkIfProjectExists(projectDto.projectName());
    val projectType = getProjectType(projectDto.projectType());
    val projectCreator = getUser(getUsername());

    Project project = ProjectMapper.convertProjectDtoAndProjectTypeToProject(projectDto, projectType, projectCreator);
    val savedProject = projectRepository.save(project);

    log.info("Saved project with id '%s', name '%s' and project creator '%s'"
        .formatted(savedProject.getProjectId(), savedProject.getProjectName(),
            savedProject.getProjectCreator().getUsername()));
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

  private ProjectType getProjectType(final String projectType) {
    val optionalProjectType = projectTypeRepository.findByProjectTypeValue(projectType);
    if (optionalProjectType.isPresent()) {
      return optionalProjectType.get();
    }
    log.error("Project type '%s' not found in database.".formatted(projectType));
    throw new ProjectTypeNotFoundException("Project type '%s' not found in database.".formatted(projectType));
  }

  private User getUser(final String username) {
    val optionalUser = userRepository.findByUsername(username);
    if (optionalUser.isPresent()) {
      return optionalUser.get();
    }
    log.error("User with username '%s' not found in database.".formatted(username));
    throw new UserNotFoundException("User with username '%s' not found in database.".formatted(username));
  }
}
