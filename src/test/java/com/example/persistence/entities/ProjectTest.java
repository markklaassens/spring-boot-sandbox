package com.example.persistence.entities;

import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import org.junit.jupiter.api.Test;

class ProjectTest {

  @Test
  void shouldConstructProject() {
    val project = new Project(
        1,
        "Ultimate Tic-Tac-Toe",
        "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        PROJ_TYPE_COLLABORATIVE
    );
    assertEquals(1, project.getProjectId());
    assertEquals("Ultimate Tic-Tac-Toe", project.getProjectName());
    assertEquals("Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        project.getProjectDescription());
    assertEquals(PROJ_TYPE_COLLABORATIVE, project.getProjectType());
  }
}
