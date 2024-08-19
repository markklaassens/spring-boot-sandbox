package com.example.services;

import static com.example.testconfig.TestConstants.CREATOR_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.example.exceptions.UserNotFoundException;
import com.example.persistence.repositories.UserRepository;
import com.example.services.implementations.GetCurrentUserService;
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
class GetCurrentUserServiceTest {

  @Mock
  UserRepository userRepository;

  @InjectMocks
  GetCurrentUserService getCurrentUserService;

  @Test
  void shouldReturnCurrentUser() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("creator");
      when(userRepository.findByUsername("creator")).thenReturn(Optional.of(CREATOR_USER));

      val currentUser = getCurrentUserService.getUser();

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

      val exception = assertThrows(UserNotFoundException.class, () -> getCurrentUserService.getUser());

      assertEquals("User with username 'unknown' not found in database.", exception.getMessage());
      assertThat(output).contains("User with username 'unknown' not found in database.");
    }
  }
}
