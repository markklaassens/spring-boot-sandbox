package com.example.api.controller;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import com.example.persistence.entities.Project;
import com.example.services.interfaces.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProjectServiceController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProjectService projectService;

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
  void shouldReturnBadRequestOnEmptyUsernameList() throws Exception {
    mockMvc.perform(put("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Ultimate Tic-Tac-Toe",
                    "usernames": []
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message").value(
            "At least one username is mandatory."
        ));
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
  }

  @Test
  void shouldReturnBadRequestOnProjectTypeNotFoundException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class))).thenThrow(
        new ProjectTypeNotFoundException("Project type 'Together' not found in database."));

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "projectName": "Ultimate Tic-Tac-Toe",
                    "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                    "projectType": "Together"
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value("400"))
        .andExpect(jsonPath("$.message").value(
            "Project type 'Together' not found in database."
        ));
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
                    "projectType": "%s"
                }
                """.formatted(COLLABORATIVE))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.statusCode").value("404"))
        .andExpect(jsonPath("$.message").value(
            "User with username 'creator' not found in database."
        ));
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
  }

  @Test
  void shouldReturnConflictOnDataIntegrityViolationException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class)))
        .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

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
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.statusCode").value("409"))
        .andExpect(jsonPath("$.message").value("Unique constraint violation"));
  }

  @Test
  void shouldReturnConflictOnObjectOptimisticLockingFailureException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class)))
        .thenThrow(new ObjectOptimisticLockingFailureException(Project.class, "1"));

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
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.statusCode").value("409"))
        .andExpect(jsonPath("$.message").value("Object of class "
            + "[com.example.persistence.entities.Project] with identifier [1]: optimistic locking failed"));
  }

  @Test
  void shouldReturnForbiddenOnAccessDeniedException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class))).thenThrow(new AccessDeniedException(
        "You do not have permission to access this resource."));

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
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.statusCode").value("403"))
        .andExpect(jsonPath("$.message").value("You do not have permission to access this resource."));
  }

  @Test
  void shouldReturnInternalServerErrorOnGeneralException() throws Exception {
    when(projectService.saveProject(any(ProjectDto.class))).thenThrow(new RuntimeException("Unexpected failure"));

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
        .andExpect(status().isInternalServerError()) // Expect 500 Internal Server Error
        .andExpect(jsonPath("$.statusCode").value("500"))
        .andExpect(jsonPath("$.message").value("An unexpected error occurred: Unexpected failure"));
  }
}
