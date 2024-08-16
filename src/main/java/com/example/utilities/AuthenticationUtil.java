package com.example.utilities;

import org.springframework.security.core.context.SecurityContextHolder;

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
