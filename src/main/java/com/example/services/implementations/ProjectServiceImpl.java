package com.example.services.implementations;

import static com.example.mapper.ProjectMapper.convertProjectToProjectDto;

import com.example.api.dto.NotAddedUserDto;
import com.example.api.dto.ProjectDto;
import com.example.api.dto.ProjectUsersDto;
import com.example.api.dto.ProjectUsersResponseDto;
import com.example.exceptions.NotCreatorOfProjectException;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectNotFoundException;
import com.example.exceptions.ProjectTypeNotFoundException;
import com.example.exceptions.UserNotFoundException;
import com.example.mapper.ProjectMapper;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.ProjectType;
import com.example.persistence.entities.User;
import com.example.persistence.repositories.ProjectRepository;
import com.example.persistence.repositories.ProjectTypeRepository;
import com.example.persistence.repositories.UserRepository;
import com.example.services.interfaces.ProjectService;
import com.example.services.interfaces.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectTypeRepository projectTypeRepository;
  private final UserRepository userRepository;
  private final UserService userService;

  @Override
  @Transactional
  public ProjectDto saveProject(final ProjectDto projectDto)
      throws ProjectAlreadyExistsException, ProjectTypeNotFoundException, UserNotFoundException {

    checkIfProjectExists(projectDto.projectName());
    val projectType = getProjectType(projectDto.projectType());
    val projectCreator = userService.getUser();

    val project = ProjectMapper.convertProjectDtoToProject(projectDto, projectType, projectCreator);
    val savedProject = projectRepository.save(project);

    log.info("Saved project with id '%s', name '%s' and project creator '%s'"
        .formatted(savedProject.getProjectId(), savedProject.getProjectName(),
            savedProject.getProjectCreator().getUsername()));
    return convertProjectToProjectDto(savedProject);
  }

  @Override
  public List<ProjectDto> findAllProjects() {
    val projectList = projectRepository.findAll();

    if (projectList.isEmpty()) {
      log.warn("No projects found");
      return Collections.emptyList();
    }
    log.info("Returned %s projects".formatted(projectList.size()));
    return projectList.stream().map(ProjectMapper::convertProjectToProjectDto).toList();
  }

  @Override
  @Transactional
  public ProjectUsersResponseDto addUsersToProject(final ProjectUsersDto projectUsersDto) {
    val project = getProject(projectUsersDto.projectName());
    val currentUser = userService.getUser();
    checkIfCurrentUserIsCreatorOfProject(project, currentUser);

    val addedUsers = new ArrayList<String>();
    val notAddedUsers = new ArrayList<NotAddedUserDto>();
    projectUsersDto.usernames().stream().map(username -> userRepository.findByUsername(username).or(() -> {
      notAddedUsers.add(NotAddedUserDto.builder()
          .username(username)
          .reason("Could not find user with username '%s'.".formatted(username))
          .build());
      log.warn("Could not find user with username '%s'.".formatted(username));
      return Optional.empty();
    })).flatMap(Optional::stream).filter(user -> {
      if (project.getProjectUsers().contains(user)) {
        notAddedUsers.add(NotAddedUserDto.builder()
            .username(user.getUsername())
            .reason("User with username '%s' is already added to project '%s'."
                .formatted(user.getUsername(), project.getProjectName()))
            .build());
        log.warn("User with username '%s' is already added to project '%s'."
            .formatted(user.getUsername(), project.getProjectName()));
        return false;
      }
      return true;
    }).forEach(user -> {
      project.getProjectUsers().add(user);
      addedUsers.add(user.getUsername());
      log.info("Added user with username '%s' to project '%s'.".formatted(user.getUsername(), project.getProjectName()));
    });
    projectRepository.save(project);

    return ProjectUsersResponseDto.builder()
        .projectName(projectUsersDto.projectName())
        .addedUsers(addedUsers)
        .notAddedUsers(notAddedUsers)
        .build();
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
    if (optionalProjectType.isEmpty()) {
      log.error("Project type '%s' not found in database.".formatted(projectType));
      throw new ProjectTypeNotFoundException("Project type '%s' not found in database.".formatted(projectType));
    }
    return optionalProjectType.get();
  }

  private Project getProject(final String projectName) {
    val optionalProject = projectRepository.findByProjectName(projectName);
    if (optionalProject.isEmpty()) {
      log.error("Project with name '%s' not found in database.".formatted(projectName));
      throw new ProjectNotFoundException("Project with name '%s' not found in database.".formatted(projectName));
    }
    return optionalProject.get();
  }

  private void checkIfCurrentUserIsCreatorOfProject(final Project project, final User currentUser) {
    if (!project.getProjectCreator().equals(currentUser)) {
      log.error("User '%s' is not the creator of project '%s'."
          .formatted(currentUser.getUsername(), project.getProjectName()));
      throw new NotCreatorOfProjectException("User '%s' is not the creator of project '%s'."
          .formatted(currentUser.getUsername(), project.getProjectName()));
    }
  }
}
