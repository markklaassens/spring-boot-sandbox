package com.example.utilities;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@SuppressWarnings("java:S1118") // Suppresses Sonar warning which is handled by the @UtilityClass annotation from Lombok
@UtilityClass
public class AuthenticationUtil {

  /**
   * Retrieves the username of the currently authenticated user from the security context.
   *
   * @return the username of the authenticated user
   */
  public static String getUsername() {
    return SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getName();
  }
}
