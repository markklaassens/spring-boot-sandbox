package com.example.persistence.entities;

import static com.example.testconfig.TestConstants.PROJECT;
import static com.example.testconfig.TestConstants.PROJECT2;
import static com.example.testconfig.TestConstants.USER_ROLE_CREATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void shouldConstructUser() {
    val user = new User(
        1,
        "creator",
        "test123",
        Set.of(USER_ROLE_CREATOR),
        Set.of(PROJECT),
        Set.of(PROJECT2)
    );
    assertEquals(1, user.getUserId());
    assertEquals("creator", user.getUsername());
    assertEquals("test123", user.getUserPassword());
    assertEquals(1, user.getUserRoles().size());
    assertTrue(user.getUserRoles().contains(USER_ROLE_CREATOR));
    assertEquals(1, user.getCreatorProjects().size());
    assertTrue(user.getCreatorProjects().contains(PROJECT));
  }
}
