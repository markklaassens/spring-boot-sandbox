package com.example.config;

import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
@Profile({"local", "test"})
public class DatabaseInitializer {

  private final DataSource dataSource;

  public DatabaseInitializer(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Event listener that loads and executes SQL scripts after the application is ready.
   * This method runs scripts to insert initial data like project types, user roles, and users.
   */
  @EventListener(ApplicationReadyEvent.class) // Trigger after application is ready
  public void loadSqlScripts() throws SQLException {
    ResourceLoader resourceLoader = new DefaultResourceLoader();

    Resource[] scripts = {
        resourceLoader.getResource("classpath:/insert_project_types.sql"),
        resourceLoader.getResource("classpath:/insert_user_roles.sql"),
        resourceLoader.getResource("classpath:/insert_users.sql")
    };

    for (Resource script : scripts) {
      executeSqlScript(dataSource.getConnection(), script);
    }
  }
}
