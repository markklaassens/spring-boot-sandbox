package com.example.api.dto;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProjectDtoTest {

  @Test
  void shouldConstructProjectDto() {
    val projectDto = new ProjectDto(
        "Ultimate Tic-Tac-Toe",
        "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        COLLABORATIVE
    );
    assertEquals("Ultimate Tic-Tac-Toe", projectDto.projectName());
    assertEquals("Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        projectDto.projectDescription());
    Assertions.assertEquals(COLLABORATIVE, projectDto.projectType());
  }
}
