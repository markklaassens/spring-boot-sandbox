package com.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  /**
   * Configures the OpenAPI documentation with basic authentication.
   *
   * @return the configured {@link OpenAPI} instance
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(new Info().title("project-service").version("1.0.0"))
        .components(new Components()
            .addSecuritySchemes("Basic authentication",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")))
        .addSecurityItem(new SecurityRequirement().addList("Basic authentication"));
  }
}
