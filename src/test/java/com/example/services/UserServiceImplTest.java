package com.example.services;

import static com.example.TestConstants.CREATOR_USER;
import static com.example.TestConstants.NEW_USER;
import static com.example.TestConstants.NEW_USER_DTO;
import static com.example.TestConstants.PROJECT;
import static com.example.TestConstants.PROJECT2;
import static com.example.TestConstants.REGULAR_USER;
import static com.example.TestConstants.USER_LIST;
import static com.example.TestConstants.USER_ROLE_USER;
import static com.example.config.ApplicationConstants.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.example.exceptions.UserNotFoundException;
import com.example.exceptions.UserRoleNotFoundException;
import com.example.exceptions.UsernameAlreadyExistsException;
import com.example.mapper.ProjectMapper;
import com.example.persistence.entities.User;
import com.example.persistence.repositories.UserRepository;
import com.example.persistence.repositories.UserRoleRepository;
import com.example.services.implementations.UserServiceImpl;
import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class UserServiceImplTest {

  @Mock
  UserRepository userRepository;

  @Mock
  UserRoleRepository userRoleRepository;

  @InjectMocks
  UserServiceImpl userService;


  @Test
  void shouldSaveUser(CapturedOutput output) {
    when(userRepository.save(any(User.class))).thenReturn(NEW_USER);
    when(userRoleRepository.findByUserRoleValue(ROLE_USER)).thenReturn(Optional.of(USER_ROLE_USER));

    val userResponseDto = userService.registerUser(NEW_USER_DTO);

    assertEquals(NEW_USER.getUsername(), userResponseDto.username());
    assertThat(output).contains("Created new user '%s'.".formatted(NEW_USER.getUsername()));
  }

  @Test
  void shouldReturnAllUsers(CapturedOutput output) {
    when(userRepository.findAll()).thenReturn(USER_LIST);
    val usernameList = USER_LIST.stream().map(User::getUsername).toList();

    val userResponseDtoList = userService.findAllUsers();

    assertEquals(USER_LIST.size(), userResponseDtoList.size());
    for (val user : userResponseDtoList) {
      assertTrue(usernameList.contains(user.username()));
    }
    assertThat(output).contains("Found '%s' users.".formatted(USER_LIST.size()));
  }

  @Test
  void shouldReturnEmptyListWhenNoUsersAreFound(CapturedOutput output) {
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    val userResponseDtoList = userService.findAllUsers();

    assertEquals(0, userResponseDtoList.size());
    assertThat(output).contains("No users found.");
  }

  @Test
  void shouldReturnCreatorProjects(CapturedOutput output) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("creator");
      when(userRepository.findByUsername("creator")).thenReturn(Optional.of(CREATOR_USER));
      CREATOR_USER.getCreatorProjects().add(PROJECT);
      CREATOR_USER.getCreatorProjects().add(PROJECT2);

      val creatorProjects = userService.findAllCreatorProjects();

      assertEquals(creatorProjects.size(), 2);
      assertTrue(creatorProjects.contains(ProjectMapper.convertProjectToProjectDto(PROJECT)));
      assertTrue(creatorProjects.contains(ProjectMapper.convertProjectToProjectDto(PROJECT)));
      assertThat(output).contains("Found '%s' creator projects.".formatted(creatorProjects.size()));
      CREATOR_USER.getCreatorProjects().clear();
    }
  }

  @Test
  void shouldReturnEmptyListWhenNoCreatorProjectsAreFound(CapturedOutput output) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("creator");
      when(userRepository.findByUsername("creator")).thenReturn(Optional.of(CREATOR_USER));

      val userProjects = userService.findAllCreatorProjects();

      assertEquals(userProjects.size(), 0);
      assertThat(output).contains("No creator projects found in database for user '%s'."
          .formatted(CREATOR_USER.getUsername()));
    }
  }

  @Test
  void shouldReturnUserProjects(CapturedOutput output) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user");
      when(userRepository.findByUsername("user")).thenReturn(Optional.of(REGULAR_USER));
      REGULAR_USER.getUserProjects().add(PROJECT);
      REGULAR_USER.getUserProjects().add(PROJECT2);

      val userProjects = userService.findAllUserProjects();

      assertEquals(userProjects.size(), 2);
      assertTrue(userProjects.contains(ProjectMapper.convertProjectToProjectDto(PROJECT)));
      assertTrue(userProjects.contains(ProjectMapper.convertProjectToProjectDto(PROJECT)));
      assertThat(output).contains("Found '%s' user projects.".formatted(userProjects.size()));
      REGULAR_USER.getCreatorProjects().clear();
    }
  }

  @Test
  void shouldReturnEmptyListWhenNoUserProjectsAreFound(CapturedOutput output) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user");
      when(userRepository.findByUsername("user")).thenReturn(Optional.of(REGULAR_USER));

      val userProjects = userService.findAllUserProjects();

      assertEquals(userProjects.size(), 0);
      assertThat(output).contains("No user projects found in database for user '%s'."
          .formatted(REGULAR_USER.getUsername()));
    }
  }

  @Test
  void shouldReturnCurrentUser() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("creator");
      when(userRepository.findByUsername("creator")).thenReturn(Optional.of(CREATOR_USER));

      val currentUser = userService.getUser();

      assertEquals(CREATOR_USER, currentUser);
    }
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound(CapturedOutput output) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("unknown");
      when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

      val exception = assertThrows(UserNotFoundException.class, () -> userService.getUser());

      assertEquals("User with username 'unknown' not found in database.", exception.getMessage());
      assertThat(output).contains("User with username 'unknown' not found in database.");
    }
  }

  @Test
  void shouldThrowExceptionWhenUsernameAlreadyExists(CapturedOutput output) {
    when(userRepository.findByUsername(NEW_USER_DTO.username())).thenReturn(Optional.of(NEW_USER));

    val exception = assertThrows(UsernameAlreadyExistsException.class, () -> userService.registerUser(NEW_USER_DTO));

    assertEquals("User with username '%s' already exists.".formatted(NEW_USER_DTO.username()), exception.getMessage());
    assertThat(output).contains("User with username '%s' already exists.".formatted(NEW_USER_DTO.username()));
  }

  @Test
  void shouldThrowExceptionWhenUserRoleIsNotFound(CapturedOutput output) {
    when(userRoleRepository.findByUserRoleValue(ROLE_USER)).thenReturn(Optional.empty());

    val exception = assertThrows(UserRoleNotFoundException.class, () -> userService.registerUser(NEW_USER_DTO));

    assertEquals("User role '%s' not found in database.".formatted(ROLE_USER), exception.getMessage());
    assertThat(output).contains("User role '%s' not found in database.".formatted(ROLE_USER));
  }
}
