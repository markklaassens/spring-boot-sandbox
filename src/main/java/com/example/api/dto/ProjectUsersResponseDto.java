package com.example.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;

/**
 * Data Transfer Object (DTO) representing a project including the added and not added users.
 */
@Builder
public record ProjectUsersResponseDto(
    @NonNull String projectName,
    @NonNull List<String> addedUsers,
    @NonNull List<NotAddedUserDto> notAddedUsers) {

  public ProjectUsersResponseDto {
    addedUsers = List.copyOf(addedUsers);
    notAddedUsers = List.copyOf(notAddedUsers);
  }
}
