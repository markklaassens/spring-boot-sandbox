package com.example.utilities;

import static com.example.testconfig.TestConstants.CREATOR_USER;
import static com.example.testconfig.TestConstants.PROJECT_DTO;
import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import org.junit.jupiter.api.Test;

class ProjectMapperTest {

  @Test
  void shouldConvertProjectDtoToProjectAndBackToProjectDto() {
    val project = ProjectMapper.convertProjectDtoToProject(PROJECT_DTO, PROJ_TYPE_COLLABORATIVE, CREATOR_USER);
    assertEquals(PROJECT_DTO.projectName(), project.getProjectName());
    assertEquals(PROJECT_DTO.projectDescription(), project.getProjectDescription());
    assertEquals(PROJ_TYPE_COLLABORATIVE, project.getProjectType());
    assertEquals(CREATOR_USER, project.getProjectCreator());
    assertTrue(project.getProjectUsers().isEmpty());

    val projectDto = ProjectMapper.convertProjectToProjectDto(project);
    assertEquals(project.getProjectName(), projectDto.projectName());
    assertEquals(project.getProjectDescription(), projectDto.projectDescription());
    assertEquals(project.getProjectType().getProjectTypeValue(), projectDto.projectType());
  }
}
