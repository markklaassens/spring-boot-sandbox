package com.example.api.controller;

import static com.example.TestConstants.CREATOR;
import static com.example.TestConstants.NEW_USER_RESPONSE_DTO;
import static com.example.TestConstants.PROJECT_DTO_LIST;
import static com.example.TestConstants.USER;
import static com.example.TestConstants.USER_RESPONSE_DTO_LIST;
import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.api.dto.UserDto;
import com.example.config.SecurityConfig;
import com.example.services.interfaces.UserService;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class)
@MockBean(DataSource.class)
@Import(SecurityConfig.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void shouldReturnUsernameAfterRegister() throws Exception {
    when(userService.registerUser(any(UserDto.class))).thenReturn(NEW_USER_RESPONSE_DTO);

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "username": "newuser",
                    "userPassword": "test123"
                }
                """
            )
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("newuser"));

    verify(userService, times(1)).registerUser(any(UserDto.class));
  }

  @Test
  @WithMockUser(roles = CREATOR)
  void shouldGetAllUsers() throws Exception {
    when(userService.findAllUsers()).thenReturn(USER_RESPONSE_DTO_LIST);
    mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("[0].username").value("newuser"))
        .andExpect(jsonPath("[1].username").value("user"))
        .andExpect(jsonPath("[2].username").value("creator"));
  }

  @Test
  @WithMockUser(roles = CREATOR)
  void shouldReturnAllCreatorProjects() throws Exception {
    when(userService.findAllCreatorProjects()).thenReturn(PROJECT_DTO_LIST);

    mockMvc.perform(get("/users/creator-projects")
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

    verify(userService, times(1)).findAllCreatorProjects();
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldReturnAllUserProjects() throws Exception {
    when(userService.findAllUserProjects()).thenReturn(PROJECT_DTO_LIST);

    mockMvc.perform(get("/users/user-projects")
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

    verify(userService, times(1)).findAllUserProjects();
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldNotGetAllUsersAsUser() throws Exception {
    mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldNotGetCreatorProjectsAsUser() throws Exception {
    mockMvc.perform(get("/users/creator-projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = CREATOR)
  void shouldNotGetUserProjectsAsCreator() throws Exception {
    mockMvc.perform(get("/users/user-projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
