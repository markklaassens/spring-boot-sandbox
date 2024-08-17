package com.example.persistence.entities;

import static com.example.testconfig.TestConstants.CREATOR;
import static com.example.testconfig.TestConstants.CREATOR_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Test;

class UserRoleTest {

  @Test
  void shouldConstructUserRole() {
    val userRole = new UserRole(
        1,
        CREATOR,
        Set.of(CREATOR_USER)
    );
    assertEquals(1, userRole.getUserRoleId());
    assertEquals(CREATOR, userRole.getUserRoleValue());
    assertEquals(1, userRole.getUsers().size());
    assertTrue(userRole.getUsers().contains(CREATOR_USER));
  }
}
