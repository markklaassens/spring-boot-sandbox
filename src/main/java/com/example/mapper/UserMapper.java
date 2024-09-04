package com.example.mapper;

import com.example.api.dto.UserDto;
import com.example.api.dto.UserResponseDto;
import com.example.persistence.entities.User;
import com.example.persistence.entities.UserRole;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class UserMapper {

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * Converts a UserDto to a User entity with the specified UserRole.
   *
   * @param userDto  the DTO containing user data
   * @param userRole the role to assign to the user
   * @return the converted User entity
   */
  public static User convertUserDtoToUser(@NonNull final UserDto userDto, @NonNull final UserRole userRole) {
    return User.builder()
        .username(userDto.username())
        .userPassword(passwordEncoder.encode(userDto.userPassword()))
        .userRoles(new HashSet<>(Set.of(userRole)))
        .build();
  }

  /**
   * Converts a User to a UserResponseDto.
   *
   * @param user the User entity to convert
   * @return the converted UserResponseDto
   */
  public static UserResponseDto convertUserToUserResponseDto(@NonNull final User user) {
    return UserResponseDto.builder()
        .username(user.getUsername())
        .build();
  }
}
