package com.example.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  /**
   * Configures the security filter chain to disable CSRF, permit access to Swagger, and require authentication for other
   * requests.
   *
   * @param http the {@link HttpSecurity} to configure
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception if a security configuration error occurs
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/projects").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  /**
   * Configures a {@link JdbcUserDetailsManager} with custom queries for user details and authorities.
   *
   * @param dataSource the {@link DataSource} to use
   * @return the configured {@link UserDetailsService}
   */
  @Bean
  public UserDetailsService userDetailsService(DataSource dataSource) {
    JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
    manager.setDataSource(dataSource);

    manager.setUsersByUsernameQuery(
        "SELECT username, user_password AS password, TRUE AS enabled FROM USERS WHERE username = ?;"
    );

    manager.setAuthoritiesByUsernameQuery(
        "SELECT u.username AS username, ur.user_role_value AS authority "
            + "FROM USERS u "
            + "JOIN USER_USER_ROLE_MAPPING uurm ON u.user_id = uurm.user_id "
            + "JOIN USER_ROLES ur ON uurm.user_role_id = ur.user_role_id "
            + "WHERE u.username = ?;"
    );

    return manager;
  }
}
