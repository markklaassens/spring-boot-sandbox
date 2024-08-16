package com.example.persistence.repositories;

import static com.example.testconfig.TestConstants.CREATOR_USER;
import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.persistence.entities.Project;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@ActiveProfiles("test")
class ProjectRepositoryTest {

  @Autowired
  private ProjectRepository projectRepository;

  @Test
  @Sql({"/insert_project_types.sql", "/insert_user_roles.sql", "/insert_users.sql"})
  void shouldSaveAndFindProject() {
    val project = new Project(
        null,
        "Ultimate Tic-Tac-Toe",
        "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        PROJ_TYPE_COLLABORATIVE,
        CREATOR_USER
    );
    projectRepository.save(project);
    val projectList = projectRepository.findAll();
    assertFalse(projectList.isEmpty());
    val foundProject = projectList.get(0);
    assertEquals(1, foundProject.getProjectId());
    assertEquals(project.getProjectName(), foundProject.getProjectName());
    assertEquals(project.getProjectDescription(), foundProject.getProjectDescription());
    assertEquals(project.getProjectType(), foundProject.getProjectType());
  }
}
