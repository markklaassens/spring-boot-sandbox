package com.example.api.controller;

import static com.example.TestConstants.CREATOR;
import static com.example.TestConstants.PROJECT_DTO;
import static com.example.TestConstants.PROJECT_DTO_LIST;
import static com.example.TestConstants.PROJECT_USERS_DTO;
import static com.example.TestConstants.PROJECT_USERS_RESPONSE_DTO;
import static com.example.TestConstants.USER;
import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;
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
import com.example.config.SecurityConfig;
import com.example.services.interfaces.ProjectService;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProjectController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class ProjectControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  DataSource dataSource; //Mock datasource to initialize SecurityConfig

  @MockBean
  private ProjectService projectService;

  @Test
  @WithMockUser(roles = CREATOR)
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
  @WithMockUser(roles = CREATOR)
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
  @WithMockUser(roles = USER)
  void shouldNotPostProjectsAsUser() throws Exception {
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
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldNotAddUsersToProjectsAsUser() throws Exception {
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
        .andExpect(status().isForbidden());
  }
}
