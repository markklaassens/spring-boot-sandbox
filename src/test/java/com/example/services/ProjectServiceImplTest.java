package com.example.services;

import static com.example.testconfig.TestConstants.CREATOR_USER;
import static com.example.testconfig.TestConstants.PROJECT;
import static com.example.testconfig.TestConstants.PROJECT_DTO;
import static com.example.testconfig.TestConstants.PROJECT_LIST;
import static com.example.testconfig.TestConstants.PROJECT_USERS_DTO;
import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static com.example.testconfig.TestConstants.REGULAR_USER;
import static com.example.testconfig.TestConstants.USER_ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.api.dto.NotAddedUserDto;
import com.example.api.dto.ProjectDto;
import com.example.api.dto.ProjectUsersDto;
import com.example.exceptions.NotCreatorOfProjectException;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectNotFoundException;
import com.example.exceptions.ProjectTypeNotFoundException;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.User;
import com.example.persistence.repositories.ProjectRepository;
import com.example.persistence.repositories.ProjectTypeRepository;
import com.example.persistence.repositories.UserRepository;
import com.example.services.implementations.GetCurrentUserService;
import com.example.services.implementations.ProjectServiceImpl;
import com.example.utilities.ProjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  @Mock
  private UserRepository userRepository;

  @Mock
  private GetCurrentUserService getCurrentUserService;

  @InjectMocks
  private ProjectServiceImpl projectService;

  @Test
  void shouldSaveProject(CapturedOutput output) {
    when(projectRepository.findByProjectName(PROJECT_DTO.projectName())).thenReturn(Optional.empty());
    when(projectTypeRepository.findByProjectTypeValue(PROJECT_DTO.projectType()))
        .thenReturn(Optional.of(PROJ_TYPE_COLLABORATIVE));
    when(getCurrentUserService.getUser()).thenReturn(CREATOR_USER);
    when(projectRepository.save(any(Project.class))).thenReturn(PROJECT);

    val result = projectService.saveProject(PROJECT_DTO);

    assertEquals(PROJECT_DTO, result);
    verify(projectRepository, times(1)).save(any(Project.class));
    assertThat(output).contains("Saved project with id '%s', name '%s' and project creator '%s'"
        .formatted(PROJECT.getProjectId(), PROJECT.getProjectName(), PROJECT.getProjectCreator().getUsername()));
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
  void shouldReturnEmptyListOnNoProjectsFound(CapturedOutput output) {
    when(projectRepository.findAll()).thenReturn(Collections.emptyList());

    val resultList = projectService.findAllProjects();

    assertTrue(resultList.isEmpty());
    assertThat(output).contains("No projects found");
  }

  @Test
  void shouldAddUsersToProject(CapturedOutput output) {
    when(projectRepository.findByProjectName(PROJECT_USERS_DTO.projectName())).thenReturn(Optional.of(PROJECT));
    when(getCurrentUserService.getUser()).thenReturn(CREATOR_USER);
    for (String username : PROJECT_USERS_DTO.usernames()) {
      when(userRepository.findByUsername(username)).thenAnswer(invocation -> Optional.of(User.builder()
          .userId(1) // id doesn't matter since data isn't persisted to database in unittest
          .username(username)
          .userPassword("test123")
          .userRoles(Set.of(USER_ROLE_USER))
          .build()));
    }

    projectService.addUsersToProject(PROJECT_USERS_DTO);
    val usernameList = PROJECT.getProjectUsers().stream().map(User::getUsername).toList();

    for (String username : PROJECT_USERS_DTO.usernames()) {
      assertTrue(usernameList.contains(username));
      assertThat(output).contains("Added user with username '%s' to project '%s'."
          .formatted(username, PROJECT_USERS_DTO.projectName()));
    }
    PROJECT.getProjectUsers().clear(); // clear users at the end of test to prevent problems with other tests
  }

  @Test
  void shouldNotAddDoubleUsersToProject(CapturedOutput output) {
    when(projectRepository.findByProjectName(PROJECT_USERS_DTO.projectName())).thenReturn(Optional.of(PROJECT));
    when(getCurrentUserService.getUser()).thenReturn(CREATOR_USER);
    List<String> newUsernames = new ArrayList<>(PROJECT_USERS_DTO.usernames());
    newUsernames.add("user6");
    newUsernames.add("user6");
    for (String username : newUsernames) {
      when(userRepository.findByUsername(username)).thenAnswer(invocation -> Optional.of(User.builder()
          .userId(1) // id doesn't matter since data isn't persisted to database in unittest
          .username(username)
          .userPassword("test123")
          .userRoles(Set.of(USER_ROLE_USER))
          .build()));
    }

    projectService.addUsersToProject(PROJECT_USERS_DTO);
    val amountOfUsers = PROJECT.getProjectUsers().size();
    val result = projectService.addUsersToProject(ProjectUsersDto.builder()
        .projectName(PROJECT_USERS_DTO.projectName())
        .usernames(newUsernames)
        .build());

    assertEquals(amountOfUsers + 1, PROJECT.getProjectUsers().size()); // newUsernames has 1 unique added user
    val usernameList = PROJECT.getProjectUsers().stream().map(User::getUsername).toList();
    for (String username : newUsernames) {
      assertTrue(result.notAddedUsers().contains(NotAddedUserDto.builder()
          .username(username)
          .reason("User with username '%s' is already added to project '%s'."
              .formatted(username, PROJECT.getProjectName()))
          .build()));
      assertThat(output).contains("User with username '%s' is already added to project '%s'."
          .formatted(username, PROJECT.getProjectName()));

      assertTrue(usernameList.contains(username));
      assertThat(output).contains("Added user with username '%s' to project '%s'."
          .formatted(username, PROJECT_USERS_DTO.projectName()));
    }
    PROJECT.getProjectUsers().clear(); // clear users at the end of test to prevent problems with other tests
  }

  @Test
  void shouldSaveNotFoundUsersWhileAddingUsersToProject(CapturedOutput output) {
    when(projectRepository.findByProjectName(PROJECT_USERS_DTO.projectName())).thenReturn(Optional.of(PROJECT));
    when(getCurrentUserService.getUser()).thenReturn(CREATOR_USER);
    for (String username : PROJECT_USERS_DTO.usernames()) {
      when(userRepository.findByUsername(username)).thenAnswer(invocation -> Optional.empty());
    }

    val result = projectService.addUsersToProject(PROJECT_USERS_DTO);

    assertEquals(0, PROJECT.getProjectUsers().size());
    for (String username : PROJECT_USERS_DTO.usernames()) {
      assertTrue(result.notAddedUsers().contains(NotAddedUserDto.builder()
          .username(username)
          .reason("Could not find user with username '%s'.".formatted(username))
          .build()));
      assertThat(output).contains("Could not find user with username '%s'."
          .formatted(username));
    }
    PROJECT.getProjectUsers().clear(); // clear users at the end of test to prevent problems with other tests
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
  void shouldThrowExceptionWhenProjectTypeIsNotFound(CapturedOutput output) {
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

  @Test
  void shouldThrowExceptionWhenProjectIsNotFound(CapturedOutput output) {
    when(projectRepository.findByProjectName(PROJECT_USERS_DTO.projectName())).thenReturn(Optional.empty());

    val exception = assertThrows(
        ProjectNotFoundException.class,
        () -> projectService.addUsersToProject(PROJECT_USERS_DTO)
    );

    assertEquals("Project with name '%s' not found in database.".formatted(PROJECT_USERS_DTO.projectName()),
        exception.getMessage());
    assertThat(output).contains("Project with name '%s' not found in database.".formatted(PROJECT_USERS_DTO.projectName()));
  }

  @Test
  void shouldThrowExceptionWhenCreatorDoesNotMatchProjectCreator(CapturedOutput output) {
    when(projectRepository.findByProjectName(PROJECT_USERS_DTO.projectName())).thenReturn(Optional.of(PROJECT));
    when(getCurrentUserService.getUser()).thenReturn(REGULAR_USER);

    val exception = assertThrows(NotCreatorOfProjectException.class,
        () -> projectService.addUsersToProject(PROJECT_USERS_DTO));

    assertEquals("User '%s' is not the creator of project '%s'."
        .formatted(REGULAR_USER.getUsername(), PROJECT_USERS_DTO.projectName()), exception.getMessage());
    assertThat(output).contains("User '%s' is not the creator of project '%s'."
        .formatted(REGULAR_USER.getUsername(), PROJECT_USERS_DTO.projectName()));
  }

}
