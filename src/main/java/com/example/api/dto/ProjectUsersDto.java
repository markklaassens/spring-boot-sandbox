package com.example.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing a project and users to add.
 */
@Builder
public record ProjectUsersDto(

    @NotEmpty(message = "Project name is mandatory.")
    @Size(max = 300, message = "Project name can have a maximum of 300 characters.")
    String projectName,

    @NotEmpty(message = "At least one username is mandatory.")
    List<String> usernames
) {

  public ProjectUsersDto {
    usernames = List.copyOf(usernames);
  }

}
