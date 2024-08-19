package com.example.persistence.repositories;

import static com.example.config.ApplicationConstants.ROLE_CREATOR;
import static com.example.config.ApplicationConstants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@ActiveProfiles("test")
class UserRoleRepositoryTest {

  @Autowired
  private UserRoleRepository userRoleRepository;

  @Test
  @Sql("/insert_user_roles.sql")
  void shouldFindProjectTypeAfterDbStartUp() {
    assertTrue(userRoleRepository.findByUserRoleValue(ROLE_USER).isPresent());
    assertTrue(userRoleRepository.findByUserRoleValue(ROLE_CREATOR).isPresent());
  }
}
