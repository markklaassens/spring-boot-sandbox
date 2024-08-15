package com.example.persistence.repositories;

import static com.example.testconfig.TestConstants.PROJ_TYPE_COLLABORATIVE;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.persistence.entities.Project;
import lombok.val;
import org.junit.jupiter.api.Assertions;
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
  @Sql("/insert_project_types.sql")
  void shouldSaveAndFindProject() {
    val project = new Project(
        null,
        "Ultimate Tic-Tac-Toe",
        "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
        PROJ_TYPE_COLLABORATIVE
    );
    projectRepository.save(project);
    val projectList = projectRepository.findAll();
    assertFalse(projectList.isEmpty());
    val foundProject = projectList.get(0);
    Assertions.assertEquals(1, foundProject.getProjectId());
    Assertions.assertEquals(project.getProjectName(), foundProject.getProjectName());
    Assertions.assertEquals(project.getProjectDescription(), foundProject.getProjectDescription());
    Assertions.assertEquals(project.getProjectType(), foundProject.getProjectType());
  }
}
