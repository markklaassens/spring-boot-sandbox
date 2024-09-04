package com.example.api.dto;

import lombok.Builder;
import lombok.NonNull;

/**
 * Data Transfer Object (DTO) representing a project and users to add.
 */
@Builder
public record NotAddedUserDto(@NonNull String username, @NonNull String reason
) {

}
