package com.example.mapper;

import static com.example.TestConstants.NEW_USER_DTO;
import static com.example.TestConstants.USER_ROLE_USER;
import static com.example.tools.PasswordEncoder.ENCODER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import org.junit.jupiter.api.Test;

class UserMapperTest {


  @Test
  void shouldConvertUserDtoToUser() {
    val user = UserMapper.convertUserDtoToUser(NEW_USER_DTO, USER_ROLE_USER);
    assertEquals(NEW_USER_DTO.username(), user.getUsername());
    assertTrue(ENCODER.matches(NEW_USER_DTO.userPassword(), user.getUserPassword()));
    assertEquals(1, user.getUserRoles().size());
    assertThat(user.getUserRoles()).contains(USER_ROLE_USER);
  }
}