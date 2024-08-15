package com.example.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility for encoding a password with BCrypt and validating the result.
 */
@SuppressWarnings("java:S1118") // Suppresses Sonar warning which is handled by the @UtilityClass annotation from Lombok
@UtilityClass
public class PasswordEncoder {

  /**
   * Encodes a password with BCrypt and verifies the result.
   */
  public static void main(String[] args) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    String rawPassword = "test123";
    String encodedPassword = passwordEncoder.encode(rawPassword);

    System.out.println("Raw Password: " + rawPassword);
    System.out.println("Encoded Password: " + encodedPassword);

    assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "ERROR, "
        + "encoded password does not match raw password");
  }
}
