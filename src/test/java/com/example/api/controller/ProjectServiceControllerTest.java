package com.example.api.controller;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;
import static com.example.testconfig.TestConstants.PROJECT_DTO;
import static com.example.testconfig.TestConstants.PROJECT_DTO_LIST;
import static com.example.testconfig.TestConstants.PROJECT_USERS_DTO;
import static com.example.testconfig.TestConstants.PROJECT_USERS_RESPONSE_DTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.api.dto.ProjectDto;
import com.example.api.dto.ProjectUsersDto;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectNotFoundException;
import com.example.exceptions.ProjectTypeNotFoundException;
import com.example.exceptions.UserNotFoundException;
import com.example.services.interfaces.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProjectServiceController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ProjectServiceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProjectService projectService;


  @Test
  void shouldAddProject() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class))).thenReturn(PROJECT_DTO);

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Ultimate Tic-Tac-Toe",
                    "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                    "projectType": "%s"
                }
                """.formatted(COLLABORATIVE))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.projectName").value("Ultimate Tic-Tac-Toe"))
        .andExpect(jsonPath("$.projectDescription").value(
            "Project for collaborating and developing the game Ultimate Tic-Tac-Toe."))
        .andExpect(jsonPath("$.projectType").value(COLLABORATIVE));
    verify(projectService, times(1)).saveProject(any(ProjectDto.class));
  }

  @Test
  void shouldGetAllProjects() throws Exception {
    when(projectService.findAllProjects()).thenReturn(PROJECT_DTO_LIST);

    mockMvc.perform(get("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("[0].projectName").value("Ultimate Tic-Tac-Toe"))
        .andExpect(jsonPath("[0].projectDescription").value(
            "Project for collaborating and developing the game Ultimate Tic-Tac-Toe."))
        .andExpect(jsonPath("[0].projectType").value(COLLABORATIVE))
        .andExpect(jsonPath("[1].projectName").value("Tetris Blocks"))
        .andExpect(jsonPath("[1].projectDescription").value(
            "Project for competing and solving the puzzle Tetris Blocks."))
        .andExpect(jsonPath("[1].projectType").value(COMPETITIVE));
    verify(projectService, times(1)).findAllProjects();
  }

  @Test
  void shouldUpdateProjectUsers() throws Exception {
    when(projectService.addUsersToProject(any(ProjectUsersDto.class))).thenReturn(PROJECT_USERS_RESPONSE_DTO);

    mockMvc.perform(put("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "%s",
                    "usernames": ["%s", "%s", "%s", "%s", "%s"]
                }
                """.formatted(
                PROJECT_USERS_DTO.projectName(),
                PROJECT_USERS_DTO.usernames().get(0),
                PROJECT_USERS_DTO.usernames().get(1),
                PROJECT_USERS_DTO.usernames().get(2),
                PROJECT_USERS_DTO.usernames().get(3),
                PROJECT_USERS_DTO.usernames().get(4)
            ))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.projectName").value(PROJECT_USERS_RESPONSE_DTO.projectName()))
        .andExpect(jsonPath("$.addedUsers[0]").value(PROJECT_USERS_RESPONSE_DTO.addedUsers().get(0)))
        .andExpect(jsonPath("$.addedUsers[1]").value(PROJECT_USERS_RESPONSE_DTO.addedUsers().get(1)))
        .andExpect(jsonPath("$.addedUsers[2]").value(PROJECT_USERS_RESPONSE_DTO.addedUsers().get(2)))
        .andExpect(jsonPath("$.notAddedUsers.[0].username").value("user4"))
        .andExpect(jsonPath("$.notAddedUsers.[0].reason").value(
            "User with username 'user4' is already added to project."))
        .andExpect(jsonPath("$.notAddedUsers.[1].username").value("user5"))
        .andExpect(jsonPath("$.notAddedUsers.[1].reason").value(
            "Could not find user with username 'user5'."));
  }


  @Test
  void shouldReturnBadRequestOnMissingValue() throws Exception {
    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                    "projectType": "%s"
                }
                """.formatted(COLLABORATIVE))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message").value("Project name is mandatory."));
  }

  @Test
  void shouldReturnBadRequestOnMaxCharactersProjectName() throws Exception {
    String projectName = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "%s",
                    "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                    "projectType": "%s"
                }
                """.formatted(projectName, COLLABORATIVE))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message").value(
            "Project name can have a maximum of 300 characters."));
  }

  @Test
  void shouldReturnBadRequestOnMaxCharactersProjectDescription() throws Exception {
    String projectDescription = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJ"
        + "KLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQ"
        + "RSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWX"
        + "YZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDE"
        + "FGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKL"
        + "MNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRS"
        + "TUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ"
        + "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFG"
        + "HIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMN"
        + "OPQRSTUVWXYZABCDEFGHIJKLM";

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Ultimate Tic-Tac-Toe",
                    "projectDescription": "%s",
                    "projectType": "%s"
                }
                """.formatted(projectDescription, COLLABORATIVE))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message").value(
            "Project description can have a maximum of 1000 characters."));
  }

  @Test
  void shouldReturnMultipleErrorsOnMissingMultipleValues() throws Exception {
    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectType": "%s"
                }
                """.formatted(COLLABORATIVE))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message.[0]").value("Project description is mandatory."))
        .andExpect(jsonPath("$.message.[1]").value("Project name is mandatory."));
  }

  @Test
  void shouldReturnBadRequestOnProjectAlreadyExistsException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class))).thenThrow(
        new ProjectAlreadyExistsException("Project with project name 'Ultimate Tic-Tac-Toe' already exists in database"));

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Ultimate Tic-Tac-Toe",
                    "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                    "projectType": "%s"
                }
                """.formatted(COLLABORATIVE))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message").value(
            "Project with project name 'Ultimate Tic-Tac-Toe' already exists in database"));
    verify(projectService, times(1)).saveProject(any(ProjectDto.class));
  }

  @Test
  void shouldReturnBadRequestOnProjectTypeNotFoundException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class))).thenThrow(
        new ProjectTypeNotFoundException("Project type 'No Game' not found in database."));

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Ultimate Tic-Tac-Toe",
                    "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                    "projectType": "No Game"
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message").value(
            "Project type 'No Game' not found in database."
        ));
    verify(projectService, times(1)).saveProject(any(ProjectDto.class));
  }

  @Test
  void shouldReturnNotFoundOnUserNotFoundException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class))).thenThrow(
        new UserNotFoundException("User with username 'creator' not found in database."));

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Ultimate Tic-Tac-Toe",
                    "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                    "projectType": "No Game"
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.statusCode").value("404"))
        .andExpect(jsonPath("$.message").value(
            "User with username 'creator' not found in database."
        ));
    verify(projectService, times(1)).saveProject(any(ProjectDto.class));
  }

  @Test
  void shouldReturnNotFoundOnProjectNotFoundException() throws Exception {
    when(projectService.addUsersToProject(any(ProjectUsersDto.class))).thenThrow(
        new ProjectNotFoundException("Project with name 'Not Ultimate Tic-Tac-Toe' not found in database."));

    mockMvc.perform(put("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Not Ultimate Tic-Tac-Toe",
                    "usernames": ["user1", "user2", "user3"]
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.statusCode").value("404"))
        .andExpect(jsonPath("$.message").value(
            "Project with name 'Not Ultimate Tic-Tac-Toe' not found in database."
        ));
    verify(projectService, times(1)).addUsersToProject(any(ProjectUsersDto.class));
  }
}
