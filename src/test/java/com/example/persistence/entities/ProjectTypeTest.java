package com.example.persistence.entities;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.testconfig.TestConstants.PROJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Test;

class ProjectTypeTest {

  @Test
  void shouldConstructProjectType() {
    val projectType = new ProjectType(
        1,
        COLLABORATIVE,
        Set.of(PROJECT)
    );
    assertEquals(1, projectType.getProjectTypeId()
    );
    assertEquals(COLLABORATIVE, projectType.getProjectTypeValue());
    assertEquals(1, projectType.getProjects().size());
    assertTrue(projectType.getProjects().contains(PROJECT));
  }
}
