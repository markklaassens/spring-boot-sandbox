package com.example.persistence.entities;

import static com.example.testconfig.TestConstants.CREATOR_USER;
import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static com.example.testconfig.TestConstants.REGULAR_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Test;

class ProjectTest {

  @Test
  void shouldConstructProject() {
    val project = new Project(
        1,
        "Ultimate Tic-Tac-Toe",
        "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        PROJ_TYPE_COLLABORATIVE,
        CREATOR_USER,
        Set.of(REGULAR_USER)
    );
    assertEquals(1, project.getProjectId());
    assertEquals("Ultimate Tic-Tac-Toe", project.getProjectName());
    assertEquals("Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        project.getProjectDescription());
    assertEquals(PROJ_TYPE_COLLABORATIVE, project.getProjectType());
    assertEquals(CREATOR_USER, project.getProjectCreator());
    assertEquals(1, project.getProjectUsers().size());
    assertTrue(project.getProjectUsers().contains(REGULAR_USER));
  }
}
