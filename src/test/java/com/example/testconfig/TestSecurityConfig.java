package com.example.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@TestConfiguration
public class TestSecurityConfig {

  /**
   * Configures the security filter chain for the test configuration.
   *
   * <p>This method disables CSRF protection and allows all incoming requests
   * without requiring authentication, which is useful in testing scenarios.</p>
   *
   * @param http the {@link HttpSecurity} to modify the security settings
   * @return the configured {@link SecurityFilterChain} object
   * @throws Exception if an error occurs while configuring the security settings
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        );
    return http.build();
  }
}
