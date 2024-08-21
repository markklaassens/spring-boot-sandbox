package com.example.config;

import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;

import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

@Configuration
@Profile({"local", "test"})
@RequiredArgsConstructor
public class DatabaseInitializer {

  private final DataSource dataSource;
  private final Environment environment;

  /**
   * Event listener that loads and executes SQL scripts after the application is ready. The scripts are loaded based on the
   * active profile.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void loadSqlScripts() throws SQLException {
    val scripts = getScriptsForActiveProfile();

    for (Resource script : scripts) {
      executeSqlScript(dataSource.getConnection(), script);
    }
  }

  private Resource[] getScriptsForActiveProfile() {
    val resourceLoader = new DefaultResourceLoader();
    if (environment.matchesProfiles("test")) {
      return new Resource[]{
          resourceLoader.getResource("classpath:/insert_project_types.sql"),
          resourceLoader.getResource("classpath:/insert_user_roles.sql"),
          resourceLoader.getResource("classpath:/insert_users.sql"),
          resourceLoader.getResource("classpath:/edit_user_sequence_h2.sql")
      };
    } else if (environment.matchesProfiles("local")) {
      return new Resource[]{
          resourceLoader.getResource("classpath:/insert_project_types.sql"),
          resourceLoader.getResource("classpath:/insert_user_roles.sql"),
          resourceLoader.getResource("classpath:/insert_users.sql"),
          resourceLoader.getResource("classpath:/edit_user_sequence_postgres.sql")
      };
    } else {
      return new Resource[0]; // No scripts if no matching profile
    }
  }
}