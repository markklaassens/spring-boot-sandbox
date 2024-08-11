package com.example.persistence.entities;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Test;

class ProjectTypeTest {

  @Test
  void shouldConstructProjectType() {
    val projectType = new ProjectType(
        1,
        COLLABORATIVE,
        Set.of()
    );
    assertEquals(1, projectType.getProjectTypeId()
    );
    assertEquals(COLLABORATIVE, projectType.getProjectTypeValue());
    assertEquals(Set.of(), projectType.getProjects());
  }
}
