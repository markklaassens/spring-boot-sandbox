package com.example.persistence.repositories;

import static com.example.TestConstants.CREATOR_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @Sql("/insert_user_roles.sql")
  void shouldSaveAndFindUserByUsername() {
    userRepository.save(CREATOR_USER);
    val optionalFoundUser = userRepository.findByUsername(CREATOR_USER.getUsername());
    assertTrue(optionalFoundUser.isPresent());
    val foundUser = optionalFoundUser.get();
    assertEquals(1, foundUser.getUserId());
    assertEquals(CREATOR_USER.getUsername(), foundUser.getUsername());
    assertEquals(CREATOR_USER.getUserPassword(), foundUser.getUserPassword());
    assertEquals(CREATOR_USER.getUserRoles().size(), foundUser.getUserRoles().size());
  }
}
