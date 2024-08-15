package com.example.persistence.entities;

import static com.example.testconfig.TestConstants.CREATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Test;

class UserRoleTest {

  @Test
  void shouldConstructUserRole() {
    val userRole = new UserRole(
        1,
        CREATOR,
        Set.of()
    );
    assertEquals(1, userRole.getUserRoleId());
    assertEquals(CREATOR, userRole.getUserRoleValue());
    assertEquals(Set.of(), userRole.getUsers());
  }
}
