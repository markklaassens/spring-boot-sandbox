package com.example.mappers;

import com.example.api.dto.ProjectDto;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.ProjectType;
import lombok.experimental.UtilityClass;

@SuppressWarnings("java:S1118")
@UtilityClass
public class ProjectMapper {

  /**
   * Converts a {@link ProjectDto} and a {@link ProjectType} into a {@link Project} entity.
   *
   * <p>This method constructs a {@link Project} by mapping the fields from the provided
   * {@link ProjectDto} and associating it with the given {@link ProjectType}.
   *
   * @param projectDto  the data transfer object containing project details.
   * @param projectType the type of the project.
   * @return a {@link Project} entity built from the provided DTO and project type.
   */
  public static Project convertProjectDtoAndProjectTypeToProject(
      final ProjectDto projectDto,
      final ProjectType projectType
  ) {
    return Project.builder()
        .projectName(projectDto.projectName())
        .projectDescription(projectDto.projectDescription())
        .projectType(projectType)
        .build();
  }

  /**
   * Converts a {@link Project} entity into a {@link ProjectDto}.
   *
   * <p>This method maps the fields from the provided {@link Project} to a new {@link ProjectDto},
   * extracting and converting the necessary details.
   *
   * @param project the {@link Project} entity to convert.
   * @return a {@link ProjectDto} representing the provided project entity.
   */
  public static ProjectDto convertProjectToProjectDto(final Project project) {
    return ProjectDto.builder()
        .projectName(project.getProjectName())
        .projectDescription(project.getProjectDescription())
        .projectType(project.getProjectType().getProjectTypeValue())
        .build();
  }
}
