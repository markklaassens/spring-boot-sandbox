package com.example.mappers;

import static com.example.testconfig.TestConstants.PROJECT_DTO;
import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import org.junit.jupiter.api.Test;

class ProjectMapperTest {

  @Test
  void shouldConvertProjectDtoToProjectAndBackToProjectDto() {
    val project = ProjectMapper.convertProjectDtoAndProjectTypeToProject(PROJECT_DTO, PROJ_TYPE_COLLABORATIVE);
    assertEquals(PROJECT_DTO.projectName(), project.getProjectName());
    assertEquals(PROJECT_DTO.projectDescription(), project.getProjectDescription());
    assertEquals(PROJECT_DTO.projectType(), project.getProjectType().getProjectTypeValue());

    val projectDto = ProjectMapper.convertProjectToProjectDto(project);
    assertEquals(project.getProjectName(), projectDto.projectName());
    assertEquals(project.getProjectDescription(), projectDto.projectDescription());
    assertEquals(project.getProjectType().getProjectTypeValue(), projectDto.projectType());
  }
}
