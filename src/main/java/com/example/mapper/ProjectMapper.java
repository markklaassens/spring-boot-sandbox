package com.example.mapper;

import com.example.api.dto.ProjectDto;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.ProjectType;
import com.example.persistence.entities.User;
import java.util.HashSet;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@SuppressWarnings("java:S1118") // Suppresses Sonar warning which is handled by the @UtilityClass annotation from Lombok
@UtilityClass
public class ProjectMapper {

  /**
   * Converts a {@link ProjectDto} and {@link ProjectType} to a {@link Project} entity.
   *
   * @param projectDto  the project details
   * @param projectType the project type
   * @return a {@link Project} entity
   */
  public static Project convertProjectDtoToProject(
      @NonNull final ProjectDto projectDto,
      @NonNull final ProjectType projectType,
      @NonNull final User projectCreator
  ) {
    return Project.builder()
        .projectName(projectDto.projectName())
        .projectDescription(projectDto.projectDescription())
        .projectType(projectType)
        .projectCreator(projectCreator)
        .projectUsers(new HashSet<>())
        .build();
  }

  /**
   * Converts a {@link Project} entity to a {@link ProjectDto}.
   *
   * @param project the project entity
   * @return a {@link ProjectDto} representation
   */
  public static ProjectDto convertProjectToProjectDto(@NonNull final Project project) {
    return ProjectDto.builder()
        .projectName(project.getProjectName())
        .projectDescription(project.getProjectDescription())
        .projectType(project.getProjectType().getProjectTypeValue())
        .build();
  }
}
