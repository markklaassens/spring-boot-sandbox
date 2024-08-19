package com.example.api.controller;

import static com.example.TestConstants.NEW_USER_DTO;
import static com.example.TestConstants.PROJECT_DTO_LIST;
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
import com.example.services.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
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
  void shouldReturnUsernameAfterRegister() throws Exception {
    when(userService.registerUser(any(UserDto.class))).thenReturn(NEW_USER_DTO.username());

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
        .andExpect(jsonPath("$").value("newuser"));

    verify(userService, times(1)).registerUser(any(UserDto.class));
  }
}
