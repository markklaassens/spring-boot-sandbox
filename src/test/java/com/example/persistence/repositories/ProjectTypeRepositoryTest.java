package com.example.persistence.repositories;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ProjectTypeRepositoryTest {

  @Autowired
  private ProjectTypeRepository projectTypeRepository;

  @Test
  void shouldFindProjectTypeAfterDbStartUp() {
    assertTrue(projectTypeRepository.findByProjectTypeValue(COMPETITIVE).isPresent());
    assertTrue(projectTypeRepository.findByProjectTypeValue(COLLABORATIVE).isPresent());
  }
}
