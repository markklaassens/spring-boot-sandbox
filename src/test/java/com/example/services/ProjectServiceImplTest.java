package com.example.services;

import static com.example.testconfig.TestConstants.PROJECT;
import static com.example.testconfig.TestConstants.PROJECT_DTO;
import static com.example.testconfig.TestConstants.PROJECT_LIST;
import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.api.dto.ProjectDto;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectTypeNotFoundException;
import com.example.mappers.ProjectMapper;
import com.example.persistence.entities.Project;
import com.example.persistence.repositories.ProjectRepository;
import com.example.persistence.repositories.ProjectTypeRepository;
import com.example.services.implementations.ProjectServiceImpl;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class ProjectServiceImplTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ProjectTypeRepository projectTypeRepository;

  @InjectMocks
  private ProjectServiceImpl projectService;

  @Test
  void shouldSaveProject(CapturedOutput output) {
    when(projectRepository.save(any(Project.class))).thenReturn(PROJECT);
    when(projectTypeRepository.findByProjectTypeValue(PROJECT_DTO.projectType()))
        .thenReturn(Optional.of(PROJ_TYPE_COLLABORATIVE));

    val result = projectService.saveProject(PROJECT_DTO);

    assertEquals(PROJECT_DTO, result);
    verify(projectRepository, times(1)).save(any(Project.class));
    assertThat(output).contains("Saved project with id '%s' and name '%s'"
        .formatted(PROJECT.getProjectId(), PROJECT.getProjectName()));
  }

  @Test
  void shouldFindAllProjects(CapturedOutput output) {
    when(projectRepository.findAll()).thenReturn(PROJECT_LIST);

    val resultList = projectService.findAllProjects();
    val expectedList = PROJECT_LIST.stream().map(ProjectMapper::convertProjectToProjectDto).toList();

    assertEquals(expectedList, resultList);
    assertThat(output).contains("Returned %s projects".formatted(PROJECT_LIST.size()));
  }

  @Test
  void shouldThrowExceptionWhenProjectAlreadyExists(CapturedOutput output) {
    when(projectRepository.findByProjectName(PROJECT_DTO.projectName()))
        .thenReturn(Optional.of(PROJECT));

    val exception = assertThrows(
        ProjectAlreadyExistsException.class,
        () -> projectService.saveProject(PROJECT_DTO)
    );

    assertEquals("Project with project name '%s' already exists in database.".formatted(PROJECT_DTO.projectName()),
        exception.getMessage());
    assertThat(output).contains(
        "Project with project name '%s' already exists in database.".formatted(PROJECT_DTO.projectName()));
    verify(projectRepository, times(1)).findByProjectName(PROJECT_DTO.projectName());
  }

  @Test
  void testSaveProject_UnhappyFlow_ThrowExceptionWhenProjectTypeIsNotFound(CapturedOutput output) {
    val wrongProjectDto = ProjectDto.builder()
        .projectName("Test")
        .projectDescription("Wrong project DTO.")
        .projectType("No Game")
        .build();
    when(projectTypeRepository.findByProjectTypeValue(wrongProjectDto.projectType()))
        .thenReturn(Optional.empty());

    val exception = assertThrows(
        ProjectTypeNotFoundException.class,
        () -> projectService.saveProject(wrongProjectDto)
    );

    assertEquals("Project type '%s' not found in database.".formatted(wrongProjectDto.projectType()),
        exception.getMessage());
    assertThat(output).contains(
        "Project type '%s' not found in database.".formatted(wrongProjectDto.projectType())
    );
    verify(projectTypeRepository, times(1)).findByProjectTypeValue(wrongProjectDto.projectType());
  }
}
