package com.example.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility for encoding a userPassword with BCrypt and validating the result.
 */
@SuppressWarnings("java:S1118") // Suppresses Sonar warning which is handled by the @UtilityClass annotation from Lombok
@UtilityClass
public class PasswordEncoder {

  public static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  /**
   * Encodes a userPassword with BCrypt and verifies the result.
   */
  public static void main(final String[] args) {

    val rawPassword = "test123";
    val encodedPassword = encoder.encode(rawPassword);

    System.out.println("Raw Password: " + rawPassword);
    System.out.println("Encoded Password: " + encodedPassword);

    assertTrue(encoder.matches(rawPassword, encodedPassword), "ERROR, "
        + "encoded password does not match raw password");
  }
}
