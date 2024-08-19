package com.example.services;

import static com.example.testconfig.TestConstants.CREATOR_USER;
import static com.example.testconfig.TestConstants.PROJECT;
import static com.example.testconfig.TestConstants.PROJECT2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.example.exceptions.UserNotFoundException;
import com.example.persistence.repositories.UserRepository;
import com.example.services.implementations.UserServiceImpl;
import com.example.utilities.ProjectMapper;
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

  @InjectMocks
  UserServiceImpl userService;

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

      val creatorProjects = userService.findAllCreatorProjects();

      assertEquals(creatorProjects.size(), 0);
      assertThat(output).contains("No creator projects found in database for user '%s'."
          .formatted(CREATOR_USER.getUsername()));
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
}
